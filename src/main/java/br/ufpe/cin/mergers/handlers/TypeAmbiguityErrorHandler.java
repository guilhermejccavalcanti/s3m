package br.ufpe.cin.mergers.handlers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.compiler.IProblem;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.files.GoogleTextDiffMatchPatch;
import br.ufpe.cin.files.GoogleTextDiffMatchPatch.Diff;
import br.ufpe.cin.mergers.util.JavaCompiler;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.Source;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;


/**
 * Behavioral or compilation type ambiguity errors might occur
 * when import statements share members with the same name (e.g <i>import java.util.List</i> and <i>import java.awt.List</i>, 
 * or <i>import java.util.*</i> and <i>import java.awt.*</i>). They also happen when imported members with the same name shared methods with
 * the same name (eg. <i>import java.util.List.add()</i> and <i>import java.awt.List.add()</i>). 
 * @author Guilherme
 */
public final class TypeAmbiguityErrorHandler {

	public static void handle(MergeContext context,	LinkedList<FSTNode> leftImportStatementsNodes, LinkedList<FSTNode> rightImportStatementsNodes) {
		/*
		 * using unstructured merge output as guide to ensure that semistructured merge is not worse than unstructured merge.
		 * if there is a conflict with the import statements in unstructured merge output, we flag the imports as conflicting
		 * in semistructured merge as well (might be false positive). If there isn't, we go futher and compile the semistructured 
		 * output to look for compilation problems. 
		 */
		if(!leftImportStatementsNodes.isEmpty() && !rightImportStatementsNodes.isEmpty()){
			List<MergeConflict> unstructuredMergeConflicts = FilesManager.extractMergeConflicts(context.unstructuredOutput);
			JavaCompiler compiler = new JavaCompiler();
			compiler.compile(context, Source.SEMISTRUCTURED);	//compiling source code
			while(!leftImportStatementsNodes.isEmpty()){
				FSTNode leftImportStatementNode = ((FSTTerminal)leftImportStatementsNodes.poll());
				String leftImportStatement 		= ((FSTTerminal) leftImportStatementNode).getBody();
				for(FSTNode rightImportStatementNode : rightImportStatementsNodes){
					
					//getting the imported member
					String rightImportStatement = ((FSTTerminal)rightImportStatementNode).getBody(); 
					String[] aux = rightImportStatement.split("\\.");
					String rightImportedMember = aux[aux.length-1];
					aux = leftImportStatement.split("\\.");
					String leftImportedMember  = aux[aux.length-1];
					
					//possible compilation type ambiguity error: p.* vs q.* or p.Z vs. q.Z	
					if( (rightImportedMember.equals("*;") && leftImportedMember.equals("*;")) ||
							(rightImportedMember.equals(leftImportedMember))){
						if(thereIsCompiltationProblemWithImportedStatements(compiler,context,leftImportStatement,rightImportStatement)){
							generateConflictWithImportStatements(context,leftImportStatement,rightImportStatement); break;
						} 
						/*					else if(thereIsUnstructuredConflictWithImportedStatements(unstructuredMergeConflicts,leftImportStatement, rightImportStatement)){
						generateConflictWithImportStatements(context,leftImportStatement,rightImportStatement); break;
					}*/
					}
					
					//possible behaviorial type ambiguity error: p.Z vs. q.*
					else if(rightImportedMember.equals("*;") || leftImportedMember.equals("*;")) {	
						if(thereIsUnstructuredConflictWithImportedStatements(unstructuredMergeConflicts,leftImportStatement, rightImportStatement)){
							if(thereIsContributionUsingImportedMember(context,rightImportedMember, leftImportedMember)){
								generateConflictWithImportStatements(context,leftImportStatement,rightImportStatement); break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Verifies if the contributions of the class that imported the package refers to the member imported in the other class
	 * @param context
	 * @param rightImportedMember
	 * @param leftImportedMember
	 */
	private static boolean thereIsContributionUsingImportedMember(MergeContext context, String rightImportedMember,String leftImportedMember) {
		String left = (context.getLeft()!=null)?FilesManager.readFileContent(context.getLeft()) :"";
		String base = (context.getBase()!=null)?FilesManager.readFileContent(context.getBase()) :"";
		String right=(context.getRight()!=null)?FilesManager.readFileContent(context.getRight()):"";
		if(rightImportedMember.equals("*;")){
			GoogleTextDiffMatchPatch differ = new GoogleTextDiffMatchPatch();
			List<Diff> differences = (!base.equals(""))?differ.diffMainAtLineLevel(base,right):differ.diffMainAtLineLevel(left,right);
			List<String> rightContributions = differ.diffText2Insertions(differences);
			String membername = leftImportedMember.substring(0,leftImportedMember.length()-1);
			for(String ctrb : rightContributions){
				//if(ctrb.contains(membername) && !ctrb.contains("import") && ctrb.matches("((?s).*\\b\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)*\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\b.*")){
				//if(ctrb.matches("(?s).*\\b"+membername+"\\b.*") && !ctrb.contains("import")){
				if(ctrb.matches("(?s).*(?<!\\.)\\b"+membername+"\\b.*") && !ctrb.contains("import")){
					return true;
				}
			}
		} else {
			GoogleTextDiffMatchPatch differ = new GoogleTextDiffMatchPatch();
			List<Diff> differences = (!base.equals(""))?differ.diffMainAtLineLevel(base,left):differ.diffMainAtLineLevel(right,left);
			List<String> leftContributions = differ.diffText2Insertions(differences);
			String membername = rightImportedMember.substring(0,rightImportedMember.length()-1);
			for(String ctrb : leftContributions){
				if(ctrb.matches("(?s).*(?<!\\.)\\b"+membername+"\\b.*") && !ctrb.contains("import")){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Give two import statements, verifies if there is a compilation type ambiguity error.
	 * @param compiler 
	 * @param context
	 * @param leftImportStatement
	 * @param rightImportStatement
	 */
	private static boolean thereIsCompiltationProblemWithImportedStatements(JavaCompiler compiler, MergeContext context,String leftImportStatement, String rightImportStatement) {
		//searching for compilation problems related to type ambiguity error due to the import statements
		for(int i = 0; i<compiler.compilationProblems.size(); i++){
			IProblem problem = compiler.compilationProblems.get(i);
			String problemMessage = problem.toString().toLowerCase();
			if(problemMessage.contains("collides")){
				for(String arg : problem.getArguments()){
					//checking if the compilation problem is related to the import statements
					if(rightImportStatement.contains(arg) || leftImportStatement.contains(arg)){
						compiler.compilationProblems.remove(i);//avoiding duplications
						return true;
					}
				}
			} 
			else if(problemMessage.contains("ambiguous")){
				compiler.compilationProblems.remove(i);//avoiding duplications
				return true;
			}
		}
		return false;
	}

	/**
	 * Given a list of unstructured merge conflicts, verifies if there is
	 * a conflict containing the imported statements.
	 * @param unstructuredMergeConflicts
	 * @param leftImportStatement
	 * @param rightImportStatement
	 * @return true if there is, false if not.
	 */
	private static boolean thereIsUnstructuredConflictWithImportedStatements(List<MergeConflict> unstructuredMergeConflicts,String leftImportStatement, String rightImportStatement) {
		for(MergeConflict mc : unstructuredMergeConflicts){
			if(mc.contains(leftImportStatement, rightImportStatement)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a merge conflict with the given import statements. 
	 * It also updates the merged AST with the new merge conflict.
	 * @param context
	 * @param leftImportStatement
	 * @param rightImportStatement
	 */
	private static void generateConflictWithImportStatements(MergeContext context, String leftImportStatement,String rightImportStatement) {
		//first creates a conflict with the import statements
		MergeConflict newConflict = new MergeConflict(leftImportStatement+'\n', rightImportStatement+'\n');
		//second put the conflict in one of the nodes containing the import statements, and deletes the other node containing the orther import statement
		FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, leftImportStatement, newConflict.body);
		FilesManager.findAndDeleteASTNode(context.superImposedTree, rightImportStatement);

		//statistics
		context.typeAmbiguityErrorsConflicts++;
	}
}
