package br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming;

import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import de.ovgu.cide.fstgen.ast.FSTNode;

import java.util.List;

public class DefaultMutualRenamingHandler implements MutualRenamingHandler {
    public void handle(MergeContext context) {
        List<FSTNode> mutualRemovedMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.deletedBaseNodes);
        List<FSTNode> leftNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedLeftNodes);
        List<FSTNode> rightNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedRightNodes);

        if (mutualRemovedMethodsOrConstructors.isEmpty()) return;
        if (leftNewMethodsOrConstructors.isEmpty() || rightNewMethodsOrConstructors.isEmpty()) return;

        for (FSTNode left : leftNewMethodsOrConstructors) {
            FSTNode sameNode = rightNewMethodsOrConstructors.stream()
                    .filter(right -> right.equals(left))
                    .filter(right -> RenamingUtils.haveSameBody(left, right))
                    .filter(right -> RenamingUtils.haveSameParent(left, right))
                    .findAny()
                    .orElse(null);

            if (sameNode != null) {
                rightNewMethodsOrConstructors.remove(sameNode);
                continue;
            }

            for (FSTNode right : rightNewMethodsOrConstructors) {
                if (RenamingUtils.haveSameParent(left, right)
                        && RenamingUtils.haveSameBody(left, right)
                        && RenamingUtils.haveDifferentSignature(left, right)) {
                    RenamingUtils.generateMutualRenamingConflict(context, left, right);
                    break;
                }
            }
        }
    }
}
