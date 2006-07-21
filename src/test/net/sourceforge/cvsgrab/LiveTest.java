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
        grab("http://cvsgrab.cvs.sourceforge.net/cvsgrab/cvsgrab/etc/script/");
        assertContainsFile("cvsgrab.bat");
    }

    /**
     * Test for Savannah.gnu.org
     */
    public void testSavannah() throws Exception {
    	grab("http://cvs.savannah.gnu.org/viewcvs/classpath/classpath/faq/?root=Web");
    	assertContainsFile("faq.html");
    }
    
    /**
     * Test for Netbeans.org
     */
    public void testNetbeans() throws Exception {
    	grab("http://www.netbeans.org/source/browse/javacvs/cvsmodule/src/org/netbeans/modules/versioning/system/cvss/util/");
    	assertContainsFile("Utils.java");
    }
    
    /**
     * Test for dev.java.net
     */
    public void testDevJava() throws Exception {
    	grab("https://swingfx.dev.java.net/source/browse/swingfx/src/net/java/swingfx/common/");
    	assertContainsFile("package.html");
    }

    /**
     * Test for w3
     */
    public void testW3() throws Exception {
    	grab("http://dev.w3.org/cvsweb/Amaya/templates/conf/");
    	assertContainsFile("templates.conf");
    }
    
    /**
     * Test for HelixCommunity
     */
    public void testHelixCommunity() throws Exception {
    	grab("https://helixcommunity.org/viewcvs/cgi/viewcvs.cgi/xiph/www/", "ViewCvs1_0");
    	assertContainsFile("LICENSE.txt");
    }
    
    /**
     * Test for Openoffice
     */
    public void testOpenoffice() throws Exception {
    	grab("http://lingucomponent.openoffice.org/source/browse/lingucomponent/source/lingutil/");
    	assertContainsFile("makefile.mk");
    }
    
    /**
     * Test for Php.net
     */
    public void testPhpNet() throws Exception {
        // ViewVC 1.1-dev
    	grab("http://cvs.php.net/viewvc.cgi/smarty/docs/scripts/");
    	assertContainsFile(".cvsignore");
    }
    
    private void assertContainsFile(String name) {
        File f = new File(tmpDir, "test/" + name);
        assertTrue(name + " not found", f.exists());
    }

    private void grab(String url) {
        CVSGrab.main(new String[] {"-url", url, "-packageDir", "test", "-destDir", tmpDir.getName()});
    }
    
    private void grab(String url, String webInterface) {
        CVSGrab.main(new String[] {"-url", url, "-packageDir", "test", "-destDir", tmpDir.getName(), "-webInterface", webInterface});
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
