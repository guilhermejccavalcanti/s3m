package com.pa.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

import com.pa.analyzer.GroupAnalyzer;
import com.pa.analyzer.GroupResult;
import com.pa.comparator.ComparationVO;
import com.pa.comparator.SetCurriculoResult;
import com.pa.database.impl.DatabaseFacade;
import com.pa.entity.Group;
import com.pa.entity.Orientation;
import com.pa.entity.Publication;
import com.pa.entity.QualisData;
import com.pa.entity.TechnicalProduction;
import com.pa.util.EnumPublicationLocalType;
import com.pa.util.EnumQualisClassification;

@ManagedBean(name = "relatorioBean")
@ViewScoped
public class RelatorioBean {
	private DualListModel<Group> groups;
	private List<QualisData> qualisDataConference;
	private List<QualisData> qualisDataPeriodic;

	private Boolean checkQualisDataConference = true;
	private Boolean checkQualisDataPeriodic = true;
	private Boolean checkOrientations = true;
	private Boolean checkTechinicalProduction = true;

	private QualisData selectedQualisDataConference;
	private QualisData selectedQualisDataPeriodic;

	private TreeNode root = null;
	
	@PostConstruct
	public void init() {
		List<Group> groupsTarget = new ArrayList<Group>();

		List<Group> groupsFromDatabase = DatabaseFacade.getInstance().listAllGroups();

		QualisData example = new QualisData();
		example.setType(EnumPublicationLocalType.CONFERENCE);
		qualisDataConference = DatabaseFacade.getInstance().listAllQualisData(example);

		QualisData examplePeriodic = new QualisData();
		examplePeriodic.setType(EnumPublicationLocalType.PERIODIC);
		qualisDataPeriodic = DatabaseFacade.getInstance().listAllQualisData(examplePeriodic);

		groups = new DualListModel<Group>(groupsFromDatabase, groupsTarget);
	}

	public void comparar(ActionEvent actionEvent) {
		List<Group> selectedGroups = groups.getTarget();

		root = new DefaultTreeNode();
		Map<String, TreeNode> mapTypeByNode = new HashMap<String, TreeNode>();

		for (Group group : selectedGroups) {
			putValuesForGroup(mapTypeByNode, group);
		}
	}
	
	public void compararDetalhado(ActionEvent actionEvent) {
		List<Group> selectedGroups = groups.getTarget();

		root = new DefaultTreeNode();
		Map<String, TreeNode> mapTypeByNode = new HashMap<String, TreeNode>();

		for (Group group : selectedGroups) {
			putValuesPublicationsForQualis(mapTypeByNode, group);
		}
	}

	private void putValuesForGroup(Map<String, TreeNode> mapTypeByNode, Group group) {
		Map<EnumPublicationLocalType, QualisData> qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(EnumPublicationLocalType.PERIODIC, selectedQualisDataPeriodic);
		qualisDataMap.put(EnumPublicationLocalType.CONFERENCE, selectedQualisDataConference);

		SetCurriculoResult gR = GroupAnalyzer.getInstance().analyzerGroup(group, qualisDataMap);

		// conferencias
		if (checkQualisDataConference) {
			putValuesFromConference(mapTypeByNode, gR);
		}

		// periódicos
		if (checkQualisDataPeriodic) {
			putValuesFromPeriodics(mapTypeByNode, gR);
		}

		// orientações
		if (checkOrientations) {
			putValuesFromOrientations(mapTypeByNode, gR);
		}
	}

	private void putValuesPublicationsForQualis(Map<String, TreeNode> mapTypeByNode, Group group) {
		Map<EnumPublicationLocalType, QualisData> qualisDataMap = new HashMap<EnumPublicationLocalType, QualisData>();
		qualisDataMap.put(EnumPublicationLocalType.PERIODIC, selectedQualisDataPeriodic);
		qualisDataMap.put(EnumPublicationLocalType.CONFERENCE, selectedQualisDataConference);

		GroupResult groupResult = GroupAnalyzer.getInstance().groupResult(group, qualisDataMap);

		if (checkQualisDataConference) {
			putPublicationsFromConference(mapTypeByNode, groupResult);
		}
		if (checkQualisDataPeriodic) {
			putPublicationsFromPeriodics(mapTypeByNode, groupResult);
		}
		if (checkOrientations) {
			putPublicationsFromOrientations(mapTypeByNode, groupResult);
		}
		if (checkTechinicalProduction) {
			putValuesFromTechinicalProduction(mapTypeByNode, groupResult);
		}
		
	}
	
	private void putValuesFromTechinicalProduction(Map<String, TreeNode> mapTypeByNode, GroupResult gR) {
		TreeNode techinicalProduction = null;

		if (!mapTypeByNode.containsKey("techinicalProductions")) {
			techinicalProduction = new DefaultTreeNode("techinicalProductions", new ComparationVO("Produção técnica", "-"), root);
			mapTypeByNode.put("techinicalProductions", techinicalProduction);
		} else {
			techinicalProduction = mapTypeByNode.get("techinicalProductions");
			ComparationVO valueObject = (ComparationVO) techinicalProduction.getData();

			List<String> valueConference = valueObject.getValues();
			valueConference.add("-");
		}
		
		List<TechnicalProduction> conferencesByQualis = gR.getTechinicalProductions();
		
		for (int j = 0; j < conferencesByQualis.size(); j++) {
			TechnicalProduction tP = conferencesByQualis.get(j);
			if (tP != null) {
				String value = tP.toString();
				if (mapTypeByNode.containsKey("techinicalProductions")) {
						TreeNode conferencesQualis = new DefaultTreeNode(
								"techinicalProductions", new ComparationVO(tP.getTipo(),	tP.getAno() + " - " + tP.getTitulo()), techinicalProduction);

						mapTypeByNode.put("techinicalProductions", conferencesQualis);
				} else {
					TreeNode periodicsQualis = mapTypeByNode.get("techinicalProductions");
					ComparationVO valueObject = (ComparationVO) periodicsQualis.getData();

					List<String> valueConference = valueObject.getValues();
					valueConference.add(value);
				}
			}
		}
	}
	
	private void putPublicationsFromPeriodics(Map<String, TreeNode> mapTypeByNode, GroupResult gR) {
		TreeNode periodics = null;

		if (!mapTypeByNode.containsKey("periodics")) {
			periodics = new DefaultTreeNode("periodics", new ComparationVO("Periódicos", "-"), root);
			mapTypeByNode.put("periodics", periodics);
		} else {
			periodics = mapTypeByNode.get("periodics");
			ComparationVO valueObject = (ComparationVO) periodics.getData();

			List<String> valueConference = valueObject.getValues();
			valueConference.add("-");
		}

		Map<EnumQualisClassification, List<Publication>> conferencesByQualis = gR.getPeriodicsByQualis();
		EnumQualisClassification[] qualis = EnumQualisClassification.values();

		for (EnumQualisClassification enumQualisClassification : qualis) {
			List<Publication> averagePeriodics = conferencesByQualis.get(enumQualisClassification);
			if (averagePeriodics != null) {
				String value = averagePeriodics.toString();
				if (!mapTypeByNode.containsKey("periodics" + enumQualisClassification.toString())) {
					for (int i = 0; i < averagePeriodics.size(); i++) {
						TreeNode conferencesQualis = new DefaultTreeNode(
								"periodics" + enumQualisClassification.toString(),
								new ComparationVO(
										enumQualisClassification.toString()
												+ "  -  "+ averagePeriodics.get(i).getPublicationType().getName(),
										averagePeriodics.get(i).getYear() + " - " + averagePeriodics.get(i).getTitle()),
								periodics);

						mapTypeByNode.put("periodics" + enumQualisClassification.toString(), conferencesQualis);
					}
				} else {
					TreeNode periodicsQualis = mapTypeByNode.get("periodics" + enumQualisClassification.toString());
					ComparationVO valueObject = (ComparationVO) periodicsQualis.getData();

					List<String> valueConference = valueObject.getValues();
					valueConference.add(value);
				}
			}
		}
	}

	private void putPublicationsFromConference(Map<String, TreeNode> mapTypeByNode, GroupResult gR) {
		TreeNode conferences = null;

		if (!mapTypeByNode.containsKey("conferences")) {
			conferences = new DefaultTreeNode("conferences", new ComparationVO("Conferências", "-"), root);
			mapTypeByNode.put("conferences", conferences);
		} else {
			conferences = mapTypeByNode.get("conferences");
			ComparationVO valueObject = (ComparationVO) conferences.getData();

			List<String> valueConference = valueObject.getValues();
			valueConference.add("-");
		}

		Map<EnumQualisClassification, List<Publication>> conferencesByQualis = gR.getConferencesByQualis();
		EnumQualisClassification[] qualis = EnumQualisClassification.values();

		for (EnumQualisClassification enumQualisClassification : qualis) {
			List<Publication> averageConferences = conferencesByQualis.get(enumQualisClassification);
			if (averageConferences != null) {
				String value = averageConferences.toString();
				if (!mapTypeByNode.containsKey("conferences" + enumQualisClassification.toString())) {
					for (int i = 0; i < averageConferences.size(); i++) {
						TreeNode conferencesQualis = new DefaultTreeNode(
								"conferences" + enumQualisClassification.toString(),
								new ComparationVO(enumQualisClassification.toString() + "  -  " + averageConferences.get(i).getPublicationType().getName(),
										averageConferences.get(i).getYear() + " - "
												+ averageConferences.get(i).getTitle()),
								conferences);

						mapTypeByNode.put("conferences" + enumQualisClassification.toString(), conferencesQualis);
					}

				} else {
					TreeNode conferencesQualis = mapTypeByNode.get("conferences" + enumQualisClassification.toString());
					ComparationVO valueObject = (ComparationVO) conferencesQualis.getData();

					List<String> valueConference = valueObject.getValues();
					valueConference.add(value);
				}
			}
		}
	}
	
	private void putPublicationsFromOrientations(Map<String, TreeNode> mapTypeByNode, GroupResult gR) {
		TreeNode orientations = null;

		if (!mapTypeByNode.containsKey("orientations")) {
			orientations = new DefaultTreeNode("orientations", new ComparationVO("Orientações", "-"), root);
			mapTypeByNode.put("orientations", orientations);
		} else {
			orientations = mapTypeByNode.get("orientations");
			ComparationVO valueObject = (ComparationVO) orientations.getData();

			List<String> valueConference = valueObject.getValues();
			valueConference.add("-");
		}

		if (mapTypeByNode.containsKey("orientations")) {
			for (int i = 0; i< gR.getOrientations().size(); i++) {
				Orientation orientation = gR.getOrientations().get(i);
				TreeNode conferencesQualis = new DefaultTreeNode("orientations",
						new ComparationVO(orientation.getTipoOrientacao().getName(), orientation.getAno() + " - " + orientation.getTitulo()), orientations);
				mapTypeByNode.put("orientations", conferencesQualis);
			}
		
		} else {
			TreeNode concludedOrientationsNode = mapTypeByNode.get("orientations");
			ComparationVO valueObject = (ComparationVO) concludedOrientationsNode.getData();
		}

	}

	private void putValuesFromOrientations(Map<String, TreeNode> mapTypeByNode, SetCurriculoResult gR) {
		TreeNode orientations = null;

		if (!mapTypeByNode.containsKey("orientations")) {
			orientations = new DefaultTreeNode("orientations", new ComparationVO("Orientações", "-"), root);
			mapTypeByNode.put("orientations", orientations);
		} else {
			orientations = mapTypeByNode.get("orientations");
			ComparationVO valueObject = (ComparationVO) orientations.getData();

			List<String> valueConference = valueObject.getValues();
			valueConference.add("-");
		}

		Double concludedOrientations = gR.getConcludedOrientations();
		Double onGoingOrientations = gR.getOnGoingOrientations();

		String concludedOrientationsValue = concludedOrientations.toString();

		if (!mapTypeByNode.containsKey("concludedOrientations")) {
			TreeNode conferencesQualis = new DefaultTreeNode("concludedOrientations",
					new ComparationVO("Concluídas", concludedOrientationsValue), orientations);
			mapTypeByNode.put("concludedOrientations", conferencesQualis);
		} else {
			TreeNode concludedOrientationsNode = mapTypeByNode.get("concludedOrientations");
			ComparationVO valueObject = (ComparationVO) concludedOrientationsNode.getData();

			List<String> concludedOrientationsValues = valueObject.getValues();
			concludedOrientationsValues.add(concludedOrientationsValue);
		}

		String onGoingOrientationsValue = onGoingOrientations.toString();
		if (!mapTypeByNode.containsKey("onGoingOrientations")) {
			TreeNode onGoingOrientationsNode = new DefaultTreeNode("onGoingOrientations",
					new ComparationVO("Em Andamento", onGoingOrientationsValue), orientations);
			mapTypeByNode.put("onGoingOrientations", onGoingOrientationsNode);
		} else {
			TreeNode onGoingOrientationsNode = mapTypeByNode.get("onGoingOrientations");
			ComparationVO valueObject = (ComparationVO) onGoingOrientationsNode.getData();

			List<String> onGoingOrientationsValues = valueObject.getValues();
			onGoingOrientationsValues.add(onGoingOrientationsValue);
		}
	}

	private void putValuesFromPeriodics(Map<String, TreeNode> mapTypeByNode, SetCurriculoResult gR) {
		TreeNode periodics = null;

		if (!mapTypeByNode.containsKey("periodics")) {
			periodics = new DefaultTreeNode("periodics", new ComparationVO("Periódicos", "-"), root);
			mapTypeByNode.put("periodics", periodics);
		} else {
			periodics = mapTypeByNode.get("periodics");
			ComparationVO valueObject = (ComparationVO) periodics.getData();

			List<String> valueConference = valueObject.getValues();
			valueConference.add("-");
		}

		Map<EnumQualisClassification, Double> conferencesByQualis = gR.getAveragePeriodicsByQualis();
		EnumQualisClassification[] qualis = EnumQualisClassification.values();

		for (EnumQualisClassification enumQualisClassification : qualis) {
			Double averagePeriodics = conferencesByQualis.get(enumQualisClassification);
			String value = averagePeriodics.toString();

			if (!mapTypeByNode.containsKey("periodics" + enumQualisClassification.toString())) {
				TreeNode conferencesQualis = new DefaultTreeNode("periodics" + enumQualisClassification.toString(),
						new ComparationVO(enumQualisClassification.toString(), value), periodics);
				mapTypeByNode.put("periodics" + enumQualisClassification.toString(), conferencesQualis);
			} else {
				TreeNode periodicsQualis = mapTypeByNode.get("periodics" + enumQualisClassification.toString());
				ComparationVO valueObject = (ComparationVO) periodicsQualis.getData();

				List<String> valueConference = valueObject.getValues();
				valueConference.add(value);
			}
		}
	}

	private void putValuesFromConference(Map<String, TreeNode> mapTypeByNode, SetCurriculoResult gR) {
		TreeNode conferences = null;

		if (!mapTypeByNode.containsKey("conferences")) {
			conferences = new DefaultTreeNode("conferences", new ComparationVO("Conferências", "-"), root);
			mapTypeByNode.put("conferences", conferences);
		} else {
			conferences = mapTypeByNode.get("conferences");
			ComparationVO valueObject = (ComparationVO) conferences.getData();

			List<String> valueConference = valueObject.getValues();
			valueConference.add("-");
		}

		Map<EnumQualisClassification, Double> conferencesByQualis = gR.getAverageConferencesByQualis();
		EnumQualisClassification[] qualis = EnumQualisClassification.values();

		for (EnumQualisClassification enumQualisClassification : qualis) {
			Double averageConferences = conferencesByQualis.get(enumQualisClassification);
			String value = averageConferences.toString();

			if (!mapTypeByNode.containsKey("conferences" + enumQualisClassification.toString())) {
				TreeNode conferencesQualis = new DefaultTreeNode("conferences" + enumQualisClassification.toString(),
						new ComparationVO(enumQualisClassification.toString(), value), conferences);
				mapTypeByNode.put("conferences" + enumQualisClassification.toString(), conferencesQualis);
			} else {
				TreeNode conferencesQualis = mapTypeByNode.get("conferences" + enumQualisClassification.toString());
				ComparationVO valueObject = (ComparationVO) conferencesQualis.getData();

				List<String> valueConference = valueObject.getValues();
				valueConference.add(value);
			}
		}
	}

	public DualListModel<Group> getGroups() {
		return groups;
	}

	public void setGroups(DualListModel<Group> groups) {
		this.groups = groups;
	}

	public List<QualisData> getQualisDataConference() {
		return qualisDataConference;
	}

	public void setQualisDataConference(List<QualisData> qualisDataConference) {
		this.qualisDataConference = qualisDataConference;
	}

	public List<QualisData> getQualisDataPeriodic() {
		return qualisDataPeriodic;
	}

	public void setQualisDataPeriodic(List<QualisData> qualisDataPeriodic) {
		this.qualisDataPeriodic = qualisDataPeriodic;
	}

	public QualisData getSelectedQualisDataConference() {
		return selectedQualisDataConference;
	}

	public void setSelectedQualisDataConference(QualisData selectedQualisDataConference) {
		this.selectedQualisDataConference = selectedQualisDataConference;
	}

	public QualisData getSelectedQualisDataPeriodic() {
		return selectedQualisDataPeriodic;
	}

	public void setSelectedQualisDataPeriodic(QualisData selectedQualisDataPeriodic) {
		this.selectedQualisDataPeriodic = selectedQualisDataPeriodic;
	}

	public Boolean getCheckQualisDataConference() {
		return checkQualisDataConference;
	}

	public void setCheckQualisDataConference(Boolean checkQualisDataConference) {
		this.checkQualisDataConference = checkQualisDataConference;
	}

	public Boolean getCheckQualisDataPeriodic() {
		return checkQualisDataPeriodic;
	}

	public void setCheckQualisDataPeriodic(Boolean checkQualisDataPeriodic) {
		this.checkQualisDataPeriodic = checkQualisDataPeriodic;
	}

	public Boolean getCheckOrientations() {
		return checkOrientations;
	}

	public void setCheckOrientations(Boolean checkOrientations) {
		this.checkOrientations = checkOrientations;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

}
