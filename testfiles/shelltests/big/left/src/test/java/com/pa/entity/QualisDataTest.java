package com.pa.entity;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import com.pa.entity.QualisData;
import com.pa.util.EnumPublicationLocalType;

import org.junit.Before;
import org.junit.Test;

public class QualisDataTest {
	
	QualisData data; 
	
	@Before
	public void createQualisData() {
		data = new QualisData("file", EnumPublicationLocalType.PERIODIC, 2014);
	}
	
	/*
	 * When create QualisData
	 * */
	
	@Test
	public void whenCreateQualisDataThenFileNameShouldNotBeNull() {
		assertNotNull(data.getFileName());
	}
	
	@Test
	public void whenCreateQualisDataThenTypeShouldNotBeNull () {
		assertNotNull(data.getType());
	}
	
	@Test
	public void whenCreateQualisDataThenQualisListShouldBeEmpty() {
		assertNotNull(data.getQualis());
		assertTrue(data.getQualis().isEmpty());
	}
	
	@Test
	public void WhenCreateQualisDataThenYearShouldNotBeZero() {
		assertNotEquals(new Integer(0), data.getYear());
	}
}
