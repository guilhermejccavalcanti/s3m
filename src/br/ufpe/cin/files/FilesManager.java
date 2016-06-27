package br.ufpe.cin.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

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
	 * @param directory root
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
			System.out.println(e.getMessage());
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
	 * Writes a content in the file of the given file path.
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
	
	public static void main(String[] args) {

		/*		FilesManager.fillFilesTuples(
				"C:\\Users\\Guilherme\\Google Drive\\Pós-Graduação\\Pesquisa\\Outros\\running_examples\\examples_nssmerge\\rev2\\left.java", 
				"C:/Users/Guilherme/Google Drive/Pós-Graduação/Pesquisa/Outros/running_examples/examples_nssmerge/rev2/base", 
				"C:/Users/Guilherme/Google Drive\\Pós-Graduação\\Pesquisa\\Outros\\running_examples\\examples_nssmerge\\rev2\\right");*/

	}
}
