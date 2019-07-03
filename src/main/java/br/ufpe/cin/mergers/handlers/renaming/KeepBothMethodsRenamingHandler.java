package br.ufpe.cin.mergers.handlers.renaming;

import org.javatuples.Quartet;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Traverser;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class KeepBothMethodsRenamingHandler implements RenamingHandler {

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes)
            throws TextualMergeException {
        
        /* If it's a single renaming, then certainly the base node is in the merge tree, and it has a conflict
         * caused by the superimposition. Then, we remove the conflict to prevent a false positive in the output. */
        if(isSingleRenaming(scenarioNodes)) {
            FSTNode baseNode = scenarioNodes.getValue1();
            removeConflictFromBaseNode(baseNode, context);
        }
    }

    private void removeConflictFromBaseNode(FSTNode baseNode, MergeContext context) {
        FSTNode mergeNode = Traverser.retrieveNodeFromTree(baseNode, context.superImposedTree);
        String conflictNodeContent = ((FSTTerminal) mergeNode).getBody();
        MergeConflict mergeConflict = FilesManager.extractMergeConflicts(conflictNodeContent).get(0);
        String nodeContent = (mergeConflict.left.isEmpty()) ? mergeConflict.right : mergeConflict.left;

        ((FSTTerminal) mergeNode).setBody(nodeContent);
    }

    private boolean isSingleRenaming(Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        return RenamingUtils.haveEqualSignature(leftNode, baseNode)
                || RenamingUtils.haveEqualSignature(rightNode, baseNode);
    }

}