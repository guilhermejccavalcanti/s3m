package br.ufpe.cin.mergers.structured;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.textual.Sepmerge;
import br.ufpe.cin.mergers.util.MergeContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertTrue;

public class LastMergeTest {

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
        @SuppressWarnings("unused")
        PrintStream originalStream = System.out;
        PrintStream hideStream    = new PrintStream(new OutputStream(){
            public void write(int b) {}
        }, true, Charset.defaultCharset().displayName());
        System.setOut(hideStream);
    }

    @Test
    public void testS3mConfiguredWithLastMerge_withConflict() {
        JFSTMerge s3m = new JFSTMerge();
        JFSTMerge.isStructured = true;

        MergeContext ctx = 	s3m.mergeFiles(
                new File("testfiles/lastmerge/conflict/left.java"),
                new File("testfiles/lastmerge/conflict/base.java"),
                new File("testfiles/lastmerge/conflict/right.java"),
                null);
        assertTrue(
                FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
                        .equals("publicclassTest{voidmethod(){intnum=<<<<<<<MINE20=======30>>>>>>>YOURS+20;System.out.println(num);}}")
        );
    }

    @Test
    public void testS3mConfiguredWithLastMerge_noConflict() {
        JFSTMerge s3m = new JFSTMerge();
        JFSTMerge.isStructured = true;

        MergeContext ctx = 	s3m.mergeFiles(
                new File("testfiles/lastmerge/noconflict/left.java"),
                new File("testfiles/lastmerge/noconflict/base.java"),
                new File("testfiles/lastmerge/noconflict/right.java"),
                null);
        assertTrue(
                FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
                        .equals("publicclassTest{voidmethod(){for(inti=1;i<11;i++){System.out.println(i);}}}")
        );
    }
}
