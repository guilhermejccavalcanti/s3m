package br.ufpe.cin.mergers.handlers.singlerenaming;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;
import br.ufpe.cin.util.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class MergeMethodsSingleRenamingHandlerTest {
    private File baseFile = new File("testfiles/renaming/method/base_method/Test.java");
    private File bodyChangedFileBelowSignature = new File("testfiles/renaming/method/changed_body_below_signature/Test.java");
    private File bodyChangedAtEndFile = new File("testfiles/renaming/method/changed_body_at_end/Test.java");
    private File renamedMethodFile = new File("testfiles/renaming/method/renamed_method_1/Test.java");
    private File renamedMethodAndChangedBodyFile = new File("testfiles/renaming/method/renamed_method_1_and_changed_body_at_end/Test.java");

    private JFSTMerge jfstMerge = new JFSTMerge();
    private File left, right;
    private MergeContext mergeContext;

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
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
    public void testHandle_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldMergeChanges() {
        left = renamedMethodFile;
        right = bodyChangedAtEndFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;a=123;}}");
    }

    @Test
    public void testHandle_whenRightRenamesMethod_andLeftChangesBodyAtEnd_shouldNotReportConflict() {
        left = bodyChangedAtEndFile;
        right = renamedMethodFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;a=123;}}");
    }

    @Test
    public void testHandle_whenLeftRenamesMethodAndChangedBody_andRightChangesBodyAtSameArea_shouldReportConflictOnBody() {
        left = renamedMethodAndChangedBodyFile;
        right = bodyChangedAtEndFile;

        merge();

        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "publicclassTest{publicvoidm1(){inta;<<<<<<<MINEa;=======a=123;>>>>>>>YOURS}}");
    }

    @Test
    public void testHandle_whenRightRenamesMethodAndChangedBody_andLeftChangesBodyAtSameArea_shouldReportConflictOnBody() {
        left = bodyChangedAtEndFile;
        right = renamedMethodAndChangedBodyFile;

        merge();

        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext, "publicclassTest{publicvoidm1(){inta;<<<<<<<MINEa=123;=======a;>>>>>>>YOURS}}");
    }

    private void merge() {
        mergeContext = jfstMerge.mergeFiles(
                left,
                baseFile,
                right,
                null);
    }
}
