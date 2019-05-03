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
        List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> renamingMatches = retrieveRenamingMatches(context);

        //when both developers rename the same method/constructor
        handleMutualRenamings(context, getMutualRenamingMatches(renamingMatches));

        //when one of the developers rename a method/constructor
        handleSingleRenamings(context, getSingleRenamingMatches(renamingMatches));
    }

    private void handleMutualRenamings(MergeContext context, 
            List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> mutualRenamingMatches) throws TextualMergeException {
        
        for (Quartet<FSTNode, FSTNode, FSTNode, FSTNode> tuple : mutualRenamingMatches)
            mutualRenamingHandler.handle(context, tuple);
    }

    private void handleSingleRenamings(MergeContext context, 
            List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> singleRenamingMatches) throws TextualMergeException {
        
        for (Quartet<FSTNode, FSTNode, FSTNode, FSTNode> tuple : singleRenamingMatches)
            singleRenamingHandler.handle(context, tuple);
    }

    private List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> retrieveRenamingMatches(MergeContext context) {
        List<Pair<Side, FSTNode>> renamedNodes = unionRenamedNodes(context.renamedWithoutBodyChanges, context.deletedOrRenamedWithBodyChanges);
        return renamedNodes.stream()
            .map(pair -> retrieveScenarioNodes(context, pair.getRight()))
            .filter(triple -> triple.getLeft() != null || triple.getRight() != null)
            .map(triple -> retrieveScenarioNodesWithMergeNode(context, triple))
            .distinct()
            .collect(Collectors.toList());
    }

    private List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> getSingleRenamingMatches(List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> renamingMatches) {
        return renamingMatches.stream()
                .filter(quadruple -> isSingleRenaming(quadruple))
                .collect(Collectors.toList());
    }

    private List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> getMutualRenamingMatches(List<Quartet<FSTNode, FSTNode, FSTNode, FSTNode>> renamingMatches) {
        return renamingMatches.stream()
                .filter(quadruple -> isMutualRenaming(quadruple))
                .collect(Collectors.toList());
    }

    private Triple<FSTNode, FSTNode, FSTNode> retrieveScenarioNodes(MergeContext context, FSTNode baseNode) {
        return Triple.of(getMostAccurateMatch(baseNode, context.leftTree), baseNode, getMostAccurateMatch(baseNode, context.rightTree));
    }
    
    private Quartet<FSTNode, FSTNode, FSTNode, FSTNode> retrieveScenarioNodesWithMergeNode(MergeContext context, Triple<FSTNode, FSTNode, FSTNode> scenarioNodes) {
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

    private FSTNode getMostAccurateMatch(FSTNode node, FSTNode contributionTree) {
        for(FSTNode contributionNode : Traverser.collectTerminals(contributionTree)) {
            if(RenamingUtils.isMethodOrConstructorNode(contributionNode) && areVerySimilarNodes(node, contributionNode))
                return contributionNode;
        }
        return null;
    }

    private boolean areVerySimilarNodes(FSTNode node1, FSTNode node2) {
        return  RenamingUtils.haveEqualSignature(node1, node2)
                || RenamingUtils.haveEqualBody(node1, node2)
                || (RenamingUtils.haveSimilarBody(node1, node2) && RenamingUtils.haveEqualSignatureButName(node1, node2))
                || RenamingUtils.oneContainsTheBodyFromTheOther(node1, node2);
    }

    private boolean isSingleRenaming(Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        return !isMutualRenaming(scenarioNodes);
    }

    private boolean isMutualRenaming(Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        return RenamingUtils.haveDifferentSignature(leftNode, baseNode) && RenamingUtils.haveDifferentSignature(rightNode, baseNode);
    }

    private List<Pair<Side, FSTNode>> unionRenamedNodes(List<Pair<Side, FSTNode>> renamedWithoutBodyChanges, 
            List<Pair<Side, FSTNode>> deletedOrRenamedWithBodyChanges) {
        List<Pair<Side, FSTNode>> unionNodes = new ArrayList<>();
        renamedWithoutBodyChanges.stream().forEach(pair -> unionNodes.add(pair));
        deletedOrRenamedWithBodyChanges.stream().forEach(pair -> unionNodes.add(pair));
        return unionNodes;
    }
}
