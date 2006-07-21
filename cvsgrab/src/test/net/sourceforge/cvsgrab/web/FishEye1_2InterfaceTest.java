package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.AbstractTestCase;
import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.RemoteDirectory;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.RemoteRepository;

import org.w3c.dom.Document;

import java.util.Properties;


/**
 * Test for FishEye 1.2
 *
 * @author <a href="mailto:shinobukawai@users.sourceforge.net">Shinobu Kawai</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created July 20, 2006
 */
public class FishEye1_2InterfaceTest extends AbstractTestCase {

    private FishEye1_2Interface _interface;
    private CVSGrab _grabber;

    /**
     * Constructor for FishEye_1_0InterfaceTest
     * @param testName
     */
    public FishEye1_2InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _grabber = new CVSGrab();
        _interface = new FishEye1_2Interface(_grabber);
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_1_2.html");

        _grabber.getWebOptions().setRootUrl("http://cvs.groovy.codehaus.org/browse/");
        _interface.detect(doc);

        assertEquals("FishEye (1.2beta3 build-161)", _interface.getType());
        
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_1_2.html");

        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".classpath", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.59", files[i++].getVersion());

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
        assertEquals("1.132", files[i++].getVersion());

        assertEquals("project.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.22", files[i++].getVersion());

        assertEquals("project.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.170", files[i++].getVersion());

        assertEquals("TODO-PARSER.txt", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.14", files[i++].getVersion());

        assertEquals("TODO.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.123", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);

    }

    public void testGetFilesEmptyDir() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_1_2_emptydir.html");

        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals("MOP.java", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("NewMetaClass.java", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.5", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);

    }

    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_1_2.html");

        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals(".settings", directories[i++]);
        assertEquals("experimental", directories[i++]);
        assertEquals("lib", directories[i++]);
        assertEquals("security", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("xdocs", directories[i++]);

        assertEquals("Expected no more directories", i, directories.length);
    }

    public void testGetDirectoriesEmptyDir() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_1_2_emptydir.html");

        String[] directories = _interface.getDirectories(doc);

        assertEquals("Expected no directories", 0, directories.length);
    }

    public void testGetDirectoriesNestedDir() throws Exception {
        Document doc = getDocument("src/test/html_docs/fisheye_1_2_nesteddir.html");

        int i = 0;
        String[] directories = _interface.getDirectories(doc);

        assertEquals("groovy", directories[i++]);
        assertEquals("org", directories[i++]);

        assertEquals("Expected no more directories", i, directories.length);
    }

    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://fisheye.codehaus.org/browse/groovy/groovy/groovy-core/", _interface.getDirectoryUrl("http://fisheye.codehaus.org/browse/", "groovy/groovy/groovy-core"));

        _interface.setVersionTag("GROOVY_1_0_JSR_02");
        assertEquals("http://fisheye.codehaus.org/browse/~tag=GROOVY_1_0_JSR_02/groovy/groovy/groovy-core/", _interface.getDirectoryUrl("http://fisheye.codehaus.org/browse/", "groovy/groovy/groovy-core"));
    }

    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://fisheye.codehaus.org/browse/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "groovy/groovy/groovy-core", "groovy/groovy/groovy-core");
        RemoteFile file = new RemoteFile("build.xml", "1.1");
        file.setDirectory(dir);

        assertEquals("http://fisheye.codehaus.org/browse/~raw,r=1.1/groovy/groovy/groovy-core/build.xml", _interface.getDownloadUrl(file));
    }

    public void testGuessWebProperties() {
        Properties webProperties = _interface.guessWebProperties("http://fisheye.codehaus.org/browse/groovy/groovy/groovy-core");
        assertEquals("http://fisheye.codehaus.org/browse/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("groovy/groovy/groovy-core", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("%40hideDeletedFiles=Y", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));

        webProperties = _interface.guessWebProperties("http://fisheye.codehaus.org/browse/~tag=GROOVY_1_0_JSR_02/groovy/groovy/groovy-core");
        assertEquals("http://fisheye.codehaus.org/browse/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("groovy/groovy/groovy-core", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("GROOVY_1_0_JSR_02", webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("%40hideDeletedFiles=Y", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));

        webProperties = _interface.guessWebProperties("http://fisheye.codehaus.org/browse/groovy/groovy/groovy-core?%40hideDeletedFiles=N");
        assertEquals("http://fisheye.codehaus.org/browse/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("groovy/groovy/groovy-core", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("%40hideDeletedFiles=N", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
    }

}
