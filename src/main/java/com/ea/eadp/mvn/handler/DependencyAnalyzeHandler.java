package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:56 AM
 */
public class DependencyAnalyzeHandler extends BaseAnalyzeHandler {

    private static final String OUTPUT_FOLDER_PARAM = "t";
    private static final String ANALYZE_REPORT_FILENAME = "dependency-analysis.html";
    private static final Pattern ANALYZE_REPORT_PATH_PATTERN = Pattern.compile("^[A-Z]:\\\\.+\\\\(.+)\\\\target\\\\dependency-analysis.html$");

    private static final DependencyAnalyzeHandler instance = new DependencyAnalyzeHandler();

    private DependencyAnalyzeHandler() {
    }

    public static DependencyAnalyzeHandler getInstance() {
        return instance;
    }

    @Override
    protected Options getOptions() {
        Options options = super.getOptions();
        Option targetFolder = Option.builder(OUTPUT_FOLDER_PARAM).longOpt("Output target folder")
                .hasArg().required(false).build();
        options.addOption(targetFolder);
        return options;
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        return Collections.singletonList("dependency:analyze-report");
    }

    @Override
    public void analyze(String[] args) {
        Options options = getOptions();
        CommandLine commandLine = parseCommandLine(args, options);
        checkHelp(commandLine, options, AnalyzeMode.ANALYZE_DEPENDENCY);
        List<String> reports = extractUsefulInfo(runMVNCommand(parseRequest(commandLine)),
                p -> StringPatterns.ANALYZE_REPORT_PATH_PATTERN.matcher(p).find())
                .stream().map(p -> {
                    Matcher matcher = StringPatterns.ANALYZE_REPORT_PATH_PATTERN.matcher(p);
                    matcher.find();
                    return matcher.group(1) + "\\" + ANALYZE_REPORT_FILENAME;
                }).collect(Collectors.toList());
        if (commandLine.hasOption(OUTPUT_FOLDER_PARAM)) {
            String folder = commandLine.getOptionValue(OUTPUT_FOLDER_PARAM);
            String targetFolder;
            if (!folder.endsWith("\\")) targetFolder = folder + "\\";
            else targetFolder = folder;
            createFolderIfNotExists(targetFolder);
            reports.forEach(p -> {
                Matcher matcher = ANALYZE_REPORT_PATH_PATTERN.matcher(p);
                String module;
                if (matcher.find()) {
                    module = matcher.group(1);
                } else {
                    throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, "Error parsing report file path %1$s", p);
                }
                File source = new File(p);
                File dest = new File(String.format("%1$s%2$s-dependency-analysis.html", targetFolder, module));
                try {
                    FileUtils.copyFile(source, dest);
                } catch (IOException e) {
                    throw new AnalyzeException(ExceptionType.INTERNAL_ERROR,
                            "Error copying %1$s to %2$s", source.getAbsolutePath(), dest.getAbsolutePath());
                }
            });
            System.out.println(String.format("Reports have been copied to %1$s", targetFolder));
        }
        System.out.print("Successfully done.");
    }

    private void createFolderIfNotExists(String folder) {
        File dir = new File(folder);
        if (!dir.isDirectory() || !dir.exists()) {
            if (!dir.mkdirs()) {
                throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Error creating folder %1$s", folder);
            }
        }
    }
}
