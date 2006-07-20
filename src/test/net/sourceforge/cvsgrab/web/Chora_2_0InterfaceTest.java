package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.AbstractTestCase;
import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.RemoteDirectory;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.RemoteRepository;

import org.w3c.dom.Document;

import java.util.Properties;


/**
 * Test for Chora 2.0
 *
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 1 avr. 2004
 */
public class Chora_2_0InterfaceTest extends AbstractTestCase {

    private Chora2_0Interface _interface;
    private CVSGrab _grabber;

    /**
     * Constructor for Chora_2_0InterfaceTest
     * @param testName
     */
    public Chora_2_0InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _grabber = new CVSGrab();
        _interface = new Chora2_0Interface(_grabber);
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/chora_2_0.html");
        _grabber.getWebOptions().setRootUrl("http://cvs.php.net/cvs.php/");
        _interface.detect(doc);
        
        assertEquals("Chora 2.x", _interface.getType());
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/chora_2_0.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("BUGS", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.7", files[i++].getVersion());

        assertEquals("ChangeLog", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.289", files[i++].getVersion());
        
        assertEquals("COPYING.lib", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("FAQ", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.39", files[i++].getVersion());
        
        assertEquals("INSTALL", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.12", files[i++].getVersion());

        assertEquals("NEWS", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.449", files[i++].getVersion());
        
        assertEquals("README", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.56", files[i++].getVersion());

        assertEquals("RELEASE_NOTES", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.47", files[i++].getVersion());
        
        assertEquals("TODO", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.28", files[i++].getVersion());
        
        assertEquals("Expected no more files", i, files.length);
        
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/chora_2_0.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("configs", directories[i++]);
        assertEquals("demo", directories[i++]);
        assertEquals("docs", directories[i++]);
        assertEquals("libs", directories[i++]);
        assertEquals("misc", directories[i++]);
        assertEquals("plugins", directories[i++]);
        assertEquals("templates", directories[i++]);
        assertEquals("unit_test", directories[i++]);
        
        assertEquals("Expected no more directories", i, directories.length);
        
    }

    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://cvs.php.net/cvs.php/smarty/", _interface.getDirectoryUrl("http://cvs.php.net/cvs.php/", "smarty"));
    }
    
    public void testGetDownloadUrl() throws Exception {
        // http://cvs.php.net/co.php/smarty/BUGS?r=1.7&p=1
        RemoteRepository repository = new RemoteRepository("http://cvs.php.net/cvs.php/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "smarty", "smarty");
        RemoteFile file = new RemoteFile("BUGS", "1.7");
        file.setDirectory(dir);
        
        assertEquals("http://cvs.php.net/co.php/smarty/BUGS?r=1.7&p=1", _interface.getDownloadUrl(file));
        
        // http://cvs.php.net/co.php/smarty/docs/scripts/.cvsignore?r=1.1&p=1
        repository = new RemoteRepository("http://cvs.php.net/", null);
        dir = new RemoteDirectory(repository, "smarty/docs/scripts", "smarty/docs/scripts");
        file = new RemoteFile(".cvsignore", "1.1");
        file.setDirectory(dir);
        _interface.setBrowsePath("");        
        
        assertEquals("http://cvs.php.net/co.php/smarty/docs/scripts/.cvsignore?r=1.1&p=1", _interface.getDownloadUrl(file));
    }
    
    public void testGuessWebProperties() {
        // 25/06/2005 - they changed the url naming scheme
        Properties webProperties = _interface.guessWebProperties("http://cvs.php.net/smarty/docs/scripts/");
        assertEquals("http://cvs.php.net/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("smarty/docs/scripts/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
        
    }
    
}
