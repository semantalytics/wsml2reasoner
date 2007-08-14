package framework.transformation;

import variant.dl.DataValuesTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledTransformationTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for transformation");
		//$JUnit-BEGIN$
		suite.addTestSuite(DebugTransformationsTest.class);
		suite.addTestSuite(AnonymousIdUtilsTest.class);
		//$JUnit-END$
		return suite;
	}

}
