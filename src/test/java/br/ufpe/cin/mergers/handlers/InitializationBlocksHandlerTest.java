package br.ufpe.cin.mergers.handlers;

import static org.assertj.core.api.Assertions.*;

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

public class InitializationBlocksHandlerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws UnsupportedEncodingException {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
			public void write(int b) {}
		},  true, Charset.defaultCharset().displayName());
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

		assertThat(mergeResult).contains("<<<<<<<MINE_name=\"Left\";=======_name=\"Right\";>>>>>>>YOURS");
		assertThat(ctx.initializationBlocksConflicts).isOne();
	}

	@Test
	public void testInitializationBlocksNoBase() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/initlblocksnobase/left/Test.java"), 
				new File("testfiles/initlblocksnobase/base/Test.java"), 
				new File("testfiles/initlblocksnobase/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("<<<<<<<MINE_name=\"Left\";=======_name=\"Right\";>>>>>>>YOURS");
		assertThat(ctx.initializationBlocksConflicts).isOne();
	}

	@Test
	public void testInitializationBlocksDistincts() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/initlblocksdistincts/left/Test.java"), 
				new File("testfiles/initlblocksdistincts/base/Test.java"), 
				new File("testfiles/initlblocksdistincts/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("<<<<<<<MINESystem.out.println(\"Left\");=======try{Class.forName(\"Right\");}catch(ClassNotFoundExceptione){e.printStackTrace();}>>>>>>>YOURS");
		assertThat(ctx.initializationBlocksConflicts).isOne();
	}

	@Test
	public void testInitializationBlocksParameter() {
        boolean defaultValue = JFSTMerge.isInitializationBlocksHandlerEnabled;

        JFSTMerge.isInitializationBlocksHandlerEnabled = false;
        MergeContext ctx = 	new JFSTMerge().mergeFiles(
                new File("testfiles/initlblocksdistincts/left/Test.java"),
                new File("testfiles/initlblocksdistincts/base/Test.java"),
                new File("testfiles/initlblocksdistincts/right/Test.java"),
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).isEqualTo("publicclassTest{staticString_name;static{System.out.println(\"Left\");}static{try{Class.forName(\"Right\");}catch(ClassNotFoundExceptione){e.printStackTrace();}}}");
        assertThat(ctx.initializationBlocksConflicts).isZero();
        JFSTMerge.isInitializationBlocksHandlerEnabled = defaultValue;
	}

}
