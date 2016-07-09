package br.ufpe.cin.mergers.handlers;

import java.util.LinkedList;

import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.printers.Prettyprinter;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Class responsbile for dealing with language specific conflicts that
 * just superimposition of trees are not able to detect/resolve.
 * @author Guilherme
 */
final public class ConflictsHandler {
	
	public static void handle(MergeContext context){
		context.semistructuredOutput = Prettyprinter.print(context.superImposedTree); //partial result of semistructured merge is necessary for further processing
		findAndDetectTypeAmbiguityErrors(context);
	}

	private static void findAndDetectTypeAmbiguityErrors(MergeContext context) {
		LinkedList<FSTNode> leftImportStatements  = new LinkedList<FSTNode>();
		LinkedList<FSTNode> rightImportStatements = new LinkedList<FSTNode>();

		//identifying the import statements added by left and right
		for(FSTNode leftNode : context.nodesAddedByLeft){
			if((leftNode instanceof FSTTerminal) && leftNode.getType().contains("ImportDeclaration")){
				leftImportStatements.add(leftNode);
			}
		}
		for(FSTNode rightNode : context.nodesAddedByRight){
			if((rightNode instanceof FSTTerminal) && rightNode.getType().contains("ImportDeclaration")){
				rightImportStatements.add(rightNode);
			}
		}
		//invoking the specific handler for type ambiguity errors
		TypeAmbiguityErrorHandler.handle(context, leftImportStatements, rightImportStatements);
	}
}
