package engine.spassplust;

import open.SpassPlusTTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledSpassPlusTTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for Spass Plus T engine");
		//$JUnit-BEGIN$
		suite.addTestSuite(SpassPlusTTest.class);
		//$JUnit-END$
		return suite;
	}

}
