package variant.flight;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledWSMLFlightTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for WSML-Flight variant");
		//$JUnit-BEGIN$
		suite.addTestSuite(Boolean.class);
		suite.addTestSuite(SatisfiablityTest.class);
		suite.addTestSuite(MaciejVTABug2.class);
		suite.addTestSuite(MaciejVTABug.class);
		suite.addTestSuite(SubConceptOfTest.class);
		suite.addTestSuite(DogsworldTest.class);
		//$JUnit-END$
		return suite;
	}

}
