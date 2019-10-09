package br.ufpe.cin.mergers.handlers.renaming;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;
import br.ufpe.cin.util.TestUtils;

public class KeepBothMethodsRenamingHandlerTest {
    private File baseFile = new File("testfiles/renaming/method/base_method/Test.java");
    private File bodyChangedBelowSignatureFile = new File("testfiles/renaming/method/body_changes_only_1/Test.java");
    private File bodyChangedAtEndFile = new File("testfiles/renaming/method/body_changes_only_2/Test.java");
    private File renamedMethodWithoutBodyChangesFile1 = new File(
            "testfiles/renaming/method/renamed_method_without_body_changes_1/Test.java");
    private File renamedMethodWithoutBodyChangesFile2 = new File(
            "testfiles/renaming/method/renamed_method_without_body_changes_2/Test.java");
    private File renamedMethodWithBodyChangesFile1 = new File(
            "testfiles/renaming/method/renamed_method_with_body_changes_1/Test.java");
    private File renamedMethodWithBodyChangesFile2 = new File(
            "testfiles/renaming/method/renamed_method_with_body_changes_2/Test.java");
    private File renamedMethodWithBodyChangesFile3 = new File(
            "testfiles/renaming/method/renamed_method_with_body_changes_3/Test.java");
    private File removedMethodFile = new File("testfiles/renaming/method/deleted_method/Test.java");
    private File abstractMethod1 = new File("testfiles/renaming/abstract_method/left/Test.java");
    private File abstractMethod2 = new File("testfiles/renaming/abstract_method/base/Test.java");
    private File abstractMethod3 = new File("testfiles/renaming/abstract_method/right/Test.java");

    private JFSTMerge jfstMerge = new JFSTMerge();
    private MergeContext mergeContext;

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
        TestUtils.hideSystemOutput();

        JFSTMerge.renamingStrategy = RenamingStrategy.KEEP_BOTH_METHODS;
    }

    @Test
    public void testHandle_whenBothRenameWithoutBodyChanges_andTheyRenameToDifferentNames_shouldNotReportConflict() {
        merge(renamedMethodWithoutBodyChangesFile1, renamedMethodWithoutBodyChangesFile2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                "publicvoidmethod1(intx,inty){inta;intb;intc;}publicvoidmethod2(intx,inty){inta;intb;intc;}");
    }

    @Test
    public void testHandle_whenBothRenameWithBodyChanges_andTheyRenameToDifferentNames_shouldNotReportConflict() {
        merge(renamedMethodWithBodyChangesFile1, renamedMethodWithBodyChangesFile2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                "publicvoidmethod1(intx,inty){intd;intb;intc;}publicvoidmethod2(intx,inty){inta;intb;intd;}");
    }

    @Test
    public void testHandle_whenLeftRenamesMethod_andRightDoesNotRename_shouldNotReportConflict() {
        merge(renamedMethodWithBodyChangesFile1, bodyChangedBelowSignatureFile);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                "publicvoidmethod1(intx,inty){intd;intb;intc;}publicvoidmethod(intx,inty){inte;intb;intc;}");
    }

    @Test
    public void testHandle_whenRightRenamesMethod_andLeftDoesNotRename_shouldNotReportConflict() {
            merge(bodyChangedAtEndFile, renamedMethodWithoutBodyChangesFile2);
            TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                "publicvoidmethod(intx,inty){inta;intb;inte;}publicvoidmethod2(intx,inty){inta;intb;intc;}");
    }

    @Test
    public void testHandle_whenLeftDeletesMethod_andRightDoesNotRename_shouldNotReportConflict() {
        merge(removedMethodFile, bodyChangedAtEndFile);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                "publicvoidmethod(intx,inty){inta;intb;inte;}");
    }

    @Test
    public void testHandle_whenRightDeletesMethod_andLeftDoesNotRename_shouldNotReportConflict() {
        merge(bodyChangedBelowSignatureFile, removedMethodFile);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                "publicvoidmethod(intx,inty){inte;intb;intc;}");
    }

    @Test
    public void testHandle_abstractMethods_whenBothRenameToDifferentName_shouldNotReportConflict() {
            merge(abstractMethod1, abstractMethod2, abstractMethod3);
            TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                            "publicabstractvoidn1();privateinta;publicabstractvoidn2();");
    }

    private void merge(File left, File right) {
        mergeContext = jfstMerge.mergeFiles(left, baseFile, right, null);
    }

    private void merge(File left, File base, File right) {
        mergeContext = jfstMerge.mergeFiles(left, base, right, null);
    }
}