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

public class TypeAmbiguityErrorHandlerTest {

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
	public void testImportMemberMember() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/importmembermember/left/Test/src/Test.java"),
				new File("testfiles/importmembermember/base/Test/src/Test.java"),
				new File("testfiles/importmembermember/right/Test/src/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("<<<<<<<MINEimportjava.awt.List;|||||||BASE=======importjava.util.List;>>>>>>>YOURS");
		assertThat(ctx.typeAmbiguityErrorsConflicts).isOne();
	}

	@Test
	public void testImportPackagePackage() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/importpackagepackage/left/Test/src/Test.java"),
				new File("testfiles/importpackagepackage/base/Test/src/Test.java"),
				new File("testfiles/importpackagepackage/right/Test/src/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("<<<<<<<MINEimportpckt.*;|||||||BASE=======importpcktright.*;>>>>>>>YOURS");
		assertThat(ctx.typeAmbiguityErrorsConflicts).isOne();
	}

	@Test
	public void testImportPackageMember() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/importpackagemember/left/Test/src/Test.java"),
				new File("testfiles/importpackagemember/base/Test/src/Test.java"),
				new File("testfiles/importpackagemember/right/Test/src/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("<<<<<<<MINEimportpckt.A;|||||||BASE=======importpcktright.*;>>>>>>>YOURS");
		assertThat(ctx.typeAmbiguityErrorsConflicts).isOne();
	}

	@Test
	public void testTypeAmbiguityErrorParameter() {
		boolean defaultValue = JFSTMerge.isTypeAmbiguityErrorHandlerEnabled;

		JFSTMerge.isTypeAmbiguityErrorHandlerEnabled = false;
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/importmembermember/left/Test/src/Test.java"),
				new File("testfiles/importmembermember/base/Test/src/Test.java"),
				new File("testfiles/importmembermember/right/Test/src/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("importjava.util.List;importjava.awt.List;publicclassTest{Listlist;}");
		assertThat(ctx.typeAmbiguityErrorsConflicts).isZero();
		JFSTMerge.isTypeAmbiguityErrorHandlerEnabled = defaultValue;
	}

}
