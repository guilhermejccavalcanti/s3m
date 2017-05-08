package br.ufpe.cin.statistics;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.files.FilesTuple;
import br.ufpe.cin.logging.LoggerStatistics;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.MergeScenario;

/**
 * Class responsible for computing statitics about the merging process.
 * @author Guilherme
 */
public final class Statistics {

	/**
	 * Computes number of conflicts, and conflicting lines of code of the merged files in a given merge context. 
	 * The result is logged in the <i>jfstmerge.log</i> file.
	 * @param context
	 * @throws Exception 
	 */
	public static void compute(MergeContext context) throws Exception{
		List<MergeConflict> semistructuredMergeConflicts  = FilesManager.extractMergeConflicts(context.semistructuredOutput);
		List<MergeConflict> unstructuredMergeConflits	  = FilesManager.extractMergeConflicts(context.unstructuredOutput);

		context.semistructuredNumberOfConflicts = computeNumberOfConflicts(semistructuredMergeConflicts);
		context.unstructuredNumberOfConflicts   = computeNumberOfConflicts(unstructuredMergeConflits);

		context.semistructuredMergeConflictsLOC = computeConflictsLOC(semistructuredMergeConflicts);
		context.unstructuredMergeConflictsLOC   = computeConflictsLOC(unstructuredMergeConflits);

		//logging
		String filesMerged = ((context.getLeft() != null)?context.getLeft().getAbsolutePath() :"<empty left>") + "#" +
				((context.getBase() != null)?context.getBase().getAbsolutePath() :"<empty base>") + "#" +
				((context.getRight()!= null)?context.getRight().getAbsolutePath():"<empty right>");
		String loggermsg = filesMerged 
				+ "," + context.semistructuredNumberOfConflicts 
				+ "," + context.semistructuredMergeConflictsLOC
				+ "," + context.renamingConflicts
				+ "," + context.deletionConflicts
				+ "," + context.typeAmbiguityErrorsConflicts
				+ "," + context.newElementReferencingEditedOneConflicts
				+ "," + context.initializationBlocksConflicts
				+ "," + context.unstructuredNumberOfConflicts 
				+ "," + context.unstructuredMergeConflictsLOC
				+ "," + context.unstructuredMergeTime
				+ "," + context.semistructuredMergeTime	;
		LoggerStatistics.log(loggermsg,context);
	}
	
	/**
	 * Aggregates the statistics of the merged files in a given merge scenario. 
	 * @param scenario
	 */
	public static void compute(MergeScenario scenario) throws Exception{
		int semistructuredNumberOfConflicts = 0; 
		int semistructuredMergeConflictsLOC = 0;
		int renamingConflicts = 0;
		int deletionConflicts = 0;
		int typeAmbiguityErrorsConflicts = 0;
		int newElementReferencingEditedOneConflicts = 0;
		int initializationBlocksConflicts = 0;
		int unstructuredNumberOfConflicts = 0;
		int unstructuredMergeConflictsLOC = 0;
		int unstructuredMergeTime = 0;
		int semistructuredMergeTime	= 0;
		
		for(FilesTuple tuple : scenario.getTuples()){
			MergeContext context = tuple.getContext();
			semistructuredNumberOfConflicts += context.semistructuredNumberOfConflicts;
			semistructuredMergeConflictsLOC += context.semistructuredMergeConflictsLOC;
			renamingConflicts += context.renamingConflicts;
			deletionConflicts += context.deletionConflicts;
			typeAmbiguityErrorsConflicts += context.typeAmbiguityErrorsConflicts;
			newElementReferencingEditedOneConflicts += context.newElementReferencingEditedOneConflicts;
			initializationBlocksConflicts += context.initializationBlocksConflicts;
			unstructuredNumberOfConflicts += context.unstructuredNumberOfConflicts;
			unstructuredMergeConflictsLOC += context.unstructuredMergeConflictsLOC;
			unstructuredMergeTime += context.unstructuredMergeTime;
			semistructuredMergeTime += context.semistructuredMergeTime;
		}

		String loggermsg = scenario.getRevisionsFilePath() 
				+ "," + semistructuredNumberOfConflicts 
				+ "," + semistructuredMergeConflictsLOC
				+ "," + renamingConflicts
				+ "," + deletionConflicts
				+ "," + typeAmbiguityErrorsConflicts
				+ "," + newElementReferencingEditedOneConflicts
				+ "," + initializationBlocksConflicts
				+ "," + unstructuredNumberOfConflicts 
				+ "," + unstructuredMergeConflictsLOC
				+ "," + unstructuredMergeTime
				+ "," + semistructuredMergeTime+'\n';
		
		LoggerStatistics.logScenario(loggermsg);
		
	}
	
	private static int computeNumberOfConflicts(List<MergeConflict> listofconflicts) {
		int numberOfConflicts = listofconflicts.size();
		return numberOfConflicts;
	}

	/**
	 * Computes the number of lines of code involved in a merge conflict.
	 * @param listofconflicts
	 * @return
	 */
	private static int computeConflictsLOC(List<MergeConflict> listofconflicts) {
		int conflictsloc = 0;
		for(MergeConflict mc : listofconflicts){
			conflictsloc += ((new BufferedReader(new StringReader(mc.left)).lines().collect(Collectors.toList()).size()) + (new BufferedReader(new StringReader(mc.right)).lines().collect(Collectors.toList()).size())) ;
		}
		return conflictsloc;
	}
}
