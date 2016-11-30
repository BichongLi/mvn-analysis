package com.ea.eadp.mvn.model.common;

import java.util.regex.Pattern;

/**
 * User: BichongLi
 * Date: 11/30/2016
 * Time: 10:28 AM
 */
public class StringPatterns {

    public static final Pattern MODULE_LINE_PATTERN = Pattern.compile("^\\[INFO\\] *Building (.*)$");
    public static final Pattern DEPENDENCY_LINE_PATTERN =
            Pattern.compile("^\\[\\w+\\] *(.+):(.+):(.+):(.+):(\\w+)$");
    public static final String START_DEPENDENCY_PRINT = "[INFO] The following files have been resolved:";
    public static final String MVN_OUTPUT_SEPARATE_LINE = "[INFO] ------------------------------------------------------------------------";
    public static final String SEPARATE_LINE = " ------------------------------------------------------------------------ ";
    public static final Pattern START_DEPENDENCY_ANALYZE =
            Pattern.compile("^\\[INFO\\] --- maven-dependency-plugin:.*:analyze \\(default-cli\\) @ (.*) ---$");

}
