package com.ea.eadp.mvn.handler;

import java.io.ByteArrayOutputStream;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:56 AM
 */
public class DependencyAnalyzeHandler implements AnalyzeHandler {

    private static final DependencyAnalyzeHandler instance = new DependencyAnalyzeHandler();

    private DependencyAnalyzeHandler() {
    }

    public static DependencyAnalyzeHandler getInstance() {
        return instance;
    }

    @Override
    public ByteArrayOutputStream runCommand(String[] args) {
        return runMVNCommand(parseRequest(args));
    }

    @Override
    public void analyze(ByteArrayOutputStream baos) {
        System.out.println("Analyze output:");
        System.out.println(baos.toString());
    }
}
