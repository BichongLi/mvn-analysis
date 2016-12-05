package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.common.DependencyDiff;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.dependency.Dependency;
import com.ea.eadp.mvn.model.dependency.Diff;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import com.ea.eadp.mvn.utils.DependencyTreeUtils;
import com.ea.eadp.mvn.utils.IOUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 9:49 AM
 */
public class TreeCompareHandler extends BaseAnalyzeHandler {

    private static final String LEFT_TREE_PARAM = "l";
    private static final String RIGHT_TREE_PARAM = "r";

    private static final Set<String> IGNORE_CMP_VERSION_GROUP_ID = new HashSet<>();

    static {
        IGNORE_CMP_VERSION_GROUP_ID.add("com.ea.eadp");
        IGNORE_CMP_VERSION_GROUP_ID.add("com.ea.nucleus");
    }

    private static final TreeCompareHandler instance = new TreeCompareHandler();

    private TreeCompareHandler() {
    }

    public static TreeCompareHandler getInstance() {
        return instance;
    }

    @Override
    protected Options getOptions() {
        Options options = super.getOptions();
        Option left = Option.builder(LEFT_TREE_PARAM).longOpt("Left dot file to compare.")
                .hasArg().build();
        Option right = Option.builder(RIGHT_TREE_PARAM).longOpt("Right dot file to compare.")
                .hasArg().build();
        options.addOption(left);
        options.addOption(right);
        return options;
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        return null;
    }

    @Override
    public void analyze(String[] args) {
        Options options = getOptions();
        CommandLine commandLine = parseCommandLine(args, options);
        checkHelp(commandLine, options, AnalyzeMode.DEPENDENCY_TREE_COMPARE);
        TreeNode leftRoot = buildDependencyTree(commandLine.getOptionValue(LEFT_TREE_PARAM));
        TreeNode rightRoot = buildDependencyTree(commandLine.getOptionValue(RIGHT_TREE_PARAM));

        DependencyDiff rootDiff = leftRoot.getDependency().compare(rightRoot.getDependency());
        if (rootDiff != DependencyDiff.SAME) {
            System.out.println(String.format("Compare result of root %1$s and %2$s is %3$s",
                    leftRoot.getDependency().toString(), rightRoot.getDependency().toString(), rootDiff.toString()));
            return;
        }

        List<Diff> diffResult = compareEachLevel(leftRoot, rightRoot);
        printDiffResults(diffResult);
    }

    private TreeNode buildDependencyTree(String filePath) {
        InputStream file = IOUtils.readFileToInputStream(filePath);
        List<String> edges = extractUsefulInfo(file,
                p -> StringPatterns.DEPENDENCY_TREE_EDGE_PATTERN.matcher(p).find());
        return DependencyTreeUtils.generateDependencyTree(edges);
    }

    private List<Diff> compareEachLevel(TreeNode leftRoot, TreeNode rightRoot) {
        List<Diff> result = new ArrayList<>();
        Set<TreeNode> leftNeedContinue = new HashSet<>();
        leftNeedContinue.add(leftRoot);
        Set<TreeNode> rightNeedContinue = new HashSet<>();
        rightNeedContinue.add(rightRoot);
        while (!leftNeedContinue.isEmpty() || !rightNeedContinue.isEmpty()) {
            Set<TreeNode> left = getNextLevelNodes(leftNeedContinue);
            Set<TreeNode> right = getNextLevelNodes(rightNeedContinue);
            leftNeedContinue.clear();
            rightNeedContinue.clear();
            left.forEach(lt -> {
                Dependency ld = lt.getDependency();
                Iterator<TreeNode> iterator = right.iterator();
                boolean match = false;
                while (iterator.hasNext()) {
                    TreeNode rt = iterator.next();
                    Dependency rd = rt.getDependency();
                    DependencyDiff diff = ld.compare(rd);
                    if (diff == DependencyDiff.DIFFERENT_DEPENDENCY) continue;
                    match = true;
                    if (diff == DependencyDiff.SAME) {
                        if (IGNORE_CMP_VERSION_GROUP_ID.contains(ld.getGroupId())) {
                            leftNeedContinue.add(lt);
                            rightNeedContinue.add(rt);
                        }
                    } else {
                        result.add(new Diff(diff, ld.toString(), rd.toString()));
                    }
                    iterator.remove();
                    break;
                }
                if (!match) {
                    result.add(new Diff(null, getTreePath(lt), null));
                }
            });
            if (!right.isEmpty()) {
                result.addAll(
                        right.stream().map(n -> new Diff(null, null, getTreePath(n)))
                                .collect(Collectors.toList()));
            }
        }
        return result;
    }

    private Set<TreeNode> getNextLevelNodes(Set<TreeNode> nodes) {
        Set<TreeNode> nextLevelNodes = new HashSet<>();
        nodes.forEach(node -> nextLevelNodes.addAll(node.getChildren()));
        return nextLevelNodes;
    }

    private String getTreePath(TreeNode node) {
        TreeNode tmp = node;
        String result = tmp.getDependency().toString();
        while (tmp.getParent() != null) {
            tmp = tmp.getParent();
            result = tmp.getDependency().toString() + " -> " + result;
        }
        return result;
    }

    private void printDiffResults(List<Diff> results) {
        List<Diff> different = new ArrayList<>();
        List<Diff> leftOnly = new ArrayList<>();
        List<Diff> rightOnly = new ArrayList<>();
        System.out.println("Find matched different dependencies:");
        results.forEach(r -> {
            if (r.getDiffType() == null) {
                if (r.getLeftDependency() == null) rightOnly.add(r);
                if (r.getRightDependency() == null) leftOnly.add(r);
            } else {
                different.add(r);
            }
        });
        different.sort((Diff d1, Diff d2) -> {
            if (d1.getDiffType() != d2.getDiffType()) return d1.getDiffType().compareTo(d2.getDiffType());
            return d1.getLeftDependency().compareTo(d2.getLeftDependency());
        });
        /*leftOnly.sort((Diff d1, Diff d2) -> d1.getLeftDependency().compareTo(d2.getLeftDependency()));
        rightOnly.sort((Diff d1, Diff d2) -> d1.getRightDependency().compareTo(d2.getRightDependency()));*/
        different.forEach(System.out::println);
        leftOnly.forEach(System.out::println);
        rightOnly.forEach(System.out::println);
    }
}
