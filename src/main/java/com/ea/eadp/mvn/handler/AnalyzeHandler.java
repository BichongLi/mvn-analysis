package com.ea.eadp.mvn.handler;

import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import org.apache.maven.shared.invoker.*;

import java.io.InputStream;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 8:46 AM
 */
public interface AnalyzeHandler {

    default InputStream runMVNCommand(InvocationRequest request) {
        Invoker invoker = new DefaultInvoker();
        try {
            InvocationResult result = invoker.execute(request);
            if (result.getExitCode() != 0) {
                throw new AnalyzeException(ExceptionType.BUILD_FAILURE);
            }
        } catch (MavenInvocationException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
        return null;
    }

    InputStream runCommand(String[] args);

    void analyze(InputStream inputStream);

}
