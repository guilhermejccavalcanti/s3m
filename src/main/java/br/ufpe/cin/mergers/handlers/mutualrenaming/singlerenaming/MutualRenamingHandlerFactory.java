package br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming;

import br.ufpe.cin.mergers.util.RenamingStrategy;

public class MutualRenamingHandlerFactory {
    private static MutualRenamingHandler mergeMethodsHandler = new MergeMethodsMutualRenamingHandler();
    private static MutualRenamingHandler keepBothMethodsHandler = new KeepBothMethodsSingleRenamingHandler();
    private static MutualRenamingHandler safeHandler = new SafeMutualRenamingHandler();

    public static MutualRenamingHandler getHandler(RenamingStrategy strategy) {
        switch (strategy) {
            case MERGE_METHODS:
                return mergeMethodsHandler;

            case KEEP_BOTH_METHODS:
                return keepBothMethodsHandler;

            default:
                return safeHandler;
        }
    }
}
