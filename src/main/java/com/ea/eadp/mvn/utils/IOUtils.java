package com.ea.eadp.mvn.utils;

import com.ea.eadp.mvn.model.dependency.*;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 10:03 AM
 */
public class IOUtils {

    private static final XStream xstream = new XStream(new DomDriver("UTF-8"));

    static {
        xstream.processAnnotations(new Class[]{
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

    public static String inputStreamToString(InputStream in) {
        String result = null;
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            while ((line = reader.readLine()) != null) {
                if (result == null) result = "";
                else result += "\n";
                result += line;
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
        return result;
    }

    public static TreeNode readTreeXML(String file) {
        TreeNode root = (TreeNode) xstream.fromXML(new File(file));
        fillInParentInfo(root);
        return root;
    }

    public static DependencyWrapper readDependencyXML(String file) {
        return (DependencyWrapper) xstream.fromXML(new File(file));
    }

    public static DiffResult readDiffResultXML(String file) {
        return (DiffResult) xstream.fromXML(new File(file));
    }

    private static void fillInParentInfo(TreeNode root) {
        root.getChildren().forEach(n -> n.setParent(root));
        root.getChildren().forEach(IOUtils::fillInParentInfo);
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

    public static void printXMLtoConsole(Object object) {
        printXML(object, System.out);
    }

    public static void printXMLtoFileByPath(Object object, String filePath) {
        try {
            printXML(object, new PrintWriter(new File(filePath)));
        } catch (FileNotFoundException e) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Error writing to file %1$s", filePath);
        }
    }

    public static void printJSONtoFileByPath(Object object, String filePath) {
        try {
            printJSON(object, new PrintWriter(new File(filePath)));
        } catch (FileNotFoundException e) {
            throw new AnalyzeException(ExceptionType.INVALID_REQUEST, "Error writing to file %1$s", filePath);
        }
    }

    public static void printXML(Object object, OutputStream stream) {
        xstream.toXML(object, stream);
    }

    public static void printXML(Object object, Writer writer) {
        xstream.toXML(object, writer);
    }

    public static void printJSON(Object object, OutputStream stream) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(stream, object);
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
    }

    public static void printJSON(Object object, Writer writer) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(writer, object);
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, e);
        }
    }
}
