package com.ea.eadp.mvn.handler;

import java.io.InputStream;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:56 AM
 */
public class DependencyAnalyzeHandler implements AnalyzeHandler {

    private static final String MVN_COMMAND = "dependency:analyze";

    private static final DependencyAnalyzeHandler instance = new DependencyAnalyzeHandler();

    private DependencyAnalyzeHandler() {
    }

    public static DependencyAnalyzeHandler getInstance() {
        return instance;
    }

    @Override
    public InputStream runCommand(String[] args) {
        return null;
    }

    @Override
    public void analyze(InputStream inputStream) {

    }
}
