package variant.flight;

import org.wsml.reasoner.api.WSMLReasonerFactory;

import base.BaseReasonerTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BundledWSMLFlightTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for WSML-Flight variant");
		//$JUnit-BEGIN$
		BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.IRIS;
		suite.addTestSuite(SatisfiablityTest.class);
		suite.addTest(MaciejVTABug.suite());
		suite.addTest(SubConceptOfTest.suite());
		suite.addTest(DogsworldTest.suite());
		//$JUnit-END$
		return suite;
	}

}
