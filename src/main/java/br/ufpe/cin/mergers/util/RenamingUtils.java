package br.ufpe.cin.mergers.util;

import br.ufpe.cin.files.FilesManager;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTTerminal;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLModelASTReader;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.apache.commons.lang3.tuple.Pair;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class RenamingUtils {
    private static ArrayList<String> getFileNames(File file, boolean firstLevel){
        ArrayList<String> ret = new ArrayList<>();
        if (file != null) {
            if (file.isDirectory()){
                File[] files = file.listFiles();
                for (File childFile: files) {
                    ArrayList<String> aux = getFileNames(childFile, false);
                    for(String str: aux){
                        if (firstLevel)
                            ret.add("/" + str);
                        else
                            ret.add(file.getName() + "/" + str);
                    }
                }
            } else {
                ret.add(file.getName());
            }
        }
        return ret;

    }

    public static List<Refactoring> getRefactorings(File base, File after){
        if(base.isFile()) base = base.getParentFile();
        ArrayList<String> baseFiles = getFileNames(base, true);

        if(after.isFile()) after = after.getParentFile();
        ArrayList<String> afterFiles = getFileNames(after, true);


        UMLModel model1 = new UMLModelASTReader(base, baseFiles).getUmlModel();
        UMLModel model2 = new UMLModelASTReader(after, afterFiles).getUmlModel();
        UMLModelDiff modelDiff = model1.diff(model2);
        List<Refactoring> refactorings = modelDiff.getRefactorings();
        return  refactorings;
    }

    public static boolean hasUnstructuredMergeConflict(MergeContext context, String baseContent) {
        String signature = getTrimmedSignature(baseContent);

        return FilesManager.extractMergeConflicts(context.unstructuredOutput).stream()
                .map(conflict -> FilesManager.getStringContentIntoSingleLineNoSpacing(conflict.body))
                .anyMatch(conflict -> conflict.contains(signature));
    }

    private static String convertSignatureFromRMiner(String signature) {
        String signatureType = signature.split(":")[1].trim();
        signature = signature.split(":")[0].trim();
        String[] signatureAsArray = signature.split(" ", 2);
        signature = signatureAsArray[0] + " " + signatureType + " " + signatureAsArray[1];
        return signature;
    }

    public static  List<Pair<Double, String>> getSimilarNodesRMiner(String baseContent, FSTNode currentNode,
                                                              List<FSTNode> addedNodes, List<Refactoring> refactorings) {
        List<Pair<Double, String>> similarNodes = new ArrayList<>();

        String oldSignature = getSignature(baseContent).trim();
        String newSignature = "";
        HashMap<String, String> renames;
        for (Refactoring refactoring : refactorings) {
            if (refactoring.getRefactoringType() == RefactoringType.RENAME_METHOD) {
                String renameSignature = refactoring.toString().split("renamed to")[0]
                        .split("Rename Method")[1]
                        .split("\t")[1];
                renameSignature = convertSignatureFromRMiner(renameSignature);
                if(renameSignature.equals(oldSignature)) {
                    newSignature = refactoring.toString().split("renamed to")[1].split("in class")[0].trim();
                    newSignature = convertSignatureFromRMiner(newSignature);
                    System.out.println(newSignature);
                    break;
                }
            }
        }


        //1. getting similar nodes to fulfill renaming conflicts
        for (FSTNode newNode : addedNodes) { // a possible renamed node is seem as "new" node due to superimposition
            if (!isMethodOrConstructorNode(newNode)) continue;
            if (!haveSameParent(newNode, currentNode)) continue;

            String possibleRenamingContent = ((FSTTerminal) newNode).getBody();
            if (getSignature(possibleRenamingContent).trim().equals(newSignature)) {
                System.out.println(newSignature + "!!!!!!!!");
                Pair<Double, String> tp = Pair.of(1.0, possibleRenamingContent);
                similarNodes.add(tp);
            }
        }

        return similarNodes;
    }

    public static List<Pair<Double, String>> getSimilarNodes(String baseContent, FSTNode currentNode,
                                                             List<FSTNode> addedNodes, double similarityThreshold) {
        //list of possible nodes renaming a previous one
        List<Pair<Double, String>> similarNodes = new ArrayList<>();

        //1. getting similar nodes to fulfill renaming conflicts
        for (FSTNode newNode : addedNodes) { // a possible renamed node is seem as "new" node due to superimposition
            if (!isMethodOrConstructorNode(newNode)) continue;
            if (!haveSameParent(newNode, currentNode)) continue;

            String possibleRenamingContent = ((FSTTerminal) newNode).getBody();
            double bodySimilarity = FilesManager.computeStringSimilarity(baseContent, possibleRenamingContent);
            if (bodySimilarity >= similarityThreshold) {
                Pair<Double, String> tp = Pair.of(bodySimilarity, possibleRenamingContent);
                similarNodes.add(tp);
            }
        }

        return similarNodes;
    }

    public static String getTrimmedSignature(String source) {
        String trimmedSource = FilesManager.getStringContentIntoSingleLineNoSpacing(source);
        return getSignature(trimmedSource);
    }

    public static String getSignature(String source) {
        return source.substring(0, (/*is interface?*/(source.contains("{")) ? source.indexOf("{") : source.indexOf(";")));
    }

    public static String getNodeBodyWithoutSignature(FSTNode node) {
        return Optional.of(node)
                .map(FSTTerminal.class::cast)
                .map(FSTTerminal::getBody)
                .map(FilesManager::getStringContentIntoSingleLineNoSpacing)
                .map(RenamingUtils::removeSignature)
                .orElse(null);
    }

    public static String removeSignature(String string) {
        string = string.replaceFirst("^.[^{]*(?=(\\{))", "");
        return string;
    }

    public static String getMostSimilarContent(List<Pair<Double, String>> similarNodes) {
        return similarNodes.stream()
                .max(Comparator.comparing(Pair::getLeft))
                .map(Pair::getRight)
                .orElse("");
    }

    public static boolean nodeHasConflict(FSTNode node) {
        if (isMethodOrConstructorNode(node)) {
            String body = ((FSTTerminal) node).getBody();
            return body.contains("<<<<<<< MINE");
        }

        return false;
    }

    public static boolean isMethodOrConstructorNode(FSTNode node) {
        if (node instanceof FSTTerminal) {
            String nodeType = node.getType();
            return nodeType.equals("MethodDecl") || nodeType.equals("ConstructorDecl");
        }

        return false;
    }

    public static boolean haveSameParent(FSTNode left, FSTNode right) {
        return left.getParent().equals(right.getParent());
    }

    public static void generateRenamingConflict(MergeContext context, String currentNodeContent, String firstContent,
                                                String secondContent, Side renamingSide) {
        if (renamingSide == Side.LEFT) {//managing the origin of the changes in the conflict
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
        if (renamingSide == Side.RIGHT) {
            FilesManager.findAndDeleteASTNode(context.superImposedTree, firstContent);
        } else {
            FilesManager.findAndDeleteASTNode(context.superImposedTree, secondContent);
        }
    }

    public static void generateMutualRenamingConflict(MergeContext context, FSTNode left, FSTNode right) {
        String leftContent = ((FSTTerminal) left).getBody();
        String rightContent = ((FSTTerminal) right).getBody();

        //statistics
        context.renamingConflicts++;

        //first creates a conflict
        MergeConflict newConflict = new MergeConflict(leftContent + '\n', rightContent + '\n');

        //second put the conflict in one of the nodes containing the previous conflict, and deletes the other node containing the possible renamed version
        FilesManager.findAndReplaceASTNodeContent(context.superImposedTree, leftContent, newConflict.body);
        FilesManager.findAndDeleteASTNode(context.superImposedTree, rightContent);
    }

    public static String getMergeConflictContentOfOppositeSide(MergeConflict mergeConflict, Side side) {
        if (side == Side.LEFT) return mergeConflict.right;
        if (side == Side.RIGHT) return mergeConflict.left;

        return null;
    }

    public static List<FSTNode> getMethodsOrConstructors(List<FSTNode> nodes) {
        return nodes.stream()
                .filter(RenamingUtils::isMethodOrConstructorNode)
                .collect(Collectors.toList());
    }

    public static boolean haveDifferentSignature(FSTNode left, FSTNode right) {
        return !left.getName().equals(right.getName());
    }

    public static boolean haveSameBody(FSTNode left, FSTNode right) {
        String leftBody = RenamingUtils.getNodeBodyWithoutSignature(left);
        String rightBody = RenamingUtils.getNodeBodyWithoutSignature(right);

        return leftBody.equals(rightBody);
    }
}
