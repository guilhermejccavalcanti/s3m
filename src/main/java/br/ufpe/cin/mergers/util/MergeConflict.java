package br.ufpe.cin.mergers.util;

import java.io.File;

import br.ufpe.cin.app.JFSTMerge;
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

	private int startLOC;
	private int endLOC;

	private File leftOriginFile;
	private File baseOriginFile;
	private File rightOriginFile;

	private String fullyQualifiedMergedClass;

	private static final String MINE_CONFLICT_MARKER = "<<<<<<< MINE";
	private static final String BASE_CONFLICT_MARKER = "||||||| BASE";
	private static final String CHANGE_CONFLICT_MARKER = "======= ";
	private static final String YOURS_CONFLICT_MARKER = ">>>>>>> YOURS";

	public MergeConflict(FSTNode left, FSTNode base, FSTNode right) {
		this.left = getNodeContent(left);
		this.base = getNodeContent(base);
		this.right = getNodeContent(right);
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
		conflict.append(MINE_CONFLICT_MARKER)
				.append('\n')
				.append(left)
				.append('\n');
		if(JFSTMerge.showBase) {
			conflict.append(BASE_CONFLICT_MARKER)
					.append('\n');
		}
		conflict.append(CHANGE_CONFLICT_MARKER)
				.append(message)
				.append('\n')
				.append(right)
				.append('\n')
				.append(YOURS_CONFLICT_MARKER);
		return conflict.toString();
	}

	public MergeConflict(FSTTerminal left, FSTTerminal base, FSTTerminal right, int startLOC, int endLOC) {
		this(left, base, right);
		this.startLOC = startLOC;
		this.endLOC = endLOC;
	}

	public MergeConflict(String left, String base, String right, int startLOC, int endLOC) {
		this.left = left;
		this.base = base;
		this.right = right;
		this.message = "";
		this.body = assembleBody();
		this.startLOC = startLOC;
		this.endLOC = endLOC;
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

	/**
	 * @return the LEFT conflicting content
	 */
	public String getLeft() {
		return left;
	}

	/**
	 * @return the BASE conflicting content
	 */
	public String getBase() {
		return base;
	}

	/**
	 * @return the YOURS conflicting content
	 */
	public String getRight() {
		return right;
	}

	/**
	 * @return the startLOC of the conflict
	 */
	public int getStartLOC() {
		return startLOC;
	}
	
	/**
	 * @return the endLOC
	 */
	public int getEndLOC() {
		return endLOC;
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
