package com.pa.analyzer;

import java.util.Map;

import com.pa.comparator.SetCurriculoMetrics;
import com.pa.comparator.SetCurriculoResult;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;
import com.pa.entity.QualisData;
import com.pa.entity.TechnicalProduction;
import com.pa.util.EnumPublicationLocalType;

public class GroupAnalyzer {
	private static GroupAnalyzer instance = null;

	private GroupAnalyzer() {}

	public static GroupAnalyzer getInstance() {
		if(instance == null) {
			instance = new GroupAnalyzer();
		}
		return instance;
	}
	
	public GroupResult groupResult(Group group, Map<EnumPublicationLocalType, QualisData> qualisDataMap) {
		GroupResult groupResult = null;
		if(group != null) {
			groupResult = new GroupResult();
			
			for (Curriculo curriculo : group.getCurriculos()) {
				CurriculoResult curriculoResult = CurriculoAnalyzer.getInstance().analyzerCurriculo(curriculo, qualisDataMap);
				
				groupResult.getConferencesByQualis().putAll(curriculoResult.getConferencesByQualis());
				groupResult.getPeriodicsByQualis().putAll(curriculoResult.getPeriodicsByQualis());
				groupResult.setTechinicalProductions(curriculoResult.getTechinicalProductions());
				groupResult.setOrientations(curriculoResult.getOrientations());
			}
		}
		return groupResult;
	}
	
	public SetCurriculoResult analyzerGroup(Group group, Map<EnumPublicationLocalType, QualisData> qualisDataMap) {
		SetCurriculoResult gR = null;
		
		if(group != null){
			gR = new SetCurriculoResult();

//			for (Curriculo curriculo : group.getCurriculos()) {
//				CurriculoResult cR = CurriculoAnalyzer.getInstance().analyzerCurriculo(curriculo, qualisDataMap);

//				Set<EnumQualisClassification> chaves = cR.getConferencesByQualis().keySet();
//				for (EnumQualisClassification chave : chaves) {
//					Iterator<Publication> it = cR.getConferencesByQualis().get(chave.A1).iterator();
//					while(it.hasNext()) {
//						System.out.println(it.next().getQualis());
//						//System.out.println(it.next().getTitle() + it.next().getQualis());
//					}
//				}
				
//				gR.getConferencesByQualis().putAll(cR.getConferencesByQualis());
//				gR.getPeriodicsByQualis().putAll(cR.getPeriodicsByQualis());
//				
//				int currentConcludedOrientations = gR.getConcludedOrientations();
//				int currentOnGoingOrientations = gR.getOnGoingOrientations();
//				
//				gR.setConcludedOrientations(currentConcludedOrientations + cR.getConcludedOrientations());
//				gR.setOnGoingOrientations(currentOnGoingOrientations + cR.getOnGoingOrientations());
//			}
			
			gR = SetCurriculoMetrics.getInstance().calculateMetrics(group, qualisDataMap);
		}
		
		return gR;
	}
	
}
