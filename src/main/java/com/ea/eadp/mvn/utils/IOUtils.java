package com.ea.eadp.mvn.utils;

import com.ea.eadp.mvn.model.dependency.Dependency;
import com.ea.eadp.mvn.model.dependency.Diff;
import com.ea.eadp.mvn.model.dependency.DiffResult;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 10:03 AM
 */
public class IOUtils {

    private static final XStream xstream = new XStream();

    static {
        xstream.processAnnotations(new Class[] {
            TreeNode.class, Dependency.class, DiffResult.class, Diff.class
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
