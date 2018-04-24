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

public class GeneralStructuredMergeTests {

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
	public void testMutualMethodDeletion() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/mutuallmethoddeletion/left.java"), 
				new File("testfiles/mutuallmethoddeletion/base.java"), 
				new File("testfiles/mutuallmethoddeletion/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("importrx.util.functions.Action1;publicclassA{inta;}")
				);
	}

	@Test
	public void testMutualMethodEdition() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/mutuallymethodedited/left.java"), 
				new File("testfiles/mutuallymethodedited/base.java"), 
				new File("testfiles/mutuallymethodedited/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{intm(){<<<<<<<MINEif(true){return;}=======while(true){return;}>>>>>>>YOURSreturn30+10;}}")
				);
	}

	@Test
	public void testRightMethodDeletion() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/rightmethoddeletion/left.java"), 
				new File("testfiles/rightmethoddeletion/base.java"), 
				new File("testfiles/rightmethoddeletion/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("importrx.util.functions.Action1;publicclassA{<<<<<<<MINEvoidm(){intvar;}=======>>>>>>>YOURS}")
				);
	}

	@Test
	public void testSameImportAddition() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/sameimportadded/left.java"), 
				new File("testfiles/sameimportadded/base.java"), 
				new File("testfiles/sameimportadded/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("importjava.util.*;publicclassA{inta;}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testSameMethodAddition() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/samemethodadded/left.java"), 
				new File("testfiles/samemethodadded/base.java"), 
				new File("testfiles/samemethodadded/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{voidm(){}inta;}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testSameStatementAddition() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/samestatementadded/left.java"), 
				new File("testfiles/samestatementadded/base.java"), 
				new File("testfiles/samestatementadded/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{voidm(){while(true){}inta;}}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testSameMethodButDifferentAddition() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/samemethodaddeddifferent/left.java"), 
				new File("testfiles/samemethodaddeddifferent/base.java"), 
				new File("testfiles/samemethodaddeddifferent/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{<<<<<<<MINEintn(){return20;}=======intn(){return10;}>>>>>>>YOURS}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testEditionsToDifferentsPartsOfSameStatement() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/editionstodifferentpartsofstatement/left.java"), 
				new File("testfiles/editionstodifferentpartsofstatement/base.java"), 
				new File("testfiles/editionstodifferentpartsofstatement/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{intm(){for(inti=11;i<11;i++){}}}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testEditionsToSamePartOfSameStatement() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/samepositioneditionfor/left.java"), 
				new File("testfiles/samepositioneditionfor/base.java"), 
				new File("testfiles/samepositioneditionfor/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassTest{voidm(){for(inti=<<<<<<<MINE1=======2>>>>>>>YOURS;i<10;i++){System.out.println(i);}}}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testEditionsToSamePositionFieldDeclarator() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/editionstosamepositioninfielddeclarator/left.java"), 
				new File("testfiles/editionstosamepositioninfielddeclarator/base.java"), 
				new File("testfiles/editionstosamepositioninfielddeclarator/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{intattr=<<<<<<<MINE10=======20>>>>>>>YOURS;}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testEditionsToDifferentPositionFieldDeclarator() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/editionstodifferentpositioninfielddeclarator/left.java"), 
				new File("testfiles/editionstodifferentpositioninfielddeclarator/base.java"), 
				new File("testfiles/editionstodifferentpositioninfielddeclarator/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{privateintattr=30+10;}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testMultipleEditionsToDifferentPositions() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/multipleeditionstodifferentpositions/left.java"), 
				new File("testfiles/multipleeditionstodifferentpositions/base.java"), 
				new File("testfiles/multipleeditionstodifferentpositions/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassTest{privateintx=2;voidm(){inty=2;}}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testMultipleStatementsEditions() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/multiplestatementseditions/left.java"), 
				new File("testfiles/multiplestatementseditions/base.java"), 
				new File("testfiles/multiplestatementseditions/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassTest{publicvoidinitRowCache(){intcachedRowsRead=CacheService.instance.rowCache.loadSaved(this);<<<<<<<MINEfor(DecoratedKeykey:rowCache.readSaved(table.name,columnFamily,partitioner)){ColumnFamilydata=getTopLevelColumns(QueryFilter.getIdentityFilter(key,newQueryPath(columnFamily)),Integer.MIN_VALUE,true);if(data!=null)CacheService.instance.rowCache.put(newRowCacheKey(metadata.cfId,key),data);cachedRowsRead++;}=======if(cachedRowsRead>0)logger.info(String.format(\"completedloading(%dms;%dkeys)rowcachefor%s.%s\",System.currentTimeMillis()-start,cachedRowsRead,table.name,columnFamily));>>>>>>>YOURS}}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testMultipleStatementsEditions2() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/multiplestatementseditions2/left.java"), 
				new File("testfiles/multiplestatementseditions2/base.java"), 
				new File("testfiles/multiplestatementseditions2/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassTest{privatestaticintgetInitialValue(){Stringnewvalue=System.getProperty(\"cassandra.fd_initial_value_ms\");if(newvalue==null){returnGossiper.intervalInMillis*30;}else<<<<<<<MINEreturnGossiper.intervalInMillis*2;======={logger.info(\"OverridingFDINITIAL_VALUEto{}ms\",newvalue);returnInteger.parseInt(newvalue);}>>>>>>>YOURS}}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testEditionToAndDeletionOfSameInnerclass() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/editionanddeletionsameinnerclass/left.java"), 
				new File("testfiles/editionanddeletionsameinnerclass/base.java"), 
				new File("testfiles/editionanddeletionsameinnerclass/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("packagecom.example;publicclassTest{<<<<<<<MINE=======classA{doublea;doubleb;}>>>>>>>YOURSclassB{doublea;}}")
				);
	}

	/**
	 * 
	 */
	@Test
	public void testEditionsToNestedInnerclasses() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/editionsnestedinnerclasses/left.java"), 
				new File("testfiles/editionsnestedinnerclasses/base.java"), 
				new File("testfiles/editionsnestedinnerclasses/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("packagecom.example;publicclassTest{<<<<<<<MINEvoida(){inta=1;}=======>>>>>>>YOURSclassA{<<<<<<<MINEvoidm(){inta=1;}=======>>>>>>>YOURSclassB{<<<<<<<MINEvoidz(){inta=1;}=======>>>>>>>YOURSvoidy(){inta;}}voidn(){inta;}}voidb(){inta;}}")
				);
	}
	
//	/**
//	 * 
//	 */
//	@Test
//	public void toy() {
//		MergeContext ctx = 	new JFSTMerge().mergeFiles(
//				new File("testfiles/toy/left.java"), 
//				new File("testfiles/toy/base.java"), 
//				new File("testfiles/toy/right.java"), 
//				null);
//		assertTrue(
//				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
//				.equals("packagecom.example;publicclassTest{<<<<<<<MINEvoida(){inta=1;}=======>>>>>>>YOURSclassA{<<<<<<<MINEvoidm(){inta=1;}=======>>>>>>>YOURSclassB{<<<<<<<MINEvoidz(){inta=1;}=======>>>>>>>YOURSvoidy(){inta;}}voidn(){inta;}}voidb(){inta;}}")
//				);
//	}
}
