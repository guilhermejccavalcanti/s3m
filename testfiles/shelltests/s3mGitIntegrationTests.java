import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RegExUtils;
import org.junit.*;

public class s3mGitIntegrationTests {

    private Path s3mFilesPath = Paths.get(System.getProperty("user.home") + "/.jfstmerge");
    private Path s3mTempFilesPath = Paths.get(System.getProperty("user.home") + "/.jfstmerge1");
    private Path s3mTempRepositoryPath;

    @Before
    public void moveS3MFiles() throws IOException {
        if(Files.exists(s3mFilesPath)) {
            FileUtils.moveDirectory(s3mFilesPath.toFile(), s3mTempFilesPath.toFile());
        }
    }

    @Before
    public void createRepository() throws IOException, InterruptedException {
        s3mTempRepositoryPath = Files.createTempDirectory("tmpDir");
        runProgram("git", "init");
    }

    @After
    public void restoreS3MFiles() throws IOException {
        if (Files.exists(s3mTempFilesPath)) {
            FileUtils.deleteDirectory(s3mFilesPath.toFile());
            FileUtils.moveDirectory(s3mTempFilesPath.toFile(), s3mFilesPath.toFile());
        }
    }

    @After
    public void deleteRepository() throws IOException {
        FileUtils.deleteDirectory(s3mTempRepositoryPath.toFile());
    }

    @Test
    public void testWellFunctioningOfSemistructuredMerge() throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("semistructuredmerge");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");

        assertEquals(1, numConflicts);
    }

    @Test
    public void testFailingOfSemistructuredMerge() throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("semistructuredmerge-invalidfiles");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");

        assertNotEquals(1, numConflicts);
    }
    
    @Test
    public void testNoConflictingFiles() throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("noconflicts");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");
        boolean recursiveMergeHasBeenCalled = StringUtils.countMatches(mergeResult, "recursive") == 1;

        assertEquals(0, numConflicts);
        assertTrue(recursiveMergeHasBeenCalled);
    }

    @Test
    public void testWellFunctioningOfGitDiffAfterMerge() throws IOException, InterruptedException {
        initBranchesAndMerge("semistructuredmerge");
        String diffResult = runProgram("git", "diff", "left", "right");

        assertTrue(diffResult.contains("@@ -1,5 +1,9 @@"));
    }

    @Test
    public void testWellFunctioningOfCryptography() throws IOException, InterruptedException {
        initBranchesAndMerge("semistructuredmerge");

        assertFalse(defectuousFileExists());
    }

    @Test
    public void testNodeReordering() throws IOException, InterruptedException {
        initBranchesAndMerge("nodereordering");
        String mergeFile = FileUtils.readFileToString(s3mTempRepositoryPath.resolve("Test.java").toFile());
        String classInterfaceSequence = RegExUtils.removePattern(mergeFile, "[^ABCI]");

        assertEquals("ABCI", classInterfaceSequence);
    }

    @Test
    public void testWellFunctioningOfSemistructuredMergeAgainstBigRepository() 
            throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("big");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");
        int numFinishedFiles = StringUtils.countMatches(mergeResult, "finished");

        assertEquals(1, numConflicts);
        assertEquals(3, numFinishedFiles);
    }

    @Test
    public void testFailingOfSemistructuredMergeAgainstBigRepository() throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("bigcorrupted");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");
        int numFinishedFiles = StringUtils.countMatches(mergeResult, "finished");

        assertEquals(1, numConflicts);
        assertEquals(1, numFinishedFiles);
    }

    @Test
    public void testLogCorrectness() throws IOException, InterruptedException {
        initBranchesAndMerge("big");

        String summaryFile = FileUtils.readFileToString(s3mFilesPath.resolve("jfstmerge.summary").toFile());

        int numJavaFiles = retrieveStat(summaryFile, "(\\d+) JAVA files", "\\d+");
        int numAvoidedFP = retrieveStat(summaryFile, "least \\d+ false positive\\(s\\)", "\\d+");
        int numAvoidedFN = retrieveStat(summaryFile, "\\d+ false negative\\(s\\)", "\\d+");
        int numS3MConflicts = retrieveStat(summaryFile, "reported \\d+ conflicts", "\\d+");
        int numS3MConflictingLOC = retrieveStat(summaryFile, "totaling \\d+ conflicting", "\\d+");
        int numTextualConflicts = retrieveStat(summaryFile, "to \\d+ conflicts", "\\d+");
        int numTextualConflictingLOC = retrieveStat(summaryFile, "and \\d+ conflicting", "\\d+");
        
        assertEquals(3, numJavaFiles);
        assertEquals(2, numAvoidedFP);
        assertEquals(1, numAvoidedFN);
        assertEquals(1, numS3MConflicts);
        assertEquals(2, numS3MConflictingLOC);
        assertEquals(2, numTextualConflicts);
        assertEquals(5, numTextualConflictingLOC);

    }

    @Test
    public void testMergeWithASCIIEncoding() throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("otherencodings/ASCII");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");

        assertEquals(1, numConflicts);
    }

    @Test
    public void testMergeWithISOEncoding() throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("otherencodings/ISO1");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");

        assertEquals(1, numConflicts);
    }

    @Test
    public void testMergeWithUTF8WithBOMEncoding() throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("otherencodings/UTF-8");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");

        assertEquals(1, numConflicts);
    }

    @Test
    public void testMergeWithUTF16Encoding() throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("otherencodings/UTF-16");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");

        assertEquals(1, numConflicts);
    }

    @Test
    public void testMergeWithFilesHavingDifferentEncodings() throws IOException, InterruptedException {
        String mergeResult = initBranchesAndMerge("otherencodings/different");
        int numConflicts = StringUtils.countMatches(mergeResult, "CONFLICT");

        assertEquals(1, numConflicts);
    }

    private int retrieveStat(String text, String regex, String regexRemove) {
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(text);
        matcher.find();
        String match = matcher.group();

        matcher = Pattern.compile(regexRemove, Pattern.DOTALL).matcher(match);
        matcher.find();
        return Integer.parseInt(matcher.group());
    }
    

    private String initBranchesAndMerge(String filesPath) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        commitFiles(filesPath + "/base", "base");
        
        output.append(runProgram("git", "checkout", "-b", "left"));
        commitFiles(filesPath + "/left", "left");
        output.append(runProgram("git", "checkout", "master"));
        output.append(runProgram("git", "checkout", "-b", "right"));
        commitFiles(filesPath + "/right", "right");
        output.append(runProgram("git", "checkout", "master"));

        output.append(runProgram("git", "merge", "left", "--no-edit"));
        output.append(runProgram("git", "merge", "right", "--no-edit"));
        return output.toString();
    }

    private void commitFiles(String filesPath, String commitMessage) throws IOException, InterruptedException {
        copyFiles(filesPath);
        runProgram("git", "add", ".");
        runProgram("git", "commit", "-m", commitMessage);
    }

    private String runProgram(String... commands) throws IOException, InterruptedException {
        Process program = new ProcessBuilder()
                .directory(s3mTempRepositoryPath.toFile())
                .command(commands)
                .start();

        InputStream outputStream = program.getInputStream();
        String output = IOUtils.toString(outputStream);
        outputStream.close();
        return output;
    }

    private void copyFiles(String filesPath) throws IOException {
        File srcFile = new File(filesPath);
        if(srcFile.isFile()) {
            FileUtils.copyFile(srcFile, s3mTempRepositoryPath.toFile());
        } else {
            FileUtils.copyDirectory(srcFile, s3mTempRepositoryPath.toFile());
        }
    }

    private Iterator<File> getIterator(String directoryPath) {
        return FileUtils.iterateFiles(new File(directoryPath), new IOFileFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {
                return true;
            }

            @Override
            public boolean accept(File arg0) {
                return true;
            }
        }, new IOFileFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {
                return true;
            }

            @Override
            public boolean accept(File arg0) {
                return true;
            }
        });
    }

    private boolean defectuousFileExists() {
        boolean defectuousFileExists = false;
        Iterator<File> files = getIterator(System.getProperty("user.home") + "/.jfstmerge");
        while(files.hasNext()) {
            File file = files.next();
            if(file.getName().contains("defect")) {
                defectuousFileExists = true;
            }
        }
        return defectuousFileExists;
    }
}