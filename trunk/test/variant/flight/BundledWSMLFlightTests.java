package variant.flight;

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
		TestSuite suite = new TestSuite(
				"Test suite for the WSML-Flight variant");
		//$JUnit-BEGIN$
		suite.addTestSuite(ViolationsTest.class);
		suite.addTestSuite(VariablePositionInTuple.class);
		suite.addTestSuite(Boolean.class);
		suite.addTestSuite(MaciejVTABug2.class);
		suite.addTestSuite(SatisfiablityTest.class);
		suite.addTestSuite(SimpleGraph.class);
		suite.addTestSuite(PreserveTypeTests.class);
		suite.addTestSuite(LordOfRings.class);
		suite.addTestSuite(SubConceptOfSemanticsTest.class);
		suite.addTestSuite(SimpleInferenceTests.class);
		suite.addTestSuite(DogsworldTest.class);
		suite.addTestSuite(AttributeInheritanceTest.class);
		suite.addTestSuite(OntologyRegistrationTest.class);
		suite.addTestSuite(MaciejVTABug.class);
		suite.addTestSuite(ConstraintViolationCheck.class);
		suite.addTestSuite(RegisterMultipleTimes.class);
		//$JUnit-END$
		return suite;
	}

}
