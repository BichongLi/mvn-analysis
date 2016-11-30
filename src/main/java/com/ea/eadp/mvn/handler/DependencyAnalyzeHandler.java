package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.mvn.Dependency;
import javafx.util.Pair;
import org.apache.commons.cli.CommandLine;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:56 AM
 */
public class DependencyAnalyzeHandler extends BaseAnalyzeHandler {

    private static final String UNDECLARED_DEPENDENCY_FOUND = "[WARNING] Used undeclared dependencies found:";
    private static final String UNUSED_DEPENDENCY_FOUND = "[WARNING] Unused declared dependencies found:";

    private static final DependencyAnalyzeHandler instance = new DependencyAnalyzeHandler();

    private DependencyAnalyzeHandler() {
    }

    public static DependencyAnalyzeHandler getInstance() {
        return instance;
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        return Collections.singletonList("dependency:analyze");
    }

    @Override
    public void analyze(InputStream in) {
        Function<Pair<String, List<String>>, List<Pair<String, List<Dependency>>>> mapStringsToDependencies = p -> {
            String currentModule = p.getKey();
            String situation = null;
            List<Dependency> dependencies = new LinkedList<>();
            List<Pair<String, List<Dependency>>> results = new LinkedList<>();
            for (String line : p.getValue()) {
                switch (line) {
                    case UNDECLARED_DEPENDENCY_FOUND:
                        situation = "Undeclared dependencies in " + currentModule;
                        break;
                    case UNUSED_DEPENDENCY_FOUND:
                        if (situation != null && !dependencies.isEmpty()) {
                            results.add(new Pair<>(situation, dependencies));
                            dependencies = new LinkedList<>();
                        }
                        situation = "Unused dependencies in " + currentModule;
                        break;
                    default:
                        if (StringPatterns.DEPENDENCY_LINE_PATTERN.matcher(line).find()) {
                            dependencies.add(lineToDependency.apply(line));
                        }
                        break;
                }
            }
            if (situation != null && !dependencies.isEmpty()) results.add(new Pair<>(situation, dependencies));
            return results;
        };
        print(parseMVNCommandOutput(in, p -> StringPatterns.START_DEPENDENCY_ANALYZE.matcher(p).find(),
                p -> p.equals(StringPatterns.MVN_OUTPUT_SEPARATE_LINE), mapStringsToDependencies));
    }
}
