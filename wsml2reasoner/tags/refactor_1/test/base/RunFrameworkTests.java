package base;
import junit.framework.Test;
import junit.framework.TestSuite;
import framework.datatypes.BundledDatatypeTests;
import framework.normalization.BundledNormalizationTests;
import framework.transformation.BundledTransformationTests;

public class RunFrameworkTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for generic framework tests");
        // $JUnit-BEGIN$
        suite.addTest(BundledNormalizationTests.suite());
        suite.addTest(BundledTransformationTests.suite());
        suite.addTest(BundledDatatypeTests.suite());
        // $JUnit-END$
        return suite;
    }

}