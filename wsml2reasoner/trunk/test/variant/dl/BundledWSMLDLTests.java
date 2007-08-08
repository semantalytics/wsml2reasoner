package variant.dl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledWSMLDLTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for the WSML-Core variant");
		//$JUnit-BEGIN$
		suite.addTestSuite(SimpleInferenceTests.class);
		suite.addTestSuite(CycleTests.class);
		suite.addTestSuite(DLOntologyRegistrationTest.class);
		//$JUnit-END$
		return suite;
	}

}
