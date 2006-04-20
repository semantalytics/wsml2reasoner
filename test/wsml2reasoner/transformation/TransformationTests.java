package wsml2reasoner.transformation;


import junit.framework.Test;
import junit.framework.TestSuite;

public class TransformationTests {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	        junit.textui.TestRunner.run(suite());
	}

 	public static Test suite(){
        TestSuite suite = new TestSuite("Test suite for the transformation");
        //$JUnit-BEGIN$
        suite.addTestSuite(AnonymousIdUtilsTest.class);
        //$JUnit-END$
        return suite;
    }
 	
}
