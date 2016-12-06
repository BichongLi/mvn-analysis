package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.utils.IOUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Collections;
import java.util.List;

/**
 * User: BichongLi
 * Date: 11/27/2016
 * Time: 5:49 PM
 */
public class MavenCommandHandler extends BaseAnalyzeHandler {

    private static final String MVN_COMMAND_PARAM = "c";

    private static final MavenCommandHandler instance = new MavenCommandHandler();

    private MavenCommandHandler() {
    }

    public static MavenCommandHandler getInstance() {
        return instance;
    }

    @Override
    protected Options getOptions() {
        Options options = super.getOptions();
        Option command = Option.builder(MVN_COMMAND_PARAM).longOpt("Maven command")
                .hasArg().required(false).build();
        options.addOption(command);
        return options;
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        if (commandLine.hasOption(MVN_COMMAND_PARAM)) {
            return Collections.singletonList(commandLine.getOptionValue(MVN_COMMAND_PARAM));
        } else {
            return Collections.singletonList(MVN_COMMAND);
        }
    }

    @Override
    public void analyze(String[] args) {
        Options options = getOptions();
        CommandLine commandLine = parseCommandLine(args, options);
        checkHelp(commandLine, options, AnalyzeMode.RUN_COMMAND);
        IOUtils.print(runMVNCommand(parseRequest(commandLine)));
    }
}
