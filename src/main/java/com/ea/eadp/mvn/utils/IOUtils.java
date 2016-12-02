package com.ea.eadp.mvn.utils;

import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 10:03 AM
 */
public class IOUtils {

    public static ByteArrayInputStream readFileToInputStream(String filePath) {
        File file = new File(filePath);
        try {
            return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Error opening file %1$s", filePath);
        }
    }

    public static void print(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
            }
        }
    }

}
