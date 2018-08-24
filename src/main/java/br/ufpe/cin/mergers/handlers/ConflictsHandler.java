package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.printers.Prettyprinter;

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
		//invoking the specific handler for type ambiguity errors
		TypeAmbiguityErrorHandler.handle(context);
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
		//invoking the specific handler for initialization blocks
		InitializationBlocksHandler.handle(context);
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
