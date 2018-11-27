package br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming;

import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import de.ovgu.cide.fstgen.ast.FSTNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MergeMethodsMutualRenamingHandler implements MutualRenamingHandler {
    public void handle(MergeContext context) {
        List<FSTNode> mutualRemovedMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.deletedBaseNodes);
        List<FSTNode> leftNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedLeftNodes);
        List<FSTNode> rightNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedRightNodes);

        if (mutualRemovedMethodsOrConstructors.isEmpty()) return;
        if (leftNewMethodsOrConstructors.isEmpty() || rightNewMethodsOrConstructors.isEmpty()) return;

        mutualRemovedMethodsOrConstructors.stream()
                .map(node -> getNodesWithMatchingBody(node, leftNewMethodsOrConstructors, rightNewMethodsOrConstructors))
                .filter(pair -> pair.getLeft() != null && pair.getRight() != null)
                .filter(pair -> RenamingUtils.haveDifferentSignature(pair.getLeft(), pair.getRight()))
                .forEach(pair -> {
                    RenamingUtils.generateMutualRenamingConflict(context, pair.getLeft(), pair.getRight());
                    leftNewMethodsOrConstructors.remove(pair.getLeft());
                    rightNewMethodsOrConstructors.remove(pair.getRight());
                });
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
}
