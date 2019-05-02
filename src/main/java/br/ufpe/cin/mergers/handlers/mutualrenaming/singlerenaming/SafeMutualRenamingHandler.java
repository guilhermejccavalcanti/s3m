package br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming;

import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.javatuples.Quartet;

import java.util.List;

public class SafeMutualRenamingHandler implements MutualRenamingHandler {
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
                .filter(node -> RenamingUtils.haveEqualBody(node, baseRemovedNode))
                .findFirst()
                .orElse(null);

        FSTNode rightNode = rightNodes.stream()
                .filter(node -> RenamingUtils.haveEqualBody(node, baseRemovedNode))
                .findFirst()
                .orElse(null);

        return Pair.of(leftNode, rightNode);
    }

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        /* Decision Tree */

        if(isRenamingWithoutBodyChanges(Side.LEFT, baseNode, context) && isRenamingWithoutBodyChanges(Side.RIGHT, baseNode, context)) {
            if(RenamingUtils.haveEqualSignature(leftNode, rightNode)) return;
            else {
                RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode);
            }
        }

        else if(isRenamingWithoutBodyChanges(Side.LEFT, baseNode, context) && isDeletionOrRenamingWithBodyChanges(Side.RIGHT, baseNode, context)) {
            if(RenamingUtils.haveEqualSignature(leftNode, rightNode)) {
                // check references
            } else {
                RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode);
            }
        }

        else if(isDeletionOrRenamingWithBodyChanges(Side.LEFT, baseNode, context) && isRenamingWithoutBodyChanges(Side.RIGHT, baseNode, context)) {

        }

        else if(isDeletionOrRenamingWithBodyChanges(Side.LEFT, baseNode, context) && isDeletionOrRenamingWithBodyChanges(Side.RIGHT, baseNode, context)) {
            if(RenamingUtils.haveEqualSignature(leftNode, rightNode)) {
                // run textual merge
            } else {
                RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode);
            }
        }
    }

    private boolean isRenamingWithoutBodyChanges(Side renamingSide, FSTNode baseNode, MergeContext context) {
        return context.renamedWithoutBodyChanges.stream()
                .anyMatch(triple -> triple.getLeft().equals(renamingSide) && triple.getMiddle().equals(baseNode));
    }

    private boolean isDeletionOrRenamingWithBodyChanges(Side renamingSide, FSTNode baseNode, MergeContext context) {
        return context.deletedOrRenamedWithBodyChanges.stream()
                .anyMatch(triple -> triple.getLeft().equals(renamingSide) && triple.getMiddle().equals(baseNode));
    }
}
