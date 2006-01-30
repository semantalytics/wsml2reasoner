package reasoner;

import reasoner.core.*;
import reasoner.flight.*;
import junit.framework.Test;
import junit.framework.TestSuite;

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
        suite.addTest(SimpleGraph.suite());
        suite.addTest(DogsworldTest.suite());
        suite.addTest(MaciejVTABug.suite());
        suite.addTest(MaciejVTABug2.suite());
        suite.addTest(LordOfRings.suite());
        suite.addTestSuite(OntologyRegistrationTest.class);
        // $JUnit-END$
        return suite;
    }

}