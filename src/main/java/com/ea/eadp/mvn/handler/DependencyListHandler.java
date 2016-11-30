package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.mvn.Dependency;
import javafx.util.Pair;
import org.apache.commons.cli.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User: BichongLi
 * Date: 11/29/2016
 * Time: 11:36 AM
 */
public class DependencyListHandler extends BaseAnalyzeHandler {

    private static final DependencyListHandler instance = new DependencyListHandler();

    private DependencyListHandler() {
    }

    public static DependencyListHandler getInstance() {
        return instance;
    }

    @Override
    public InputStream runCommand(String[] args) {
        ByteArrayOutputStream out = (ByteArrayOutputStream) runMVNCommand(parseRequest(args));
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    List<String> getCommands(CommandLine commandLine) {
        return Collections.singletonList(MVN_COMMAND);
    }

    @Override
    public void analyze(InputStream in) {
        Function<Pair<String, List<String>>, List<Pair<String, List<Dependency>>>> mapStringsToDependencies = p -> {
            List<Dependency> dependencies = p.getValue().stream()
                    .filter(q -> StringPatterns.DEPENDENCY_LINE_PATTERN.matcher(q).find())
                    .map(lineToDependency)
                    .collect(Collectors.toList());
            List<Pair<String, List<Dependency>>> entries = new ArrayList<>();
            entries.add(new Pair<>(p.getKey(), dependencies));
            return entries;
        };
        print(parseMVNCommandOutput(in, p -> p.equals(StringPatterns.START_DEPENDENCY_PRINT),
                p -> p.equals(StringPatterns.MVN_OUTPUT_SEPARATE_LINE), mapStringsToDependencies));
    }
}
