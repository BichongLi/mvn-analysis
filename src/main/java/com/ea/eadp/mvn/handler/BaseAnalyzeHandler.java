package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import org.apache.commons.cli.*;
import org.apache.maven.shared.invoker.*;

import java.io.*;
import java.util.Collections;

/**
 * User: BichongLi
 * Date: 11/28/2016
 * Time: 9:53 AM
 */
public abstract class BaseAnalyzeHandler implements AnalyzeHandler {

    protected static final String MODE_PARAM = "m";
    protected static final String POM_PATH_PARAM = "p";
    protected static final String HELP_PARAM = "h";
    protected static final String COMMAND_PARAM = "c";
    protected static final String MAVEN_HOME_PARAM = "mh";
    protected static final String JAVA_HOME_PARAM = "jh";
    protected static final String MVN_COMMAND = "dependency:analyze";

    private static final String LINE_SEPARATOR = "\n";
    private static final String MAVEN_SEPARATE_LINE = "[INFO] ------------------------------------------------------------------------";
    private static final String MODULE_BUILD_PREFIX = "[INFO] Building";
    private static final String WARN_LEVEL_LOG_PREFIX = "[WARNING]";

    protected String mvnHome;

    public Options getOptions() {
        Option mode = Option.builder(MODE_PARAM)
                .longOpt(String.format("Analyze mode: %1$s", AnalyzeMode.getDescription()))
                .hasArg().build();
        Option pom = Option.builder(POM_PATH_PARAM).longOpt("Project pom.xml path")
                .hasArg().build();
        Option command = Option.builder(COMMAND_PARAM).longOpt("Maven command")
                .hasArg().build();
        Option mvnHome = Option.builder(MAVEN_HOME_PARAM).longOpt("Maven home")
                .hasArg().build();
        Option javaHome = Option.builder(JAVA_HOME_PARAM).longOpt("Java home")
                .hasArg().build();
        Option help = Option.builder(HELP_PARAM).longOpt("help").build();
        Options options = new Options();
        options.addOption(mode);
        options.addOption(pom);
        options.addOption(command);
        options.addOption(mvnHome);
        options.addOption(javaHome);
        options.addOption(help);
        return options;
    }

    protected CommandLine parseCommandLine(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(getOptions(), args, true);
        } catch (ParseException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
    }

    protected InvocationRequest parseRequest(String args[]) {
        CommandLine commandLine = parseCommandLine(args);
        if (!commandLine.hasOption(POM_PATH_PARAM)) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Missing project pom.xml specified.");
        }
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(commandLine.getOptionValue(POM_PATH_PARAM)));
        request.setGoals(Collections.singletonList(MVN_COMMAND));
        if (commandLine.hasOption(MAVEN_HOME_PARAM)) mvnHome = commandLine.getOptionValue(MAVEN_HOME_PARAM);
        if (commandLine.hasOption(JAVA_HOME_PARAM)) {
            request.setJavaHome(new File(commandLine.getOptionValue(JAVA_HOME_PARAM)));
        }
        return request;
    }

    protected OutputStream runMVNCommand(InvocationRequest request) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        Invoker invoker = new DefaultInvoker();
        invoker.setOutputHandler(new PrintStreamHandler(ps, false));
        if (mvnHome != null) invoker.setMavenHome(new File(mvnHome));
        try {
            InvocationResult result = invoker.execute(request);
            if (result.getExitCode() != 0) {
                throw new AnalyzeException(ExceptionType.BUILD_FAILURE,
                        "Error running command: mvn %1$s", request.getGoals().get(0));
            }
        } catch (MavenInvocationException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
        return out;
    }

    public abstract void analyze(InputStream in);

    protected String extractUsefulInfo(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;
        boolean isLastSeparateLine = false;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.equals(MAVEN_SEPARATE_LINE) && !isLastSeparateLine) {
                    builder.append(line).append(LINE_SEPARATOR);
                    isLastSeparateLine = true;
                } else if (line.startsWith(MODULE_BUILD_PREFIX) || line.startsWith(WARN_LEVEL_LOG_PREFIX)) {
                    builder.append(line).append(LINE_SEPARATOR);
                    isLastSeparateLine = false;
                }
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, "Error closing BufferedReader.", e);
            }
        }
        return builder.toString();
    }

    protected void print(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append(LINE_SEPARATOR);
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, "Error closing BufferedReader.", e);
            }
        }
        System.out.println(builder.toString());
    }

}
