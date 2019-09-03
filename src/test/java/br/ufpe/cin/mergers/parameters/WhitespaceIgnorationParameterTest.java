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

public class WhitespaceIgnorationParameterTest {

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
    public void testNoSpacingIgnoration() {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(getMergeContext(false).semistructuredOutput);
        assertThat(mergeResult).isEqualTo("publicclassTest{voidhelloWorld(){<<<<<<<MINESystem.out.println(\"HelloWorld!\");=======System.out.println(\"HelloWorld!\");>>>>>>>YOURS}<<<<<<<MINE=======>>>>>>>YOURS<<<<<<<MINEStringhello=\"hello\";=======>>>>>>>YOURS<<<<<<<MINE=======>>>>>>>YOURS}");
    }

    @Test
    public void testSpacingIgnoration() {
        MergeContext mergeContext = getMergeContext(true);

        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(mergeContext.semistructuredOutput);
        assertThat(mergeResult).isEqualTo("publicclassTest{voidhelloWorld(){System.out.println(\"HelloWorld!\");}}");
        assertThat(mergeContext.semistructuredNumberOfConflicts).isZero();
    }

    private MergeContext getMergeContext(boolean activation) {
        JFSTMerge.isWhitespaceIgnored = activation;
        MergeContext context = new JFSTMerge().mergeFiles(
                new File("testfiles/spacing/left.java"),
                new File("testfiles/spacing/base.java"),
                new File("testfiles/spacing/right.java"),
                null);
        return context;
    }

}
