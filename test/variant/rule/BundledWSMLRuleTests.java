package variant.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledWSMLRuleTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for WSML-Rule variant");
		//$JUnit-BEGIN$
		suite.addTest(FunctionSymbolsTest.suite());
		suite.addTest(Martin.suite());
		//$JUnit-END$
		return suite;
	}

}