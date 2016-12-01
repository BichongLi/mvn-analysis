package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import org.apache.commons.cli.*;
import org.apache.maven.shared.invoker.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * User: BichongLi
 * Date: 11/28/2016
 * Time: 9:53 AM
 */
public abstract class BaseAnalyzeHandler implements AnalyzeHandler {

    private static final String POM_PATH_PARAM = "p";
    private static final String HELP_PARAM = "h";
    private static final String MAVEN_HOME_PARAM = "mh";
    private static final String JAVA_HOME_PARAM = "jh";
    protected static final String MVN_COMMAND = "dependency:list";

    protected String mvnHome;

    protected Options getOptions() {
        Option pom = Option.builder(POM_PATH_PARAM).longOpt("Project pom.xml path")
                .hasArg().build();
        Option mvnHome = Option.builder(MAVEN_HOME_PARAM).longOpt("Maven home")
                .hasArg().build();
        Option javaHome = Option.builder(JAVA_HOME_PARAM).longOpt("Java home")
                .hasArg().build();
        Option help = Option.builder(HELP_PARAM).longOpt("help").build();
        Options options = new Options();
        options.addOption(pom);
        options.addOption(mvnHome);
        options.addOption(javaHome);
        options.addOption(help);
        return options;
    }

    protected void checkHelp(CommandLine commandLine, Options options, AnalyzeMode mode) {
        if (commandLine.hasOption(HELP_PARAM) || commandLine.getOptions().length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(String.format("Analyze mode %1$s", mode.getName()), options, true);
            System.exit(1);
        }
    }

    abstract List<String> getCommands(CommandLine commandLine);

    protected InvocationRequest parseRequest(CommandLine commandLine) {
        if (!commandLine.hasOption(POM_PATH_PARAM)) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Missing project pom.xml specified.");
        }
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(commandLine.getOptionValue(POM_PATH_PARAM)));
        request.setGoals(getCommands(commandLine));
        if (commandLine.hasOption(MAVEN_HOME_PARAM)) mvnHome = commandLine.getOptionValue(MAVEN_HOME_PARAM);
        if (commandLine.hasOption(JAVA_HOME_PARAM)) {
            request.setJavaHome(new File(commandLine.getOptionValue(JAVA_HOME_PARAM)));
        }
        return request;
    }

    protected CommandLine parseCommandLine(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args, true);
        } catch (ParseException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
    }

    protected InputStream runMVNCommand(InvocationRequest request) {
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

        return new ByteArrayInputStream(out.toByteArray());
    }

    protected void print(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
            }
        }
    }

    protected List<String> extractUsefulInfo(InputStream in, Predicate<String> isUseful) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> usefulLines = new LinkedList<>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (isUseful.test(line)) usefulLines.add(line);
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
            }
        }
        return usefulLines;
    }

}
