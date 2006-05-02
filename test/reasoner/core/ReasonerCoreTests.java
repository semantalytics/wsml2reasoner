package reasoner.core;

import junit.framework.*;

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
        suite.addTest(RegisterMultipleTimes.suite());
        //$JUnit-END$
        return suite;
    }
    
    
 	
}
