package br.ufpe.cin.mergers.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.JavaCompiler;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Removing inner declarations (high-level elements, FSTNonTerminal nodes) challenges superimposition. 
 * For instance, left removed an inner classe edited by right.
 * This handler updates merged tree based on information of the deleted nodes and remaining nodes.
 * The handler combine two solutions. Basically, if the changeset of who edited the original class (without renaming) 
 * include a new reference to that class, it keeps both declarations. 
 * Otherwise, it joins the content of the two declarations to avoid code duplication. 
 * This last solution also verify if the renamed changeset includes a new reference to that class.
 * @author Guilherme
 */
public class DeletionsHandler implements ConflictHandler {

	private final static double DEFAULT_SIMILARITY_THRESHOLD = 0.9;

	public void handle(MergeContext context) {
		normalizeDeletions(context);
		for(FSTNode deletedLeft : context.nodesDeletedByLeft){
			manageDeletions(context, deletedLeft, context.rightTree, true);
		}
		for(FSTNode deletedRight : context.nodesDeletedByRight){
			manageDeletions(context, deletedRight, context.leftTree, false);
		}

	}

	private static void normalizeDeletions(MergeContext context) {
		//Getting original deleted nodes from its source
		normalize(context,context.nodesDeletedByLeft);
		normalize(context,context.nodesDeletedByRight);
	}

	private static void normalize(MergeContext context, List<FSTNode> nodes) {
		for(int i = 0; i<nodes.size();i++){
			FSTNode deleted = nodes.get(i);
			if(deleted instanceof FSTNonTerminal){
				FSTNode id = getId(deleted);
				if(id!=null){
					id = FilesManager.findNodeByID(context.baseTree, ((FSTTerminal) id).getBody());
					FSTNode normal_declaration = id.getParent();
					nodes.set(i, normal_declaration);
				}
			}
		}
	}

	private static void manageDeletions(MergeContext context, FSTNode deletedNode, FSTNode source, boolean isLeftDeletion) {
		if(deletedNode instanceof FSTNonTerminal){ //high-level elements are FSTNonTerminal nodes
			//1. search node's ID
			FSTNode identifier = getId(deletedNode);

			if(identifier != null){
				FSTNode correspondingIdInSource = FilesManager.findNodeByID(source, ((FSTTerminal) identifier).getBody());
				if(correspondingIdInSource != null && hasChanges(deletedNode, correspondingIdInSource.getParent())){
					//2. compare the number of element instances between the base version and the version with the original element (not deleted or renamed)
					// when there are new references to the original element, keep both declarations, otherwise join them
					if(hasNewInstance(context,((FSTTerminal) identifier).getBody(),isLeftDeletion)){
						keepDeclarations(context, source, ((FSTTerminal) identifier).getBody());
					} else {
						joinDeclarations(context, source, deletedNode,((FSTTerminal) identifier).getBody(), isLeftDeletion);
					}
				} else { //if there are no changes in the renamed/deleted node
					delete(context,((FSTTerminal)identifier).getBody());
				}
			}
		}
	}

	private static FSTNode getId(FSTNode node) {
		for (FSTNode child : ((FSTNonTerminal)node).getChildren()) {
			if(child.getType().equals("Id")){
				return child;
			}
		}
		return null;
	}

	private static void keepDeclarations(MergeContext context, FSTNode source, String identifier) {
		FSTNode correspondingInMerged = FilesManager.findNodeByID(context.superImposedTree, identifier); //TODO add type checking?
		FSTNode correspondingInSource = FilesManager.findNodeByID(source, identifier);
		if(correspondingInMerged != null && correspondingInSource !=null){
			FSTNonTerminal declarationInMerged = correspondingInMerged.getParent();
			FSTNonTerminal declarationInSource = correspondingInSource.getParent();

			FSTNonTerminal parent = declarationInMerged.getParent();
			int index = declarationInMerged.index;
			parent.removeChild(declarationInMerged);
			parent.addChild(declarationInSource, index);
		}
	}

	private static void joinDeclarations(MergeContext context, FSTNode source, FSTNode deletedNode, String identifier, boolean isLeftDeletion) {
		//1. remove original declaration from merged code
		FSTNonTerminal declarationInMerged = delete(context, identifier);
		if(declarationInMerged != null){
			int index = declarationInMerged.index;
			FSTNonTerminal parent = declarationInMerged.getParent();

			//2. update the content of the renamed node, if there is one
			List<FSTNode> candidates = (isLeftDeletion)? context.addedLeftNodes : context.addedRightNodes;
			boolean conflict = true;
			for(FSTNode renamingCandidate : candidates){
				if(renamingCandidate instanceof FSTNonTerminal){
					if(hasSameShape(renamingCandidate,deletedNode) && hasSimilarContent(renamingCandidate,deletedNode) 
							&& !hasNewInstance(context,((FSTTerminal) getId(renamingCandidate)).getBody(),!isLeftDeletion)){
						joinContent(source, identifier, parent, index,renamingCandidate);
						conflict = false;
						break;
					}
				}
			}
			if(conflict){
				FSTNode correspondingInSource = FilesManager.findNodeByID(source, identifier);
				if(correspondingInSource!=null){
					FSTNonTerminal declarationInSource = correspondingInSource.getParent();
					//conflict only if there are editions to the original element
					if(hasChanges(deletedNode, declarationInSource)){ 
						generateNonTerminalConflict(source, identifier, isLeftDeletion,	parent, index, context);
					}
				}
			}
		}
	}

	private static boolean hasChanges(FSTNode deletedNode, FSTNonTerminal declarationInSource) {
		return !(hasSameShape(declarationInSource, deletedNode) && hasSimilarContent(declarationInSource, deletedNode, 1));
	}

	private static boolean hasSimilarContent(FSTNode candidate, FSTNode deletedNode) {
		return hasSimilarContent(candidate, deletedNode, DEFAULT_SIMILARITY_THRESHOLD);
	}

	private static boolean hasSimilarContent(FSTNode candidate, FSTNode deletedNode, double similarityThreshold) {
		String printRenamedCandidate = FilesManager.getStringContentIntoSingleLineNoSpacing(FilesManager.prettyPrint((FSTNonTerminal) candidate));
		String printDeletedNode = FilesManager.getStringContentIntoSingleLineNoSpacing(FilesManager.prettyPrint((FSTNonTerminal) deletedNode));
		double similarity = FilesManager.computeStringSimilarity(printRenamedCandidate, printDeletedNode);
		return similarity >= similarityThreshold;
	}

	private static boolean hasSameShape(FSTNode renamingCadidate, FSTNode deletedNode) {
		String typesA = getTypes(renamingCadidate);
		String typesB = getTypes(deletedNode);
		return typesA.equals(typesB);
	}

	private static FSTNonTerminal delete(MergeContext context, String identifier){
		FSTNode correspondingInMerged = FilesManager.findNodeByID(context.superImposedTree, identifier);
		if(correspondingInMerged!=null){
			FSTNonTerminal declarationInMerged = correspondingInMerged.getParent();
			FSTNonTerminal parent = declarationInMerged.getParent();
			parent.removeChild(declarationInMerged);
			return declarationInMerged;
		}
		return null;
	}


	private static String getTypes(FSTNode node) {
		String types = node.getType();
		if (node instanceof FSTNonTerminal) {
			for (FSTNode child : ((FSTNonTerminal) node).getChildren()) { 
				types+= getTypes(child);
			}
		}
		return types;
	}

	private static void generateNonTerminalConflict(FSTNode source,
			String identifier, boolean isLeftDeletion, FSTNonTerminal parent,
			int index, MergeContext context) {
		/*This is a workaround as there is no comprehensive way to create a conflict for a non-terminal node.
			We pretty print the non-terminal node and create a terminal node representation instead.*/
		FSTNode correspondingInSource = FilesManager.findNodeByID(source, identifier);
		if(correspondingInSource!=null){
			FSTNonTerminal declarationInSource = correspondingInSource.getParent();
			String body = FilesManager.prettyPrint((FSTNonTerminal) declarationInSource);
			MergeConflict newConflict;
			if(isLeftDeletion){
				newConflict = new MergeConflict("", body+'\n');
			} else {
				newConflict = new MergeConflict(body+'\n',"");
			}
			FSTTerminal terminal = new FSTTerminal(declarationInSource.getType(), identifier, newConflict.body, "");
			parent.addChild(terminal, index);
			context.innerDeletionConflicts++;
		}
	}

	private static void joinContent(FSTNode source, String identifier, FSTNonTerminal parent, int index, FSTNode renamingCandidate) {
		//composition corresponds to put the new id on the the original declaration 
		parent.removeChild(renamingCandidate);
		FSTNode correspondingInSource = FilesManager.findNodeByID(source, identifier);
		FSTNode declarationInSource = correspondingInSource.getParent();
		declarationInSource.setName(renamingCandidate.getName());

		FSTNode newId = getId(renamingCandidate);
		((FSTTerminal) correspondingInSource).setBody(((FSTTerminal) newId).getBody());
		((FSTTerminal) correspondingInSource).setName(newId.getName());
		parent.addChild(declarationInSource, index);
	}

	private static boolean hasNewInstance(MergeContext context,	String identifier, boolean isLeftDeletion) {
		int baseInstances  = countInstances(context.getBase(),identifier);
		int otherInstances = countInstances(((isLeftDeletion)?context.getRight():context.getLeft()), identifier);
		return otherInstances > baseInstances;
	}

	private static int countInstances(File file, String id){
		JavaCompiler compiler = new JavaCompiler();
		String source = FilesManager.readFileContent(file);
		CompilationUnit cu = compiler.compile(source);
		List<org.eclipse.jdt.core.dom.ASTNode> instances = new ArrayList<ASTNode>();
		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(ClassInstanceCreation node) {
				org.eclipse.jdt.core.dom.Type t = node.getType();
				if(t.toString().equals(id)) instances.add(node);
				return super.visit(node);
			}
		});
		return instances.size();
	}
}
