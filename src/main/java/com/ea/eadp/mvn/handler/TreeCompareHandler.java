package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import com.ea.eadp.mvn.utils.DependencyTreeUtils;
import com.ea.eadp.mvn.utils.IOUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.InputStream;
import java.util.List;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 9:49 AM
 */
public class TreeCompareHandler extends BaseAnalyzeHandler {

    private static final String LEFT_TREE_PARAM = "l";
    private static final String RIGHT_TREE_PARAM = "r";

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
    }

    private TreeNode buildDependencyTree(String filePath) {
        InputStream file = IOUtils.readFileToInputStream(filePath);
        List<String> edges = extractUsefulInfo(file,
                p -> StringPatterns.DEPENDENCY_TREE_EDGE_PATTERN.matcher(p).find());
        return DependencyTreeUtils.generateDependencyTree(edges);
    }
}
