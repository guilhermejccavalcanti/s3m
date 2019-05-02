package br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming;

import org.javatuples.Quartet;

import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;

public class KeepBothMethodsSingleRenamingHandler implements MutualRenamingHandler {
    public void handle(MergeContext context) {
        // Do nothing (keep both)
    }

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        // Do nothing (keep both)
    }
}
