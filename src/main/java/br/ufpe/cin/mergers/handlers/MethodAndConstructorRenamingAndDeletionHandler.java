package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming.MutualRenamingHandler;
import br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming.MutualRenamingHandlerFactory;
import br.ufpe.cin.mergers.handlers.singlerenaming.SingleRenamingHandler;
import br.ufpe.cin.mergers.handlers.singlerenaming.SingleRenamingHandlerFactory;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import br.ufpe.cin.mergers.util.Traverser;
import de.ovgu.cide.fstgen.ast.FSTNode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.javatuples.Quartet;

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

    @Override
    public void handle(MergeContext context) throws TextualMergeException {
        List<Pair<Side, FSTNode>> allRenamedNodes = unionRenamedNodes(context.renamedWithoutBodyChanges,
                context.deletedOrRenamedWithBodyChanges);

        //when both developers rename the same method/constructor
        handleMutualRenamings(context, allRenamedNodes);

        //when one of the developers rename a method/constructor
        handleSingleRenamings(context, allRenamedNodes);
    }

    private void handleMutualRenamings(MergeContext context, List<Pair<Side, FSTNode>> allRenamedNodes) throws TextualMergeException {
        List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> mutualRenamedNodes = getMutualRenamingMatches(context, allRenamedNodes);
        for (Quartet<FSTNode, FSTNode, FSTNode, FSTNode> tuple : mutualRenamedNodes)
            mutualRenamingHandler.handle(context, tuple);
    }

    private void handleSingleRenamings(MergeContext context, List<Pair<Side, FSTNode>> allRenamedNodes) throws TextualMergeException {
        if (context.possibleRenamedLeftNodes.isEmpty() && context.possibleRenamedRightNodes.isEmpty()) return;

        //possible renamings or deletions in left
        handleSingleRenamings(context, context.possibleRenamedLeftNodes, context.addedLeftNodes, allRenamedNodes, Side.LEFT);

        //possible renamings or deletions in right
        handleSingleRenamings(context, context.possibleRenamedRightNodes, context.addedRightNodes, allRenamedNodes, Side.RIGHT);
    }

    private void handleSingleRenamings(MergeContext context, List<Pair<String, FSTNode>> possibleRenamedNodes,
                                       List<FSTNode> addedNodes, List<Pair<Side, FSTNode>> allRenamedNodes, Side renamingSide) throws TextualMergeException {

        List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> singleRenamingMatches = getSingleRenamingMatches(renamingSide, context, allRenamedNodes);
        for (Quartet<FSTNode, FSTNode, FSTNode, FSTNode> tuple : singleRenamingMatches)
            singleRenamingHandler.handle(context, tuple, renamingSide);
    }

    private List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> getSingleRenamingMatches(Side renamingSide, MergeContext context,
            List<Pair<Side, FSTNode>> allRenamedNodes) {
        return allRenamedNodes.stream()
                .filter(pair -> isSingleRenamingNode(pair.getLeft(), pair.getRight(), allRenamedNodes))
                .map(pair -> retrieveScenarioNodes(renamingSide, context, pair.getRight()))
                .filter(triple -> triple.getLeft() != null || triple.getRight() != null)
                .map(triple -> retrieveMergeNode(context, triple))
                .collect(Collectors.toList());
    }

    private List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> getMutualRenamingMatches(MergeContext context, 
            List<Pair<Side, FSTNode>> allRenamedNodes) {
        return allRenamedNodes.stream()
                .filter(pair -> isMutualRenamingNode(pair.getLeft(), pair.getRight(), allRenamedNodes))
                .map(pair -> retrieveScenarioNodes(context, pair.getRight()))
                .filter(triple -> triple.getLeft() != null || triple.getRight() != null)
                .map(triple -> retrieveMergeNode(context, triple))
                .distinct()
                .collect(Collectors.toList());
    }

    private Triple<FSTNode, FSTNode, FSTNode> retrieveScenarioNodes(Side renamingSide, MergeContext context, FSTNode baseNode) {
        if(renamingSide.equals(Side.LEFT))
            return Triple.of(getMostAccurateMatch(baseNode, context.addedLeftNodes), baseNode, getCorrespondentNode(baseNode, context.rightTree));
        else
            return Triple.of(getCorrespondentNode(baseNode, context.leftTree), baseNode, getMostAccurateMatch(baseNode, context.addedRightNodes));
    }

    private Triple<FSTNode, FSTNode, FSTNode> retrieveScenarioNodes(MergeContext context, FSTNode baseNode) {
        return Triple.of(getMostAccurateMatch(baseNode, context.addedLeftNodes), baseNode, getMostAccurateMatch(baseNode, context.addedRightNodes));
    }
    
    private Quartet<FSTNode, FSTNode, FSTNode, FSTNode> retrieveMergeNode(MergeContext context, Triple<FSTNode, FSTNode, FSTNode> scenarioNodes) {
        FSTNode leftNode = scenarioNodes.getLeft();
        FSTNode baseNode = scenarioNodes.getMiddle();
        FSTNode rightNode = scenarioNodes.getRight();

        if(leftNode != null)
            return Quartet.with(leftNode, baseNode, rightNode, getCorrespondentNode(leftNode, context.superImposedTree));
        else
            return Quartet.with(leftNode, baseNode, rightNode, getCorrespondentNode(rightNode, context.superImposedTree));
    }

    private FSTNode getCorrespondentNode(FSTNode node, FSTNode tree) {
        return Traverser.retrieveNodeFromTree(node, tree);
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

    private boolean isSingleRenamingNode(Side contribution, FSTNode node, 
            List<Pair<Side, FSTNode>> allRenamedNodes) {
        return !isMutualRenamingNode(contribution, node, allRenamedNodes);
    }

    private boolean isMutualRenamingNode(Side contribution, FSTNode node, 
            List<Pair<Side, FSTNode>> allRenamedNodes) {
        return allRenamedNodes.stream().anyMatch(pair -> isOppositeContributionSameNode(contribution, node, pair));
    }

    private boolean isOppositeContributionSameNode(Side contribution, FSTNode node, Pair<Side, FSTNode> pair) {
        return pair.getLeft().equals(contribution.opposite()) && pair.getRight().equals(node); 
    }

    private List<Pair<Side, FSTNode>> unionRenamedNodes(List<Pair<Side, FSTNode>> renamedWithoutBodyChanges, 
            List<Pair<Side, FSTNode>> deletedOrRenamedWithoutBodyChanges) {
        List<Pair<Side, FSTNode>> unionNodes = new ArrayList<>();
        renamedWithoutBodyChanges.stream().forEach(pair -> unionNodes.add(pair));
        deletedOrRenamedWithoutBodyChanges.stream().forEach(pair -> unionNodes.add(pair));
        return unionNodes;
    }
}
