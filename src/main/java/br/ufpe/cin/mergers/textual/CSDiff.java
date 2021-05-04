package br.ufpe.cin.mergers.textual;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.io.CharStreams;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;

public class CSDiff implements TextualMergeStrategy {
    private static final String CSDiffScriptPath = "/csdiff.sh";

    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
        try {
            File leftFile = createContributionFile("left", leftContent);
            File baseFile = createContributionFile("base", baseContent);
            File rightFile = createContributionFile("right", rightContent);
            File outputFile = FilesManager.createTempFile("output");
            
            runCSDiff(leftFile, baseFile, rightFile, outputFile);
            String output = FilesManager.readFileContent(outputFile);
            return fixConflictMarkers(output);
        } catch (IOException e) {
            throw new TextualMergeException("Error during opening of temporary output file");
        }
    }
    
    private static File createContributionFile(String name, String content) throws TextualMergeException {
        try {
            File file = FilesManager.createTempFile(name);
            FilesManager.writeContent(file.getAbsolutePath(), content);
            return file;
        } catch (IOException e) {
            throw new TextualMergeException("Error during opening of temporary files");
        }
    }
    
    private static void runCSDiff(File left, File base, File right, File output) throws TextualMergeException {
        try {
            File script = createScriptFile();
            String[] command = buildCommand(script, left, base, right, output);

            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (InterruptedException|IOException e) {
            throw new TextualMergeException("Error during CSDiff's execution");
        }
    }

    private static File createScriptFile() throws TextualMergeException {
        try {
            InputStream scriptStream = CSDiff.class.getResourceAsStream(CSDiffScriptPath);
            InputStreamReader scriptStreamReader = new InputStreamReader(scriptStream, StandardCharsets.UTF_8);
            String content = CharStreams.toString(scriptStreamReader);
            
            File file = File.createTempFile("csdiff", ".sh");
            FilesManager.writeContent(file.getAbsolutePath(), content);
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            throw new TextualMergeException("Error during opening of temporary script file");
        }
    }

    private static String[] buildCommand(File script, File left, File base, File right, File output) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().trim().startsWith("windows");

        String[] command = new String[6];
        command[0] = isWindows ? "" : "sh"; //TODO: add command for Windows
        command[1] = script.getAbsolutePath();
        command[2] = left.getAbsolutePath();
        command[3] = base.getAbsolutePath();
        command[4] = right.getAbsolutePath();
        command[5] = output.getAbsolutePath();

        return command;
    }

    private static String fixConflictMarkers(String output) {
        List<String> lines = new ArrayList<String>(Arrays.asList(output.split("\\R")));

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("<<<<<<<"))
                lines.set(i, MergeConflict.MINE_CONFLICT_MARKER);
            else if (lines.get(i).startsWith("|||||||"))
                lines.set(i, MergeConflict.BASE_CONFLICT_MARKER);
            else if (lines.get(i).startsWith(">>>>>>>"))
                lines.set(i, MergeConflict.YOURS_CONFLICT_MARKER);
        }

        if (!JFSTMerge.showBase)
            removeBaseFromConflicts(lines);

        String result = "";
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) result += "\n";
            result += lines.get(i);
        }

        return result;
    }

    private static void removeBaseFromConflicts(List<String> lines) {
        int index = 0;
        boolean inBase = false;

        while (index < lines.size()) {
            if (lines.get(index).equals(MergeConflict.BASE_CONFLICT_MARKER)) {
                inBase = true;
                lines.remove(index);
            } else if (lines.get(index).equals(MergeConflict.CHANGE_CONFLICT_MARKER)) {
                index++;
                inBase = false;
            } else if (inBase) {
                lines.remove(index);
            } else {
                index++;
            }
        }
    }
}
