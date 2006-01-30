package wsml2reasoner;

import reasoner.core.*;
import reasoner.flight.*;
import wsml2reasoner.normalization.*;
import wsml2reasoner.transformation.*;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author nagypal
 */
public class FrameworkTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for the WSML reasoner");
        // $JUnit-BEGIN$
        suite.addTestSuite(AnonymousIDReplacementTest.class);
        suite.addTestSuite(ConstructReductionNormalizerTest.class);
        suite.addTestSuite(LloydToporNormalizerTest.class);
        suite.addTestSuite(WSMLNormalizationTest.class);
        suite.addTestSuite(AnonymousIdUtilsTest.class);
        suite.addTestSuite(WSML2DatalogTransformerTest.class);
        // $JUnit-END$
        return suite;
    }

}