package reasoner.fol;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ReasonerFOLTests {
	
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
        //requires online connection...
        suite.addTest(new TPTPTest());
        
        suite.addTest(new org.wsml.reasoner.builtin.tptp.TPTPTest());
        //$JUnit-END$
        return suite;
    }
 	
}
