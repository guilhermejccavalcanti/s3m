package br.ufpe.cin.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.util.MergeContext;

public class DuplicatedDeclarationErrorsHandlerTest {
	
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
	public void testDuplicationErrorNoConflict() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/duplicationsnoconflict/left/Test.java"), 
				new File("testfiles/duplicationsnoconflict/base/Test.java"), 
				new File("testfiles/duplicationsnoconflict/right/Test.java"),
				null);
		assertTrue(ctx.duplicatedDeclarationErrors==1);
	}
	
	@Test
	public void testConflictingDuplicationError() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/duplicationsconflicting/left/Test.java"), 
				new File("testfiles/duplicationsconflicting/base/Test.java"), 
				new File("testfiles/duplicationsconflicting/right/Test.java"),
				null);
		assertTrue(ctx.duplicatedDeclarationErrors==0);
	}
}
