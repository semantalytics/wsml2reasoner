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
		TestSuite suite = new TestSuite("Test suite for the WSML-DL variant");
		//$JUnit-BEGIN$
		suite.addTestSuite(WSML2OWLTest.class);
		suite.addTestSuite(DLSimpleInferenceTests.class);
		suite.addTestSuite(WSMLDLNormalizerTest.class);
		suite.addTestSuite(DLOntologyRegistrationTest.class);
		suite.addTestSuite(CycleTests.class);
		//$JUnit-END$
		return suite;
	}

}
