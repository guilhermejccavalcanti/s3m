package br.ufpe.cin.mergers.structured;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.textual.Diff3;

import java.io.File;
import java.io.IOException;

public final class LastMerge {

    public static final String LAST_MERGE_SCRIPT_PATH = "dependencies/last-merge/release";

    public static String merge(String leftContent, String baseContent, String rightContent) throws TextualMergeException {
        File leftFile = null;
        File baseFile = null;
        File rightFile = null;
        File outputFile = null;

        try {
            leftFile = createContributionFile("left", leftContent);
            baseFile = createContributionFile("base", baseContent);
            rightFile = createContributionFile("right", rightContent);
            outputFile = FilesManager.createTempFile("output");

            int status = runLastMergeScript(leftFile, baseFile, rightFile, outputFile);
            return processLastMergeOutput(status, leftFile, baseFile, rightFile, outputFile);
        } catch (Exception e) {
            e.printStackTrace();
            return Diff3.merge(leftFile, baseFile, rightFile, JFSTMerge.isWhitespaceIgnored);
        }
    }

    private static File createContributionFile(String prefix, String content) throws IOException {
        return FilesManager.createContributionFile(prefix + System.currentTimeMillis(), content);
    }

    private static String processLastMergeOutput(int status, File leftFile, File baseFile, File rightFile, File outputFile)
            throws TextualMergeException {
        String mergeOutput;
        if ((status != LastMergeStatus.SUCCESS.getValue())
                && status != LastMergeStatus.CONFLICT.getValue()) { //means an internal last-merge error, so fallback to diff3
            mergeOutput = Diff3.merge(leftFile, baseFile, rightFile, JFSTMerge.isWhitespaceIgnored);
            return mergeOutput;
        }

        mergeOutput = FilesManager.readFileContent(outputFile);
        mergeOutput = fixConflictMarkers(mergeOutput);
        return mergeOutput;
    }

    private static String fixConflictMarkers(String s) {
        if (s.contains("<<<<<<<")) {
            s = s.replaceAll("<<<<<<<", "<<<<<<< MINE");
            s = s.replaceAll(">>>>>>>", ">>>>>>> YOURS");
        }
        return s;
    }

    private static int runLastMergeScript(File leftFile, File baseFile, File rightFile, File outputFile) throws Exception {
        String[] command = {
                "./last-merge",
                "merge",
                "--base-path", baseFile.getAbsolutePath(),
                "--left-path", leftFile.getAbsolutePath(),
                "--right-path", rightFile.getAbsolutePath(),
                "--merge-path", outputFile.getAbsolutePath(),
                "--language=java"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(LAST_MERGE_SCRIPT_PATH));

        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        System.out.println("LastMerge process exited with code: " + exitCode);

        return exitCode;
    }

//    public static void main(String[] args) {
//        // Define the command and arguments
//        String[] command = {
//                "./last-merge",
//                "merge",
//                "--base-path", "/home/gjcc/dev/s3m/testfiles/lastmerge/conflict/base.java",
//                "--left-path", "/home/gjcc/dev/s3m/testfiles/lastmerge/conflict/left.java",
//                "--right-path", "/home/gjcc/dev/s3m/testfiles/lastmerge/conflict/right.java",
//                "--merge-path", "/home/gjcc/Desktop/out.java",
//                "--language=java"
//        };
//
//        ProcessBuilder processBuilder = new ProcessBuilder(command);
//        processBuilder.directory(new File("dependencies/last-merge/release"));
//
//        try {
//            Process process = processBuilder.start();
//
//            int exitCode = process.waitFor();
//            System.out.println("Process exited with code: " + exitCode);
//            String s = fixConflictMarkers(FilesManager.readFileContent(
//                    new File("/home/gjcc/Desktop/out.java")));
//            System.out.println(s);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}

enum LastMergeStatus {
    CONFLICT(1),
    SUCCESS(0);

    private final int value;

    LastMergeStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}