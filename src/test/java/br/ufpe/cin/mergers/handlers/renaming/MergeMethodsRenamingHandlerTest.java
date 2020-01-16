package br.ufpe.cin.mergers.handlers.renaming;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;
import br.ufpe.cin.util.TestUtils;

public class MergeMethodsRenamingHandlerTest { 
    private File baseFile = new File("testfiles/renaming/method/base_method/Test.java");
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
    private File abstractMethod1 = new File("testfiles/renaming/abstract_method/left/Test.java");
    private File abstractMethod2 = new File("testfiles/renaming/abstract_method/base/Test.java");
    private File abstractMethod3 = new File("testfiles/renaming/abstract_method/right/Test.java");

    private JFSTMerge jfstMerge = new JFSTMerge();
    private MergeContext mergeContext;

    @BeforeClass
    public static void setUpBeforeClass() throws UnsupportedEncodingException {
        TestUtils.hideSystemOutput();

        JFSTMerge.renamingStrategy = RenamingStrategy.MERGE_SIMILAR;
    }

    @Test
    public void testHandle_whenBothRenameWithoutBodyChanges_andTheyRenameToTheSameName_shouldNotReportConflict() {
        merge(renamedMethodWithoutBodyChangesFile1, renamedMethodWithoutBodyChangesFile1);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                "publicvoidmethod1(intx,inty){inta;intb;intc;}");
    } 

    @Test
    public void testHandle_whenBothRenameWithoutBodyChanges_andTheyRenameToDifferentNames_shouldReportConflict() {
        merge(renamedMethodWithoutBodyChangesFile1, renamedMethodWithoutBodyChangesFile2);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext,
                "<<<<<<<MINEpublicvoidmethod1(intx,inty){=======publicvoidmethod2(intx,inty){>>>>>>>YOURSinta;intb;intc;}");
    }

    @Test
    public void testHandle_whenBothRenameWithBodyChanges_andTheyRenameToTheSameName_shouldMergeChanges() {
        merge(renamedMethodWithBodyChangesFile1, renamedMethodWithBodyChangesFile3);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext,
                "publicvoidmethod1(intx,inty){<<<<<<<MINEintd;=======inta;>>>>>>>YOURSintb;intd;");
    }

    @Test
    public void testHandle_whenBothRenameWithBodyChanges_andTheyRenameToDifferentNames_shouldMergeChanges() {
        merge(renamedMethodWithBodyChangesFile2, renamedMethodWithBodyChangesFile3);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext,
                "<<<<<<<MINEpublicvoidmethod2(intx,inty){=======publicvoidmethod1(intx,inty){>>>>>>>YOURSinta;intb;intd;}");
    }

    @Test
    public void testHandle_whenLeftRenamesWithoutBodyChanges_andRightRenamesWithBodyChanges_andBothRenameToTheSameName_shouldMergeChanges() {
        merge(renamedMethodWithoutBodyChangesFile2, renamedMethodWithBodyChangesFile2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                "publicvoidmethod2(intx,inty){inta;intb;intd;}");
    }

    @Test
    public void testHandle_whenRightRenamesWithoutBodyChanges_andLeftRenamesWithBodyChanges_andBothRenameToTheSameName_shouldMergeChanges() {
        merge(renamedMethodWithBodyChangesFile2, renamedMethodWithoutBodyChangesFile2);
        TestUtils.verifyMergeResultWithoutRenamingConflict(mergeContext,
                "publicvoidmethod2(intx,inty){inta;intb;intd;}");
    }

    @Test
    public void testHandle_whenLeftRenamesWithoutBodyChanges_andRightRenamesWithBodyChanges_andBothRenameToDifferentNames_shouldReportConflict() {
        merge(renamedMethodWithoutBodyChangesFile1, renamedMethodWithBodyChangesFile2);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext,
                "<<<<<<<MINEpublicvoidmethod1(intx,inty){=======publicvoidmethod2(intx,inty){>>>>>>>YOURSinta;intb;intd;}");
    }

    @Test
    public void testHandle_whenRightRenamesWithoutBodyChanges_andLeftRenamesWithBodyChanges_andBothRenameToDifferentNames_shouldReportConflict() {
        merge(renamedMethodWithBodyChangesFile2, renamedMethodWithoutBodyChangesFile1);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext,
                "<<<<<<<MINEpublicvoidmethod2(intx,inty){=======publicvoidmethod1(intx,inty){>>>>>>>YOURSinta;intb;intd;}");
    }
    
    @Test
    public void testHandle_abstractMethods_whenBothRenameToDifferentName_shouldReportConflict() {
        merge(abstractMethod1, abstractMethod2, abstractMethod3);
        TestUtils.verifyMergeResultWithRenamingConflict(mergeContext,
                "<<<<<<<MINEpublicabstractvoidn1();=======publicabstractvoidn2();>>>>>>>YOURS");
    }

    private void merge(File left, File right) {
        mergeContext = jfstMerge.mergeFiles(left, baseFile, right, null);
    }

    private void merge(File left, File base, File right) {
        mergeContext = jfstMerge.mergeFiles(left, base, right, null);
    }

}