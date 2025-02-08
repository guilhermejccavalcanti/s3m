package br.ufpe.cin.mergers.textual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class SepmergeTest {

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
    public void testS3mConfiguredWithSepmerge() {
        JFSTMerge s3m = new JFSTMerge();
        s3m.textualMergeStrategy = new Sepmerge();

        MergeContext ctx = 	s3m.mergeFiles(
                new File("testfiles/sepmerge/left.java"),
                new File("testfiles/sepmerge/base.java"),
                new File("testfiles/sepmerge/right.java"),
                null);
        assertTrue(
                FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
                        .equals("publicclassTest{StringtoString(List<T>l){inta=20;intb=20;<<<<<<<MINEintc=20=======intc=30>>>>>>>YOURS;for(inti=1;i<11;i++){System.out.println(i);}if(l==null||l.isEmpty()){returnEMPTY;}returnString.join(\",\",l);}}")
        );
    }
}
