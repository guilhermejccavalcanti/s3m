package br.ufpe.cin.mergers.handlers.singlerenaming;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;
import br.ufpe.cin.util.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class KeepBothMethodsSingleRenamingHandlerTest {
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

        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;
    }

    @Test
    public void testHandle_whenLeftRenamesMethod_andRightChangesBodyBelowSignature_shouldNotReportConflict() {
        left = renamedMethodFile;
        right = bodyChangedFileBelowSignature;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}publicvoidm(){inta=123;}}");
    }

    @Test
    public void testHandle_whenRightRenamesMethod_andLeftChangesBodyBelowSignature_shouldNotReportConflict() {
        left = bodyChangedFileBelowSignature;
        right = renamedMethodFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidm(){inta=123;}publicvoidn1(){inta;}}");
    }

    @Test
    public void testHandle_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldNotReportConflict() {
        left = renamedMethodFile;
        right = bodyChangedAtEndFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidn1(){inta;}publicvoidm(){inta;a=123;}}");
    }

    @Test
    public void testHandle_whenLeftRenamesMethod_andRightnChangesBodyAtEnd_shouldNotReportConflict() {
        left = bodyChangedAtEndFile;
        right = renamedMethodFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidm(){inta;a=123;}publicvoidn1(){inta;}}");
    }

    @Test
    public void testHandle_whenLeftRenamesMethodAndChangedBody_andRightChangesBodyAtEnd_shouldNotReportConflictOnBody() {
        left = renamedMethodAndChangedBodyFile;
        right = bodyChangedAtEndFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidm1(){inta;a;}publicvoidm(){inta;a=123;}}");
    }

    @Test
    public void testHandle_whenRightRenamesMethodAndChangedBody_andLeftChangesBodyAtEnd_shouldNotReportConflictOnBody() {
        left = bodyChangedAtEndFile;
        right = renamedMethodAndChangedBodyFile;

        merge();

        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext, "publicclassTest{publicvoidm(){inta;a=123;}publicvoidm1(){inta;a;}}");
    }

    private void merge() {
        mergeContext = jfstMerge.mergeFiles(
                left,
                baseFile,
                right,
                null);
    }
}
