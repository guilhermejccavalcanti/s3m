package br.ufpe.cin.mergers.handlers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class InitializationBlocksHandlerNewApproachTest {

    JFSTMerge merge;

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
	
	@Before
	public void initJFSTMergeWithNewApproachForInitializationHandler() {
		merge = new JFSTMerge();
		merge.useInitializationBlocksHandlerNewApproach();
	}
	
	@Test
	public void testInitializationBlocksBlockAddition() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/nobaseandblockaddition/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/nobaseandblockaddition/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/nobaseandblockaddition/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("publicclassTest{static{inta=3;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksNoEditionAndBlockDeletion() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/noeditionandblockdeletion/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/noeditionandblockdeletion/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/noeditionandblockdeletion/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("publicclassTest{}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksEditionAndBlockDeletion() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/editionanddeletion/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionanddeletion/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionanddeletion/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("publicclassTest{}");
		assertThat(ctx.initializationBlocksConflicts).isOne();
	}
	
	@Test
	public void testInitializationBlocksDoubledBlockAddition() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/doubledblockaddition/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/doubledblockaddition/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/doubledblockaddition/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("publicclassTest{static{inta=3;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksAdditionOfSimilarBlocks() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/additionofsimilarblocks/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/additionofsimilarblocks/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/additionofsimilarblocks/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

//		static {
//			<<<<<<< MINE
//					int a = 3;
//			=======
//					int a = 5;
//			>>>>>>> YOURS
//				}
//		
//		FIXME merge result
		
		assertThat(mergeResult).contains("publicclassTest{static{inta=3;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksAdditionOfSimilarBlocksDiffVars() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/additionofsimilarblocksdiffvars/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/additionofsimilarblocksdiffvars/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/additionofsimilarblocksdiffvars/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("publicclassTest{static{inta=3;}static{intb=5;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksEditionInOnlyOneBlock() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/editioninonlyoneblock/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editioninonlyoneblock/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editioninonlyoneblock/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("publicclassTest{static{inta=4;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksEditionInBothBranches() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/editioninbothbranches/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editioninbothbranches/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editioninbothbranches/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

//		static {
//			<<<<<<< MINE
//					int a = 4;
//			=======
//					int a = 5;
//			>>>>>>> YOURS
//				}
//
//		FIXME merge result
		
		assertThat(mergeResult).contains("publicclassTest{static{inta=4;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksEditionAndBlockAddition() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/editionandblockaddition/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionandblockaddition/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionandblockaddition/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

//		static {
//			<<<<<<< MINE
//					int a = 4;
//			=======
//					int b = 5;
//					int c = 5;
//					int d = 5;
//					int e = 5;
//			>>>>>>> YOURS
//				}
//
//		FIXME merge result
		
		assertThat(mergeResult).contains("publicclassTest{static{inta=4;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksEditionAndContentInsertion() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/editionandcontentinsertion/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionandcontentinsertion/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionandcontentinsertion/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

//		static {
//			int a = 4;
//			int c = 5;
//			int d = 5;
//			int e = 5;
//		}
//
//		FIXME merge result
		
		assertThat(mergeResult).contains("publicclassTest{static{inta=4;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksEditionOrAdditionInBothBranchesIndependentVars() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/editionoradditioninbothbranchesindependentvars/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionoradditioninbothbranchesindependentvars/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionoradditioninbothbranchesindependentvars/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);
		
		assertThat(mergeResult).contains("publicclassTest{static{inth=4;intf=6;intg=6;}static{intb=5;intc=5;intd=5;inte=5;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksEditionOrAdditionInBothBranchesDependentVars() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/editionoradditioninbothbranchesindependentvars/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionoradditioninbothbranchesindependentvars/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/editionoradditioninbothbranchesindependentvars/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

//		static int h = 4;
//		
//		static {
//	<<<<<<< MINE
//			h = 5;
//			int f = 6;
//			int g = 6;
//	=======
//			int b = 5;
//			int c = 5;
//			int g = 5;
//			int e = h;
//	>>>>>>> YOURS
//		}


//		FIXME merge result
		
		assertThat(mergeResult).contains("publicclassTest{static{inta=4;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksMultipleBlocksEditionInOnlyOneBranch() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/multipleblockseditioninonlyonebranch/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblockseditioninonlyonebranch/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblockseditioninonlyonebranch/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("publicclassTest{static{inta=3;}static{inta=7;intb=8;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksMultipleBlocksEditionInBothBranchesBlocks() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/multipleblockseditioninbothbranchesblocks/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblockseditioninbothbranchesblocks/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblockseditioninbothbranchesblocks/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		
//		static {
//			int var_a = 1;
//			int var_b = 2;
//			int var_c = 3;
//
//			for (int i = 0; i < 5; i++) {
//	                              System.out.println("Índice " + i);
//	                         }  
//		}
//		static {
//			int a = 7;
//			int b = 8;
//			int c = 9;
//		}

//		FIXME merge result
		
		assertThat(mergeResult).contains("publicclassTest{static{inta=4;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksMultipleBlocksReordering() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/multipleblocksreordering/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblocksreordering/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblocksreordering/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		
//		static {
//			int d = 4;
//			int e = 5;
//			int f = 6;
//		}
//
//		static {
//			int a = 1;
//			int b = 2;
//			int c = 3;
//			int d = 4;
//		}


//		FIXME merge result
		
		assertThat(mergeResult).contains("publicclassTest{static{inta=4;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksMultipleBlocksEditionAndDeletion() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/multipleblockseditionanddeletion/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblockseditionanddeletion/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblockseditionanddeletion/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("publicclassTest{static{inta=4;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
	
	@Test
	public void testInitializationBlocksMultipleBlocksEditionInBothBranchesAndDeletion() {
		MergeContext ctx = 	merge.mergeFiles(
				new File("testfiles/initlblocksnewapproach/multipleblockseditiondinbothbranchesanddeletion/left/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblockseditiondinbothbranchesanddeletion/base/Test.java"), 
				new File("testfiles/initlblocksnewapproach/multipleblockseditiondinbothbranchesanddeletion/right/Test.java"), 
				null);
		
		String mergeResult = FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.semistructuredOutput);

		assertThat(mergeResult).contains("publicclassTest{static{intvar_a=4;}}");
		assertThat(ctx.initializationBlocksConflicts).isZero();
	}
}
