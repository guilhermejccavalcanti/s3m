package br.ufpe.cin.mergers.textual;

import static org.assertj.core.api.Assertions.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;
import br.ufpe.cin.util.TestUtils;

public class ConsecutiveLinesTest {
    private static TextualMergeStrategy originalStrategy;

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
        //hidding sysout output
        @SuppressWarnings("unused")
        PrintStream originalStream = System.out;
        PrintStream hideStream = new PrintStream(new OutputStream() {
            public void write(int b) {}
        }, true, Charset.defaultCharset().displayName());
        System.setOut(hideStream);
    }

    @BeforeClass
    public static void setUpTextualMergeStrategy() {
        originalStrategy = JFSTMerge.textualMergeStrategy;
        JFSTMerge.textualMergeStrategy = new ConsecutiveLines();
    }

    @AfterClass
    public static void retrieveTextualMergeStrategy() {
        JFSTMerge.textualMergeStrategy = originalStrategy;
    }

    @Test
    public void testChangesToConsecutiveLines() {
        String testFilesPath = "consecutivelines";
        String mergeOutput = TestUtils.mergeTestFiles(testFilesPath);

        String expectedOutput = TestUtils.getTestExpectedOutput(testFilesPath);
        String actualOutput = FilesManager.getStringContentIntoSingleLineNoSpacing(mergeOutput);

        assertThat(actualOutput).isEqualTo(expectedOutput);
    }
}
