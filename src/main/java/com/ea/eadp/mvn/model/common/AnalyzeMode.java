package com.ea.eadp.mvn.model.common;

import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:52 AM
 */
public enum AnalyzeMode {

    ANALYZE_DEPENDENCY("dependencyAnalyze"),
    ANALYZE_DEPENDENCY_TREE("dependencyTree"),
    ANALYZE_DEPENDENCY_LIST("dependencyList"),
    DEPENDENCY_TREE_COMPARE("treeCompare"),
    RUN_COMMAND("commandRun");

    private String name;

    public String getName() {
        return name;
    }

    AnalyzeMode(String name) {
        this.name = name;
    }

    public static String getDescription() {
        List<String> modes = Arrays.stream(AnalyzeMode.values())
                .map(AnalyzeMode::getName).collect(Collectors.toList());
        return modes.toString();
    }

    public static AnalyzeMode valueFromName(String name) {
        List<AnalyzeMode> matchModes = Arrays.stream(AnalyzeMode.values())
                .filter(p -> p.getName().equals(name)).collect(Collectors.toList());
        if (matchModes.isEmpty()) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Unknown analyze mode: %1$s", name);
        }
        return matchModes.get(0);
    }
}
