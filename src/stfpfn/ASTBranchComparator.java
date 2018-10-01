package stfpfn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import br.ufpe.cin.files.st.FilesManager;
import br.ufpe.cin.mergers.util.st.MergeContextSt;
import de.ovgu.cide.fstgen.ast.st.FSTNode;
import de.ovgu.cide.fstgen.ast.st.FSTNonTerminal;
import fpfn.Difference;
import fpfn.FPFNUtils;
import fpfn.Difference.Type;

/**
 * FPFN
 * Compares two given tree branches
 * @author Guilherme
 *
 */
public class ASTBranchComparator {

	public final static int LAST_COMMON_STMT_INDEX 			= 0;
	public final static int LAST_COMMON_NODE_INDEX 			= 1;
	public final static int LAST_COMMON_STMT_INDEX_LEFT	 	= 2;
	public final static int LAST_COMMON_STMT_INDEX_RIGHT 	= 3;


	public int countEditionsToDifferentPartsOfSameStmt(ArrayList<ArrayList<FSTNode>> branchesFromLeft, ArrayList<ArrayList<FSTNode>> branchesFromRight, MergeContextSt context){
		int editionsToDifferentPartsOfSameStmt = 0;
		for(ArrayList<FSTNode> leftBranch : branchesFromLeft){
			for(ArrayList<FSTNode> rightBranch : branchesFromRight){
				//if(!isChangesInTheSameParentNode(leftBranch, rightBranch)){
					ArrayList<FSTNode> results = findDeeperEqualStmt(leftBranch, rightBranch);
					FSTNode comonStmt = results.get(LAST_COMMON_STMT_INDEX);
					FSTNode leftStmt  = results.get(LAST_COMMON_STMT_INDEX_LEFT);
					FSTNode rightStmt = results.get(LAST_COMMON_STMT_INDEX_RIGHT);				
					if(isStmtValid(comonStmt)){
						if(isContentValid(leftStmt, rightStmt)){
							editionsToDifferentPartsOfSameStmt++;
							logFalseNegative(leftBranch,rightBranch, comonStmt,leftStmt,rightStmt,context);
						}
					}
				//}
			}
		}
		return (editionsToDifferentPartsOfSameStmt/2);

	}

	private void logFalseNegative(ArrayList<FSTNode> leftBranch, ArrayList<FSTNode> rightBranch, FSTNode common, FSTNode left, FSTNode right, MergeContextSt context) {
		String leftBranchRepresentation  = leftBranch.toString();
		String rightBranchRepresentation = rightBranch.toString();
		String fnBranch 	= (printCommonBranch(common)).toString();
		String leftCode 	= FilesManager.prettyPrint((FSTNonTerminal) left);
		String rightCode 	= FilesManager.prettyPrint((FSTNonTerminal) right);
		String files = ((context.left != null) ? context.left.getAbsolutePath() : "<empty left>") + ";" + ((context.base != null) ? context.base.getAbsolutePath() : "<empty base>") + ";" + ((context.right != null) ? context.right.getAbsolutePath() : "<empty right>");
		//String entry = (files+";"+fnBranch+";"+rightBranchRepresentation+";"+leftBranchRepresentation+";"+leftCode+";"+rightCode);
		String entry = (files+";\n"+leftCode+";\n"+rightCode);
		context.branches.LOG_EDITIONS_TO_DIFFERENT_PARTS_OF_SAME_STMT.add(entry); //not used yet, but usefull if you want to log it


		/*
		 * It will be printed two times, but you have to count as only one instance
		 */
		FSTNode methodDecl = FPFNUtils.getMethodNode(common);
		if(methodDecl != null){
			String mergedBodyContent   = FPFNUtils.getMethodBody(methodDecl);
			String signature 		   = FPFNUtils.extractSignature(mergedBodyContent);
			br.ufpe.cin.mergers.util.MergeConflict mc = new br.ufpe.cin.mergers.util.MergeConflict(leftCode,rightCode); //actually how the false negative should be conflict

			for(Difference jfstmergeDiff: context.differences){ //filling differences with jdime's info
				if(FPFNUtils.areSignatureEqual(signature, jfstmergeDiff.signature))
					jfstmergeDiff.jdimeBody = mergedBodyContent;
			}
			Difference diff = FPFNUtils.getOrCreateDifference(context.differences,signature,mc);
			diff.types.add(Type.SAME_STATEMENT);
			diff.jdimeConf = mc;
			diff.jdimeBody = mergedBodyContent;
			diff.signature = signature;
		}
	}

	private boolean isStmtValid(FSTNode stmt) {
		return ((null != stmt) && !(stmt.getType().equals("Block")));
	}

	private boolean isStmt(FSTNode deeperEqualNode) {
		return (deeperEqualNode.getType().contains("Statement")) || (deeperEqualNode.getType().equals(" BlockLocalVariableDecl")); 
	}

	private boolean isChangesInTheSameParentNode(ArrayList<FSTNode> leftBranch, ArrayList<FSTNode> rightBranch){
		if(leftBranch.size() != rightBranch.size()){
			return false;
		} else {
			for(int i = 0; i < leftBranch.size(); i++){
				FSTNode left  = leftBranch.get(i);
				FSTNode right = rightBranch.get(i);
				if(left.compatibleType(right)){
					continue;
				} else {
					return false;
				}
			}
			return true;
		}
	}

	private ArrayList<FSTNode> findDeeperEqualStmt(ArrayList<FSTNode> leftBranch, ArrayList<FSTNode> rightBranch) {
		FSTNode deeperEqualNode 	= null; 
		FSTNode lastEqualStmt 		= null; 
		FSTNode lastEqualStmtLeft 	= null; 
		FSTNode lastEqualStmtRight	= null;

		List<FSTNode> AUXlastEqualStmtLeftSubBranch 	= null; 
		List<FSTNode> AUXlastEqualStmtRightSubBranch 	= null; 
		int limit = (leftBranch.size() < rightBranch.size()) ? leftBranch.size() : rightBranch.size();
		int index = 0;
		while(index < limit){
			FSTNode left  = leftBranch.get(index);
			FSTNode right = rightBranch.get(index);
			if(left.compatibleType(right) && left.getName().equals(right.getName())){
				deeperEqualNode = leftBranch.get(index);
				if(isStmt(deeperEqualNode)){
					lastEqualStmt 		= deeperEqualNode;
					lastEqualStmtLeft 	= leftBranch.get(index);
					lastEqualStmtRight 	= rightBranch.get(index);
					try{
						AUXlastEqualStmtLeftSubBranch  = leftBranch.subList(index+1, leftBranch.size()-1);
					} catch(IllegalArgumentException e){
						AUXlastEqualStmtLeftSubBranch = new LinkedList<FSTNode>();
					}
					try{
						AUXlastEqualStmtRightSubBranch = rightBranch.subList(index+1, rightBranch.size()-1);
					} catch(IllegalArgumentException e){
						AUXlastEqualStmtLeftSubBranch = new LinkedList<FSTNode>();
					}
				}
				index++;
			} else {
				break;
			}
		}

		if((null != AUXlastEqualStmtLeftSubBranch) && (null != AUXlastEqualStmtRightSubBranch)){
			if(thereIsFutherBlock(AUXlastEqualStmtLeftSubBranch, AUXlastEqualStmtRightSubBranch)){
				lastEqualStmt = null;
			}
		}

		ArrayList<FSTNode> result = new ArrayList<FSTNode>();
		result.add(lastEqualStmt);
		result.add(deeperEqualNode);
		result.add(lastEqualStmtLeft);
		result.add(lastEqualStmtRight);
		return result;

	}

	private boolean thereIsFutherBlock(List<FSTNode> subBranchLeft, List<FSTNode> subBranchRight) {
		for(FSTNode n : subBranchLeft){
			if(n.getType().equals("Block")){
				return true;
			}
		}
		for(FSTNode n : subBranchRight){
			if(n.getType().equals("Block")){
				return true;
			}
		}
		return false;
	}

	private ArrayList<FSTNode> printCommonBranch(FSTNode deeperEqualNode){
		return rebuildAST(deeperEqualNode);

	}

	private ArrayList<FSTNode> rebuildAST(FSTNode node){
		ArrayList<FSTNode> parents = new ArrayList<FSTNode>();
		rebuild(node, parents);
		Collections.reverse(parents);
		parents.add(node);
		return parents;
	}

	private void rebuild(FSTNode node, ArrayList<FSTNode> parents){
		if(null == node.getParent()){
			return;
		} else {
			parents.add(node.getParent());
			rebuild(node.getParent(),parents);
		}
	}

	private  boolean isContentValid(FSTNode left, FSTNode right){
		String leftContent  	= FilesManager.getStringContentIntoSingleLineNoSpacing(left.printFST(0));
		String rightContent 	= FilesManager.getStringContentIntoSingleLineNoSpacing(right.printFST(0));
		return (!leftContent.equals(rightContent));
	}
}
