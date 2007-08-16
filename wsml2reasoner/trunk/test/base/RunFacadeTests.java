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
        suite.addTestSuite(org.wsml.reasoner.builtin.iris.IrisFacadeTest.class);
        suite.addTestSuite(org.wsml.reasoner.builtin.tptp.TPTPTest.class);
        suite.addTestSuite(org.wsml.reasoner.builtin.spass.SpassTest.class);
        // $JUnit-END$
        return suite;
    }

}
