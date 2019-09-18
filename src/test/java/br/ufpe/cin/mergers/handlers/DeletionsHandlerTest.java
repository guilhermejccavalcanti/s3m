package br.ufpe.cin.mergers.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class DeletionsHandlerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws UnsupportedEncodingException {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
			public void write(int b) {}
		}, true, Charset.defaultCharset().displayName());
		System.setOut(hideStream);
	}
	
	@Test
	public void testFileDeletion() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninner/left.java"), 
				new File("testfiles/deletioninner/base.java"), 
				null,
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("<<<<<<<MINEpackagecom.example;publicclassTest{voidm(){}}=======>>>>>>>YOURS")
				);
	}
	
	@Test
	public void testInnerDeletion() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninner/left.java"), 
				new File("testfiles/deletioninner/base.java"), 
				new File("testfiles/deletioninner/right.java"),
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{voidm(){}intb;}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);

	}
	
	@Test
	public void testInnerDeletionReversed() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninner/left.java"), 
				new File("testfiles/deletioninner/base.java"), 
				new File("testfiles/deletioninner/right.java"),
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{voidm(){}intb;}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);
	}
	
	@Test
	public void testInnerDeletionWithNewInstanceOfOriginal() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernewinstanceoforiginal/left.java"), 
				new File("testfiles/deletioninnernewinstanceoforiginal/base.java"), 
				new File("testfiles/deletioninnernewinstanceoforiginal/right.java"),
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{classA{doublea;doubleb;}classB{doublea;}publicstaticvoidmain(String[]args){newA();}}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);
	}
	
	@Test
	public void testInnerDeletionWithNoNewInstanceOfOriginal() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernoinstanceoforiginal/left.java"), 
				new File("testfiles/deletioninnernoinstanceoforiginal/base.java"), 
				new File("testfiles/deletioninnernoinstanceoforiginal/right.java"),
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{classB{doublea;doubleb;}}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);
	}
	
	@Test
	public void testInnerDeletionWithNewInstanceOfRenamed() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernewinstanceofrenamed/left.java"), 
				new File("testfiles/deletioninnernewinstanceofrenamed/base.java"), 
				new File("testfiles/deletioninnernewinstanceofrenamed/right.java"),
				null);
		assertEquals("packagecom.example;publicclassTest{<<<<<<<MINE=======classA{doublea;doubleb;}>>>>>>>YOURSclassB{doublea;}publicstaticvoidmain(String[]args){newB();}}", FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput));
		assertEquals(1, ctx.innerDeletionConflicts);

	}

	@Test
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
	public void testInnerDeletionWithNewInstanceOfRenamed_givenShowMessageOptionIsEnabled_whenLeftRenamesAClass_andAddsAReferenceToTheEditedClassByRight_shouldReportConflict() {
		JFSTMerge.showConflictMessages = true;
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernewinstanceofrenamed/left.java"), 
				new File("testfiles/deletioninnernewinstanceofrenamed/base.java"), 
				new File("testfiles/deletioninnernewinstanceofrenamed/right.java"),
				null);
		JFSTMerge.showConflictMessages = false;
		assertEquals("packagecom.example;publicclassTest{<<<<<<<MINE=======possibledeletionofclassAclassA{doublea;doubleb;}>>>>>>>YOURSclassB{doublea;}publicstaticvoidmain(String[]args){newB();}}", FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput));
		assertEquals(1, ctx.innerDeletionConflicts);

	}
	
	@Test
	public void testInnerDeletionWithNoEditionOfOriginal() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernoeditionoforiginal/left.java"), 
				new File("testfiles/deletioninnernoeditionoforiginal/base.java"), 
				new File("testfiles/deletioninnernoeditionoforiginal/right.java"),
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{classB{doublea;}inta;}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);

	}
	
	@Test
	public void testInnerDeletionNotRefactoring() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernotrefactoring/left.java"), 
				new File("testfiles/deletioninnernotrefactoring/base.java"), 
				new File("testfiles/deletioninnernotrefactoring/right.java"),
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{classB{doublea;doubleb;}inta;}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);
	}

	@Test
	public void testInnerDeletionWithNewInstanceOfOriginalReversed() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernewinstanceoforiginal/right.java"),
				new File("testfiles/deletioninnernewinstanceoforiginal/base.java"), 
				new File("testfiles/deletioninnernewinstanceoforiginal/left.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{classA{doublea;doubleb;}publicstaticvoidmain(String[]args){newA();}classB{doublea;}}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);
	}
	
	@Test
	public void testInnerDeletionWithNoNewInstanceOfOriginalReversed() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernoinstanceoforiginal/right.java"),
				new File("testfiles/deletioninnernoinstanceoforiginal/base.java"), 
				new File("testfiles/deletioninnernoinstanceoforiginal/left.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{classB{doublea;doubleb;}}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);
	}
	
	@Test
	public void testInnerDeletionWithNewInstanceOfRenamedReversed() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernewinstanceofrenamed/right.java"),
				new File("testfiles/deletioninnernewinstanceofrenamed/base.java"), 
				new File("testfiles/deletioninnernewinstanceofrenamed/left.java"), 
				null);
		assertEquals("packagecom.example;publicclassTest{<<<<<<<MINEclassA{doublea;doubleb;}=======>>>>>>>YOURSclassB{doublea;}publicstaticvoidmain(String[]args){newB();}}", FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput));
		assertEquals(1, ctx.innerDeletionConflicts);
	}
	
	@Test
	public void testInnerDeletionWithNoEditionOfOriginalReversed() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernoeditionoforiginal/right.java"),
				new File("testfiles/deletioninnernoeditionoforiginal/base.java"), 
				new File("testfiles/deletioninnernoeditionoforiginal/left.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{inta;classB{doublea;}}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);

	}
	
	@Test
	public void testInnerDeletionNotRefactoringReversed() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnernotrefactoring/right.java"),
				new File("testfiles/deletioninnernotrefactoring/base.java"), 
				new File("testfiles/deletioninnernotrefactoring/left.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("packagecom.example;publicclassTest{inta;classB{doublea;doubleb;}}")
				);
		assertTrue(ctx.innerDeletionConflicts==0);
	}
}
