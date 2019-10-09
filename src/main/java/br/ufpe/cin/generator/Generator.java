package br.ufpe.cin.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Class reponsible for dealing with the automatic generation 
 * of parsers trough <i>featureBNF</i> grammars. See the 
 * text files in the folder <i>/documentation</i> for more details.
 * @author Guilherme
 *
 */
public class Generator {

	public static void main(String[] args) {
		try{
			/* 1. read the featurebnf grammar in a .gcide file and converts into a javaCC grammar (.jj)
			   with all associated artefacts (pretty printer,etc) */
			new FSTgenTask().generate(
					"grammars/java18_merge_fst.gcide", 
					"src/main/java/br/ufpe/cin/generated/java18_merge.jj",
					"br.ufpe.cin.generated"
					);

			//2. generate a javaCC parser based on the .jj file
			new JavaCCTask().generate(
					"src/main/java/br/ufpe/cin/generated/java18_merge.jj",
					"src/main/java/br/ufpe/cin/generated/"
					);

			//3. test the generated artefacts
			new GeneratedParserTest().test(
					"br.ufpe.cin.generated.Java18MergeParser", 
					"CompilationUnit", 
					"grammars/java18_merge_fst_test.java"
					);
		
					alterInheritance();
		}catch(Exception e){
			System.err.println("Refresh the root project folder (F5). Try again.");
			e.printStackTrace();
		}
	}

	private static void alterInheritance() throws IOException {
		alterSimplePrintVisitorInheritance();
	}

	private static void alterSimplePrintVisitorInheritance() throws IOException {
		Path path = Paths.get("src/main/java/br/ufpe/cin/generated/SimplePrintVisitor.java");
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).contains("extends AbstractFSTPrintVisitor")) { // when we find the inheritance line
				lines.set(i, "public class SimplePrintVisitor extends S3MPrettyPrinter {"); // change the inheritance
				lines.set(i - 1, "import br.ufpe.cin.printers.S3MPrettyPrinter;"); // add an import to our class one line above
				for (int j = 1; j <= 6; j++) { // erase the next 6 lines, where there are unnecessary constructors
					lines.set(i + j, "");
				}
				break;
			}
		}

		Files.write(path, lines, StandardCharsets.UTF_8);
	}
}
