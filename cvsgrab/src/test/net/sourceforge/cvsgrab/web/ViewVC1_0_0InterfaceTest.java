package net.sourceforge.cvsgrab.web;

import java.util.Properties;

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
public class ViewVC1_0_0InterfaceTest extends AbstractTestCase {

    private ViewVC1_0_0Interface _interface;
    private CVSGrab _grabber;
    
    /**
     * Constructor for ViewCvs1_0InterfaceTest
     * @param testName
     */
    public ViewVC1_0_0InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _grabber = new CVSGrab();
        _interface = new ViewVC1_0_0Interface(_grabber);
        _interface.setRoot("cvsgrab");
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_vc_1_0_0.html");
        _grabber.getWebOptions().setRootUrl("http://cvsgrab.cvs.sourceforge.net/");
        _interface.detect(doc);
        
        assertEquals("ViewVC 1.0.0", _interface.getType());
        assertEquals("cvsgrab", _interface.getRoot());
    }

    
    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_vc_1_0_0_cvsgrab.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".classpath", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.12", files[i++].getVersion());
        
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.7", files[i++].getVersion());
        
        assertEquals(".project", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.3", files[i++].getVersion());
        
        assertEquals(".teamtask_meta", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.3", files[i++].getVersion());
        
        assertEquals("License.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("RELEASE.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.6", files[i++].getVersion());
        
        assertEquals("build.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());
        
        assertEquals("maven.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.7", files[i++].getVersion());
        
        assertEquals("project.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.9", files[i++].getVersion());
        
        assertEquals("project.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.23", files[i++].getVersion());
        
        assertEquals("Expected no more files", i, files.length);
        
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_vc_1_0_0_cvsgrab.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals(".settings", directories[i++]);
        assertEquals("doc", directories[i++]);
        assertEquals("etc", directories[i++]);
        assertEquals("lib", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("web", directories[i++]);
        assertEquals("xdocs", directories[i++]);
        
        assertEquals("Expected no more directories", i, directories.length);
        
    }
 
    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://cvsgrab.cvs.sourceforge.net/cvsgrab/cvsgrab/src/java/", 
            _interface.getDirectoryUrl("http://cvsgrab.cvs.sourceforge.net/", "cvsgrab/src/java"));

        _interface.setVersionTag("RELEASE_2_0_3");
        assertEquals("http://cvsgrab.cvs.sourceforge.net/cvsgrab/cvsgrab/src/java/?pathrev=RELEASE_2_0_3", 
            _interface.getDirectoryUrl("http://cvsgrab.cvs.sourceforge.net/", "cvsgrab/src/java"));
    }
    
    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://cvsgrab.cvs.sourceforge.net/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "cvsgrab/src/java/net/sourceforge/cvsgrab", "cvsgrab");
        RemoteFile file = new RemoteFile("CVSGrab.java", "1.21");
        file.setDirectory(dir);
        file.setInAttic(false);
        assertEquals("http://cvsgrab.cvs.sourceforge.net/*checkout*/cvsgrab/cvsgrab/src/java/net/sourceforge/cvsgrab/CVSGrab.java?revision=1.21", _interface.getDownloadUrl(file));
        
        dir = new RemoteDirectory(repository, "cvsgrab", "cvsgrab");
        file = new RemoteFile("CVSGrab.html", "1.1.1.1");
        file.setDirectory(dir);
        file.setInAttic(true);
        assertEquals("http://cvsgrab.cvs.sourceforge.net/*checkout*/cvsgrab/cvsgrab/CVSGrab.html?revision=1.1.1.1", _interface.getDownloadUrl(file));
    }
    
    public void testGuessWebPropertiesForSourceforge() {
        Properties webProperties = _interface.guessWebProperties("http://cvsgrab.cvs.sourceforge.net/cvsgrab/cvsgrab/");
        assertEquals("http://cvsgrab.cvs.sourceforge.net/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("cvsgrab/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertEquals("cvsgrab", webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
        webProperties = _interface.guessWebProperties("http://cvsgrab.cvs.sourceforge.net/cvsgrab/cvsgrab/src/?hideattic=1&pathrev=RELEASE_2_0_3");
        assertEquals("http://cvsgrab.cvs.sourceforge.net/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("cvsgrab/src/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("RELEASE_2_0_3", webProperties.get(CVSGrab.TAG_OPTION));
        assertEquals("cvsgrab", webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("hideattic=1", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
    }
    
}
