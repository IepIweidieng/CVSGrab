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
public class CvsWeb1_0InterfaceTest extends AbstractTestCase {

    private CvsWeb1_0Interface _interface;
    private CVSGrab _grabber;
    
    /**
     * Constructor for CvsWeb1_0InterfaceTest
     * @param testName
     */
    public CvsWeb1_0InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _grabber = new CVSGrab();
        _interface = new CvsWeb1_0Interface(_grabber);
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_1_0.html");
        _grabber.getWebOptions().setRootUrl("http://dev.w3.org/cvsweb/");
        _interface.detect(doc);
        
        assertEquals("hennerik CVSweb Revision: 1.1", _interface.getType());
        
        doc = getDocument("src/test/html_docs/cvsweb_1_58.html");
        _grabber.getWebOptions().setRootUrl("http://dev.w3.org/cvsweb/");
        _interface.detect(doc);
        
        assertEquals("hennerik CVSweb Revision: 1.58", _interface.getType());
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_1_0.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals("README.cvs", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());
        
        assertEquals("Expected no more files", i, files.length);
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_1_0.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("classes", directories[i++]);
        assertEquals("classes.EGP", directories[i++]);
        assertEquals("lib", directories[i++]);
        assertEquals("makefiles", directories[i++]);
        
        assertEquals("Expected no more directories", i, directories.length);
    }

    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://dev.w3.org/cvsweb/java/", _interface.getDirectoryUrl("http://dev.w3.org/cvsweb/", "java"));
        _interface.setVersionTag("R_2_0_3_B0");
        assertEquals("http://dev.w3.org/cvsweb/java/?only_with_tag=R_2_0_3_B0", _interface.getDirectoryUrl("http://dev.w3.org/cvsweb/", "java"));
    }
    
    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://dev.w3.org/cvsweb/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "java", "java");
        RemoteFile file = new RemoteFile("README.cvs", "1.1");
        file.setDirectory(dir);
        
        assertEquals("http://dev.w3.org/cvsweb/~checkout~/java/README.cvs?rev=1.1", _interface.getDownloadUrl(file));
    }
    
    public void testGuessWebProperties() {
        Properties webProperties = _interface.guessWebProperties("http://dev.w3.org/cvsweb/java/");
        assertEquals("http://dev.w3.org/cvsweb/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("java/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
        webProperties = _interface.guessWebProperties("http://dev.w3.org/cvsweb/java/?only_with_tag=R_2_0_3_B0");
        assertEquals("http://dev.w3.org/cvsweb/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("java/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("R_2_0_3_B0", webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
    }
    
    public void testGetFilesXPDevelopper() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_1_58.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        
        assertEquals("Makefile", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1.1.1", files[i++].getVersion());
        
        assertEquals("README", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1.1.1", files[i++].getVersion());
        
        assertEquals("lilo.conf", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1.1.1", files[i++].getVersion());
        
        assertEquals("Expected no more files", i, files.length);
    }
    
    public void testGetDirectoriesXPDevelopper() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_1_58.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("Squeak-VM", directories[i++]);
        assertEquals("SqueakNOS", directories[i++]);
        assertEquals("cd", directories[i++]);
        assertEquals("images", directories[i++]);
        assertEquals("include", directories[i++]);
        assertEquals("linux-boot", directories[i++]);
        assertEquals("micro", directories[i++]);
        assertEquals("test", directories[i++]);

        assertEquals("Expected no more directories", i, directories.length);
    }


}
