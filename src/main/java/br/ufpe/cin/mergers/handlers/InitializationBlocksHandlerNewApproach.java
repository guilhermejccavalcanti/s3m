package br.ufpe.cin.mergers.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    private List<InitializationBlocksHandlerNode> rightEditedNodes = new ArrayList<InitializationBlocksHandlerNode>();
	private List<FSTNode> rightDeletedNodes = new ArrayList<FSTNode>();
	
    private List<InitializationBlocksHandlerNode> leftEditedNodes =  new ArrayList<InitializationBlocksHandlerNode>();
    private List<FSTNode> leftDeletedNodes = new ArrayList<FSTNode>();
    
    private final static String LINE_BREAKER = "\\r?\\n(\\t)*";	
    private final static String INITIALIZATION_BLOCK_IDENTIFIER = "InitializerDecl";	
    private final static String TEMPORARY_STATIC_NEW_BLOCK = "\nstatic {\n";
    private final static String BLOCK_DELIMITER = "}";
    
	// TODO add comments
	public void handle(MergeContext context) throws TextualMergeException {
		
        List<FSTNode> leftNodes = findInitializationBlocks(context.addedLeftNodes);
        List<FSTNode> rightNodes = findInitializationBlocks(context.addedRightNodes);
        List<FSTNode> baseNodes = findInitializationBlocks(context.deletedBaseNodes);

    	preProcessNodes(leftNodes, baseNodes, rightNodes);
       
    	for(FSTNode node : baseNodes) {
    		mergeContentAndUpdateAST(node, context);
    	}
    }

	private void mergeContentAndUpdateAST(FSTNode baseNode, MergeContext context) 
			throws TextualMergeException {
		
		InitializationBlocksHandlerNode leftNode = getEditedNodeByBaseNode(leftEditedNodes, baseNode);
		InitializationBlocksHandlerNode rightNode = getEditedNodeByBaseNode(rightEditedNodes, baseNode);
	
		String baseContent = (baseNode != null) ? ((FSTTerminal) baseNode).getBody() : "";
		String leftContent = (leftNode != null) ? ((FSTTerminal) leftNode.getPairNode()).getBody() : "";
		String rightContent = (rightNode != null) ? ((FSTTerminal) rightNode.getPairNode()).getBody() : "";

		String mergedContent = null;
		
		// both branches edited the node
		if(leftNode != null && rightNode != null) {
			
		    mergedContent = TextualMerge.merge(leftContent, baseContent, rightContent, 
					JFSTMerge.isWhitespaceIgnored);
			
			if(leftNode.isEditedNode() || rightNode.isEditedNode()) {
				mergedContent = mergedContent.replace(BLOCK_DELIMITER + TEMPORARY_STATIC_NEW_BLOCK, "");
				String originalContent = leftNode.getOriginalNodeContent() != null ? leftNode.getOriginalNodeContent() :
					rightNode.getOriginalNodeContent();
				FilesManager.findAndDeleteASTNode(context.superImposedTree, originalContent);
			}
			
            FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, leftContent, mergedContent);
            FilesManager.findAndDeleteASTNode(context.superImposedTree, rightContent);
            
        } else if(leftNode == null && rightNode == null) {
            FilesManager.findAndDeleteASTNode(context.superImposedTree, baseContent);
		} else {
			if (leftNode == null) {
	    		leftContent = (baseNode != null) ? ((FSTTerminal) baseNode).getBody() : "";
	    		if(leftDeletedNodes.contains(baseNode)) {
	                FilesManager.findAndDeleteASTNode(context.superImposedTree, baseContent);
	        		leftContent = "";
	        	}
	    		
	    		mergedContent = TextualMerge.merge(leftContent, baseContent, rightContent, 
	    				JFSTMerge.isWhitespaceIgnored);
	    		
				if(rightNode != null && rightNode.isEditedNode()) {
					mergedContent = mergedContent.replace(BLOCK_DELIMITER + TEMPORARY_STATIC_NEW_BLOCK, "");
				}
				
	            FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, rightContent, mergedContent);
	            FilesManager.findAndDeleteASTNode(context.superImposedTree, leftContent);
	
			}
			
			if (rightNode == null) {    		
				rightContent = (baseNode != null) ? ((FSTTerminal) baseNode).getBody() : "";
				
				if(rightDeletedNodes.contains(baseNode)) {
					FilesManager.findAndDeleteASTNode(context.superImposedTree, baseContent);
					rightContent = "";
				}
				
				mergedContent = TextualMerge.merge(leftContent, baseContent, rightContent, 
						JFSTMerge.isWhitespaceIgnored);
				
				if(leftNode != null && leftNode.isEditedNode()) {
					mergedContent = mergedContent.replace(BLOCK_DELIMITER + TEMPORARY_STATIC_NEW_BLOCK, "");
				}
				
				FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, leftContent, mergedContent);
				FilesManager.findAndDeleteASTNode(context.superImposedTree, rightContent);
			}
        }
		
		// statistics
        if (mergedContent != null && mergedContent.contains("<<<<<<<")) //has conflict
             context.initializationBlocksConflicts++;
	}

	private InitializationBlocksHandlerNode getEditedNodeByBaseNode(List<InitializationBlocksHandlerNode> nodesList,
			FSTNode baseNode) {
		for(InitializationBlocksHandlerNode node : nodesList) {
			if(node.getBaseNode().equals(baseNode)) {
				return node;
			}
		}
		
		return null;
	}
    
    private void preProcessNodes(List<FSTNode> leftNodes, List<FSTNode> baseNodes, List<FSTNode> rightNodes) {
    	
    	// Finding deleted candidates
    	List<FSTNode> leftDeletedCandidates = removeContainedNodesFromList(baseNodes, leftNodes);
    	List<FSTNode> rightDeletedCandidates = removeContainedNodesFromList(baseNodes, rightNodes);

    	// Finding added candidates
    	List<FSTNode> leftAddedCandidates = removeContainedNodesFromList(leftNodes, baseNodes);
    	List<FSTNode> rightAddedCandidates = removeContainedNodesFromList(rightNodes, baseNodes);
    	
    	setLeftEditedNodes(defineEditedNodes(leftAddedCandidates, leftDeletedCandidates));
    	setRightEditedNodes(defineEditedNodes(rightAddedCandidates, rightDeletedCandidates));
    	
    	setLeftDeletedNodes(removeContainedNodesFromList(leftDeletedCandidates, getBaseNodes(leftEditedNodes)));
    	setRightDeletedNodes(removeContainedNodesFromList(rightDeletedCandidates, getBaseNodes(rightEditedNodes)));
    }
    
    private List<FSTNode> getBaseNodes(List<InitializationBlocksHandlerNode> initializationHandlerNodes) {
    	return initializationHandlerNodes.stream().collect(Collectors.mapping(InitializationBlocksHandlerNode::getBaseNode,
				Collectors.toList()));
    }

    private List<FSTNode> removeContainedNodesFromList(Collection<FSTNode> nodesList, 
    		Collection<FSTNode> nodesToCheck) {
    	List<FSTNode> resultNodesList = new ArrayList<FSTNode>();
    	
    	for(FSTNode branchNode : nodesList) {
    		if(!containsNode(nodesToCheck, branchNode)) {
				resultNodesList.add(branchNode);
    		}
    	}
    	
    	return resultNodesList;
    }
    
    private boolean containsNode(Collection<FSTNode> nodes, FSTNode node) {
    	for(FSTNode listNode : nodes) {
    		String listNodeContent = (listNode != null) ? ((FSTTerminal) listNode).getBody() : "";
            String nodeContent = (node != null) ? ((FSTTerminal) node).getBody() : "";
            
            if(listNodeContent.equals(nodeContent))
            	return true;
    	}
    	
    	return false;
    }
  
    private static List<FSTNode> findInitializationBlocks(List<FSTNode> nodes) {
        return nodes.stream()
                .filter(p -> p.getType().equals(INITIALIZATION_BLOCK_IDENTIFIER))
                .collect(Collectors.toList());
    }
    
    private static Pair<FSTNode, Double> maxInsertionLevel(FSTNode node, List<FSTNode> nodes) {
    	Map<FSTNode, Double> nodesInsertionLevelMap = new HashMap<>();
    	String nodeBody = ((FSTTerminal) node).getBody();
    	String nodeLines = StringUtils.substringBetween(nodeBody, "{", "}").trim();
    	List<String> splitNodeContent = Arrays.asList(nodeLines.split(LINE_BREAKER));

    	for(FSTNode pairNode : nodes) {
        	String pairNodeBody = ((FSTTerminal) pairNode).getBody();
        	String pairNodeLines = StringUtils.substringBetween(pairNodeBody, "{", "}").trim();
        	List<String> splitPairNodeContent = Arrays.asList(pairNodeLines.split(LINE_BREAKER));
        	
        	double numOfInsertions = 0;
        	for(String content : splitNodeContent) {
        		for(String pairContent : splitPairNodeContent) {
        			if(!content.trim().isEmpty() && !pairContent.trim().isEmpty()
        					&& content.trim().equals(pairContent.trim()))
        				numOfInsertions++;
        		}
        	}
        	
        	double insertionLevel = numOfInsertions / splitPairNodeContent.size();
        	
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
    
    private List<InitializationBlocksHandlerNode> defineEditedNodes(List<FSTNode> addedCandidates, List<FSTNode> deletedCandidates) {
    	List<InitializationBlocksHandlerNode> editedNodes = new ArrayList<InitializationBlocksHandlerNode>();
    	
    	for(FSTNode node : addedCandidates) {
    		Pair<FSTNode, Double> maxInsertionPair = maxInsertionLevel(node, deletedCandidates);
    		Pair<FSTNode, Double> maxSimilarityPair = maxSimilarity(node, deletedCandidates);
    		FSTNode baseNode = maxInsertionPair.getKey() != null ? maxInsertionPair.getKey() : maxSimilarityPair.getKey();
    		
    		if(baseNode != null && (maxInsertionPair.getValue() > 0.7 || maxSimilarityPair.getValue() > 0.5)) {
    			InitializationBlocksHandlerNode editedNode;
    		
    			if(maxInsertionPair.getValue() == 1.0) {
    				String originalNodeContent = ((FSTTerminal) node).getBody();
    				FSTNode nodeClone  = node.getShallowClone();
    				updateNodeBody(nodeClone, baseNode);
    				editedNode = new InitializationBlocksHandlerNode(baseNode, nodeClone, true,
    						originalNodeContent);
    			} else {
    				editedNode = new InitializationBlocksHandlerNode(baseNode, node);
    			}
    			
    			editedNodes.add(editedNode);
    		} 
    	}
    	
    	return editedNodes;
    }
    
    private void updateNodeBody(FSTNode node, FSTNode baseNode) {
    	StringBuffer finalNodeBody = new StringBuffer();
    	
    	String nodeBody = ((FSTTerminal) node).getBody();
    	List<String> splitNodeContent = Arrays.asList(nodeBody.split(LINE_BREAKER));
    	
    	String baseNodeBody = ((FSTTerminal) baseNode).getBody();
    	List<String> splitBaseNodeContent = Arrays.asList(baseNodeBody.split(LINE_BREAKER));
    	
    	finalNodeBody.append(baseNodeBody);
    	finalNodeBody.append(TEMPORARY_STATIC_NEW_BLOCK);
    	
    	for(String line : splitNodeContent) {
    		if(!splitBaseNodeContent.contains(line)) {
    			finalNodeBody.append(line + "\n");
    		}
    	}
    	
    	finalNodeBody.append(BLOCK_DELIMITER + "\n");
    	
    	((FSTTerminal) node).setBody(finalNodeBody.toString());
    }
    
    public List<InitializationBlocksHandlerNode> getRightEditedNodes() {
		return rightEditedNodes;
	}

	public void setRightEditedNodes(List<InitializationBlocksHandlerNode> rightEditedNodes) {
		this.rightEditedNodes = rightEditedNodes;
	}

	public List<FSTNode> getRightDeletedNodes() {
		return rightDeletedNodes;
	}

	public void setRightDeletedNodes(List<FSTNode> rightDeletedNodes) {
		this.rightDeletedNodes = rightDeletedNodes;
	}

	public List<InitializationBlocksHandlerNode> getLeftEditedNodes() {
		return leftEditedNodes;
	}

	public void setLeftEditedNodes(List<InitializationBlocksHandlerNode> leftEditedNodes) {
		this.leftEditedNodes = leftEditedNodes;
	}

	public List<FSTNode> getLeftDeletedNodes() {
		return leftDeletedNodes;
	}

	public void setLeftDeletedNodes(List<FSTNode> leftDeletedNodes) {
		this.leftDeletedNodes = leftDeletedNodes;
	}

}

class InitializationBlocksHandlerNode {
	private FSTNode baseNode;
	private FSTNode pairNode;
	private boolean editedNode = false;
	private String originalNodeContent;

	public InitializationBlocksHandlerNode(FSTNode baseNode, FSTNode pairNode) {
		this.baseNode = baseNode;
		this.pairNode = pairNode;
	}
	
	public InitializationBlocksHandlerNode(FSTNode baseNode, FSTNode pairNode, boolean editedNode,
			String originalNodeContent) {
		this(baseNode, pairNode);
		this.editedNode = editedNode;
		this.originalNodeContent = originalNodeContent;
	}
	
	public FSTNode getBaseNode() {
		return baseNode;
	}

	public void setBaseNode(FSTNode baseNode) {
		this.baseNode = baseNode;
	}

	public FSTNode getPairNode() {
		return pairNode;
	}

	public void setPairNode(FSTNode pairNode) {
		this.pairNode = pairNode;
	}

	public boolean isEditedNode() {
		return editedNode;
	}

	public void setEditedNode(boolean editedNode) {
		this.editedNode = editedNode;
	}
	
	public String getOriginalNodeContent() {
		return originalNodeContent;
	}

	public void setOriginalNodeContent(String originalNodeContent) {
		this.originalNodeContent = originalNodeContent;
	}
}
