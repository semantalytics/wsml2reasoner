package reasoner.core;

import test.BaseReasonerTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class ReasonerCoreTests {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	        junit.textui.TestRunner.run(suite());
	}

 	public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for the WSML-Core reasoner");
        //$JUnit-BEGIN$
        suite.addTest(SimpleGraph.suite());
        suite.addTest(SimpleGraph2.suite());
        //$JUnit-END$
        return suite;
    }
 	
}
