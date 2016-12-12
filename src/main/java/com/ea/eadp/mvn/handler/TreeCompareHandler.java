package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.common.Constants;
import com.ea.eadp.mvn.model.common.DependencyDiff;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.dependency.Dependency;
import com.ea.eadp.mvn.model.dependency.Diff;
import com.ea.eadp.mvn.model.dependency.DiffResult;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import com.ea.eadp.mvn.utils.IOUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

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
    private static final String OUTPUT_FILE_PARAM = "o";

    private static final TreeCompareHandler instance = new TreeCompareHandler();

    private TreeCompareHandler() {
    }

    public static TreeCompareHandler getInstance() {
        return instance;
    }

    @Override
    protected Options getOptions() {
        Options options = super.getOptions();
        Option left = Option.builder(LEFT_TREE_PARAM).longOpt("Left tree xml file to compare.")
                .hasArg().build();
        Option right = Option.builder(RIGHT_TREE_PARAM).longOpt("Right tree xml file to compare.")
                .hasArg().build();
        Option outputFile = Option.builder(OUTPUT_FILE_PARAM).longOpt("Output txt file")
                .hasArg().build();
        options.addOption(left);
        options.addOption(right);
        options.addOption(outputFile);
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
        TreeNode leftRoot = IOUtils.readTreeXML(commandLine.getOptionValue(LEFT_TREE_PARAM));
        TreeNode rightRoot = IOUtils.readTreeXML(commandLine.getOptionValue(RIGHT_TREE_PARAM));

        DependencyDiff rootDiff = leftRoot.getDependency().compare(rightRoot.getDependency());
        if (rootDiff != DependencyDiff.SAME) {
            System.out.println(String.format("Compare result of root %1$s and %2$s is %3$s",
                    leftRoot.getDependency().toString(), rightRoot.getDependency().toString(), rootDiff.toString()));
            return;
        }

        DiffResult result = generateDiffResult(compareEachLevel(leftRoot, rightRoot));

        if (commandLine.hasOption(OUTPUT_FILE_PARAM)) {
            IOUtils.printXMLtoFileByPath(result, commandLine.getOptionValue(OUTPUT_FILE_PARAM));
        } else {
            IOUtils.printXMLtoConsole(result);
        }
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
                        if (Constants.GROUPID_TO_ANALYZE.contains(ld.getGroupId())) {
                            leftNeedContinue.add(lt);
                            rightNeedContinue.add(rt);
                        }
                    } else {
                        result.add(new Diff(
                                diff, ld.toString(), rd.toString(),
                                String.format(StringPatterns.MVN_REPOSITORY_REFERENCE, ld.getGroupId(), ld.getArtifactId())
                        ));
                    }
                    iterator.remove();
                    break;
                }
                if (!match) {
                    result.add(new Diff(
                            null, getTreePath(lt), null,
                            String.format(StringPatterns.MVN_REPOSITORY_REFERENCE, lt.getDependency().getGroupId(), lt.getDependency().getArtifactId()))
                    );
                }
            });
            if (!right.isEmpty()) {
                result.addAll(
                        right.stream().map(n -> new Diff(null, null, getTreePath(n), String.format(StringPatterns.MVN_REPOSITORY_REFERENCE, n.getDependency().getGroupId(), n.getDependency().getArtifactId())))
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
            result = tmp.getDependency().toString() + " -- " + result;
        }
        return result;
    }

    private DiffResult generateDiffResult(List<Diff> total) {
        List<Diff> differences = new ArrayList<>();
        List<Diff> leftOnly = new ArrayList<>();
        List<Diff> rightOnly = new ArrayList<>();
        total.forEach(r -> {
            if (r.getDiffType() == null) {
                if (r.getLeftDependency() == null) rightOnly.add(r);
                if (r.getRightDependency() == null) leftOnly.add(r);
            } else {
                differences.add(r);
            }
        });
        differences.sort((Diff d1, Diff d2) -> {
            if (d1.getDiffType() != d2.getDiffType()) return d1.getDiffType().compareTo(d2.getDiffType());
            return d1.getLeftDependency().compareTo(d2.getLeftDependency());
        });
        return new DiffResult(differences, leftOnly, rightOnly);
    }
}
