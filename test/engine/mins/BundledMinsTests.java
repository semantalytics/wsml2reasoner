package engine.mins;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledMinsTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for MINS engine");
		//$JUnit-BEGIN$
		suite.addTestSuite(MinsFactsSizeTest.class);
		suite.addTest(Date1.suite());
		suite.addTest(IRITest.suite());
		//$JUnit-END$
		return suite;
	}

}
