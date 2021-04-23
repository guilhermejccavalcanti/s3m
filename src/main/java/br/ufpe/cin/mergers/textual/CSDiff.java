package br.ufpe.cin.mergers.textual;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.CharStreams;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;

public class CSDiff implements TextualMergeStrategy {
    private static final String CSDiffScriptPath = "/csdiff.sh";
    private static final String CSDiffOutputFileName = "csdiff-merge-output";
    private static final String diff3OutputFileName = "diff3-merge-output";
    private static final String gitMergeOutputFileName = "git-merge-output";

    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
        try {
            Path leftFile = createContributionFile("left", leftContent);
            Path baseFile = createContributionFile("base", baseContent);
            Path rightFile = createContributionFile("right", rightContent);

            Path CSDiffOutputFile = createTempJavaFile(CSDiffOutputFileName);
            Path diff3OutputFile = createTempJavaFile(diff3OutputFileName);

            runCSDiff(leftFile, baseFile, rightFile, CSDiffOutputFile, diff3OutputFile);
            String CSDiffOutput = FilesManager.readFileContent(CSDiffOutputFile.toFile());
            
            deleteTempFiles(leftFile, baseFile, rightFile, CSDiffOutputFile, diff3OutputFile);

            return CSDiffOutput;
        } catch (IOException e) {
            throw new TextualMergeException("Error during opening/closing of temporary files");
        } catch (InterruptedException e) {
            throw new TextualMergeException("Error during CSDiff's execution");
        }
    }

    private static Path createContributionFile(String name, String content) throws IOException {
        Path file = createTempJavaFile(name);
        Files.write(file, content.getBytes(Charset.forName(Diff3.encoding)));
        return file;
    }

    private static Path createTempJavaFile(String name) throws IOException {
        return Files.createTempFile(name, ".java");
    }

    private static void runCSDiff(Path left, Path base, Path right, Path CSDiffOutput, Path diff3Output) throws IOException, InterruptedException {
        Path script = createScriptFile();
        Path gitMergeOutputFile = createTempJavaFile(gitMergeOutputFileName);

        String[] command = buildCommand(script, left, base, right, CSDiffOutput, diff3Output);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        deleteTempFiles(script, gitMergeOutputFile);
    }

    private static Path createScriptFile() throws IOException {
        InputStream inputStream = CSDiff.class.getResourceAsStream(CSDiffScriptPath);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String content = CharStreams.toString(inputStreamReader);
        
        Path file = Files.createTempFile("csdiff", ".sh");
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private static String[] buildCommand(Path script, Path left, Path base, Path right, Path CSDiffOutput, Path diff3Output) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().trim().startsWith("windows");

        String[] command = new String[7];
        command[0] = isWindows ? "" : "sh";
        command[1] = script.toAbsolutePath().toString();
        command[2] = left.toAbsolutePath().toString();
        command[3] = base.toAbsolutePath().toString();
        command[4] = right.toAbsolutePath().toString();
        command[5] = CSDiffOutput.toAbsolutePath().toString();
        command[6] = diff3Output.toAbsolutePath().toString();

        return command;
    }

    private static void deleteTempFiles(Path... files) throws IOException {
        for (Path file: files)
            Files.delete(file);
    }
}
