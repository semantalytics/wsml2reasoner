package engine.mins;

import org.wsml.reasoner.api.WSMLReasonerFactory;

import base.BaseReasonerTest;
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
		suite.addTestSuite(Date1.class);
		suite.addTestSuite(IRITest.class);
		//$JUnit-END$
		return suite;
	}

}
