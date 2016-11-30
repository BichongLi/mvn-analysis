package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.StringPatterns;
import org.apache.commons.cli.CommandLine;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * User: BichongLi
 * Date: 11/29/2016
 * Time: 11:36 AM
 */
public class DependencyListHandler extends BaseAnalyzeHandler {

    private static final DependencyListHandler instance = new DependencyListHandler();

    private DependencyListHandler() {
    }

    public static DependencyListHandler getInstance() {
        return instance;
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        return Collections.singletonList(MVN_COMMAND);
    }

    @Override
    public void analyze(InputStream in) {
        print(parseMVNCommandOutput(in, p -> p.equals(StringPatterns.START_DEPENDENCY_PRINT),
                p -> p.equals(StringPatterns.MVN_OUTPUT_SEPARATE_LINE), defaultMapStringsToDependencies));
    }
}
