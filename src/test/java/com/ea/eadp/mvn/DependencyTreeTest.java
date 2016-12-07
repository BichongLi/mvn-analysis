package com.ea.eadp.mvn;

import com.ea.eadp.mvn.handler.DependencyTreeHandler;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import com.ea.eadp.mvn.utils.DependencyUtils;
import com.ea.eadp.mvn.utils.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 1:52 PM
 */
public class DependencyTreeTest {

    private static final String TREE_FILE = "catalogJava8";
    private static final String LIST_FILE = "catalogJava8List.txt";

    @Test
    public void test() {
        ClassLoader classLoader = getClass().getClassLoader();
        DependencyTreeHandler handler = DependencyTreeHandler.getInstance();
        TreeNode root = DependencyUtils.generateDependencyTree(
                handler.extractUsefulInfo(IOUtils.readFileToInputStream(classLoader.getResource(TREE_FILE).getFile()),
                        p -> StringPatterns.DEPENDENCY_TREE_EDGE_PATTERN.matcher(p).find())
        );
        Set<String> dependencies = handler.extractUsefulInfo(
                IOUtils.readFileToInputStream(classLoader.getResource(LIST_FILE).getFile()), p -> true)
                .stream().map(String::trim).collect(Collectors.toSet());
        dependencies.add(root.getDependency().toString());
        Set<String> tree = new HashSet<>();
        collect(root, tree);
        Assert.assertEquals(tree.size(), dependencies.size());
    }

    private void collect(TreeNode node, Set<String> dependencies) {
        dependencies.add(node.getDependency().toString());
        node.getChildren().forEach(p -> collect(p, dependencies));
    }

}
