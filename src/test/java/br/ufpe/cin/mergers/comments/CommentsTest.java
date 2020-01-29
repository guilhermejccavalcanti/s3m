package br.ufpe.cin.mergers.comments;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.util.TestUtils;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentsTest {

    private JFSTMerge merger = new JFSTMerge();

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
        TestUtils.hideSystemOutput();
    }

    @Test
    public void testComments_whenLeftEditAPrefixedComment_andRightEditTheSamePrefixedCommentInTheSameArea_shouldReportConflict() {
        File leftFile = new File("testfiles/comments/editedcomment/left/Test.java");
        File baseFile = new File("testfiles/comments/editedcomment/base/Test.java");
        File rightFile = new File("testfiles/comments/editedcomment/right/Test.java");

        String mergeOutput = merge(leftFile, baseFile, rightFile);
        assertThat(mergeOutput)
                .contains("/***Methodm<<<<<<<MINE*@authorAuthor=======*@parama>>>>>>>YOURS*/publicvoidm(inta){intb;}");
    }

    @Test
    public void testComments_whenLeftAddsACommentAtTheEndOfTheClass_andRightAddsTheSameCommentAtTheEndOfTheClass_shouldNotReportConflict() {
        File leftFile = new File("testfiles/comments/additionalcommentsatend/left/Test.java");
        File baseFile = new File("testfiles/comments/additionalcommentsatend/base/Test.java");
        File rightFile = new File("testfiles/comments/additionalcommentsatend/right/Test.java");

        String mergeOutput = merge(leftFile, baseFile, rightFile);
        assertThat(mergeOutput).isEqualTo("publicclassTest{publicvoidm(inta){intb;}inta;intc;//Additionalcomment.}");
    }

    @Test
    public void testComments_whenLeftClassHasOnlyAComment_andRightClassHasOnlyADifferentComment_shouldReportConflict() {
        File leftFile = new File("testfiles/comments/isolatedcomments/left/Test.java");
        File baseFile = new File("testfiles/comments/isolatedcomments/base/Test.java");
        File rightFile = new File("testfiles/comments/isolatedcomments/right/Test.java");

        String mergeOutput = merge(leftFile, baseFile, rightFile);
        assertThat(mergeOutput)
                .isEqualTo("publicclassTest{/*<<<<<<<MINE*Leftcomment.=======*Rightcomment.>>>>>>>YOURS*/}");
    }

    @Test
    public void testComments_whenLeftAddsACommentJustBeforeField_andRightRemovesTheField_shouldNotReportConflict() {
        File leftFile = new File("testfiles/comments/addandremovecomments/left/Test.java");
        File baseFile = new File("testfiles/comments/addandremovecomments/base/Test.java");
        File rightFile = new File("testfiles/comments/addandremovecomments/right/Test.java");

        String mergeOutput = merge(leftFile, baseFile, rightFile);
        assertThat(mergeOutput).isEqualTo("publicclassTest{//Fielda.}");
    }

    @Test
    public void testComments_whenLeftAddsACommentJustBeforeMethod_andRightRenamesTheMethod_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFELY_MERGE_SIMILAR;
        File leftFile = new File("testfiles/comments/addandrenamecomments/left/Test.java");
        File baseFile = new File("testfiles/comments/addandrenamecomments/base/Test.java");
        File rightFile = new File("testfiles/comments/addandrenamecomments/right/Test.java");

        String mergeOutput = merge(leftFile, baseFile, rightFile);
        assertThat(mergeOutput)
                .contains("/**<<<<<<<MINE*Methodm.*@authorAuthor=======*Methodm1.>>>>>>>YOURS*/publicvoidm1(){inta;}");
    }

    private String merge(File left, File base, File right) {
        MergeContext context = merger.mergeFiles(left, base, right, null);
        return getMergeOutput(context);
    }

    private String getMergeOutput(MergeContext mergeContext) {
        return FilesManager.getStringContentIntoSingleLineNoSpacing(mergeContext.semistructuredOutput);
    }

}