package open;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

import base.BaseReasonerTest;

public class CyclicalImportsTest extends BaseReasonerTest {
	
//    private static final String ONTOLOGY_FILE = "test/files/EG1.wsml";
    private static final String ONTOLOGY_FILE = "test/files/TimeOntology.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(CyclicalImportsTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
        		CyclicalImportsTest.class)) {
            protected void setUp() throws Exception {
//            	parseThis(getReaderForFile("test/files/CompensationOntology.wsml"));
//            	parseThis(getReaderForFile("test/files/CompetenceOntology.wsml"));
//            	parseThis(getReaderForFile("test/files/DrivingLicenseOntology.wsml"));
//            	parseThis(getReaderForFile("test/files/EconomicActivityOntology.wsml"));
//            	parseThis(getReaderForFile("test/files/EducationOntology.wsml"));
//            	parseThis(getReaderForFile("test/files/GeographyOntology.wsml"));
            	parseThis(getReaderForFile("test/files/JobOfferOntology.wsml"));
//            	parseThis(getReaderForFile("test/files/JobSeekerOntology.wsml"));
//            	parseThis(getReaderForFile("test/files/LabourRegulatoryOntology.wsml"));
//            	parseThis(getReaderForFile("test/files/LanguageOntology.wsml"));
//            	parseThis(getReaderForFile("test/files/OccupationOntologyC.wsml"));
//            	parseThis(getReaderForFile("test/files/TimeOntology.wsml"));
                setupScenario(ONTOLOGY_FILE);                
             }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }

    
    public void testCyclicalImports() throws Exception {
        String query = "?x memberOf ?z";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"),  wsmoFactory.createIRI("http://here.comes.the.whistleman/CyclicalImports2#Cy2i1"));
        binding.put(leFactory.createVariable("y"),  wsmoFactory.createIRI("http://here.comes.the.whistleman/CyclicalImports1#Master"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
}
