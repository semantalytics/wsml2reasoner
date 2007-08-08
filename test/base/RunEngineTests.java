package base;

import junit.framework.Test;
import junit.framework.TestSuite;
import engine.spassplust.BundledSpassPlusTTests;
import engine.tptp.BundledTPTPTests;

public class RunEngineTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for specific reasoning engines");
        // $JUnit-BEGIN$
        suite.addTest(BundledTPTPTests.suite());
        suite.addTest(BundledSpassPlusTTests.suite());
        // $JUnit-END$
        return suite;
    }

}
