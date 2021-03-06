package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.AbstractTestCase;
import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.RemoteDirectory;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.RemoteRepository;

import org.w3c.dom.Document;

import java.util.Properties;


/**
 * Test for FishEye 1.0
 *
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 1 avr. 2004
 */
public class FishEye1_0InterfaceTest extends AbstractTestCase {

    private FishEye1_0Interface _interface;
    private CVSGrab _grabber;

    /**
     * Constructor for FishEye_1_0InterfaceTest
     * @param testName
     */
    public FishEye1_0InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _grabber = new CVSGrab();
        _interface = new FishEye1_0Interface(_grabber);
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_0_8.html");
        
        _grabber.getWebOptions().setRootUrl("http://cvs.codehaus.org/viewrep/");
        _interface.detect(doc);

        //assertEquals("FishEye (0.8.1 build-61)", _interface.getType());
        
        doc = getDocument("src/test/html_docs/fisheye_1_0_1.html");

        _grabber.getWebOptions().setRootUrl("http://cvs.groovy.codehaus.org/viewrep/");
        _interface.detect(doc);

        assertEquals("FishEye (1.0.1 build-78)", _interface.getType());
        
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_0_8.html");

        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.29.2.1", files[i++].getVersion());

        assertEquals("build.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.16", files[i++].getVersion());

        assertEquals("continuous-integration.sh", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.16", files[i++].getVersion());

        assertEquals("getdocs.rb", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.2.2.1", files[i++].getVersion());

        assertEquals("LICENSE.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());

        assertEquals("locator.ent", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.3", files[i++].getVersion());

        assertEquals("massage_site.rb", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.3.2.1", files[i++].getVersion());

        assertEquals("maven.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.26", files[i++].getVersion());

        assertEquals("picocontainer.iml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1.2.2", files[i++].getVersion());

        assertEquals("project.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.25", files[i++].getVersion());

        assertEquals("project.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.103", files[i++].getVersion());

        assertEquals("TODO.txt", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.6", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);

    }

    public void testGetFilesEmptyDir() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_0_8_emptydir.html");

        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals("picosite.zip", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);

    }

    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_0_8.html");

        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("site", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("xdocs", directories[i++]);

        assertEquals("Expected no more directories", i, directories.length);
    }

    public void testGetDirectoriesEmptyDir() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_0_8_emptydir.html");

        String[] directories = _interface.getDirectories(doc);

        assertEquals("Expected no directories", 0, directories.length);
    }

    public void testGetDirectoriesNestedDir() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_0_8_nesteddir.html");

        int i = 0;
        String[] directories = _interface.getDirectories(doc);

        assertEquals("alternatives", directories[i++]);
        assertEquals("composite", directories[i++]);
        assertEquals("defaults", directories[i++]);
        assertEquals("doc", directories[i++]);
        assertEquals("extras", directories[i++]);
        assertEquals("hierarchical", directories[i++]);
        assertEquals("lifecycle", directories[i++]);
        assertEquals("monitors", directories[i++]);
        assertEquals("tck", directories[i++]);
        assertEquals("testmodel", directories[i++]);

        assertEquals("Expected no more directories", i, directories.length);
    }

    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://cvs.codehaus.org/viewrep/picocontainer/java/picocontainer/src/", _interface.getDirectoryUrl("http://cvs.codehaus.org/viewrep/", "picocontainer/java/picocontainer/src"));

        _interface.setVersionTag("MX_PROPOSAL");
        assertEquals("http://cvs.codehaus.org/viewrep/~br=MX_PROPOSAL/picocontainer/java/picocontainer/src/", _interface.getDirectoryUrl("http://cvs.codehaus.org/viewrep/", "picocontainer/java/picocontainer/src"));
    }

    public void testGetFilesGroovy() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_1_0_1.html");

        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".classpath", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.51", files[i++].getVersion());

        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.14", files[i++].getVersion());

        assertEquals(".project", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.10", files[i++].getVersion());

        assertEquals("ASM-LICENSE.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("build.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("GroovyDoc.txt", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.3", files[i++].getVersion());

        assertEquals("LICENSE.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());

        assertEquals("maven.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.124", files[i++].getVersion());

        assertEquals("project.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.20", files[i++].getVersion());

        assertEquals("project.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.146", files[i++].getVersion());

        assertEquals("TODO-PARSER.txt", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.14", files[i++].getVersion());

        assertEquals("TODO.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.123", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);

    }

    public void testGetDirectoriesGroovy() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_1_0_1.html");

        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals(".settings", directories[i++]);
        assertEquals("lib", directories[i++]);
        assertEquals("security", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("xdocs", directories[i++]);

        assertEquals("Expected no more directories", i, directories.length);
    }

    public void testGetDownloadUrl() throws Exception {
        // http://cvs.php.net/co.php/smarty/BUGS?r=1.7&p=1
        RemoteRepository repository = new RemoteRepository("http://cvs.codehaus.org/viewrep/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "picocontainer/java/picocontainer", "picocontainer/java/picocontainer");
        RemoteFile file = new RemoteFile("build.xml", "1.12.2.1");
        file.setDirectory(dir);

        assertEquals("http://cvs.codehaus.org/viewrep/~raw,r=1.12.2.1/picocontainer/java/picocontainer/build.xml", _interface.getDownloadUrl(file));
    }

    public void testGuessWebProperties() {
        Properties webProperties = _interface.guessWebProperties("http://cvs.codehaus.org/viewrep/picocontainer/java/picocontainer");
        assertEquals("http://cvs.codehaus.org/viewrep/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("picocontainer/java/picocontainer", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("hideDeletedFiles=Y", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));

        webProperties = _interface.guessWebProperties("http://cvs.codehaus.org/viewrep/~br=MX_PROPOSAL/picocontainer/java/picocontainer");
        assertEquals("http://cvs.codehaus.org/viewrep/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("picocontainer/java/picocontainer", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("MX_PROPOSAL", webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("hideDeletedFiles=Y", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));

        webProperties = _interface.guessWebProperties("http://cvs.codehaus.org/viewrep/picocontainer/java/picocontainer?hideDeletedFiles=N");
        assertEquals("http://cvs.codehaus.org/viewrep/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("picocontainer/java/picocontainer", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("hideDeletedFiles=N", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
    }

}
