package reasoner.flight;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ReasonerFlightTests {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	        junit.textui.TestRunner.run(suite());
	}

 	public static Test suite(){
        TestSuite suite = new TestSuite("Test suite for the WSML-Flight reasoner");
        //BaseReasonerTest.evalMethod = 3;
        //$JUnit-BEGIN$
        suite.addTest(DogsworldTest.suite());
        suite.addTest(LordOfRings.suite());
        suite.addTest(MaciejVTABug.suite());
        suite.addTest(MaciejVTABug2.suite());
        suite.addTestSuite(ViolationsTest.class);
        suite.addTestSuite(SimpleInferenceTests.class);
        //$JUnit-END$
        return suite;
    }
 	
}
