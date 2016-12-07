package com.ea.eadp.mvn.utils;

import com.ea.eadp.mvn.model.dependency.*;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 10:03 AM
 */
public class IOUtils {

    private static final XStream xstream = new XStream(new DomDriver("UTF-8"));

    static {
        xstream.processAnnotations(new Class[] {
            TreeNode.class, Dependency.class, DiffResult.class, Diff.class, DependencyWrapper.class
        });
    }

    public static ByteArrayInputStream readFileToInputStream(String filePath) {
        File file = new File(filePath);
        try {
            return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Error opening file %1$s", filePath);
        }
    }

    public static void print(InputStream in) {
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
    }

    public static String inputStreamToString(InputStream in) {
        String result = "";
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            while ((line = reader.readLine()) != null) {
                result += line + "\n";
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
        return result;
    }

    public static void printXMLtoConsole(Object object) {
        printXMLtoFile(object, null);
    }

    public static void printXMLtoFile(Object object, String outputFile) {
        if (outputFile == null) {
            xstream.toXML(object, System.out);
        } else {
            try {
                PrintWriter writer = new PrintWriter(new File(outputFile));
                xstream.toXML(object, writer);
            } catch (FileNotFoundException e) {
                throw new AnalyzeException(ExceptionType.INVALID_REQUEST, e);
            }
        }
    }

    public static TreeNode readTreeXML(String file) {
        TreeNode root = (TreeNode) xstream.fromXML(new File(file));
        fillInParentInfo(root);
        return root;
    }

    private static void fillInParentInfo(TreeNode root) {
        root.getChildren().forEach(n -> n.setParent(root));
        root.getChildren().forEach(IOUtils::fillInParentInfo);
    }

}
