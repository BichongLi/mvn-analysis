package com.ea.eadp.mvn;

import com.ea.eadp.mvn.model.dependency.TreeNode;
import org.junit.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * User: BichongLi
 * Date: 12/7/2016
 * Time: 11:33 AM
 */
public class TreeBaseTest {

    protected void validateTree(TreeNode root, String expectedRoot,
                                int expectedDepth, int expectedNodesInSecondLevel) {
        int depth = getDepth(root);
        Assert.assertEquals(expectedRoot, root.getDependency().toString());
        Assert.assertEquals(expectedDepth, depth);
        Assert.assertEquals(expectedNodesInSecondLevel, root.getChildren().size());
    }

    private int getDepth(TreeNode root) {
        int depth = 0;
        Set<TreeNode> currentLevel = new HashSet<>();
        currentLevel.add(root);
        Set<TreeNode> nextLevel = new HashSet<>();
        while (!currentLevel.isEmpty()) {
            depth++;
            currentLevel.forEach(n -> nextLevel.addAll(n.getChildren()));
            currentLevel = new HashSet<>(nextLevel);
            nextLevel.clear();
        }
        return depth;
    }

}
