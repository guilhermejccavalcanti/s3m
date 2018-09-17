package br.ufpe.cin.mergers.parameters;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class WhitespacesIgnorationParameterTest {

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
        assertThat(mergeResult).contains("publicclassTest{<<<<<<<MINEvoidhelloWorld(){System.out.println(\"HelloWorld!\");=======voidhelloWorld(){System.out.println(\"HelloWorld!\");>>>>>>>YOURS}}");
    }

    @Test
    public void testSpacingIgnoration() {
        int S3MNumConflicts = getMergeContext(true).semistructuredNumberOfConflicts;
        assertThat(S3MNumConflicts).isZero();
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
