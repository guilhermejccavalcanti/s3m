package br.ufpe.cin.mergers.util;

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

public final class CSDiffRunner {
    public static String runCSDiff(CSDiffScript script, String leftContent, String baseContent, String rightContent) throws TextualMergeException {
        try {
            File leftFile = createContributionFile("left", leftContent);
            File baseFile = createContributionFile("base", baseContent);
            File rightFile = createContributionFile("right", rightContent);
            File outputFile = FilesManager.createTempFile("output");

            runCSDiff(script, leftFile, baseFile, rightFile, outputFile);
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
            throw new TextualMergeException("Error during opening of temporary input file(s)");
        }
    }

    private static void runCSDiff(CSDiffScript script, File leftFile, File baseFile, File rightFile, File outputFile) throws TextualMergeException {
        try {
            File scriptFile = createScriptFile(script);
            String[] command = buildCSDiffCommand(scriptFile, leftFile, baseFile, rightFile, outputFile);
    
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (InterruptedException|IOException e) {
            throw new TextualMergeException("Error during CSDiff's execution");
        }
    }

    private static File createScriptFile(CSDiffScript script) throws TextualMergeException {
        try {
            InputStream scriptStream = CSDiffRunner.class.getResourceAsStream(script.getPath());
            InputStreamReader scriptStreamReader = new InputStreamReader(scriptStream, StandardCharsets.UTF_8);
            String scriptContent = CharStreams.toString(scriptStreamReader);

            File scriptFile = FilesManager.createTempFile("csdiff", ".sh");
            FilesManager.writeContent(scriptFile.getAbsolutePath(), scriptContent);
            return scriptFile;
        } catch (IOException e) {
            throw new TextualMergeException("Error during opening of temporary script file");
        }
    }

    private static String[] buildCSDiffCommand(File scriptFile, File leftFile, File baseFile, File rightFile, File outputFile) {
        String[] command = new String[6];
        command[0] = "sh";
        command[1] = scriptFile.getAbsolutePath();
        command[2] = leftFile.getAbsolutePath();
        command[3] = baseFile.getAbsolutePath();
        command[4] = rightFile.getAbsolutePath();
        command[5] = outputFile.getAbsolutePath();
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

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) result.append("\n");
            result.append(lines.get(i));
        }

        return result.toString();
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
