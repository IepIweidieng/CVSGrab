/*
 * Insito J2EE - Copyright 2003-2004 Finance Active
 */
package net.sourceforge.cvsgrab;

import java.io.File;



public class LiveTest extends AbstractTestCase {

    private File tmpDir;

    public LiveTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        tmpDir = new File(getTestFile("tmp"));
        tmpDir.mkdir();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        cleanDir(tmpDir);
    }
    
    /**
     * Test for Eclipse.org
     */
    public void testEclipse() throws Exception {
        grab("http://dev.eclipse.org/viewcvs/index.cgi/equinox-home/services/?cvsroot=Technology_Project");
        assertContainsFile("index.html");
    }

    /**
     * Test for Sourceforge.net
     */
    public void testSourceforge() throws Exception {
        grab("http://cvs.sourceforge.net/viewcvs.py/cvsgrab/cvsgrab/etc/script");
        assertContainsFile("cvsgrab.bat");
    }
    
    /**
     * Test for Apache.org
     */
    public void testApache() throws Exception {
        grab("http://cvs.apache.org/viewcvs.cgi/ws-fx/wss4j/interop/keys/");
        assertContainsFile("README.txt");
    }

    /**
     * Test for Savannah.gnu.org
     */
    public void testSavannah() throws Exception {
    	grab("http://savannah.gnu.org/cgi-bin/viewcvs/classpath/classpath/faq/?cvsroot=Web");
    	assertContainsFile("faq.html");
    }
    
    /**
     * Test for Netbeans.org
     */
    public void testNetbeans() throws Exception {
    	grab("http://www.netbeans.org/source/browse/javacvs/src/org/netbeans/modules/javacvs/util/");
    	assertContainsFile("Debug.java");
    }
    
    private void assertContainsFile(String name) {
        File f = new File(tmpDir, "test/" + name);
        assertTrue(name + " not found", f.exists());
    }

    private void grab(String url) {
        CVSGrab.main(new String[] {"-url", url, "-packageDir", "test", "-destDir", tmpDir.getName()});
    }
    
    private void cleanDir(File dir) {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                cleanDir(files[i]);
            } else {
                files[i].delete();
            }
        }
        dir.delete();
    }

}
