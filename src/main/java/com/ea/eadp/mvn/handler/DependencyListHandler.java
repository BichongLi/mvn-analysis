package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.dependency.Dependency;
import com.ea.eadp.mvn.model.dependency.DependencyWrapper;
import com.ea.eadp.mvn.utils.DependencyUtils;
import com.ea.eadp.mvn.utils.IOUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: BichongLi
 * Date: 12/6/2016
 * Time: 11:38 AM
 */
public class DependencyListHandler extends BaseAnalyzeHandler {

    private static final String OUTPUT_FILE_PARAM = "o";

    private static final Set<String> IGNORE_LIST_GROUP_ID = new HashSet<>();

    static {
        IGNORE_LIST_GROUP_ID.add("com.ea.eadp");
        IGNORE_LIST_GROUP_ID.add("com.ea.nucleus");
    }

    private static final String MVN_COMMAND_FORMAT = "dependency:list -DoutputFile=%1$s";

    private static final DependencyListHandler instance = new DependencyListHandler();

    private DependencyListHandler() {
    }

    public static DependencyListHandler getInstance() {
        return instance;
    }

    @Override
    protected Options getOptions() {
        Options options = super.getOptions();
        Option output = Option.builder(OUTPUT_FILE_PARAM).longOpt("Output file")
                .hasArg().build();
        options.addOption(output);
        return options;
    }

    @Override
    public void analyze(String[] args) {
        Options options = getOptions();
        CommandLine commandLine = parseCommandLine(args, options);
        checkHelp(commandLine, options, AnalyzeMode.ANALYZE_DEPENDENCY_LIST);
        runMVNCommand(parseRequest(commandLine));

        String output = commandLine.getOptionValue(OUTPUT_FILE_PARAM);
        InputStream input = IOUtils.readFileToInputStream(output);
        List<String> dependencyLines = extractUsefulInfo(input,
                p -> StringPatterns.DEPENDENCY_STRING_PATTERN.matcher(p.trim()).find());
        List<Dependency> dependencies = dependencyLines.stream()
                .map(line -> DependencyUtils.generateDependency(line.trim()))
                .filter(d -> !IGNORE_LIST_GROUP_ID.contains(d.getGroupId()))
                .collect(Collectors.toList());
        DependencyWrapper wrapper = new DependencyWrapper(dependencies);
        IOUtils.printXMLtoFileByPath(wrapper, output);
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        return Collections.singletonList(
                String.format(MVN_COMMAND_FORMAT, commandLine.getOptionValue(OUTPUT_FILE_PARAM))
        );
    }
}
