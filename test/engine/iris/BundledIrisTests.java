package engine.iris;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledIrisTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for IRIS engine");
		//$JUnit-BEGIN$
		suite.addTestSuite(BuiltInDateTimeTest.class);
		//$JUnit-END$
		return suite;
	}

}
