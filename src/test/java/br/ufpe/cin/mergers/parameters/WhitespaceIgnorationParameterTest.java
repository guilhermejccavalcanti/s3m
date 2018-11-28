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

public class WhitespaceIgnorationParameterTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        //hidding sysout output
        @SuppressWarnings("unused")
        PrintStream originalStream = System.out;
        PrintStream hideStream    = new PrintStream(new OutputStream(){
            public void write(int b) {}
        });
        System.setOut(hideStream);
    }

    @Test
    public void testNoSpacingIgnoration() {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(getMergeContext(false).semistructuredOutput);
        assertThat(mergeResult).isEqualTo("publicclassTest{voidhelloWorld(){<<<<<<<MINESystem.out.println(\"HelloWorld!\");=======System.out.println(\"HelloWorld!\");>>>>>>>YOURS}<<<<<<<MINEStringhello=\"hello\";=======>>>>>>>YOURS}");
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
