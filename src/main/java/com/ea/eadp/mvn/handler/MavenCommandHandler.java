package com.ea.eadp.mvn.handler;

import org.apache.commons.cli.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * User: BichongLi
 * Date: 11/27/2016
 * Time: 5:49 PM
 */
public class MavenCommandHandler extends BaseAnalyzeHandler {

    private static final MavenCommandHandler instance = new MavenCommandHandler();

    private MavenCommandHandler() {
    }

    public static MavenCommandHandler getInstance() {
        return instance;
    }

    @Override
    public InputStream runCommand(String[] args) {
        ByteArrayOutputStream out = (ByteArrayOutputStream) runMVNCommand(parseRequest(args));
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        if (commandLine.hasOption(COMMAND_PARAM)) {
            return Collections.singletonList(commandLine.getOptionValue(COMMAND_PARAM));
        } else {
            return Collections.singletonList(MVN_COMMAND);
        }
    }

    @Override
    public void analyze(InputStream in) {
    }
}
