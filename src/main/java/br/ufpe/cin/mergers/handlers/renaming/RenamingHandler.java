package br.ufpe.cin.mergers.handlers.renaming;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;

import org.javatuples.Quartet;

public interface RenamingHandler {
    void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes)
            throws TextualMergeException;
}
