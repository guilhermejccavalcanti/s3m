package br.ufpe.cin.generator.st;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import br.ufpe.cin.files.st.FilesManager;
import br.ufpe.cin.printers.st.Prettyprinter;
import cide.gparser.CharStream;
import cide.gparser.OffsetCharStream;
import de.ovgu.cide.fstgen.ast.st.AbstractFSTParser;
import de.ovgu.cide.fstgen.ast.st.AbstractFSTPrintVisitor;
import de.ovgu.cide.fstgen.ast.st.FSTNode;
import de.ovgu.cide.fstgen.ast.st.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.st.FSTTerminal;

public class GeneratedParserTest {

	/**
	 * @param args
	 *            parserClass, mainProduction, targetFile
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, SecurityException,
			InvocationTargetException, NoSuchMethodException,
			FileNotFoundException {

		// if (args.length>3)

		// System.out.println(args[0]);
		// System.out.println(args[1]);
		// System.out.println(args[2]);

		String parserClassName = args[0];
		String mainProduction = args[1];
		String targetFileName = args[2];

		Class.forName("de.ovgu.cide.fstgen.ast.st.AbstractFSTParser");

		File inputFile = new File(targetFileName);
		if (!inputFile.exists())
			throw new FileNotFoundException(targetFileName);
		OffsetCharStream input = new OffsetCharStream(new FileInputStream(
				inputFile));

		Class<?> parserClass = Class.forName(parserClassName);
		Constructor<?> parserConstructor = parserClass
				.getConstructor(CharStream.class);
		AbstractFSTParser parser = (AbstractFSTParser) parserConstructor
				.newInstance(input);
		parserClass.getMethod(mainProduction, boolean.class).invoke(parser,
				new Boolean(false));

		System.out.println(parser.getRoot().printFST(0));

		String pkg = parserClassName.substring(0,
				parserClassName.lastIndexOf("."));
		Class<?> printerClass = Class.forName(pkg + ".SimplePrintVisitor");
		AbstractFSTPrintVisitor printer = (AbstractFSTPrintVisitor) printerClass
				.newInstance();
		parser.getRoot().accept(printer);
		System.out.println(printer.getResult());

	}

	public void test(String parserClassName, String mainProduction,
			String targetFileName) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, SecurityException,
			InvocationTargetException, NoSuchMethodException,
			FileNotFoundException, InterruptedException {

		// time to refresh folders' content
		Thread.sleep(2000);

		Class.forName("de.ovgu.cide.fstgen.ast.st.AbstractFSTParser");

		File inputFile = new File(targetFileName);
		if (!inputFile.exists())
			throw new FileNotFoundException(targetFileName);

		OffsetCharStream input = new OffsetCharStream(new FileInputStream(
				inputFile));

		Class<?> parserClass = Class.forName(parserClassName);
		Constructor<?> parserConstructor = parserClass.getConstructor(CharStream.class);
		AbstractFSTParser parser = (AbstractFSTParser) parserConstructor.newInstance(input);
		parserClass.getMethod(mainProduction, boolean.class).invoke(parser,	new Boolean(false));

		System.out.println(parser.getRoot().printFST(0));

		String pkg = parserClassName.substring(0,parserClassName.lastIndexOf("."));
		Class<?> printerClass = Class.forName(pkg + ".SimplePrintVisitor");
		AbstractFSTPrintVisitor printer = (AbstractFSTPrintVisitor) printerClass.newInstance();
		parser.getRoot().accept(printer);
		
		System.out.println(printer.getResult());
	}

	public void pruneTree(FSTNode node) {
		if (node instanceof FSTNonTerminal) {
			if (node.getType().equals("Expression")) {
				pruneExpressions((FSTNonTerminal) node);
			} else {
				for (FSTNode child : ((FSTNonTerminal) node).getChildren()) {
					pruneTree(child);
				}
			}
		} else {
			return;
		}
	}

	private void pruneExpressions(FSTNonTerminal expression) {
		FSTNode rexp = findRelevantExpression(expression);
		if (rexp != null) {
			rexp.setParent(expression);
			expression.getChildren().set(0, rexp);
		}
	}

	private FSTNode findRelevantExpression(FSTNonTerminal expression) {
		for (FSTNode subexpression : expression.getChildren()) {
			if (subexpression instanceof FSTNonTerminal) {
				if (((FSTNonTerminal) subexpression).getChildren().size() != 1) {
					pruneTree(subexpression);
					return subexpression;
				} else {
					return findRelevantExpression((FSTNonTerminal) subexpression);
				}
			} else {
				return subexpression;
			}
		}
		return null;
	}
}