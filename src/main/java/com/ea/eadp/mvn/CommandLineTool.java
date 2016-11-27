package com.ea.eadp.mvn;

import com.ea.eadp.mvn.handler.AnalyzeHandler;
import com.ea.eadp.mvn.handler.DependencyAnalyzeHandler;
import com.ea.eadp.mvn.handler.MavenCommandHandler;
import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
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

    private static final String HELP_PARAM = "h";
    private static final String MODE_PARAM = "m";

    public static void main(String[] args) {
        CommandLine commandLine = preParseArgs(args);
        AnalyzeHandler handler = AnalyzeHandlerFactory.getHandler(commandLine.getOptionValue(MODE_PARAM));
        handler.analyze(handler.runCommand(args));
    }

    private static CommandLine preParseArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = MavenCommandHandler.getInstance().getOptions();
        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args, true);
        } catch (ParseException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }

        if (commandLine.hasOption(HELP_PARAM)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Analyze maven dependencies", options, true);
            System.exit(1);
        }
        if (!commandLine.hasOption(MODE_PARAM)) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Missing mode specified.");
        }
        return commandLine;
    }

}
