package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming.MutualRenamingHandler;
import br.ufpe.cin.mergers.handlers.mutualrenaming.singlerenaming.MutualRenamingHandlerFactory;
import br.ufpe.cin.mergers.handlers.singlerenaming.SingleRenamingHandler;
import br.ufpe.cin.mergers.handlers.singlerenaming.SingleRenamingHandlerFactory;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.RenamingUtils;
import br.ufpe.cin.mergers.util.Side;
import de.ovgu.cide.fstgen.ast.FSTNode;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLModelASTReader;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.apache.commons.lang3.tuple.Pair;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Renaming or deletions conflicts happen when one developer edits a element renamed or deleted by other.
 * Semistructured merge is unable to detect such cases because it matches elements via its identifier, so
 * if a element is renamed or deleted it cannot match the elements anymore. This class overcomes this issue.
 *
 * @author Guilherme
 */
public final class MethodAndConstructorRenamingAndDeletionHandler implements ConflictHandler {
    private static final double BODY_SIMILARITY_THRESHOLD = 0.7;  //a typical value of 0.7 (up to 1.0) is used, increase it for a more accurate comparison, or decrease for a more relaxed one.

    private SingleRenamingHandler singleRenamingHandler;
    private MutualRenamingHandler mutualRenamingHandler;

    public MethodAndConstructorRenamingAndDeletionHandler() {
        this.singleRenamingHandler = SingleRenamingHandlerFactory.getHandler(JFSTMerge.renamingStrategy);
        this.mutualRenamingHandler = MutualRenamingHandlerFactory.getHandler(JFSTMerge.renamingStrategy);
    }

    private ArrayList<String> getFileNames(File file, boolean firstLevel){
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

    private boolean hasRenaming(File base, File after){
        if(base.isFile()) base = base.getParentFile();
        ArrayList<String> baseFiles = getFileNames(base, true);

        if(after.isFile()) after = after.getParentFile();
        ArrayList<String> afterFiles = getFileNames(after, true);


        UMLModel model1 = new UMLModelASTReader(base, baseFiles).getUmlModel();
        UMLModel model2 = new UMLModelASTReader(after, afterFiles).getUmlModel();
        UMLModelDiff modelDiff = model1.diff(model2);
        List<Refactoring> refactorings = modelDiff.getRefactorings();
        boolean ret = false;
        for (int i = 0; i < refactorings.size(); i++){
            if(refactorings.get(i).getRefactoringType() == RefactoringType.RENAME_METHOD) ret = true;
        }
        System.out.println("ret");
        System.out.println(ret);
        return ret;
    }

    public void handle(MergeContext context) {
        boolean leftRenaming = hasRenaming(context.getBase(), context.getLeft());
        boolean rightRenaming = hasRenaming(context.getBase(), context.getRight());

        //when both developers rename the same method/constructor
        if (leftRenaming && rightRenaming)
            handleMutualRenamings(context);

        //when one of the developers rename a method/constructor
        if (leftRenaming || rightRenaming)
            handleSingleRenamings(context);
    }

    private void handleMutualRenamings(MergeContext context) {
        mutualRenamingHandler.handle(context);
    }

    private void handleSingleRenamings(MergeContext context) {
        if (context.possibleRenamedLeftNodes.isEmpty() && context.possibleRenamedRightNodes.isEmpty()) return;

        //possible renamings or deletions in left
        handleSingleRenamings(context, context.possibleRenamedLeftNodes, context.addedLeftNodes, Side.LEFT);

        //possible renamings or deletions in right
        handleSingleRenamings(context, context.possibleRenamedRightNodes, context.addedRightNodes, Side.RIGHT);
    }

    private void handleSingleRenamings(MergeContext context, List<Pair<String, FSTNode>> possibleRenamedNodes,
                                       List<FSTNode> addedNodes, Side renamingSide) {
        for (Pair<String, FSTNode> tuple : possibleRenamedNodes) {
            String baseContent = tuple.getLeft();
            FSTNode currentNode = tuple.getRight();
            if (!RenamingUtils.nodeHasConflict(currentNode)) continue;

            List<Pair<Double, String>> similarNodes = RenamingUtils.getSimilarNodes(baseContent, currentNode, addedNodes,
                    BODY_SIMILARITY_THRESHOLD);

            singleRenamingHandler.handle(context, baseContent, currentNode, similarNodes, renamingSide);
        }
    }
}
