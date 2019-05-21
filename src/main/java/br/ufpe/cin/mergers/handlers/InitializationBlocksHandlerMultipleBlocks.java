package br.ufpe.cin.mergers.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * As in semistructured merge the ID is used for matching the nodes and initialization blocks don't have it, during
 * the superimposition of these blocks they are duplicated. This handler matches the nodes mainly by the level of
 * content insertion compared to the blocks on base, also checking for possible dependency between blocks and solving 
 * variable renaming conflicts.
 * 
 *  @author Alice Borner
 *  
 */
public class InitializationBlocksHandlerMultipleBlocks implements ConflictHandler {
    
    private final static String INITIALIZATION_BLOCK_IDENTIFIER = "InitializerDecl";	
    private final static String LINE_BREAKER_REGEX = "\\r\\n(\\t)*";
    private final static String LOCAL_VAR_DECLARATION_REGEX = "^[a-zA-Z_$][a-zA-Z_$0-9]* ([a-zA-Z_$][a-zA-Z_$0-9]*)( *=.*)?;$";
    private final static String VAR_ASSIGNMENT_SET_REGEX = "^([a-zA-Z_$][a-zA-Z_$0-9]*) ?(=|\\.set).*;$";
    
    // conflict markers
    private final static String CONFLICT_MARKER = "<<<<<<<";
    private final static String CONLFICT_MINE = "<<<<<<< MINE";
    private final static String CONFLICT_YOURS = ">>>>>>> YOURS";
    private final static String CONFLICTS_SEPARATOR = "=======";
    
	public void handle(MergeContext context) throws TextualMergeException {
		
        List<FSTNode> leftNodes = findInitializationBlocks(context.addedLeftNodes);
        List<FSTNode> rightNodes = findInitializationBlocks(context.addedRightNodes);
        List<FSTNode> baseNodes = findInitializationBlocks(context.deletedBaseNodes);

        Map<FSTNode, FSTNode> baseLeftEditedNodesMap = selectEditedNodes(leftNodes, baseNodes);
        Map<FSTNode, FSTNode> baseRightEditedNodesMap = selectEditedNodes(rightNodes, baseNodes);

        Pair<List<FSTNode>, List<FSTNode>> addedNodes = selectAddedNodes(leftNodes, baseNodes, rightNodes,
        		baseLeftEditedNodesMap.keySet(), baseRightEditedNodesMap.keySet());
       
        Pair<List<FSTNode>, List<FSTNode>> deletedNodes = selectDeletedNodes(leftNodes, baseNodes, rightNodes,
        		baseLeftEditedNodesMap.keySet(), baseRightEditedNodesMap.keySet());
        
    	for(FSTNode baseNode : baseNodes) {
    		FSTNode leftNode = baseLeftEditedNodesMap.get(baseNode);
    		FSTNode rightNode = baseRightEditedNodesMap.get(baseNode);
    		
    		mergeContentAndUpdateAST(leftNode, baseNode, rightNode, context, deletedNodes);
    	}

    	// there are static global variables and left/right added blocks, so could be the case of a dependency
    	Map<FSTNode, List<FSTNode>> commonVarsNodesMap = getCommonAccessGlobalVariablesNodes(addedNodes);
    	if(!commonVarsNodesMap.isEmpty())
    		mergeDependentAddedNodesAndUpdateAST(context, commonVarsNodesMap);
    }
	
    private Map<FSTNode, FSTNode> defineEditedNodes(List<FSTNode> addedCandidates,
    		List<FSTNode> deletedCandidates) {
    	
    	Map<FSTNode, FSTNode> baseEditedNodesMap = new HashMap<FSTNode, FSTNode>();

    	for(FSTNode node : addedCandidates) {
    		Pair<FSTNode, Double> maxInsertionPair = maxInsertionLevel(node, deletedCandidates);
    		Pair<FSTNode, Double> maxSimilarityPair = maxSimilarity(node, deletedCandidates);
    		FSTNode baseNode = getBaseNode(maxInsertionPair, maxSimilarityPair);
    		
    		if(baseNode != null && (maxInsertionPair.getValue() > 0.7 || maxSimilarityPair.getValue() > 0.5)) {
    			baseEditedNodesMap.put(baseNode, node);
    		}
    	}
    	
    	return baseEditedNodesMap;
    }

	private void mergeContentAndUpdateAST(FSTNode leftNode, FSTNode baseNode, FSTNode rightNode, MergeContext context, 
			Pair<List<FSTNode>,List<FSTNode>> deletedNodes) throws TextualMergeException {
		
		String baseContent = ((FSTTerminal) baseNode).getBody();
		String leftContent = (leftNode != null) ? ((FSTTerminal) leftNode).getBody() : "";
		String rightContent = (rightNode != null) ? ((FSTTerminal) rightNode).getBody() : "";

		if(leftNode != null && rightNode != null) {
			// both branches edited the node
			
		    String mergedContent = TextualMerge.merge(leftContent, baseContent, rightContent, 
					JFSTMerge.isWhitespaceIgnored);
		    
			if (mergedContent != null && mergedContent.contains(CONFLICT_MARKER)) {
				// there is a conflict, check if it's only a renaming conflict to fix it
				mergedContent = checkVariableRenamingConflict(mergedContent, baseContent);
			} 
            
            FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, leftContent, mergedContent);
            FilesManager.findAndDeleteASTNode(context.superImposedTree, rightContent);
            
    		if (mergedContent.contains(CONFLICT_MARKER)) {
    			context.initializationBlocksConflicts++;
    		}
            
        } else if(leftNode == null && rightNode == null) {
        	// any branch edited the node, delete one of the nodes content to don't have duplicates
            FilesManager.findAndDeleteASTNode(context.superImposedTree, baseContent);
		} else {
			// one of the branches edited or deleted the node
			if (leftNode == null) {
				// left deleted or right edited the node 
				List<FSTNode> leftDeletedNodes = deletedNodes.getLeft();
				mergeDeletedEditedContentAndUpdateAST(context, baseNode, leftDeletedNodes, rightNode, true);
			}
			
			if (rightNode == null) {  
				// right deleted or left edited the node 
				List<FSTNode> rightDeletedNodes = deletedNodes.getRight();
				mergeDeletedEditedContentAndUpdateAST(context, baseNode, rightDeletedNodes, leftNode, false);
			}
        }
	}
	

	private Map<FSTNode, FSTNode> selectEditedNodes (List<FSTNode> branchNodes, List<FSTNode> baseNodes) {
    	
    	// Finding deleted candidates
    	List<FSTNode> branchDeletedCandidates = removeContainedNodesFromList(baseNodes, branchNodes);

    	// Finding added candidates
    	List<FSTNode> branchAddedCandidates = removeContainedNodesFromList(branchNodes, baseNodes);
    	
    	// Defining edited nodes by similarity and/or insertion level
    	Map<FSTNode, FSTNode> branchEditedNodes = defineEditedNodes(branchAddedCandidates,
    			branchDeletedCandidates);
    			
    	return branchEditedNodes;
    }
	
	private Pair<List<FSTNode>, List<FSTNode>> selectDeletedNodes(List<FSTNode> leftNodes, List<FSTNode> baseNodes,
			List<FSTNode> rightNodes, Set<FSTNode> leftEditedNodes, Set<FSTNode> rightEditedNodes) {
		
		// Finding deleted candidates
    	List<FSTNode> leftDeletedCandidates = removeContainedNodesFromList(baseNodes, leftNodes);
    	List<FSTNode> rightDeletedCandidates = removeContainedNodesFromList(baseNodes, rightNodes);
    	
      	// Defining deleted nodes removing edited ones from the candidates list
    	List<FSTNode> leftDeletedNodes = removeContainedNodesFromList(leftDeletedCandidates, leftEditedNodes);
    	List<FSTNode> rightDeletedNodes = removeContainedNodesFromList(rightDeletedCandidates, rightEditedNodes);
    	
    	return Pair.of(leftDeletedNodes, rightDeletedNodes);
	}
	
	private Pair<List<FSTNode>, List<FSTNode>> selectAddedNodes(List<FSTNode> leftNodes, List<FSTNode> baseNodes,
			List<FSTNode> rightNodes, Set<FSTNode> leftEditedNodes, Set<FSTNode> rightEditedNodes) {
    	
		// Finding added candidates
    	List<FSTNode> leftAddedCandidates = removeContainedNodesFromList(leftNodes, baseNodes);
    	List<FSTNode> rightAddedCandidates = removeContainedNodesFromList(rightNodes, baseNodes);
    	
     	// Defining added nodes removing edited ones from the candidates list
    	List<FSTNode> leftAddedNodes = removeContainedNodesFromList(leftAddedCandidates, leftEditedNodes);
    	List<FSTNode> rightAddedNodes = removeContainedNodesFromList(rightAddedCandidates, rightEditedNodes);
    	
    	return Pair.of(leftAddedNodes, rightAddedNodes);
	}
	
	private void mergeDeletedEditedContentAndUpdateAST(MergeContext context, FSTNode baseNode,
			List<FSTNode> deletedNodes, FSTNode node, boolean isLeftNode) 
					throws TextualMergeException {
		
		String baseContent = ((FSTTerminal) baseNode).getBody();
		String editedNodeContent = ((FSTTerminal) node).getBody();
		String otherNodeContent = baseContent;
		
		if(containsNode(deletedNodes, baseNode)) {
            FilesManager.findAndDeleteASTNode(context.superImposedTree, baseContent);
            otherNodeContent = "";
    	}
		
		String mergedContent;
		
		// order of parameters changes depending on which branch changes/deleted the node
		if(isLeftNode) {
			mergedContent = TextualMerge.merge(otherNodeContent, baseContent, editedNodeContent, 
					JFSTMerge.isWhitespaceIgnored);
		} else {
			mergedContent = TextualMerge.merge(editedNodeContent, baseContent, otherNodeContent, 
					JFSTMerge.isWhitespaceIgnored);
		}
		
		FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, editedNodeContent, mergedContent);
		FilesManager.findAndDeleteASTNode(context.superImposedTree, otherNodeContent);

		if (mergedContent != null && mergedContent.contains(CONFLICT_MARKER)) {
			context.initializationBlocksConflicts++;
		}
	}

	private Map<FSTNode,List<FSTNode>> getCommonAccessGlobalVariablesNodes(Pair<List<FSTNode>,
			List<FSTNode>> addedNodes) {
		
		Map<FSTNode,List<FSTNode>> commonVarsNodesMap = new HashMap<>();
		List<FSTNode> leftAddedNodes = addedNodes.getLeft();
		List<FSTNode> rightAddedNodes = addedNodes.getRight();

		for(FSTNode leftNode : leftAddedNodes) {
			for(FSTNode rightNode : rightAddedNodes) {
				String leftContent = ((FSTTerminal) leftNode).getBody();
				String rightContent = ((FSTTerminal) rightNode).getBody();

				List<String> leftGlobalVariables = getGlobalVariables(leftContent);
				List<String> rightGlobalVariables = getGlobalVariables(rightContent);
			    
				leftGlobalVariables.removeAll(rightGlobalVariables);
		
				if(!leftGlobalVariables.isEmpty()) {
					if(commonVarsNodesMap.get(leftNode) != null) {
						commonVarsNodesMap.get(leftNode).add(rightNode);
					} else {
						List<FSTNode> rightNodes = new ArrayList<FSTNode>();
						rightNodes.add(rightNode);
						commonVarsNodesMap.put(leftNode, rightNodes);
					}
				}
			}
		}
		
       return commonVarsNodesMap;
	}
	
	private List<String> getGlobalVariables(String nodeContent) {
		
		List<String> nodeContentLines = Arrays.asList(nodeContent.split(LINE_BREAKER_REGEX));
		List<String> nodeGlobalVars = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile(LOCAL_VAR_DECLARATION_REGEX);
		List<String> localVars = new ArrayList<String>();

		for(String line : nodeContentLines) {
			Matcher matcher = pattern.matcher(line);
			if(matcher.matches()) 
				localVars.add(matcher.group(1));
		}
		
		pattern = Pattern.compile(VAR_ASSIGNMENT_SET_REGEX);
		for(String line : nodeContentLines) {
			Matcher matcher = pattern.matcher(line);
			String varName = matcher.matches() ? matcher.group(1) : "";
			if(!varName.isEmpty() && localVars.contains(varName))
				nodeGlobalVars.add(varName);
		}
		
		return nodeGlobalVars;
	}
	
	private void mergeDependentAddedNodesAndUpdateAST(MergeContext context, Map<FSTNode, List<FSTNode>>
		commonVarsNodesMap) throws TextualMergeException {
		// TODO: implement this
	}

	private List<FSTNode> removeContainedNodesFromList(Collection<FSTNode> nodesList, 
    		Collection<FSTNode> nodesToCheck) {
    	
    	return nodesList.stream().filter(node -> !containsNode(nodesToCheck, node)).collect(Collectors.toList());
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
    
    private FSTNode getBaseNode(Pair<FSTNode, Double> maxInsertionPair, Pair<FSTNode, Double> maxSimilarityPair) {
    	
    	FSTNode baseNode;
    	
    	if(maxInsertionPair.getKey() != null && maxInsertionPair.getValue() != 0) {
    		baseNode = maxInsertionPair.getKey();
    	} else {
    		baseNode =  maxSimilarityPair.getKey();
    	}
    	
    	return baseNode;
    }

    // TODO: improve implementation to be more general
    private String checkVariableRenamingConflict(String mergedContent, String baseContent) {
    	
		String beforeConflict = StringUtils.substringBefore(mergedContent, CONLFICT_MINE);
		String afterConflict = StringUtils.substringAfter(mergedContent, CONFLICT_YOURS);
		String conflictContent = StringUtils.substringBetween(mergedContent, beforeConflict, afterConflict).trim();
		
		String leftContent = StringUtils.substringBetween(mergedContent, CONLFICT_MINE, CONFLICTS_SEPARATOR).trim();
		String rightContent = StringUtils.substringBetween(mergedContent, CONFLICTS_SEPARATOR, CONFLICT_YOURS).trim();

		/**
		 * parts[1] contains the name of the variable and parts[3] the content to be compared later if 
		 * one branch changed only the name and the other the content. 
		 */
		String[] leftParts = leftContent.split(" ");
		String[] rightParts = rightContent.split(" ");
		
		String baseVarLeft = findVarInBaseContent(leftParts[0] + ".*" + leftParts[1] + ".*;", baseContent);
		String baseVarRight = findVarInBaseContent(rightParts[0] + ".*" + rightParts[1] + ".*;", baseContent);
		
		if(baseVarLeft != null || baseVarRight != null) {
			// variable from left or right was found in base
			
		    String baseVarContent = baseVarLeft != null ? baseVarLeft : baseVarRight;
			String[] baseParts = baseVarContent.split(" ");
			String newVarContent = "";
			
			// if variable name from base and left is the same and value from base and right is the same
			if(isNameAndContentChangedByDifferentBranches(baseParts, leftParts, rightParts)) 
				newVarContent = leftParts[0] + " " + rightParts[1] + " " + leftParts[2] + " " + leftParts[3];
			
			if(isNameAndContentChangedByDifferentBranches(baseParts, rightParts, leftParts)) 
				newVarContent = leftParts[0] + " " + leftParts[1] + " " + leftParts[2] + " " + rightParts[3];
			
			if(!newVarContent.isEmpty())
				mergedContent = mergedContent.replace(conflictContent, newVarContent);
		}
		
		return mergedContent;
	}
	
	private boolean isNameAndContentChangedByDifferentBranches(String[] baseParts, String[] nameDiffBranch,
			String[] contentDiffBranch) {
		
		return baseParts[1].equals(nameDiffBranch[1]) && baseParts[3].equals(contentDiffBranch[3])
				&& !baseParts[1].equals(contentDiffBranch[1]) && !baseParts[3].equals(nameDiffBranch[3]);
	}
	
	private String findVarInBaseContent(String varRegex, String baseContent) {
		
		Pattern pattern = Pattern.compile(varRegex);
        Matcher matcher = pattern.matcher(baseContent);
        
        String baseVar = null;
        
        if(matcher.find()) 
        	baseVar = matcher.group();

        return baseVar;
	}
}
