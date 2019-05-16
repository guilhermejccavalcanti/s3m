package br.ufpe.cin.mergers.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 * 
 * TODO: UPDATE DESCRIPTION
 * Semistructured merge uses elements identifier to match nodes, if a node has no identifier,
 * the algorithm is unable to match this node. This lead to problems with initialization block
 * as these elements has no identifier, semistructured merge cannot match them, and therefore creates duplicates.
 * To avoid this problem, we attempt to match them by textual similarity. 
 * 
 * Matching by level of content insertion
 * If insertion is 100% then split node into 2 different ones to merge them and then join again
 * Also checks for dependency!
 * 
 *  @author Alice Borner
 *  
 */
public class InitializationBlocksHandlerNewApproach implements ConflictHandler {

    private List<InitializationBlocksHandlerNode> rightEditedNodes = new ArrayList<InitializationBlocksHandlerNode>();
	private List<FSTNode> rightDeletedNodes = new ArrayList<FSTNode>();
	private List<FSTNode> rightAddedNodes = new ArrayList<FSTNode>();

	private List<InitializationBlocksHandlerNode> leftEditedNodes =  new ArrayList<InitializationBlocksHandlerNode>();
    private List<FSTNode> leftDeletedNodes = new ArrayList<FSTNode>();
    private List<FSTNode> leftAddedNodes = new ArrayList<FSTNode>();

    private final static String LINE_BREAKER_REGEX = "\\r?\\n(\\t)*";	
    private final static String INITIALIZATION_BLOCK_IDENTIFIER = "InitializerDecl";	
    private final static String TEMPORARY_STATIC_NEW_BLOCK = "\nstatic {\n";
    private final static String BLOCK_DELIMITER = "}";
    private final static String NEW_LINE = "\n";
    private final static String STATIC_GLOBAL_VARIABLE_REGEX = "static.*=.*;";
    private final static String CONFLICT_MARKER = "<<<<<<<";
    
    // TODO: add comments and rename variables/methods
	public void handle(MergeContext context) throws TextualMergeException {
		
        List<FSTNode> leftNodes = findInitializationBlocks(context.addedLeftNodes);
        List<FSTNode> rightNodes = findInitializationBlocks(context.addedRightNodes);
        List<FSTNode> baseNodes = findInitializationBlocks(context.deletedBaseNodes);

    	preProcessNodes(leftNodes, baseNodes, rightNodes);
       
    	for(FSTNode baseNode : baseNodes) {
    		InitializationBlocksHandlerNode leftNode = getEditedNodeByBaseNode(leftEditedNodes, baseNode);
    		InitializationBlocksHandlerNode rightNode = getEditedNodeByBaseNode(rightEditedNodes, baseNode);
    		
    		mergeContentAndUpdateAST(leftNode, baseNode, rightNode, context);
    	}
    	
    	List<String> staticGlobalVariables = getStaticGlobalVariables(context.getBaseContent());
    	
    	if(!staticGlobalVariables.isEmpty()) {
    		mergePossibleDependentAddedNodesAndUpdateAST(context, staticGlobalVariables);
    	}
    	
    }

	private List<String> getStaticGlobalVariables(String baseContent) {
		
		List<String> staticGlobalVariables = new ArrayList<String>();
		Pattern pattern = Pattern.compile(STATIC_GLOBAL_VARIABLE_REGEX);
        Matcher matcher = pattern.matcher(baseContent);

    	while(matcher.find()) {
    		// TODO: refactor this
    		String variable = StringUtils.substringBetween(matcher.group(), "static", "=").trim();
    		String[] parts = variable.split(" ");
    		String variableName = parts[1];
    		staticGlobalVariables.add(variableName);
    	}
        
       return staticGlobalVariables;
	}
	
	private void mergePossibleDependentAddedNodesAndUpdateAST(MergeContext context, List<String> staticGlobalVariables)
			throws TextualMergeException {
		
		for(String variable : staticGlobalVariables) {
			String baseContent = "";
			String mergedContent = null;
			FSTNode leftNode = findNodeUsesVariable(context.addedLeftNodes, variable);
			FSTNode rightNode = findNodeUsesVariable(context.addedRightNodes, variable);
			
			if(leftNode != null && rightNode != null) {
				String leftContent = ((FSTTerminal) leftNode).getBody();
				String rightContent = ((FSTTerminal) rightNode).getBody();
				
				mergedContent = TextualMerge.merge(leftContent, baseContent, rightContent, 
						JFSTMerge.isWhitespaceIgnored);
				
	            FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, leftContent, mergedContent);
	            FilesManager.findAndDeleteASTNode(context.superImposedTree, rightContent);
	            
				// statistics
				if (mergedContent != null && mergedContent.contains(CONFLICT_MARKER)) //has conflict
					context.initializationBlocksConflicts++;
			}
		}
	}
	
	private FSTNode findNodeUsesVariable(List<FSTNode> nodesList, String variable) {
		for(FSTNode node : nodesList) {
			String nodeContent = ((FSTTerminal) node).getBody();
			// TODO: refactor this regex to find variable redefinition and variable assignment
			Pattern pattern = Pattern.compile(variable);
	        Matcher matcher = pattern.matcher(nodeContent);

	    	if(matcher.find()) {
	    		return node;
	    	}
		}
		
		return null;
	}
	
	private void mergeContentAndUpdateAST(InitializationBlocksHandlerNode leftNode, FSTNode baseNode,
			InitializationBlocksHandlerNode rightNode, MergeContext context) 
			throws TextualMergeException {
		
		String baseContent = ((FSTTerminal) baseNode).getBody();
		String leftContent = (leftNode != null) ? ((FSTTerminal) leftNode.getEditedNode()).getBody() : "";
		String rightContent = (rightNode != null) ? ((FSTTerminal) rightNode.getEditedNode()).getBody() : "";

		String mergedContent = null;
		
		// both branches edited the node
		if(leftNode != null && rightNode != null) {
			
		    mergedContent = TextualMerge.merge(leftContent, baseContent, rightContent, 
					JFSTMerge.isWhitespaceIgnored);
		    
			if(leftNode.isSplitedNode() || rightNode.isSplitedNode()) {
				mergedContent = mergedContent.replace(BLOCK_DELIMITER + TEMPORARY_STATIC_NEW_BLOCK, "");
				String originalContent = leftNode.getOriginalNodeContent() != null ? leftNode.getOriginalNodeContent() :
					rightNode.getOriginalNodeContent();
				FilesManager.findAndDeleteASTNode(context.superImposedTree, originalContent);
			}
			
			if (mergedContent != null && mergedContent.contains(CONFLICT_MARKER)) {
				mergedContent = checkVariableRenamingConflict(mergedContent, baseContent);
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
	    		
				if(rightNode != null && rightNode.isSplitedNode()) {
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
				
				if(leftNode != null && leftNode.isSplitedNode()) {
					mergedContent = mergedContent.replace(BLOCK_DELIMITER + TEMPORARY_STATIC_NEW_BLOCK, "");
				}
				
				FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, leftContent, mergedContent);
				FilesManager.findAndDeleteASTNode(context.superImposedTree, rightContent);
			}
			
        }
		
		// statistics
		if (mergedContent != null && mergedContent.contains(CONFLICT_MARKER)) {
			//has conflict
			context.initializationBlocksConflicts++;
		}
	}
	
	// TODO: refactor this method
	private String checkVariableRenamingConflict(String mergedContent, String baseContent) {
		String leftContent = StringUtils.substringBetween(mergedContent, "<<<<<<< MINE", "=======").trim();
		String rightContent = StringUtils.substringBetween(mergedContent, "=======", ">>>>>>> YOURS").trim();

		String[] leftParts = leftContent.split(" ");
		String[] rightParts = rightContent.split(" ");
		
		String baseVarLeft = findVarInBaseContent(leftParts[0] + ".*" + leftParts[1] + ".*;", baseContent);
		String baseVarRight = findVarInBaseContent(rightParts[0] + ".*" + rightParts[1] + ".*;", baseContent);
		
		if(baseVarLeft != null || baseVarRight != null) {
		    String baseVarContent = baseVarLeft != null ? baseVarLeft : baseVarRight;
			String[] baseParts = baseVarContent.split(" ");
			String beforeConflict = StringUtils.substringBefore(mergedContent, "<<<<<<< MINE");
			String afterConflict = StringUtils.substringAfter(mergedContent, ">>>>>>> YOURS");
			String conflictContent = StringUtils.substringBetween(mergedContent, beforeConflict, afterConflict).trim();
			String newVarContent = "";
			
			if(baseParts[1].equals(leftParts[1]) && baseParts[3].equals(rightParts[3])) {
				newVarContent = leftParts[0] + " " + rightParts[1] + " " + leftParts[2] + " " + leftParts[3];
			}
			
			if(baseParts[1].equals(rightParts[1]) && baseParts[3].equals(leftParts[3])) {
				newVarContent = leftParts[0] + " " + leftParts[1] + " " + leftParts[2] + " " + rightParts[3];
			}
			
			if(!newVarContent.isEmpty())
				mergedContent = mergedContent.replace(conflictContent, newVarContent);
		}
		
		return mergedContent;
	}
	
	private String findVarInBaseContent(String varRegex, String baseContent) {
		Pattern pattern = Pattern.compile(varRegex);
        Matcher matcher = pattern.matcher(baseContent);
        
        String baseVar = null;
        
        if(matcher.find()) {
        	baseVar = matcher.group();
        }
        
        return baseVar;
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
    	
    	setLeftAddedNodes(removeContainedNodesFromList(leftAddedCandidates, getBaseNodes(leftEditedNodes)));
    	setRightAddedNodes(removeContainedNodesFromList(rightAddedCandidates, getBaseNodes(rightEditedNodes)));
    	
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
    	List<String> splitNodeContent = Arrays.asList(nodeLines.split(LINE_BREAKER_REGEX));

    	for(FSTNode pairNode : nodes) {
        	String pairNodeBody = ((FSTTerminal) pairNode).getBody();
        	String pairNodeLines = StringUtils.substringBetween(pairNodeBody, "{", "}").trim();
        	List<String> splitPairNodeContent = Arrays.asList(pairNodeLines.split(LINE_BREAKER_REGEX));
        	
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
    		FSTNode baseNode = getBaseNode(maxInsertionPair, maxSimilarityPair);
    		
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
    
    private FSTNode getBaseNode(Pair<FSTNode, Double> maxInsertionPair, Pair<FSTNode, Double> maxSimilarityPair) {
    	FSTNode baseNode;
    	if(maxInsertionPair.getKey() != null && maxInsertionPair.getValue() != 0) {
    		baseNode = maxInsertionPair.getKey();
    	} else {
    		baseNode =  maxSimilarityPair.getKey();
    	}
    	
    	return baseNode;
    }
    private void updateNodeBody(FSTNode node, FSTNode baseNode) {
    	StringBuffer finalNodeBody = new StringBuffer();
    	
    	String nodeBody = ((FSTTerminal) node).getBody();
    	List<String> splitNodeContent = Arrays.asList(nodeBody.split(LINE_BREAKER_REGEX));
    	
    	String baseNodeBody = ((FSTTerminal) baseNode).getBody();
    	List<String> splitBaseNodeContent = Arrays.asList(baseNodeBody.split(LINE_BREAKER_REGEX));
    	
    	finalNodeBody.append(baseNodeBody);
    	finalNodeBody.append(TEMPORARY_STATIC_NEW_BLOCK);
    	
    	for(String line : splitNodeContent) {
    		if(!splitBaseNodeContent.contains(line)) {
    			finalNodeBody.append(line + NEW_LINE);
    		}
    	}
    	
    	finalNodeBody.append(BLOCK_DELIMITER + NEW_LINE);
    	
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
	
    public List<FSTNode> getRightAddedNodes() {
		return rightAddedNodes;
	}

	public void setRightAddedNodes(List<FSTNode> rightAddedNodes) {
		this.rightAddedNodes = rightAddedNodes;
	}

	public List<FSTNode> getLeftAddedNodes() {
		return leftAddedNodes;
	}

	public void setLeftAddedNodes(List<FSTNode> leftAddedNodes) {
		this.leftAddedNodes = leftAddedNodes;
	}

}

class InitializationBlocksHandlerNode {
	private FSTNode baseNode;
	private FSTNode editedNode;
	private boolean splitedNode = false;
	private String originalNodeContent;

	public InitializationBlocksHandlerNode(FSTNode baseNode, FSTNode editedNode) {
		this.baseNode = baseNode;
		this.editedNode = editedNode;
	}
	
	public InitializationBlocksHandlerNode(FSTNode baseNode, FSTNode editedNode, boolean splitedNode,
			String originalNodeContent) {
		this(baseNode, editedNode);
		this.splitedNode = splitedNode;
		this.originalNodeContent = originalNodeContent;
	}
	
	public FSTNode getBaseNode() {
		return baseNode;
	}

	public void setBaseNode(FSTNode baseNode) {
		this.baseNode = baseNode;
	}

	public FSTNode getEditedNode() {
		return editedNode;
	}

	public void setEditedNode(FSTNode editedNode) {
		this.editedNode = editedNode;
	}

	public boolean isSplitedNode() {
		return splitedNode;
	}

	public void setSplitedNode(boolean splitedNode) {
		this.splitedNode = splitedNode;
	}
	
	public String getOriginalNodeContent() {
		return originalNodeContent;
	}

	public void setOriginalNodeContent(String originalNodeContent) {
		this.originalNodeContent = originalNodeContent;
	}
}
