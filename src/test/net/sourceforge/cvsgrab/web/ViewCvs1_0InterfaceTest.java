package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.AbstractTestCase;
import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.RemoteDirectory;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.RemoteRepository;

import org.w3c.dom.Document;

import java.util.Properties;

/**
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 12 oct. 2003
 */
public class ViewCvs1_0InterfaceTest extends AbstractTestCase {

    private ViewCvs1_0Interface _interface;
    private CVSGrab _grabber;
    
    /**
     * Constructor for ViewCvs1_0InterfaceTest
     * @param testName
     */
    public ViewCvs1_0InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _grabber = new CVSGrab();
        _interface = new ViewCvs1_0Interface(_grabber);
        _interface.setRoot("picocontainer");
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0.html");
        _grabber.getWebOptions().setRootUrl("http://cvs.picocontainer.codehaus.org/viewcvs.cgi/");
        _interface.detect(doc);
        
        assertEquals("ViewCVS 1.0-dev", _interface.getType());
        assertEquals("picocontainer", _interface.getRoot());
        
        doc = getDocument("src/test/html_docs/view_cvs_1_0_maven.html");
        _grabber.getWebOptions().setRootUrl("http://cvs.apache.org/viewcvs.cgi/");
        _interface.detect(doc);
        
        assertEquals("ViewCVS 1.0-dev", _interface.getType());
        assertNull(_interface.getRoot());
    }

    /**
     * Test for bug #2308061, no root defined in the web page,
     * xpath //A/@href[contains(., 'root=')] returns null
     * @throws Exception if the test fails
     */
    public void testDetectBusyBox() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0_busybox.net.html");
        _grabber.getWebOptions().setRootUrl("http://cvs.uclibc.org/cgi-bin/cvsweb/");
        _interface.detect(doc);
        
        assertEquals("ViewCVS 1.0-dev", _interface.getType());
        assertNull(_interface.getRoot());
    }
    
    /**
     * Test for bug #1024587, cannot download file in Attic, caused by bad detection of attic flag  
     * @throws Exception if the test fails
     */
    public void testFileInAttic() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0_attic.html");
        CVSGrab grabber = new CVSGrab();
        grabber.getWebOptions().setRootUrl("http://cvs.apache.org/viewcvs.cgi/");
        grabber.getWebOptions().setVersionTag("JCS_1_0");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals("ILateralCacheTCPListener.java", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.1.1.1", files[i++].getVersion());
        
        assertEquals("Expected no more files", i, files.length);
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
 
    /**
     * Bug #1077452
     */
    public void testGetFilesMaven() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0_maven.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.20", files[i++].getVersion());
        
        assertEquals("KEYS", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("LICENSE.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.6", files[i++].getVersion());
        
        assertEquals("NOTICE.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("README.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("build-bootstrap.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.219", files[i++].getVersion());
        
        assertEquals("checkstyle-license.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("checkstyle.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.4", files[i++].getVersion());
        
        assertEquals("gump.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.9", files[i++].getVersion());
        
        assertEquals("maven.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.109", files[i++].getVersion());
        
        assertEquals("plugin-profile.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.4", files[i++].getVersion());
        
        assertEquals("plugins-site.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.3", files[i++].getVersion());
        
        assertEquals("project.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.69", files[i++].getVersion());
        
        assertEquals("project.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.343", files[i++].getVersion());
        
        assertEquals("release.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("Expected no more files", i, files.length);
        
    }
    
    /**
     * Bug #1077452
     */
    public void testGetDirectoriesMaven() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_1_0_maven.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("announcements", directories[i++]);
        assertEquals("conf", directories[i++]);
        assertEquals("maven-jelly-tags", directories[i++]);
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
    
    public void testGuessWebProperties() {
        Properties webProperties = _interface.guessWebProperties("http://cvs.apache.org/viewcvs.cgi/ant/");
        assertEquals("http://cvs.apache.org/viewcvs.cgi/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("ant/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
        webProperties = _interface.guessWebProperties("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/ooo4r/?only_with_tag=jamesgb&cvsroot=ooo4r");
        assertEquals("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("ooo4r/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("jamesgb", webProperties.get(CVSGrab.TAG_OPTION));
        assertEquals("ooo4r", webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
    }
    
}
