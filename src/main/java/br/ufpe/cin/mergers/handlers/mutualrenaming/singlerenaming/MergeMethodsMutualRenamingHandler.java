package br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming;

import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import de.ovgu.cide.fstgen.ast.FSTNode;
import org.apache.commons.lang3.tuple.Pair;
import org.javatuples.Quartet;

import java.util.List;

public class MergeMethodsMutualRenamingHandler implements MutualRenamingHandler {
    public void handle(MergeContext context) {
        List<FSTNode> mutuallyRemovedMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.deletedBaseNodes);
        List<FSTNode> leftNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedLeftNodes);
        List<FSTNode> rightNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedRightNodes);

        if (mutuallyRemovedMethodsOrConstructors.isEmpty()) return;
        if (leftNewMethodsOrConstructors.isEmpty() || rightNewMethodsOrConstructors.isEmpty()) return;

        mutuallyRemovedMethodsOrConstructors.stream()
                .map(node -> getNodesWithMatchingBody(node, leftNewMethodsOrConstructors, rightNewMethodsOrConstructors))
                .filter(this::hasFoundMatchingNodes)
                .filter(this::hasConflict)
                .forEach(pair -> reportConflictAndUpdateNodesLists(context, pair.getLeft(), pair.getRight(),
                        leftNewMethodsOrConstructors, rightNewMethodsOrConstructors)
                );
    }

    private Pair<FSTNode, FSTNode> getNodesWithMatchingBody(FSTNode baseRemovedNode, List<FSTNode> leftNodes,
                                                            List<FSTNode> rightNodes) {
        FSTNode leftNode = leftNodes.stream()
                .filter(node -> RenamingUtils.haveSimilarBody(node, baseRemovedNode))
                .findFirst()
                .orElse(null);

        FSTNode rightNode = rightNodes.stream()
                .filter(node -> RenamingUtils.haveSimilarBody(node, baseRemovedNode))
                .findFirst()
                .orElse(null);

        return Pair.of(leftNode, rightNode);
    }

    private boolean hasFoundMatchingNodes(Pair<FSTNode, FSTNode> nodePair) {
        return nodePair.getLeft() != null && nodePair.getRight() != null;
    }

    private boolean hasConflict(Pair<FSTNode, FSTNode> nodePair) {
        return RenamingUtils.haveDifferentSignature(nodePair.getLeft(), nodePair.getRight());
    }

    private void reportConflictAndUpdateNodesLists(MergeContext context, FSTNode leftNode, FSTNode rightNode,
                                                   List<FSTNode> leftNewMethodsOrConstructors,
                                                   List<FSTNode> rightNewMethodsOrConstructors) {
        RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode, null);
        leftNewMethodsOrConstructors.remove(leftNode);
        rightNewMethodsOrConstructors.remove(rightNode);
    }

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        // Do nothing (keep both)
    }
    
}
