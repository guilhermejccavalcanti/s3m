package br.ufpe.cin.mergers.handlers.renaming;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.javatuples.Quartet;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Traverser;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Renaming or deletions conflicts happen when one developer edits a element
 * renamed or deleted by other. Semistructured merge is unable to detect such
 * cases because it matches elements via its identifier, so if a element is
 * renamed or deleted it cannot match the elements anymore. This class overcomes
 * this issue.
 * 
 * @author Guilherme
 *
 */
public final class NoExtraFalsePositivesRenamingHandler implements RenamingHandler {

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes)
            throws TextualMergeException {
        if(isSingleRenaming(scenarioNodes)) {
            handleSingleRenaming(context, scenarioNodes);
        } 
        
        else {
            handleDoubleRenaming(context, scenarioNodes);
        }

    }
    
    private void handleSingleRenaming(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        FSTNode baseNodeInMergeTree = Traverser.retrieveNodeFromTree(baseNode, context.superImposedTree);

        if(RenamingUtils.nodeHasConflict(baseNodeInMergeTree)) {
            String leftNodeContent = RenamingUtils.getNodeContent(leftNode);
            String rightNodeContent = RenamingUtils.getNodeContent(rightNode);
            String mergeNodeContent = RenamingUtils.getNodeContent(mergeNode);

            if(unstructuredMergeHasConflictInvolvingSignature(baseNode, context)) {
                // Right renaming.
                if(RenamingUtils.haveEqualSignature(leftNode, baseNode)) {
                    RenamingUtils.generateRenamingConflict(context, mergeNodeContent, rightNodeContent, leftNodeContent, false);
                }

                else {
                    RenamingUtils.generateRenamingConflict(context, mergeNodeContent, leftNodeContent, rightNodeContent, false);
                }
            }

            else {
                // Right renaming. It's node is already in the merge tree if hasn't been deleted.
                if (RenamingUtils.haveEqualSignature(leftNode, baseNode)) {
                   ((FSTTerminal) mergeNode).setBody(leftNodeContent);
                }

                else {
                    ((FSTTerminal) mergeNode).setBody(rightNodeContent);
                }
            }
        }
    }

    private void handleDoubleRenaming(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        if(RenamingUtils.haveDifferentSignature(leftNode, rightNode) && RenamingUtils.haveEqualBodyModuloWhitespace(leftNode, rightNode)) {
            String conflictMessage = "double renaming to different signatures and edited to the same bodies";
            RenamingUtils.generateMutualRenamingConflict(context, leftNode, baseNode, rightNode, mergeNode, conflictMessage);
        }
    }

    private boolean unstructuredMergeHasConflictInvolvingSignature(FSTNode node, MergeContext context) {
        String nodeContent = ((FSTTerminal) node).getBody();
        String signature = RenamingUtils.getTrimmedSignature(nodeContent);

        return FilesManager.extractMergeConflicts(context.unstructuredOutput)
                .stream()
                .map(Object::toString)
                .map(StringUtils::deleteWhitespace)
                .filter(conflict -> conflict.contains(signature))
                .count() > 0;
    }

    private boolean isSingleRenaming(Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        return RenamingUtils.haveEqualSignature(leftNode, baseNode)
                || RenamingUtils.haveEqualSignature(rightNode, baseNode);
    }

}