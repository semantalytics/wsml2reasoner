package framework.registration;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledRegistrationTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for registration");
		//$JUnit-BEGIN$
		suite.addTestSuite(ReasonerCreationTest.class);
		//$JUnit-END$
		return suite;
	}

}
