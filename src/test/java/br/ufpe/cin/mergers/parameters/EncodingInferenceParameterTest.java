package br.ufpe.cin.mergers.parameters;

import static org.assertj.core.api.Assertions.*;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class EncodingInferenceParameterTest {

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
        //hidding sysout output
        @SuppressWarnings("unused")
        PrintStream originalStream = System.out;
        PrintStream hideStream    = new PrintStream(new OutputStream(){
            public void write(int b) {}
        }, true, Charset.defaultCharset().displayName());
        System.setOut(hideStream);
    }

    @Test
    public void testEncodingInferenceDisabled() {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(getMergeResult(false));
        // The first two characters are weird ones.
        assertThat(mergeResult.substring(2)).isEqualTo("publicclassTest{voidhelloWorld(){System.out.println(\"HelloWorld!\");}}");
    }

    @Test
    public void testEncodingInferenceEnabled() {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(getMergeResult(true));
        // The first character is a special one from UTF-16.
        assertThat(mergeResult.substring(1)).isEqualTo("publicclassTest{voidhelloWorld(){System.out.println(\"HelloWorld!\");}}");
    }

    private String getMergeResult(boolean activation) {
        JFSTMerge.isEncodingInferenceEnabled = activation;
        MergeContext context = new JFSTMerge().mergeFiles(
                new File("testfiles/differentencodings/left.java"),
                new File("testfiles/differentencodings/base.java"),
                new File("testfiles/differentencodings/right.java"),
                null);
        return context.semistructuredOutput;
    }

}

