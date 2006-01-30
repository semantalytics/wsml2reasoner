package wsml2reasoner;

import junit.framework.*;
import wsml2reasoner.normalization.*;
import wsml2reasoner.transformation.*;

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
        // $JUnit-END$
        return suite;
    }

}