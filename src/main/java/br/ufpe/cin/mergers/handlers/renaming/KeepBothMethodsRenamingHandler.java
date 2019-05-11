package br.ufpe.cin.mergers.handlers.renaming;

import org.javatuples.Quartet;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;

public class KeepBothMethodsRenamingHandler implements RenamingHandler {

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes)
            throws TextualMergeException {
        // Do nothing.
    }

}