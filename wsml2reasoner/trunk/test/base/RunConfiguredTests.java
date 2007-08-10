package base;

import engine.spass.BundledSpassTests;
import engine.tptp.BundledTPTPTests;
import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * Prior to running tests, 
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
        // $JUnit-BEGIN$
        suite.addTest(BundledTPTPTests.suite());
        suite.addTest(BundledSpassTests.suite());
        // $JUnit-END$
        return suite;
    }

}