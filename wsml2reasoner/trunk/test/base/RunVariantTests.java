package base;

import variant.core.BundledWSMLCoreTests;
import variant.dl.BundledWSMLDLTests;
import variant.flight.BundledWSMLFlightTests;
import variant.rule.BundledWSMLRuleTests;
import junit.framework.Test;
import junit.framework.TestSuite;

public class RunVariantTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for WSML variants");
        // $JUnit-BEGIN$
        suite.addTest(BundledWSMLCoreTests.suite());
        suite.addTest(BundledWSMLDLTests.suite());
        suite.addTest(BundledWSMLFlightTests.suite());
        suite.addTest(BundledWSMLRuleTests.suite());
        // $JUnit-END$
        return suite;
    }

}