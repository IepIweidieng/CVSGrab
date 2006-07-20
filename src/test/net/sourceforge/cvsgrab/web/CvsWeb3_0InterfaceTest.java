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
public class CvsWeb3_0InterfaceTest extends AbstractTestCase {

    private CvsWeb3_0Interface _interface;
    private CVSGrab _grabber;

    /**
     * Constructor for CvsWeb3_0InterfaceTest
     * @param testName
     */
    public CvsWeb3_0InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _grabber = new CVSGrab();
        _interface = new CvsWeb3_0Interface(_grabber);
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_3_0.html");
        _grabber.getWebOptions().setRootUrl("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/maven-jahiawar-plugin/");
        _interface.detect(doc);

        assertEquals("FreeBSD-CVSweb 3.0.4", _interface.getType());
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_3_0.html");

        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());

        assertEquals("maven-jahiawar-plugin.jpx", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("maven.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("plugin.jelly", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.12", files[i++].getVersion());

        assertEquals("plugin.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.5", files[i++].getVersion());

        assertEquals("project.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("project.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.7", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);

    }

    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_3_0.html");

        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("src", directories[i++]);
        assertEquals("xdocs", directories[i++]);

        assertEquals("Expected no more directories", i, directories.length);

    }

    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/maven-jahiawar-plugin/src/", _interface.getDirectoryUrl("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/maven-jahiawar-plugin/", "src"));
        _interface.setVersionTag("JAHIA_4_0_5_PR");
        assertEquals("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/maven-jahiawar-plugin/src/?only_with_tag=JAHIA_4_0_5_PR", _interface.getDirectoryUrl("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/maven-jahiawar-plugin/", "src"));
    }

    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "maven-jahiawar-plugin/", ".");
        RemoteFile file = new RemoteFile("maven.xml", "1.1");
        file.setDirectory(dir);

        assertEquals("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/~checkout~/maven-jahiawar-plugin/maven.xml?rev=1.1", _interface.getDownloadUrl(file));
    }

    public void testGuessWebProperties() {
        Properties webProperties = _interface.guessWebProperties("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/maven-jahiawar-plugin/");
        assertEquals("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("maven-jahiawar-plugin/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
        webProperties = _interface.guessWebProperties("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/maven-jahiawar-plugin/?sortby=date;only_with_tag=JAHIA_4_0_5_PR");
        assertEquals("http://cvspub.jahia.org/cgi-bin/cvsweb.cgi/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("maven-jahiawar-plugin/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("JAHIA_4_0_5_PR", webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("sortby=date", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
    }

    public void testGetFilesJahia() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_3_0_jahia.html");

        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());

        assertEquals("README", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.58.4.2", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);

    }

    public void testGetDirectoriesJahia() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_3_0_jahia.html");

        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("CVS", directories[i++]);
        assertEquals("core", directories[i++]);
        assertEquals("docs", directories[i++]);
        assertEquals("etc", directories[i++]);
        assertEquals("external-search-engine", directories[i++]);
        assertEquals("lib", directories[i++]);
        assertEquals("metadata", directories[i++]);
        assertEquals("patched-slide", directories[i++]);
        assertEquals("pluto", directories[i++]);
        assertEquals("slide", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("test", directories[i++]);
        assertEquals("torque", directories[i++]);
        assertEquals("var", directories[i++]);
        assertEquals("xdocs", directories[i++]);

        assertEquals("Expected no more directories", i, directories.length);

    }


}
