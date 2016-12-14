package com.ea.eadp.mvn.model.common;

import java.util.regex.Pattern;

/**
 * User: BichongLi
 * Date: 11/30/2016
 * Time: 10:28 AM
 */
public class StringPatterns {

    private StringPatterns() {
    }

    public static final Pattern ANALYZE_REPORT_PATH_PATTERN = Pattern.compile("^\\[INFO\\] ([A-Z]:\\\\.+\\\\target)$");
    public static final Pattern DEPENDENCY_TREE_EDGE_PATTERN = Pattern.compile("^.*\"(.+)\" -> \"(.+)\".*$");
    public static final Pattern DEPENDENCY_STRING_PATTERN = Pattern.compile("^([^:]+):([^:]+):([^:]+[:?:shaded|sources]*):([^:]+):*([^:]+)*$");
    public static final String MVN_REPOSITORY_REFERENCE = "https://mvnrepository.com/artifact/%1$s/%2$s";

}
