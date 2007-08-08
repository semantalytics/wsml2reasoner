package base;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RunAllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for wsml2reasoner (all tests)");
        // $JUnit-BEGIN$
        suite.addTest(RunFacadeTests.suite());
        suite.addTest(RunFrameworkTests.suite());
        suite.addTest(RunVariantTests.suite());
        // $JUnit-END$
        return suite;
    }

}