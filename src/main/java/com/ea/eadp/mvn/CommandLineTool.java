package com.ea.eadp.mvn;

import org.apache.commons.cli.*;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;

/**
 * User: BichongLi
 * Date: 11/23/2016
 * Time: 9:20 AM
 */
public class CommandLineTool {

    private static final String POM_FILE_PARAM = "p";
    private static final String MAVEN_HOME_PARAM = "m";
    private static final String HELP_PARAM = "h";

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            Options options = initiateCommandOptions();
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption(HELP_PARAM)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Analyze maven dependencies", options, true);
                return;
            }

            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(new File(commandLine.getOptionValue(POM_FILE_PARAM)));
            request.setGoals(Collections.singletonList("dependency:list"));

            Invoker invoker = new DefaultInvoker();
            if (commandLine.hasOption(MAVEN_HOME_PARAM)) {
                invoker.setMavenHome(new File(commandLine.getOptionValue(MAVEN_HOME_PARAM)));
            }
            invoker.execute(request);
        } catch (ParseException | MavenInvocationException e) {
            e.printStackTrace();
        }
    }

    private static Options initiateCommandOptions() {
        Option pomFile = Option.builder(POM_FILE_PARAM).argName("pom").desc("Project pom.xml file path")
                .hasArg().build();
        Option mavenHome = Option.builder(MAVEN_HOME_PARAM).argName("mavenHome").desc("Maven home")
                .hasArg().build();
        Option help = Option.builder(HELP_PARAM).longOpt("help").build();
        Options options = new Options();
        options.addOption(help);
        options.addOption(pomFile);
        options.addOption(mavenHome);
        return options;
    }

}
