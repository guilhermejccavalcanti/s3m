package br.ufpe.cin.mergers.handlers;

import java.util.List;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Behavioral errors might occur when one developer adds an element that refers to an edited one.
 * This class handle such cases.
 */
public final class NewElementReferencingEditedOneHandler implements ConflictHandler {

	public void handle(MergeContext context) {
		/*
		 * using unstructured merge output as guide to ensure that semistructured merge is not worse than unstructured merge.
		 * if there is a conflict with the investigated elements in unstructured merge output, we flag the elements as conflicting
		 * in semistructured merge as well (might be false positive). If there isn't, we go futher and textually we look 
		 * if added elements refers to the edited ones.  In particular, we handle the likewise cases: field declarations and methods.
		 */
		if((!context.editedLeftNodes.isEmpty() && !context.addedRightNodes.isEmpty()) ||
		   (!context.editedRightNodes.isEmpty()&& !context.addedLeftNodes.isEmpty())){
		List<MergeConflict> unstructuredMergeConflicts = FilesManager.extractMergeConflicts(context.unstructuredOutput);
		for(FSTNode addedLeftNode : context.addedLeftNodes){
			if(isValidNode(addedLeftNode)){
				for(FSTNode editedRightNode : context.editedRightNodes){
					if(isValidNode(editedRightNode)){
						String newElementContent 	  = ((FSTTerminal) addedLeftNode).getBody();
						String editedElementContent   = getEditedElementContent(editedRightNode);
						String editedElementIdentfier = getElementIdentifier(editedRightNode);
						if(thereIsUnstructuredConflictWithAddedAndEditedElements(unstructuredMergeConflicts, newElementContent, editedElementContent)){
							if(addedElementRefersToEditedOne(newElementContent,editedElementIdentfier)){
								generateConflictWithAddedAndEditedElements(context, editedRightNode, addedLeftNode);
							}
						}
					}
				}
			}
		}
		for(FSTNode addedRightNode : context.addedRightNodes){
			if(isValidNode(addedRightNode)){
				for(FSTNode editedLeftNode : context.editedLeftNodes){
					if(isValidNode(editedLeftNode)){
						String newElementContent 	  = ((FSTTerminal) addedRightNode).getBody();
						String editedElementContent   = getEditedElementContent(editedLeftNode);
						String editedElementIdentfier = getElementIdentifier(editedLeftNode);
						if(thereIsUnstructuredConflictWithAddedAndEditedElements(unstructuredMergeConflicts, newElementContent, editedElementContent)){
							if(addedElementRefersToEditedOne(newElementContent,editedElementIdentfier)){
								generateConflictWithAddedAndEditedElements(context, editedLeftNode, addedRightNode);
							}
						}
					}
				}
			}
		}
		}
	}

	/**
	 * Given a list of unstructured merge conflicts, verifies if there is
	 * a conflict containing the added and edited elements.
	 * @param unstructuredMergeConflicts
	 * @param added element
	 * @param edited element
	 * @return true if there is, false if not.
	 */
	private static boolean thereIsUnstructuredConflictWithAddedAndEditedElements(List<MergeConflict> unstructuredMergeConflicts,String addedContent, String editedContent) {
		for(MergeConflict mc : unstructuredMergeConflicts){
			if(mc.contains(addedContent, editedContent) || mc.contains(editedContent,addedContent)){
				return true;
			}
		}
		return false;
	}
	
	private static boolean addedElementRefersToEditedOne(String newElementContent, String editedElementIdentifier) {
		return newElementContent.matches("(?s).*\\b"+editedElementIdentifier+"\\b.*");
	}

	private static boolean isValidNode(FSTNode node){
		if(node instanceof FSTTerminal){
			String nodeType = ((FSTTerminal)node).getType();
			if(nodeType.equals("MethodDecl") || nodeType.equals("FieldDecl")){
				return true;
			}
		}
		return false;
	}

	private static String getElementIdentifier(FSTNode element) {
		String id =	(element.getType().equals("MethodDecl")) ? element.getName().split("\\(")[0] : element.getName();
		return id;
	}
	
	private static String getEditedElementContent(FSTNode editedelement) {
		//through tests we observed that only the signature (the first line) of an edited method appears in the possible conflict 
		String id =	(editedelement.getType().equals("MethodDecl")) ? (((FSTTerminal) editedelement).getBody()).split("\\{")[0] : ((FSTTerminal)editedelement).getBody();
		return id;
	}
	
	/**
	 * Creates a merge conflict with the given added and edited elements. 
	 * It also updates the merged AST with the new merge conflict.
	 * @param context
	 * @param editedElementContent
	 * @param addedElementContent
	 */
	private static void generateConflictWithAddedAndEditedElements(MergeContext context, FSTNode editedElement, FSTNode addedElement) {
		//first creates a conflict with the import statements
		MergeConflict newConflict = new MergeConflict(editedElement, addedElement);

		String editedElementContent = ((FSTTerminal) editedElement).getBody();
		String addedElementContent = ((FSTTerminal) addedElement).getBody();

		//second put the conflict in one of the nodes containing the import statements, and deletes the other node containing the orther import statement
		FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, editedElementContent, newConflict.toString());
		FilesManager.findAndDeleteASTNode(context.superImposedTree, addedElementContent);
		
		//statistics
		context.newElementReferencingEditedOneConflicts++;
	}
}
