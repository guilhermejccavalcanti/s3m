package br.ufpe.cin.mergers.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

            String leftFilePath = leftFile.getAbsolutePath();
            String baseFilePath = baseFile.getAbsolutePath();
            String rightFilePath = rightFile.getAbsolutePath();

            return fixConflictMarkers(output, leftFilePath, baseFilePath, rightFilePath);
            // return output;
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
        String[] command = new String[!JFSTMerge.showBase ? 6 : 7];

        command[0] = "sh";
        command[1] = scriptFile.getAbsolutePath();
        command[2] = leftFile.getAbsolutePath();
        command[3] = baseFile.getAbsolutePath();
        command[4] = rightFile.getAbsolutePath();
        command[5] = outputFile.getAbsolutePath();

        if (JFSTMerge.showBase) {
            command[6] = "--show-base";
        }

        return command;
    }

    public static String fixConflictMarkers(
        String mergeContent,
        String leftFilePath,
        String baseFilePath,
        String rightFilePath
    ) throws TextualMergeException {
        String[] conflictMarkers = getConflictMarkers(leftFilePath, baseFilePath, rightFilePath);

        List<String> lines = new ArrayList<String>(Arrays.asList(mergeContent.split("\\R")));
        List<String> newLines = removeConflictMarkersFromLineEndings(lines, conflictMarkers);

        replaceConflictMarkers(newLines);
        return linesToString(newLines);
    }

    private static String[] getConflictMarkers(String leftFilePath, String baseFilePath, String rightFilePath) {
        String[] conflictMarkers = new String[4];
        conflictMarkers[0] = "<<<<<<< " + leftFilePath;
        conflictMarkers[1] = "||||||| " + baseFilePath;
        conflictMarkers[2] = "=======";
        conflictMarkers[3] = ">>>>>>> " + rightFilePath;
        return conflictMarkers;
    }

    private static List<String> removeConflictMarkersFromLineEndings(
        List<String> lines,
        String[] conflictMarkers
    ) throws TextualMergeException {
        List<String> newLines = new ArrayList<String>();

        for (String line: lines) {
            if (!lineEndsWithConflictMarker(line, conflictMarkers)) {
                newLines.add(line);
            } else {
                String conflictMarker = extractConflictMarker(line, conflictMarkers);
                String newLine = line.replace(conflictMarker, "");

                newLines.add(newLine);
                newLines.add(conflictMarker);
            }
        }

        return newLines;
    }

    private static void replaceConflictMarkers(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("<<<<<<<"))
                lines.set(i, MergeConflict.MINE_CONFLICT_MARKER);
            else if (lines.get(i).startsWith("|||||||"))
                lines.set(i, MergeConflict.BASE_CONFLICT_MARKER);
            else if (lines.get(i).startsWith(">>>>>>>"))
                lines.set(i, MergeConflict.YOURS_CONFLICT_MARKER);
        }
    }

    private static String linesToString(List<String> lines) {
        return lines.stream().collect(Collectors.joining("\n"));
    }

    private static boolean lineEndsWithConflictMarker(String line, String[] conflictMarkers) {
        for (String conflictMarker: conflictMarkers) {
            if (!line.startsWith(conflictMarker) && line.endsWith(conflictMarker))
                return true;
        }

        return false;
    }

    private static String extractConflictMarker(String line, String[] conflictMarkers) throws TextualMergeException {
        for (String conflictMarker: conflictMarkers) {
            if (line.endsWith(conflictMarker))
                return conflictMarker;
        }

        throw new TextualMergeException("Line doesn't end with any conflict marker");
    }
}
