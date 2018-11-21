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
    private File baseFile = new File("testfiles/renaming/method/base_method/Test.java");
    private File renamedMethodFile1 = new File("testfiles/renaming/method/renamed_method_1/Test.java");
    private File renamedMethodFile2 = new File("testfiles/renaming/method/renamed_method_2/Test.java");
    private File addedMethodFile1 = new File("testfiles/renaming/method/added_method_1/Test.java");
    private File addedMethodFile2 = new File("testfiles/renaming/method/added_method_2/Test.java");

    private JFSTMerge jfstMerge = new JFSTMerge();
    private MergeContext mergeContext;

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
        TestUtils.hideSystemOutput();
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenameMethodDifferently_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        merge(renamedMethodFile1, renamedMethodFile2);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidn2(){inta;}>>>>>>>YOURS");

        merge(renamedMethodFile2, renamedMethodFile1);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn2(){inta;}=======publicvoidn1(){inta;}>>>>>>>YOURS");
    }

    @Test
    public void testMutualMethodRenaming_whenSomeVersionHasNotRenamedAnyMethodsOrConstructors_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        merge(addedMethodFile1, addedMethodFile2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn1(){inta;}publicvoidn2(){inta;}");

        merge(addedMethodFile2, addedMethodFile1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn2(){inta;}publicvoidn1(){inta;}");

        merge(renamedMethodFile1, addedMethodFile2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn1(){inta;}publicvoidn2(){inta;}");

        merge(addedMethodFile2, renamedMethodFile1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn2(){inta;}publicvoidn1(){inta;}");

        merge(addedMethodFile1, renamedMethodFile2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn1(){inta;}publicvoidn2(){inta;}");

        merge(renamedMethodFile2, addedMethodFile1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicvoidn2(){inta;}publicvoidn1(){inta;}");
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenameToSameName_andBodiesAreEqual_shouldNotReportConflict() {
        //TODO
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenameToSameName_andBodiesAreNotEqual_shouldReportConflict() {
        //TODO
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsAddVerySimilarMethods_andBodiesAreEqual_shouldNotReportConflict() {
        //TODO
    }

    @Test
    public void testMutualMethodRenaming_givenKeepBothMethodsIsEnabled_whenBothVersionsRenameMethodDifferently_shouldNotReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;

        merge(renamedMethodFile1, renamedMethodFile2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}publicvoidn2(){inta;}}");

        merge(renamedMethodFile2, renamedMethodFile1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn2(){inta;}publicvoidn1(){inta;}}");
    }

    @Test
    public void testMutualMethodRenaming_givenMergeRenamingsIsEnabled_whenBothVersionsRenameMethodDifferently_shouldMergeChanges() {
        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;

        merge(renamedMethodFile1, renamedMethodFile2);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidn2(){inta;}>>>>>>>YOURS");

        merge(renamedMethodFile2, renamedMethodFile1);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn2(){inta;}=======publicvoidn1(){inta;}>>>>>>>YOURS");
    }

    private void merge(File left, File right) {
        mergeContext = jfstMerge.mergeFiles(
                left,
                baseFile,
                right,
                null);
    }
}
