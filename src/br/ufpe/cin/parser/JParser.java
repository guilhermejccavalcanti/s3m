package br.ufpe.cin.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FilenameUtils;

import br.ufpe.cin.generated.Java18MergeParser;
import cide.gparser.OffsetCharStream;
import cide.gparser.ParseException;
import cide.gparser.TokenMgrError;
import de.ovgu.cide.fstgen.ast.FSTFeatureNode;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

/**
 * Class responsible for parsing java files, based on a 
 * <i>featurebnf</i> Java 1.8 annotated grammar: 
 * {@link http://tinyurl.com/java18featurebnf}
 * For more information, see the documents in <i>guides</i> package.
 * @author Guilherme
 */
public class JParser {

	/**
	 * Parses a given .java file
	 * @param javaFile
	 * @return ast representing the java file
	 * @throws ParseException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public FSTNode parse(File javaFile) throws ParseException, UnsupportedEncodingException, FileNotFoundException, TokenMgrError {
		FSTFeatureNode generatedAst = new FSTFeatureNode("");//root node
		if(isValidFile(javaFile)){
			System.out.println("Parsing: " + javaFile.getAbsolutePath());
			//Java18MergeParser parser = new Java18MergeParser(new OffsetCharStream(new FileInputStream(javaFile)));
			Java18MergeParser parser = new Java18MergeParser(new OffsetCharStream(new InputStreamReader(new FileInputStream(javaFile),"UTF8")));
			parser.CompilationUnit(false);
			generatedAst.addChild(new FSTNonTerminal("Java-File", javaFile.getName()));
			generatedAst.addChild(parser.getRoot());
		} 
		return generatedAst;
	}

	/**
	 * Checks if the given file is adequate for parsing.
	 * @param file to be parsed
	 * @return true if the file is appropriated, or false
	 */
	private boolean isValidFile(File file) {
		return file != null && file.exists() && isJavaFile(file);
	}

	/**
	 * Checks if a given file is a .java file.
	 * @param file
	 * @return true in case file extension is <i>java</i>, or false
	 */
	private boolean isJavaFile(File file){
		return FilenameUtils.getExtension(file.getAbsolutePath()).equalsIgnoreCase("java");
	}

	/*	public static void main(String[] args) {
		new JParser().parse(new File("C:\\GGTS\\workspace\\ssmerge\\fstgen\\test\\Test.java"));
	}*/
}