package br.ufpe.cin.mergers.handlers;

import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.printers.Prettyprinter;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Class responsbile for dealing with language specific conflicts that
 * just superimposition of trees is not able to detect/resolve.
 * @author Guilherme
 */
final public class ConflictsHandler {

	public static void handle(MergeContext context) throws TextualMergeException{
		context.semistructuredOutput = Prettyprinter.print(context.superImposedTree); //partial result of semistructured merge is necessary for further processing
		
		findAndDetectTypeAmbiguityErrors(context);
		findAndDetectNewElementReferencingEditedOne(context);
		findAndResolveRenamingOrDeletionConflicts(context);
		findAndDetectInitializationBlocks(context);
		findAndDetectDeletionsOfHighLevelElements(context);
		findAndAccountDuplicatedDeclarationErrors(context);
	}

	private static void findAndDetectTypeAmbiguityErrors(MergeContext context) {
		LinkedList<FSTNode> leftImportStatements  = new LinkedList<FSTNode>();
		LinkedList<FSTNode> rightImportStatements = new LinkedList<FSTNode>();

		//identifying the import statements added by left and right
		for(int i = 0; i < context.addedLeftNodes.size();i++){
			FSTNode leftNode = context.addedLeftNodes.get(i);
			if((leftNode instanceof FSTTerminal) && leftNode.getType().contains("ImportDeclaration")){
				leftImportStatements.add(leftNode);
				context.addedLeftNodes.remove(i); //to not interfere with the others handlers
			}
		}
		for(int i = 0; i<context.addedRightNodes.size();i++){
			FSTNode rightNode = context.addedRightNodes.get(i);
			if((rightNode instanceof FSTTerminal) && rightNode.getType().contains("ImportDeclaration")){
				rightImportStatements.add(rightNode);
				context.addedRightNodes.remove(i);
			}
		}
		//invoking the specific handler for type ambiguity errors
		TypeAmbiguityErrorHandler.handle(context, leftImportStatements, rightImportStatements);
	}

	private static void findAndDetectNewElementReferencingEditedOne(MergeContext context) {
		//invoking the specific handler for new element referencing edited one
		NewElementReferencingEditedOneHandler.handle(context);
	}
	
	private static void findAndResolveRenamingOrDeletionConflicts(MergeContext context) {
		//invoking the specific handler for renaming and deletion conflicts
		RenamingConflictsHandler.handle(context);
	}
	
	private static void findAndDetectInitializationBlocks(MergeContext context) throws TextualMergeException {
		List<FSTNode> leftInitlBlocks = context.addedLeftNodes.stream()
				.filter(p -> p.getType().equals("InitializerDecl"))
				.collect(Collectors.toList());
		List<FSTNode> rightInitlBlocks= context.addedRightNodes.stream()
				.filter(p -> p.getType().equals("InitializerDecl"))
				.collect(Collectors.toList());
		List<FSTNode> baseInitlBlocks = context.deletedBaseNodes.stream()
				.filter(p -> p.getType().equals("InitializerDecl"))
				.collect(Collectors.toList());
		
		//invoking the specific handler for initialization blocks
		InitializationBlocksHandler.handle(context, leftInitlBlocks, baseInitlBlocks, rightInitlBlocks);		
	}
	
	private static void findAndAccountDuplicatedDeclarationErrors(MergeContext context) {
		//invoking the specific handler for duplicated declaration errors
		DuplicatedDeclarationHandler.handle(context);
	}
	
	private static void findAndDetectDeletionsOfHighLevelElements(MergeContext context) {
		//invoking the specific handler for high level deletions (classes, inner classes, etc.)
		DeletionsHandler.handle(context);
	}
}
