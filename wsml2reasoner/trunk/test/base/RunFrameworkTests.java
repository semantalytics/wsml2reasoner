package base;
import framework.datatypes.BundledDatatypeTests;
import framework.normalization.BundledNormalizationTests;
import framework.registration.BundledRegistrationTests;
import framework.transformation.BundledTransformationTests;
import junit.framework.Test;
import junit.framework.TestSuite;

public class RunFrameworkTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for framework (generic tests)");
        // $JUnit-BEGIN$
        suite.addTest(BundledNormalizationTests.suite());
        suite.addTest(BundledTransformationTests.suite());
        suite.addTest(BundledRegistrationTests.suite());
        suite.addTest(BundledDatatypeTests.suite());
        // $JUnit-END$
        return suite;
    }

}