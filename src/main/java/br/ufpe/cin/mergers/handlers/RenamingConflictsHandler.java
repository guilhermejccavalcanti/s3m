package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Renaming or deletions conflicts happen when one developer edits a element renamed or deleted by other.
 * Semistructured merge is unable to detect such cases because it matches elements via its identifier, so
 * if a element is renamed or deleted it cannot match the elements anymore. This class overcomes this issue.
 *
 * @author Guilherme
 */
public final class RenamingConflictsHandler {
    private static final double BODY_SIMILARITY_THRESHOLD = 0.7;  //a typical value of 0.7 (up to 1.0) is used, increase it for a more accurate comparison, or decrease for a more relaxed one.

    private enum RenamingSide {LEFT, RIGHT}

    public static void handle(MergeContext context) {
        //when both developers rename the same method/constructor
        if (!JFSTMerge.keepBothVersionsOfRenamedMethod) handleMutualRenamings(context);

        //when one of the developers rename a method/constructor
        handleSingleRenamings(context);
    }

    private static void handleMutualRenamings(MergeContext context) {
        if (context.addedLeftNodes.isEmpty() || context.addedRightNodes.isEmpty()) return;

        List<FSTNode> leftNewMethodsOrConstructors = context.addedLeftNodes.stream().filter(m -> isMethodOrConstructorNode(m)).collect(Collectors.toList());
        List<FSTNode> rightNewMethodsOrConstructors = context.addedRightNodes.stream().filter(m -> isMethodOrConstructorNode(m)).collect(Collectors.toList());
        for (FSTNode left : leftNewMethodsOrConstructors) {
            for (FSTNode right : rightNewMethodsOrConstructors) {
                if (!left.getName().equals(right.getName())) { //only if the two declarations have different signatures
                    String leftBody = ((FSTTerminal) left).getBody();
                    leftBody = FilesManager.getStringContentIntoSingleLineNoSpacing(leftBody);
                    leftBody = removeSignature(leftBody);

                    String rightBody = ((FSTTerminal) right).getBody();
                    rightBody = FilesManager.getStringContentIntoSingleLineNoSpacing(rightBody);
                    rightBody = removeSignature(rightBody);

                    if (leftBody.equals(rightBody)) {//the methods have the same body, ignoring their signature
                        generateMutualRenamingConflict(context, ((FSTTerminal) left).getBody(), ((FSTTerminal) left).getBody(), ((FSTTerminal) right).getBody());
                    }
                    break;
                }
            }
        }
    }

    private static String removeSignature(String string) {
        string = string.replaceFirst("^.[^{]*(?=(\\{))", "");
        return string;
    }

    private static void handleSingleRenamings(MergeContext context) {
        if (context.possibleRenamedLeftNodes.isEmpty() && context.possibleRenamedRightNodes.isEmpty()) return;

        //possible renamings or deletions in left
        handleSingleRenamings(context, context.possibleRenamedLeftNodes, context.addedLeftNodes, RenamingSide.LEFT);

        //possible renamings or deletions in right
        handleSingleRenamings(context, context.possibleRenamedRightNodes, context.addedRightNodes, RenamingSide.RIGHT);
    }

    private static void handleSingleRenamings(MergeContext context, List<Pair<String, FSTNode>> possibleRenamedNodes, List<FSTNode> addedNodes, RenamingSide renamingSide) {
        for (Pair<String, FSTNode> tuple : possibleRenamedNodes) {
            if (!nodeHasConflict(tuple.getRight())) continue;

            String baseContent = tuple.getLeft();
            String currentNodeContent = ((FSTTerminal) tuple.getRight()).getBody(); //node content with conflict
            MergeConflict mergeConflict = FilesManager.extractMergeConflicts(currentNodeContent).get(0);
            String oppositeSideNodeContent = getMergeConflictContentOfOppositeSide(mergeConflict, renamingSide);

            if (JFSTMerge.keepBothVersionsOfRenamedMethod) {
                ((FSTTerminal) tuple.getRight()).setBody(oppositeSideNodeContent);
                continue;
            }

            List<Pair<Double, String>> similarNodes = new ArrayList<>(); //list of possible nodes renaming a previous one
            //1. getting similar nodes to fulfill renaming conflicts
            for (FSTNode newNode : addedNodes) { // a possible renamed node is seem as "new" node due to superimposition
                if (!isMethodOrConstructorNode(newNode)) continue;

                String possibleRenamingContent = ((FSTTerminal) newNode).getBody();
                double bodySimilarity = FilesManager.computeStringSimilarity(baseContent, possibleRenamingContent);
                if (bodySimilarity >= BODY_SIMILARITY_THRESHOLD) {
                    Pair<Double, String> tp = Pair.of(bodySimilarity, possibleRenamingContent);
                    similarNodes.add(tp);
                }
            }

            //2. checking if unstructured merge also reported the renaming conflict
            String signature = getSignature(baseContent);
            boolean hasUnstructuredMergeConflict = FilesManager.extractMergeConflicts(context.unstructuredOutput).stream()
                    .map(conflict -> FilesManager.getStringContentIntoSingleLineNoSpacing(conflict.body))
                    .anyMatch(conflict -> conflict.contains(signature));

            if (hasUnstructuredMergeConflict) {
                String possibleRenamingContent = getMostSimilarContent(similarNodes);
                generateRenamingConflict(context, currentNodeContent, possibleRenamingContent, oppositeSideNodeContent, renamingSide);
            } else { //do not report the renaming conflict
                ((FSTTerminal) tuple.getRight()).setBody(oppositeSideNodeContent);
            }
        }
    }

    private static String getSignature(String source) {
        String trim = FilesManager.getStringContentIntoSingleLineNoSpacing(source);
        String signatureTrimmed = trim.substring(0, (/*is interface?*/(trim.contains("{")) ? trim.indexOf("{") : trim.indexOf(";")));
        return signatureTrimmed;
    }

    private static String getMostSimilarContent(List<Pair<Double, String>> similarNodes) {
        if (!similarNodes.isEmpty()) {
            similarNodes.sort((n1, n2) -> n1.getLeft().compareTo(n2.getLeft()));
            return (similarNodes.get(similarNodes.size() - 1)).getRight();// the top of the list
        } else {
            return "";
        }
    }

    private static boolean nodeHasConflict(FSTNode node) {
        if (isMethodOrConstructorNode(node)) {
            String body = ((FSTTerminal) node).getBody();
            return body.contains("<<<<<<< MINE");
        }

        return false;
    }

    private static boolean isMethodOrConstructorNode(FSTNode node) {
        if (node instanceof FSTTerminal) {
            String nodeType = node.getType();
            return nodeType.equals("MethodDecl") || nodeType.equals("ConstructorDecl");
        }

        return false;
    }

    private static void generateRenamingConflict(MergeContext context, String currentNodeContent, String firstContent, String secondContent, RenamingSide renamingSide) {
        if (renamingSide == RenamingSide.LEFT) {//managing the origin of the changes in the conflict
            String aux = secondContent;
            secondContent = firstContent;
            firstContent = aux;
        }

        //statistics
        if (firstContent.isEmpty() || secondContent.isEmpty()) {
            context.deletionConflicts++;
        } else {
            context.renamingConflicts++;
        }

        //first creates a conflict
        MergeConflict newConflict = new MergeConflict(firstContent + '\n', secondContent + '\n');
        //second put the conflict in one of the nodes containing the previous conflict, and deletes the other node containing the possible renamed version
        FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, currentNodeContent, newConflict.body);
        if (renamingSide == RenamingSide.RIGHT) {
            FilesManager.findAndDeleteASTNode(context.superImposedTree, firstContent);
        } else {
            FilesManager.findAndDeleteASTNode(context.superImposedTree, secondContent);
        }
    }

    private static void generateMutualRenamingConflict(MergeContext context, String currentNodeContent, String firstContent, String secondContent) {
        //statistics
        context.renamingConflicts++;

        //first creates a conflict
        MergeConflict newConflict = new MergeConflict(firstContent + '\n', secondContent + '\n');

        //second put the conflict in one of the nodes containing the previous conflict, and deletes the other node containing the possible renamed version
        FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, currentNodeContent, newConflict.body);
        FilesManager.findAndDeleteASTNode(context.superImposedTree, secondContent);
    }

    private static String getMergeConflictContentOfOppositeSide(MergeConflict mergeConflict, RenamingSide side) {
        if (side == RenamingSide.LEFT) return mergeConflict.right;
        if (side == RenamingSide.RIGHT) return mergeConflict.left;

        return null;
    }
}
