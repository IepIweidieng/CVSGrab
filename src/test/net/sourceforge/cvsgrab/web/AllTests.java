/*
 * Created on Jul 14, 2006
 */
package net.sourceforge.cvsgrab.web;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for net.sourceforge.cvsgrab.web");
        //$JUnit-BEGIN$
        suite.addTestSuite(CvsWeb1_0InterfaceTest.class);
        suite.addTestSuite(CvsWeb2_0InterfaceTest.class);
        suite.addTestSuite(Chora_2_0InterfaceTest.class);
        suite.addTestSuite(Sourcecast1_0InterfaceTest.class);
        suite.addTestSuite(FishEye_1_0InterfaceTest.class);
        suite.addTestSuite(Sourcecast3_0InterfaceTest.class);
        suite.addTestSuite(Sourcecast2_0InterfaceTest.class);
        suite.addTestSuite(CvsWeb3_0InterfaceTest.class);
        suite.addTestSuite(ViewCvs0_9InterfaceTest.class);
        suite.addTestSuite(ViewCvs0_8InterfaceTest.class);
        suite.addTestSuite(ViewCvs1_0InterfaceTest.class);
        suite.addTestSuite(ViewCvs0_7InterfaceTest.class);
        suite.addTestSuite(ViewVC1_0_0InterfaceTest.class);
        //$JUnit-END$
        return suite;
    }

}
