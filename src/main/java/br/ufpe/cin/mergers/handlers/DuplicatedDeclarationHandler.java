package br.ufpe.cin.mergers.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.compiler.IProblem;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.JavaCompiler;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.Source;

/**
 * Unstructured merge added false negatives are mostly caused by failing to detect that the contributions to be merged add duplicated declarations. 
 * For example, unstructured merge reports no conflict when merging developers contributions that add declarations with the same signature to different areas of the same class.
 * This handler detects such situations. Note that semistructured merge detects such false negatives without handlers, so this handler serves only for statistics purpose.
 * @author Guilherme Cavalcanti
 */
public final class DuplicatedDeclarationHandler {

	public static void handle(MergeContext context){
		int duplicatedDeclarationErrors = 0;
		
		long t0 = System.nanoTime();

		//1. compile unstructured merge output
		JavaCompiler compiler = new JavaCompiler();
		compiler.compile(context, Source.UNSTRUCTURED);	
		
		//2. list its compilation problems
		List<IProblem> iproblems = compiler.compilationProblems;
		List<MyProblem> problems = filterDistinctProblems(iproblems);

		//3. search and account duplicated declaration errors not surround by conflicts (otherwise, it would be not a false negative)
		for(int i = 0; i<problems.size(); i++){
			MyProblem problem = problems.get(i);
			String problemMessage = problem.toString().toLowerCase();
			if(problemMessage.contains("duplicate")){
				if(!isConflictingLOC(context,problem.sourceLOCs)){
					duplicatedDeclarationErrors++;
				}
			}
		}
		
		//excluding handler execution time to not bias peformance evaluation as it is not required to original semistructured merge time
		long tf = System.nanoTime();
		long tt = tf-t0;
		context.semistructuredMergeTime = context.semistructuredMergeTime - tt;

		context.duplicatedDeclarationErrors = duplicatedDeclarationErrors;
	}

	/**
	 * Aggregates compilations problems by its message and source line numbers.
	 * @param problems
	 */
	private static List<MyProblem> filterDistinctProblems(List<IProblem> problems) {
		List<MyProblem> distinctproblems = new ArrayList<MyProblem>();
		for(IProblem problem : problems){
			boolean found = false;
			for(MyProblem mp : distinctproblems){
				if(mp.message.equals(problem.getMessage())){
					mp.sourceLOCs.add(problem.getSourceLineNumber());
					found = true; 
				}
			}
			if(!found){
				MyProblem mp = new MyProblem(problem.getMessage(),problem.getSourceLineNumber());
				distinctproblems.add(mp);
			}
		}
		return distinctproblems;
	}


	/**
	 * Checks if at least one given source line number is surround by conflicts.
	 * @param context
	 * @param sourceLOCs
	 */
	private static boolean isConflictingLOC(MergeContext context, List<Integer> sourceLOCs) {
		List<MergeConflict> conflicts = FilesManager.extractMergeConflicts(context.unstructuredOutput);
		for(int sourceLOC : sourceLOCs){
			for(MergeConflict mc : conflicts){
				if((mc.startLOC <= sourceLOC) && (mc.endLOC >= sourceLOC)){
					return true;
				}
			}
		}
		return false;
	}
}

/**
 * Custom IProblem
 * @author Guilherme Cavalcanti
 */
class MyProblem {
	List<Integer> sourceLOCs = new ArrayList<Integer>();
	String message = "";

	public MyProblem(String message, int sourceLineNumber){
		this.message = message;
		this.sourceLOCs.add(sourceLineNumber);
	}
	
	@Override
	public String toString() {
		return this.message;
	}
}
