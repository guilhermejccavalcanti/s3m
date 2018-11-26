package br.ufpe.cin.mergers.handlers.singlerenaming;

import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface SingleRenamingHandler {
    void handle(MergeContext context, String baseContent, FSTNode conflictNode,
                List<Pair<Double, String>> similarNodes, Side newNodeSide);
}
