package br.ufpe.cin.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class InitializationBlocksHandlerTest {

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
	public void testInitializationBlocksInThreeVersions() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/initlblocksthreeversions/left/Test.java"), 
				new File("testfiles/initlblocksthreeversions/base/Test.java"), 
				new File("testfiles/initlblocksthreeversions/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertTrue(mergeResult.contains("<<<<<<<MINE_name=\"Left\";=======_name=\"Right\";>>>>>>>YOURS"));
		assertTrue(ctx.initializationBlocksConflicts == 1);
	}

	@Test
	public void testInitializationBlocksNoBase() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/initlblocksnobase/left/Test.java"), 
				new File("testfiles/initlblocksnobase/base/Test.java"), 
				new File("testfiles/initlblocksnobase/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertTrue(mergeResult.contains("<<<<<<<MINE_name=\"Left\";=======_name=\"Right\";>>>>>>>YOURS"));
		assertTrue(ctx.initializationBlocksConflicts == 1);
	}

	@Test
	public void testInitializationBlocksDistincts() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/initlblocksdistincts/left/Test.java"), 
				new File("testfiles/initlblocksdistincts/base/Test.java"), 
				new File("testfiles/initlblocksdistincts/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertTrue(mergeResult.contains("<<<<<<<MINESystem.out.println(\"Left\");=======try{Class.forName(\"Right\");}catch(ClassNotFoundExceptione){e.printStackTrace();}>>>>>>>YOURS"));
		assertTrue(ctx.initializationBlocksConflicts == 1);
	}

}
