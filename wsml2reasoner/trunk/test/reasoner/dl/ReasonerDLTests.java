package reasoner.dl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ReasonerDLTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	        junit.textui.TestRunner.run(suite());
	}

 	public static Test suite(){
        TestSuite suite = new TestSuite("Test suite for the WSML-DL reasoner");
        //$JUnit-BEGIN$
        suite.addTestSuite(SimpleInferenceTests.class);
        //$JUnit-END$
        return suite;
    }
 	
}
