/*
 * Created on 12 oct. 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.AbstractTestCase;
import net.sourceforge.cvsgrab.RemoteDirectory;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.RemoteRepository;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 12 oct. 2003
 */
public class CvsWeb2_0InterfaceTest extends AbstractTestCase {

    private CvsWeb2_0Interface _interface = new CvsWeb2_0Interface();
    
    /**
     * Constructor for CvsWeb2_0InterfaceTest
     * @param testName
     */
    public CvsWeb2_0InterfaceTest(String testName) {
        super(testName);
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

}
