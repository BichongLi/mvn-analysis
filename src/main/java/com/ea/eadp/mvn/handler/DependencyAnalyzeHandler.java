package com.ea.eadp.mvn.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:56 AM
 */
public class DependencyAnalyzeHandler extends BaseAnalyzeHandler {

    private static final DependencyAnalyzeHandler instance = new DependencyAnalyzeHandler();

    private DependencyAnalyzeHandler() {
    }

    public static DependencyAnalyzeHandler getInstance() {
        return instance;
    }

    @Override
    public InputStream runCommand(String[] args) {
        ByteArrayOutputStream out = (ByteArrayOutputStream) runMVNCommand(parseRequest(args));
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void analyze(InputStream in) {
        System.out.println(extractUsefulInfo(in));
    }
}
