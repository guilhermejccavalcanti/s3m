package br.ufpe.cin.mergers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.tuple.Pair;

import br.ufpe.cin.exceptions.SemistructuredMergeException;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.handlers.ConflictsHandler;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.parser.JParser;
import br.ufpe.cin.printers.Prettyprinter;
import cide.gparser.ParseException;
import cide.gparser.TokenMgrError;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Represents semistructured merge.
 * Semistrucutred merge is based on the concept of <i>superimposition</i> of ASTs. 
 * Superimposition merges trees recursively, 
 * beginning from the root, based on structural and nominal similarities.
 * @author Guilherme
 */
public final class SemistructuredMerge {

	static final String MERGE_SEPARATOR 	  = "##FSTMerge##";
	static final String SEMANTIC_MERGE_MARKER = "~~FSTMerge~~";

	/**
	 * Three-way semistructured merge of three given files.
	 * @param left
	 * @param base
	 * @param right
	 * @param context an empty MergeContext to store relevant information of the merging process.
	 * @return string representing the merge result.
	 * @throws SemistructuredMergeException 
	 * @throws TextualMergeException 
	 */
	public static String merge(File left, File base, File right, MergeContext context) throws SemistructuredMergeException, TextualMergeException{
		try{
			//parsing the files to be merged
			JParser parser 	 = new JParser();
			FSTNode leftTree = parser.parse(left);
			FSTNode baseTree = parser.parse(base);
			FSTNode rightTree= parser.parse(right);

			//merging
			//context = merge(leftTree,baseTree,rightTree);
			context.join(merge(leftTree,baseTree,rightTree)); 

			//handling special kinds of conflicts 
			ConflictsHandler.handle(context);

		} catch (ParseException | FileNotFoundException | UnsupportedEncodingException | TokenMgrError ex){
			throw new SemistructuredMergeException(ex.getMessage(),context);
		}
		
		//during the parsing process, code indentation is typically lost, so we reindent the code
		return FilesManager.indentCode(Prettyprinter.print(context.superImposedTree));
	}

	/**
	 * Merges the AST representation of previous given java files.
	 * @param left tree
	 * @param base tree
	 * @param right tree
	 * @throws TextualMergeException 
	 */
	private static MergeContext merge(FSTNode left, FSTNode base, FSTNode right) throws TextualMergeException{
		//indexes are necessary to a proper matching between nodes
		left.index = 0;
		base.index = 1;
		right.index= 2;


		MergeContext context 	  = new MergeContext();
		FSTNode mergeLeftBase 	  = superimpose(left, base, null, context, true);
		FSTNode mergeLeftBaseRight= superimpose(mergeLeftBase, right, null, context, false);
		removeRemainingBaseNodes(mergeLeftBaseRight, context);
		mergeMatchedContent(mergeLeftBaseRight,context);
		context.superImposedTree = mergeLeftBaseRight;
		return context;
	}

	/**
	 * Superimposes two given ASTs. 
	 * @param nodeA representing the first tree
	 * @param nodeB representing the second tree
	 * @param parent node to be superimposed in (can be null)
	 * @param context
	 * @param isProcessingBaseTree 
	 * @return superimposed tree
	 */
	private static FSTNode superimpose(FSTNode nodeA, FSTNode nodeB, FSTNonTerminal parent, MergeContext context, boolean isProcessingBaseTree) {
		if (nodeA.compatibleWith(nodeB)) {
			FSTNode composed = nodeA.getShallowClone();
			composed.index = nodeB.index;
			composed.setParent(parent);

			if (nodeA instanceof FSTNonTerminal	&& nodeB instanceof FSTNonTerminal) {
				FSTNonTerminal nonterminalA = (FSTNonTerminal) nodeA;
				FSTNonTerminal nonterminalB = (FSTNonTerminal) nodeB;
				FSTNonTerminal nonterminalComposed = (FSTNonTerminal) composed;

				for (FSTNode childB : nonterminalB.getChildren()) { //nodes from base or right
					FSTNode childA = nonterminalA.getCompatibleChild(childB);
					if (childA == null) { //means that a base node was deleted by left, or that a right node was added
						FSTNode cloneB = childB.getDeepClone();
						if( childB.index == -1)
							childB.index = nodeB.index;
						cloneB.index = childB.index;
						nonterminalComposed.addChild(cloneB);//cloneB must be removed afterwards if it is a base node 
						if(isProcessingBaseTree) { 
							context.deletedBaseNodes.add(cloneB); //base nodes deleted by left
						} else {
							context.addedRightNodes.add(cloneB); //nodes added by right
						}
					} else {
						if( childA.index == -1)
							childA.index = nodeA.index;
						if( childB.index == -1)
							childB.index = nodeB.index;
						nonterminalComposed.addChild(superimpose(childA, childB, nonterminalComposed, context, isProcessingBaseTree));
					}
				}
				for (FSTNode childA : nonterminalA.getChildren()) { //nodes from left, leftBase
					FSTNode childB = nonterminalB.getCompatibleChild(childA);
					if (childB == null) { //is a new node from left, or a deleted base node in right
						FSTNode cloneA = childA.getDeepClone();
						if(childA.index == -1)
							childA.index = nodeA.index;
						cloneA.index = childA.index;
						nonterminalComposed.addChild(cloneA);
						if( context.deletedBaseNodes.contains(childA)) { //this is only possible when processing right nodes because this is a base node not present either in left and right
							context.deletedBaseNodes.remove(childA);
							context.deletedBaseNodes.add(cloneA);
						} else {
							if(!context.addedLeftNodes.contains(cloneA)){
								context.addedLeftNodes.add(cloneA); //node added by left
							}
						}
					} else { 
						if(!isProcessingBaseTree) {
							context.deletedBaseNodes.remove(childA); //node common to right and base but not to left
						}
					}
				}
				return nonterminalComposed;

			} else if (nodeA instanceof FSTTerminal && nodeB instanceof FSTTerminal && parent instanceof FSTNonTerminal) {
				FSTTerminal terminalA = (FSTTerminal) nodeA;
				FSTTerminal terminalB = (FSTTerminal) nodeB;
				FSTTerminal terminalComposed = (FSTTerminal) composed;

				if (!terminalA.getMergingMechanism().equals("Default")) {
					terminalComposed.setBody(markContributions(terminalA.getBody(), terminalB.getBody(), isProcessingBaseTree, terminalA.index, terminalB.index));
				} 
				return terminalComposed;
			}
			return null;
		} else
			return null;
	}


	/**
	 * After superimposition, the content of a matched node is the content of those that originated him (left,base,right)
	 * So, this methods indicates the origin (left,base or right) in node's body content.
	 * @return node's body content marked
	 */
	private static String markContributions(String bodyA, String bodyB, boolean firstPass, int indexA, int indexB) { //#PAREI AQUI, indexB é necessário? pra que serve index
		if (bodyA.contains(SEMANTIC_MERGE_MARKER)) {
			return 	bodyA
					+ " "
					+ bodyB;
		}
		else {
			if(firstPass) {
				return  SEMANTIC_MERGE_MARKER 
						+ " " 
						+ bodyA 
						+ " " 
						+ MERGE_SEPARATOR 
						+ " " 
						+ bodyB 
						+ " " 
						+ MERGE_SEPARATOR;
			} else {
				if(indexA == 0){
					return  SEMANTIC_MERGE_MARKER 
							+ " " 
							+ bodyA 
							+ " "
							+ MERGE_SEPARATOR
							+ " "
							+ MERGE_SEPARATOR
							+ " "
							+ bodyB;
				} else {
					return  SEMANTIC_MERGE_MARKER
							+ " "
							+ MERGE_SEPARATOR
							+ " "
							+ bodyA
							+ " "
							+ MERGE_SEPARATOR
							+ " "
							+ bodyB;
				}	
			}
		}
	}

	/**
	 * After superimposition, base nodes supposed to be removed might remain.
	 * This method removes these nodes from the merged tree. 
	 * @param mergedTree
	 * @param context
	 */
	private static void removeRemainingBaseNodes(FSTNode mergedTree, MergeContext context) {
		boolean removed = false;
		for(FSTNode loneBaseNode : context.deletedBaseNodes) {
			if(mergedTree == loneBaseNode) {
				FSTNonTerminal parent = (FSTNonTerminal)mergedTree.getParent();
				if(parent != null) {
					parent.removeChild(mergedTree);
					removed = true;
				}
			}
		}
		if(!removed && mergedTree instanceof FSTNonTerminal) {
			Object[] children = ((FSTNonTerminal)mergedTree).getChildren().toArray();
			for(Object child : children) {
				removeRemainingBaseNodes((FSTNode)child, context);
			}
		}
	}

	/**
	 * After superimposition, the content of a matched node is the content of those that originated him (left,base,right).
	 * This method merges these parents' content. For instance, calling unstructured merge to merge methods' body.
	 * We use the tags from the method {@link #markContributions(String, String, boolean, int, int)} to guide this process.
	 * @param node to be merged
	 * @throws TextualMergeException 
	 */
	private static void mergeMatchedContent(FSTNode node, MergeContext context) throws TextualMergeException {
		if(node instanceof FSTNonTerminal) {
			for(FSTNode child : ((FSTNonTerminal)node).getChildren())
				mergeMatchedContent(child,context);
		} else if(node instanceof FSTTerminal) {
			if(((FSTTerminal)node).getBody().contains(SemistructuredMerge.MERGE_SEPARATOR)) {
				String body = ((FSTTerminal) node).getBody() + " ";
				String[] splittedBodyContent = body.split(SemistructuredMerge.MERGE_SEPARATOR);

				String leftContent = splittedBodyContent[0].replace(SemistructuredMerge.SEMANTIC_MERGE_MARKER, "").trim();
				String baseContent = splittedBodyContent[1].trim();
				String rightContent= splittedBodyContent[2].trim();

				String mergedBodyContent = TextualMerge.merge(leftContent, baseContent, rightContent, true);
				((FSTTerminal) node).setBody(mergedBodyContent);

				identifyNodesEditedInOnlyOneVersion(node,context,leftContent,baseContent, rightContent);

				identifyPossibleNodesDeletionOrRenamings(node,context,leftContent, baseContent, rightContent);
			}
		} else {
			System.err.println("Warning: node is neither non-terminal nor terminal!");			
		}		
	}

	/**
	 * Verifies if a node was deleted/renamed in one of the revisions
	 * @param node
	 * @param context
	 * @param leftContent
	 * @param baseContent
	 * @param rightContent
	 */
	private static void identifyPossibleNodesDeletionOrRenamings(FSTNode node,MergeContext context, String leftContent, String baseContent,	String rightContent) {
		String leftContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(leftContent);
		String baseContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(baseContent);
		String rightContenttrim= FilesManager.getStringContentIntoSingleLineNoSpacing(rightContent);
		if(!baseContenttrim.isEmpty()){
			if(!baseContenttrim.equals(leftContenttrim) && rightContenttrim.isEmpty()){
				Pair<String,FSTNode> tuple = Pair.of(baseContent, node);
				context.deletedRightNodes.add(tuple);
			} else if(!baseContenttrim.equals(rightContenttrim) && leftContenttrim.isEmpty()){
				Pair<String,FSTNode> tuple = Pair.of(baseContent, node);
				context.deletedLeftNodes.add(tuple);
			}
		}
	}

	/**
	 * Verifies if a node was edited in only one of the revisions (left, or right), and fills the given merge context with
	 * this information.
	 * @param node
	 * @param context
	 * @param leftContent
	 * @param baseContent
	 * @param rightContent
	 */
	private static void identifyNodesEditedInOnlyOneVersion(FSTNode node,MergeContext context, String leftContent, String baseContent,String rightContent) {
		String leftContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(leftContent);
		String baseContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(baseContent);
		String rightContenttrim= FilesManager.getStringContentIntoSingleLineNoSpacing(rightContent);
		if(!baseContenttrim.isEmpty()){
			if(baseContenttrim.equals(leftContenttrim) && !rightContenttrim.equals(leftContenttrim)){
				context.editedRightNodes.add(node);
			} else if(baseContenttrim.equals(rightContenttrim) && !leftContenttrim.equals(rightContenttrim)){
				context.editedLeftNodes.add(node);
			}
		}
	}
}
