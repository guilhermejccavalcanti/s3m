package com.pa.comparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.boot.model.relational.Database;

import com.pa.analyzer.CurriculoAnalyzer;
import com.pa.analyzer.CurriculoResult;
import com.pa.database.impl.DatabaseFacade;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;
import com.pa.entity.Publication;
import com.pa.entity.QualisData;
import com.pa.util.EnumPublicationLocalType;
import com.pa.util.EnumQualisClassification;

public class SetCurriculoMetrics {

	private static SetCurriculoMetrics instance = null;
	
	private SetCurriculoMetrics() {}
	
	public static SetCurriculoMetrics getInstance() {
		if(instance == null) {
			instance = new SetCurriculoMetrics();
		
		ations = concludedOrientations/size;
		onGoingOrientations = onGoingOrientations/size;
		
		SetCurriculoResult setResult = new SetCurriculoResult();
		setResult.setAverageConferencesByQualis(mapQualis.get(EnumPublicationLocalType.CONFERENCE));
		setResult.setAveragePeriodicsByQualis(mapQualis.get(EnumPublicationLocalType.PERIODIC));
		setResult.setConcludedOrientations(concludedOrientations);
		setResult.setOnGoingOrientations(onGoingOrientations);
		
		return setResult;
	}
	
	private Map<EnumPublicationLocalType, Map<EnumQualisClassification, Double>> initiMapQualis() {
		Map<EnumPublicationLocalType, Map<EnumQualisClassification, Double>> map = new HashMap<EnumPublicationLocalType, Map<EnumQualisClassification, Double>>();
		
		Map<EnumQualisClassification, Double> conferenceMap = new HashMap<EnumQualisClassification, Double>();
		conferenceMap.put(EnumQualisClassification.A1, 0.0);
		conferenceMap.put(EnumQualisClassification.A2, 0.0);
		conferenceMap.put(EnumQualisClassification.B1, 0.0);
		conferenceMap.put(EnumQualisClassification.B2, 0.0);
		conferenceMap.put(EnumQualisClassification.B3, 0.0);
		conferenceMap.put(EnumQualisClassification.B4, 0.0);
		conferenceMap.put(EnumQualisClassification.B5, 0.0);
		conferenceMap.put(EnumQualisClassification.C, 0.0);
		conferenceMap.put(EnumQualisClassification.NONE, 0.0);
		
		Map<EnumQualisClassification, Double> periodicMap = new HashMap<EnumQualisClassification, Double>();
		periodicMap.put(EnumQualisClassification.A1, 0.0);
		periodicMap.put(EnumQualisClassification.A2, 0.0);
		periodicMap.put(EnumQualisClassification.B1, 0.0);
		periodicMap.put(EnumQualisClassification.B2, 0.0);
		periodicMap.put(EnumQualisClassification.B3, 0.0);
		periodicMap.put(EnumQualisClassification.B4, 0.0);
		periodicMap.put(EnumQualisClassification.B5, 0.0);
		periodicMap.put(EnumQualisClassification.C, 0.0);
		periodicMap.put(EnumQualisClassification.NONE, 0.0);
		
		map.put(EnumPublicationLocalType.CONFERENCE, conferenceMap);
		map.put(EnumPublicationLocalType.PERIODIC, periodicMap);
		return map;
	}
	
	private Map<EnumPublicationLocalType, Map<EnumQualisClassification, Double>> addQualisFromCurriculo(CurriculoResult result, Map<EnumPublicationLocalType, Map<EnumQualisClassification, Double>> mapQualis) {
		//Conference
		for (EnumQualisClassification classification : EnumQualisClassification.values()) {
			List<Publication> publicationsConferences = result.getConferencesByQualis().get(classification);
			List<Publication> periodics = result.getPeriodicsByQualis().get(classification);
			
			if (publicationsConferences != null) {
				Integer curriculoPublications = result.getConferencesByQualis().get(classification).size();
				
				Map<EnumQualisClassification, Double> conferenceMap = mapQualis.get(EnumPublicationLocalType.CONFERENCE);
				Double actualPublications = conferenceMap.get(classification);
				conferenceMap.put(classification, actualPublications + curriculoPublications);
			}
			
			if (periodics != null) {
				Integer curriculoPublications = result.getPeriodicsByQualis().get(classification).size();
				
				Map<EnumQualisClassification, Double> periodicMap = mapQualis.get(EnumPublicationLocalType.PERIODIC);
				Double actualPublications = periodicMap.get(classification);
				periodicMap.put(classification, actualPublications + curriculoPublications);
			}
		}
		
		return mapQualis;
	}
	
	private Map<EnumPublicationLocalType, Map<EnumQualisClassification, Double>> calculateAllAverage(Map<EnumPublicationLocalType, Map<EnumQualisClassification, Double>> mapQualis, int groupSize) {
		for (EnumQualisClassification classification : EnumQualisClassification.values()) {
			Map<EnumQualisClassification, Double> conferenceMap = mapQualis.get(EnumPublicationLocalType.CONFERENCE);
			Map<EnumQualisClassification, Double> periodicMap = mapQualis.get(EnumPublicationLocalType.PERIODIC);
			
			Double totalPublicationsConference = conferenceMap.get(classification);
			Double totalPublicationsPeriodic = periodicMap.get(classification);
			
			if (totalPublicationsConference != 0) {
				conferenceMap.put(classification, totalPublicationsConference/groupSize);
			}
			
			if (totalPublicationsPeriodic != 0) {
				periodicMap.put(classification, totalPublicationsPeriodic/groupSize);
			}
		}
		
		return mapQualis;
	}
}
