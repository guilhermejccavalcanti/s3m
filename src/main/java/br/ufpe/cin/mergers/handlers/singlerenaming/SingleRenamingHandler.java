package br.ufpe.cin.mergers.handlers.singlerenaming;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;
import org.apache.commons.lang3.tuple.Pair;
import org.javatuples.Quartet;

import java.util.List;

public interface SingleRenamingHandler {
    void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes, Side renamingSide)throws TextualMergeException;
}
