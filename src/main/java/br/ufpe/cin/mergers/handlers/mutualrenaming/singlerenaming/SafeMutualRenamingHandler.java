package br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;

import org.apache.commons.lang3.tuple.Pair;
import org.javatuples.Quartet;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SafeMutualRenamingHandler implements MutualRenamingHandler {
    public void handle(MergeContext context) {
        List<FSTNode> mutualRemovedMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.deletedBaseNodes);
        List<FSTNode> leftNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedLeftNodes);
        List<FSTNode> rightNewMethodsOrConstructors = RenamingUtils.getMethodsOrConstructors(context.addedRightNodes);

        if (mutualRemovedMethodsOrConstructors.isEmpty()) return;
        if (leftNewMethodsOrConstructors.isEmpty() || rightNewMethodsOrConstructors.isEmpty()) return;

        mutualRemovedMethodsOrConstructors.stream()
                .map(node -> getNodesWithMatchingBody(node, leftNewMethodsOrConstructors, rightNewMethodsOrConstructors))
                .filter(pair -> pair.getLeft() != null && pair.getRight() != null)
                .filter(pair -> RenamingUtils.haveDifferentSignature(pair.getLeft(), pair.getRight()))
                .forEach(pair -> {
                    RenamingUtils.generateMutualRenamingConflict(context, pair.getLeft(), pair.getRight(), null);
                    leftNewMethodsOrConstructors.remove(pair.getLeft());
                    rightNewMethodsOrConstructors.remove(pair.getRight());
                });
    }

    private Pair<FSTNode, FSTNode> getNodesWithMatchingBody(FSTNode baseRemovedNode, List<FSTNode> leftNodes,
                                                            List<FSTNode> rightNodes) {
        FSTNode leftNode = leftNodes.stream()
                .filter(node -> RenamingUtils.haveEqualBody(node, baseRemovedNode))
                .findFirst()
                .orElse(null);

        FSTNode rightNode = rightNodes.stream()
                .filter(node -> RenamingUtils.haveEqualBody(node, baseRemovedNode))
                .findFirst()
                .orElse(null);

        return Pair.of(leftNode, rightNode);
    }

    @Override
    public void handle(MergeContext context, Quartet<FSTNode, FSTNode, FSTNode, FSTNode> scenarioNodes) throws TextualMergeException {
        FSTNode leftNode = scenarioNodes.getValue0();
        FSTNode baseNode = scenarioNodes.getValue1();
        FSTNode rightNode = scenarioNodes.getValue2();
        FSTNode mergeNode = scenarioNodes.getValue3();

        if(isRenamingWithoutBodyChanges(Side.LEFT, baseNode, context) && isRenamingWithoutBodyChanges(Side.RIGHT, baseNode, context)) {
            decideWhenBothRenamedWithoutBodyChanges(context, leftNode, rightNode, mergeNode);
        }

        else if(isRenamingWithoutBodyChanges(Side.LEFT, baseNode, context) && isDeletionOrRenamingWithBodyChanges(Side.RIGHT, baseNode, context)) {
            decideWhenTheyRenamedDifferently(context, leftNode, baseNode, rightNode, mergeNode, leftNode.getName(), context.getLeft());
        }
        
        else if(isDeletionOrRenamingWithBodyChanges(Side.LEFT, baseNode, context) && isRenamingWithoutBodyChanges(Side.RIGHT, baseNode, context)) {
            decideWhenTheyRenamedDifferently(context, leftNode, baseNode, rightNode, mergeNode, rightNode.getName(), context.getRight());
        }

        else if(isDeletionOrRenamingWithBodyChanges(Side.LEFT, baseNode, context) && isDeletionOrRenamingWithBodyChanges(Side.RIGHT, baseNode, context)) {
            decideWhenBothDeletedOrRenamedWithBodyChanges(context, leftNode, baseNode, rightNode, mergeNode);
        }
    }

    private void decideWhenBothRenamedWithoutBodyChanges(MergeContext context, FSTNode leftNode, FSTNode rightNode, FSTNode mergeNode) {
        if (RenamingUtils.haveEqualSignature(leftNode, rightNode))
            return;
        else
            RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode, mergeNode);
    }

    private void decideWhenTheyRenamedDifferently(MergeContext context, FSTNode leftNode, FSTNode baseNode,
            FSTNode rightNode, FSTNode mergeNode, String signature, File toCheckReferencesFile) throws TextualMergeException {
        if (RenamingUtils.haveEqualSignature(leftNode, rightNode)) {
            if(thereIsNewReference(toCheckReferencesFile, signature, context.getBase()))
                RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode, mergeNode);
            else 
                RenamingUtils.runTextualMerge(context, leftNode, baseNode, rightNode, mergeNode);
        } else 
            RenamingUtils.generateMutualRenamingConflict(context, leftNode, rightNode, mergeNode);
    }

    private void decideWhenBothDeletedOrRenamedWithBodyChanges(MergeContext context, FSTNode leftNode, FSTNode baseNode, FSTNode rightNode, FSTNode mergeNode) throws TextualMergeException {
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
        return numberContributionReferences > numberBaseReferences + 1; // The declaration is not present in the base, so we add one as a 'handicap'.
    }

    private int countReferences(File file, String signature) {
        String fileSource = FilesManager.readFileContent(file);
        String methodName = signature.substring(0, signature.indexOf('('));
        Pattern pattern = Pattern.compile(methodName + "\\(.*\\)");
        Matcher matcher = pattern.matcher(fileSource);

        int numberReferences = 0;
        while(matcher.find()) numberReferences++;
        return numberReferences;
    }
}
