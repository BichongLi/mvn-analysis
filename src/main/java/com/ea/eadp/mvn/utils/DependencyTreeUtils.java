package com.ea.eadp.mvn.utils;

import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.dependency.Dependency;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;

import java.util.*;
import java.util.regex.Matcher;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 10:07 AM
 */
public class DependencyTreeUtils {

    public static TreeNode generateDependencyTree(List<String> edges) {
        Map<String, TreeNode> dictionary = new HashMap<>();
        Map<TreeNode, Integer> indegreeMap = new HashMap<>();
        for (String edge : edges) {
            Matcher matcher = StringPatterns.DEPENDENCY_TREE_EDGE_PATTERN.matcher(edge);
            if (matcher.find()) {
                TreeNode parent = getOrCreateNode(matcher.group(1), dictionary);
                TreeNode child = getOrCreateNode(matcher.group(2), dictionary);
                parent.getChildren().add(child);
                child.setParent(parent);
                int indegree = indegreeMap.containsKey(child) ? indegreeMap.get(child) + 1 : 1;
                indegreeMap.put(child, indegree);
                if (!indegreeMap.containsKey(parent)) indegreeMap.put(parent, 0);
            }
        }
        for (Map.Entry<TreeNode, Integer> entry : indegreeMap.entrySet()) {
            if (entry.getValue() == 0) return entry.getKey();
        }
        throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Invalid dependency tree structure, there is no node with 0 indegree.");
    }

    private static TreeNode getOrCreateNode(String dependencyString, Map<String, TreeNode> dictionary) {
        TreeNode node = dictionary.get(dependencyString);
        if (node == null) {
            Dependency dependency = generateDependency(dependencyString);
            node = new TreeNode(dependency);
            dictionary.put(dependencyString, node);
        }
        return node;
    }

    private static Dependency generateDependency(String dependencyString) {
        Matcher matcher = StringPatterns.DEPENDENCY_STRING_PATTERN.matcher(dependencyString);
        if (matcher.find()) {
            String groupId = matcher.group(1);
            String artifactId = matcher.group(2);
            String type = matcher.group(3);
            String version = matcher.group(4);
            String scope = matcher.group(5);
            return new Dependency(groupId, artifactId, type, version, scope);
        }
        throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Invalid dependency string %1$s", dependencyString);
    }

}
