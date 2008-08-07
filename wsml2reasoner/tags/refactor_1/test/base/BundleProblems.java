package base;

import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * Prior to running tests, 
 *
 * @author grahen
 *
 */
public class BundleProblems {

    public static void main(String[] args) {
    	
    	junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for wsml2reasoner (all tests)");
        // $JUnit-BEGIN$ 
        suite.addTest(RunEngineTests.suite());
        // $JUnit-END$
        return suite;
    }

}