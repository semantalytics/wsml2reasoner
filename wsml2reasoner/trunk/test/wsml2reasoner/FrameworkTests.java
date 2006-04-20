package wsml2reasoner;

import junit.framework.Test;
import junit.framework.TestSuite;
import wsml2reasoner.normalization.AnonymousIDReplacementTest;
import wsml2reasoner.normalization.ConstructReductionNormalizerTest;
import wsml2reasoner.normalization.LloydToporNormalizerTest;
import wsml2reasoner.normalization.WSMLNormalizationTest;
import wsml2reasoner.transformation.AnonymousIdUtilsTest;

/**
 * @author nagypal
 * 
 */
//CAN BE DELETED - NOW HANDLED BY NormalizationTests
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
        //suite.addTestSuite(WSMLNormalizationTest.class);
        suite.addTestSuite(AnonymousIdUtilsTest.class);
        // $JUnit-END$
        return suite;
    }

}