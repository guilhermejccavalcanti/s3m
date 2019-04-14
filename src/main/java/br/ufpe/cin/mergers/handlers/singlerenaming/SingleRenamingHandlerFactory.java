package br.ufpe.cin.mergers.handlers.singlerenaming;

import br.ufpe.cin.mergers.util.RenamingStrategy;

public class SingleRenamingHandlerFactory {
    private static SingleRenamingHandler mergeMethodsHandler = new MergeMethodsSingleRenamingHandler();
    private static SingleRenamingHandler keepBothMethodsHandler = new KeepBothMethodsSingleRenamingHandler();
    private static SingleRenamingHandler defaultHandler = new SafeSingleRenamingHandler();

    public static SingleRenamingHandler getHandler(RenamingStrategy strategy) {
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
