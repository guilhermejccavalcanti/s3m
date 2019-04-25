package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming.MutualRenamingHandler;
import br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming.MutualRenamingHandlerFactory;
import br.ufpe.cin.mergers.handlers.singlerenaming.SingleRenamingHandler;
import br.ufpe.cin.mergers.handlers.singlerenaming.SingleRenamingHandlerFactory;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Renaming or deletions conflicts happen when one developer edits a element renamed or deleted by other.
 * Semistructured merge is unable to detect such cases because it matches elements via its identifier, so
 * if a element is renamed or deleted it cannot match the elements anymore. This class overcomes this issue.
 *
 * @author Guilherme
 */
public final class MethodAndConstructorRenamingAndDeletionHandler implements ConflictHandler {
    private SingleRenamingHandler singleRenamingHandler;
    private MutualRenamingHandler mutualRenamingHandler;

    public MethodAndConstructorRenamingAndDeletionHandler() {
        this.singleRenamingHandler = SingleRenamingHandlerFactory.getHandler(JFSTMerge.renamingStrategy);
        this.mutualRenamingHandler = MutualRenamingHandlerFactory.getHandler(JFSTMerge.renamingStrategy);
    }

    public void handle(MergeContext context) {
        List<Pair<Side, FSTNode>> allRenamedNodes = unionRenamedNodes(context.renamedWithoutBodyChanges,
                context.deletedOrRenamedWithBodyChanges);

        //when both developers rename the same method/constructor
        handleMutualRenamings(context, allRenamedNodes);

        //when one of the developers rename a method/constructor
        handleSingleRenamings(context, allRenamedNodes);
    }

    private void handleMutualRenamings(MergeContext context, List<Pair<Side, FSTNode>> allRenamedNodes) {
        List<Triple<FSTNode, FSTNode, FSTNode>> mutualRenamedNodes = getMutualRenamingMatches(context, allRenamedNodes);
        mutualRenamingHandler.handle(context);
    }

    private void handleSingleRenamings(MergeContext context, List<Pair<Side, FSTNode>> allRenamedNodes) {
        if (context.possibleRenamedLeftNodes.isEmpty() && context.possibleRenamedRightNodes.isEmpty()) return;

        //possible renamings or deletions in left
        handleSingleRenamings(context, context.possibleRenamedLeftNodes, context.addedLeftNodes, Side.LEFT);

        //possible renamings or deletions in right
        handleSingleRenamings(context, context.possibleRenamedRightNodes, context.addedRightNodes, Side.RIGHT);
    }

    private void handleSingleRenamings(MergeContext context, List<Pair<String, FSTNode>> possibleRenamedNodes,
                                       List<FSTNode> addedNodes, Side renamingSide) {
        for (Pair<String, FSTNode> tuple : possibleRenamedNodes) {
            String baseContent = tuple.getLeft();
            FSTNode currentNode = tuple.getRight();

            if (RenamingUtils.nodeHasConflict(currentNode)) {
                singleRenamingHandler.handle(context, baseContent, currentNode, addedNodes, renamingSide);
            }
        }
    }

    private List<Triple<FSTNode, FSTNode, FSTNode>> getMutualRenamingMatches(MergeContext context, List<Pair<Side, FSTNode>> allRenamedNodes) {
        return allRenamedNodes.stream()
            .filter(pair -> isMutualRenamingNode(pair.getLeft(), pair.getRight(), allRenamedNodes))
            .map(Pair::getRight)
            .map(node -> Triple.of(getMostAccurateMatch(node, context.addedLeftNodes), node, getMostAccurateMatch(node, context.addedRightNodes)))
            .collect(Collectors.toList());
    }

    private FSTNode getMostAccurateMatch(FSTNode node, List<FSTNode> addedNodes) {
        for(FSTNode addedNode : addedNodes) {
            if(RenamingUtils.isMethodOrConstructorNode(addedNode) && areVerySimilarNodes(node, addedNode))
                return addedNode;
        }
        return null;
    }

    private boolean areVerySimilarNodes(FSTNode node1, FSTNode node2) {
        return RenamingUtils.haveEqualBody(node1, node2)
                || (RenamingUtils.haveSimilarBody(node1, node2) && RenamingUtils.haveEqualSignatureButName(node1, node2))
                || RenamingUtils.oneContainsTheBodyFromTheOther(node1, node2);
    }

    private boolean isMutualRenamingNode(Side contribution, FSTNode node, List<Pair<Side, FSTNode>> allRenamedNodes) {
        return allRenamedNodes.stream().anyMatch(pair -> isOppositeContributionSameNode(contribution, node, pair));
    }

    private boolean isOppositeContributionSameNode(Side contribution, FSTNode node, Pair<Side, FSTNode> pair) {
        return pair.getLeft().equals(contribution.opposite()) && pair.getRight().equals(node); 
    }

    private List<Pair<Side, FSTNode>> unionRenamedNodes(List<Pair<Side, FSTNode>> renamedWithoutBodyChanges, List<Pair<Side, FSTNode>> deletedOrRenamedWithoutBodyChanges) {
        List<Pair<Side, FSTNode>> unionNodes = new ArrayList<>();
        renamedWithoutBodyChanges.stream().forEach(pair -> unionNodes.add(pair));
        deletedOrRenamedWithoutBodyChanges.stream().forEach(pair -> unionNodes.add(pair));
        return unionNodes;
    }
}
