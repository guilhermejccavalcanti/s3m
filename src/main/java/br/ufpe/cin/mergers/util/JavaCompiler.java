package br.ufpe.cin.mergers.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import br.ufpe.cin.files.FilesManager;
/**
 * Java compiler based on Eclipse JDT. 
 * @author Guilherme
 */
public class JavaCompiler {

	public List<IProblem> compilationProblems = new ArrayList<IProblem>();

	/**
	 * Compile the semistructured java code of a given MergeContext.
	 * @param context containing the semistructured java code.
	 * @return CompilationUnit representing the compiled code.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CompilationUnit compile(MergeContext context, Source source){
		String unitName 		= generateUnitName(context);
		String[] sources 		= findResources(context,"java");
		String[] classpaths 	= findResources(context,"jar");
		
		CompilationUnit cunit;
		switch (source) {
		case UNSTRUCTURED:
			cunit = compile(unitName,context.unstructuredOutput,sources,classpaths);
			break;
		case SEMISTRUCTURED:
			cunit = compile(unitName,context.semistructuredOutput,sources,classpaths);
			break;
		default:
			cunit = compile(unitName,context.semistructuredOutput,sources,classpaths);
			break;
		}
		
		this.compilationProblems= new ArrayList(Arrays.asList(cunit.getProblems()));
		return cunit;
	}
	/**
	 * Compiles a given java source code.
	 * @param javaSource
	 * @param unitName being compiled
	 * @return CompilationUnit representing the compiled code.
	 */
	@SuppressWarnings("unchecked")
	public CompilationUnit compile(String unitName, String javaSource, String[] sources, String[] classpaths){
		ASTParser parser = ASTParser.newParser(AST.JLS8);

		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);

		parser.setCompilerOptions(options);

		//setUnitName for resolve bindings
		parser.setUnitName(unitName);

		//setEnvironment for resolve bindings even if the args is empty
		//		String[] sources 	= sources;
		//		String[] classpath 	= classpaths;
		String[] encodings = fillEncondings(sources.length);
		parser.setEnvironment(classpaths, sources, encodings, true);

		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setBindingsRecovery(true);
		parser.setSource(javaSource.toCharArray());
		parser.setIgnoreMethodBodies(false);

		return (CompilationUnit)parser.createAST(null);
	}
	
	/**
	 * Compiles a given java source code.
	 * @param javaSource
	 * @param unitName being compiled
	 * @return CompilationUnit representing the compiled code.
	 */
	public CompilationUnit compile(String javaSource){
		return compile("UnitName", javaSource, new String[]{}, new String[]{});
	}

	/**
	 * Gets the name of the compilation unit that would hypothetically contains the source string to be compiled. Defaults to none (null). 
	 * The name of the compilation unit must be supplied for resolving bindings. This name should be suffixed by a dot ('.') followed 
	 * by one of the Java-like extensions and match the name of the main (public) class or interface declared in the source. This name must
	 * represent the full path of the unit inside the given project. For example, if the source declares a public class named "Foo" in a 
	 * project "P" where the source folder is the project itself, the name of the compilation unit must be "/P/Foo.java". If the source 
	 * declares a public class name "Bar" in a package "p1.p2" in a project "P" in a source folder "src",  the name of the compilation unit 
	 * must be "/P/src/p1/p2/Bar.java".
	 * @param context
	 * @return
	 */
	private String generateUnitName(MergeContext context) {
		String unitName = "anyname";
		try{
			String projectpath = FilesManager.estimateProjectRootFolderPath(context);
			String pattern = Pattern.quote(System.getProperty("file.separator"));
			String[] splt  = projectpath.split(pattern);
			String projectname = File.separator + splt[splt.length-1] + File.separator;

			File left  = context.getLeft();
			File base  = context.getBase();
			File right = context.getRight();

			if(left!=null){
				int srcidx = left.getAbsolutePath().indexOf(projectname);
				unitName = (left.getAbsolutePath().substring(srcidx, left.getAbsolutePath().length()));
			}else if(base!=null){
				int srcidx = base.getAbsolutePath().indexOf(projectname);
				unitName = (base.getAbsolutePath().substring(srcidx, base.getAbsolutePath().length()));
			} else if(right!=null){
				int srcidx = right.getAbsolutePath().indexOf(projectname);
				unitName = (right.getAbsolutePath().substring(srcidx, right.getAbsolutePath().length()));
			}
		}catch(Exception e){} //in case of any error gives a generic unit name
		return unitName;
	}

	/*		
		String unitName = "anyname";
		if(context.left!=null && context.right!=null){
			String diff = StringUtils.difference(context.left.getAbsolutePath(),context.right.getAbsolutePath());
			unitName = diff.substring(diff.indexOf(File.separator),diff.length());
		} else if(context.left!=null){
			String diff = StringUtils.difference(context.left.getAbsolutePath(),context.base.getAbsolutePath());
			unitName = diff.substring(diff.indexOf(File.separator),diff.length());
		} else {
			String diff = StringUtils.difference(context.base.getAbsolutePath(),context.right.getAbsolutePath());
			unitName = diff.substring(diff.indexOf(File.separator),diff.length());
		}
		return unitName;*/


	/**
	 * Gets a list o files path with the given extension, related to the given merge context. 
	 * @param context
	 * @param fileExtension
	 * @return list of files path
	 */
	private String[] findResources(MergeContext context, String fileExtension){
		//String projectpath = FilesManager.estimateProjectFolderPath(context);
		String[] projectpaths = FilesManager.estimateFilesProjectFolderPath(context);
		List<String> filespath= new ArrayList<String>();
		for(String path : projectpaths){
			if(!path.isEmpty()){
				List<File> files = (List<File>) FileUtils.listFiles(new File(path),new String[]{fileExtension}, true);
				for(File f: files){
					if(!filespath.contains(f.getParent())) 
						filespath.add(f.getParent());
				}
			}
		}
		return filespath.isEmpty()? (new String[] {""}) : filespath.toArray(new String[0]);
	}

	private String[] fillEncondings(int size){
		String[] encodings = new String[size];
		for(int i = 0; i<size; i++){
			encodings[i] = "UTF_8";
		}
		return encodings;
	}
}


