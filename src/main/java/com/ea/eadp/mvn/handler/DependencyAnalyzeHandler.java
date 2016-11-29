package com.ea.eadp.mvn.handler;

import org.apache.commons.cli.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

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
    List<String> getCommands(CommandLine commandLine) {
        return Collections.singletonList(MVN_COMMAND);
    }

    @Override
    public void analyze(InputStream in) {
        print(in);
    }
}
