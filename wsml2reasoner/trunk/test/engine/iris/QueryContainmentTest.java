package engine.iris;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

/**
 * Test to check the IRIS query containment task
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 *
 */
public class QueryContainmentTest extends BaseReasonerTest {
	
	private LogicalExpressionFactory leFactory;
	
	private WSMLReasoner wsmlReasoner;

	private Parser parser;
	   
	private Ontology ontology;
	   
	protected void setUp() throws Exception {
		super.setUp();
			
		// get a reasoner
		// currently set to IRIS since the other reasoning engines
		// cannot yet handle such built-ins
		BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.IRIS;
		wsmlReasoner = BaseReasonerTest.getReasoner();
		wsmoFactory = Factory.createWsmoFactory(null);
		leFactory = Factory.createLogicalExpressionFactory(null);
		parser = Factory.createParser(null);
	}
	
	public void testSimpleQueryContainment() throws Exception {
		String ns = "http://queryContainment#";
        String test = "namespace _\"" + ns + "\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x).\n";
        ontology = (Ontology) parser.parse(new StringBuffer(test))[0];
        wsmlReasoner = BaseReasonerTest.getReasoner();
        wsmlReasoner.registerOntology(ontology);
        
        // build queries
        LogicalExpression query1 = leFactory.createLogicalExpression("car(?x)", ontology);
        LogicalExpression query2 = leFactory.createLogicalExpression("vehicle(?x)", ontology);
        
        // perform query containment check
        boolean check = wsmlReasoner.checkQueryContainment(query1, query2);
        assertTrue(check);
        
        wsmlReasoner.deRegister();
        wsmlReasoner.registerOntology(ontology);
        
        // get containment mapping result set
        Set<Map<Variable, Term>> result = wsmlReasoner.getQueryContainment(
        		query1, query2);
//        System.out.println(result);
        wsmlReasoner.deRegister();
	}
	
	public void testConjunctiveQueryContainment() throws Exception {
		String ns = "http://queryContainment#";
        String test = "namespace _\"" + ns + "\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x).\n " +
                "car(?x) :- mobile(?x) and ?x[hasWheel hasValue ?y] " +
                "and ?x[hasTires hasValue 4].\n";
        ontology = (Ontology) parser.parse(new StringBuffer(test))[0];
        wsmlReasoner = BaseReasonerTest.getReasoner();
        wsmlReasoner.registerOntology(ontology);
        
        // build queries
        LogicalExpression query1 = leFactory.createLogicalExpression("mobile(?x) and " +
        		"?x[hasWheel hasValue ?y] and ?x[hasTires hasValue 4]", ontology);
        LogicalExpression query2 = leFactory.createLogicalExpression("vehicle(?x)", ontology);
        
        // perform query containment check
        boolean check = wsmlReasoner.checkQueryContainment(query1, query2);
        assertTrue(check);
        
        wsmlReasoner.deRegister();
        wsmlReasoner.registerOntology(ontology);
        
        // get containment mapping result set
        Set<Map<Variable, Term>> result = wsmlReasoner.getQueryContainment(
        		query1, query2);
//        System.out.println(result);
        
        wsmlReasoner.deRegister();
	}
	
	public void testConjunctiveQueryContainment2() throws Exception {
		String ns = "http://queryContainment#";
        String test = "namespace _\"" + ns + "\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x).\n " +
                "car(?x) :- mobile(?x) and ?x[hasWheel hasValue ?y] " +
                "and ?x[hasTires hasValue 4].\n";
        ontology = (Ontology) parser.parse(new StringBuffer(test))[0];
        wsmlReasoner = BaseReasonerTest.getReasoner();
        wsmlReasoner.registerOntology(ontology);
        
        // build queries
        LogicalExpression query1 = leFactory.createLogicalExpression("mobile(?x) and " +
        		"?x[hasWheel hasValue ?y] and ?x[hasTires hasValue 4]", ontology);
        LogicalExpression query2 = leFactory.createLogicalExpression(
        		"vehicle(?x) and ?x[hasTires hasValue 5]", ontology);
        
        // perform query containment check
        boolean check = wsmlReasoner.checkQueryContainment(query1, query2);
        assertFalse(check);
        
        wsmlReasoner.deRegister();
	}
	
	public void testDisjunctiveQueryContainment() throws Exception {
		String ns = "http://queryContainment#";
        String test = "namespace _\"" + ns + "\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x) and mobile(?x).\n";
        ontology = (Ontology) parser.parse(new StringBuffer(test))[0];
        wsmlReasoner = BaseReasonerTest.getReasoner();
        wsmlReasoner.registerOntology(ontology);
        
        // build queries
        LogicalExpression query1 = leFactory.createLogicalExpression("car(?x) or mobile(?x) ", ontology);
        LogicalExpression query2 = leFactory.createLogicalExpression("vehicle(?x)", ontology);
        
        // perform query containment check
        try {
        	wsmlReasoner.checkQueryContainment(query1, query2);
        	fail("Should fail because disjunctive queries are not allowed for " +
        			"the query containment check");
		} catch (Exception e) {
			e.getMessage();
		};
	}
	
	public void testNegativeQueryContainment() throws Exception {
		String ns = "http://queryContainment#";
        String test = "namespace _\"" + ns + "\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x) and mobile(?x).\n";
        ontology = (Ontology) parser.parse(new StringBuffer(test))[0];
        wsmlReasoner = BaseReasonerTest.getReasoner();
        wsmlReasoner.registerOntology(ontology);
        
        // build queries
        LogicalExpression query1 = leFactory.createLogicalExpression("car(?x) or not(mobile(?x)) ", ontology);
        LogicalExpression query2 = leFactory.createLogicalExpression("vehicle(?x)", ontology);
        
        // perform query containment check
        try {
        	wsmlReasoner.checkQueryContainment(query1, query2);
        	fail("Should fail because disjunctive queries are not allowed for " +
        			"the query containment check");
		} catch (Exception e) {
			e.getMessage();
		};
	}
	
	public void testBuiltInQueryContainment() throws Exception {
		String ns = "http://queryContainment#";
        String test = "namespace _\"" + ns + "\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x) and ?x[hasTires hasValue ?y] and ?y=4.\n";
        ontology = (Ontology) parser.parse(new StringBuffer(test))[0];
        wsmlReasoner = BaseReasonerTest.getReasoner();
        wsmlReasoner.registerOntology(ontology);
        
        // build queries
        LogicalExpression query1 = leFactory.createLogicalExpression("car(?x) and " +
        		"?x[hasTires hasValue ?y] and ?y=5 ", ontology);
        LogicalExpression query2 = leFactory.createLogicalExpression("vehicle(?x)", ontology);
        
        // perform query containment check
        try {
        	wsmlReasoner.checkQueryContainment(query1, query2);
        	fail("Should fail because disjunctive queries are not allowed for " +
        			"the query containment check");
		} catch (Exception e) {
			e.getMessage();
		};
	}
	
//	public void testDisjunctiveQueryContainment() throws Exception {
//		String ns = "http://queryContainment#";
//        String test = "namespace _\"" + ns + "\" \n" +
//                "ontology o \n" +
//                "axiom definedBy \n" +
//                "vehicle(?x) :- car(?x) and mobile(?x).\n";
//        ontology = (Ontology) parser.parse(new StringBuffer(test))[0];
//        wsmlReasoner = BaseReasonerTest.getReasoner();
//        wsmlReasoner.registerOntology(ontology);
//        
//        // build queries
//        LogicalExpression query1 = leFactory.createLogicalExpression("car(?x) or mobile(?x) ", ontology);
//        LogicalExpression query2 = leFactory.createLogicalExpression("vehicle(?x)", ontology);
//        
//        // perform query containment check
//        boolean check = wsmlReasoner.checkQueryContainment(query1, query2, 
//        		(IRI) ontology.getIdentifier());
//        assertFalse(check);
//	}
	
    @Override
    protected void tearDown() throws Exception {
    	// TODO Auto-generated method stub
    	super.tearDown();
    }

	
}
