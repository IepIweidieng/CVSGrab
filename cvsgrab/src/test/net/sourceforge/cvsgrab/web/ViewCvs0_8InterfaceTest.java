/*
 * Created on 12 oct. 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.AbstractTestCase;
import net.sourceforge.cvsgrab.RemoteDirectory;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.RemoteRepository;

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
        
        assertEquals("Expected no more files", i, files.length);
        
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
        
        assertEquals("Expected no more directories", i, directories.length);
        
    }
    
    /**
     * Fix for bug #853915
     */
    public void testStrangeUrls() {
        RemoteRepository repository = new RemoteRepository("http://cvs.sourceforge.net/viewcvs.py/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "avantgarde/AvantGarde/src/st/fr/cageauxtrolls/avantgarde/gestion/partie/", "partie");
        RemoteFile file = new RemoteFile("RestrictionsArmée.java", "1.1");
        file.setDirectory(dir);
        String fileUrl = _interface.getDownloadUrl(file);
        assertEquals("http://cvs.sourceforge.net/viewcvs.py/*checkout*/avantgarde/AvantGarde/src/st/fr/cageauxtrolls/avantgarde/gestion/partie/RestrictionsArm%E9e.java?rev=1.1", fileUrl);
    }
    
}
