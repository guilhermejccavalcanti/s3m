package br.ufpe.cin.mergers.textual;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.CSDiffRunner;
import br.ufpe.cin.mergers.util.CSDiffScript;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;

public class ConsecutiveLines implements TextualMergeStrategy {
    private static final String linesIntervalPattern = "\\d+(,)?\\d*";
    private static final CSDiffScript script = CSDiffScript.ConsecutiveLines;

    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
        return CSDiffRunner.runCSDiff(script, leftContent, baseContent, rightContent);
    }

    private static boolean thereAreChangesOnConsecutiveLines(String leftContent, String baseContent, String rightContent) throws TextualMergeException {
        Set<Integer> leftContributionLines = getContributionLines(leftContent, baseContent);
        Set<Integer> rightContributionLines = getContributionLines(rightContent, baseContent);

        for (int lineNumber: rightContributionLines) {
            if (leftContributionLines.contains(lineNumber - 1) || leftContributionLines.contains(lineNumber + 1))
                return true;
        }

        return false;
    }

    private static Set<Integer> getContributionLines(String parentContent, String baseContent) throws TextualMergeException {
        File parentFile, baseFile;
        try {
            parentFile = FilesManager.createTempFile("parent");
            FilesManager.writeContent(parentFile.getAbsolutePath(), parentContent);
    
            baseFile = FilesManager.createTempFile("base");
            FilesManager.writeContent(baseFile.getAbsolutePath(), baseContent);
        } catch (IOException e) {
            throw new TextualMergeException("Error during opening of temporary files");
        }

        List<String> diffOutput = runDiff(parentFile, baseFile);

        Set<Integer> contributionLines = new HashSet<Integer>();
        for (String outputLine: diffOutput) {
            if (outputLine.matches(linesIntervalPattern + "[acd]" + linesIntervalPattern)) {
                String linesInterval = outputLine.split("[acd]")[1];

                String[] bounds = linesInterval.split(",");
                if (bounds.length == 1) {
                    contributionLines.add(Integer.parseInt(bounds[0]));
                } else {
                    int leftBound = Integer.parseInt(bounds[0]);
                    int rightBound = Integer.parseInt(bounds[1]);

                    for (int i = leftBound; i <= rightBound; i++)
                        contributionLines.add(i);
                }
            }
        }

        return contributionLines;
    }

    private static List<String> runDiff(File parentFile, File baseFile) throws TextualMergeException {
        try {
            String[] command = buildDiffCommand(parentFile, baseFile);
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            InputStreamReader processOutput = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(processOutput);
            return reader.lines().collect(Collectors.toList());
        } catch (InterruptedException|IOException e) {
            throw new TextualMergeException("Error during diff's execution");
        }
    }

    private static String[] buildDiffCommand(File parentFile, File baseFile) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().trim().startsWith("windows");

        String[] command = new String[3];
        command[0] = isWindows ? "C:/KDiff3/bin/diff.exe" : "diff";
        command[1] = parentFile.getAbsolutePath();
        command[2] = baseFile.getAbsolutePath();

        return command;
    }
}
