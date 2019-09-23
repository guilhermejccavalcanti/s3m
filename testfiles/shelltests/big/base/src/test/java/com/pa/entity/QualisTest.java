package com.pa.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import com.pa.entity.Qualis;
import com.pa.util.EnumQualisClassification;

import org.junit.Test;

public class QualisTest {
	
	/*
	 * When create Qualis
	 * */

	@Test
	public void whenCreateQualisThenTitleShouldNotBeNull() {
		Qualis  qualis = new Qualis("Article", "A1");
		
		assertNotNull(qualis.getName());
	}
	
	@Test
	public void whenCreateQualisThenClassificationShouldNotBeNull() {
		Qualis  qualis = new Qualis("Article", "A1");
		
		assertNotNull(qualis.getClassification());
	}
	
	@Test
	public void whenCreateWrongClassificationQualisThenClassificationShouldBeNONE() {
		Qualis  qualis = new Qualis("Article", "A3");
		
		assertNotNull(qualis.getClassification());
		assertEquals(EnumQualisClassification.NONE, qualis.getClassification());
	}
	
	@Test
	public void whenCreateNullClassificationQualisThenClassificationShouldBeNONE() {
		Qualis  qualis = new Qualis("Article", null);
		
		assertNotNull(qualis.getClassification());
		assertEquals(EnumQualisClassification.NONE, qualis.getClassification());
	}
}
