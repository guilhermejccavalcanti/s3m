import org.junit.*;

public class s3mCronTests {

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
        output.append(runProgram("git", "merge", "origin/left", "--no-edit", "--quiet"));
        output.append(runProgram("git", "merge", "origin/right", "--no-edit", "--quiet"));
        return output.toString();
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
}