package br.ufpe.cin.mergers.textual;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.apache.commons.lang.SystemUtils;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;
import br.ufpe.cin.util.TestUtils;

public class CSDiffTest {
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
        JFSTMerge.textualMergeStrategy = new CSDiff();
    }

    @AfterClass
    public static void retrieveTextualMergeStrategy() {
        JFSTMerge.textualMergeStrategy = originalStrategy;
    }

    @Test
    public void testChangesToConsecutiveLines() {
        Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
        String testFilesPath = "consecutivelines";
        TestUtils.testMerge(testFilesPath);
    }

    @Test
    public void testChangesToDifferentArgumentsOfSameMethod() {
        Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
        String testFilesPath = Paths.get("methodarguments", "different").toString();
        TestUtils.testMerge(testFilesPath);
    }

    @Test
    public void testChangesToSameArgumentsOfSameMethod() {
        Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
        String testFilesPath = Paths.get("methodarguments", "same").toString();
        TestUtils.testMerge(testFilesPath);
    }

    @Test
    public void testChangesToArithmeticExpressionWithParentheses() {
        Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
        String testFilesPath = "arithmeticexpression";
        TestUtils.testMerge(testFilesPath);
    }
}
