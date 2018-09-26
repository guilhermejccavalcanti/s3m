package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodAndConstructorRenamingAndDeletionHandlerTest {
    private File baseFile = new File("testfiles/renaming/method/base_method/Test.java");
    private File bodyChangedFileBelowSignature = new File("testfiles/renaming/method/changed_body_below_signature/Test.java");
    private File bodyChangedAtEndFile = new File("testfiles/renaming/method/changed_body_at_end/Test.java");
    private File renamedMethodFile1 = new File("testfiles/renaming/method/renamed_method_1/Test.java");
    private File renamedMethodFile2 = new File("testfiles/renaming/method/renamed_method_2/Test.java");

    private JFSTMerge jfstMerge = new JFSTMerge();

    @BeforeClass
    public static void setUpBeforeClass() {
        //hidding sysout output
        @SuppressWarnings("unused")
        PrintStream originalStream = System.out;
        PrintStream hideStream = new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        });
        System.setOut(hideStream);
    }

    @Test
    public void testMethodRenamingOnLeft_whenLeftRenamesMethod_andRightChangesBodyBelowSignature_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedFileBelowSignature,
                null);

        verifyMergeResultWithConflict(mergeContext, "<<<<<<<MINEpublicvoidm(){inta=123;}=======publicvoidn1(){inta;}>>>>>>>YOURS");
    }

    @Test
    public void testMethodRenamingOnRight_whenRightRenamesMethod_andLeftChangesBodyBelowSignature_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                bodyChangedFileBelowSignature,
                baseFile,
                renamedMethodFile1,
                null);

        verifyMergeResultWithConflict(mergeContext, "<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidm(){inta=123;}>>>>>>>YOURS");
    }

    @Test
    public void testMethodRenamingOnLeft_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedAtEndFile,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}publicvoidm(){inta;a=123;}}");
    }

    @Test
    public void testMethodRenamingOnRight_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                bodyChangedAtEndFile,
                baseFile,
                renamedMethodFile1,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidm(){inta;a=123;}publicvoidn1(){inta;}}");
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenamesMethodDifferently_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                renamedMethodFile1,
                baseFile,
                renamedMethodFile2,
                null);

        verifyMergeResultWithConflict(mergeContext, "<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidn2(){inta;}>>>>>>>YOURS");
    }

    @Test
    public void testMethodRenamingOnLeft_givenKeepBothMethodsIsEnabled_whenLeftRenamesMethod_andRightChangesBodyBelowSignature_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedFileBelowSignature,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}publicvoidm(){inta=123;}}");
    }

    @Test
    public void testMethodRenamingOnRight_givenKeepBothMethodsIsEnabled_whenRightRenamesMethod_andLeftChangesBodyBelowSignature_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                bodyChangedFileBelowSignature,
                baseFile,
                renamedMethodFile1,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidm(){inta=123;}publicvoidn1(){inta;}}");
    }

    @Test
    public void testMethodRenamingOnLeft_givenKeepBothMethodsIsEnabled_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedAtEndFile,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}publicvoidm(){inta;a=123;}}");
    }

    @Test
    public void testMethodRenamingOnRight_givenKeepBothMethodsIsEnabled_whenLeftRenamesMethod_andRightnChangesBodyAtEnd_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                bodyChangedAtEndFile,
                baseFile,
                renamedMethodFile1,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidm(){inta;a=123;}publicvoidn1(){inta;}}");
    }

    @Test
    public void testMutualMethodRenaming_givenKeepBothMethodsIsEnabled_whenBothVersionsRenameMethodDifferently_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                renamedMethodFile1,
                baseFile,
                renamedMethodFile2,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}publicvoidn2(){inta;}}");
    }

    @Test
    public void testMethodRenamingOnLeft_givenMergeRenamingsIsEnabled_whenLeftRenamesMethod_andRightChangesBodyBelowSignature_shouldMergeChanges() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedFileBelowSignature,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidn1(){inta=123;}}");
    }

    @Test
    public void testMethodRenamingOnRight_givenMergeRenamingsIsEnabled_whenRightRenamesMethod_andLeftChangesBodyBelowSignature_shouldMergeChanges() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                bodyChangedFileBelowSignature,
                baseFile,
                renamedMethodFile1,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidn1(){inta=123;}}");
    }

    @Test
    public void testMethodRenamingOnLeft_givenMergeRenamingsIsEnabled_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldMergeChangess() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedAtEndFile,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;a=123;}}");
    }

    @Test
    public void testMethodRenamingOnRight_givenMergeRenamingsIsEnabled_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldMergeChanges() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                bodyChangedAtEndFile,
                baseFile,
                renamedMethodFile1,
                null);

        verifyMergeResultWithoutConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;a=123;}}");
    }

    @Test
    public void testMutualMethodRenaming_givenMergeRenamingsIsEnabled_whenBothVersionsRenameMethodDifferently_shouldMergeChanges() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        MergeContext mergeContext = jfstMerge.mergeFiles(
                renamedMethodFile1,
                baseFile,
                renamedMethodFile2,
                null);

        verifyMergeResultWithConflict(mergeContext, "<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidn2(){inta;}>>>>>>>YOURS");
    }

    private void verifyMergeResultWithConflict(MergeContext context, String expectedResult) {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(context.semistructuredOutput);
        assertThat(mergeResult).contains(expectedResult);
        assertThat(context.renamingConflicts).isOne();
    }

    private void verifyMergeResultWithoutConflict(MergeContext context, String expectedResult) {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(context.semistructuredOutput);
        assertThat(mergeResult).contains(expectedResult);
        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(context.renamingConflicts).isZero();
    }
}
