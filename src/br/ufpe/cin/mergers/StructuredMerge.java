package br.ufpe.cin.mergers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.StructuredMergeException;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.parser.JParser;
import br.ufpe.cin.printers.Prettyprinter;
import cide.gparser.ParseException;
import cide.gparser.TokenMgrError;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Represents structured merge. Structured merge is based on the concept
 * of <i>superimposition</i> of ASTs. Superimposition merges trees recursively,
 * beginning from the root, based on structural and nominal similarities.
 * @author Guilherme
 */
public final class StructuredMerge {

	/**
	 * Three-way structured merge of three given files.
	 * @param left
	 * @param base
	 * @param right
	 * @param context an empty MergeContext to store relevant information of the merging process.
	 * @return string representing the merge result.
	 * @throws StructuredMergeException
	 * @throws TextualMergeException
	 */
	public static String merge(File left, File base, File right, MergeContext context)	throws StructuredMergeException, TextualMergeException {
		try {
			// parsing the files to be merged
			JParser parser = new JParser();
			FSTNode leftTree = parser.parse(left);
			FSTNode baseTree = parser.parse(base);
			FSTNode rightTree = parser.parse(right);

//			System.out.println(leftTree);
//			System.out.println(baseTree);
//			System.out.println(rightTree);
			
			/*
			 * common base left right nodes, or deleted based nodes
			 */
			FSTNode merged = merge_Left_Base_Right(leftTree, baseTree, rightTree, null);

			System.out.println(merged);

			
			/*
			 * added left right nodes
			 */
			FSTNode l = leftTree.getDeepClone();
			FSTNode r = rightTree.getDeepClone();
			merge_Left_Right(l, baseTree, r, merged, false);
			merge_Left_Right(r, baseTree, l, merged, true);

			context.superImposedTree = merged;

		} catch (ParseException | FileNotFoundException | UnsupportedEncodingException | TokenMgrError ex) {
			String message = ExceptionUtils.getCauseMessage(ex);
			if(ex instanceof FileNotFoundException) //FileNotFoundException does not support custom messages
				message = "The merged file was deleted in one version.";
			throw new StructuredMergeException(message, context);
		}

		// during the parsing process, code indentation is typically lost, so we indent the code
		return FilesManager.indentCode(Prettyprinter.print(context.superImposedTree));
	}

	private static FSTNode merge_Left_Base_Right (FSTNode left, FSTNode base, FSTNode right, FSTNonTerminal target) throws TextualMergeException{
		if(isOrdered(base) && (left != null && right!=null)){
			return merge_Left_Base_Right_Ordered(left, base, right, target);
		} else {
			if(left!=null)left.setMerged();
			if(base!=null)base.setMerged();
			if(right!=null)right.setMerged();
			if (base.compatibleWith(left) && base.compatibleWith(right)) { 
				/*
				 * the three-nodes have the same type and name
				 */
				FSTNode merged = base.getShallowClone();
				merged.setParent(target);

				if (left instanceof FSTNonTerminal && base instanceof FSTNonTerminal && right instanceof FSTNonTerminal) {
					for(FSTNode baseChild : ((FSTNonTerminal) base).getChildren()){
						FSTNode leftChild  = ((FSTNonTerminal) left).getCompatibleChild(baseChild);
						FSTNode rightChild = ((FSTNonTerminal)right).getCompatibleChild(baseChild);
						((FSTNonTerminal) merged).addChildOnMerge(merge_Left_Base_Right(leftChild, baseChild, rightChild, (FSTNonTerminal) merged));
					}
				} else {
					mergeLeaves(left, base, right, merged);
				}
				return merged;

			} else if(base.compatibleWith(left) && !base.compatibleWith(right)){ 	
				/*
				 * only left has the same type and name
				 */
				//setNodesProcessed(left, right);
				String baseAST = FilesManager.getStringContentIntoSingleLineNoSpacing(base.printFST(0));
				String leftAST = FilesManager.getStringContentIntoSingleLineNoSpacing(left.printFST(0));
				if(baseAST.equals(leftAST)){
					return right;
				} else {
					return createConflict(left, base, right, false);
				}
			} else if(!base.compatibleWith(left) && base.compatibleWith(right)) {
				/*
				 * only right has the same type and name
				 */
				//setNodesProcessed(left, right);
				String baseAST = FilesManager.getStringContentIntoSingleLineNoSpacing(base.printFST(0));
				String rightAST = FilesManager.getStringContentIntoSingleLineNoSpacing(right.printFST(0));
				if(baseAST.equals(rightAST)){
					return left;
				} else {
					return createConflict(left, base, right, false);
				}
			} else {
				/*
				 * the three nodes have different type and name
				 */
				if(left == null && right ==null) {
					/*
					 * base node mutually deleted, 
					 * no need for further actions
					 */
				} else {
					//setNodesProcessed(left, right);
					return createConflict(left, base, right, false);
				}
			}
		}
		return null;
	}

	private static FSTNode merge_Left_Base_Right_Ordered(FSTNode left, FSTNode base,FSTNode right, FSTNonTerminal target) throws TextualMergeException {
		if(left !=null) left.setMerged();
		if(base !=null) base.setMerged();
		if(right!=null)right.setMerged();

		if(base.compatibleType(left) && base.compatibleType(right)){
			/*
			 * the three-nodes have the same type
			 */
			FSTNode merged = base.getShallowClone();
			merged.setParent(target);

			if (left instanceof FSTNonTerminal && base instanceof FSTNonTerminal && right instanceof FSTNonTerminal) {
				/*
				 * Ordered merge considers the position a node
				 */
				for(int i = 0; i<((FSTNonTerminal) base).getChildren().size(); i++){
					FSTNode baseChild  = ((FSTNonTerminal) base).getChildren().get(i);
					FSTNode leftChild  = null;
					FSTNode rightChild = null;
					try { leftChild  = ((FSTNonTerminal) left).getChildren().get(i); } catch (IndexOutOfBoundsException e){/*when there is no children at that position*/}
					try { rightChild = ((FSTNonTerminal) right).getChildren().get(i);} catch (IndexOutOfBoundsException e){/*when there is no children at that position*/}

					((FSTNonTerminal) merged).addChildOnMerge(merge_Left_Base_Right_Ordered(leftChild, baseChild, rightChild, (FSTNonTerminal) merged));
				}
			} else {
				mergeLeaves(left, base, right, merged);
			}
			return merged;
		} else if(base.compatibleType(left) && !base.compatibleType(right)){ 	
			/*
			 * only left has the same type
			 */
			String baseAST = FilesManager.getStringContentIntoSingleLineNoSpacing(base.printFST(0));
			String leftAST = FilesManager.getStringContentIntoSingleLineNoSpacing(left.printFST(0));
			if(baseAST.equals(leftAST)){
				return right;
			} else {
				return createConflict(left, base, right, false);
			}
		} else if(!base.compatibleType(left) && base.compatibleType(right)) {
			/*
			 * only right has the same type
			 */
			String baseAST = FilesManager.getStringContentIntoSingleLineNoSpacing(base.printFST(0));
			String rightAST = FilesManager.getStringContentIntoSingleLineNoSpacing(right.printFST(0));
			if(baseAST.equals(rightAST)){
				return left;
			} else {
				return createConflict(left, base, right, false);
			}
		} else {
			/*
			 * the three nodes have different type
			 */
			if(left == null && right ==null) {
				/*
				 * base node mutually deleted, 
				 * no need for further actions
				 */
			} else {
				return createConflict(left, base, right, false);
			}
		}
		return null;
	}

	private static void merge_Left_Right(FSTNode a, FSTNode base, FSTNode b, FSTNode merged, boolean isProceessingRight){
		if(isOrdered(a)){
			merge_Left_Right_Ordered(a, base, b, merged, isProceessingRight);
		} else {
			if(a.isMerged()){
				if(a instanceof FSTNonTerminal){
					for(FSTNode aChild : ((FSTNonTerminal) a).getChildren()){
						FSTNode baseChild 	= ((FSTNonTerminal) base).getCompatibleChild(aChild);
						FSTNode bChild 		= ((FSTNonTerminal) b).getCompatibleChild(aChild);
						FSTNode mergedChild = ((FSTNonTerminal) merged).getCompatibleChild(aChild);
						if(baseChild == null){
							if(bChild == null){
								/*
								 * added node
								 */
								((FSTNonTerminal)merged).addChildOnMerge(aChild);
								aChild.setMerged();
								continue;	
							} else {
								if(!isProceessingRight) {
									/*
									 * mutually added node
									 */
									merge_Left_Right(aChild, baseChild, bChild, merged, isProceessingRight);
								} else {
									/*
									 * already processed mutually added node in Merge_Left_Right first pass
									 */
								}
							}
						} else {
							if(mergedChild == null){
								/*
								 * Already processed deletion in Merge_Left_Base_Right
								 */
							} else {
								if(mergedChild.isConflict()){
									/*
									 * Already processed merge conflict in Merge_Left_Base_Right
									 */
								} else {
									merge_Left_Right(aChild, baseChild, bChild, mergedChild, isProceessingRight);
								}
							}
						}
					}
				}
			} else {
				String aAST = FilesManager.getStringContentIntoSingleLineNoSpacing(a.printFST(0));
				String bAST = FilesManager.getStringContentIntoSingleLineNoSpacing(b.printFST(0));
				if(aAST.equals(bAST)){ 
					/*
					 * mutually added node
					 */
					((FSTNonTerminal)merged).addChildOnMerge(a);
				} else { 
					/*
					 * common added nodes mutually edited
					 */
					((FSTNonTerminal)merged).addChildOnMerge(createConflict(a, null, b, isProceessingRight));
				}

				//avoiding re-processing nodes
				b.setMerged();
				a.setMerged();

				/*				b.getParent().removeChild(b);
				a.getParent().removeChild(a);*/
			}
		}
	}

	private static void merge_Left_Right_Ordered(FSTNode a, FSTNode base, FSTNode b, FSTNode merged, boolean isProceessingRight){
		if(a.isMerged()){
			if(a instanceof FSTNonTerminal){
				for(int i = 0; i<((FSTNonTerminal) a).getChildren().size(); i++){
					FSTNode aChild  	= getChildAtPosition(a,i);
					FSTNode baseChild 	= getChildAtPosition(base,i);
					FSTNode bChild  	= getChildAtPosition(b,i);
					FSTNode mergedChild = getChildAtPosition(merged,i);
					if(baseChild == null){
						if(bChild == null){
							/*
							 * added node
							 */
							((FSTNonTerminal)merged).addChildOnMerge(aChild);
							aChild.setMerged();
							continue;	
						} else {
							if(!isProceessingRight) {
								/*
								 * mutually added node
								 */
								merge_Left_Right_Ordered(aChild, baseChild, bChild, merged, isProceessingRight);
							} else {
								/*
								 * already processed mutually added node in Merge_Left_Right_Ordered first pass
								 */
							}
						}
					} else {
						if(mergedChild == null){
							/*
							 * Already processed deletion in Merge_Left_Base_Right_Ordered
							 */
						} else {
							if(mergedChild.isConflict()){
								/*
								 * Already processed merge conflict in Merge_Left_Base_Right_Ordered
								 */
							} else {
								merge_Left_Right_Ordered(aChild, baseChild, bChild, mergedChild, isProceessingRight);
							}
						}
					}
				}
			}
		} else {
			String aAST = FilesManager.getStringContentIntoSingleLineNoSpacing(a.printFST(0));
			String bAST = FilesManager.getStringContentIntoSingleLineNoSpacing(b.printFST(0));
			if(aAST.equals(bAST)){ 
				/*
				 * mutually added node
				 */
				((FSTNonTerminal)merged).addChildOnMerge(a);
			} else { 
				/*
				 * common added nodes mutually edited
				 */
				((FSTNonTerminal)merged).addChildOnMerge(createConflict(a, null, b, isProceessingRight));
			}

			//avoiding re-processing nodes
			b.setMerged();
			a.setMerged();

			/*			b.getParent().removeChild(b);
			a.getParent().removeChild(a);*/
		}
	}

	private static void mergeLeaves(FSTNode left, FSTNode base, FSTNode right,	FSTNode merged) throws TextualMergeException {
		String mergedBody = "";
		String leftBody  = FilesManager.getStringContentIntoSingleLineNoSpacing(((FSTTerminal) left).getBody());
		String baseBody  = FilesManager.getStringContentIntoSingleLineNoSpacing(((FSTTerminal) base).getBody());
		String rightBody = FilesManager.getStringContentIntoSingleLineNoSpacing(((FSTTerminal)right).getBody());
		if(leftBody.equals(rightBody)){
			mergedBody = ((FSTTerminal) left).getBody(); //or right
		} else if(leftBody.equals(baseBody) && !rightBody.equals(leftBody)){
			mergedBody = ((FSTTerminal) right).getBody();
		} else if(rightBody.equals(baseBody) && !leftBody.equals(rightBody)){
			mergedBody = ((FSTTerminal) left).getBody(); 
		} else {
			mergedBody = createConflict(left, base, right, false).getBody();
			merged.setConflict(true);
		}
		//		String mergedBody = TextualMerge.merge(((FSTTerminal) left).getBody(), ((FSTTerminal) base).getBody(), ((FSTTerminal) right).getBody(), false);
		((FSTTerminal) merged).setBody(mergedBody);
	}

	private static boolean isOrdered(FSTNode node) {
		return 	   node.getType().equals("MethodDeclarationBodyBlock") 
				|| node.getType().equals("ConstructorDeclarationBody")
				|| node.getType().equals("FieldDeclaration");
	}

	private static FSTTerminal createConflict(FSTNode left, FSTNode base, FSTNode right, boolean invertBody) {
		String baseBody  = "";
		String leftBody  = "";
		String rightBody = "";
		if(base instanceof FSTTerminal){
			if(base != null) baseBody = ((FSTTerminal) base).getBody()+"\n";
			if(left != null) leftBody = ((FSTTerminal) left).getBody()+"\n";
			if(right!= null) rightBody= ((FSTTerminal)right).getBody()+"\n";
		} else {
			if(base != null) baseBody = FilesManager.prettyPrint((FSTNonTerminal) base);
			if(left != null) leftBody = FilesManager.prettyPrint((FSTNonTerminal) left);
			if(right!= null) rightBody = FilesManager.prettyPrint((FSTNonTerminal) right);
		}
		MergeConflict mc;
		if(invertBody){
			mc = new MergeConflict(rightBody, baseBody, leftBody); 
		} else {
			mc = new MergeConflict(leftBody, baseBody, rightBody);
		}

		//Workaround to pretty print merge conflicts
		String type = "MergeConflict";
		if(base !=null) type = base.getType();
		else if(left!=null) type = left.getType();
		else type = right.getType();

		FSTTerminal conflict = new FSTTerminal(type, "-", mc.toString(),"");
		conflict.setConflict(true);
		return conflict;
	}

	private static FSTNode getChildAtPosition(FSTNode node, int position){
		try {
			return((FSTNonTerminal) node).getChildren().get(position);
		} catch(Exception e){
			return null;
		}
	}

	/*	private static void setNodesProcessed(FSTNode left, FSTNode right) {
		//avoiding re-processing nodes in further merging processes (e.g. merge_Left_Right)
		if(left!=null  && left.getParent()!=null) left.getParent().removeChild(left);
		if(right!=null && right.getParent()!=null)right.getParent().removeChild(right);
	}*/
}
