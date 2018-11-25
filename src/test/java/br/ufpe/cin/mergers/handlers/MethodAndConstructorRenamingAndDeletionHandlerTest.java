package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;
import br.ufpe.cin.util.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class MethodAndConstructorRenamingAndDeletionHandlerTest {
    private File base = new File("testfiles/renaming/method/base_method/Test.java");
    private File renamedMethod1 = new File("testfiles/renaming/method/renamed_method_1/Test.java");
    private File renamedMethod2 = new File("testfiles/renaming/method/renamed_method_2/Test.java");
    private File addedMethod1 = new File("testfiles/renaming/method/added_method_1/Test.java");
    private File addedMethod2 = new File("testfiles/renaming/method/added_method_2/Test.java");
    private File removedAndAddedNewMethod1 = new File("testfiles/renaming/method/removed_and_added_new_method_1/Test.java");
    private File removedAndAddedNewMethod2 = new File("testfiles/renaming/method/removed_and_added_new_method_2/Test.java");
    private File renamedMethod1AndAddedComment1 = new File("testfiles/renaming/method/renamed_method_1_and_added_comment_1/Test.java");
    private File renamedMethod1AndAddedComment2 = new File("testfiles/renaming/method/renamed_method_1_and_added_comment_2/Test.java");
    private File renamedMethod1AndChangedBody1 = new File("testfiles/renaming/method/renamed_method_1_and_changed_body/Test.java");

    private JFSTMerge jfstMerge = new JFSTMerge();
    private MergeContext mergeContext;

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
        TestUtils.hideSystemOutput();
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenameMethodDifferently_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        merge(renamedMethod1, renamedMethod2);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidn2(){inta;}>>>>>>>YOURS");

        merge(renamedMethod2, renamedMethod1);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn2(){inta;}=======publicvoidn1(){inta;}>>>>>>>YOURS");
    }

    @Test
    public void testMutualMethodRenaming_whenSomeVersionHasNotRenamedAnyMethodsOrConstructors_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        merge(addedMethod1, addedMethod2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn1(){inta;}publicvoidn2(){inta;}");

        merge(addedMethod2, addedMethod1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn2(){inta;}publicvoidn1(){inta;}");

        merge(renamedMethod1, addedMethod2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn1(){inta;}publicvoidn2(){inta;}");

        merge(addedMethod2, renamedMethod1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn2(){inta;}publicvoidn1(){inta;}");

        merge(addedMethod1, renamedMethod2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn1(){inta;}publicvoidn2(){inta;}");

        merge(renamedMethod2, addedMethod1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn2(){inta;}publicvoidn1(){inta;}");
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenameToSameName_andBodiesAreEqual_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        merge(renamedMethod1AndAddedComment1, renamedMethod1AndAddedComment2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}}");

        merge(renamedMethod1AndAddedComment2, renamedMethod1AndAddedComment1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}}");
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenameToSimilarNames_andBodiesAreSimilar_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        merge(renamedMethod2, renamedMethod1AndChangedBody1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn2(){inta;}publicvoidn1(){inta=2;}}");

        merge(renamedMethod1AndChangedBody1, renamedMethod2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta=2;}publicvoidn2(){inta;}}");
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsAddVerySimilarNamedMethods_andBodiesAreEqual_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        merge(removedAndAddedNewMethod1, removedAndAddedNewMethod2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidaVeryUnsimilarMethod1(){intaVeryUnsimilarVariable;}publicvoidaVeryUnsimilarMethod2(){intaVeryUnsimilarVariable;}");
    }

    @Test
    public void testMutualMethodRenaming_givenKeepBothMethodsIsEnabled_whenBothVersionsRenameMethodDifferently_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;

        merge(renamedMethod1, renamedMethod2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}publicvoidn2(){inta;}}");

        merge(renamedMethod2, renamedMethod1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn2(){inta;}publicvoidn1(){inta;}}");
    }

    @Test
    public void testMutualMethodRenaming_givenMergeRenamingsIsEnabled_whenBothVersionsRenameMethodDifferently_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        merge(renamedMethod1, renamedMethod2);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidn2(){inta;}>>>>>>>YOURS");

        merge(renamedMethod2, renamedMethod1);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn2(){inta;}=======publicvoidn1(){inta;}>>>>>>>YOURS");
    }

    @Test
    public void testMutualMethodRenaming_givenMergeRenamingsIsEnabled_whenSomeVersionHasNotRenamedAnyMethodsOrConstructors_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        merge(addedMethod1, addedMethod2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn1(){inta;}publicvoidn2(){inta;}");

        merge(addedMethod2, addedMethod1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn2(){inta;}publicvoidn1(){inta;}");

        merge(renamedMethod1, addedMethod2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn1(){inta;}publicvoidn2(){inta;}");

        merge(addedMethod2, renamedMethod1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn2(){inta;}publicvoidn1(){inta;}");

        merge(addedMethod1, renamedMethod2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn1(){inta;}publicvoidn2(){inta;}");

        merge(renamedMethod2, addedMethod1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn2(){inta;}publicvoidn1(){inta;}");
    }

    @Test
    public void testMutualMethodRenaming_givenMergeRenamingsIsEnabled_whenBothVersionsRenameToSameName_andBodiesAreEqual_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        merge(renamedMethod1AndAddedComment1, renamedMethod1AndAddedComment2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}}");

        merge(renamedMethod1AndAddedComment2, renamedMethod1AndAddedComment1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}}");
    }

    @Test
    public void testMutualMethodRenaming_givenMergeRenamingsIsEnabled_whenBothVersionsRenameToSimilarNames_andBodiesAreSimilar_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        merge(renamedMethod2, renamedMethod1AndChangedBody1);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "publicclassTest{<<<<<<<MINEpublicvoidn2(){inta;}=======publicvoidn1(){inta=2;}>>>>>>>YOURS}");

        merge(renamedMethod1AndChangedBody1, renamedMethod2);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "publicclassTest{<<<<<<<MINEpublicvoidn1(){inta=2;}=======publicvoidn2(){inta;}>>>>>>>YOURS}");
    }

    private void merge(File left, File right) {
        mergeContext = jfstMerge.mergeFiles(
                left,
                base,
                right,
                null);
    }
}
