/*
 * Created on 12 oct. 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.AbstractTestCase;
import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.RemoteDirectory;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.RemoteRepository;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 12 oct. 2003
 */
public class ViewCvs1_0InterfaceTest extends AbstractTestCase {

    private ViewCvs1_0Interface _interface;
    
    /**
     * Constructor for ViewCvs1_0InterfaceTest
     * @param testName
     */
    public ViewCvs1_0InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _interface = new ViewCvs1_0Interface();
        _interface.setRoot("picocontainer");
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0.html");
        CVSGrab grabber = new CVSGrab();
        grabber.setRootUrl("http://cvs.picocontainer.codehaus.org/viewcvs.cgi/");
        _interface.detect(grabber, doc);
        
        assertEquals("ViewCVS 1.0-dev", _interface.getType());
        assertEquals("picocontainer", _interface.getRoot());
    }

    /**
     * Test for bug #2308061, no root defined in the web page,
     * xpath //A/@href[contains(., 'root=')] returns null
     * @throws Exception if the test fails
     */
    public void testDetectBusyBox() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0_busybox.net.html");
        CVSGrab grabber = new CVSGrab();
        grabber.setRootUrl("http://cvs.uclibc.org/cgi-bin/cvsweb/");
        _interface.detect(grabber, doc);
        
        assertEquals("ViewCVS 1.0-dev", _interface.getType());
        assertNull(_interface.getRoot());
    }
    
    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.11", files[i++].getVersion());
        
        assertEquals("LICENSE.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());
        
        assertEquals("TODO.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.5", files[i++].getVersion());
        
        assertEquals("build.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.8", files[i++].getVersion());
        
        assertEquals("continuous-integration.sh", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.15", files[i++].getVersion());
        
        assertEquals("maven.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.16", files[i++].getVersion());
        
        assertEquals("project.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.10", files[i++].getVersion());
        
        assertEquals("project.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.39", files[i++].getVersion());
        
        assertEquals("ClassRegistrationPicoContainer.java", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.4", files[i++].getVersion());
        
        assertEquals("Expected no more files", i, files.length);
        
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("src", directories[i++]);
        assertEquals("xdocs", directories[i++]);
        
        assertEquals("Expected no more directories", i, directories.length);
        
    }
 
    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://cvs.picocontainer.codehaus.org/viewcvs.cgi/pico/src/java/picocontainer/?root=picocontainer", 
            _interface.getDirectoryUrl("http://cvs.picocontainer.codehaus.org/viewcvs.cgi/", "pico/src/java/picocontainer"));
        _interface.setVersionTag("PICOCONTAINER_1_0_ALPHA_1");
        assertEquals("http://cvs.picocontainer.codehaus.org/viewcvs.cgi/pico/src/java/picocontainer/?only_with_tag=PICOCONTAINER_1_0_ALPHA_1&root=picocontainer", 
            _interface.getDirectoryUrl("http://cvs.picocontainer.codehaus.org/viewcvs.cgi/", "pico/src/java/picocontainer"));
    }
    
    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://cvs.picocontainer.codehaus.org/viewcvs.cgi/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "pico/src/java/org/picocontainer", "picocontainer");
        RemoteFile file = new RemoteFile("PicoContainer.java", "1.10");
        file.setDirectory(dir);
        file.setInAttic(false);
        assertEquals("http://cvs.picocontainer.codehaus.org/viewcvs.cgi/*checkout*/pico/src/java/org/picocontainer/PicoContainer.java?rev=1.10&root=picocontainer", _interface.getDownloadUrl(file));
        
        dir = new RemoteDirectory(repository, "pico/src/java/picocontainer", "picocontainer");
        file = new RemoteFile("ClassRegistrationPicoContainer.java", "1.4");
        file.setDirectory(dir);
        file.setInAttic(true);
        assertEquals("http://cvs.picocontainer.codehaus.org/viewcvs.cgi/*checkout*/pico/src/java/picocontainer/Attic/ClassRegistrationPicoContainer.java?rev=1.4&root=picocontainer", _interface.getDownloadUrl(file));
    }
    
    /**
     * Fix for help request https://sourceforge.net/forum/message.php?msg_id=2294014
     */
    public void testStrangeUrls() {
        _interface.setRoot(null);
        String directoryUrl = _interface.getDirectoryUrl("http://cvs.apache.org/viewcvs.cgi/", 
                        "ant/src/etc/testcases/core/include/frag#ment/");
        assertEquals("http://cvs.apache.org/viewcvs.cgi/ant/src/etc/testcases/core/include/frag%23ment/", directoryUrl);
        directoryUrl = _interface.getDirectoryUrl("http://cvs.apache.org/viewcvs.cgi/", 
                        "ant/src/etc/testcases/core/include/with space/");
        assertEquals("http://cvs.apache.org/viewcvs.cgi/ant/src/etc/testcases/core/include/with%20space/", directoryUrl);
    }
}
