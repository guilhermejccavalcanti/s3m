package br.ufpe.cin.printers.st;

import java.io.File;
import java.util.List;

import br.ufpe.cin.app.st.STJFSTMerge;
import br.ufpe.cin.exceptions.st.PrintException;
import br.ufpe.cin.files.st.FilesManager;
import br.ufpe.cin.files.st.FilesTuple;
import br.ufpe.cin.generated.st.SimplePrintVisitorStructured;
import br.ufpe.cin.mergers.util.st.MergeContextSt;
import br.ufpe.cin.mergers.util.st.MergeScenario;
import de.ovgu.cide.fstgen.ast.st.FSTNode;
import de.ovgu.cide.fstgen.ast.st.FSTNonTerminal;

/**
 * Class responsible for converting ASTs into source code and
 * responsible for operations related to printing/generating merged code.
 * @author Guilherme
 */
public final class Prettyprinter {

	/**
	 * Converts a given tree into textual source code.
	 * @param tree
	 * @return textual representation of the given tree, or empty string in case of given empty tree.
	 */
	public static String print(FSTNode tree){
		//uncomment to print the AST
		//System.out.println(tree.printFST(0));

		String printable = "";
		//de.ovgu.cide.fstgen.parsers.generated_java18_merge.SimplePrintVisitor printer = new de.ovgu.cide.fstgen.parsers.generated_java18_merge.SimplePrintVisitor();
		SimplePrintVisitorStructured printer = new SimplePrintVisitorStructured();
		FSTNode root = getCompilationUnit(tree);
		if(root != null){
			root.accept(printer);
			printable = printer.getResult();
		}
		//printable = printable.trim().replaceAll(" +", " "); //fix for a bug in java18_merge.SimplePrintVisitor, not working 
		return printable;
	}

	/**
	 * Prints the merged code result of both unstructured and structured merge.
	 * @param context
	 */
	public static void printOnScreenMergedCode(MergeContextSt context) {
		System.out.println("MERGE OUTPUT:");
		System.out.println((context.structuredOutput.isEmpty()?"empty (deleted, inexistent or invalid merged files)\n":context.structuredOutput));

		/*		System.out.println("UNSTRUCTURED MERGE OUTPUT:");
		System.out.println((context.unstructuredOutput.isEmpty()?"empty (deleted, inexistent or invalid merged files)\n":context.unstructuredOutput));*/
	}

	/**
	 * Prints the merged code result of both unstructured and structured merge in the given
	 * output file.
	 * @param context
	 * @param outputFilePath of the merged file. 
	 * @throws PrintException in case cannot write output file.
	 */
	public static void generateMergedFile(MergeContextSt context, String outputFilePath) throws PrintException {
		if(outputFilePath != null){
			if(outputFilePath.isEmpty())outputFilePath = context.getRight().getAbsolutePath(); //merging mine(left) into yours(right)
			String structuredOutputFilePath 	= outputFilePath;
			String structuredMergeOutputContent = context.structuredOutput;
			boolean writeSucceed = FilesManager.writeContent(structuredOutputFilePath, structuredMergeOutputContent);
			if(writeSucceed && !STJFSTMerge.isGit){
				String unstructuredOutputFilePath  		= outputFilePath +".merge"; 
				String unstructuredMergeOutputContent 	= context.unstructuredOutput;
				writeSucceed = FilesManager.writeContent(unstructuredOutputFilePath, unstructuredMergeOutputContent);
			}
			if(!writeSucceed){
				throw new PrintException("Unable to manage merged output file!");
			}
		}
	}

	/**
	 * Prints the merged code of the given file tuple into a given directory.
	 * @param outputDirPath
	 * @param tuple
	 * @throws PrintException in case cannot write output file.
	 */
	public static void generateMergedTuple(FilesTuple tuple) throws PrintException {
		String outputDirPath = tuple.getOutputpath();
		if(outputDirPath != null && tuple.getContext()!=null){
			String fileNameExample;
			if(tuple.getBaseFile()!=null){
				fileNameExample = tuple.getBaseFile().getName();
			} else if(tuple.getLeftFile() != null){
				fileNameExample = tuple.getLeftFile().getName();
			} else {
				fileNameExample =tuple.getRightFile().getName();
			}
			String outputFilePath = outputDirPath+File.separator+fileNameExample;
			generateMergedFile(tuple.getContext(), outputFilePath);
		}
	}

	/**
	 * Create files with the resulting merged code of the given merge scenario.
	 * @throws PrintException 
	 */
	public static void generateMergedScenario(MergeScenario scenario) throws PrintException  {
		String mergedRevisionId = "rev_"+scenario.getLeftRevisionID()+"-"+scenario.getRightRevisionID();
		List<FilesTuple> tuples = scenario.getTuples();
		for(FilesTuple mergedTuple : tuples){
			String mergedDirectory = mergedTuple.getOutputpath().replace(scenario.getRightRevisionID(), mergedRevisionId); 
			mergedTuple.setOutputpath(mergedDirectory);
			generateMergedTuple(mergedTuple);
		}
	}

	/**
	 * Returns the first printable node of a AST, namely, the compilation unit.
	 * @param tree
	 * @return node representing the compilation unit, or null in case there is no compilation unit
	 */
	private static FSTNonTerminal getCompilationUnit(FSTNode tree){
		if(null != tree && tree instanceof FSTNonTerminal){
			FSTNonTerminal node = (FSTNonTerminal)tree;
			if(node.getType().equals("CompilationUnit")){
				return node;
			} else {
				for(FSTNode child : node.getChildren()){
					if(child.getType().equals("CompilationUnit")){
						return (FSTNonTerminal) child;
					}
				}
				return null;
			}
		} else {
			return null;
		}
		//return node.getChildren().isEmpty()? null : getCompilationUnit(node.getChildren().get(1));
	}
}