package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import org.apache.commons.cli.*;
import org.apache.maven.shared.invoker.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:46 AM
 */
public interface AnalyzeHandler {

    String MODE_PARAM = "m";
    String POM_PATH_PARAM = "p";
    String MVN_COMMAND = "dependency:analyze";

    default Options getOptions() {
        Option mode = Option.builder(MODE_PARAM)
                .longOpt(String.format("Analyze mode: %1$s", AnalyzeMode.getDescription()))
                .hasArg().build();
        Option pom = Option.builder(POM_PATH_PARAM).longOpt("Project pom.xml path")
                .hasArg().build();
        Options options = new Options();
        options.addOption(mode);
        options.addOption(pom);
        return options;
    }

    default InvocationRequest parseRequest(String args[]) {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = parser.parse(getOptions(), args, true);
        } catch (ParseException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
        if (!commandLine.hasOption(POM_PATH_PARAM)) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Missing project pom.xml specified.");
        }
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(commandLine.getOptionValue(POM_PATH_PARAM)));
        request.setGoals(Collections.singletonList(MVN_COMMAND));
        return request;
    }

    default ByteArrayOutputStream runMVNCommand(InvocationRequest request) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Invoker invoker = new DefaultInvoker();
        invoker.setOutputHandler(new PrintStreamHandler(ps, false));
        try {
            InvocationResult result = invoker.execute(request);
            if (result.getExitCode() != 0) {
                throw new AnalyzeException(ExceptionType.BUILD_FAILURE);
            }
        } catch (MavenInvocationException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
        return baos;
    }

    ByteArrayOutputStream runCommand(String[] args);

    void analyze(ByteArrayOutputStream baos);

}
