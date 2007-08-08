package variant.core;

import open.SimpleGraph2;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledWSMLCoreTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for the WSML-Core variant");
		//$JUnit-BEGIN$
		suite.addTest(SimpleGraph.suite());
		//$JUnit-END$
		return suite;
	}

}
