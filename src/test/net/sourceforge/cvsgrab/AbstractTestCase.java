package net.sourceforge.cvsgrab;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;


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
        DOMParser parser = new DOMParser();
        XMLInputSource source = new XMLInputSource(null, null, null, new FileInputStream(getTestFile(testFile)), null);
    
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        parser.parse(source);
    
        Document doc = parser.getDocument();
        return doc;        
    }
    
}
