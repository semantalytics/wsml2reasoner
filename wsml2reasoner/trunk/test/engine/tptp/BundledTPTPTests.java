package engine.tptp;

import open.TPTPEntailmentTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledTPTPTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for TPTP engine");
		//$JUnit-BEGIN$
		suite.addTestSuite(TPTPEntailmentTest.class);
		//$JUnit-END$
		return suite;
	}

}
