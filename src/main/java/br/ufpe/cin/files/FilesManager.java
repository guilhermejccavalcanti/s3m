package br.ufpe.cin.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import br.ufpe.cin.generated.SimplePrintVisitor;
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
	@Deprecated
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

		//searches corresponding files beginning from files in the base version, followed by files in left version, and finally in files in right version
		searchCorrespondingFiles(leftDir, baseDir, rightDir, tuples, filesPathFromLeft, filesPathFromBase, filesPathFromRight,false,true,false);
		searchCorrespondingFiles(baseDir, leftDir, rightDir, tuples, filesPathFromBase, filesPathFromLeft, filesPathFromRight,true,false,false);
		searchCorrespondingFiles(leftDir, rightDir, baseDir, tuples, filesPathFromLeft, filesPathFromRight, filesPathFromBase,false,false,true);

		return tuples;
	}

	/**
	 * Fills a list of matched files across the revisions involved in a three-way merge.
	 * @param leftDir
	 * @param baseDir
	 * @param rightDir
	 * @param outputpath
	 * @param visitedPaths should be empty in the first iteration
	 * @return list of tuples of matched files
	 */
	public static List<FilesTuple> fillFilesTuples(String leftDir, String baseDir, String rightDir, String outputpath, List<String> visitedPaths){
		//avoiding revisiting directories
		String visitedPath = leftDir + baseDir + rightDir;
		visitedPaths.add(visitedPath);

		//avoiding file systems separator issues
		leftDir = FilenameUtils.separatorsToSystem(leftDir);
		baseDir = FilenameUtils.separatorsToSystem(baseDir);
		rightDir= FilenameUtils.separatorsToSystem(rightDir);

		List<FilesTuple> tuples = new ArrayList<FilesTuple>();

		//first, matches files of the three directories
		LinkedList<String> filesFromLeft = new LinkedList<String>(listFiles(leftDir));
		LinkedList<String> filesFromBase = new LinkedList<String>(listFiles(baseDir));
		LinkedList<String> filesFromRight= new LinkedList<String>(listFiles(rightDir));
		for(String l: filesFromLeft){
			File leftFile = new File(l);
			File baseFile = new File(baseDir + File.separator +leftFile.getName());
			File rightFile= new File(rightDir+ File.separator +leftFile.getName());

			if(!baseFile.exists()) baseFile = null;
			if(!rightFile.exists())rightFile= null;

			FilesTuple tp = new FilesTuple(leftFile, baseFile, rightFile, ((null!=outputpath&&!outputpath.isEmpty())?outputpath:rightDir));
			if(!tuples.contains(tp)) tuples.add(tp);
		}
		for(String b: filesFromBase){
			File baseFile = new File(b);
			File leftFile = new File(leftDir + File.separator +baseFile.getName());
			File rightFile= new File(rightDir+ File.separator +baseFile.getName());

			if(!leftFile.exists()) leftFile = null;
			if(!rightFile.exists())rightFile= null;

			FilesTuple tp = new FilesTuple(leftFile, baseFile, rightFile, ((null!=outputpath&&!outputpath.isEmpty())?outputpath:rightDir));
			if(!tuples.contains(tp)) tuples.add(tp);
		}
		for(String r: filesFromRight){
			File rightFile= new File(r);
			File baseFile = new File(baseDir + File.separator +rightFile.getName());
			File leftFile = new File(leftDir + File.separator +rightFile.getName());

			if(!baseFile.exists()) baseFile = null;
			if(!leftFile.exists()) leftFile = null;

			FilesTuple tp = new FilesTuple(leftFile, baseFile, rightFile, ((null!=outputpath&&!outputpath.isEmpty())?outputpath:rightDir));
			if(!tuples.contains(tp)) tuples.add(tp);
		}

		//second, run recursively over the subdirectories
		LinkedList<String> subdirectoriesFromLeft = new LinkedList<String>(listDirectories(leftDir));
		LinkedList<String> subdirectoriesFromBase = new LinkedList<String>(listDirectories(baseDir));
		LinkedList<String> subdirectoriesFromRight= new LinkedList<String>(listDirectories(rightDir));
		for(String sl : subdirectoriesFromLeft){
			String foldername = new File(sl).getName();

			if(!visitedPaths.contains(sl+ (baseDir+File.separator+foldername)+ (rightDir+File.separator+foldername))){
				List<FilesTuple> tps = fillFilesTuples(sl, (baseDir+File.separator+foldername), (rightDir+File.separator+foldername), outputpath,visitedPaths);
				tuples.removeAll(tps); //removing duplicates
				tuples.addAll(tps);
			}
		}
		for(String sb : subdirectoriesFromBase){
			String foldername = new File(sb).getName();

			if(!visitedPaths.contains((leftDir+File.separator+foldername)+ sb + (rightDir+File.separator+foldername))){
				List<FilesTuple> tps = fillFilesTuples((leftDir+File.separator+foldername), sb, (rightDir+File.separator+foldername), outputpath,visitedPaths);
				tuples.removeAll(tps);
				tuples.addAll(tps);
			}
		}
		for(String sr : subdirectoriesFromRight){
			String foldername = new File(sr).getName();

			if(!visitedPaths.contains((leftDir+File.separator+foldername)+ (baseDir+File.separator+foldername)+ sr)){
				List<FilesTuple> tps = fillFilesTuples((leftDir+File.separator+foldername), (baseDir+File.separator+foldername), sr, outputpath,visitedPaths);
				tuples.removeAll(tps);
				tuples.addAll(tps);
			}
		}
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
	 * Lists all files path from a directory.
	 * @param root directory path
	 * @return list containing all files path found
	 */
	public static List<String> listFiles(String directory){
		List<String> allFiles = new ArrayList<String>();
		File[] fList = (new File(directory)).listFiles();
		if(fList != null){
			for (File file : fList){
				if (file.isFile()){
					allFiles.add(file.getAbsolutePath());
				}
			}
		}
		return allFiles;
	}

	/**
	 * Lists all subdirectories from a given directory.
	 * @param root directory path
	 * @return list containing all files path found
	 */
	public static List<String> listDirectories(String directory){
		List<String> allFiles = new ArrayList<String>();
		File[] fList = (new File(directory)).listFiles();
		if(fList != null){
			for (File file : fList){
				if (file.isDirectory()){
					allFiles.add(file.getAbsolutePath());
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
	public static String readFileContent(File file) {
		String content = "";
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8)) {
			content = reader.lines().collect(Collectors.joining("\n"));
		} catch (Exception e) {
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
			} catch(NullPointerException ne){
				//empty, necessary for integration with git version control system
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
		String CONFLICT_HEADER_BEGIN= "<<<<<<< MINE";
		String CONFLICT_MID			= "=======";
		String CONFLICT_HEADER_END 	= ">>>>>>> YOURS";
		String leftConflictingContent = "";
		String rightConflictingContent= "";
		boolean isConflictOpen		  = false;
		boolean isLeftContent		  = false;
		int lineCounter				  = 0;
		int startLOC				  = 0;
		int endLOC				  	  = 0;

		List<MergeConflict> mergeConflicts = new ArrayList<MergeConflict>();
		List<String> lines = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new StringReader(mergedCode));
		lines = reader.lines().collect(Collectors.toList());
		Iterator<String> itlines = lines.iterator();
		while(itlines.hasNext()){
			String line = itlines.next();
			lineCounter++;
			if(line.contains(CONFLICT_HEADER_BEGIN)){
				isConflictOpen = true;
				isLeftContent  = true;
				startLOC = lineCounter;
			}
			else if(line.contains(CONFLICT_MID)){
				isLeftContent = false;
			}
			else if(line.contains(CONFLICT_HEADER_END)) {
				endLOC = lineCounter;
				MergeConflict mergeConflict = new MergeConflict(leftConflictingContent,rightConflictingContent,startLOC,endLOC);
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
	 * Returns the compatible node of <i>source</i> with <i>id</i>, or null if there isn't.
	 * @param source
	 * @param id
	 */
	public static FSTNode findNodeByID (FSTNode source, String id){
		if(source instanceof FSTNonTerminal){
			for (FSTNode child : ((FSTNonTerminal)source).getChildren()) {
				FSTNode result = findNodeByID(child, id);
				if(result!=null){
					return result;
				}
			}
		} else {
			if(source instanceof FSTTerminal){
				if(source.getType().equals("Id")){
					if(((FSTTerminal) source).getBody().equals(id)){
						return source;
					}
				}
			}
		}
		return null;
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
			CompilationUnit indenter = JavaParser.parse(new ByteArrayInputStream(sourceCode.getBytes()), StandardCharsets.UTF_8.displayName());
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

	/**
	 * Compute the similarity between two given strings based on the <i>Levenshtein Distance</i>.
	 * @param first
	 * @param second
	 * @return <b>double</b> similarity between 0.0 and 1.0
	 */
	public static double computeStringSimilarity(String first,String second) {
		@SuppressWarnings("unused")
		String longer = first, shorter = second;
		if (first.length() < second.length()) { // longer should always have greater length
			longer = second;
			shorter= first;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 1.0; /* both strings are zero length */
		}

		int levenshteinDistance = StringUtils.getLevenshteinDistance(first, second);
		return ((longerLength - levenshteinDistance)/(double) longerLength);
	}

	@SuppressWarnings("unused")
	private static String undoReplaceConflictMarkers(String indentedCode) {
		// dummy code for identation purposes
		indentedCode=indentedCode.replaceAll("int mmmm;", "<<<<<<< MINE");
		indentedCode=indentedCode.replaceAll("int bbbb;", "=======");
		indentedCode=indentedCode.replaceAll("int yyyy;", ">>>>>>> YOURS");
		return indentedCode;
	}

	@SuppressWarnings("unused")
	private static String replaceConflictMarkers(String sourceCode) {
		sourceCode = sourceCode.replaceAll("<<<<<<< MINE", "int mmmm;");
		sourceCode = sourceCode.replaceAll("=======", "int bbbb;");
		sourceCode = sourceCode.replaceAll(">>>>>>> YOURS", "int yyyy;");
		return sourceCode;
	}

	/**
	 * Pretty print of a given non-terminal node.
	 * @param node
	 */
	public static String prettyPrint(FSTNonTerminal node) {
		SimplePrintVisitor visitor = new SimplePrintVisitor();
		visitor.visit(node);
		return visitor.getResult().replaceAll(("  "), " ");
	}
}
