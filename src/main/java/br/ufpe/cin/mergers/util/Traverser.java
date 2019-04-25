package br.ufpe.cin.mergers.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * A class for traversing trees (FSTNode) involved in a merge.
 */
public class Traverser {

    public static boolean isInTree(FSTNode node, FSTNode tree) {
        List<FSTNode> nodes = collectNodes(tree);
        return nodes.contains(node);
    }    

    public static List<FSTTerminal> collectTerminals(FSTNode tree) {
        return collectNodes(tree).stream()
            .filter(node -> node instanceof FSTTerminal)
            .map(FSTTerminal.class::cast)
            .collect(Collectors.toList());
    }

    public static FSTNode retrieveNodeFromTree(FSTNode node, FSTNode tree) {
        List<FSTNode> nodes = collectNodes(tree);
        for (FSTNode treeNode : nodes) {
            if(treeNode.getName().equals(node.getName()))
                return treeNode;
        }
        return null;
    }

    private static List<FSTNode> collectNodes(FSTNode tree) {
       return collectNodes(new ArrayList<FSTNode>(), tree);
    }

    private static List<FSTNode> collectNodes(List<FSTNode> nodes, FSTNode tree) {
        if(tree instanceof FSTTerminal)
            nodes.add(tree);
        else if(tree instanceof FSTNonTerminal) {
            nodes.add(tree);
            for(FSTNode child : ((FSTNonTerminal) tree).getChildren())
                collectNodes(nodes, child);
        } else {
            System.err.println("Warning: node is neither non-terminal nor terminal!");
        }
        return nodes;
    }

    
}