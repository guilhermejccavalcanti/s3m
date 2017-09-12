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

public class NewElementReferencingEditedOneHandlerTest {

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
	public void testNereoMethodField() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/nereomethodfield/left/Test.java"), 
				new File("testfiles/nereomethodfield/base/Test.java"), 
				new File("testfiles/nereomethodfield/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertTrue(mergeResult.contains("<<<<<<<MINEinta=15;=======intm(){returna+15;}>>>>>>>YOURS"));
		assertTrue(ctx.newElementReferencingEditedOneConflicts==1);

	}
	
	
	@Test
	public void testNereoFieldMethod() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/nereofieldmethod/left/Test.java"), 
				new File("testfiles/nereofieldmethod/base/Test.java"), 
				new File("testfiles/nereofieldmethod/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertTrue(mergeResult.contains("<<<<<<<MINEstaticintm(){return15;}=======inta=m();>>>>>>>YOURS"));
		assertTrue(ctx.newElementReferencingEditedOneConflicts==1);
	}
	
	@Test
	public void testNereoFieldField() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/nereofieldfield/left/Test.java"), 
				new File("testfiles/nereofieldfield/base/Test.java"), 
				new File("testfiles/nereofieldfield/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertTrue(mergeResult.contains("<<<<<<<MINEinta=15;=======intb=a+20;>>>>>>>YOURS"));
		assertTrue(ctx.newElementReferencingEditedOneConflicts==1);
	}
	
	
	@Test
	public void testNereoMethodMethod() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/nereomethodmethod/left/Test.java"), 
				new File("testfiles/nereomethodmethod/base/Test.java"), 
				new File("testfiles/nereomethodmethod/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertTrue(mergeResult.contains("<<<<<<<MINEstaticStringm(){return\"insidemethodmedited\";}=======voidn(){if(m().equals(\"something...\")){System.out.println(\"insidemethodn\");}}>>>>>>>YOURS"));
		assertTrue(ctx.newElementReferencingEditedOneConflicts==1);
	}
}
