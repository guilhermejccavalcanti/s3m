package br.ufpe.cin.statistics.st;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import br.ufpe.cin.files.st.FilesManager;
import br.ufpe.cin.files.st.FilesTuple;
import br.ufpe.cin.logging.st.LoggerStatistics;
import br.ufpe.cin.mergers.util.st.MergeConflict;
import br.ufpe.cin.mergers.util.st.MergeContextSt;
import br.ufpe.cin.mergers.util.st.MergeScenario;
import br.ufpe.cin.mergers.util.st.Source;

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
	public static void compute(MergeContextSt context) throws Exception{
		List<MergeConflict> structuredMergeConflicts  = FilesManager.extractMergeConflicts(context.structuredOutput);
		List<MergeConflict> unstructuredMergeConflits	  = FilesManager.extractMergeConflicts(context.unstructuredOutput);

		context.structuredNumberOfConflicts = computeNumberOfConflicts(structuredMergeConflicts);
		context.unstructuredNumberOfConflicts   = computeNumberOfConflicts(unstructuredMergeConflits);

		context.structuredMergeConflictsLOC = computeConflictsLOC(structuredMergeConflicts);
		context.unstructuredMergeConflictsLOC   = computeConflictsLOC(unstructuredMergeConflits);

		context.equalConflicts = computeEqualConflicts(unstructuredMergeConflits,structuredMergeConflicts);

		String filesMerged = ((context.getLeft() != null)?context.getLeft().getAbsolutePath() :"<empty left>") + "#" +
				((context.getBase() != null)?context.getBase().getAbsolutePath() :"<empty base>") + "#" +
				((context.getRight()!= null)?context.getRight().getAbsolutePath():"<empty right>");
		String loggermsg = filesMerged 
				+ "," + context.structuredNumberOfConflicts 
				+ "," + context.structuredMergeConflictsLOC
				+ "," + context.unstructuredNumberOfConflicts 
				+ "," + context.unstructuredMergeConflictsLOC
				+ "," + context.unstructuredMergeTime
				+ "," + context.structuredMergeTime	
				+ "," + context.equalConflicts;

		computeDifferentConflicts(context);		
		LoggerStatistics.logContext(loggermsg,context);

	}


	/**
	 * Aggregates the statistics of the merged files in a given merge scenario. 
	 * @param scenario
	 */
	public static void compute(MergeScenario scenario) throws Exception{
		int structuredNumberOfConflicts = 0; 
		int structuredMergeConflictsLOC = 0;
		int unstructuredNumberOfConflicts = 0;
		int unstructuredMergeConflictsLOC = 0;
		long unstructuredMergeTime = 0;
		long structuredMergeTime	= 0;
		int equalConflicts 	  = 0;

		for(FilesTuple tuple : scenario.getTuples()){
			MergeContextSt context = tuple.getContext();
			structuredNumberOfConflicts += context.structuredNumberOfConflicts;
			structuredMergeConflictsLOC += context.structuredMergeConflictsLOC;
			unstructuredNumberOfConflicts += context.unstructuredNumberOfConflicts;
			unstructuredMergeConflictsLOC += context.unstructuredMergeConflictsLOC;
			unstructuredMergeTime 	+= context.unstructuredMergeTime;
			structuredMergeTime += context.structuredMergeTime;
			equalConflicts 	  += context.equalConflicts;
		}

		String loggermsg = scenario.getRevisionsFilePath() 
				+ "," + structuredNumberOfConflicts 
				+ "," + structuredMergeConflictsLOC
				+ "," + unstructuredNumberOfConflicts 
				+ "," + unstructuredMergeConflictsLOC
				+ "," + unstructuredMergeTime
				+ "," + structuredMergeTime
				+ "," + equalConflicts+	'\n';

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

	/**
	 * Calculates textually equal conflicts from given list of unstructured and structured merge conflicts.
	 * @param unstructuredMergeConflits
	 * @param structuredMergeConflicts
	 * @return number of equal conflicts
	 */
	private static int computeEqualConflicts(List<MergeConflict> unstructuredMergeConflits,	List<MergeConflict> structuredMergeConflicts) {
		int equalconfs = 0;
		for(MergeConflict mctxt : unstructuredMergeConflits ){
			for(MergeConflict mcssm : structuredMergeConflicts){
				if(areEquivalentConflicts(mctxt,mcssm)){equalconfs++;break;}
			}
		}
		return equalconfs;
	}

	/**
	 * Computes and print textually equal and different conflicts from a given merge context/merged file. 
	 * @param scenario
	 * @throws IOException 
	 */
	private static void computeDifferentConflicts(MergeContextSt context) throws IOException {
		Deque<MergeConflict> structuredMergeConflicts  = new ArrayDeque<MergeConflict>();
		structuredMergeConflicts.addAll(FilesManager.extractMergeConflicts(context.structuredOutput));

		Deque<MergeConflict> unstructuredMergeConflits = new ArrayDeque<MergeConflict>();
		unstructuredMergeConflits.addAll(FilesManager.extractMergeConflicts(context.unstructuredOutput));

		List<MergeConflict> differentUnstructuredMergeConflicts = new ArrayList<MergeConflict>();
		List<MergeConflict> differentSemistructuredMergeConflicts = new ArrayList<MergeConflict>();
		List<MergeConflict> equalMergeConflicts = new ArrayList<MergeConflict>();


		for(MergeConflict confa : unstructuredMergeConflits){
			confa.setOriginFiles(context.getLeft(), context.getBase(), context.getRight());
			boolean found = false;
			for(MergeConflict confb : structuredMergeConflicts){
				if(areEquivalentConflicts(confa,confb)){
					equalMergeConflicts.add(confb); //or confa
					found = true;
					break;
				}
			}
			if(!found){
				differentUnstructuredMergeConflicts.add(confa);
			}
		}
		for(MergeConflict confa : structuredMergeConflicts){
			confa.setOriginFiles(context.getLeft(), context.getBase(), context.getRight());
			boolean found = false;
			for(MergeConflict confb : unstructuredMergeConflits){
				if(areEquivalentConflicts(confa,confb)){
					found = true;
					break;
				}
			}
			if(!found){
				differentSemistructuredMergeConflicts.add(confa);
			}
		}

		LoggerStatistics.logConflicts(equalMergeConflicts,null);
		LoggerStatistics.logConflicts(differentUnstructuredMergeConflicts,Source.UNSTRUCTURED);
		LoggerStatistics.logConflicts(differentSemistructuredMergeConflicts,Source.STRUCTURED);
	}

	private static boolean areEquivalentConflicts(MergeConflict confa, MergeConflict confb) {
		String bodya = FilesManager.getStringContentIntoSingleLineNoSpacing(confa.left + confa.right);
		String bodyb = FilesManager.getStringContentIntoSingleLineNoSpacing(confb.left + confb.right);
		if(bodya.equals(bodyb)){
			return true;
		}
		return false;
	}
}
