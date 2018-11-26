package br.ufpe.cin.mergers.regular;

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

import static org.assertj.core.api.Assertions.assertThat;

public class CommentsTest {

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
    public void testAddedCommentsInBothContributions() {
        MergeContext ctx = new JFSTMerge().mergeFiles(
                new File("testfiles/comments/bothcontributions/left.java"),
                new File("testfiles/comments/bothcontributions/base.java"),
                new File("testfiles/comments/bothcontributions/right.java"),
                null
        );

        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
        assertThat(mergeResult).isEqualTo("publicclassTest{<<<<<<<MINE/*HelloWorld!*/=======/*Hello,World!*/>>>>>>>YOURSprivateStringhelloWorld;}");
        assertThat(ctx.semistructuredNumberOfConflicts).isOne();
    }

    @Test
    public void testAddedAdditionalCommentsInRight() {
        MergeContext ctx = new JFSTMerge().mergeFiles(
                new File("testfiles/comments/additionalcommentinright/left.java"),
                new File("testfiles/comments/additionalcommentinright/base.java"),
                new File("testfiles/comments/additionalcommentinright/right.java"),
                null
        );

        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
        assertThat(mergeResult).isEqualTo("publicclassTest{<<<<<<<MINE/*Hello,World!*/=======//Rightincommand./*Hello,World.*/>>>>>>>YOURSprivateStringhelloWorld;}");
        assertThat(ctx.semistructuredNumberOfConflicts).isOne();
    }

    @Test
    public void testIsolatedCommentsInAllContributions() {
        MergeContext ctx = new JFSTMerge().mergeFiles(
                new File("testfiles/comments/isolatedcomments/left.java"),
                new File("testfiles/comments/isolatedcomments/base.java"),
                new File("testfiles/comments/isolatedcomments/right.java"),
                null
        );

        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
        assertThat(mergeResult).isEqualTo("publicclassTest{}");
        assertThat(ctx.semistructuredNumberOfConflicts).isZero();
    }

    @Test
    public void testAddedSingleLineCommentsJustAfterElements() {
        MergeContext ctx = new JFSTMerge().mergeFiles(
                new File("testfiles/comments/singlelinecomments/left.java"),
                new File("testfiles/comments/singlelinecomments/base.java"),
                new File("testfiles/comments/singlelinecomments/right.java"),
                null
        );

        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
        assertThat(mergeResult).isEqualTo("publicclassTest{privatebooleangettingAttention;publicvoidgiveAttention(){}}");
        assertThat(ctx.semistructuredNumberOfConflicts).isZero();
    }


}
