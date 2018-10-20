package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.handlers.singlerenaming.SingleRenamingHandler;
import br.ufpe.cin.mergers.handlers.singlerenaming.SingleRenamingHandlerFactory;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingStrategy;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Renaming or deletions conflicts happen when one developer edits a element renamed or deleted by other.
 * Semistructured merge is unable to detect such cases because it matches elements via its identifier, so
 * if a element is renamed or deleted it cannot match the elements anymore. This class overcomes this issue.
 *
 * @author Guilherme
 */
public final class MethodAndConstructorRenamingAndDeletionHandler implements ConflictHandler {
    private static final double BODY_SIMILARITY_THRESHOLD = 0.7;  //a typical value of 0.7 (up to 1.0) is used, increase it for a more accurate comparison, or decrease for a more relaxed one.

    public void handle(MergeContext context) {
        //when both developers rename the same method/constructor
        handleMutualRenamings(context);

        //when one of the developers rename a method/constructor
        handleSingleRenamings(context);
    }

    private static void handleMutualRenamings(MergeContext context) {
        if (context.addedLeftNodes.isEmpty() || context.addedRightNodes.isEmpty()) return;
        if (JFSTMerge.renamingStrategy == RenamingStrategy.KEEP_BOTH_METHODS) return;

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

    private static void handleSingleRenamings(MergeContext context) {
        if (context.possibleRenamedLeftNodes.isEmpty() && context.possibleRenamedRightNodes.isEmpty()) return;

        //possible renamings or deletions in left
        handleSingleRenamings(context, context.possibleRenamedLeftNodes, context.addedLeftNodes, Side.LEFT);

        //possible renamings or deletions in right
        handleSingleRenamings(context, context.possibleRenamedRightNodes, context.addedRightNodes, Side.RIGHT);
    }

    private static void handleSingleRenamings(MergeContext context, List<Pair<String, FSTNode>> possibleRenamedNodes,
                                              List<FSTNode> addedNodes, Side renamingSide) {
        SingleRenamingHandler handler = SingleRenamingHandlerFactory.getHandler(JFSTMerge.renamingStrategy);

        for (Pair<String, FSTNode> tuple : possibleRenamedNodes) {
            String baseContent = tuple.getLeft();
            FSTNode currentNode = tuple.getRight();
            if (!RenamingUtils.nodeHasConflict(currentNode)) continue;

            List<Pair<Double, String>> similarNodes = RenamingUtils.getSimilarNodes(baseContent, currentNode, addedNodes,
                    BODY_SIMILARITY_THRESHOLD);

            handler.handle(context, baseContent, currentNode, similarNodes, renamingSide);
        }
    }
}
