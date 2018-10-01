package stfpfn;

import java.util.ArrayList;
import java.util.Collections;
import de.ovgu.cide.fstgen.ast.st.FSTNode;

/**
 * FPFN Stores the parents nodes of givens AST.Node
 * @author Guilherme
 */
public class ASTBranchesResult {

	// ATTRIBUTES EDITED FOR EACH MERGED FILE
	private ArrayList<ArrayList<FSTNode>> branchesFromLeft = new ArrayList<ArrayList<FSTNode>>();
	private ArrayList<ArrayList<FSTNode>> branchesFromRight = new ArrayList<ArrayList<FSTNode>>();
	public int EDITIONS_TO_DIFFERENT_PARTS_OF_SAME_STMT = 0;
	public ArrayList<String> LOG_EDITIONS_TO_DIFFERENT_PARTS_OF_SAME_STMT = new ArrayList<String>();
	public int CONFS = 0;
	public int LOCS = 0;
	public int FILES = 0;

	public void buildASTResultFromLeft(FSTNode node) {
		ArrayList<FSTNode> leftParents = new ArrayList<FSTNode>();
		build(node, leftParents);
		Collections.reverse(leftParents);
		branchesFromLeft.add(leftParents);
	}

	public void buildASTResultFromRight(FSTNode node) {
		ArrayList<FSTNode> rightParents = new ArrayList<FSTNode>();
		build(node, rightParents);
		Collections.reverse(rightParents);
		branchesFromRight.add(rightParents);
	}

	private void build(FSTNode node, ArrayList<FSTNode> parents) {
		if (null == node.getParent()) {
			return;
		} else {
			parents.add(node.getParent());
			build(node.getParent(), parents);
		}
	}

	public ArrayList<ArrayList<FSTNode>> getBranchesFromLeft() {
		return branchesFromLeft;
	}

	public ArrayList<ArrayList<FSTNode>> getBranchesFromRight() {
		return branchesFromRight;
	}
}