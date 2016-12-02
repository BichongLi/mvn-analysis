package com.ea.eadp.mvn;

import com.ea.eadp.mvn.handler.*;
import com.ea.eadp.mvn.model.common.AnalyzeMode;

import java.util.HashMap;
import java.util.Map;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:48 AM
 */
public class AnalyzeHandlerFactory {

    private static final Map<AnalyzeMode, AnalyzeHandler> handlerMap = new HashMap<>();

    static {
        handlerMap.put(AnalyzeMode.ANALYZE_DEPENDENCY, DependencyAnalyzeHandler.getInstance());
        handlerMap.put(AnalyzeMode.RUN_COMMAND, MavenCommandHandler.getInstance());
        handlerMap.put(AnalyzeMode.ANALYZE_DEPENDENCY_TREE, DependencyTreeHandler.getInstance());
    }

    public static AnalyzeHandler getHandler(String mode) {
        AnalyzeMode analyzeMode = AnalyzeMode.valueFromName(mode);
        return handlerMap.get(analyzeMode);
    }

}
