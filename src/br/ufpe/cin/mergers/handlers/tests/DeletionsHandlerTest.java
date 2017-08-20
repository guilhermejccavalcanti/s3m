package br.ufpe.cin.mergers.handlers.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class DeletionsHandlerTest {

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
	public void testInnerDeletionInLeft() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnerinleft/left/Test.java"), 
				new File("testfiles/deletioninnerinleft/base/Test.java"), 
				new File("testfiles/deletioninnerinleft/right/Test.java"),
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("publicclassTest{voidm(){intb;}classInnerextendsa{intn(){}}}")
				);
	}

	@Test
	public void testInnerDeletionInRight() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/deletioninnerinright/left/Test.java"), 
				new File("testfiles/deletioninnerinright/base/Test.java"), 
				new File("testfiles/deletioninnerinright/right/Test.java"),
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput)
				.equals("publicclassTest{voidm(){intb;}classInnerextendsa{intn(){}}}")
				);	}
}
