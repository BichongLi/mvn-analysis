package com.ea.eadp.mvn;

import com.ea.eadp.mvn.handler.AnalyzeHandler;
import com.ea.eadp.mvn.model.common.AnalyzeMode;
import com.ea.eadp.mvn.model.exception.AnalyzeException;

import java.util.Arrays;

/**
 * User: BichongLi
 * Date: 11/23/2016
 * Time: 9:20 AM
 */
public class CommandLineTool {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.print(String.format("Please specify analyze mode: %1$s", AnalyzeMode.getDescription()));
            return;
        }
        AnalyzeHandler handler = parseMode(args[0]);
        handler.analyze(Arrays.copyOfRange(args, 1, args.length));
    }

    private static AnalyzeHandler parseMode(String mode) {
        AnalyzeHandler handler = null;
        try {
            handler = AnalyzeHandlerFactory.getHandler(mode);
        } catch (AnalyzeException e) {
            System.out.println(e.getMessage());
            System.out.println(String.format("Legal analyze mode: %1$s", AnalyzeMode.getDescription()));
            System.exit(1);
        }
        return handler;
    }

}
