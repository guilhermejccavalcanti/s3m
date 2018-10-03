package br.ufpe.cin.mergers.handlers.singlerenaming;

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

public class KeepBothMethodsSingleRenamingHandlerTest {
    private File baseFile = new File("testfiles/renaming/method/base_method/Test.java");
    private File bodyChangedFileBelowSignature = new File("testfiles/renaming/method/changed_body_below_signature/Test.java");
    private File bodyChangedAtEndFile = new File("testfiles/renaming/method/changed_body_at_end/Test.java");
    private File renamedMethodFile1 = new File("testfiles/renaming/method/renamed_method_1/Test.java");
    private File renamedMethodFile2 = new File("testfiles/renaming/method/renamed_method_2/Test.java");

    private JFSTMerge jfstMerge = new JFSTMerge();
    private File left, right;
    private MergeContext mergeContext;

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

        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenamesMethodDifferently_shouldReportConflict() {
        JFSTMerge.renamingStrategy = RenamingStrategy.SAFE;
        left = renamedMethodFile1;
        right = renamedMethodFile2;

        merge();

        verifyMergeResultWithConflict("<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidn2(){inta;}>>>>>>>YOURS");
    }

    @Test
    public void testMethodRenamingOnLeft_givenKeepBothMethodsIsEnabled_whenLeftRenamesMethod_andRightChangesBodyBelowSignature_shouldReportConflict() {
        left = renamedMethodFile1;
        right = bodyChangedFileBelowSignature;

        merge();

        verifyMergeResultWithoutConflict("publicclassTest{publicvoidn1(){inta;}publicvoidm(){inta=123;}}");
    }

    @Test
    public void testMethodRenamingOnRight_givenKeepBothMethodsIsEnabled_whenRightRenamesMethod_andLeftChangesBodyBelowSignature_shouldNotReportConflict() {
        left = bodyChangedFileBelowSignature;
        right = renamedMethodFile1;

        merge();

        verifyMergeResultWithoutConflict("publicclassTest{publicvoidm(){inta=123;}publicvoidn1(){inta;}}");
    }

    @Test
    public void testMethodRenamingOnLeft_givenKeepBothMethodsIsEnabled_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldNotReportConflict() {
        left = renamedMethodFile1;
        right = bodyChangedAtEndFile;

        merge();

        verifyMergeResultWithoutConflict("publicclassTest{publicvoidn1(){inta;}publicvoidm(){inta;a=123;}}");
    }

    @Test
    public void testMethodRenamingOnRight_givenKeepBothMethodsIsEnabled_whenLeftRenamesMethod_andRightnChangesBodyAtEnd_shouldNotReportConflict() {
        left = bodyChangedAtEndFile;
        right = renamedMethodFile1;

        merge();

        verifyMergeResultWithoutConflict("publicclassTest{publicvoidm(){inta;a=123;}publicvoidn1(){inta;}}");
    }

    @Test
    public void testMutualMethodRenaming_givenKeepBothMethodsIsEnabled_whenBothVersionsRenameMethodDifferently_shouldNotReportConflict() {
        left = renamedMethodFile1;
        right = renamedMethodFile2;

        merge();

        verifyMergeResultWithoutConflict("publicclassTest{publicvoidn1(){inta;}publicvoidn2(){inta;}}");
    }

    private void merge() {
        mergeContext = jfstMerge.mergeFiles(
                left,
                baseFile,
                right,
                null);
    }

    private void verifyMergeResultWithConflict(String expectedResult) {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(mergeContext.semistructuredOutput);
        assertThat(mergeResult).contains(expectedResult);
        assertThat(mergeContext.renamingConflicts).isOne();
    }

    private void verifyMergeResultWithoutConflict(String expectedResult) {
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(mergeContext.semistructuredOutput);
        assertThat(mergeResult).contains(expectedResult);
        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(mergeContext.renamingConflicts).isZero();
    }
}
