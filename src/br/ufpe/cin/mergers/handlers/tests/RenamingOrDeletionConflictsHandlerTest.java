package br.ufpe.cin.mergers.handlers.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class RenamingOrDeletionConflictsHandlerTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
		    public void write(int b) {}
		});
		System.setOut(hideStream);
	}
	
	@Test
	public void testRenamingInLeft() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/renaminginleft/left/Test.java"), 
				new File("testfiles/renaminginleft/base/Test.java"), 
				new File("testfiles/renaminginleft/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(mergeResult.contains("<<<<<<<MINEintsum(inta,intb){returna+b;}=======(cause:possiblerenaming)intdoMath(inta,intb){returna*b;}>>>>>>>YOURS"));
		assertTrue(ctx.renamingConflicts == 1);
	}

	@Test
	public void testRenamingInRight() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/renaminginright/left/Test.java"), 
				new File("testfiles/renaminginright/base/Test.java"), 
				new File("testfiles/renaminginright/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(mergeResult.contains("<<<<<<<MINEintdoMath(inta,intb){returna*b;}=======(cause:possiblerenaming)intsum(inta,intb){returna+b;}>>>>>>>YOURS"));
		assertTrue(ctx.renamingConflicts == 1);
	}
	
	@Test
	public void testDeletionInLeft() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninleft/left/Test.java"), 
				new File("testfiles/deletioninleft/base/Test.java"), 
				new File("testfiles/deletioninleft/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(!mergeResult.contains("<<<<<<<MINE"));
		assertTrue(ctx.deletionConflicts == 1);
	}

	@Test
	public void testDeletionInRight() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninright/left/Test.java"), 
				new File("testfiles/deletioninright/base/Test.java"), 
				new File("testfiles/deletioninright/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(!mergeResult.contains(">>>>>>>YOURS"));
		assertTrue(ctx.deletionConflicts == 1);
	}

}
