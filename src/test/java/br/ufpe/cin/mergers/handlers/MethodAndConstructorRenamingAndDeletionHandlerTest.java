package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;
import br.ufpe.cin.util.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;


public class MethodAndConstructorRenamingAndDeletionHandlerTest {
    private File baseFile = new File("testfiles/renaming/method/base_method/Test.java");
    private File renamedMethodFile1 = new File("testfiles/renaming/method/renamed_method_1/Test.java");
    private File renamedMethodFile2 = new File("testfiles/renaming/method/renamed_method_2/Test.java");

    private JFSTMerge jfstMerge = new JFSTMerge();
    private MergeContext mergeContext;

    @BeforeClass
    public static void setUpBeforeClass() {
        //hidding sysout output
        TestUtils.hideSystemOutput();
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenamesMethodDifferently_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;

        merge(renamedMethodFile1, renamedMethodFile2);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidn2(){inta;}>>>>>>>YOURS");

        merge(renamedMethodFile2, renamedMethodFile1);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "<<<<<<<MINEpublicvoidn2(){inta;}=======publicvoidn1(){inta;}>>>>>>>YOURS");
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
