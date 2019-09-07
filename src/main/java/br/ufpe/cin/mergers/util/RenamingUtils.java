package br.ufpe.cin.mergers.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.TextualMerge;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class RenamingUtils {
    public static boolean hasUnstructuredMergeConflict(MergeContext context, String baseContent) {
        String signature = getTrimmedSignature(baseContent);

        return FilesManager.extractMergeConflicts(context.unstructuredOutput).stream()
                .map(conflict -> FilesManager.getStringContentIntoSingleLineNoSpacing(conflict.toString()))
                .anyMatch(conflict -> conflict.contains(signature));
    }

    public static String getMostSimilarNodeContent(String baseContent, FSTNode currentNode, List<FSTNode> addedNodes) {
        List<Pair<Double, String>> similarNodes = getSimilarNodes(baseContent, currentNode, addedNodes);

        return getMostSimilarContent(similarNodes);
    }

    public static List<Pair<Double, String>> getSimilarNodes(String baseContent, FSTNode currentNode, List<FSTNode> addedNodes) {
        //list of possible nodes renaming a previous one
        List<Pair<Double, String>> similarNodes = new ArrayList<>();

        //1. getting similar nodes to fulfill renaming conflicts
        for (FSTNode newNode : addedNodes) { // a possible renamed node is seem as "new" node due to superimposition
            if (!isMethodOrConstructorNode(newNode)) continue;
            if (!haveSameParent(newNode, currentNode)) continue;

            String possibleRenamingContent = ((FSTTerminal) newNode).getBody();
            double bodySimilarity = FilesManager.computeStringSimilarity(baseContent, possibleRenamingContent);
            if (bodySimilarity >= JFSTMerge.RENAMING_SIMILARITY_THRESHOLD) {
                Pair<Double, String> tp = Pair.of(bodySimilarity, possibleRenamingContent);
                similarNodes.add(tp);
            }
        }

        return similarNodes;
    }

    public static String getMostSimilarContent(List<Pair<Double, String>> similarNodes) {
        return similarNodes.stream()
                .max(Comparator.comparing(Pair::getLeft))
                .map(Pair::getRight)
                .orElse(StringUtils.EMPTY);
    }

    public static String getTrimmedSignature(String source) {
        String trimmedSource = FilesManager.getStringContentIntoSingleLineNoSpacing(source);
        return getSignature(trimmedSource);
    }

    public static String getSignature(String source) {
        return source.substring(0, hasDefinedBody(source) ? source.indexOf("{") : source.indexOf(";"));
    }

    public static boolean hasDefinedBody(String methodSource) {
        return methodSource.contains("{");
    }

    public static String getMethodBodyWithoutWhitespaces(FSTNode node) {
        if(isMethodOrConstructorNode(node))
            return StringUtils.deleteWhitespace(((FSTTerminal) node).getDeclarationBody());
        
        return StringUtils.EMPTY;
    }

    public static String removeSignature(String string) {
        if (!hasDefinedBody(string)) return StringUtils.EMPTY;

        return string.replaceFirst("^.[^{]*(?=(\\{))", "");
    }

    public static boolean nodeHasConflict(FSTNode node) {
        if (isMethodOrConstructorNode(node)) {
            String body = ((FSTTerminal) node).getBody();
            return body.contains("<<<<<<< MINE");
        }

        return false;
    }

    public static boolean isMethodOrConstructorNode(FSTNode node) {
        return isMethod(node) || isConstructor(node);
    }

    private static boolean isMethod(FSTNode node) {
        return node instanceof FSTTerminal && node.getType().equals("MethodDecl");
    }

    private static boolean isConstructor(FSTNode node) {
        return node instanceof FSTTerminal && node.getType().equals("ConstructorDecl");
    }

    public static boolean haveSameParent(FSTNode left, FSTNode right) {
        return left.getParent().equals(right.getParent());
    }

    public static void generateMutualRenamingConflict(MergeContext context, FSTNode leftNode, FSTNode baseNode, FSTNode rightNode, FSTNode mergeNode) {
        MergeConflict conflict = new MergeConflict(leftNode, baseNode, rightNode);
        ((FSTTerminal) mergeNode).setBody(conflict.toString());

        context.renamingConflicts++;
        removeUnmmatchedNode(context.superImposedTree, leftNode, rightNode, mergeNode);
    }

    public static void removeUnmmatchedNode(FSTNode mergeTree, FSTNode leftNode, FSTNode rightNode, FSTNode mergeNode) {
        if(equalIfExists(leftNode, mergeNode) && !equalIfExists(rightNode, leftNode))
            Traverser.removeNode(rightNode, mergeTree);
    }

    private static boolean equalIfExists(FSTNode node1, FSTNode node2) {
        return node1 != null && node1.equals(node2);
    }

    public static String getMergeConflictContentOfOppositeSide(MergeConflict mergeConflict, Side side) {
        if (side == Side.LEFT) return mergeConflict.getRight();
        if (side == Side.RIGHT) return mergeConflict.getLeft();

        return null;
    }

    public static List<FSTNode> getMethodsOrConstructors(List<FSTNode> nodes) {
        return nodes.stream()
                .filter(RenamingUtils::isMethodOrConstructorNode)
                .collect(Collectors.toList());
    }

    public static boolean haveDifferentSignature(FSTNode left, FSTNode right) {
        return !haveEqualSignature(left, right);
    }

    public static boolean haveEqualSignature(FSTNode left, FSTNode right) {
        return left != null && right != null && left.getName().equals(right.getName());
    }

    public static boolean haveEqualSignatureButName(FSTNode left, FSTNode right) {
        String[] leftArgumentTypes = getMethodArgumentTypes(left);
        String[] rightArgumentTypes = getMethodArgumentTypes(right);

        return Arrays.equals(leftArgumentTypes, rightArgumentTypes);
    }

    private static String[] getMethodArgumentTypes(FSTNode node) {
        // The signature is stored in a FSTNode has the format 'name(arg1-arg1-arg2-arg2-arg3-arg3...)'.
        String[] nodeNameAndArguments = node.getName().split("[()]");
        
        String[] arguments = nodeNameAndArguments[1].split("-");
        return arguments;
    }

    public static boolean oneContainsTheBodyFromTheOther(FSTNode left, FSTNode right) {
        String leftBody = RenamingUtils.getMethodBodyWithoutWhitespaces(left);
        String rightBody = RenamingUtils.getMethodBodyWithoutWhitespaces(right);

        return leftBody != StringUtils.EMPTY && rightBody != StringUtils.EMPTY && (leftBody.contains(rightBody) || rightBody.contains(leftBody));
    }

    public static boolean haveDifferentBody(FSTNode left, FSTNode right) {
        return !haveEqualBodyModuloWhitespace(left, right);
    }

    public static boolean haveEqualBodyModuloWhitespace(FSTNode left, FSTNode right) {
        String leftBody = RenamingUtils.getMethodBodyWithoutWhitespaces(left);
        String rightBody = RenamingUtils.getMethodBodyWithoutWhitespaces(right);

        return leftBody.equals(rightBody);
    }

    public static boolean haveSimilarBodyModuloWhitespace(FSTNode left, FSTNode right) {
        String leftBody = RenamingUtils.getMethodBodyWithoutWhitespaces(left);
        String rightBody = RenamingUtils.getMethodBodyWithoutWhitespaces(right);

        double bodySimilarity = FilesManager.computeStringSimilarity(leftBody, rightBody);

        return bodySimilarity >= JFSTMerge.RENAMING_SIMILARITY_THRESHOLD;
    }

    public static void runTextualMerge(MergeContext context, FSTNode leftNode, FSTNode baseNode, FSTNode rightNode, FSTNode mergeNode) throws TextualMergeException {
        ((FSTTerminal) mergeNode).setBody(mergeContent(leftNode, baseNode, rightNode));
        ((FSTTerminal) mergeNode).setSpecialTokenPrefix(mergePrefix(leftNode, baseNode, rightNode));

        if(nodeHasConflict(mergeNode))
            context.renamingConflicts++;

        removeUnmmatchedNode(context.superImposedTree, leftNode, rightNode, mergeNode);
    }

    private static String mergeContent(FSTNode leftNode, FSTNode baseNode, FSTNode rightNode)
            throws TextualMergeException {
        return TextualMerge.merge(getNodeContent(leftNode), getNodeContent(baseNode), getNodeContent(rightNode),
                JFSTMerge.isWhitespaceIgnored);
    }

    private static String mergePrefix(FSTNode leftNode, FSTNode baseNode, FSTNode rightNode)
            throws TextualMergeException {
        String leftPrefix = getNodePrefix(leftNode);
        String basePrefix = getNodePrefix(baseNode);
        String rightPrefix = getNodePrefix(rightNode);
        return compareAndMerge(leftPrefix, basePrefix, rightPrefix);
    }

    public static String compareAndMerge(String left, String base, String right) throws TextualMergeException {
        String leftTrimmed = left.trim();
        String baseTrimmed = base.trim();
        String rightTrimmed = right.trim();

        if(JFSTMerge.isWhitespaceIgnored) {
            if(baseTrimmed.equals(leftTrimmed) && !baseTrimmed.equals(rightTrimmed)) {
                return right;
            } else if(baseTrimmed.equals(rightTrimmed) && !baseTrimmed.equals(leftTrimmed)) {
                return left;
            } else if(leftTrimmed.equals(rightTrimmed)) {
                return (left.length() > right.length()) ? left : right;
            } 
        } 
        
        return TextualMerge.merge(left, base, right, JFSTMerge.isWhitespaceIgnored);
    }

    private static String getNodeContent(FSTNode node) {
        if (node == null)
            return "";
        return ((FSTTerminal) node).getBody();
    }

    private static String getNodePrefix(FSTNode node) {
        if (node == null)
            return "";
        return ((FSTTerminal) node).getSpecialTokenPrefix();
    }
}
