package br.ufpe.cin.mergers.handlers.renaming;

import br.ufpe.cin.mergers.util.RenamingStrategy;

public class RenamingHandlerFactory {
    private static RenamingHandler mergeSimilarHandler = new MergeSimilarRenamingHandler();
    private static RenamingHandler keepSimilarMethodsHandler = new KeepSimilarMethodsRenamingHandler();
    private static RenamingHandler noExtraFPHandler = new NoExtraFalsePositivesRenamingHandler();
    private static RenamingHandler safelyMergeSimilarHandler = new SafelyMergeSimilarRenamingHandler();

    public static RenamingHandler getHandler(RenamingStrategy strategy) {
        switch (strategy) {
        case MERGE_SIMILAR:
            return mergeSimilarHandler;

        case KEEP_SIMILAR_METHODS:
            return keepSimilarMethodsHandler;

        case NO_EXTRA_FP:
            return noExtraFPHandler;

        default:
            return safelyMergeSimilarHandler;
        }
    }
}
