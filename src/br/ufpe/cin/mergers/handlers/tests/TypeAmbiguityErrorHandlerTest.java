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

public class TypeAmbiguityErrorHandlerTest {
	
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
	public void testImportMemberMember() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/importmembermember/left/Test/src/Test.java"), 
				new File("testfiles/importmembermember/base/Test/src/Test.java"), 
				new File("testfiles/importmembermember/right/Test/src/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(mergeResult.contains("<<<<<<<MINEimportjava.awt.List;=======importjava.util.List;>>>>>>>YOURS"));
		assertTrue(ctx.typeAmbiguityErrorsConflicts==1);
	}
	
	@Test
	public void testImportPackagePackage() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/importpackagepackage/left/Test/src/Test.java"), 
				new File("testfiles/importpackagepackage/base/Test/src/Test.java"), 
				new File("testfiles/importpackagepackage/right/Test/src/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(mergeResult.contains("<<<<<<<MINEimportpckt.*;=======importpcktright.*;>>>>>>>YOURS"));
		assertTrue(ctx.typeAmbiguityErrorsConflicts==1);
	}
	
	@Test
	public void testImportPackageMember() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/importpackagemember/left/Test/src/Test.java"), 
				new File("testfiles/importpackagemember/base/Test/src/Test.java"), 
				new File("testfiles/importpackagemember/right/Test/src/Test.java"),
				null);
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertTrue(mergeResult.contains("<<<<<<<MINEimportpckt.A;=======importpcktright.*;>>>>>>>YOURS"));
		assertTrue(ctx.typeAmbiguityErrorsConflicts==1);
	}

}
