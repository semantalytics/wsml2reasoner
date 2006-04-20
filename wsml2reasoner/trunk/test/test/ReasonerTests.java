package test;

import junit.framework.Test;
import junit.framework.TestSuite;
import reasoner.OntologyRegistrationTest;
import reasoner.core.ReasonerCoreTests;
import reasoner.flight.ReasonerFlightTests;
import reasoner.rule.ReasonerRuleTests;
import wsml2reasoner.normalization.NormalizationTests;
import wsml2reasoner.transformation.TransformationTests;

/**
 * @author nagypal
 */
public class ReasonerTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for the WSML reasoner");
        // $JUnit-BEGIN$
        suite.addTest(ReasonerCoreTests.suite());
        suite.addTest(ReasonerFlightTests.suite());
        suite.addTest(ReasonerRuleTests.suite());
        suite.addTest(NormalizationTests.suite());
        suite.addTest(TransformationTests.suite());
        suite.addTestSuite(OntologyRegistrationTest.class);
        // $JUnit-END$
        return suite;
    }

}