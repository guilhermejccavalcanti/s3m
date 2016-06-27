package br.ufpe.cin.files;

import java.io.File;

import br.ufpe.cin.mergers.util.MergeContext;

/**
 * Represents a triple of matched files. That is,
 * files with the same name from the same directory in 
 * the three revisions being merged (three-way merge).
 * It also stores the output of both unstructured and 
 * semistructured merge.
 * @author Guilherme
 */
public class FilesTuple {
	private File leftFile;
	private File baseFile;
	private File rightFile;

	private MergeContext context;
	
	private File unstructuredOutput;
	private File semistructuredOutput;
	
	
	public FilesTuple(File left, File base, File right){
		this.leftFile = left;
		this.baseFile = base;
		this.rightFile = right;		
	}

	public File getLeftFile() {
		return leftFile;
	}

	public void setLeftFile(File leftFile) {
		this.leftFile = leftFile;
	}

	public File getBaseFile() {
		return baseFile;
	}

	public void setBaseFile(File baseFile) {
		this.baseFile = baseFile;
	}

	public File getRightFile() {
		return rightFile;
	}

	public void setRightFile(File rightFile) {
		this.rightFile = rightFile;
	}

	public File getUnstructuredOutput() {
		return unstructuredOutput;
	}

	public void setUnstructuredOutput(File unstructuredOutput) {
		this.unstructuredOutput = unstructuredOutput;
	}

	public File getSemistructuredOutput() {
		return semistructuredOutput;
	}

	public void setSemistructuredOutput(File semistructuredOutput) {
		this.semistructuredOutput = semistructuredOutput;
	}
	
	public MergeContext getContext() {
		return context;
	}

	public void setContext(MergeContext context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "LEFT: " + ((leftFile == null) ? "empty" : leftFile.getAbsolutePath()) + "\n" +
			   "BASE: " + ((baseFile == null) ? "empty" : baseFile.getAbsolutePath()) + "\n" +
			   "RIGHT: "+ ((rightFile == null)? "empty" : rightFile.getAbsolutePath()) ;
	}
}
