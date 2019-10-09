package br.ufpe.cin.mergers.handlers.renaming;

import br.ufpe.cin.mergers.util.RenamingStrategy;

public class RenamingHandlerFactory {
    private static RenamingHandler mergeMethodsHandler = new MergeMethodsRenamingHandler();
    private static RenamingHandler keepBothMethodsHandler = new KeepBothMethodsRenamingHandler();
    private static RenamingHandler defaultHandler = new SafeRenamingHandler();

    public static RenamingHandler getHandler(RenamingStrategy strategy) {
        switch (strategy) {
        case MERGE_METHODS:
            return mergeMethodsHandler;

        case KEEP_BOTH_METHODS:
            return keepBothMethodsHandler;

        default:
            return defaultHandler;
        }
    }
}
