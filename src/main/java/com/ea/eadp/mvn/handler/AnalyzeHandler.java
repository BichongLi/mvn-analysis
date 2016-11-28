package com.ea.eadp.mvn.handler;

import java.io.InputStream;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:46 AM
 */
public interface AnalyzeHandler {

    InputStream runCommand(String[] args);

    void analyze(InputStream in);

}
