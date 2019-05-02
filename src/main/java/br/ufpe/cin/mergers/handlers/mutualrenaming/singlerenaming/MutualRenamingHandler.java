package br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming;

import org.javatuples.Quartet;

import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;

public interface MutualRenamingHandler {
    void handle(MergeContext context);

    void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes);
}
