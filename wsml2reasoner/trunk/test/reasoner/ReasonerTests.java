package reasoner;

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
        suite.addTest(TestWSMLCoreReasoner.suite());
        suite.addTest(TestKaon2WSMLFlightReasoner.suite());
        suite.addTest(MaciejBugTest.suite());
        // $JUnit-END$
        return suite;
    }

}