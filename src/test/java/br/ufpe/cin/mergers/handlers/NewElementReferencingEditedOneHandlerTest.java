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

public class NewElementReferencingEditedOneHandlerTest {

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
	public void testNereoMethodField() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/nereomethodfield/left/Test.java"), 
				new File("testfiles/nereomethodfield/base/Test.java"), 
				new File("testfiles/nereomethodfield/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("<<<<<<<MINEinta=15;=======intm(){returna+15;}>>>>>>>YOURS");
		assertThat(ctx.newElementReferencingEditedOneConflicts).isOne();
	}
	
	
	@Test
	public void testNereoFieldMethod() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/nereofieldmethod/left/Test.java"), 
				new File("testfiles/nereofieldmethod/base/Test.java"), 
				new File("testfiles/nereofieldmethod/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("<<<<<<<MINEstaticintm(){return15;}=======inta=m();>>>>>>>YOURS");
		assertThat(ctx.newElementReferencingEditedOneConflicts).isOne();
	}
	
	@Test
	public void testNereoFieldField() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/nereofieldfield/left/Test.java"), 
				new File("testfiles/nereofieldfield/base/Test.java"), 
				new File("testfiles/nereofieldfield/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("<<<<<<<MINEinta=15;=======intb=a+20;>>>>>>>YOURS");
		assertThat(ctx.newElementReferencingEditedOneConflicts).isOne();
	}
	
	
	@Test
	public void testNereoMethodMethod() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/nereomethodmethod/left/Test.java"), 
				new File("testfiles/nereomethodmethod/base/Test.java"), 
				new File("testfiles/nereomethodmethod/right/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("<<<<<<<MINEstaticStringm(){return\"insidemethodmedited\";}=======voidn(){if(m().equals(\"something...\")){System.out.println(\"insidemethodn\");}}>>>>>>>YOURS");
		assertThat(ctx.newElementReferencingEditedOneConflicts).isOne();
	}

	@Test
    public void testNewElementReferencingEditedOneParameter() {
        boolean defaultValue = JFSTMerge.isNewElementReferencingEditedOneHandlerEnabled;

        JFSTMerge.isNewElementReferencingEditedOneHandlerEnabled = false;
        MergeContext ctx = 	new JFSTMerge().mergeFiles(
                new File("testfiles/nereomethodfield/left/Test.java"),
                new File("testfiles/nereomethodfield/base/Test.java"),
                new File("testfiles/nereomethodfield/right/Test.java"),
                null);
        String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

        assertThat(mergeResult).isEqualTo("publicclassTest{inta=15;intm(){returna+15;}}");
        assertThat(ctx.newElementReferencingEditedOneConflicts).isZero();
        JFSTMerge.isNewElementReferencingEditedOneHandlerEnabled = defaultValue;
    }
}
