package br.ufpe.cin.mergers.handlers.singlerenaming;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;
import org.apache.commons.lang.StringUtils;
import org.javatuples.Quartet;

import java.util.List;

public class MergeMethodsSingleRenamingHandler implements SingleRenamingHandler {
    public void handle(MergeContext context, String baseContent, FSTNode conflictNode, List<FSTNode> addedNodes,
            Side renamingSide) {

        String conflictNodeContent = ((FSTTerminal) conflictNode).getBody();
        MergeConflict mergeConflict = FilesManager.extractMergeConflicts(conflictNodeContent).get(0);
        String oppositeSideNodeContent = RenamingUtils.getMergeConflictContentOfOppositeSide(mergeConflict,
                renamingSide);

        String possibleRenamingContent = RenamingUtils.getMostSimilarNodeContent(baseContent, conflictNode, addedNodes);
        if (StringUtils.isEmpty(possibleRenamingContent)) {
            ((FSTTerminal) conflictNode).setBody(oppositeSideNodeContent);
            return;
        }

        String newSignature = RenamingUtils.getSignature(possibleRenamingContent);
        String newBody = RenamingUtils.removeSignature(oppositeSideNodeContent);

        // TODO: check method calls

        // replace node with both nodes content
        FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, conflictNodeContent,
                newSignature + newBody);

        // remove other node
        FilesManager.findAndDeleteASTNode(context.superImposedTree, possibleRenamingContent);
    }

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes,
            Side renamingSide) {

    }
}
