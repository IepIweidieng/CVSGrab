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
public class Sourcecast2_0InterfaceTest extends AbstractTestCase {

    private Sourcecast2_0Interface _interface = new Sourcecast2_0Interface();
    
    /**
     * Constructor for Sourcecast2_0InterfaceTest
     * @param testName
     */
    public Sourcecast2_0InterfaceTest(String testName) {
        super(testName);
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/sourcecast_2_0.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
        
        assertEquals("LICENSE.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.3", files[i++].getVersion());
        
        assertEquals("README.html", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.5", files[i++].getVersion());
        
        assertEquals("RELEASE-NOTES.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.9", files[i++].getVersion());
        
        assertEquals("build.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.7", files[i++].getVersion());
        
        assertEquals("default.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.9", files[i++].getVersion());
        
        assertEquals("todo.txt", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.2", files[i++].getVersion());
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/sourcecast_2_0.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals("com", directories[i++]);
        assertEquals("docs", directories[i++]);
        assertEquals("formsdemo-0.9.9", directories[i++]);
        assertEquals("jgoodies", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("www", directories[i++]);
    }

    public void testGetDirectoryUrl() throws Exception {
        assertEquals("https://forms.dev.java.net/source/browse/forms/", _interface.getDirectoryUrl("https://forms.dev.java.net/source/browse/", "forms"));
        _interface.setVersionTag("HEAD");
        assertEquals("https://forms.dev.java.net/source/browse/forms/?only_with_tag=HEAD", _interface.getDirectoryUrl("https://forms.dev.java.net/source/browse/", "forms"));
    }
    
    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("https://forms.dev.java.net/source/browse/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "forms");
        RemoteFile file = new RemoteFile("LICENSE.txt", "1.3");
        file.setDirectory(dir);
        
        assertEquals("https://forms.dev.java.net/source/browse/*checkout*/forms/LICENSE.txt?rev=1.3", _interface.getDownloadUrl(file));
        
        file = new RemoteFile("ant.jar", "1.1.2.1");
        file.setDirectory(dir);
        file.setInAttic(true);
        
        assertEquals("https://forms.dev.java.net/source/browse/*checkout*/forms/Attic/ant.jar?rev=1.1.2.1", _interface.getDownloadUrl(file));
        
    }

}