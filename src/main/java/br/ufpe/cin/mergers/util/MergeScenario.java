package br.ufpe.cin.mergers.util;

import java.util.List;

import br.ufpe.cin.files.FilesTuple;

/**
 * A merge scenario represents the revisions involved 
 * in a three-way merge: first revision(left), base revision, second revision(right).
 * This class mainly encapsulates the tuples of matched files across the revisions.
 * @author Guilherme
 */
public class MergeScenario {
	private String mergeCommitSHA;
	private String revisionsFilePath;
	private String leftRevisionID;
	private String baseRevisionID;
	private String rightRevisionID;
	private List<FilesTuple> tuples;
	
	public MergeScenario(String revisionsFilePath, String leftId, String baseId, String rightId, List<FilesTuple> tuples){
		this.revisionsFilePath = revisionsFilePath;
		this.leftRevisionID  = leftId;
		this.baseRevisionID  = baseId;
		this.rightRevisionID = rightId;
		this.tuples = tuples;
	}
	
	public String getMergeCommitSHA() {
		return mergeCommitSHA;
	}

	public void setMergeCommitSHA(String mergeCommitSHA) {
		this.mergeCommitSHA = mergeCommitSHA;
	}

	public String getRevisionsFilePath() {
		return revisionsFilePath;
	}

	public void setRevisionsFilePath(String revisionsFilePath) {
		this.revisionsFilePath = revisionsFilePath;
	}

	public String getLeftRevisionID() {
		return leftRevisionID;
	}

	public void setLeftRevisionID(String leftRevisionID) {
		this.leftRevisionID = leftRevisionID;
	}

	public String getBaseRevisionID() {
		return baseRevisionID;
	}

	public void setBaseRevisionID(String baseRevisionID) {
		this.baseRevisionID = baseRevisionID;
	}

	public String getRightRevisionID() {
		return rightRevisionID;
	}

	public void setRightRevisionID(String rightRevisionID) {
		this.rightRevisionID = rightRevisionID;
	}

	public List<FilesTuple> getTuples() {
		return tuples;
	}

	public void setTuples(List<FilesTuple> tuples) {
		this.tuples = tuples;
	}

}
