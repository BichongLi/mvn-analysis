package com.ea.eadp.mvn.utils;

import com.ea.eadp.mvn.TreeBaseTest;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.URL;

/**
 * User: BichongLi
 * Date: 12/7/2016
 * Time: 11:07 AM
 */
public class IOUtilsTest extends TreeBaseTest {

    @Test
    public void testReadFile() throws FileNotFoundException {
        String expected = "test correctly read this file";
        ByteArrayInputStream inputStream = IOUtils.readFileToInputStream(getPath("testFile"));
        Assert.assertNotNull(inputStream);
        String actual = IOUtils.inputStreamToString(inputStream);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadTreeXML() throws FileNotFoundException {
        TreeNode root = IOUtils.readTreeXML(getPath("tree.xml"));
        validateTree(root, "com.ea.eadp:mvn-analysis:jar:1.0-SNAPSHOT", 3, 7);
    }

    //TODO: Supposed to use XMLUnit to compare two xml strings, but logic is too complicated
    @Test
    public void testPrintXML() throws IOException, SAXException, ParserConfigurationException {
        TreeNode root = IOUtils.readTreeXML(getPath("tree.xml"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.printXML(root, outputStream);
        Assert.assertNotNull(outputStream.toString());
        Assert.assertTrue(validateXML(new ByteArrayInputStream(outputStream.toByteArray()), "tree.xsd"));
    }

    private boolean validateXML(InputStream stream, String xsdFileName) throws IOException {
        try {
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(getFile(xsdFileName));
            Validator validator = schema.newValidator();
            Source source = new StreamSource(stream);
            validator.validate(source);
        } catch (SAXException e) {
            return false;
        }
        return true;
    }

    private File getFile(String resourceFileName) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(resourceFileName);
        if (url == null) throw new FileNotFoundException();
        return new File(url.getFile());
    }

    private String getPath(String resourceFileName) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(resourceFileName);
        if (url != null) {
            return url.getFile();
        }
        throw new FileNotFoundException();
    }

}
