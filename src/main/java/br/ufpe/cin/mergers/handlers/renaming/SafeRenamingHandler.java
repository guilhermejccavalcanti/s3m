package br.ufpe.cin.mergers.handlers.renaming;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.javatuples.Quartet;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.JavaCompiler;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;

/**
 * Default implementation of the renaming handler. It divides renaming or
 * deletion cases in two types: Single Renaming and Mutual Renaming.
 * 
 * @see #handleSingleRenaming(MergeContext, Quartet)
 * @see #handleMutualRenaming(MergeContext, Quartet)
 * 
 * @author João Victor (jvsfc@cin.ufpe.br)
 * @author Giovanni Barros (gaabs@cin.ufpe.br)
 */
public class SafeRenamingHandler implements RenamingHandler {

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes)
            throws TextualMergeException {

            // Only one developer renamed or deleted the method.    
            if(isSingleRenaming(scenarioNodes)) {
                handleSingleRenaming(context, scenarioNodes);
            }
            
            // Both of the developers renamed or deleted the method.
            else {
                handleMutualRenaming(context, scenarioNodes);
            }
    }

    private boolean isSingleRenaming(Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) {
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
    private void handleSingleRenaming(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) throws TextualMergeException {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        RenamingUtils.runTextualMerge(context, leftNode, baseNode, rightNode, mergeNode);
    }

    /**
     * When both developers renamed or deleted a method, we classify the renaming as mutual.
     * Then, we run a decision tree based on which renaming type each developer did.
     * 
     * For example, if both developers renamed without body changes, we check if they renamed
     * to the same signature. If true, we do nothing. Otherwise, we report a conflict.
     * 
     * @param context
     * @param scenarioNodes
     * @throws TextualMergeException
     */
    private void handleMutualRenaming(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) throws TextualMergeException {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        if (isRenamingWithoutBodyChanges(Side.LEFT, baseNode, context) && isRenamingWithoutBodyChanges(Side.RIGHT, baseNode, context)) {
            decideWhenBothRenamedWithoutBodyChanges(context, leftNode, rightNode, mergeNode);
        }

        else if (isRenamingWithoutBodyChanges(Side.LEFT, baseNode, context) && isDeletionOrRenamingWithBodyChanges(Side.RIGHT, baseNode, context)) {
            decideWhenTheyRenamedDifferently(context, leftNode, baseNode, rightNode, mergeNode, leftNode.getName(), context.getLeft());
        }

        else if (isDeletionOrRenamingWithBodyChanges(Side.LEFT, baseNode, context) && isRenamingWithoutBodyChanges(Side.RIGHT, baseNode, context)) {
            decideWhenTheyRenamedDifferently(context, leftNode, baseNode, rightNode, mergeNode, rightNode.getName(), context.getRight());
        }

        else if (isDeletionOrRenamingWithBodyChanges(Side.LEFT, baseNode, context) && isDeletionOrRenamingWithBodyChanges(Side.RIGHT, baseNode, context)) {
            decideWhenBothDeletedOrRenamedWithBodyChanges(context, leftNode, baseNode, rightNode, mergeNode);
        }
    }

    private void decideWhenBothRenamedWithoutBodyChanges(MergeContext context, FSTNode leftNode, FSTNode rightNode,
            FSTNode mergeNode) {
        if (RenamingUtils.haveEqualSignature(leftNode, rightNode))
            return;
        else
            RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode, mergeNode);
    }

    private void decideWhenTheyRenamedDifferently(MergeContext context, FSTNode leftNode, FSTNode baseNode,
            FSTNode rightNode, FSTNode mergeNode, String signature, File toCheckReferencesFile)
            throws TextualMergeException {
                
        if (RenamingUtils.haveEqualSignature(leftNode, rightNode)) {
            if (thereIsNewReference(toCheckReferencesFile, signature, context.getBase())) {
                RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode, mergeNode);
            } else {
                RenamingUtils.runTextualMerge(context, leftNode, baseNode, rightNode, mergeNode);
            }
        } else {
            RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode, mergeNode);
        }
    }

    private void decideWhenBothDeletedOrRenamedWithBodyChanges(MergeContext context, FSTNode leftNode, FSTNode baseNode,
            FSTNode rightNode, FSTNode mergeNode) throws TextualMergeException {
        if (RenamingUtils.haveEqualSignature(leftNode, rightNode))
            RenamingUtils.runTextualMerge(context, leftNode, baseNode, rightNode, mergeNode);
        else
            RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode, mergeNode);
    }

    private boolean isRenamingWithoutBodyChanges(Side renamingSide, FSTNode baseNode, MergeContext context) {
        return context.renamedWithoutBodyChanges.stream()
                .anyMatch(pair -> pair.getLeft().equals(renamingSide) && pair.getRight().equals(baseNode));
    }

    private boolean isDeletionOrRenamingWithBodyChanges(Side renamingSide, FSTNode baseNode, MergeContext context) {
        return context.deletedOrRenamedWithBodyChanges.stream()
                .anyMatch(pair -> pair.getLeft().equals(renamingSide) && pair.getRight().equals(baseNode));
    }

    private boolean thereIsNewReference(File contributionFile, String signature, File baseFile) {
        int numberBaseReferences = countReferences(baseFile, signature);
        int numberContributionReferences = countReferences(contributionFile, signature);
        return numberContributionReferences > numberBaseReferences;
    }

    private int countReferences(File file, String signature) {
        String programSource = FilesManager.readFileContent(file);
        CompilationUnit compilationUnit = new JavaCompiler().compile(programSource);
        
        List<org.eclipse.jdt.core.dom.ASTNode> instances = new ArrayList<ASTNode>();
        compilationUnit.accept(new ASTVisitor() {
            
            @Override
            public void endVisit(MethodInvocation node) {
                if (sameSignatureAs(node, signature))
                    instances.add(node);
            }

            private boolean sameSignatureAs(MethodInvocation node, String signature) {
                String[] nameAndArguments = signature.split("[\\(\\)]");
                String nodeMethodName = node.getName().toString();
                return nodeMethodName.equals(nameAndArguments[0]) && sameArgumentList(node.arguments(), nameAndArguments[1].split("-"));
            }

            private boolean sameArgumentList(List nodeArguments, String[] arguments) {
                for (int i = 0; i < nodeArguments.size(); i++) {
                    String typeName = ((Expression) nodeArguments.get(i)).resolveTypeBinding().getName();
                    if(!typeName.equals(arguments[i*2])) // Twice because the FST parser replicates the arguments.
                        return false;
                }
                return true;
            }

        });
        return instances.size();
    }    
    
}