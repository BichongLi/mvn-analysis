package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.mvn.Dependency;
import org.apache.commons.cli.CommandLine;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: BichongLi
 * Date: 11/30/2016
 * Time: 4:36 PM
 */
public class DependencyCompareHandler extends BaseAnalyzeHandler {

    private static final DependencyCompareHandler instance = new DependencyCompareHandler();

    private DependencyCompareHandler() {
    }

    public static DependencyCompareHandler getInstance() {
        return instance;
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        return Collections.singletonList(MVN_COMMAND);
    }

    @Override
    public void analyze(InputStream in) {
        Map<String, List<Dependency>> currentProject = parseMVNCommandOutput(in,
                p -> p.equals(StringPatterns.START_DEPENDENCY_PRINT),
                p -> p.equals(StringPatterns.MVN_OUTPUT_SEPARATE_LINE),
                defaultMapStringsToDependencies);
    }
}
