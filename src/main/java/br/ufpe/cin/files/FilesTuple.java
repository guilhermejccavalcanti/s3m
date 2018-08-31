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
	
	private String outputpath;
	
	public FilesTuple(File left, File base, File right){
		this.leftFile = left;
		this.baseFile = base;
		this.rightFile = right;		
	}
	
	public FilesTuple(File left, File base, File right, String outputpath){
		this.leftFile = left;
		this.baseFile = base;
		this.rightFile = right;
		this.outputpath= outputpath;
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
	
	public MergeContext getContext() {
		return context;
	}

	public void setContext(MergeContext context) {
		this.context = context;
	}
	
	public String getOutputpath() {
		return outputpath;
	}

	public void setOutputpath(String outputpath) {
		this.outputpath = outputpath;
	}

	@Override
	public String toString() {
		return "LEFT: " + ((leftFile == null) ? "empty" : leftFile.getAbsolutePath()) + "\n" +
			   "BASE: " + ((baseFile == null) ? "empty" : baseFile.getAbsolutePath()) + "\n" +
			   "RIGHT: "+ ((rightFile == null)? "empty" : rightFile.getAbsolutePath()) ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FilesTuple){
			FilesTuple tp = (FilesTuple) obj;
			
			String thisleftid = (this.leftFile !=null)?leftFile.getAbsolutePath():"";
			String thisbaseid = (this.baseFile !=null)?baseFile.getAbsolutePath():"";
			String thisrightid= (this.rightFile!=null)?rightFile.getAbsolutePath():"";
			
			String otherleftid = (tp.leftFile !=null)?tp.leftFile.getAbsolutePath():"";
			String otherbaseid = (tp.baseFile !=null)?tp.baseFile.getAbsolutePath():"";
			String otherrightid= (tp.rightFile!=null)?tp.rightFile.getAbsolutePath():"";
			
			return thisleftid.equals(otherleftid) && thisbaseid.equals(otherbaseid) && thisrightid.equals(otherrightid);
			
		} else {
			return false;
		}
	}
	
}
