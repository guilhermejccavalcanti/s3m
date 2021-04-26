package br.ufpe.cin.mergers.textual;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import com.google.common.io.CharStreams;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;

public class CSDiff implements TextualMergeStrategy {
    private static final String tempPath = System.getProperty("java.io.tmpdir");

    private static final String CSDiffScriptPath = "/csdiff.sh";
    private static final String CSDiffOutputFileName = "csdiff-output";
    private static final String diff3OutputFileName = "diff3-output";
    private static final String gitMergeOutputFileName = "git_merge.java";

    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
        try {
            File leftFile = createContributionFile("left", leftContent);
            File baseFile = createContributionFile("base", baseContent);
            File rightFile = createContributionFile("right", rightContent);
            File outputFile = createTempJavaFile(CSDiffOutputFileName);
            
            runCSDiff(leftFile, baseFile, rightFile, outputFile);
            return FilesManager.readFileContent(outputFile);
        } catch (IOException e) {
            throw new TextualMergeException("Error during opening/closing of temporary files");
        } catch (InterruptedException e) {
            throw new TextualMergeException("Error during CSDiff's execution");
        }
    }
    
    private static File createContributionFile(String name, String content) throws IOException {
        File file = createTempJavaFile(name);
        FilesManager.writeContent(file.getAbsolutePath(), content);
        return file;
    }
    
    private static File createTempJavaFile(String name) throws IOException {
        File file = File.createTempFile(name, ".java");
        file.deleteOnExit();
        return file;
    }
    
    private static void runCSDiff(File left, File base, File right, File output) throws IOException, InterruptedException {
        File script = createScriptFile();
        File diff3Output = createTempJavaFile(diff3OutputFileName);

        String[] command = buildCommand(script, left, base, right, output, diff3Output);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        // Deletes CSDiff's generated git_merge.java file
        Paths.get(tempPath, gitMergeOutputFileName).toFile().delete();
    }

    private static File createScriptFile() throws IOException {
        InputStream scriptStream = CSDiff.class.getResourceAsStream(CSDiffScriptPath);
        InputStreamReader scriptStreamReader = new InputStreamReader(scriptStream);
        String content = CharStreams.toString(scriptStreamReader);
        
        File file = File.createTempFile("csdiff", ".sh");
        FilesManager.writeContent(file.getAbsolutePath(), content);
        file.deleteOnExit();
        return file;
    }

    private static String[] buildCommand(File script, File left, File base, File right, File CSDiffOutput, File diff3Output) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().trim().startsWith("windows");

        String[] command = new String[7];
        command[0] = isWindows ? "" : "sh";
        command[1] = script.getAbsolutePath();
        command[2] = left.getAbsolutePath();
        command[3] = base.getAbsolutePath();
        command[4] = right.getAbsolutePath();
        command[5] = CSDiffOutput.getAbsolutePath();
        command[6] = diff3Output.getAbsolutePath();

        return command;
    }
}
