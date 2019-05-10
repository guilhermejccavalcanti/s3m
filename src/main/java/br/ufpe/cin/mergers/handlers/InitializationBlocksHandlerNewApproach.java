package br.ufpe.cin.mergers.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.TextualMerge;
import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * TODO: fill it
 * @author Alice Borner
 *
 */
public class InitializationBlocksHandlerNewApproach implements ConflictHandler {

	public void handle(MergeContext context) throws TextualMergeException {
        List<FSTNode> leftNodes = findInitializationBlocks(context.addedLeftNodes);
        List<FSTNode> rightNodes = findInitializationBlocks(context.addedRightNodes);
        List<FSTNode> baseNodes = findInitializationBlocks(context.deletedBaseNodes);

    	InitializationBlocksHandlerNodes preProcessedNodes = preProcessNodes(leftNodes, baseNodes, rightNodes);
       
    	for(FSTNode node : baseNodes) {
    		if(preProcessedNodes.rightDeletedNodes.contains(node) || 
    				preProcessedNodes.leftDeletedNodes.contains(node)) {
    			if(preProcessedNodes.rightBaseEditedNodeMap.containsKey(node) ||
    					preProcessedNodes.leftBaseEditedNodeMap.containsKey(node)) {
    				
    	           	String mergeContent = getMergeContent(preProcessedNodes.leftBaseEditedNodeMap.get(node), 
    	           			node,
    	           			preProcessedNodes.rightBaseEditedNodeMap.get(node));
    	           	
    	           	// TODO update AST accordingly
    			}
    		} else if(preProcessedNodes.leftBaseEditedNodeMap.containsKey(node) &&
    				preProcessedNodes.rightBaseEditedNodeMap.containsKey(node)) {
    			
    			String mergeContent = getMergeContent(preProcessedNodes.leftBaseEditedNodeMap.get(node), 
	           			node,
	           			preProcessedNodes.rightBaseEditedNodeMap.get(node));

    			// TODO update AST accordingly

			}
    	}
    }
	
	private String getMergeContent(FSTNode leftNode, FSTNode baseNode, FSTNode rightNode) throws TextualMergeException {
		String leftContent = (leftNode != null) ? ((FSTTerminal) leftNode).getBody() : "";
        String baseContent = (baseNode != null) ? ((FSTTerminal) baseNode).getBody() : "";
        String rightContent = (rightNode != null) ? ((FSTTerminal) rightNode).getBody() : "";
        
		return TextualMerge.merge(leftContent, baseContent, rightContent, 
				JFSTMerge.isWhitespaceIgnored);
	}
    
    private InitializationBlocksHandlerNodes preProcessNodes(List<FSTNode> leftNodes, List<FSTNode> baseNodes,
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
    	
    	preProcessedNodes.leftBaseEditedNodeMap = defineEditedNodes(leftAddedCandidates, leftDeletedCandidates);
    	preProcessedNodes.rightBaseEditedNodeMap = defineEditedNodes(rightAddedCandidates, rightDeletedCandidates);
    	
    	List<FSTNode> leftDeletedNodes = new ArrayList<FSTNode>();
    	leftDeletedNodes.addAll(leftDeletedCandidates);
    	leftDeletedNodes.removeAll(preProcessedNodes.leftBaseEditedNodeMap.keySet());
    	preProcessedNodes.leftDeletedNodes.addAll(leftDeletedNodes);
    	
    	List<FSTNode> rightDeletedNodes = new ArrayList<FSTNode>();
    	rightDeletedNodes.addAll(rightDeletedCandidates);
    	rightDeletedNodes.removeAll(preProcessedNodes.rightBaseEditedNodeMap.keySet());
    	preProcessedNodes.rightDeletedNodes.addAll(rightDeletedNodes);

    	return preProcessedNodes;
    }
  
    private static List<FSTNode> findInitializationBlocks(List<FSTNode> nodes) {
        return nodes.stream()
                .filter(p -> p.getType().equals("InitializerDecl"))
                .collect(Collectors.toList());
    }
    
    private static Pair<FSTNode, Double> maxInsertionLevel(FSTNode node, List<FSTNode> nodes) {
    	Map<FSTNode, Double> nodesInsertionLevelMap = new HashMap<>();
    	String nodeBody = ((FSTTerminal) node).getBody();
    	String nodeLines = StringUtils.substringBetween(nodeBody, "{", "}");
    	List<String> splitNodeContent = Arrays.asList(nodeLines.split(";"));

    	for(FSTNode pairNode : nodes) {
        	String pairNodeBody = ((FSTTerminal) pairNode).getBody();
        	String pairNodeLines = StringUtils.substringBetween(pairNodeBody, "{", "}");
        	List<String> splitPairNodeContent = Arrays.asList(pairNodeLines.split(";"));
        	
        	double numOfInsertions = 0;
        	for(String content : splitNodeContent) {
        		for(String pairContent : splitPairNodeContent) {
        			if(!content.trim().isEmpty() && !pairContent.trim().isEmpty()
        					&& content.trim().equals(pairContent.trim()))
        				numOfInsertions++;
        		}
        	}
        	
        	double insertionLevel = numOfInsertions / splitNodeContent.size();
        	
        	nodesInsertionLevelMap.put(pairNode, insertionLevel);
    	}
    	
    	FSTNode nodeMaxValue = getNodeWithHighestValue(nodesInsertionLevelMap);
    	return Pair.of(nodeMaxValue, nodesInsertionLevelMap.get(nodeMaxValue));
    }
    
    private static Pair<FSTNode, Double> maxSimilarity(FSTNode node, List<FSTNode> nodes) {
    	String nodeContent = ((FSTTerminal) node).getBody();
    	Map<FSTNode, Double> nodesSimilarityLevelMap = new HashMap<>();
    	
    	for(FSTNode pairNode : nodes) {
        	String pairNodeContent = ((FSTTerminal) pairNode).getBody();
        	double similarity = FilesManager.computeStringSimilarity(nodeContent, pairNodeContent);
        	nodesSimilarityLevelMap.put(pairNode, similarity);
    	}
    	
    	FSTNode nodeMaxValue = getNodeWithHighestValue(nodesSimilarityLevelMap);
    	return Pair.of(nodeMaxValue, nodesSimilarityLevelMap.get(nodeMaxValue));
    }
    
    private static FSTNode getNodeWithHighestValue(Map<FSTNode, Double> nodesMap) {
    	if(nodesMap.entrySet().isEmpty())
    		return null;
    	
    	return Collections.max(nodesMap.entrySet(), Comparator.comparingDouble(Map.Entry::getValue))
    			.getKey();
    }
    
    private Map<FSTNode, FSTNode> defineEditedNodes(List<FSTNode> addedCandidates, List<FSTNode> deletedCandidates) {
    	Map<FSTNode, FSTNode> editedNodes = new HashMap<FSTNode, FSTNode>();
    	
    	for(FSTNode node : addedCandidates) {
    		Pair<FSTNode, Double> maxInsertionPair = maxInsertionLevel(node, deletedCandidates);
    		Pair<FSTNode, Double> maxSimilarityPair = maxSimilarity(node, deletedCandidates);
    		FSTNode baseNode = maxInsertionPair.getKey() != null ? maxInsertionPair.getKey() : maxSimilarityPair.getKey();
    		
    		// TODO: TEST TO SELECT BEST THRESHOLDS!
    		if(baseNode != null && (maxInsertionPair.getValue() > 0.7 || maxSimilarityPair.getValue() > 0.9)) {
    			editedNodes.put(baseNode, node);
    		} 
    	}
    	
    	return editedNodes;
    	
    	// TODO probably after selecting the edited ones that are not additions I will have to update the AST to remove them
    }
}

class InitializationBlocksHandlerNodes {
    protected Map<FSTNode, FSTNode> rightBaseEditedNodeMap = new HashMap<FSTNode, FSTNode>();
    protected List<FSTNode> rightDeletedNodes = new ArrayList<FSTNode>();
	
    protected Map<FSTNode, FSTNode> leftBaseEditedNodeMap = new HashMap<FSTNode, FSTNode>();
    protected List<FSTNode> leftDeletedNodes = new ArrayList<FSTNode>();
}
