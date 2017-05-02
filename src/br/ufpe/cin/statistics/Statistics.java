package br.ufpe.cin.statistics;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.logging.LoggerStatistics;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;

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

		int semistructuredNumberOfConflicts = computeNumberOfConflicts(semistructuredMergeConflicts);
		int unstructuredNumberOfConflicts   = computeNumberOfConflicts(unstructuredMergeConflits);

		int semistructuredMergeConflictsLOC = computeConflictsLOC(semistructuredMergeConflicts);
		int unstructuredMergeConflictsLOC   = computeConflictsLOC(unstructuredMergeConflits);

		//logging
		String filesMerged = ((context.getLeft() != null)?context.getLeft().getAbsolutePath() :"<empty left>") + "#" +
				((context.getBase() != null)?context.getBase().getAbsolutePath() :"<empty base>") + "#" +
				((context.getRight()!= null)?context.getRight().getAbsolutePath():"<empty right>");
		String loggermsg = filesMerged 
				+ "," + semistructuredNumberOfConflicts 
				+ "," + semistructuredMergeConflictsLOC
				+ "," + context.renamingConflicts
				+ "," + context.deletionConflicts
				+ "," + context.typeAmbiguityErrorsConflicts
				+ "," + context.newElementReferencingEditedOneConflicts
				+ "," + context.initializationBlocksConflicts
				+ "," + unstructuredNumberOfConflicts 
				+ "," + unstructuredMergeConflictsLOC;
		LoggerStatistics.log(loggermsg,context);
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
