package framework.normalization;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledNormalizationTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for normalization");
		//$JUnit-BEGIN$
		suite.addTestSuite(WSMLDLNormalizerTest.class);
		suite.addTestSuite(DataValuesTest.class);
		suite.addTestSuite(ConstructReductionNormalizerTest.class);
		suite.addTestSuite(LloydToporNormalizerTest.class);
		suite.addTestSuite(AnonymousIDReplacementTest.class);
		//$JUnit-END$
		return suite;
	}

}
