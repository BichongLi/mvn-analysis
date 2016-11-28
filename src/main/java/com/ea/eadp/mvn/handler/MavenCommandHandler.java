package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import org.apache.commons.cli.CommandLine;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;

import java.io.*;
import java.util.Collections;

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
        if (commandLine.hasOption(MAVEN_HOME_PARAM)) mvnHome = commandLine.getOptionValue(MAVEN_HOME_PARAM);
        if (commandLine.hasOption(JAVA_HOME_PARAM)) {
            request.setJavaHome(new File(commandLine.getOptionValue(JAVA_HOME_PARAM)));
        }
        request.setPomFile(new File(commandLine.getOptionValue(POM_PATH_PARAM)));
        return request;
    }

    @Override
    public InputStream runCommand(String[] args) {
        ByteArrayOutputStream out = (ByteArrayOutputStream) runMVNCommand(parseRequest(args));
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void analyze(InputStream in) {
        print(in);
    }
}
