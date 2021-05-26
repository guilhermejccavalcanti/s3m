package br.ufpe.cin.util;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {
    private static final String TEST_FILE_NAME = "Test.java";

    public static void hideSystemOutput() throws UnsupportedEncodingException {
        PrintStream hideStream = new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }, true, Charset.defaultCharset().displayName());
        System.setOut(hideStream);
    }

    public static void verifyMergeResultWithRenamingConflict(MergeContext mergeContext, String expectedResult) {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(mergeContext.semistructuredOutput);
        assertThat(mergeResult).contains(expectedResult);
        assertThat(mergeContext.renamingConflicts).isOne();
    }

    public static void verifyMergeResultWithoutRenamingConflict(MergeContext mergeContext, String expectedResult) {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(mergeContext.semistructuredOutput);
        assertThat(mergeResult).contains(expectedResult);
        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(mergeContext.renamingConflicts).isZero();
    }

    public static String mergeTestFiles(String testFilesPath) {
        return new JFSTMerge().mergeFiles(
            new File(testFilesPath + "left/" + TEST_FILE_NAME),
            new File(testFilesPath + "base/" + TEST_FILE_NAME),
            new File(testFilesPath + "right/" + TEST_FILE_NAME),
            null
        ).semistructuredOutput;
    }

    public static String getTestExpectedOutput(String testFilesPath) {
        File mergeFile = new File(testFilesPath + "merge/" + TEST_FILE_NAME);
        String content = FilesManager.readFileContent(mergeFile);
        return FilesManager.getStringContentIntoSingleLineNoSpacing(content);
    }
}
