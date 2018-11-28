package br.ufpe.cin.mergers.handlers.singlerenaming;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;
import br.ufpe.cin.util.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

public class MergeMethodsSingleRenamingHandlerTest {
    private File baseFile = new File("testfiles/renaming/method/base_method/Test.java");
    private File bodyChangedFileBelowSignature = new File("testfiles/renaming/method/changed_body_below_signature/Test.java");
    private File bodyChangedAtEndFile = new File("testfiles/renaming/method/changed_body_at_end/Test.java");
    private File renamedMethodFile = new File("testfiles/renaming/method/renamed_method_1/Test.java");

    private JFSTMerge jfstMerge = new JFSTMerge();
    private File left, right;
    private MergeContext mergeContext;

    @BeforeClass
    public static void setUpBeforeClass() {
        TestUtils.hideSystemOutput();

        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_METHODS;
    }

    @Test
    public void testHandle_whenLeftRenamesMethod_andRightChangesBodyBelowSignature_shouldMergeChanges() {
        left = renamedMethodFile;
        right = bodyChangedFileBelowSignature;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta=123;}}");
    }

    @Test
    public void testHandle_whenRightRenamesMethod_andLeftChangesBodyBelowSignature_shouldMergeChanges() {
        left = bodyChangedFileBelowSignature;
        right = renamedMethodFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta=123;}}");
    }

    @Test
    public void testHandle_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldMergeChangess() {
        left = renamedMethodFile;
        right = bodyChangedAtEndFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;a=123;}}");
    }

    @Test
    public void testHandle_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldNotReportConflict() {
        left = bodyChangedAtEndFile;
        right = renamedMethodFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;a=123;}}");
    }

    private void merge() {
        mergeContext = jfstMerge.mergeFiles(
                left,
                baseFile,
                right,
                null);
    }
}
