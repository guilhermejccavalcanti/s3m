package br.ufpe.cin.mergers.handlers.renaming;

import org.javatuples.Quartet;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import de.ovgu.cide.fstgen.ast.FSTNode;

/**
 * Alternative implementation of the renaming handler.
 * 
 * This handler always resolves a renaming or deletion conflict by merging
 * the involved declarations.
 * 
 * @author Giovanni Barros (gaabs@cin.ufpe.br)
 * @author João Victor (jvsfc@cin.ufpe.br)
 */
public class MergeMethodsRenamingHandler implements RenamingHandler {

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes)
            throws TextualMergeException {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        RenamingUtils.runTextualMerge(context, leftNode, baseNode, rightNode, mergeNode);
    }

}