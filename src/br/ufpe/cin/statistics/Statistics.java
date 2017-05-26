package br.ufpe.cin.statistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.files.FilesTuple;
import br.ufpe.cin.logging.LoggerStatistics;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.MergeScenario;
import br.ufpe.cin.mergers.util.Source;

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

		context.equalConflicts = computeEqualConflicts(unstructuredMergeConflits,semistructuredMergeConflicts);

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
				+ "," + context.semistructuredMergeTime	
				+ "," + context.duplicatedDeclarationErrors
				+ "," + context.equalConflicts;

		LoggerStatistics.logContext(loggermsg,context);
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
		long unstructuredMergeTime = 0;
		long semistructuredMergeTime	= 0;
		int duplicatedDeclarationErrors = 0;
		int equalConflicts = 0;

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
			duplicatedDeclarationErrors += context.duplicatedDeclarationErrors;
			equalConflicts += context.equalConflicts;
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
				+ "," + semistructuredMergeTime
				+ "," + duplicatedDeclarationErrors
				+ "," + equalConflicts+	'\n';

		LoggerStatistics.logScenario(loggermsg);
		computeDifferentConflicts(scenario);		
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
	 * Calculates textually equal conflicts from given list of unstructured and semistructured merge conflicts.
	 * @param unstructuredMergeConflits
	 * @param semistructuredMergeConflicts
	 * @return number of equal conflicts
	 */
	private static int computeEqualConflicts(List<MergeConflict> unstructuredMergeConflits,	List<MergeConflict> semistructuredMergeConflicts) {
		int equalconfs = 0;
		for(MergeConflict mctxt : unstructuredMergeConflits ){
			for(MergeConflict mcssm : semistructuredMergeConflicts){
				String txtbody = FilesManager.getStringContentIntoSingleLineNoSpacing(mctxt.left + mctxt.right);
				String ssmbody = FilesManager.getStringContentIntoSingleLineNoSpacing(mcssm.left + mcssm.right);
				if(txtbody.equals(ssmbody)){ equalconfs++;break;}
			}
		}
		return equalconfs;
	}
	
	/**
	 * Computes and print textually equal and different conflicts from merged files of a given merge scenarios. 
	 * @param scenario
	 * @throws IOException 
	 */
	private static void computeDifferentConflicts(MergeScenario scenario) throws IOException {
		for(FilesTuple tuple : scenario.getTuples()){
			MergeContext context = tuple.getContext();

			Deque<MergeConflict> semistructuredMergeConflicts  = new ArrayDeque<MergeConflict>();
			semistructuredMergeConflicts.addAll(FilesManager.extractMergeConflicts(context.semistructuredOutput));

			Deque<MergeConflict> unstructuredMergeConflits = new ArrayDeque<MergeConflict>();
			unstructuredMergeConflits.addAll(FilesManager.extractMergeConflicts(context.unstructuredOutput));

			List<MergeConflict> differentUnstructuredMergeConflicts = new ArrayList<MergeConflict>();
			List<MergeConflict> differentSemistructuredMergeConflicts = new ArrayList<MergeConflict>();
			List<MergeConflict> equalMergeConflicts = new ArrayList<MergeConflict>();


			for(MergeConflict confa : unstructuredMergeConflits){
				confa.setOriginFiles(context.getLeft(), context.getBase(), context.getRight());
				boolean found = false;
				for(MergeConflict confb : semistructuredMergeConflicts){
					String bodya = FilesManager.getStringContentIntoSingleLineNoSpacing(confa.left + confa.right);
					String bodyb = FilesManager.getStringContentIntoSingleLineNoSpacing(confb.left + confb.right);
					if(bodya.equals(bodyb)){
						equalMergeConflicts.add(confa);
						found = true;
						break;
					}
				}
				if(!found){
					differentUnstructuredMergeConflicts.add(confa);
				}
			}
			for(MergeConflict confa : semistructuredMergeConflicts){
				confa.setOriginFiles(context.getLeft(), context.getBase(), context.getRight());
				boolean found = false;
				for(MergeConflict confb : unstructuredMergeConflits){
					String bodya = FilesManager.getStringContentIntoSingleLineNoSpacing(confa.left + confa.right);
					String bodyb = FilesManager.getStringContentIntoSingleLineNoSpacing(confb.left + confb.right);
					if(bodya.equals(bodyb)){
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
			LoggerStatistics.logConflicts(differentSemistructuredMergeConflicts,Source.SEMISTRUCTURED);

		}
	}

}
