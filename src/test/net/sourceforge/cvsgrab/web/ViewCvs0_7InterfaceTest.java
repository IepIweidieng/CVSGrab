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
public class ViewCvs0_7InterfaceTest extends AbstractTestCase {

    private ViewCvs0_7Interface _interface;
    
    /**
     * Constructor for ViewCvs0_7InterfaceTest
     * @param testName
     */
    public ViewCvs0_7InterfaceTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        _interface = new ViewCvs0_7Interface();
    }

    public void testGetFiles() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_7.html");
        
        int i = 0;
        RemoteFile[] files = _interface.getFiles(doc);
        assertEquals(".classpath", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.3.2.2", files[i++].getVersion());
        
        assertEquals(".cvsignore", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1.2.1", files[i++].getVersion());
        
        assertEquals(".vcm_meta", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.1.2.1", files[i++].getVersion());
        
        assertEquals("about.html", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.4.2.1", files[i++].getVersion());
        
        assertEquals("ant.jar", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.1.2.1", files[i++].getVersion());
        
        assertEquals("build.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.9.2.3", files[i++].getVersion());
        
        assertEquals("jakarta-ant-1.3-optional.jar", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.1.2.1", files[i++].getVersion());
        
        assertEquals("plugin.jars", files[i].getName());
        assertTrue(files[i].isInAttic());
        assertEquals("1.1.2.2", files[i++].getVersion());
        
        assertEquals("plugin.properties", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.1.2.1", files[i++].getVersion());
        
        assertEquals("plugin.xml", files[i].getName());
        assertFalse(files[i].isInAttic());
        assertEquals("1.5.2.3", files[i++].getVersion());
    }
    
    public void testGetDirectories() throws Exception {
        Document doc = getDocument("src/test/html_docs/view_cvs_0_7.html");
        
        int i = 0;
        String[] directories = _interface.getDirectories(doc);
        assertEquals(".externalToolBuilders", directories[i++]);
        assertEquals("buildfiles", directories[i++]);
        assertEquals("doc", directories[i++]);
        assertEquals("lib", directories[i++]);
        assertEquals("notes", directories[i++]);
        assertEquals("old_root", directories[i++]);
        assertEquals("os", directories[i++]);
        assertEquals("schema", directories[i++]);
        assertEquals("scripts", directories[i++]);
        assertEquals("src", directories[i++]);
        assertEquals("src_ant", directories[i++]);
    }
    
    public void testGetDirectoryUrl() throws Exception {
        assertEquals("http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.ant.core/", _interface.getDirectoryUrl("http://dev.eclipse.org/viewcvs/index.cgi/", "org.eclipse.ant.core/"));
        _interface.setVersionTag("R2_0_1");
        assertEquals("http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.ant.core/?only_with_tag=R2_0_1", _interface.getDirectoryUrl("http://dev.eclipse.org/viewcvs/index.cgi/", "org.eclipse.ant.core/"));
    }
    
    public void testGetDownloadUrl() throws Exception {
        RemoteRepository repository = new RemoteRepository("http://dev.eclipse.org/viewcvs/index.cgi/", null);
        RemoteDirectory dir = new RemoteDirectory(repository, "org.eclipse.ant.core/", "ant-core");
        RemoteFile file = new RemoteFile("about.html", "1.16");
        file.setDirectory(dir);
        
        assertEquals("http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/org.eclipse.ant.core/about.html?rev=1.16", _interface.getDownloadUrl(file));
        
        file = new RemoteFile("ant.jar", "1.1.2.1");
        file.setDirectory(dir);
        file.setInAttic(true);
        
        assertEquals("http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/org.eclipse.ant.core/Attic/ant.jar?rev=1.1.2.1", _interface.getDownloadUrl(file));
        
    }
}
