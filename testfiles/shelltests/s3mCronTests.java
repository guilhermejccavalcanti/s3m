import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class s3mCronTests {

    private final Path samplesRepositoryPath = Paths.get("./samples");
    private final Path s3mFilesPath = Paths.get(System.getProperty("user.home") + "/.jfstmerge");

    @BeforeClass
    public static void cloneSamplesRepository() throws IOException, InterruptedException {
        runProgram(Paths.get("."), "git", "clone", "https://github.com/jvcoutinho/s3m-test-samples.git", "samples");
    }

    @Test
    public void testLogCorrectness() throws IOException, InterruptedException {
        initBranchesAndMerge();

        String summaryFile = FileUtils.readFileToString(s3mFilesPath.resolve("jfstmerge.summary").toFile());

        int numJavaFiles = retrieveStat(summaryFile, "(\\d+) JAVA files", "\\d+");
        int numAvoidedFP = retrieveStat(summaryFile, "least \\d+ false positive\\(s\\)", "\\d+");
        int numAvoidedFN = retrieveStat(summaryFile, "\\d+ false negative\\(s\\)", "\\d+");
        int numS3MConflicts = retrieveStat(summaryFile, "reported \\d+ conflicts", "\\d+");
        int numS3MConflictingLOC = retrieveStat(summaryFile, "totaling \\d+ conflicting", "\\d+");
        int numTextualConflicts = retrieveStat(summaryFile, "to \\d+ conflicts", "\\d+");
        int numTextualConflictingLOC = retrieveStat(summaryFile, "and \\d+ conflicting", "\\d+");
        
        assertEquals(1746, numJavaFiles);
        assertEquals(1668, numAvoidedFP);
        assertEquals(51, numAvoidedFN);
        assertEquals(1761, numS3MConflicts);
        assertEquals(23511, numS3MConflictingLOC);
        assertEquals(3358, numTextualConflicts);
        assertEquals(49448, numTextualConflictingLOC);

    }

    private int retrieveStat(String text, String regex, String regexRemove) {
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(text);
        matcher.find();
        String match = matcher.group();

        matcher = Pattern.compile(regexRemove, Pattern.DOTALL).matcher(match);
        matcher.find();
        return Integer.parseInt(matcher.group());
    }

    private String initBranchesAndMerge() throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        output.append(runProgram(samplesRepositoryPath, "git", "merge", "origin/left", "--no-edit", "--quiet"));
        output.append(runProgram(samplesRepositoryPath, "git", "merge", "origin/right", "--no-edit", "--quiet"));
        return output.toString();
    }

    private static String runProgram(Path directory, String... commands) throws IOException, InterruptedException {
        Process program = new ProcessBuilder()
                .directory(directory.toFile())
                .command(commands)
                .start();

        InputStream outputStream = program.getInputStream();
        String output = IOUtils.toString(outputStream);
        outputStream.close();
        return output;
    }
}