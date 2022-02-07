package br.ufpe.cin.mergers.handlers.renaming;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.javatuples.Quartet;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.JavaCompiler;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import de.ovgu.cide.fstgen.ast.FSTNode;

/**
 * Default implementation of the renaming handler. It divides renaming or
 * deletion cases in two types: Single Renaming and Mutual Renaming.
 * 
 * @see #handleSingleRenaming(MergeContext, Quartet)
 * @see #handleDoubleRenaming(MergeContext, Quartet)
 * 
 * @author Guilherme Cavalcanti (gjcc@cin.ufpe.br)
 * @author João Victor (jvsfc@cin.ufpe.br)
 * @author Giovanni Barros (gaabs@cin.ufpe.br)
 */
public class SafelyMergeSimilarRenamingHandler implements RenamingHandler {

    @Override
    public void handle(
        MergeContext context,
        Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes
    ) throws TextualMergeException {
            // Only one developer renamed or deleted the method.    
            if(atMostSingleRenamingOrDeletion(scenarioNodes)) {
                runTextualMergeOnNodes(context, scenarioNodes);
            }
            
            // Both of the developers renamed or deleted the method.
            else {
                handleDoubleRenaming(context, scenarioNodes);
            }
    }

    private boolean atMostSingleRenamingOrDeletion(Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        return RenamingUtils.haveEqualSignature(leftNode, baseNode)
                || RenamingUtils.haveEqualSignature(rightNode, baseNode);
    }

    /**
     * When only one developer renamed or deleted the method (while the other edited its body),
     * we classify the renaming as single.
     * 
     * To solve this scenario, we run textual merge in the nodes' contents.
     * 
     * @param context
     * @param scenarioNodes
     * @throws TextualMergeException
     */
    private void runTextualMergeOnNodes(
        MergeContext context,
        Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes
    ) throws TextualMergeException {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        RenamingUtils.runTextualMerge(context, leftNode, baseNode, rightNode, mergeNode);
    }

    /**
     * When both developers renamed or deleted a method, we classify the renaming as double.
     * Then, we run a decision tree based on which renaming type each developer did.
     * 
     * For example, if both developers renamed without body changes, we check if they renamed
     * to the same signature. If true, we do nothing. Otherwise, we report a conflict.
     * 
     * To see the complete decision tree, please check documentation/Renaming-Handler-Table.png file.
     * 
     * @param context
     * @param scenarioNodes
     * @throws TextualMergeException
     */
    private void handleDoubleRenaming(
        MergeContext context,
        Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes
    ) throws TextualMergeException {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        if(RenamingUtils.haveDifferentSignature(leftNode, rightNode)) {
            RenamingUtils.generateMutualRenamingConflict(
                context, leftNode, baseNode, rightNode, mergeNode,
                "double renaming to different signatures"
            );
        } else if(RenamingUtils.haveDifferentBody(leftNode, rightNode)) {
            try {
                if(thereIsNewReference(leftNode, rightNode, context)) {
                    RenamingUtils.generateMutualRenamingConflict(
                        context, leftNode, baseNode, rightNode, mergeNode,
                        "addition of a new reference when both changed the method's body"
                    );
                } else {
                    RenamingUtils.runTextualMerge(context, leftNode, baseNode, rightNode, mergeNode);
                }
            } catch (CountReferencesException e) {
                if (textualHasConflictWithSignature(context, leftNode.getName())) {
                    RenamingUtils.generateMutualRenamingConflict(
                        context, leftNode, baseNode, rightNode, mergeNode,
                        "unable to count references to method signature, but textual reported conflict"
                    );
                } else {
                    RenamingUtils.runTextualMerge(context, leftNode, baseNode, rightNode, mergeNode);
                }
            }  
        }
    }

    private boolean thereIsNewReference(
        FSTNode leftNode,
        FSTNode rightNode,
        MergeContext context
    ) throws CountReferencesException {
        boolean thereIs = thereIsNewReference(leftNode, context.getLeft(), context);
        thereIs |= thereIsNewReference(rightNode, context.getRight(), context);
        return thereIs;
    }

    private boolean thereIsNewReference(
        FSTNode toNode,
        File inFile,
        MergeContext context
    ) throws CountReferencesException {
        String signature = toNode.getName();
        int numberBaseReferences = countReferences(context.getBase(), signature);
        int numberContributionReferences = countReferences(inFile, signature);
        return numberContributionReferences > numberBaseReferences;
    }

    private int countReferences(File file, String signature) throws CountReferencesException {
        String programSource = FilesManager.readFileContent(file);
        CompilationUnit compilationUnit = new JavaCompiler().compile(programSource);

        CountReferencesVisitor visitor = new CountReferencesVisitor(signature);
        return visitor.countReferences(compilationUnit);
    }

    private boolean textualHasConflictWithSignature(MergeContext context, String signature) {
        String textualOutput = context.unstructuredOutput;
        List<MergeConflict> textualConflicts = FilesManager.extractMergeConflicts(textualOutput);

        Pattern pattern = getMethodSignaturePattern(signature);
        for (MergeConflict conflict: textualConflicts) {
            Matcher matcher = pattern.matcher(conflict.getLeft());
            if (matcher.find()) return true;

            matcher = pattern.matcher(conflict.getRight());
            if (matcher.find()) return true;
        }

        return false;
    }

    private Pattern getMethodSignaturePattern(String signature) {
        String[] nameAndParameters = signature.split("[\\(\\)]");
        String methodName = nameAndParameters[0];
        String[] methodParameters = nameAndParameters[1].split("-");

        String[] noDuplicates = new String[methodParameters.length / 2];
        for (int i = 0; i < methodParameters.length; i += 2)
            noDuplicates[i / 2] = methodParameters[i];

        methodParameters = noDuplicates;

        String pattern = methodName + "\\s*\\(";
        for (int i = 0; i < methodParameters.length; i++) {
            if (i > 0) pattern += "\\s*,";
            pattern += "\\s*" + methodParameters[i] + "\\s+.+";
        }

        pattern += "\\s*\\)";
        return Pattern.compile(pattern);
    }

}

class CountReferencesVisitor extends ASTVisitor {
    private final String methodName;
    private final String[] methodParameters;
    private List<ASTNode> instances;

    public CountReferencesVisitor(String methodSignature) {
        String[] nameAndParameters = methodSignature.split("[\\(\\)]");
        this.methodName = nameAndParameters[0];
        this.methodParameters = nameAndParameters[1].split("-");
    }

    public int countReferences(CompilationUnit compilationUnit) throws CountReferencesException {
        try {
            instances = new ArrayList<ASTNode>();
            compilationUnit.accept(this);
            return instances.size();
        } catch (RuntimeException e) {
            // Parser may fail at identifying an expression's type at argumentsMatchParameters
            throw new CountReferencesException();
        }
    }

    @Override
    public void endVisit(MethodInvocation node) {
        if (sameSignature(node))
            instances.add(node);
    }

    private boolean sameSignature(MethodInvocation node) {
        String currentMethodName = node.getName().toString();
        return currentMethodName.equals(methodName) && argumentsMatchParameters(node.arguments());
    }

    private boolean argumentsMatchParameters(List arguments) {
        for (int i = 0; i < arguments.size(); i++) {
            Expression argument = (Expression) arguments.get(i);
            String typeName = argument.resolveTypeBinding().getName();

            // Twice because the FST parser replicates the parameters
            if (!typeName.equals(methodParameters[i * 2]))
                return false;
        }

        return true;
    }
}

class CountReferencesException extends Exception {}
