package base;

import engine.iris.BundledIrisTests;
import engine.mins.BundledMinsTests;
import junit.framework.Test;
import junit.framework.TestSuite;

public class RunEngineTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for reasoning engines");
        // $JUnit-BEGIN$
        suite.addTest(BundledIrisTests.suite());
        suite.addTest(BundledMinsTests.suite());        
        // $JUnit-END$
        return suite;
    }

}
