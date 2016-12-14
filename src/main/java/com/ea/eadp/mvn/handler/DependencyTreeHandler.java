package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.common.DependencyDiff;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.dependency.Dependency;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import com.ea.eadp.mvn.utils.DependencyUtils;
import com.ea.eadp.mvn.utils.IOUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 9:28 AM
 */
public class DependencyTreeHandler extends BaseAnalyzeHandler {

    private static final String OUTPUT_FILE_PARAM = "o";
    private static final String OUTPUT_TYPE_PARAM = "t";
    private static final String BASE_DEPENDENCY_PARAM = "b";

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
        Option outputType = Option.builder(OUTPUT_TYPE_PARAM).longOpt("Output file type [xml(default)/json]")
                .hasArg().build();
        Option baseDependencies = Option.builder(BASE_DEPENDENCY_PARAM).longOpt("Base dependency set file")
                .hasArg().build();
        options.addOption(outputFile);
        options.addOption(outputType);
        options.addOption(baseDependencies);
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
        sortChildren(treeRoot);

        if (commandLine.hasOption(BASE_DEPENDENCY_PARAM)) {
            List<Dependency> baseDependencies = IOUtils.readDependencyXML(
                    commandLine.getOptionValue(BASE_DEPENDENCY_PARAM)
            ).getDependencies();
            Map<String, Dependency> dictionary = new HashMap<>();
            baseDependencies.forEach(d -> dictionary.put(genDependencyKey(d), d));
            compareDependencies(treeRoot, dictionary);
        }

        if (commandLine.hasOption(OUTPUT_TYPE_PARAM)) {
            String type = commandLine.getOptionValue(OUTPUT_TYPE_PARAM);
            if (type.equalsIgnoreCase("json")) {
                IOUtils.printJSONtoFileByPath(treeRoot, outputFile);
                return;
            }
        }
        IOUtils.printXMLtoFileByPath(treeRoot, outputFile);
    }

    private TreeNode buildDependencyTree(String filePath) {
        InputStream file = IOUtils.readFileToInputStream(filePath);
        List<String> edges = extractUsefulInfo(file,
                p -> StringPatterns.DEPENDENCY_TREE_EDGE_PATTERN.matcher(p).find());
        return DependencyUtils.generateDependencyTree(edges);
    }

    private void compareDependencies(TreeNode root, Map<String, Dependency> dictionary) {
        String key = genDependencyKey(root.getDependency());
        if (dictionary.containsKey(key) &&
                root.getDependency().compare(dictionary.get(key)) != DependencyDiff.SAME) {
            root.setSuggestDependency(dictionary.get(key));
        }
        root.getChildren().forEach(t -> compareDependencies(t, dictionary));
    }

    private String genDependencyKey(Dependency dependency) {
        String format = "%1$s:%2$s";
        return String.format(format, dependency.getGroupId(), dependency.getArtifactId());
    }

    private void sortChildren(TreeNode root) {
        Collections.sort(root.getChildren(), TreeNode::compareTo);
        root.getChildren().forEach(this::sortChildren);
    }
}
