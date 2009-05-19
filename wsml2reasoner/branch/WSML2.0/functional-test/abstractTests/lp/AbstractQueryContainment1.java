package abstractTests.lp;

import helper.OntologyHelper;
import junit.framework.TestCase;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.LPReasoner;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import abstractTests.LP;

/**
 * Test to check the IRIS query containment task
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 *
 */
public abstract class AbstractQueryContainment1 extends TestCase implements LP {
	
	public void testSimpleQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment1#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x).\n";
        
        checkQueryContainment( ontology, "car(?x)", "vehicle(?x)", true);
	}
	
	public void testConjunctiveQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment2#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x).\n " +
                "car(?x) :- mobile(?x) and ?x[hasWheel hasValue ?y] " +
                "and ?x[hasTires hasValue 4].\n";
        
        checkQueryContainment( ontology,
        				"mobile(?x) and ?x[hasWheel hasValue ?y] and ?x[hasTires hasValue 4]",
        				"vehicle(?x)", true);
	}
	
	public void testConjunctiveQueryContainment2() throws Exception {
        String ontology = "namespace _\"http://queryContainment3#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x).\n " +
                "car(?x) :- mobile(?x) and ?x[hasWheel hasValue ?y] " +
                "and ?x[hasTires hasValue 4].\n";

        checkQueryContainment( ontology,
        				"mobile(?x) and ?x[hasWheel hasValue ?y] and ?x[hasTires hasValue 4]",
        				"vehicle(?x) and ?x[hasTires hasValue 5]", false);
	}
	
	public void testExtendedPluginQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment4#\" \n" +
                "ontology o \n" +
                "concept GM_Object " +
        		"hasSRS impliesType SRS " +
        		"concept Polygon subConceptOf GM_Object " +
        		"concept SRS " +
        		"concept projSRS subConceptOf SRS " +
        		"instance gk memberOf projSRS " +
        		"axiom gm_objectDefinition definedBy " +
        		"?x[hasSRS hasValue ?srs] implies " +
        		"?x memberOf GM_Object. \n" +
                "relation overlay/3 " +
        		"relation intersection/3 " +
        		"relation union/3 " +
        		"relation dihfference/3 " +
        		"relation symmetricDifference/3 " +
        		"axiom overlayDefinition definedBy " +
        		"intersection(?x,?y,?z) implies overlay(?x,?y,?z). " +
        		"union(?x,?y,?z) implies overlay(?x,?y,?z). " +
        		"difference(?x,?y,?z) implies overlay(?x,?y,?z). " +
        		"symmetricDifference(?x,?y,?z) implies overlay(?x,?y,?z). " +
        		"axiom symmetricDifferenceDefinition definedBy " +
        		"symmetricDifference(?x,?y,?z) implies symmetricDifference(?y,?x,?z). " +
        		"symmetricDifference(?x,?y,?z) impliedBy difference(?x,?y, ?xydif) and " +
        		"difference(?y,?x,?yxdif) and union(?xydif, ?yxdif,?z). \n";
        
        checkQueryContainment( ontology,
        				"?x[hasSRS hasValue gk] memberOf Polygon and ?y[hasSRS hasValue gk] memberOf Polygon.\n",
        				"?a[hasSRS hasValue ?refsys] memberOf GM_Object and ?b[hasSRS hasValue ?refsys] memberOf GM_Object and ?refsys memberOf projSRS.",
        				true);
	}
	
	public void testDisjunctiveQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment5#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x) and mobile(?x).\n";
        
        try {
            checkQueryContainment( ontology, "car(?x) or mobile(?x)", "vehicle(?x)", false);
        	fail();
		} catch (Exception e) {
		};
	}
	
	public void testNegativeQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment6#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x) and mobile(?x).\n";

        try {
            checkQueryContainment( ontology, "car(?x) or not(mobile(?x))", "vehicle(?x)", false);
        	fail();
		} catch (Exception e) {
		};
	}
	
	public void testBuiltInQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment7#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x) and ?x[hasTires hasValue ?y] and ?y=4.\n";

        try {
            checkQueryContainment( ontology, "car(?x) and ?x[hasTires hasValue ?y] and ?y=5", "vehicle(?x)", false);
        	fail();
		} catch (Exception e) {
		};
	}
	
	private void checkQueryContainment( String ontologyString, String queryString1, String queryString2, boolean expected ) throws Exception
	{
        Ontology ontology = OntologyHelper.parseOntology( ontologyString );

        LPReasoner reasoner = getLPReasoner();
        reasoner.registerOntology(ontology);
        
        LogicalExpressionFactory leFactory = Factory.createLogicalExpressionFactory(null);

		// build queries
        LogicalExpression query1 = leFactory.createLogicalExpression(queryString1, ontology);
        LogicalExpression query2 = leFactory.createLogicalExpression(queryString2, ontology);
        
        // perform query containment check
        boolean check = reasoner.checkQueryContainment(query1, query2);
        assertEquals(expected, check);
        
        reasoner.deRegister();
	}
}
