package br.ufpe.cin.mergers.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class InitializationBlocksHandlerNewApproach implements ConflictHandler {

    public void handle(MergeContext context) throws TextualMergeException {
        List<FSTNode> leftNodes = findInitializationBlocks(context.addedLeftNodes);
        List<FSTNode> rightNodes = findInitializationBlocks(context.addedRightNodes);
        List<FSTNode> baseNodes = findInitializationBlocks(context.deletedBaseNodes);
        
        // TODO: improve this name and maybe structure!!
        InitializationBlocksHandlerNodes nodes = new InitializationBlocksHandlerNodes();

        preProcessNodes(leftNodes, baseNodes, rightNodes);
        
    	List<FSTNode> finalNodes = new ArrayList<FSTNode>();

    	//for node in baseNodes 
    	// if node in leftDeletedNodes or node in rightDeletedNodes 
    	// 		if node in leftEditedNodes or node in rightEditedNodes 
    	// 			finalNodes.add(result of textualMerge in node changes)
    	// else if node in leftEditedNodes and node not in rightEditedNodes 
    	// 		finalNodes.add(node)
    	// else if node in rightEditedNodes and node not in leftEditedNodes 
   	    //	    finalNodes.add(node)
    	// else if node in rightEditedNodes and node in leftEditedNodes 
    	//    	finalNodes.add(result of textualMerge in node changes)
    	//
    	
    	//finalNodes.add(leftAddedNodes)
    	//finalNodes.add(rightAddedNodes)
        
    	
    	// TODO: CHECK HOW TO WRITE THE CONTENT IN THE AST IN THE END!!! check methods in FilesManager class
    }
    
    private static void preProcessNodes(List<FSTNode> leftNodes, List<FSTNode> baseNodes, List<FSTNode> rightNodes) {
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
    	
    	separateNodesIntoEditedOrAdded(leftAddedCandidates, leftDeletedCandidates);
    	separateNodesIntoEditedOrAdded(rightAddedCandidates, rightDeletedCandidates);
    	
    	List<FSTNode> leftDeletedNodes = new ArrayList<FSTNode>();
    	leftDeletedNodes.addAll(leftDeletedCandidates);
    	// leftDeletedNodes.removeAll(leftEditedNodes);
    	
    	List<FSTNode> rightDeletedNodes = new ArrayList<FSTNode>();
    	rightDeletedNodes.addAll(rightDeletedCandidates);
    	// rightDeletedNodes.removeAll(rightEditedNodes);
    	
    }
  
    private static List<FSTNode> findInitializationBlocks(List<FSTNode> nodes) {
        return nodes.stream()
                .filter(p -> p.getType().equals("InitializerDecl"))
                .collect(Collectors.toList());
    }
    
    private static Pair<FSTNode, Double> maxInsertionLevel(FSTNode node, List<FSTNode> nodes) {
//      nodeContent = node.split(";")
//    	for pairNode in nodesList
//    		pairNodeContent = pairNode.split(";")
//    	               %insertionPerNode.add((pairNode, pairNodeContent.compare(nodeContent)))
//    	return maxInsertion%Pair(%insertionPerNode)
    	
    	String nodeContent = ((FSTTerminal) node).getBody();
    	String[] splitNodeContent = nodeContent.split(";");
    	
    	for(FSTNode pairNode : nodes) {
        	String pairNodeContent = ((FSTTerminal) node).getBody();
        	String[] splitPairNodeContent = pairNodeContent.split(";");
        	
        	// TODO: compute the level of insertion checking line by line if it's equals
        	
    	}
		return null;
    	
    }
    
    private static Pair<FSTNode, Double> maxSimilarity(FSTNode node, List<FSTNode> nodes) {
//    	nodeContent = node.getContent()
//    		  	for pairNode in nodesList
//    		  		pairNodeContent = pairNode.getContent()
//    		  	               similarityPerNode.add((pairNode, computeStringSimilarity(nodeContent, pairNodeContent)))
//    		  	return maxSimilarityPair(similarityPerNode)
//    		    	
    	String nodeContent = ((FSTTerminal) node).getBody();
    	
    	for(FSTNode pairNode : nodes) {
        	String pairNodeContent = ((FSTTerminal) node).getBody();
        	
        	double similarity = FilesManager.computeStringSimilarity(nodeContent, pairNodeContent);
        	
        	// TODO: check the similarity for with each node to select the node with max similarity
        	
    	}
		return null;
    }
    
    // FIXME: fix return type to return the lists of edited and added nodes
    private static void separateNodesIntoEditedOrAdded(List<FSTNode> addedCandidates, List<FSTNode> deletedCandidates) {
//for node in addedCandidates
//  (baseNode, maxInsertion) = maxInsertionLevel(node, deletedCandidates)
//  (baseNode, maxSimilarity) =  maxSimilarity(node, deletedCandidates) 
//  if maxInsertion > x% or maxSimilarity > y%  
//    		editedNodes.add((baseNode, node)) 
//    	 else
//         		addedNodes.add((baseNode, null))	
    	
    	List<FSTNode> editedNodes = new ArrayList<FSTNode>();
    	List<FSTNode> addedNodes = new ArrayList<FSTNode>();

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
