package br.ufpe.cin.mergers.util;

import java.io.File;

import br.ufpe.cin.files.FilesManager;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Class representing a textual merge conflict.
 * 
 * @author Guilherme
 */

public class MergeConflict {

	private final String left;
	private final String base;
	private final String right;
	private final String body;
	private final String message;

	public int startLOC;
	public int endLOC;

	public File leftOriginFile;
	public File baseOriginFile;
	public File rightOriginFile;

	public String fullyQualifiedMergedClass;

	private static final String MINE_CONFLICT_MARKER = "<<<<<<< MINE";
	private static final String BASE_CONFLICT_MARKER = "||||||| BASE";
	private static final String CHANGE_CONFLICT_MARKER = "======= ";
	private static final String YOURS_CONFLICT_MARKER = ">>>>>>> YOURS";

	public MergeConflict(FSTNode left, FSTNode right) {
		this.left = getNodeContent(left);
		this.right = getNodeContent(right);
		this.base = "";
		this.message = "";
		this.body = assembleBody();
	}

	private String getNodeContent(FSTNode node) {
		if (node == null) {
			return "";
		} else if (node instanceof FSTTerminal) {
			return IndentationUtils.indentFirstLine((FSTTerminal) node);
		} else {
			return FilesManager.prettyPrint((FSTNonTerminal) node);
		}
	}

	private String assembleBody() {
		StringBuilder conflict = new StringBuilder();
		conflict.append(MINE_CONFLICT_MARKER).append('\n').append(left).append('\n').append(CHANGE_CONFLICT_MARKER)
				.append(message).append('\n').append(right).append('\n').append(YOURS_CONFLICT_MARKER);
		return conflict.toString();
	}

	public MergeConflict(FSTTerminal left, FSTTerminal right, int startLOC, int endLOC) {
		this(left, right);
		this.startLOC = startLOC;
		this.endLOC = endLOC;
	}

	public MergeConflict(String left, String right, int startLOC, int endLOC) {
		this.left = left;
		this.base = "";
		this.right = right;
		this.message = "";
		this.body = assembleBody();
		this.startLOC = startLOC;
		this.endLOC = endLOC;
	}

	public MergeConflict(FSTTerminal left, FSTTerminal right, String message) {
		this.left = IndentationUtils.indentFirstLine(left);
		this.right = IndentationUtils.indentFirstLine(right);
		this.base = "";
		this.message = message;
		this.body = assembleBody();
	}

	public boolean contains(String leftPattern, String rightPattern) {
		if (leftPattern.isEmpty() || rightPattern.isEmpty()) {
			return false;
		} else {
			leftPattern = (leftPattern.replaceAll("\\r\\n|\\r|\\n", "")).replaceAll("\\s+", "");
			rightPattern = (rightPattern.replaceAll("\\r\\n|\\r|\\n", "")).replaceAll("\\s+", "");
			String lefttrim = (this.left.replaceAll("\\r\\n|\\r|\\n", "")).replaceAll("\\s+", "");
			String righttrim = (this.right.replaceAll("\\r\\n|\\r|\\n", "")).replaceAll("\\s+", "");
			return (lefttrim.contains(leftPattern) && righttrim.contains(rightPattern));
		}
	}

	public void setOriginFiles(File left, File base, File right) {
		this.leftOriginFile = left;
		this.rightOriginFile = right;
		this.baseOriginFile = base;
	}

	public String getFullyQualifiedMergedClass() {
		return fullyQualifiedMergedClass;
	}

	public void setFullyQualifiedMergedClass(String fullyQualifiedMergedClass) {
		this.fullyQualifiedMergedClass = fullyQualifiedMergedClass;
	}

	@Override
	public String toString() {
		return this.body;
	}

	public String getLeft() {
		return left;
	}

	public String getRight() {
		return right;
	}

	/*
	 * public boolean containsRelaxed(String leftPattern, String rightPattern){
	 * if(leftPattern.isEmpty() || rightPattern.isEmpty()){ return false; } else {
	 * leftPattern =
	 * (leftPattern.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
	 * rightPattern =
	 * (rightPattern.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+",""); String
	 * lefttrim = (this.left.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
	 * String righttrim =
	 * (this.right.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
	 * 
	 * leftPattern = Util.removeReservedKeywords(leftPattern); rightPattern =
	 * Util.removeReservedKeywords(rightPattern); lefttrim =
	 * Util.removeReservedKeywords(lefttrim); righttrim =
	 * Util.removeReservedKeywords(righttrim);
	 * 
	 * return (lefttrim.contains(leftPattern) && righttrim.contains(rightPattern));
	 * } }
	 */
}
