package br.ufpe.cin.mergers.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.internal.bytebuddy.dynamic.scaffold.InstrumentedType.Prepareable;
import org.assertj.core.util.Arrays;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.TextualMerge;
import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class InitializationBlocksHandlerNewApproach implements ConflictHandler {

	// TODO: improve this name and maybe structure!!

	public void handle(MergeContext context) throws TextualMergeException {
        List<FSTNode> leftNodes = findInitializationBlocks(context.addedLeftNodes);
        List<FSTNode> rightNodes = findInitializationBlocks(context.addedRightNodes);
        List<FSTNode> baseNodes = findInitializationBlocks(context.deletedBaseNodes);

    	InitializationBlocksHandlerNodes preProcessedNodes = preProcessNodes(leftNodes, baseNodes, rightNodes);
        
    	List<FSTNode> finalNodes = new ArrayList<FSTNode>();

    	for(FSTNode node : baseNodes) {
    		if(preProcessedNodes.leftDeletedNodes.contains(node) || preProcessedNodes.rightDeletedNodes.contains(node)) {
    			if(preProcessedNodes.leftEditedNodes.contains(node) || preProcessedNodes.rightEditedNodes.contains(node)) {
    				// TODO: check how to get the content from the edition
    				// finalNodes.add(TextualMerge.merge(leftContent, baseContent, rightContent, JFSTMerge.isWhitespaceIgnored));
    			}
    		} else if(preProcessedNodes.leftEditedNodes.contains(node) &&
    				!preProcessedNodes.rightEditedNodes.contains(node)) {
    			finalNodes.add(node);
    		} else if(preProcessedNodes.rightEditedNodes.contains(node) &&
    				!preProcessedNodes.leftEditedNodes.contains(node)) {
    			finalNodes.add(node);
    		} else if(preProcessedNodes.rightEditedNodes.contains(node) &&
    				preProcessedNodes.leftEditedNodes.contains(node)) {
				//finalNodes.add(TextualMerge.merge(leftContent, baseContent, rightContent, ignoreWhiteSpaces));
    		}
    	}
    	
    	finalNodes.addAll(preProcessedNodes.leftAddedNodes);
    	finalNodes.addAll(preProcessedNodes.rightAddedNodes);
    	
    	// TODO: CHECK HOW TO WRITE THE CONTENT IN THE AST IN THE END!!! check methods in FilesManager class
    }
    
    private static InitializationBlocksHandlerNodes preProcessNodes(List<FSTNode> leftNodes, List<FSTNode> baseNodes,
    		List<FSTNode> rightNodes) {
    	
    	InitializationBlocksHandlerNodes preProcessedNodes = new InitializationBlocksHandlerNodes();
    	
    	// Finding deleted candidates
    	List<FSTNode> leftDeletedCandidates = new ArrayList<FSTNode>();
    	leftDeletedCandidates.addAll(baseNodes);
    	leftDeletedCandidates.removeAll(leftNodes);

    	List<FSTNode> rightDeletedCandidates = new ArrayList<FSTNode>();
    	rightDeletedCandidates.addAll(baseNodes);
    	rightDeletedCandidates.removeAll(rightNodes);
    	
    	// Finding added candidates
    	List<FSTNode> leftAddedCandidates = new ArrayList<FSTNode>();
    	leftAddedCandidates.addAll(leftNodes);
    	leftAddedCandidates.removeAll(baseNodes);
    	
    	List<FSTNode> rightAddedCandidates = new ArrayList<FSTNode>();
    	rightAddedCandidates.addAll(rightNodes);
    	rightAddedCandidates.removeAll(baseNodes);
    	
    	separateNodesIntoEditedOrAdded(leftAddedCandidates, leftDeletedCandidates, preProcessedNodes.leftEditedNodes,
    			preProcessedNodes.leftAddedNodes);
    	separateNodesIntoEditedOrAdded(rightAddedCandidates, rightDeletedCandidates, preProcessedNodes.rightEditedNodes,
    			preProcessedNodes.rightAddedNodes);
    	
    	
    	// FIXME: improve this
    	List<FSTNode> leftDeletedNodes = new ArrayList<FSTNode>();
    	leftDeletedNodes.addAll(leftDeletedCandidates);
    	leftDeletedNodes.removeAll(preProcessedNodes.leftEditedNodes);
    	preProcessedNodes.leftDeletedNodes.addAll(leftDeletedNodes);
    	
    	List<FSTNode> rightDeletedNodes = new ArrayList<FSTNode>();
    	rightDeletedNodes.addAll(rightDeletedCandidates);
    	rightDeletedNodes.removeAll(preProcessedNodes.rightEditedNodes);
    	preProcessedNodes.rightDeletedNodes.addAll(rightDeletedNodes);
    	
    	return preProcessedNodes;
    }
  
    private static List<FSTNode> findInitializationBlocks(List<FSTNode> nodes) {
        return nodes.stream()
                .filter(p -> p.getType().equals("InitializerDecl"))
                .collect(Collectors.toList());
    }
    
    private static Pair<FSTNode, Double> maxInsertionLevel(FSTNode node, List<FSTNode> nodes) {
    	List<Pair<FSTNode, Double>> nodesList = new ArrayList<>();
    	String nodeContent = ((FSTTerminal) node).getBody();
    	List<Object> splitNodeContent = Arrays.asList(nodeContent.split(";"));

    	for(FSTNode pairNode : nodes) {
        	String pairNodeContent = ((FSTTerminal) pairNode).getBody();
        	List<Object> splitPairNodeContent = Arrays.asList(pairNodeContent.split(";"));
        	double insertionLevel = 0;
        	for(Object content : splitNodeContent) {
        		for(Object pairContent : splitPairNodeContent) {
        			if(pairContent.equals(content)) {
        				insertionLevel++;
        			}
        		}
        	}
        	
        	nodesList.add(Pair.of(pairNode, insertionLevel));
    	}
    	
    	// TODO: return pair with highest insertion level
		return null;
    	
    }
    
    private static Pair<FSTNode, Double> maxSimilarity(FSTNode node, List<FSTNode> nodes) {
    	String nodeContent = ((FSTTerminal) node).getBody();
    	List<Pair<FSTNode, Double>> nodesList = new ArrayList<>();
    	
    	for(FSTNode pairNode : nodes) {
        	String pairNodeContent = ((FSTTerminal) pairNode).getBody();
        	double similarity = FilesManager.computeStringSimilarity(nodeContent, pairNodeContent);
        	nodesList.add(Pair.of(pairNode, similarity));
    	}
    	
    	// TODO: return pair with highest similarity
		return null;
    }
    
    // FIXME: fix return type to return the lists of edited and added nodes
    private static void separateNodesIntoEditedOrAdded(List<FSTNode> addedCandidates, List<FSTNode> deletedCandidates,
    		List<FSTNode> editedNodes, List<FSTNode> addedNodes) {

    	for(FSTNode node : addedCandidates) {
    		Pair<FSTNode, Double> maxInsertionPair = maxInsertionLevel(node, deletedCandidates);
    		Pair<FSTNode, Double> maxSimilarityPair = maxSimilarity(node, deletedCandidates);
    		
    		// TODO: TEST TO SELECT CORRECT THRESHOLDS!
    		if(maxInsertionPair.getValue() > 70.0 || maxSimilarityPair.getValue() > 90.0) {
    			editedNodes.add(node);
    		} else {
    			addedNodes.add(node);
    		}
    	}
    }
}

// TODO: think the best way to store these lists
class InitializationBlocksHandlerNodes {
    protected List<FSTNode> rightEditedNodes = new ArrayList<FSTNode>();
	protected List<FSTNode> rightAddedNodes = new ArrayList<FSTNode>();
	protected List<FSTNode> rightDeletedNodes = new ArrayList<FSTNode>();
	
	protected List<FSTNode> leftEditedNodes = new ArrayList<FSTNode>();
	protected List<FSTNode> leftAddedNodes = new ArrayList<FSTNode>();
	protected List<FSTNode> leftDeletedNodes = new ArrayList<FSTNode>();
}
