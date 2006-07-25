package net.sourceforge.cvsgrab.web;

import java.util.Properties;

import net.sourceforge.cvsgrab.AbstractTestCase;
import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.RemoteDirectory;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.RemoteRepository;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:shinobukawai@users.sourceforge.net">Shinobu Kawai</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created July 24, 2006
 */
public class ViewVC1_1InterfaceTest extends AbstractTestCase {

    private ViewVC1_1Interface _interface;
    private CVSGrab _grabber;
    
    /**
     * Constructor for ViewCvs1_0InterfaceTest
     * @param testName
     */
    public ViewVC1_1InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _grabber = new CVSGrab();
        _interface = new ViewVC1_1Interface(_grabber);
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_vc_1_1.html");
        _grabber.getWebOptions().setRootUrl("http://cvsgrab.cvs.sourceforge.net/");
        _interface.detect(doc);
        
        assertEquals("ViewVC 1.1-dev", _interface.getType());
    }

    
    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_vc_1_1.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals("bookinfo.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.3", files[i++].getVersion());
        
        assertEquals("getting-started.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());
        
        assertEquals("language-defs.ent", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());
        
        assertEquals("language-snippets.ent", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("livedocs.ent", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());
        
        assertEquals("preface.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);
        
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_vc_1_1.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("appendixes", directories[i++]);
        assertEquals("designers", directories[i++]);
        assertEquals("programmers", directories[i++]);
        
        assertEquals("Expected no more directories", i, directories.length);
        
    }
 
    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://cvs.php.net/viewvc.cgi/smarty/docs/it/designers/", 
            _interface.getDirectoryUrl("http://cvs.php.net/viewvc.cgi/", "smarty/docs/it/designers"));

        _interface.setVersionTag("Smarty_2_6_13");
        assertEquals("http://cvs.php.net/viewvc.cgi/smarty/docs/it/designers/?pathrev=Smarty_2_6_13", 
            _interface.getDirectoryUrl("http://cvs.php.net/viewvc.cgi/", "smarty/docs/it/designers"));
    }
    
    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://cvs.php.net/viewvc.cgi/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "smarty/docs/it", "it");
        RemoteFile file = new RemoteFile("bookinfo.xml", "1.3");
        file.setDirectory(dir);
        file.setInAttic(false);
        assertEquals("http://cvs.php.net/viewvc.cgi/smarty/docs/it/bookinfo.xml?revision=1.3", _interface.getDownloadUrl(file));
        
        dir = new RemoteDirectory(repository, "smarty/docs/it", "it");
        file = new RemoteFile("language-snippets.ent", "1.1");
        file.setDirectory(dir);
        file.setInAttic(true);
        assertEquals("http://cvs.php.net/viewvc.cgi/smarty/docs/it/language-snippets.ent?revision=1.1", _interface.getDownloadUrl(file));
    }

    public void testGuessWebProperties() {
        Properties webProperties = _interface.guessWebProperties("http://cvs.php.net/viewvc.cgi/smarty/docs/it/");
        assertEquals("http://cvs.php.net/viewvc.cgi/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("smarty/docs/it/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));

        webProperties = _interface.guessWebProperties("http://cvs.php.net/viewvc.cgi/smarty/docs/it/?hideattic=0&pathrev=Smarty_2_6_13");
        assertEquals("http://cvs.php.net/viewvc.cgi/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("smarty/docs/it/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("Smarty_2_6_13", webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("hideattic=0", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
    }

}
