package base;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RunFacadeTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for facades");
        // $JUnit-BEGIN$
        suite.addTest(org.wsml.reasoner.builtin.iris.IrisFacadeTest.suite());
        suite.addTestSuite(org.wsml.reasoner.builtin.tptp.TPTPTest.class);
        // $JUnit-END$
        return suite;
    }

}
