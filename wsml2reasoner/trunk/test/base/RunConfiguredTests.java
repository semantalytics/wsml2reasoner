package base;

import junit.framework.Test;
import junit.framework.TestSuite;
import engine.spass.BundledSpassTests;
import engine.tptp.BundledTPTPTests;
/**
 * These tests are dependent upon certain configuration settings
 *
 * @author grahen
 *
 */
public class RunConfiguredTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for wsml2reasoner tests with dependencies");
        
        //The following 2 suites are dependent upon a remote reasoner
        suite.addTest(BundledTPTPTests.suite());
        suite.addTest(BundledSpassTests.suite());
        //Must specify the local path for locator
//        suite.addTestSuite(ImportOntologyTest.class);
        
        return suite;
    }

}