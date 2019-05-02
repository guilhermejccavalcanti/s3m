package br.ufpe.cin.mergers.handlers.singlerenaming;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.TextualMerge;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import br.ufpe.cin.mergers.util.Traverser;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

import java.util.List;

import org.javatuples.Quartet;

public class SafeSingleRenamingHandler implements SingleRenamingHandler {
    public void handle(MergeContext context, String baseContent, FSTNode conflictNode, List<FSTNode> addedNodes,
            Side renamingSide) {

        String conflictNodeContent = ((FSTTerminal) conflictNode).getBody();
        MergeConflict mergeConflict = FilesManager.extractMergeConflicts(conflictNodeContent).get(0);
        String oppositeSideNodeContent = RenamingUtils.getMergeConflictContentOfOppositeSide(mergeConflict,
                renamingSide);

        if (RenamingUtils.hasUnstructuredMergeConflict(context, baseContent)) {
            String possibleRenamingContent = RenamingUtils.getMostSimilarNodeContent(baseContent, conflictNode,
                    addedNodes);
            RenamingUtils.generateRenamingConflict(context, conflictNodeContent, possibleRenamingContent,
                    oppositeSideNodeContent, renamingSide);
        } else {
            ((FSTTerminal) conflictNode).setBody(oppositeSideNodeContent);
        }
    }

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes,
            Side renamingSide) throws TextualMergeException {

        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        RenamingUtils.runTextualMerge(leftNode, baseNode, rightNode, mergeNode);
        removeUnmatchedNode(renamingSide, scenarioNodes, context);        
    }

    private void removeUnmatchedNode(Side renamingSide, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes, MergeContext context) {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode nodeToBeRemoved = (renamingSide == Side.LEFT) ? leftNode : rightNode; 
        
        Traverser.removeNode(nodeToBeRemoved, context.superImposedTree);
    }
}
