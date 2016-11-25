package com.ea.eadp.mvn.model.common;

import org.apache.commons.lang3.StringUtils;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:52 AM
 */
public enum AnalyzeMode {

    ANALYZE_DEPENDENCY;

    public static String getDescription() {
        return StringUtils.join(AnalyzeMode.values(), ", ");
    }
}
