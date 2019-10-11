package com.pa.comparator;

import java.util.HashMap;
import java.util.Map;

import com.pa.util.EnumQualisClassification;

public class SetCurriculoResult {
	private Map<EnumQualisClassification, Double> averageConferencesByQualis;
	private Map<EnumQualisClassification, Double> averagePeriodicsByQualis;
	private Double concludedOrientations;
	private Double onGoingOrientations;
	
	public SetCurriculoResult() {
		this.averageConferencesByQualis = new HashMap<EnumQualisClassification, Double>();
		this.averagePeriodicsByQualis = new HashMap<EnumQualisClassification, Double>();
		this.concludedOrientations = 0.0;
		this.onGoingOrientations = 0.0;
	}

	public Map<EnumQualisClassification, Double> getAverageConferencesByQualis() {
		return averageConferencesByQualis;
	}

	public void setAverageConferencesByQualis(
			Map<EnumQualisClassification, Double> averageConferencesByQualis) {
		this.averageConferencesByQualis = averageConferencesByQualis;
	}

	public Double getConcludedOrientations() {
		return concludedOrientations;
	}

	public void setConcludedOrientations(Double concludedOrientations) {
		this.concludedOrientations = concludedOrientations;
	}

	public Double getOnGoingOrientations() {
		return onGoingOrientations;
	}

	public void setOnGoingOrientations(Double onGoingOrientations) {
		this.onGoingOrientations = onGoingOrientations;
	}

	public Map<EnumQualisClassification, Double> getAveragePeriodicsByQualis() {
		return averagePeriodicsByQualis;
	}

	public void setAveragePeriodicsByQualis(
			Map<EnumQualisClassification, Double> averagePeriodicsByQualis) {
		this.averagePeriodicsByQualis = averagePeriodicsByQualis;
	}
}
