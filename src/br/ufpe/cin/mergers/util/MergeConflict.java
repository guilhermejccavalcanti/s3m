package br.ufpe.cin.mergers.util;

/**
 * Class representing a textual merge conflict.
 * @author Guilherme
 */

public class MergeConflict {

	public String left;
	public String base;
	public String right;
	public String body;

	public MergeConflict(String leftConflictingContent,	String rightConflictingContent) {
		this.left  = leftConflictingContent;
		this.right = rightConflictingContent;
		this.body  ="<<<<<<< LEFT\n"+
				    leftConflictingContent+
				    "=======\n"+
				    rightConflictingContent+
				    ">>>>>>> RIGHT";
	}
	
	public MergeConflict(String leftConflictingContent,	String rightConflictingContent, String message) {
		this.left  = leftConflictingContent;
		this.right = rightConflictingContent;
		this.body  ="<<<<<<< LEFT\n"+
				    leftConflictingContent+
				    "======= "+ message + " \n" +
				    rightConflictingContent+
				    ">>>>>>> RIGHT";
	}
	
	
	public boolean contains(String leftPattern, String rightPattern){
		if(leftPattern.isEmpty() || rightPattern.isEmpty()){
			return false;
		} else {
			leftPattern  = (leftPattern.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			rightPattern = (rightPattern.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			String lefttrim  = (this.left.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			String righttrim = (this.right.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			return (lefttrim.contains(leftPattern) && righttrim.contains(rightPattern));
		}
	}
	
	@Override
	public String toString() {
		return this.body;
	}
	
/*	public boolean containsRelaxed(String leftPattern, String rightPattern){
		if(leftPattern.isEmpty() || rightPattern.isEmpty()){
			return false;
		} else {
			leftPattern  	 = (leftPattern.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			rightPattern 	 = (rightPattern.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			String lefttrim  = (this.left.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			String righttrim = (this.right.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			
			leftPattern 	= Util.removeReservedKeywords(leftPattern);
			rightPattern 	= Util.removeReservedKeywords(rightPattern);
			lefttrim 		= Util.removeReservedKeywords(lefttrim);
			righttrim 		= Util.removeReservedKeywords(righttrim);
			
			return (lefttrim.contains(leftPattern) && righttrim.contains(rightPattern));
		}
	}*/
}
