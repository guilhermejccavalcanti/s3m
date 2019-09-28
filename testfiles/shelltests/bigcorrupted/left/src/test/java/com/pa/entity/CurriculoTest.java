package com.pa.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import com.pa.entity.Curriculo;

import org.junit.Before;
import org.junit.Test;

public class CurriculoTest {

	/*
	 * When create Curriculo
	 * */
	
	Curriculo curriculo;
	
	@Before
	public void createCurriculo() {
		curriculo = new Curriculo("Paulo", new Date());
	}
	
	@Test
	public void whenCreateCurriculoThenNameShouldNotBeNull() {
		assertNotNull(curriculo.getName());
	}
	
	@Test
	public void whenCreateCurriculoThenLastUpdateShouldNotBeNull() {
		assertNotNull(curriculo.getLastUpdate());
	}
	
	@Test
	public void whenCreateCurriculoThenOrientationsShouldBeZero() {
		assertEquals(0, curriculo.getCountConcludedOrientations());
	}
	
	@Test
	public void whenCreateCurriculoThenPublicationsShouldBeEmpty() {
		assertNotNull(curriculo.getPublications());
		assertTrue(curriculo.getPublications().isEmpty());
	}
}
