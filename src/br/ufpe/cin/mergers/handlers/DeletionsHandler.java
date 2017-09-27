package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Removing inner declarations (high-level elements, FSTNonTerminal nodes) challenges superimposition. 
 * For instance, left removed an inner classe, while right edited it.
 * This handler updates merged tree based on information of the deleted nodes and remaining nodes.
 * In particular, it updates the merged tree with information of the edited version. For instance, 
 * in the example of left deleting a inner classe while right edited it. The merged code will have
 * the innerclasse definition from right.
 * @author Guilherme
 */
public class DeletionsHandler {

	public static void handle(MergeContext context) {
		for(FSTNode deletedLeft : context.nodesDeletedByLeft){
			manageDeletions(context, deletedLeft, context.rightTree);
		}
		for(FSTNode deletedRight : context.nodesDeletedByRight){
			manageDeletions(context, deletedRight, context.leftTree);
		}
	}

	private static void manageDeletions(MergeContext context, FSTNode deletedNode, FSTNode source) {
		if(deletedNode instanceof FSTNonTerminal){ //high-level elements are FSTNonTerminal nodes
			//1. search node's ID
			String identifier = null;
			for (FSTNode child : ((FSTNonTerminal)deletedNode).getChildren()) {
				if(child.getType().equals("Id")){
					identifier = ((FSTTerminal)child).getBody();
					break;
				}
			}
			//2. search corresponding nodes through the ID above
			if(identifier != null){
				FSTNode correspondingInMerged = FilesManager.findNodeByID(context.superImposedTree, identifier);
				FSTNode correspondingInSource  = FilesManager.findNodeByID(source, identifier);
				
				//3. update merged node
				if(correspondingInMerged!= null && correspondingInSource!=null){
					correspondingInMerged = correspondingInMerged.getParent();
					correspondingInSource = correspondingInSource.getParent();
					updateMergedTree(correspondingInMerged, correspondingInSource);
				}
			}
		}
	}

	private static void updateMergedTree(FSTNode merged, FSTNode source) {
		if (merged.compatibleWith(source)) {
			for (FSTNode childB : ((FSTNonTerminal) source).getChildren()) { 
				FSTNode childA = ((FSTNonTerminal) merged).getCompatibleChild(childB);
				if (childA == null) { 								
				} else {
					if(childA instanceof FSTTerminal){
						((FSTTerminal) childA).setBody(((FSTTerminal) childB).getBody());
					} else {
				          updateMergedTree(childA, childB);
			        }
				}
			}
		}
	}
}
