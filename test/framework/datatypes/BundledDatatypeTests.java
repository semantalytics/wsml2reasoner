package framework.datatypes;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledDatatypeTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for datatypes");
		//$JUnit-BEGIN$
		suite.addTestSuite(AttributeRangeTest.class);
		suite.addTestSuite(Boolean.class);
		//$JUnit-END$
		return suite;
	}

}
