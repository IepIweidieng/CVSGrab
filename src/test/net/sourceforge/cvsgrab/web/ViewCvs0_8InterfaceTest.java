/*
 * Created on 12 oct. 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.AbstractTestCase;
import net.sourceforge.cvsgrab.RemoteFile;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 12 oct. 2003
 */
public class ViewCvs0_8InterfaceTest extends AbstractTestCase {

    private ViewCvs0_8Interface _interface = new ViewCvs0_8Interface();
    
    /**
     * Constructor for ViewCvs0_8InterfaceTest
     * @param testName
     */
    public ViewCvs0_8InterfaceTest(String testName) {
        super(testName);
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_8.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals("AntRunner.properties", files[i].getName());
        assertEquals("1.1.1.1", files[i++].getVersion());
        assertEquals("CVSGrab.html", files[i].getName());
        assertEquals("1.1.1.1", files[i++].getVersion());
        assertEquals("CVSGrab.jpx", files[i].getName());
        assertEquals("1.2", files[i++].getVersion());
        assertEquals("HotSAX.library", files[i].getName());
        assertEquals("1.1", files[i++].getVersion());
        assertEquals("License.txt", files[i].getName());
        assertEquals("1.1.1.1", files[i++].getVersion());
        assertEquals("build.bat", files[i].getName());
        assertEquals("1.1.1.1", files[i++].getVersion());
        assertEquals("build.xml", files[i].getName());
        assertEquals("1.4", files[i++].getVersion());
        assertEquals("jCVS.library", files[i].getName());
        assertEquals("1.1", files[i++].getVersion());
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_8.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("doc", directories[i++]);
        assertEquals("etc", directories[i++]);
        assertEquals("lib", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("web", directories[i++]);
    }
    
}
