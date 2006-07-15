/*
 * Created on Jul 14, 2006
 */
package net.sourceforge.cvsgrab;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for net.sourceforge.cvsgrab");
        //$JUnit-BEGIN$
        suite.addTestSuite(LiveTest.class);
        suite.addTestSuite(LocalRepositoryTest.class);
        //$JUnit-END$
        suite.addTest(net.sourceforge.cvsgrab.web.AllTests.suite());
        return suite;
    }

}
