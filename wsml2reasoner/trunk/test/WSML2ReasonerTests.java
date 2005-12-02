

import junit.framework.*;
import logexpapi.*;
import normalization.*;

import org.wsml.reasoner.transformation.*;

import reasoner.*;
import test.wsmx.*;
import wrapper.*;
import wsmo4j.*;
import cs2logexp.*;
import datalog.*;


public class WSML2ReasonerTests extends TestSuite {

    public WSML2ReasonerTests() {
        super();
        //common
//        addTestSuite(WSML2ReasonerTests.class);
        
//        addTestSuite(UnitTestWSMLConceptualSyntax2LExprs.class);
//        addTestSuite(ConstantTest.class);
//        addTestSuite(DataTypeValueTest.class);
//        addTestSuite(LiteralTest.class);
//        addTestSuite(PredicateTest.class);
//        addTestSuite(RuleTest.class);
//        addTestSuite(VariableTest.class);
//        addTestSuite(LogExpTest.class);
//        addTestSuite(AnonymousIDReplacementTest.class);
//        addTestSuite(ConstructReductionNormalizerTest.class);
//        addTestSuite(LloydToporNormalizerTest.class);
//        addTestSuite(WSMLNormalizationTest.class);
//        addTestSuite(AnonymousIdUtilsTest.class);
//        addTestSuite(WSML2DatalogTransformer.class);
//        
//        //add
//        
//        addTestSuite(AnonymousIRIsTest.class);
//        addTestSuite(DogsworldTest.class);
//        addTestSuite(MaciejBugTest.class);
//        addTestSuite(MaciejBugTest2.class);
//        addTestSuite(OntologyRegistrationTest.class);
//         
//        addTestSuite(ReasonerTests.class);
//        addTestSuite(TestWSMLCoreReasoner.class);
//        addTestSuite(WSMXReasonerTest.class);
////        addTestSuite(BaseReasonerTest.class);
////        addTestSuite(BaseTest.class);
//        addTestSuite(Kaon2FacadeTest.class);
//        addTestSuite(DatatypesTest.class);
//        addTestSuite(LocationBugTest.class);
//        addTestSuite(Wsmo4jFactoryCreationTest.class);
//
//        addTestSuite(TestKaon2WSMLFlightReasoner.class);
    }

    public static Test suite() {
        return new WSML2ReasonerTests();
    }
}

/*
 * $Log$
 * Revision 1.16  2005/11/29 15:43:32  holgerlausen
 * added missing tests
 *
 * Revision 1.15  2005/11/21 13:16:01  holgerlausen
 * added DL Validator tests.
 *
 * Revision 1.14  2005/09/20 13:21:31  holgerlausen
 * refactored logical expression API to have simple molecules and compound molecules (RFE 1290043)
 *
 * Revision 1.13  2005/09/14 11:24:19  marin_dimitrov
 * no message
 *
 * Revision 1.12  2005/09/14 11:13:46  marin_dimitrov
 * removed XML test case for LE
 *
 * Revision 1.10  2005/09/12 14:34:18  vassil_momtchev
 * tests updated
 *
 * Revision 1.9  2005/09/09 17:11:45  nathaliest
 * changed validator test structure and changed wsml test files
 *
 * Revision 1.8  2005/09/09 06:35:47  holgerlausen
 * all open unit tests are moved to clarification:* wokring unit tests under test.*, Wsmo4jTestSuite contains now all tests that are expect to work
 *
 * Revision 1.7  2005/09/07 18:45:00  holgerlausen
 * updated unit tests
 *
 * Revision 1.6  2005/09/02 14:10:04  holgerlausen
 * unit tests are all greenisch now. Moved the ones that are pending to different package. Will check in more detail next week
 *
 * Revision 1.5  2005/07/29 10:44:36  holgerlausen
 * wsmo4j test cases run again, however due to open issues some of them fail
 *
 * Revision 1.4  2005/01/12 16:19:15  alex_simov
 * checkstyle formatting
 *
 * Revision 1.3  2005/01/12 14:13:37  alex_simov
 * static TestSuite constructing method added
 *
 * Revision 1.2  2005/01/11 14:51:55  alex_simov
 * The class now extends TestSuite
 *
 * Revision 1.1  2005/01/11 14:11:54  alex_simov
 * A TestSuit for the current collection of tests
 *
 */