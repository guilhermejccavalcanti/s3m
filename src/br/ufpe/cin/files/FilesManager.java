package br.ufpe.cin.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * A set of utilities for managing files.
 * @author Guilherme
 */
public final class FilesManager {

	private FilesManager(){}

	/**
	 * Fills a list of matched files across the revisions involved in a three-way merge.
	 * @param leftDir
	 * @param baseDir
	 * @param rightDir
	 * @return list of tuples of matched files
	 */
	public static List<FilesTuple> fillFilesTuples(String leftDir, String baseDir, String rightDir){
		//avoiding file systems separator issues
		leftDir = FilenameUtils.separatorsToSystem(leftDir);
		baseDir = FilenameUtils.separatorsToSystem(baseDir);
		rightDir = FilenameUtils.separatorsToSystem(rightDir);

		List<FilesTuple> tuples = new ArrayList<FilesTuple>();

		//using linked lists as queues to avoid duplicates in the forthcoming steps
		LinkedList<String> filesPathFromBase = new LinkedList<String>(listFilesPath(baseDir));
		LinkedList<String> filesPathFromLeft = new LinkedList<String>(listFilesPath(leftDir));
		LinkedList<String> filesPathFromRight = new LinkedList<String>(listFilesPath(rightDir));

		//searches corresponding files begginning from files in the base version, followed by files in left version, and finally in files in right version
		searchCorrespondingFiles(leftDir, baseDir, rightDir, tuples, filesPathFromLeft, filesPathFromBase, filesPathFromRight,false,true,false);
		searchCorrespondingFiles(baseDir, leftDir, rightDir, tuples, filesPathFromBase, filesPathFromLeft, filesPathFromRight,true,false,false);
		searchCorrespondingFiles(leftDir, rightDir, baseDir, tuples, filesPathFromLeft, filesPathFromRight, filesPathFromBase,false,false,true);

		return tuples;
	}

	/**
	 * Lists all files path from a directory and its subdirectories.
	 * @param root directory path
	 * @return list containing all files path found
	 */
	public static List<String> listFilesPath(String directory){
		List<String> allFiles = new ArrayList<String>();
		File[] fList = (new File(directory)).listFiles();
		if(fList != null){
			for (File file : fList){
				if (file.isFile()){
					allFiles.add(file.getAbsolutePath());
				} else if (file.isDirectory()){
					allFiles.addAll(listFilesPath(file.getAbsolutePath()));
				}
			}
		}
		return allFiles;
	}

	/**
	 * Read the content of a given file.
	 * @param file to be read
	 * @return string content of the file, or null in case of errors.
	 */
	public static String readFileContent(File file){
		String content = null;
		try{
			BufferedReader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
			content = reader.lines().collect(Collectors.joining("\n"));
		}catch(Exception e){
			//System.err.println(e.getMessage());
		}
		return content;
	}

	/**
	 * Given a main list of files path, searches for corresponding files in other two given files path list.
	 * @param firstVariantDir root directory 
	 * @param mainDir root directory
	 * @param secondVariantDir root directory
	 * @param listOfTuplesToBeFilled 
	 * @param filesPathFromMainVariant 
	 * @param filesPathFromFirstVariant
	 * @param filesPathFromSecondVariant
	 */
	private static void searchCorrespondingFiles(String firstVariantDir, String mainDir,
			String secondVariantDir, List<FilesTuple> listOfTuplesToBeFilled,
			LinkedList<String> filesPathFromFirstVariant,
			LinkedList<String> filesPathFromMainVariant,
			LinkedList<String> filesPathFromSecondVariant,
			boolean isFirstVariantDriven,
			boolean isMainVariantDriven,
			boolean isSecondVariantDriven) {

		while(!filesPathFromMainVariant.isEmpty()){
			String baseFilePath = filesPathFromMainVariant.poll();
			String correspondingFirstVariantFilePath = replaceFilePath(baseFilePath,mainDir,firstVariantDir);
			String correspondingSecondVariantFilePath = replaceFilePath(baseFilePath,mainDir,secondVariantDir);

			File firstVariantFile = new File(correspondingFirstVariantFilePath);
			File baseFile = new File(baseFilePath);
			File secondVariantFile = new File(correspondingSecondVariantFilePath);

			if(!firstVariantFile.exists())firstVariantFile = null;
			if(!baseFile.exists())baseFile = null;
			if(!secondVariantFile.exists())secondVariantFile = null;

			//to fill the tuples parameters accordingly
			if(isFirstVariantDriven){
				FilesTuple tuple = new FilesTuple(baseFile, firstVariantFile, secondVariantFile);
				listOfTuplesToBeFilled.add(tuple);
			} else if(isMainVariantDriven){
				FilesTuple tuple = new FilesTuple(firstVariantFile, baseFile, secondVariantFile);
				listOfTuplesToBeFilled.add(tuple);
			} else if(isSecondVariantDriven){
				FilesTuple tuple = new FilesTuple(firstVariantFile, secondVariantFile, baseFile);
				listOfTuplesToBeFilled.add(tuple);
			}

			if(filesPathFromFirstVariant.contains(correspondingFirstVariantFilePath)){
				filesPathFromFirstVariant.remove(correspondingFirstVariantFilePath);
			}
			if(filesPathFromSecondVariant.contains(correspondingSecondVariantFilePath)){
				filesPathFromSecondVariant.remove(correspondingSecondVariantFilePath);
			}
		}
	}

	/**
	 * Replace files paths.
	 * @param filePath
	 * @param oldPattern
	 * @param newPattern
	 * @return replaced path
	 */
	private static String replaceFilePath(String filePath, String oldPattern, String newPattern){
		String result = (filePath.replace(oldPattern, newPattern));
		return result;

	}

	/**
	 * Writes the given content in the file of the given file path.
	 * @param filePath
	 * @param content
	 * @return boolean indicating the success of the write operation.
	 */
	public static boolean writeContent(String filePath, String content){
		if(!content.isEmpty()){
			try{
				File file = new File(filePath);
				if(!file.exists()){
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
				writer.write(content);
				writer.flush();	writer.close();
			} catch(Exception e){
				System.err.println(e.toString());
				return false;
			}
		}
		return true;
	}

	/**
	 * Validate the given files by verifying if they exist.
	 * In case of non-existing file, the execution terminates.
	 * @param files to be validated
	 */
	public static void validateFiles(File... files) {
		for(File f : files){
			if(f!=null && !f.exists()){
				System.err.println(f.getAbsolutePath()+" does not exists! Try again with a valid file.");
				System.exit(-1);
			}
		}
	}

	/**
	 * Returns a single line no spaced representation of a given string.
	 * @param content
	 * @return
	 */
	public static String getStringContentIntoSingleLineNoSpacing(String content) {
		return (content.replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
	}

	/**
	 * Extracts the merge conflicts of a string representation of merged code.
	 * @param mergedCode
	 * @return list o merge conflicts
	 */
	public static List<MergeConflict> extractMergeConflicts(String mergedCode){
		String CONFLICT_HEADER_BEGIN= "<<<<<<< LEFT";
		String CONFLICT_MID			= "=======";
		String CONFLICT_HEADER_END 	= ">>>>>>> RIGHT";
		String leftConflictingContent = "";
		String rightConflictingContent= "";
		boolean isConflictOpen		  = false;
		boolean isLeftContent		  = false;

		List<MergeConflict> mergeConflicts = new ArrayList<MergeConflict>();
		List<String> lines = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new StringReader(mergedCode));
		lines = reader.lines().collect(Collectors.toList());
		Iterator<String> itlines = lines.iterator();
		while(itlines.hasNext()){
			String line = itlines.next();
			if(line.contains(CONFLICT_HEADER_BEGIN)){
				isConflictOpen = true;
				isLeftContent  = true;
			}
			else if(line.contains(CONFLICT_MID)){
				isLeftContent = false;
			}
			else if(line.contains(CONFLICT_HEADER_END)) {
				MergeConflict mergeConflict = new MergeConflict(leftConflictingContent,rightConflictingContent);
				mergeConflicts.add(mergeConflict);
				//reseting the flags
				isConflictOpen	= false;
				isLeftContent   = false;
				leftConflictingContent = "";
				rightConflictingContent= "";
			} else {
				if(isConflictOpen){
					if(isLeftContent){leftConflictingContent+=line + "\n";
					}else{rightConflictingContent+=line + "\n";}
				}
			}
		}
		return mergeConflicts;
	}

	/**
	 * Finds a node with the content in the first parameter, 
	 * and replace the content with the content in the second parameter.
	 * @param tree
	 * @param newContent
	 */
	public static boolean findAndReplaceASTNodeContent(FSTNode node, String oldContent, String newContent) {
		if(node instanceof FSTNonTerminal){
			for (FSTNode child : ((FSTNonTerminal)node).getChildren()) {
				if(findAndReplaceASTNodeContent(child, oldContent, newContent)){
					return true;
				}
			}
		} else {
			if(node instanceof FSTTerminal){
				FSTTerminal terminal = (FSTTerminal) node;
				if(getStringContentIntoSingleLineNoSpacing(terminal.getBody())
						.equals(getStringContentIntoSingleLineNoSpacing(oldContent))){
					terminal.setBody(newContent);
					return true;
				}
			}
		}
		return false;		
	}

	/**
	 * Finds a node with the given content and
	 * deletes it from the given tree. 
	 * @param node represeting the AST.
	 * @param content
	 * @return if the deletion was successful
	 */
	public static boolean findAndDeleteASTNode (FSTNode node, String content){
		if(node instanceof FSTNonTerminal){
			for (FSTNode child : ((FSTNonTerminal)node).getChildren()) {
				if(findAndDeleteASTNode(child, content)){
					return true;
				}
			}
		} else {
			if(node instanceof FSTTerminal){
				if(getStringContentIntoSingleLineNoSpacing(((FSTTerminal) node).getBody())
						.equals(getStringContentIntoSingleLineNoSpacing(content))){
					FSTNonTerminal parent = ((FSTTerminal) node).getParent();
					parent.removeChild(node);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Estimates the root path of the project owning the files being merged.
	 * @param context
	 * @return projects file path, or "" in case not able to estimate
	 */
	public static String estimateProjectRootFolderPath(MergeContext context){
		File left  = context.getLeft();
		File base  = context.getBase();
		File right = context.getRight();
		String rootFolderPath = "";
		if(left!=null && left.getAbsolutePath().contains(File.separator+"src"+File.separator)){
			int srcidx = left.getAbsolutePath().indexOf(File.separator+"src"+File.separator);
			rootFolderPath = (left.getAbsolutePath().substring(0, srcidx))+File.separator;
		} else if(base!=null && base.getAbsolutePath().contains(File.separator+"src"+File.separator)){
			int srcidx = base.getAbsolutePath().indexOf(File.separator+"src"+File.separator);
			rootFolderPath = (base.getAbsolutePath().substring(0, srcidx))+File.separator;
		} else if(right!=null && right.getAbsolutePath().contains(File.separator+"src"+File.separator)){
			int srcidx = right.getAbsolutePath().indexOf(File.separator+"src"+File.separator);
			rootFolderPath = (right.getAbsolutePath().substring(0, srcidx))+File.separator;
		}
		return rootFolderPath;
	}

	/**
	 * Estimates the root path of the project for each of the three files being merged.
	 * @param context holding the three files being merged
	 * @return three projects file path, or "" in case not able to estimate
	 */
	public static String[] estimateFilesProjectFolderPath(MergeContext context){
		File left  = context.getLeft();
		File base  = context.getBase();
		File right = context.getRight();
		String rootFolderPathLeft = "";
		String rootFolderPathBase = "";
		String rootFolderPathRight= "";

		if(left!=null && left.getAbsolutePath().contains(File.separator+"src"+File.separator)){
			int srcidx = left.getAbsolutePath().indexOf(File.separator+"src"+File.separator);
			rootFolderPathLeft = (left.getAbsolutePath().substring(0, srcidx))+File.separator;
		}
		if(base!=null && base.getAbsolutePath().contains(File.separator+"src"+File.separator)){
			int srcidx = base.getAbsolutePath().indexOf(File.separator+"src"+File.separator);
			rootFolderPathBase = (base.getAbsolutePath().substring(0, srcidx))+File.separator;
		} 
		if(right!=null && right.getAbsolutePath().contains(File.separator+"src"+File.separator)){
			int srcidx = right.getAbsolutePath().indexOf(File.separator+"src"+File.separator);
			rootFolderPathRight= (right.getAbsolutePath().substring(0, srcidx))+File.separator;
		}
		return new String[] {rootFolderPathLeft, rootFolderPathBase, rootFolderPathRight};
	}

	/**
	 * Indents a given string representing Java source code.
	 * @param sourceCode
	 * @return indented sourceCode
	 */
	public static String indentCode(String sourceCode){
		String indentedCode = sourceCode;
		try{
			CompilationUnit indenter = JavaParser.parse(new ByteArrayInputStream(sourceCode.getBytes()));
			indentedCode = indenter.toString();
		} catch (Exception e){} //in case of any errors, returns the non-indented sourceCode
		return indentedCode;
	}

	/**
	 * Optimization that merges files equals or consistently changed. e.g left equals to right.
	 * @param left file
	 * @param base file
	 * @param right file
	 * @param outputFilePath
	 * @param context
	 * @return <b>true</b> if the files are equal or consistently changed, <b>false</b> otherwise
	 */
	public static boolean areFilesDifferent(File left, File base, File right,String outputFilePath, MergeContext context) {
		boolean result = true;
		
		//reading files content
		String auxleft = FilesManager.readFileContent(left);
		String leftcontent = (auxleft == null)?"":auxleft;
		String leftcontenttrim = (auxleft == null)?"":FilesManager.getStringContentIntoSingleLineNoSpacing(auxleft);
		
		String auxright = FilesManager.readFileContent(right);
		String rightcontent= (auxright == null)?"":auxright;
		String rightcontenttrim= (auxright== null)?"":FilesManager.getStringContentIntoSingleLineNoSpacing(auxright);
		
		String auxbase = FilesManager.readFileContent(base);
		String basecontentrim = (auxbase == null)?"":FilesManager.getStringContentIntoSingleLineNoSpacing(auxbase);
		
		//comparing files content
		if(basecontentrim.equals(leftcontenttrim)){
			//result is right
			context.semistructuredOutput = rightcontent;
			context.unstructuredOutput = rightcontent;
			result = false;
		} else if(basecontentrim.equals(rightcontenttrim)){
			//result is left
			context.semistructuredOutput = leftcontent;
			context.unstructuredOutput = leftcontent;
			result = false;
		} else if(leftcontenttrim.equals(rightcontenttrim)){
			//result is both left or right
			context.semistructuredOutput = leftcontent;
			context.unstructuredOutput = leftcontent;
			result = false;
		}
		return result;
	}
}
