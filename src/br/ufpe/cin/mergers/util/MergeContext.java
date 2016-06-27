package br.ufpe.cin.mergers.util;

import java.util.ArrayList;
import java.util.List;

import de.ovgu.cide.fstgen.ast.FSTNode;

/**
 * Encapsulates pertinent information of the merging process. A context
 * is necessary to handle specific conflicts that simple
 * superimposition of trees is not able to address. 
 * Currently, it encapsulates the nodes added by the merged 
 * revisions and the superimposed tree.
 * @author Guilherme
 */
public class MergeContext {
	public List<FSTNode> nodesAddedByLeft = new ArrayList<FSTNode>();
	public List<FSTNode> nodesAddedByRight= new ArrayList<FSTNode>();
	public List<FSTNode> deletedBaseNodes = new ArrayList<FSTNode>();

	public FSTNode superImposedTree;
	public String semistructuredOutput;
	public String unstructuredOutput;

	/**
	 * Joins the information of another context.
	 * @param otherContext the context to be joined with
	 */
	public MergeContext join(MergeContext otherContext){
		for(FSTNode n : otherContext.nodesAddedByLeft){
			this.nodesAddedByLeft.add(n);
		}
		for(FSTNode n : otherContext.nodesAddedByRight){
			this.nodesAddedByRight.add(n);
		}
		return this;
	}
}
