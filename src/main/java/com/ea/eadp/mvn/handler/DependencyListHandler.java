package com.ea.eadp.mvn.handler;

import org.apache.commons.cli.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * User: BichongLi
 * Date: 11/29/2016
 * Time: 11:36 AM
 */
public class DependencyListHandler extends BaseAnalyzeHandler {

    private static final DependencyListHandler instance = new DependencyListHandler();

    private DependencyListHandler() {
    }

    public static DependencyListHandler getInstance() {
        return instance;
    }

    @Override
    public InputStream runCommand(String[] args) {
        ByteArrayOutputStream out = (ByteArrayOutputStream) runMVNCommand(parseRequest(args));
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        return Collections.singletonList("dependency:list");
    }

    @Override
    public void analyze(InputStream in) {

    }
}
