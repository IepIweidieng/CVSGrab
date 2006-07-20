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
public class CvsWeb2_0InterfaceTest extends AbstractTestCase {

    private CvsWeb2_0Interface _interface;
    private CVSGrab _grabber;

    /**
     * Constructor for CvsWeb2_0InterfaceTest
     * @param testName
     */
    public CvsWeb2_0InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        _grabber = new CVSGrab();
        _interface = new CvsWeb2_0Interface(_grabber);
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_2_0.html");
        _grabber.getWebOptions().setRootUrl("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/");
        _interface.detect(doc);

        assertEquals("FreeBSD-CVSweb 2.0.6", _interface.getType());
    }

    /**
     * Bug #1069355
     * @throws Exception
     */
    public void testDetectSgi() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_2_0_sgi.html");
        CVSGrab grabber = new CVSGrab();
        grabber.getWebOptions().setRootUrl("http://oss.sgi.com/cgi-bin/cvsweb.cgi/");
        _interface.detect(doc);

        assertEquals("FreeBSD-cvsweb 2.0.0", _interface.getType());
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_2_0.html");

        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("Makefile.am", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.13", files[i++].getVersion());

        assertEquals("docbook.toolbar.tgz", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());

        assertEquals("kde_docbook.toolbar.tgz", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);

    }

    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/cvsweb_2_0.html");

//        logDocument(doc);
//        testXpath(doc, "//TR/TD");

        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("cfml", directories[i++]);
        assertEquals("default", directories[i++]);
        assertEquals("docbook", directories[i++]);
        assertEquals("html", directories[i++]);
        assertEquals("kde-docbook", directories[i++]);
        assertEquals("schema", directories[i++]);
        assertEquals("tagxml", directories[i++]);
        assertEquals("wml-1-2", directories[i++]);
        assertEquals("xml", directories[i++]);

        assertEquals("Expected no more directories", i, directories.length);

    }

    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/quanta/quanta/data/toolbars/", _interface.getDirectoryUrl("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/quanta/quanta/data/", "toolbars"));
        _interface.setVersionTag("KDE_3_2_0_ALPHA_1");
        assertEquals("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/quanta/quanta/data/toolbars/?only_with_tag=KDE_3_2_0_ALPHA_1", _interface.getDirectoryUrl("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/quanta/quanta/data/", "toolbars"));
    }

    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "quanta/quanta/data/toolbars", "toolbars");
        RemoteFile file = new RemoteFile("Makefile.am", "1.13");
        file.setDirectory(dir);

        assertEquals("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/~checkout~/quanta/quanta/data/toolbars/Makefile.am?rev=1.13", _interface.getDownloadUrl(file));

        file = new RemoteFile("Makefile", "1.1");
        file.setDirectory(dir);
        file.setInAttic(true);

        assertEquals("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/~checkout~/quanta/quanta/data/toolbars/Attic/Makefile?rev=1.1", _interface.getDownloadUrl(file));

    }

    public void testGuessWebProperties() {
        Properties webProperties = _interface.guessWebProperties("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/quanta/");
        assertEquals("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("quanta/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertNull(webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertNull(webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
        webProperties = _interface.guessWebProperties("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/quanta/?sortby=date;only_with_tag=QUANTA_3_1_BRANCH");
        assertEquals("http://webcvs.kde.org/cgi-bin/cvsweb.cgi/", webProperties.get(CVSGrab.ROOT_URL_OPTION));
        assertEquals("quanta/", webProperties.get(CVSGrab.PACKAGE_PATH_OPTION));
        assertEquals("QUANTA_3_1_BRANCH", webProperties.get(CVSGrab.TAG_OPTION));
        assertNull(webProperties.get(CVSGrab.PROJECT_ROOT_OPTION));
        assertEquals("sortby=date", webProperties.get(CVSGrab.QUERY_PARAMS_OPTION));
    }


}
