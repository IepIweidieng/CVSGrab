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
 * @created on 12 oct. 2003
 */
public class ViewCvs0_9InterfaceTest extends AbstractTestCase {

    private ViewCvs0_9Interface _interface = new ViewCvs0_9Interface();
    
    /**
     * Constructor for ViewCvs0_9InterfaceTest
     * @param testName
     */
    public ViewCvs0_9InterfaceTest(String testName) {
        super(testName);
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_9_2.html");
        CVSGrab grabber = new CVSGrab();
        grabber.getWebOptions().setRootUrl("http://cvs.apache.org/viewcvs/");
        _interface.detect(grabber, doc);
        
        assertEquals("ViewCVS 0.9.2", _interface.getType());
    }
    
    public void testDetectWithGraph() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_9_2_graph.html");
        CVSGrab grabber = new CVSGrab();
        grabber.getWebOptions().setRootUrl("http://cvs.apache.org/viewcvs/");
        _interface.detect(grabber, doc);
        
        assertEquals("ViewCVS 0.9.2", _interface.getType());
    }

    public void testDetectWithMultipleRoots() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_9_2_multi_roots.html");
        CVSGrab grabber = new CVSGrab();
        grabber.getWebOptions().setRootUrl("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/");
        _interface.detect(grabber, doc);
        
        assertEquals("ViewCVS 0.9.2", _interface.getType());
        assertEquals("ooo4r", _interface.getRoot());
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_9_2.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("INSTALL", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.14", files[i++].getVersion());
        
        assertEquals("LICENSE.APL", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("Makefile", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("build.bat", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());
        
        assertEquals("build.sh", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());
        
        assertEquals("Expected no more files", i, files.length);
        
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_9_2.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("bin", directories[i++]);
        assertEquals("build", directories[i++]);
        assertEquals("contribs", directories[i++]);
        assertEquals("docs", directories[i++]);
        assertEquals("examples", directories[i++]);
        assertEquals("icons", directories[i++]);
        assertEquals("log4jMini", directories[i++]);
        assertEquals("make", directories[i++]);
        assertEquals("packaging", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("tests", directories[i++]);
        
        assertEquals("Expected no more directories", i, directories.length);
        
    }

    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://cvs.apache.org/viewcvs/jakarta-log4j/", _interface.getDirectoryUrl("http://cvs.apache.org/viewcvs/", "jakarta-log4j"));
        _interface.setVersionTag("v_1_1");
        assertEquals("http://cvs.apache.org/viewcvs/jakarta-log4j/?only_with_tag=v_1_1", _interface.getDirectoryUrl("http://cvs.apache.org/viewcvs/", "jakarta-log4j"));
    }
    
    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://cvs.apache.org/viewcvs/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "jakarta-log4j", "log4j");
        RemoteFile file = new RemoteFile("INSTALL", "1.14");
        file.setDirectory(dir);
        file.setInAttic(false);
        assertEquals("http://cvs.apache.org/viewcvs/*checkout*/jakarta-log4j/INSTALL?rev=1.14", _interface.getDownloadUrl(file));
        
        file = new RemoteFile("Makefile", "1.2");
        file.setDirectory(dir);
        file.setInAttic(true);
        assertEquals("http://cvs.apache.org/viewcvs/*checkout*/jakarta-log4j/Attic/Makefile?rev=1.2", _interface.getDownloadUrl(file));
    }
    
    public void testGetFilesWithGraph() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_9_2_graph.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.25", files[i++].getVersion());
        
        assertEquals("BuildCVS.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.34", files[i++].getVersion());
        
        assertEquals("Makefile.in", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.352", files[i++].getVersion());
        
        assertEquals("acinclude.m4", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.14", files[i++].getVersion());
        
        assertEquals("aclocal.m4", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.57", files[i++].getVersion());
        
        assertEquals("autoconf_inc.m4", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.44", files[i++].getVersion());
    }
        
    public void testGetDirectoriesWithGraph() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_9_2_graph.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("art", directories[i++]);
        assertEquals("build", directories[i++]);
        assertEquals("contrib", directories[i++]);
        assertEquals("debian", directories[i++]);
        assertEquals("demos", directories[i++]);
        assertEquals("distrib", directories[i++]);
        assertEquals("docs", directories[i++]);
        assertEquals("include", directories[i++]);
        assertEquals("install", directories[i++]);
        assertEquals("lib", directories[i++]);
        assertEquals("locale", directories[i++]);
        assertEquals("misc", directories[i++]);
        assertEquals("mobile", directories[i++]);
        assertEquals("samples", directories[i++]);
        assertEquals("setup", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("tests", directories[i++]);
        assertEquals("user", directories[i++]);
        assertEquals("utils", directories[i++]);
        assertEquals("wxPython", directories[i++]);
    }
    
    public void testGetDirectoryUrlWithMultipleRoots() throws Exception {
        _interface.setRoot("ooo4r");
        assertEquals("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/ooo4r/?cvsroot=ooo4r", _interface.getDirectoryUrl("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/", "ooo4r"));
        _interface.setVersionTag("jamesgb");
        assertEquals("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/ooo4r/?only_with_tag=jamesgb&cvsroot=ooo4r", _interface.getDirectoryUrl("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/", "ooo4r"));
    }
    
    public void testGetDownloadUrlWithMultipleRoots() throws Exception {
        _interface.setRoot("ooo4r");
        RemoteRepository repository = new RemoteRepository("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "ooo4r", "ooo4r");
        RemoteFile file = new RemoteFile("README.txt", "1.1");
        file.setDirectory(dir);
        file.setInAttic(false);
        assertEquals("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/*checkout*/ooo4r/README.txt?rev=1.1&cvsroot=ooo4r", _interface.getDownloadUrl(file));
    }
    
    public void testGuessWebProperties() {
        Properties webProperties = _interface.guessWebProperties("http://cvs.apache.org/viewcvs/jakarta-log4j/?only_with_tag=v_1_1");
        assertEquals("http://cvs.apache.org/viewcvs/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("jakarta-log4j/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("v_1_1", webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.CVS_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
        webProperties = _interface.guessWebProperties("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/ooo4r/?only_with_tag=jamesgb&cvsroot=ooo4r");
        assertEquals("http://rubyforge.org/cgi-bin/viewcvs/cgi/viewcvs.cgi/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("ooo4r/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("jamesgb", webProperties.get(CVSGrab.TAG_OPTION));
        assertEquals("ooo4r", webProperties.get(CVSGrab.CVS_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
        webProperties = _interface.guessWebProperties("http://savannah.gnu.org/cgi-bin/viewcvs/sed/");
        assertEquals("http://savannah.gnu.org/cgi-bin/viewcvs/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("sed/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.CVS_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
    }
    
    
}
