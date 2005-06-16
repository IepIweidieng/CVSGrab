package net.sourceforge.cvsgrab;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;

import junit.framework.TestCase;


/**
 * Abstract base class for test cases.
 *
 * @author <a href="jason@zenplex.com">Jason van Zyl</a>
 */
public abstract class AbstractTestCase extends TestCase {
    /**
     * Basedir for all file I/O. Important when running tests from
     * the reactor.
     */
    public String basedir = System.getProperty("basedir");

    /**
     * Constructor.
     */
    public AbstractTestCase(String testName) {
        super(testName);
    }

    /**
     * Get test input file.
     *
     * @param path Path to test input file.
     */
    public String getTestFile(String path) {
        return new File(basedir, path).getAbsolutePath();
    }

    protected Document getDocument(String testFile) throws Exception {
        FileReader fileReader = new FileReader(testFile);
        LineNumberReader lnReader = new LineNumberReader(fileReader);
        StringBuffer sb = new StringBuffer();
        String line = lnReader.readLine();
        while (line != null) {
            sb.append(line);
            line = lnReader.readLine();
        }
        return WebBrowser.getInstance().getDocument(sb.toString());
    }

    protected void logDocument(Document doc) {
        Serializer serializer = new XMLSerializer(System.out, new OutputFormat());
        try {
            DOMSerializer domSerializer = serializer.asDOMSerializer();
            domSerializer.serialize(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void testXpath(Document doc, String xpath) {
        System.out.println();
        System.out.println("Found nodes for xpath " + xpath);
        JXPathContext context = JXPathContext.newContext(doc);
        context.setLenient(true);
        Iterator i = context.iteratePointers(xpath);
        while (i.hasNext()) {
            Pointer pointer = (Pointer) i.next();
            JXPathContext nodeContext = context.getRelativeContext(pointer);
            String text = (String) nodeContext.getValue(".//text()");
            System.out.println(pointer + " " + text);
        }
        System.out.println("---------");
    }
}
