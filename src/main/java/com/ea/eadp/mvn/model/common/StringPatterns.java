package com.ea.eadp.mvn.model.common;

import java.util.regex.Pattern;

/**
 * User: BichongLi
 * Date: 11/30/2016
 * Time: 10:28 AM
 */
public class StringPatterns {

    public static final Pattern ANALYZE_REPORT_PATH_PATTERN = Pattern.compile("^\\[INFO\\] ([A-Z]:\\\\.+\\\\target)$");

}
