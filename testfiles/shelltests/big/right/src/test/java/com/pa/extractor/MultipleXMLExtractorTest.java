package com.pa.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.pa.database.util.HibernateUtil;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;
import com.pa.exception.InvalidPatternFileException;
import com.pa.extractor.MultipleXMLExtractor;

import org.junit.BeforeClass;
import org.junit.Test;

public class MultipleXMLExtractorTest {
	static Group folderGroup;
	static Group fileGroup;
	
	@BeforeClass
	public static void getResourceFile() throws InvalidPatternFileException, FileNotFoundException {
		HibernateUtil.createSessionFactory("hibernateTest.cfg.xml");
		
		URL url = MultipleXMLExtractorTest.class.getResource("/multipleXML/");
		File curriculoFolder = new File(url.getFile());
		
		url = MultipleXMLExtractorTest.class.getResource("/Alex.xml");
		File curriculoFile = new File(url.getFile());
		
		MultipleXMLExtractor multipleExtractor = new MultipleXMLExtractor();
		
		List<InputStream> inputList = new ArrayList<InputStream>();
		inputList.add(new FileInputStream(curriculoFolder.listFiles()[0]));
		inputList.add(new FileInputStream(curriculoFolder.listFiles()[1]));
		
		folderGroup = multipleExtractor.lattesExtractor("CIn", inputList);
		
		inputList = new ArrayList<InputStream>();
		inputList.add(new FileInputStream(curriculoFile));
		
		fileGroup = multipleExtractor.lattesExtractor("Alex", inputList);
	}
	
	/*
	 * When extract a folder
	 * */

	@Test
	public void whenExtractFolderXMLThenReturnedGroupShouldNotBeNull() {
		assertNotNull(folderGroup);
	}
	
	@Test
	public void whenExtractFolderXMLThenReturnedGroupShouldHaveCorrectName() {
		assertEquals("CIn", folderGroup.getName());
	}
	
	@Test
	public void whenExtractFolderXMLThenReturnedGroupCurriculosShouldNotBeEmpty() {
		assertFalse(folderGroup.getCurriculos().isEmpty());
	}
	
	@Test
	public void whenExtractFolderXMLThenReturnedGroupCurriculosShouldHaveCorrectSize() {
		assertEquals(2, folderGroup.getCurriculos().size());
	}
	
	@Test
	public void whenExtractFolderXMLThenReturnedGroupCurriculosShouldHaveCorrectIds() {
		assertEquals(Long.valueOf("7310046838140771"), folderGroup.getCurriculos().get(0).getId());
		assertEquals(Long.valueOf("9395715443254344"), folderGroup.getCurriculos().get(1).getId());
	}
	
	/*
	 * When extract a single file
	 * */
	
	@Test
	public void whenExtractFileXMLThenReturnedGroupShouldNotBeNull() {
		assertNotNull(fileGroup);
	}
	
	@Test
	public void whenExtractFileXMLThenReturnedGroupShouldHaveCorrectName() {
		assertEquals("Alex", fileGroup.getName());
	}
	
	@Test
	public void whenExtractFileXMLThenReturnedGroupCurriculosShouldNotBeEmpty() {
		assertFalse(fileGroup.getCurriculos().isEmpty());
	}
	
	@Test
	public void whenExtractFileXMLThenReturnedGroupCurriculosShouldHaveCorrectSize() {
		assertEquals(1, fileGroup.getCurriculos().size());
	}
	
	@Test
	public void whenExtractFileXMLThenReturnedGroupCurriculosShouldHaveCorrectId() {
		assertEquals(Long.valueOf("7188784344595649"), fileGroup.getCurriculos().get(0).getId());
	}
	
	/*
	 * Check curriculo existence
	 * */
	
	@Test
	public void whenExtractFileXMLWithSameCurriculoThenReturnShouldBeTrue() throws FileNotFoundException, InvalidPatternFileException {
		MultipleXMLExtractor multipleExtractor = new MultipleXMLExtractor();
		multipleExtractor.saveGroup(folderGroup, false);
		
		assertTrue(multipleExtractor.checkCurriculoExistence(folderGroup.getCurriculos()));
	}
	
	@Test
	public void whenExtractFolderXMLWithSameCurriculoThenReturnShouldBeTrue() throws FileNotFoundException, InvalidPatternFileException {
		MultipleXMLExtractor multipleExtractor = new MultipleXMLExtractor();
		multipleExtractor.saveGroup(folderGroup, false);
		
		List<Curriculo> mixList = new ArrayList<Curriculo>(fileGroup.getCurriculos());
		mixList.add(folderGroup.getCurriculos().get(0));
		
		assertTrue(multipleExtractor.checkCurriculoExistence(mixList));
	}
	
	@Test
	public void whenExtractFileXMLWithNewCurriculoThenReturnShouldBeFalse() throws FileNotFoundException, InvalidPatternFileException {
		MultipleXMLExtractor multipleExtractor = new MultipleXMLExtractor();
		
		assertFalse(multipleExtractor.checkCurriculoExistence(fileGroup.getCurriculos()));
	}
	
	/*
	 * Updating curriculo
	 * */
	
	@Test
	public void whenExtractXMLFileThenCurriculoShuldBeUpdated() throws InvalidPatternFileException, FileNotFoundException {
		URL url = MultipleXMLExtractorTest.class.getResource("/Alex.xml");
		File curriculoFile = new File(url.getFile());
		
		List<InputStream> inputList = new ArrayList<InputStream>();
		inputList.add(new FileInputStream(curriculoFile));
		
		MultipleXMLExtractor multipleExtractor = new MultipleXMLExtractor();
		multipleExtractor.saveGroup(fileGroup, false);
		
		Group group = multipleExtractor.lattesExtractor("Alex (2)", inputList);
		multipleExtractor.saveGroup(group, true);
		
		assertEquals(233, group.getCurriculos().get(0).getPublications().size());
	}
}
