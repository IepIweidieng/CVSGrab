/*
 * Created on 12 oct. 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.sourceforge.cvsgrab.web;

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
public class Sourcecast1_0InterfaceTest extends AbstractTestCase {

    private Sourcecast1_0Interface _interface = new Sourcecast1_0Interface();
    
    /**
     * Constructor for Sourcecast2_0InterfaceTest
     * @param testName
     */
    public Sourcecast1_0InterfaceTest(String testName) {
        super(testName);
    }

    public void testDetect() throws Exception {
        Document doc = getDocument("src/test/html_docs/sourcecast_1_0.html");
        CVSGrab grabber = new CVSGrab();
        grabber.setRootUrl("http://javacvs.netbeans.org/source/browse/");
        _interface.detect(grabber, doc);
        
        assertEquals("SourceCast 1.1.3.000", _interface.getType());
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/sourcecast_1_0.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("build.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.35", files[i++].getVersion());
        
        assertEquals("l10n.list", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.6", files[i++].getVersion());
        
        assertEquals("manifest.mf", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.28", files[i++].getVersion());
        
        assertEquals("nbl10n.list.generated", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.8", files[i++].getVersion());

        assertEquals("Expected no more files", i, files.length);
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/sourcecast_1_0.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("changelog", directories[i++]);
        assertEquals("compat", directories[i++]);
        assertEquals("libmodule", directories[i++]);
        assertEquals("libsrc", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("test", directories[i++]);
        assertEquals("www", directories[i++]);
        
        assertEquals("Expected no more directories", i, directories.length);
        
    }

    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://javacvs.netbeans.org/source/browse/javacvs/", _interface.getDirectoryUrl("http://javacvs.netbeans.org/source/browse/", "javacvs"));
        _interface.setVersionTag("release35R");
        assertEquals("http://javacvs.netbeans.org/source/browse/javacvs/?only_with_tag=release35R", _interface.getDirectoryUrl("http://javacvs.netbeans.org/source/browse/", "javacvs"));
    }
    
    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://javacvs.netbeans.org/source/browse/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "javacvs", "javacvs");
        RemoteFile file = new RemoteFile("build.xml", "1.32.6.1");
        file.setDirectory(dir);
        
        assertEquals("http://javacvs.netbeans.org/source/browse/~checkout~/javacvs/build.xml?rev=1.32.6.1", _interface.getDownloadUrl(file));
        
    }

}
