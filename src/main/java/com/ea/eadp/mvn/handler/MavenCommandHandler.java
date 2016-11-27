package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import org.apache.commons.cli.CommandLine;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collections;

/**
 * User: BichongLi
 * Date: 11/27/2016
 * Time: 5:49 PM
 */
public class MavenCommandHandler implements AnalyzeHandler {

    private static final MavenCommandHandler instance = new MavenCommandHandler();

    private MavenCommandHandler() {
    }

    public static MavenCommandHandler getInstance() {
        return instance;
    }

    @Override
    public InvocationRequest parseRequest(String args[]) {
        CommandLine commandLine = parseCommandLine(args);
        if (!commandLine.hasOption(POM_PATH_PARAM)) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Missing project pom.xml specified.");
        }
        InvocationRequest request = new DefaultInvocationRequest();
        if (commandLine.hasOption(COMMAND_PARAM)) {
            request.setGoals(Collections.singletonList(commandLine.getOptionValue(COMMAND_PARAM)));
        } else {
            request.setGoals(Collections.singletonList(MVN_COMMAND));
        }
        request.setPomFile(new File(commandLine.getOptionValue(POM_PATH_PARAM)));
        return request;
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
