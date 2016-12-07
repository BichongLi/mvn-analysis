package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import com.ea.eadp.mvn.utils.DependencyUtils;
import com.ea.eadp.mvn.utils.IOUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 9:28 AM
 */
public class DependencyTreeHandler extends BaseAnalyzeHandler {

    private static final String OUTPUT_FILE_PARAM = "o";

    private static final String MVN_COMMAND_FORMAT = "dependency:tree -DoutputFile=%1$s -DoutputType=dot";

    private static final DependencyTreeHandler instance = new DependencyTreeHandler();

    private DependencyTreeHandler() {
    }

    public static DependencyTreeHandler getInstance() {
        return instance;
    }

    @Override
    protected Options getOptions() {
        Options options = super.getOptions();
        Option outputFile = Option.builder(OUTPUT_FILE_PARAM).longOpt("Output tree structure file")
                .hasArg().build();
        options.addOption(outputFile);
        return options;
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        return Collections.singletonList(
                String.format(MVN_COMMAND_FORMAT, commandLine.getOptionValue(OUTPUT_FILE_PARAM))
        );
    }

    @Override
    public void analyze(String[] args) {
        Options options = getOptions();
        CommandLine commandLine = parseCommandLine(args, options);
        checkHelp(commandLine, options, AnalyzeMode.ANALYZE_DEPENDENCY_TREE);
        runMVNCommand(parseRequest(commandLine));

        String outputFile = commandLine.getOptionValue(OUTPUT_FILE_PARAM);
        TreeNode treeRoot = buildDependencyTree(outputFile);
        IOUtils.printXMLtoFileByPath(treeRoot, outputFile);
    }

    private TreeNode buildDependencyTree(String filePath) {
        InputStream file = IOUtils.readFileToInputStream(filePath);
        List<String> edges = extractUsefulInfo(file,
                p -> StringPatterns.DEPENDENCY_TREE_EDGE_PATTERN.matcher(p).find());
        return DependencyUtils.generateDependencyTree(edges);
    }
}
