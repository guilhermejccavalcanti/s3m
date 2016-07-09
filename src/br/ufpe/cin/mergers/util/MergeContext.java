package br.ufpe.cin.mergers.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.ovgu.cide.fstgen.ast.FSTNode;

/**
 * Encapsulates pertinent information of the merging process. A context
 * is necessary to handle specific conflicts that simple
 * superimposition of trees is not able to address. 
 * @author Guilherme
 */
public class MergeContext {
	File base;
	File right;
	File left;
	String outputFilePath;
	
	public List<FSTNode> nodesAddedByLeft = new ArrayList<FSTNode>();
	public List<FSTNode> nodesAddedByRight= new ArrayList<FSTNode>();
	public List<FSTNode> deletedBaseNodes = new ArrayList<FSTNode>();

	public FSTNode superImposedTree;
	public String semistructuredOutput;
	public String unstructuredOutput;
	
	public MergeContext(){
	}

	public MergeContext(File left, File base, File right, String outputFilePath) {
		this.left = left;
		this.base = base;
		this.right= right;
		this.outputFilePath = outputFilePath;
	}

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
		this.superImposedTree = otherContext.superImposedTree;
		
		return this;
	}
	
	public File getBase() {
		return base;
	}

	public void setBase(File base) {
		this.base = base;
	}

	public File getRight() {
		return right;
	}

	public void setRight(File right) {
		this.right = right;
	}

	public File getLeft() {
		return left;
	}

	public void setLeft(File left) {
		this.left = left;
	}
}
