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
import br.ufpe.cin.mergers.util.MergeContext;

public class DuplicatedDeclarationErrorsHandlerTest {

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
	public void testDuplicationErrorNoConflict() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/duplicationsnoconflict/left/Test.java"),
				new File("testfiles/duplicationsnoconflict/base/Test.java"),
				new File("testfiles/duplicationsnoconflict/right/Test.java"),
				null);
		assertThat(ctx.duplicatedDeclarationErrors).isZero();
	}

	@Test
	public void testConflictingDuplicationError() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/duplicationsconflicting/left/Test.java"),
				new File("testfiles/duplicationsconflicting/base/Test.java"),
				new File("testfiles/duplicationsconflicting/right/Test.java"),
				null);
		assertThat(ctx.duplicatedDeclarationErrors).isOne();
	}

	@Test
	public void testDuplicationDeclarationParameter() {
		boolean defaultValue = JFSTMerge.isDuplicatedDeclarationHandlerEnabled;

		JFSTMerge.isDuplicatedDeclarationHandlerEnabled = false;
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/duplicationsconflicting/left/Test.java"),
				new File("testfiles/duplicationsconflicting/base/Test.java"),
				new File("testfiles/duplicationsconflicting/right/Test.java"),
				null);
		assertThat(ctx.duplicatedDeclarationErrors).isZero();
		JFSTMerge.isDuplicatedDeclarationHandlerEnabled = defaultValue;
	}
}
