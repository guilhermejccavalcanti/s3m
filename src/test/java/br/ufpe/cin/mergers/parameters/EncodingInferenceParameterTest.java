package br.ufpe.cin.mergers.parameters;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class EncodingInferenceParameterTest {
	
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
	public void testEncodingInferenceDisabled() {
		String mergeResult = getMergeResult(false);
		assertThat(mergeResult).isEmpty();
	}

	@Test
	public void testEncodingInferenceEnabled() {
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(getMergeResult(true));
		assertThat(mergeResult.substring(1)).isEqualTo("publicclassTest{voidhelloWorld(){System.out.println(\"HelloWorld!\");}}");
	}
	
	private String getMergeResult(boolean activation) {
		JFSTMerge.isEncodingInferenceEnabled = activation;
		MergeContext context = new JFSTMerge().mergeFiles(
				new File("testfiles/differentencodings/left.java"),
				new File("testfiles/differentencodings/base.java"),
				new File("testfiles/differentencodings/right.java"),
				null);
		return context.semistructuredOutput;
	}

}



