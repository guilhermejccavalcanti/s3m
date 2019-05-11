package br.ufpe.cin.mergers.handlers.renaming;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Quartet;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;

/**
 * SafeRenamingHandler
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

    private void handleSingleRenaming(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) throws TextualMergeException {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        RenamingUtils.runTextualMerge(context, leftNode, baseNode, rightNode, mergeNode);
    }

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
        return numberContributionReferences > numberBaseReferences + 1; // The declaration is not present in the base,
                                                                        // so we add one as a 'handicap'.
    }

    private int countReferences(File file, String signature) {
        String fileSource = FilesManager.readFileContent(file);
        String methodName = signature.substring(0, signature.indexOf('('));
        Pattern pattern = Pattern.compile(methodName + "\\(.*\\)");
        Matcher matcher = pattern.matcher(fileSource);

        int numberReferences = 0;
        while (matcher.find())
            numberReferences++;
        return numberReferences;
    }
    
}