package net.sourceforge.cvsgrab;

import net.sourceforge.cvsgrab.web.Sourcecast2_0Interface;
import net.sourceforge.cvsgrab.web.ViewCvs0_7Interface;
import net.sourceforge.cvsgrab.web.ViewCvs0_8Interface;
import net.sourceforge.cvsgrab.web.ViewCvs0_9Interface;
import net.sourceforge.cvsgrab.web.ViewCvs1_0Interface;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public class CvsWebInterfaceTest extends AbstractTestCase {

    /**
     * Constructor for CvsWebInterfaceTest
     * @param testName
     */
    public CvsWebInterfaceTest(String testName) {
        super(testName);
    }
    
    public void testDetectViewCvs0_7() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_7.html");
        
        CvsWebInterface webInterface = CvsWebInterface.findInterface(doc);
        assertTrue("Was " + webInterface,  webInterface instanceof ViewCvs0_7Interface);
    }

    public void testDetectViewCvs0_8() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_8.html");
        
        CvsWebInterface webInterface = CvsWebInterface.findInterface(doc);
        assertTrue("Was " + webInterface,  webInterface instanceof ViewCvs0_8Interface);
    }

    public void testDetectViewCvs0_9() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_9_2.html");
        
        CvsWebInterface webInterface = CvsWebInterface.findInterface(doc);
        assertTrue("Was " + webInterface,  webInterface instanceof ViewCvs0_9Interface);
    }

    public void testDetectViewCvs1_0() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0.html");
        
        CvsWebInterface webInterface = CvsWebInterface.findInterface(doc);
        assertTrue("Was " + webInterface,  webInterface instanceof ViewCvs1_0Interface);
    }

    public void testDetectSourceCast2_0() throws Exception {
        Document doc = getDocument("src/test/html_docs/sourcecast_2_0.html");
        
        CvsWebInterface webInterface = CvsWebInterface.findInterface(doc);
        assertTrue("Was " + webInterface,  webInterface instanceof Sourcecast2_0Interface);
    }
}
