package com.pa.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.pa.analyzer.CurriculoAnalyzer;
import com.pa.analyzer.CurriculoResult;
import com.pa.comparator.SetCurriculoMetrics;
import com.pa.comparator.SetCurriculoResult;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.pa.entity.Curriculo;
import com.pa.entity.Group;
import com.pa.entity.Publication;
import com.pa.entity.QualisData;
import com.pa.util.EnumQualisClassification;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CurriculoAnalyzer.class})
public class SetCurriculoMetricsTest {
	static SetCurriculoResult result;
	
	@BeforeClass
	static public void createGroup() {
		mocking();
		
		Curriculo nielson = new Curriculo("Nielson", new Date());
		nielson.setId((long) 123456);
		Curriculo fernando = new Curriculo("Fernando", new Date());
		fernando.setId((long) 456789);
		Curriculo jessica = new Curriculo("Jéssica", new Date());
		jessica.setId((long) 456123);
		
		Group group = new Group("New");
		group.getCurriculos().add(jessica);
		group.getCurriculos().add(fernando);
		group.getCurriculos().add(nielson);
		
//		SetCurriculoMetrics metrics = new SetCurriculoMetrics();
		//XXX adicionar map 
//		result = metrics.calculateMetrics(group, Arrays.asList(new QualisData()));
	}
	
	static public void mocking() {
		List<CurriculoResult> results = createCurriculoResults();
		
		// Mock CurriculoAnalyzer class and instance
		PowerMockito.mockStatic(CurriculoAnalyzer.class);
		CurriculoAnalyzer analyzer = mock(CurriculoAnalyzer.class);
		
		// Return CurriculoAnalyzer mock when call getInstance
		PowerMockito.when(CurriculoAnalyzer.getInstance()).thenReturn(analyzer);
		// XXX Mock results for CurriculoAnalyzer
//		when(analyzer.analyzerCurriculo(any(Curriculo.class), any(QualisData.class))).thenReturn(results.get(0), results.get(1), results.get(2));
	}
	
	static public List<CurriculoResult> createCurriculoResults() {
		List<CurriculoResult> results = new ArrayList<CurriculoResult>();
		
		HashMap<EnumQualisClassification, List<Publication>> map = new HashMap<EnumQualisClassification, List<Publication>>();
		
		map.put(EnumQualisClassification.A1, Arrays.asList(new Publication(), new Publication()));
		map.put(EnumQualisClassification.A2, Arrays.asList(new Publication()));
		map.put(EnumQualisClassification.B2, Arrays.asList(new Publication(), new Publication(), new Publication()));
		map.put(EnumQualisClassification.B4, Arrays.asList(new Publication(), new Publication()));
		
		CurriculoResult result1 = new CurriculoResult();
		//XXX
//		result1.setPublicationsByQualis(map);
		result1.setConcludedOrientations(5);
		results.add(result1);
		
		map = new HashMap<EnumQualisClassification, List<Publication>>();
		
		map.put(EnumQualisClassification.A1, Arrays.asList(new Publication()));
		map.put(EnumQualisClassification.A2, Arrays.asList(new Publication(), new Publication(), new Publication()));
		map.put(EnumQualisClassification.B2, Arrays.asList(new Publication()));
		map.put(EnumQualisClassification.B4, Arrays.asList(new Publication(), new Publication()));
		
		CurriculoResult result2 = new CurriculoResult();
		//XXX
//		result2.setPublicationsByQualis(map);
		result2.setOnGoingOrientations(5);
		results.add(result2);
		
		map = new HashMap<EnumQualisClassification, List<Publication>>();
		
		map.put(EnumQualisClassification.A1, Arrays.asList(new Publication(), new Publication(), new Publication()));
		map.put(EnumQualisClassification.A2, Arrays.asList(new Publication(), new Publication()));
		map.put(EnumQualisClassification.B2, Arrays.asList(new Publication()));
		
		CurriculoResult result3 = new CurriculoResult();
		//XXX
//		result3.setPublicationsByQualis(map);
		result3.setConcludedOrientations(7);
		result3.setOnGoingOrientations(4);
		results.add(result3);
		
		return results;
	}
	
	/*
	 * When calculating metrics
	 * */
	
	@Test
	public void whenCalculatingMetricsThenQualisA1ShouldBeCorrect() {
//	XXX	assertEquals(new Double(2), result.getAveragePublicationByQualis().get(EnumQualisClassification.A1));
	}
	
	@Test
	public void whenCalculatingMetricsThenQualisA2ShouldBeCorrect() {
//	XXX	assertEquals(new Double(2), result.getAveragePublicationByQualis().get(EnumQualisClassification.A2));
	}
	
	@Test
	public void whenCalculatingMetricsThenQualisB2ShouldBeCorrect() {
//	XXX	assertEquals(new Double(5.0/3.0), result.getAveragePublicationByQualis().get(EnumQualisClassification.B2));
	}
	
	@Test
	public void whenCalculatingMetricsThenQualisB4ShouldBeCorrect() {
//	XXX	assertEquals(new Double(4.0/3.0), result.getAveragePublicationByQualis().get(EnumQualisClassification.B4));
	}
	
	@Test
	public void whenCalculatingMetricsThenConcludedOrientationsShouldBeCorrect() {
//		assertEquals(new Double(4), result.getConcludedOrientations());
	}
	
	@Test
	public void whenCalculatingMetricsThenOnGoingOrientationsShouldBeCorrect() {
//		assertEquals(new Double(3), result.getOnGoingOrientations());
	}
}
