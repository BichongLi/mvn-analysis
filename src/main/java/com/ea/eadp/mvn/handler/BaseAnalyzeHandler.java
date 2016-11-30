package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import com.ea.eadp.mvn.model.mvn.Dependency;
import javafx.util.Pair;
import org.apache.commons.cli.*;
import org.apache.maven.shared.invoker.*;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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
    protected static final String MVN_COMMAND = "dependency:list";
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

    @Override
    public InputStream runCommand(String[] args) {
        ByteArrayOutputStream out = (ByteArrayOutputStream) runMVNCommand(parseRequest(args));
        return new ByteArrayInputStream(out.toByteArray());
    }

    public abstract void analyze(InputStream in);

    protected void print(Map<String, List<Dependency>> dependencyMap) {
        dependencyMap.forEach((k, v) -> {
            System.out.println(StringPatterns.SEPARATE_LINE);
            System.out.println( "[MODULE] "+ k + LINE_SEPARATOR);
            v.forEach(System.out::println);
            System.out.println(StringPatterns.SEPARATE_LINE);
        });
    }

    protected Function<String, Dependency> lineToDependency = p -> {
        Matcher matcher = StringPatterns.DEPENDENCY_LINE_PATTERN.matcher(p);
        matcher.find();
        return new Dependency(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
    };

    protected Function<Pair<String, List<String>>, List<Pair<String, List<Dependency>>>> defaultMapStringsToDependencies = p -> {
        List<Dependency> dependencies = p.getValue().stream()
                .filter(q -> StringPatterns.DEPENDENCY_LINE_PATTERN.matcher(q).find())
                .map(lineToDependency)
                .collect(Collectors.toList());
        List<Pair<String, List<Dependency>>> entries = new ArrayList<>();
        entries.add(new Pair<>(p.getKey(), dependencies));
        return entries;
    };

    protected Map<String, List<Dependency>> parseMVNCommandOutput(InputStream in, Predicate<String> startCollect, Predicate<String> endCollect,
                                                                  Function<Pair<String, List<String>>, List<Pair<String, List<Dependency>>>> mapStringsToDependencies) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        boolean collecting = false;
        Map<String, List<Dependency>> resultMap = new LinkedHashMap<>();
        String currentModule = null;
        List<String> dependencyStrings = new LinkedList<>();
        try {
            while ((line = reader.readLine()) != null) {
                if (collecting) {
                    if (endCollect.test(line)) {
                        collecting = false;
                        List<Pair<String, List<Dependency>>> dependencies =
                                mapStringsToDependencies.apply(new Pair<>(currentModule, dependencyStrings));
                        dependencies.forEach(p -> resultMap.put(p.getKey(), p.getValue()));
                    } else {
                        dependencyStrings.add(line);
                    }
                } else if (startCollect.test(line)) {
                    collecting = true;
                    dependencyStrings = new LinkedList<>();
                } else {
                    Matcher matcher = StringPatterns.MODULE_LINE_PATTERN.matcher(line);
                    if (matcher.find()) {
                        currentModule = matcher.group(1);
                    }
                }
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
            }
        }
        return resultMap;
    }

}
