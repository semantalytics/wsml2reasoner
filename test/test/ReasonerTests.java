package test;

import junit.framework.Test;
import junit.framework.TestSuite;
import reasoner.OntologyRegistrationTest;
import reasoner.core.ReasonerCoreTests;
import reasoner.dl.ReasonerDLTests;
import reasoner.flight.ReasonerFlightTests;
import reasoner.fol.ReasonerFOLTests;
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
        BaseReasonerTest.evalMethod=3;
        suite.addTest(TransformationTests.suite());
        suite.addTest(NormalizationTests.suite());
        suite.addTest(ReasonerRuleTests.suite());
        suite.addTestSuite(OntologyRegistrationTest.class);
        suite.addTest(ReasonerCoreTests.suite());
        suite.addTest(ReasonerFlightTests.suite());
        suite.addTest(ReasonerDLTests.suite());      
        suite.addTest(ReasonerFOLTests.suite());      
        // $JUnit-END$
        return suite;
    }

}