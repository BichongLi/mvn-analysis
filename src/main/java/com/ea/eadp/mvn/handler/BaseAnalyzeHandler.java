package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import com.ea.eadp.mvn.model.mvn.Dependency;
import org.apache.commons.cli.*;
import org.apache.maven.shared.invoker.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: BichongLi
 * Date: 11/28/2016
 * Time: 9:53 AM
 */
public abstract class BaseAnalyzeHandler implements AnalyzeHandler {

    private static final String MODE_PARAM = "m";
    private static final String POM_PATH_PARAM = "p";
    private static final String HELP_PARAM = "h";
    protected static final String COMMAND_PARAM = "c";
    private static final String MAVEN_HOME_PARAM = "mh";
    private static final String JAVA_HOME_PARAM = "jh";
    protected static final String MVN_COMMAND = "dependency:analyze";

    protected static final Pattern MODULE_LINE_PATTERN = Pattern.compile("^\\[INFO\\] *Building (.*)$");
    protected static final Pattern DEPENDENCY_LINE_PATTERN = Pattern.compile("^\\[.+\\] *(.*):(.*):(.*):(.*):(.*)$");

    private static final String SEPARATE_LINE = " ------------------------------------------------------------------------ ";
    protected static final String LINE_SEPARATOR = "\n";

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
        request.setGoals(getCommands(commandLine));
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

    abstract List<String> getCommands(CommandLine commandLine);

    public abstract void analyze(InputStream in);

    protected void print(InputStream in) {
        Map<String, List<Dependency>> dependencyMap = parseInputStream(in);
        dependencyMap.forEach((k, v) -> {
            System.out.println(SEPARATE_LINE);
            System.out.println(k + LINE_SEPARATOR);
            v.forEach(System.out::println);
            System.out.println(SEPARATE_LINE);
        });
    }

    private Map<String, List<Dependency>> parseInputStream(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        Map<String, List<Dependency>> resultMap = new HashMap<>();
        String currentModule = null;
        List<Dependency> dependencies = new ArrayList<>();
        try {
            while ((line = reader.readLine()) != null) {
                Matcher matcher = MODULE_LINE_PATTERN.matcher(line);
                if (matcher.find()) {
                    if (currentModule != null) resultMap.put(currentModule, dependencies);
                    currentModule = matcher.group(1);
                    dependencies = new ArrayList<>();
                }
                matcher = DEPENDENCY_LINE_PATTERN.matcher(line);
                if (matcher.find()) {
                    Dependency dependency = new Dependency(matcher.group(1), matcher.group(2),
                            matcher.group(3), matcher.group(4), matcher.group(5));
                    dependencies.add(dependency);
                }
            }
            if (currentModule != null && !dependencies.isEmpty()) {
                resultMap.put(currentModule, dependencies);
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
        return resultMap;
    }

}
