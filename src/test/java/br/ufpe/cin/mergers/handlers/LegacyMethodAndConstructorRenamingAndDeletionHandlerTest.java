package br.ufpe.cin.mergers.handlers;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class LegacyMethodAndConstructorRenamingAndDeletionHandlerTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
		    public void write(int b) {}
		}, true, Charset.defaultCharset().displayName());
		System.setOut(hideStream);
	}


	@BeforeClass
	public static void enableParameter() {
		JFSTMerge.isLegacyMethodAndConstructorRenamingAndDeletionHandlerEnabled = true;
	}

	@AfterClass
	public static void disableParameter() {
		JFSTMerge.isLegacyMethodAndConstructorRenamingAndDeletionHandlerEnabled = false;
	}
	
	@Test
	public void testConflictingRenamingInLeft() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/renamingmethodleftconf/left.java"), 
				new File("testfiles/renamingmethodleftconf/base.java"), 
				new File("testfiles/renamingmethodleftconf/right.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(mergeResult.contains("<<<<<<<MINEpublicvoidm(){inta;}=======publicvoidn(){}>>>>>>>YOURS"));
		assertTrue(ctx.renamingConflicts == 1);
	}

	@Test
	public void testConflictingRenamingInRight() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/renamingmethodrightconf/left.java"), 
				new File("testfiles/renamingmethodrightconf/base.java"), 
				new File("testfiles/renamingmethodrightconf/right.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(mergeResult.contains("<<<<<<<MINEpublicvoidm(){inta;}=======publicvoidn(){}>>>>>>>YOURS"));
		assertTrue(ctx.renamingConflicts == 1);
	}
	
	@Test
	public void testNoConflictingRenamingInLeft() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/renamingmethodleftnoconf/left.java"), 
				new File("testfiles/renamingmethodleftnoconf/base.java"), 
				new File("testfiles/renamingmethodleftnoconf/right.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(!mergeResult.contains("(cause:possiblerenaming)"));
		assertTrue(ctx.renamingConflicts == 0);
	}

	@Test
	public void testNoConflictingRenamingInRight() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/renamingmethodrightnoconf/left.java"), 
				new File("testfiles/renamingmethodrightnoconf/base.java"), 
				new File("testfiles/renamingmethodrightnoconf/right.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(!mergeResult.contains("(cause:possiblerenaming)"));
		assertTrue(ctx.renamingConflicts == 0);
	}
	
	@Test
	public void testConflictingRenamingMutual() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/renamingmutual/left.java"), 
				new File("testfiles/renamingmutual/base.java"), 
				new File("testfiles/renamingmutual/right.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(mergeResult.contains("<<<<<<<MINEintsum(inta,intb){returna+b;}=======intmySum(inta,intb){returna+b;}>>>>>>>YOURS"));
		assertTrue(ctx.renamingConflicts == 1);
	}
	

}