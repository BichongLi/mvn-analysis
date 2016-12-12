package com.ea.eadp.mvn.model.common;

import java.util.HashSet;
import java.util.Set;

/**
 * User: BichongLi
 * Date: 12/12/2016
 * Time: 10:37 AM
 */
public class Constants {

    public static final Set<String> GROUPID_TO_ANALYZE = new HashSet<>();

    static {
        GROUPID_TO_ANALYZE.add("com.ea.eadp");
        GROUPID_TO_ANALYZE.add("com.ea.nucleus");
    }

}
