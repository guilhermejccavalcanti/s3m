package br.ufpe.cin.statistics;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.logging.LoggerFactory;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;

/**
 * Class responsible for computing statitics about the merging process.
 * @author Guilherme
 */
public final class Statistics {

	//log of statistics
	private static final Logger LOGGER = LoggerFactory.make(true);

	/**
	 * Computes number of conflicts, and conflicting lines of code of the merged files in a given merge context. 
	 * The result is logged in the <i>jfstmerge.log</i> file.
	 * @param context
	 */
	public static void compute(MergeContext context){
		LOGGER.log(Level.INFO,"FILES MERGED: "+
				((context.getLeft() != null)?context.getLeft().getAbsolutePath() :"<empty left>") + ";" +
				((context.getBase() != null)?context.getBase().getAbsolutePath() :"<empty base>") + ";" +
				((context.getRight()!= null)?context.getRight().getAbsolutePath():"<empty right>"));

		List<MergeConflict> semistructuredMergeConflicts  = FilesManager.extractMergeConflicts(context.semistructuredOutput);
		List<MergeConflict> unstructuredMergeConflits	  = FilesManager.extractMergeConflicts(context.unstructuredOutput);

		int semistructuredNumberOfConflicts = computeNumberOfConflicts(semistructuredMergeConflicts);
		int unstructuredNumberOfConflicts   = computeNumberOfConflicts(unstructuredMergeConflits);

		int semistructuredMergeConflictsLOC = computeConflictsLOC(semistructuredMergeConflicts);
		int unstructuredMergeConflictsLOC   = computeConflictsLOC(unstructuredMergeConflits);

		LOGGER.log(Level.INFO, "SEMISTRUCTURED MERGE CONFLICTS:" + semistructuredNumberOfConflicts 
				+ "#SEMISTRUCTURED MERGE CONFLICTS LOC:" + semistructuredMergeConflictsLOC
				+ "#RENAMING CONFLICTS:" + context.renamingConflicts
				+ "#DELETION CONFLICTS:" + context.deletionConflicts
				+ "#TAE CONFLICTS:" + context.typeAmbiguityErrorsConflicts
				+ "#NEREO CONFLICTS:" + context.newElementReferencingEditedOneConflicts
				);

		LOGGER.log(Level.INFO, "UNSTRUCTURED MERGE CONFLICTS:" + unstructuredNumberOfConflicts 
				+ "#UNSTRUCTURED MERGE CONFLICTS LOC:" + unstructuredMergeConflictsLOC
				);
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
