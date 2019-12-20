package br.ufpe.cin.unit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;

public class SuperimpositionTest {

    private final Path TEST_FILES_DIRECTORY = Paths.get("testfiles/superimposition");

    @BeforeClass
    public static void disableHandlers() {
        JFSTMerge.isDuplicatedDeclarationHandlerEnabled = false;
        JFSTMerge.isInitializationBlocksHandlerEnabled = false;
        JFSTMerge.isMethodAndConstructorRenamingAndDeletionHandlerEnabled = false;
        JFSTMerge.isNewElementReferencingEditedOneHandlerEnabled = false;
        JFSTMerge.isTypeAmbiguityErrorHandlerEnabled = false;
    }

    @BeforeClass
    public static void enableHandlers() {
        JFSTMerge.isDuplicatedDeclarationHandlerEnabled = true;
        JFSTMerge.isInitializationBlocksHandlerEnabled = true;
        JFSTMerge.isMethodAndConstructorRenamingAndDeletionHandlerEnabled = true;
        JFSTMerge.isNewElementReferencingEditedOneHandlerEnabled = true;
        JFSTMerge.isTypeAmbiguityErrorHandlerEnabled = true;
    }

    @Test
    public void testWhenTwoDevelopersDeleteAMethod_AndAddAnInnerClassContainingAMethodWithSameSignatureAsDeletedOne_shouldDeleteIt()
            throws IOException {
        Path files = TEST_FILES_DIRECTORY.resolve("deleted-innerclass");

        String mergeResult = merge(new JFSTMerge(), files);
        assertEquals(mergeResult, FileUtils.readFileToString(files.resolve("merge.java").toFile()));
    }

    private String merge(JFSTMerge merger, Path filesPath) {
        return merger.mergeFiles(filesPath.resolve("left/Test.java").toFile(),
                filesPath.resolve("base/Test.java").toFile(), filesPath.resolve("right/Test.java").toFile(),
                null).semistructuredOutput;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
        // hidding sysout output
        @SuppressWarnings("unused")
        PrintStream originalStream = System.out;
        PrintStream hideStream = new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }, true, Charset.defaultCharset().displayName());
        System.setOut(hideStream);
    }

}