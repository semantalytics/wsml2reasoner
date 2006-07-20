package wsml2reasoner.normalization;

import junit.framework.Test;
import junit.framework.TestSuite;

public class NormalizationTests {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	        junit.textui.TestRunner.run(suite());
	}

 	public static Test suite(){
        TestSuite suite = new TestSuite("Test suite for the normalization");
        //$JUnit-BEGIN$
        suite.addTestSuite(AnonymousIDReplacementTest.class);
        suite.addTestSuite(ConstructReductionNormalizerTest.class);
        suite.addTestSuite(LloydToporNormalizerTest.class);
        suite.addTestSuite(WSMLDLNormalizerTest.class);
        //$JUnit-END$
        return suite;
    }
 	
}
