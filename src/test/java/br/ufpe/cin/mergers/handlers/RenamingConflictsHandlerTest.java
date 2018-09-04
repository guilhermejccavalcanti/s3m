package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RenamingConflictsHandlerTest {
    private File baseFile = new File("testfiles/renaming/method/base_method/Test.java");
    private File bodyChangedFileBelowSignature = new File("testfiles/renaming/method/changed_body_below_signature/Test.java");
    private File bodyChangedAtEndFile = new File("testfiles/renaming/method/changed_body_at_end/Test.java");
    private File renamedMethodFile1 = new File("testfiles/renaming/method/renamed_method_1/Test.java");
    private File renamedMethodFile2 = new File("testfiles/renaming/method/renamed_method_2/Test.java");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
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
        JFSTMerge.keepBothVersionsOfRenamedMethod = false;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedFileBelowSignature,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).contains("<<<<<<<MINEpublicvoidm(){inta=123;}=======publicvoidn1(){inta;}>>>>>>>YOURS");
        assertThat(ctx.renamingConflicts).isOne();
    }

    @Test
    public void testMethodRenamingOnRight_whenRightRenamesMethod_andLeftChangesBodyBelowSignature_shouldReportConflict() {
        JFSTMerge.keepBothVersionsOfRenamedMethod = false;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                bodyChangedFileBelowSignature,
                baseFile,
                renamedMethodFile1,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).contains("<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidm(){inta=123;}>>>>>>>YOURS");
        assertThat(ctx.renamingConflicts).isOne();
    }

    @Test
    public void testMethodRenamingOnLeft_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldNotReportConflict() {
        JFSTMerge.keepBothVersionsOfRenamedMethod = false;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedAtEndFile,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(ctx.renamingConflicts).isZero();
    }

    @Test
    public void testMethodRenamingOnRight_whenLeftRenamesMethod_andRightnChangesBodyAtEnd_shouldNotReportConflict() {
        JFSTMerge.keepBothVersionsOfRenamedMethod = false;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                bodyChangedAtEndFile,
                baseFile,
                renamedMethodFile1,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(ctx.renamingConflicts).isZero();
    }

    @Test
    public void testMutualMethodRenaming_whenBothVersionsRenamesMethodDifferently_shouldReportConflict() {
        JFSTMerge.keepBothVersionsOfRenamedMethod = false;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                renamedMethodFile1,
                baseFile,
                renamedMethodFile2,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).contains("<<<<<<<MINEpublicvoidn1(){inta;}=======publicvoidn2(){inta;}>>>>>>>YOURS");
        assertThat(ctx.renamingConflicts).isOne();
    }

    @Test
    public void testMethodRenamingOnLeft_givenKeepBothMethodsIsEnabled_whenLeftRenamesMethod_andRightChangesBodyBelowSignature_shouldReportConflict() {
        JFSTMerge.keepBothVersionsOfRenamedMethod = true;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedFileBelowSignature,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(ctx.renamingConflicts).isZero();
    }

    @Test
    public void testMethodRenamingOnRight_givenKeepBothMethodsIsEnabled_whenRightRenamesMethod_andLeftChangesBodyBelowSignature_shouldNotReportConflict() {
        JFSTMerge.keepBothVersionsOfRenamedMethod = true;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                bodyChangedFileBelowSignature,
                baseFile,
                renamedMethodFile1,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(ctx.renamingConflicts).isZero();
    }

    @Test
    public void testMethodRenamingOnLeft_givenKeepBothMethodsIsEnabled_whenLeftRenamesMethod_andRightChangesBodyAtEnd_shouldNotReportConflict() {
        JFSTMerge.keepBothVersionsOfRenamedMethod = true;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                renamedMethodFile1,
                baseFile,
                bodyChangedAtEndFile,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(ctx.renamingConflicts).isZero();
    }

    @Test
    public void testMethodRenamingOnRight_givenKeepBothMethodsIsEnabled_whenLeftRenamesMethod_andRightnChangesBodyAtEnd_shouldNotReportConflict() {
        JFSTMerge.keepBothVersionsOfRenamedMethod = true;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                bodyChangedAtEndFile,
                baseFile,
                renamedMethodFile1,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(ctx.renamingConflicts).isZero();
    }

    @Test
    public void testMutualMethodRenaming_givenKeepBothMethodsIsEnabled_whenBothVersionsRenameMethodDifferently_shouldNotReportConflict() {
        JFSTMerge.keepBothVersionsOfRenamedMethod = true;

        MergeContext ctx = new JFSTMerge().mergeFiles(
                renamedMethodFile1,
                baseFile,
                renamedMethodFile2,
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).doesNotContain("(cause:possiblerenaming)");
        assertThat(ctx.renamingConflicts).isZero();
    }
}
