package br.ufpe.cin.generated;

import java.util.*;
import cide.gast.*;

import java.io.PrintStream;

import cide.languages.*;

import de.ovgu.cide.fstgen.ast.*;

public class SimplePrintVisitor extends AbstractFSTPrintVisitor  {
	public SimplePrintVisitor(PrintStream out) {
		super(out); generateSpaces=true;
	}
	public SimplePrintVisitor() {
		super(); generateSpaces=true;
	}
	public boolean visit(FSTNonTerminal nonTerminal) {
		if (nonTerminal.getType().equals("CompilationUnit")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PackageDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"ImportDeclaration")) {
				v.accept(this);
			}
			for (FSTNode v : getChildren(nonTerminal,"TypeDeclaration")) {
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AnnotationTypeDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			hintSingleSpace();
			{
				FSTNode v=getChild(nonTerminal, "AnnotationTypeDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("EnumDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			hintSingleSpace();
			printToken("enum");
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ImplementsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("{");
			{
				FSTNode v=getChild(nonTerminal, "EnumConstants");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "EnumBodyInternal");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("}");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ClassOrInterfaceDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "FinalOrAbstract");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterface");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "TypeParameters");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ExtendsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ImplementsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterfaceBody");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("EnumBodyInternal")) {
			printFeatures(nonTerminal,true);
			printToken(";");
			hintIncIndent();
			hintNewLine();
			for (FSTNode v : getChildren(nonTerminal,"ClassOrInterfaceBodyDeclaration")) {
				v.accept(this);
			}
			hintDecIndent();
			hintNewLine();
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("EnumConstants")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "EnumConstant").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken(",");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("EnumConstant")) {
			printFeatures(nonTerminal,true);
			for (FSTNode v : getChildren(nonTerminal,"Annotation")) {
				v.accept(this);
			}
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Arguments");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterfaceBody");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ClassOrInterfaceBody")) {
			printFeatures(nonTerminal,true);
			printToken("{");
			hintIncIndent();
			hintNewLine();
			for (FSTNode v : getChildren(nonTerminal,"ClassOrInterfaceBodyDeclaration")) {
				v.accept(this);
			}
			hintDecIndent();
			hintNewLine();
			printToken("}");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("InnerClassDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "FinalOrAbstract");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterface");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "TypeParameters");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ExtendsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ImplementsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterfaceBody");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("InnerEnumDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			hintSingleSpace();
			printToken("enum");
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ImplementsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("{");
			{
				FSTNode v=getChild(nonTerminal, "EnumConstants");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "EnumBodyInternal");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("}");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ConstructorDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ConstructorDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("FieldDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "FieldDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("InnerAnnotationTypeDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			hintSingleSpace();
			{
				FSTNode v=getChild(nonTerminal, "AnnotationTypeDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MethodDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "MethodDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("FieldDeclaration")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "VariableDeclarator").iterator();
			{
				FSTNode v=getChild(nonTerminal, "Type");
				if (v!=null) {
					v.accept(this);
				}
			}
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken(",");
				listElements.next().accept(this);
			}
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("VariableDeclarator")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "VariableDeclaratorId");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "VariableInitializer");
				if (v!=null) {
					printToken("=");
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("VariableInitializerArray")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ArrayInitializer");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("VariableInitializerExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ArrayInitializerInternal")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "VariableInitializer");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"VariableInitializer")) {
				printToken(",");
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MethodDeclaration")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "TypeParameters");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ResultType");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "MethodDeclarator");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "NameList");
				if (v!=null) {
					hintSingleSpace();
					printToken("throws");
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "MethodDeclarationBody");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MethodDeclarationBodyBlock")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Block");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MethodDeclarationBodyNone")) {
			printFeatures(nonTerminal,true);
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ConstructorDeclaration")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Annotation");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "TypeParameters");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ConstructorDeclarator");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "NameList");
				if (v!=null) {
					hintSingleSpace();
					printToken("throws");
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ConstructorDeclarationBody");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ExplicitConstructorInvocationThisArguments")) {
			printFeatures(nonTerminal,true);
			printToken("this");
			{
				FSTNode v=getChild(nonTerminal, "Arguments");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			hintNewLine();
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ExplicitConstructorInvocationThisTypeArguments")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "TypeArguments");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("this");
			{
				FSTNode v=getChild(nonTerminal, "Arguments");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			hintNewLine();
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ExplicitConstructorInvocationSuper")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PrimaryExpression");
				if (v!=null) {
					v.accept(this);
					printToken(".");
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "TypeArguments");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("super");
			{
				FSTNode v=getChild(nonTerminal, "Arguments");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			hintNewLine();
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ConstructorDeclarationBody")) {
			printFeatures(nonTerminal,true);
			printToken("{");
			hintIncIndent();
			hintNewLine();
			{
				FSTNode v=getChild(nonTerminal, "ExplicitConstructorInvocation");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"BlockStatement")) {
				v.accept(this);
			}
			hintDecIndent();
			printToken("}");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("Expression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ConditionalExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "AssignmentExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AssignmentExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "AssignmentOperator");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ConditionalExpression1")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ShortIf");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ConditionalExpression2")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ConditionalOrExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ShortIf")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ShortIfInternal");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(":");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ShortIfInternal")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ConditionalOrExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("?");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ConditionalOrExpression")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "ConditionalAndExpression").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken("||");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ConditionalAndExpression")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "InclusiveOrExpression").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken("&&");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("InclusiveOrExpression")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "ExclusiveOrExpression").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken("|");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ExclusiveOrExpression")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "AndExpression").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken("^");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AndExpression")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "EqualityExpression").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken("&");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("EqualityExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "InstanceOfExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"EqualityExpressionInternal")) {
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("EqualityExpressionInternal")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "EqualityOp");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "InstanceOfExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("InstanceOfExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "RelationalExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Type");
				if (v!=null) {
					printToken("instanceof");
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("RelationalExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ShiftExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"RelationalExpressionInternal")) {
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("RelationalExpressionInternal")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "RelationalOp");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ShiftExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ShiftExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "AdditiveExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"ShiftExpressionInternal")) {
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ShiftExpressionInternal")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ShiftOp");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "AdditiveExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AdditiveExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "MultiplicativeExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"AdditiveExpressionInternal")) {
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AdditiveExpressionInternal")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "AdditiveOp");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "MultiplicativeExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MultiplicativeExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "UnaryExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"MultiplicativeExpressionInternal")) {
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MultiplicativeExpressionInternal")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "MultiplicativeOp");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "UnaryExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("UnaryExpressionAdditive")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "AdditiveOp");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "UnaryExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("UnaryPreIncrement")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PreIncrementExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("UnaryPreDecrement")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PreDecrementExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("UnaryExpNotPlusMinus")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "UnaryExpressionNotPlusMinus");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PreIncrementExpression")) {
			printFeatures(nonTerminal,true);
			printToken("++");
			{
				FSTNode v=getChild(nonTerminal, "PrimaryExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PreDecrementExpression")) {
			printFeatures(nonTerminal,true);
			printToken("--");
			{
				FSTNode v=getChild(nonTerminal, "PrimaryExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("UnaryExpressionNotPlusMinusUnaryOp")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "UnaryOp");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "UnaryExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("UnaryExpressionNotPlusMinusCastExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "CastExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("UnaryExpressionNotPlusMinusPostfixExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PostfixExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PostfixExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PrimaryExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "PostfixOp");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("CastExpression1")) {
			printFeatures(nonTerminal,true);
			printToken("(");
			for (FSTNode v : getChildren(nonTerminal,"Annotation")) {
				v.accept(this);
			}
			{
				FSTNode v=getChild(nonTerminal, "Type");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			{
				FSTNode v=getChild(nonTerminal, "UnaryExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("CastExpression2")) {
			printFeatures(nonTerminal,true);
			printToken("(");
			for (FSTNode v : getChildren(nonTerminal,"Annotation")) {
				v.accept(this);
			}
			{
				FSTNode v=getChild(nonTerminal, "Type");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"ReferenceType")) {
				printToken("&");
				v.accept(this);
			}
			printToken(")");
			{
				FSTNode v=getChild(nonTerminal, "UnaryExpressionNotPlusMinus");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("CastExpression3")) {
			printFeatures(nonTerminal,true);
			printToken("(");
			for (FSTNode v : getChildren(nonTerminal,"Annotation")) {
				v.accept(this);
			}
			{
				FSTNode v=getChild(nonTerminal, "Type");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			{
				FSTNode v=getChild(nonTerminal, "UnaryExpressionNotPlusMinus");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryExpression")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PrimaryPrefix");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"PrimarySuffix")) {
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix4")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LambdaExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix5")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LambdaExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix6")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LambdaExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix7")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LambdaExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix8")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LambdaExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix9")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LambdaExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix10")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LambdaExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix11")) {
			printFeatures(nonTerminal,true);
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix12")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "AllocationExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimaryPrefix15")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ReferenceType");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "MethodReference");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("LambdaExpression1")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "VariableDeclaratorId");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("->");
			{
				FSTNode v=getChild(nonTerminal, "ExpressionOrBlock");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("LambdaExpression2")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "FormalParameters");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("->");
			{
				FSTNode v=getChild(nonTerminal, "ExpressionOrBlock");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("LambdaExpression3")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "VariableDeclaratorId").iterator();
			printToken("(");
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken(",");
				listElements.next().accept(this);
			}
			printToken(")");
			printToken("->");
			{
				FSTNode v=getChild(nonTerminal, "ExpressionOrBlock");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ExpressionOrBlock1")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ExpressionOrBlock2")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Block");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimarySuffix3")) {
			printFeatures(nonTerminal,true);
			printToken(".");
			{
				FSTNode v=getChild(nonTerminal, "AllocationExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimarySuffix4")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "MemberSelector");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimarySuffix5")) {
			printFeatures(nonTerminal,true);
			printToken("[");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("]");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PrimarySuffix7")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Arguments");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("Arguments")) {
			printFeatures(nonTerminal,true);
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "ArgumentList");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ArgumentList")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "Expression").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken(",");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AllocationExpressionArray")) {
			printFeatures(nonTerminal,true);
			hintSingleSpace();
			printToken("new");
			for (FSTNode v : getChildren(nonTerminal,"Annotation")) {
				v.accept(this);
			}
			{
				FSTNode v=getChild(nonTerminal, "PrimitiveType");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ArrayDimsAndInits");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AllocationExpressionType")) {
			printFeatures(nonTerminal,true);
			hintSingleSpace();
			printToken("new");
			for (FSTNode v : getChildren(nonTerminal,"Annotation")) {
				v.accept(this);
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterfaceType");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "TypeArguments");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "AllocationExpressionInit");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AllocationExpressionInitArrayDimsAndInits")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ArrayDimsAndInits");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AllocationExpressionInitArguments")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Arguments");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterfaceBody");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ArrayDimsAndInits1")) {
			printFeatures(nonTerminal,true);
			for (FSTNode v : getChildren(nonTerminal,"ArrayDims")) {
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ArrayDimsAndInits2")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ArrayInitializer");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ArrayDims")) {
			printFeatures(nonTerminal,true);
			printToken("[");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("]");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("LabeledStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LabeledStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AssertStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "AssertStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("BlockStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Block");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("StatementExp")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "StatementExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("SwitchStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "SwitchStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("IfStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "IfStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("WhileStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "WhileStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("DoStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "DoStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ForStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ForStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ReturnStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ReturnStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ThrowStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ThrowStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("SynchronizedStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "SynchronizedStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("TryStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "TryStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AssertStatement")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "Expression").iterator();
			printToken("assert");
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			if (listElements.hasNext()) {
				printToken(":");
				listElements.next().accept(this);
			}
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("LabeledStatement")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(":");
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("Block")) {
			printFeatures(nonTerminal,true);
			printToken("{");
			for (FSTNode v : getChildren(nonTerminal,"BlockStatement")) {
				v.accept(this);
			}
			printToken("}");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("BlockStatement")) {
			printFeatures(nonTerminal,true);
			hintNewLine();
			hintIncIndent();
			{
				FSTNode v=getChild(nonTerminal, "Annotation");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "BlockStatementInternal");
				if (v!=null) {
					v.accept(this);
				}
			}
			hintDecIndent();
			hintNewLine();
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("BlockClassOrInterfaceDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Annotation");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "FinalOrAbstract");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterface");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "TypeParameters");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ExtendsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ImplementsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterfaceBody");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("BlockLocalVariableDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LocalVariableDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("BlockAssertStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "AssertStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("InnerBlockStmt")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("LocalVariableDeclaration")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "VariableDeclarator").iterator();
			{
				FSTNode v=getChild(nonTerminal, "Annotation");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"FinalOrAnnotation")) {
				v.accept(this);
			}
			{
				FSTNode v=getChild(nonTerminal, "Type");
				if (v!=null) {
					v.accept(this);
				}
			}
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken(",");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PreIncrementStmtExp")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PreIncrementExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("PreDecrementStmtExp")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PreDecrementExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AssignmentStmtExp")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "PrimaryExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "StatementExpressionAssignment");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("Assignment")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "AssignmentOperator");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("SwitchStatement")) {
			printFeatures(nonTerminal,true);
			printToken("switch");
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			printToken("{");
			hintNewLine();
			hintIncIndent();
			for (FSTNode v : getChildren(nonTerminal,"SwitchStatementLabel")) {
				v.accept(this);
			}
			hintDecIndent();
			hintNewLine();
			printToken("}");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("SwitchStatementLabel")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "SwitchLabel");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"BlockStatement")) {
				v.accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("CaseSwitchExp")) {
			printFeatures(nonTerminal,true);
			printToken("case");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(":");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("IfStatement")) {
			printFeatures(nonTerminal,true);
			printToken("if");
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "IfStatementInternal");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("IfStatementInternal")) {
			printFeatures(nonTerminal,true);
			printToken("else");
			hintSingleSpace();
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("WhileStatement")) {
			printFeatures(nonTerminal,true);
			printToken("while");
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("DoStatement")) {
			printFeatures(nonTerminal,true);
			printToken("do");
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("while");
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ForStatement")) {
			printFeatures(nonTerminal,true);
			printToken("for");
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "ForStatementInternal");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ForEach")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Annotation");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Type");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(":");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ForTraditional")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ForInit");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			{
				FSTNode v=getChild(nonTerminal, "ForUpdate");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ForInit1")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "LocalVariableDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ForInit2")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "StatementExpressionList");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("StatementExpressionList")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "StatementExpression").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken(",");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ForUpdate")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "StatementExpressionList");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("BreakStatement")) {
			printFeatures(nonTerminal,true);
			printToken("break");
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ContinueStatement")) {
			printFeatures(nonTerminal,true);
			printToken("continue");
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ReturnStatement")) {
			printFeatures(nonTerminal,true);
			printToken("return");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ThrowStatement")) {
			printFeatures(nonTerminal,true);
			printToken("throw");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("SynchronizedStatement")) {
			printFeatures(nonTerminal,true);
			printToken("synchronized");
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("TryStatement")) {
			printFeatures(nonTerminal,true);
			printToken("try");
			{
				FSTNode v=getChild(nonTerminal, "ResourceSpecification");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Block");
				if (v!=null) {
					v.accept(this);
				}
			}
			for (FSTNode v : getChildren(nonTerminal,"CatchStatement")) {
				v.accept(this);
				hintNewLine();
			}
			{
				FSTNode v=getChild(nonTerminal, "FinallyStatement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("ResourceSpecification")) {
			printFeatures(nonTerminal,true);
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "Resources");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("Resources")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "Resource").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken(";");
				listElements.next().accept(this);
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("Resource")) {
			printFeatures(nonTerminal,true);
			for (FSTNode v : getChildren(nonTerminal,"FinalOrAnnotation")) {
				v.accept(this);
			}
			{
				FSTNode v=getChild(nonTerminal, "Type");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "VariableDeclaratorId");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("=");
			{
				FSTNode v=getChild(nonTerminal, "Expression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("CatchStatement")) {
			printFeatures(nonTerminal,true);
			printToken("catch");
			printToken("(");
			{
				FSTNode v=getChild(nonTerminal, "CatchBlockInternal");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(")");
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("SingleCatching")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "FormalParameter");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MultipleCatching")) {
			printFeatures(nonTerminal,true);
			Iterator<FSTNode> listElements = getChildren(nonTerminal, "ClassOrInterfaceType").iterator();
			if (listElements.hasNext()) {
				listElements.next().accept(this);
			}
			while (listElements.hasNext()) {
				printToken("|");
				listElements.next().accept(this);
			}
			{
				FSTNode v=getChild(nonTerminal, "VariableDeclaratorId");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("FinallyStatement")) {
			printFeatures(nonTerminal,true);
			printToken("finally");
			{
				FSTNode v=getChild(nonTerminal, "Statement");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MemberValueAnnotation")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Annotation");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MemberValueArrayInitl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "MemberValueArrayInitializer");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("MemberValueConditionalExp")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "ConditionalExpression");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AnnotationTypeDeclaration")) {
			printFeatures(nonTerminal,true);
			printToken("@");
			printToken("interface");
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("{");
			for (FSTNode v : getChildren(nonTerminal,"AnnotationTypeMemberDeclaration")) {
				v.accept(this);
			}
			hintNewLine();
			printToken("}");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AnnotationTypeMemberDeclaration1")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Type");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("(");
			printToken(")");
			{
				FSTNode v=getChild(nonTerminal, "DefaultValue");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken(";");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AnnotationInnerClassDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "FinalOrAbstract");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterface");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "TypeParameters");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ExtendsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ImplementsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ClassOrInterfaceBody");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AnnotationInnerEnumDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			hintSingleSpace();
			printToken("enum");
			{
				FSTNode v=getChild(nonTerminal, "Id");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "ImplementsList");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("{");
			{
				FSTNode v=getChild(nonTerminal, "EnumConstants");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "EnumBodyInternal");
				if (v!=null) {
					v.accept(this);
				}
			}
			printToken("}");
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AnnotationInnerTypeDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			hintSingleSpace();
			{
				FSTNode v=getChild(nonTerminal, "AnnotationTypeDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("AnnotationInnerFieldDecl")) {
			printFeatures(nonTerminal,true);
			{
				FSTNode v=getChild(nonTerminal, "Modifiers");
				if (v!=null) {
					v.accept(this);
				}
			}
			{
				FSTNode v=getChild(nonTerminal, "FieldDeclaration");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		if (nonTerminal.getType().equals("DefaultValue")) {
			printFeatures(nonTerminal,true);
			printToken("default");
			{
				FSTNode v=getChild(nonTerminal, "MemberValue");
				if (v!=null) {
					v.accept(this);
				}
			}
			printFeatures(nonTerminal,false);
			return false;
		}
		throw new RuntimeException("Unknown Non Terminal in FST "+nonTerminal);
	}
	protected boolean isSubtype(String type, String expectedType) {
		if (type.equals(expectedType)) return true;
		if (type.equals("ConditionalExpression1") && expectedType.equals("ConditionalExpression")) return true;
		if (type.equals("CastExpression3") && expectedType.equals("CastExpression")) return true;
		if (type.equals("PrimaryPrefix15") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("InnerEnumDecl") && expectedType.equals("ClassOrInterfaceBodyDeclaration")) return true;
		if (type.equals("LambdaExpression1") && expectedType.equals("LambdaExpression")) return true;
		if (type.equals("UnaryPreDecrement") && expectedType.equals("UnaryExpression")) return true;
		if (type.equals("MultipleCatching") && expectedType.equals("CatchBlockInternal")) return true;
		if (type.equals("PostfixOp1") && expectedType.equals("PostfixOp")) return true;
		if (type.equals("MultiplicativeOp1") && expectedType.equals("MultiplicativeOp")) return true;
		if (type.equals("PrimaryPrefix1") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("ConditionalExpression2") && expectedType.equals("ConditionalExpression")) return true;
		if (type.equals("LambdaExpression2") && expectedType.equals("LambdaExpression")) return true;
		if (type.equals("EnumDecl") && expectedType.equals("TypeDeclaration")) return true;
		if (type.equals("PrimaryPrefix16") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("PostfixOp2") && expectedType.equals("PostfixOp")) return true;
		if (type.equals("PrimaryPrefix2") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("Assignment") && expectedType.equals("StatementExpressionAssignment")) return true;
		if (type.equals("TypeArgument2") && expectedType.equals("TypeArgument")) return true;
		if (type.equals("RelationalOp1") && expectedType.equals("RelationalOp")) return true;
		if (type.equals("SwitchStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("AnnotationInnerFieldDecl") && expectedType.equals("AnnotationTypeMemberDeclaration")) return true;
		if (type.equals("DoStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("AssertStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("UnaryOp2") && expectedType.equals("UnaryOp")) return true;
		if (type.equals("TypeArgument1") && expectedType.equals("TypeArgument")) return true;
		if (type.equals("ClassOrInterfaceDecl") && expectedType.equals("TypeDeclaration")) return true;
		if (type.equals("RelationalOp2") && expectedType.equals("RelationalOp")) return true;
		if (type.equals("AllocationExpressionArray") && expectedType.equals("AllocationExpression")) return true;
		if (type.equals("TryStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("Modifier1") && expectedType.equals("Modifier")) return true;
		if (type.equals("Literal1") && expectedType.equals("Literal")) return true;
		if (type.equals("RelationalOp3") && expectedType.equals("RelationalOp")) return true;
		if (type.equals("Modifier13") && expectedType.equals("Modifier")) return true;
		if (type.equals("ExplicitConstructorInvocationThisTypeArguments") && expectedType.equals("ExplicitConstructorInvocation")) return true;
		if (type.equals("SynchronizedStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("AssignmentOperator2") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("PreDecrementStmtExp") && expectedType.equals("StatementExpression")) return true;
		if (type.equals("AnnotationInnerEnumDecl") && expectedType.equals("AnnotationTypeMemberDeclaration")) return true;
		if (type.equals("Modifier12") && expectedType.equals("Modifier")) return true;
		if (type.equals("MethodDeclarationBodyBlock") && expectedType.equals("MethodDeclarationBody")) return true;
		if (type.equals("RelationalOp4") && expectedType.equals("RelationalOp")) return true;
		if (type.equals("PrimarySuffix7") && expectedType.equals("PrimarySuffix")) return true;
		if (type.equals("CastLOOK_AHEAD3") && expectedType.equals("CastLOOK_AHEAD")) return true;
		if (type.equals("EmptyStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("AllocationExpressionType") && expectedType.equals("AllocationExpression")) return true;
		if (type.equals("AssignmentOperator3") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("WhileStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("UnaryOp1") && expectedType.equals("UnaryOp")) return true;
		if (type.equals("InnerAnnotationTypeDecl") && expectedType.equals("ClassOrInterfaceBodyDeclaration")) return true;
		if (type.equals("Modifier11") && expectedType.equals("Modifier")) return true;
		if (type.equals("PrimarySuffix6") && expectedType.equals("PrimarySuffix")) return true;
		if (type.equals("BooleanLiteral2") && expectedType.equals("BooleanLiteral")) return true;
		if (type.equals("FinalOrAnnotation1") && expectedType.equals("FinalOrAnnotation")) return true;
		if (type.equals("FinalOrAbstract1") && expectedType.equals("FinalOrAbstract")) return true;
		if (type.equals("SingleCatching") && expectedType.equals("CatchBlockInternal")) return true;
		if (type.equals("Annotation2") && expectedType.equals("Annotation")) return true;
		if (type.equals("Modifier10") && expectedType.equals("Modifier")) return true;
		if (type.equals("AnnoationEmptyDecl") && expectedType.equals("AnnotationTypeMemberDeclaration")) return true;
		if (type.equals("VariableInitializerArray") && expectedType.equals("VariableInitializer")) return true;
		if (type.equals("ResultType1") && expectedType.equals("ResultType")) return true;
		if (type.equals("PrimarySuffix5") && expectedType.equals("PrimarySuffix")) return true;
		if (type.equals("LabeledStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("MemberValueConditionalExp") && expectedType.equals("MemberValue")) return true;
		if (type.equals("PrimarySuffix3") && expectedType.equals("PrimarySuffix")) return true;
		if (type.equals("FinalOrAbstract2") && expectedType.equals("FinalOrAbstract")) return true;
		if (type.equals("FinalOrAnnotation2") && expectedType.equals("FinalOrAnnotation")) return true;
		if (type.equals("MethodDecl") && expectedType.equals("ClassOrInterfaceBodyDeclaration")) return true;
		if (type.equals("AllocationExpressionInitArrayDimsAndInits") && expectedType.equals("AllocationExpressionInit")) return true;
		if (type.equals("AssignmentOperator1") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("AnnotationInnerTypeDecl") && expectedType.equals("AnnotationTypeMemberDeclaration")) return true;
		if (type.equals("Annotation1") && expectedType.equals("Annotation")) return true;
		if (type.equals("ResultType2") && expectedType.equals("ResultType")) return true;
		if (type.equals("PrimarySuffix4") && expectedType.equals("PrimarySuffix")) return true;
		if (type.equals("PreIncrementStmtExp") && expectedType.equals("StatementExpression")) return true;
		if (type.equals("ForTraditional") && expectedType.equals("ForStatementInternal")) return true;
		if (type.equals("PrimitiveType1") && expectedType.equals("PrimitiveType")) return true;
		if (type.equals("PrimarySuffix2") && expectedType.equals("PrimarySuffix")) return true;
		if (type.equals("ShiftOp2") && expectedType.equals("ShiftOp")) return true;
		if (type.equals("MemberSelector1") && expectedType.equals("MemberSelector")) return true;
		if (type.equals("Modifier6") && expectedType.equals("Modifier")) return true;
		if (type.equals("AssignmentOperator12") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("InnerBlockStmt") && expectedType.equals("BlockStatementInternal")) return true;
		if (type.equals("Literal6") && expectedType.equals("Literal")) return true;
		if (type.equals("MemberValueArrayInitl") && expectedType.equals("MemberValue")) return true;
		if (type.equals("ForInit2") && expectedType.equals("ForInit")) return true;
		if (type.equals("AssignmentOperator7") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("UnaryExpressionNotPlusMinusUnaryOp") && expectedType.equals("UnaryExpressionNotPlusMinus")) return true;
		if (type.equals("CastLOOK_AHEADOp1") && expectedType.equals("CastLOOK_AHEADOp")) return true;
		if (type.equals("ShiftOp1") && expectedType.equals("ShiftOp")) return true;
		if (type.equals("BooleanLiteral1") && expectedType.equals("BooleanLiteral")) return true;
		if (type.equals("PrimarySuffix1") && expectedType.equals("PrimarySuffix")) return true;
		if (type.equals("Modifier7") && expectedType.equals("Modifier")) return true;
		if (type.equals("AssignmentOperator11") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("AssignmentOperator8") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("Annotation3") && expectedType.equals("Annotation")) return true;
		if (type.equals("CastLOOK_AHEADOp2") && expectedType.equals("CastLOOK_AHEADOp")) return true;
		if (type.equals("ForInit1") && expectedType.equals("ForInit")) return true;
		if (type.equals("EmptyTypeDecl") && expectedType.equals("TypeDeclaration")) return true;
		if (type.equals("PrimitiveType2") && expectedType.equals("PrimitiveType")) return true;
		if (type.equals("CastLOOK_AHEAD2") && expectedType.equals("CastLOOK_AHEAD")) return true;
		if (type.equals("Type1") && expectedType.equals("Type")) return true;
		if (type.equals("AssignmentOperator4") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("PrimaryPrefix8") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("ReturnStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("Modifier8") && expectedType.equals("Modifier")) return true;
		if (type.equals("ExplicitConstructorInvocationSuper") && expectedType.equals("ExplicitConstructorInvocation")) return true;
		if (type.equals("AssignmentOperator10") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("AnnotationTypeDecl") && expectedType.equals("TypeDeclaration")) return true;
		if (type.equals("CastLOOK_AHEADOp3") && expectedType.equals("CastLOOK_AHEADOp")) return true;
		if (type.equals("ReferenceType2") && expectedType.equals("ReferenceType")) return true;
		if (type.equals("PrimitiveType3") && expectedType.equals("PrimitiveType")) return true;
		if (type.equals("MemberValueAnnotation") && expectedType.equals("MemberValue")) return true;
		if (type.equals("CastLOOK_AHEAD1") && expectedType.equals("CastLOOK_AHEAD")) return true;
		if (type.equals("Type2") && expectedType.equals("Type")) return true;
		if (type.equals("MemberSelector2") && expectedType.equals("MemberSelector")) return true;
		if (type.equals("BreakStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("MethodDeclarationBodyNone") && expectedType.equals("MethodDeclarationBody")) return true;
		if (type.equals("AssignmentOperator5") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("PrimaryPrefix9") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("Modifier9") && expectedType.equals("Modifier")) return true;
		if (type.equals("UnaryExpressionAdditive") && expectedType.equals("UnaryExpression")) return true;
		if (type.equals("AssignmentStmtExp") && expectedType.equals("StatementExpression")) return true;
		if (type.equals("PrimaryPrefix10") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("AssignmentOperator6") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("UnaryExpNotPlusMinus") && expectedType.equals("UnaryExpression")) return true;
		if (type.equals("CastLOOK_AHEADOp4") && expectedType.equals("CastLOOK_AHEADOp")) return true;
		if (type.equals("AllocationExpressionInitArguments") && expectedType.equals("AllocationExpressionInit")) return true;
		if (type.equals("AnnotationTypeMemberDeclaration1") && expectedType.equals("AnnotationTypeMemberDeclaration")) return true;
		if (type.equals("PrimitiveType4") && expectedType.equals("PrimitiveType")) return true;
		if (type.equals("VariableInitializerExpression") && expectedType.equals("VariableInitializer")) return true;
		if (type.equals("Modifier2") && expectedType.equals("Modifier")) return true;
		if (type.equals("NewOrIdentifier2") && expectedType.equals("NewOrIdentifier")) return true;
		if (type.equals("PrimaryPrefix6") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("Increment") && expectedType.equals("StatementExpressionAssignment")) return true;
		if (type.equals("ContinueStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("UnaryExpressionNotPlusMinusCastExpression") && expectedType.equals("UnaryExpressionNotPlusMinus")) return true;
		if (type.equals("Literal2") && expectedType.equals("Literal")) return true;
		if (type.equals("WildcardBounds2") && expectedType.equals("WildcardBounds")) return true;
		if (type.equals("TypeArguments2") && expectedType.equals("TypeArguments")) return true;
		if (type.equals("PrimaryPrefix11") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("ConstructorDecl") && expectedType.equals("ClassOrInterfaceBodyDeclaration")) return true;
		if (type.equals("UnaryExpressionNotPlusMinusPostfixExpression") && expectedType.equals("UnaryExpressionNotPlusMinus")) return true;
		if (type.equals("CastLOOK_AHEADOp5") && expectedType.equals("CastLOOK_AHEADOp")) return true;
		if (type.equals("PrimitiveType5") && expectedType.equals("PrimitiveType")) return true;
		if (type.equals("BlockAssertStmt") && expectedType.equals("BlockStatementInternal")) return true;
		if (type.equals("ForStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("EmptyDecl") && expectedType.equals("ClassOrInterfaceBodyDeclaration")) return true;
		if (type.equals("Modifier3") && expectedType.equals("Modifier")) return true;
		if (type.equals("CastLOOK_AHEADOp6") && expectedType.equals("CastLOOK_AHEADOp")) return true;
		if (type.equals("PrimaryPrefix7") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("PrimaryPrefix12") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("BlockLocalVariableDecl") && expectedType.equals("BlockStatementInternal")) return true;
		if (type.equals("FieldDecl") && expectedType.equals("ClassOrInterfaceBodyDeclaration")) return true;
		if (type.equals("Literal3") && expectedType.equals("Literal")) return true;
		if (type.equals("WildcardBounds1") && expectedType.equals("WildcardBounds")) return true;
		if (type.equals("TypeArguments1") && expectedType.equals("TypeArguments")) return true;
		if (type.equals("Decrement") && expectedType.equals("StatementExpressionAssignment")) return true;
		if (type.equals("ExplicitConstructorInvocationThisArguments") && expectedType.equals("ExplicitConstructorInvocation")) return true;
		if (type.equals("BlockClassOrInterfaceDecl") && expectedType.equals("BlockStatementInternal")) return true;
		if (type.equals("BlockStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("VariableDeclaratorId3") && expectedType.equals("VariableDeclaratorId")) return true;
		if (type.equals("PrimitiveType6") && expectedType.equals("PrimitiveType")) return true;
		if (type.equals("ReferenceType1") && expectedType.equals("ReferenceType")) return true;
		if (type.equals("AdditiveOp1") && expectedType.equals("AdditiveOp")) return true;
		if (type.equals("CastLOOK_AHEADOp7") && expectedType.equals("CastLOOK_AHEADOp")) return true;
		if (type.equals("EqualityOp2") && expectedType.equals("EqualityOp")) return true;
		if (type.equals("ArrayDimsAndInits2") && expectedType.equals("ArrayDimsAndInits")) return true;
		if (type.equals("ForEach") && expectedType.equals("ForStatementInternal")) return true;
		if (type.equals("CastExpression1") && expectedType.equals("CastExpression")) return true;
		if (type.equals("InitializerDecl") && expectedType.equals("ClassOrInterfaceBodyDeclaration")) return true;
		if (type.equals("PrimaryPrefix13") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("ExpressionOrBlock1") && expectedType.equals("ExpressionOrBlock")) return true;
		if (type.equals("Modifier4") && expectedType.equals("Modifier")) return true;
		if (type.equals("UnaryPreIncrement") && expectedType.equals("UnaryExpression")) return true;
		if (type.equals("LambdaExpression3") && expectedType.equals("LambdaExpression")) return true;
		if (type.equals("AnnotationInnerClassDecl") && expectedType.equals("AnnotationTypeMemberDeclaration")) return true;
		if (type.equals("AssignmentOperator9") && expectedType.equals("AssignmentOperator")) return true;
		if (type.equals("Literal4") && expectedType.equals("Literal")) return true;
		if (type.equals("MultiplicativeOp3") && expectedType.equals("MultiplicativeOp")) return true;
		if (type.equals("PrimaryPrefix3") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("VariableDeclaratorId2") && expectedType.equals("VariableDeclaratorId")) return true;
		if (type.equals("PrimitiveType7") && expectedType.equals("PrimitiveType")) return true;
		if (type.equals("IfStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("AdditiveOp2") && expectedType.equals("AdditiveOp")) return true;
		if (type.equals("ShiftOp3") && expectedType.equals("ShiftOp")) return true;
		if (type.equals("ArrayDimsAndInits1") && expectedType.equals("ArrayDimsAndInits")) return true;
		if (type.equals("CastExpression2") && expectedType.equals("CastExpression")) return true;
		if (type.equals("EqualityOp1") && expectedType.equals("EqualityOp")) return true;
		if (type.equals("CastLOOK_AHEADOp8") && expectedType.equals("CastLOOK_AHEADOp")) return true;
		if (type.equals("NewOrIdentifier1") && expectedType.equals("NewOrIdentifier")) return true;
		if (type.equals("PrimaryPrefix5") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("PrimaryPrefix14") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("Modifier5") && expectedType.equals("Modifier")) return true;
		if (type.equals("ExpressionOrBlock2") && expectedType.equals("ExpressionOrBlock")) return true;
		if (type.equals("InnerClassDecl") && expectedType.equals("ClassOrInterfaceBodyDeclaration")) return true;
		if (type.equals("ThrowStmt") && expectedType.equals("Statement")) return true;
		if (type.equals("DefaultSwitch") && expectedType.equals("SwitchLabel")) return true;
		if (type.equals("MultiplicativeOp2") && expectedType.equals("MultiplicativeOp")) return true;
		if (type.equals("PrimaryPrefix4") && expectedType.equals("PrimaryPrefix")) return true;
		if (type.equals("Literal5") && expectedType.equals("Literal")) return true;
		if (type.equals("VariableDeclaratorId1") && expectedType.equals("VariableDeclaratorId")) return true;
		if (type.equals("PrimitiveType8") && expectedType.equals("PrimitiveType")) return true;
		if (type.equals("StatementExp") && expectedType.equals("Statement")) return true;
		if (type.equals("CaseSwitchExp") && expectedType.equals("SwitchLabel")) return true;
		return false;
	}
}
