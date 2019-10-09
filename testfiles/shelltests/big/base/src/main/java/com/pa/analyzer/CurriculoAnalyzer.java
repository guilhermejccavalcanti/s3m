package com.pa.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pa.associator.QualisAssociatorService;

import com.pa.entity.Curriculo;
import com.pa.entity.Publication;
import com.pa.entity.QualisData;
import com.pa.util.EnumPublicationLocalType;
import com.pa.util.EnumQualisClassification;

public class CurriculoAnalyzer {
	private static CurriculoAnalyzer instance = null;
	
	private CurriculoAnalyzer() {}
	
	public static CurriculoAnalyzer getInstance() {
		if(instance == null) {
			instance = new CurriculoAnalyzer();
		}
		return instance;
	}

	public CurriculoResult analyzerCurriculo(Curriculo curriculo, Map<EnumPublicationLocalType, QualisData> qualisDataMap) {
		CurriculoResult cR = null;
		
		if(curriculo != null){
			cR = new CurriculoResult();

			associateAllPublicationWithQualis(curriculo, cR, qualisDataMap);
			cR.setOrientations(curriculo.getOrientations());
			cR.setConcludedOrientations(curriculo.getCountConcludedOrientations());
			cR.setOnGoingOrientations(curriculo.getCountOnGoingOrientations());
			cR.setTechinicalProductions(curriculo.getTechnicalProduction());
		}
		
		return cR;
	}

	private void associateAllPublicationWithQualis(Curriculo curriculo, CurriculoResult cR, Map<EnumPublicationLocalType, QualisData> qualisDataMap) {
		for (Publication publication : curriculo.getPublications()) {
			QualisAssociatorService.getInstance().associatePublicationQualis(publication, qualisDataMap);
			
			EnumQualisClassification qualisFromPublication = publication.getQualis();
			
			if (publication.getPublicationType() != null) {
				
				if(publication.getPublicationType().getType().equals(EnumPublicationLocalType.PERIODIC)) {
	
					if(!cR.getPeriodicsByQualis().containsKey(qualisFromPublication)) {
						List<Publication> publications = new ArrayList<Publication>();
						publications.add(publication);
	
						cR.getPeriodicsByQualis().put(qualisFromPublication, publications);
					}
					else {
						List<Publication> publicationsWithQualis = cR.getPeriodicsByQualis().get(qualisFromPublication);
						publicationsWithQualis.add(publication);
					}
				}
				else if(publication.getPublicationType().getType().equals(EnumPublicationLocalType.CONFERENCE)) {
					if(!cR.getConferencesByQualis().containsKey(qualisFromPublication)) {
						List<Publication> publications = new ArrayList<Publication>();
						publications.add(publication);
	
						cR.getConferencesByQualis().put(qualisFromPublication, publications);
					}
					else {
						List<Publication> publicationsWithQualis = cR.getConferencesByQualis().get(qualisFromPublication);
						publicationsWithQualis.add(publication);
					}
				}
			}
		}
	}
}
