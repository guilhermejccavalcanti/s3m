package br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming;

import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import de.ovgu.cide.fstgen.ast.FSTNode;

import java.util.List;

public class DefaultMutualRenamingHandler implements MutualRenamingHandler {
    public void handle(MergeContext context) {
        if (context.addedLeftNodes.isEmpty() || context.addedRightNodes.isEmpty()) return;

        List<FSTNode> leftNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedLeftNodes);
        List<FSTNode> rightNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedRightNodes);
        for (FSTNode left : leftNewMethodsOrConstructors) {
            for (FSTNode right : rightNewMethodsOrConstructors) {
                if (RenamingUtils.haveSameParent(left, right)
                        && RenamingUtils.haveDifferentSignature(left, right)
                        && RenamingUtils.haveSameBody(left, right)) {
                    RenamingUtils.generateMutualRenamingConflict(context, left, right);
                    break;
                }
            }
        }
    }
}
