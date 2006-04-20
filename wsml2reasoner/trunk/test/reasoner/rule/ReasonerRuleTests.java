package reasoner.rule;

import test.BaseReasonerTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class ReasonerRuleTests {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	        junit.textui.TestRunner.run(suite());
	}

 	public static Test suite(){
        TestSuite suite = new TestSuite("Test suite for the WSML-Rule reasoner");
        BaseReasonerTest.evalMethod = 1;
        //$JUnit-BEGIN$
        suite.addTest(FunctionSymbolsTest.suite());
        //$JUnit-END$
        return suite;
    }
 	
}
