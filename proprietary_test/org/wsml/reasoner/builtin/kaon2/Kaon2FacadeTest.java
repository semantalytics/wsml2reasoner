package org.wsml.reasoner.builtin.kaon2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import junit.framework.TestCase;

public class Kaon2FacadeTest extends TestCase {

	    private WsmoFactory wsmoFactory;

	    private LogicalExpressionFactory leFactory;

	    private WSMLReasoner wsmlReasoner;

	    private Parser parser; 

	    BuiltInReasoner previous;
	    
	    @Override
	    protected void setUp()throws Exception {
	    	super.setUp();
	    	WSMO4JManager wsmoManager = new WSMO4JManager();
	    	leFactory = wsmoManager.getLogicalExpressionFactory();
	    	wsmoFactory = wsmoManager.getWSMOFactory();
	 		parser = Factory.createParser(null);
	    }
	    
	    @Override
	    protected void tearDown() throws Exception {
	    	super.tearDown();
	    	System.gc();
	    }

	    public void ontologyRegistration() throws Exception {
	    	 // get A reasoner
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
	                WSMLReasonerFactory.BuiltInReasoner.KAON2);
	        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
	                .createWSMLFlightReasoner(params);
	        
	        Ontology o1 = wsmoFactory.createOntology(wsmoFactory
	                .createIRI("urn:test1"));
	        Atom a1 = leFactory.createAtom(wsmoFactory.createIRI("urn:test:a1"),
	                new ArrayList());
	        Axiom ax1 = wsmoFactory.createAxiom(wsmoFactory
	                .createIRI("urn:test:ax1"));
	        ax1.addDefinition(a1);
	        o1.addAxiom(ax1);

	        try {
	            executeQuery("_\"urn:test:xxx\"()", o1);
	            fail();
	        } catch (InternalReasonerException expected) {

	        }

	        wsmlReasoner.registerOntology(o1);
	        assertEquals(1, executeQuery("_\"urn:test:a1\"()", o1).size());

	        Ontology o2 = wsmoFactory.createOntology(wsmoFactory
	                .createIRI("urn:test2"));
	        Atom a2 = leFactory.createAtom(wsmoFactory.createIRI("urn:test:a2"),
	                new ArrayList());
	        Axiom ax2 = wsmoFactory.createAxiom(wsmoFactory
	                .createIRI("urn:test:ax2"));
	        ax2.addDefinition(a2);
	        o2.addAxiom(ax2);

	        try {
	            executeQuery("_\"urn:test:a2\"()", o2);
	            fail();
	        } catch (InternalReasonerException expected) {

	        }
	        wsmlReasoner.registerOntology(o2);
	        assertEquals(1, executeQuery("_\"urn:test:a1\"()", o1).size());
	        assertEquals(0, executeQuery("_\"urn:test:a11\"()", o1).size());
	        assertEquals(1, executeQuery("_\"urn:test:a2\"()", o2).size());

	        Atom a11 = leFactory.createAtom(wsmoFactory.createIRI("urn:test:a11"),
	                new ArrayList());
	        Axiom ax11 = wsmoFactory.createAxiom(wsmoFactory
	                .createIRI("urn:test:ax11"));
	        ax11.addDefinition(a11);
	        o1.addAxiom(ax11);

	        wsmlReasoner.registerOntology(o1);
	        assertEquals(1, executeQuery("_\"urn:test:a1\"()", o1).size());
	        assertEquals(1, executeQuery("_\"urn:test:a11\"()", o1).size());
	        assertEquals(1, executeQuery("_\"urn:test:a2\"()", o2).size());

	         wsmlReasoner.deRegisterOntology((IRI) o2.getIdentifier());
	         try {
	             executeQuery("_\"urn:test:a2\"()", o2);
	             fail();
	         } catch (InternalReasonerException expected) {

	         }
	         assertEquals(1, executeQuery("_\"urn:test:a1\"()", o1).size());
	         assertEquals(1, executeQuery("_\"urn:test:a11\"()", o1).size());
	         
	         wsmlReasoner.deRegisterOntology((IRI) o1.getIdentifier());
	         try {
	             executeQuery("_\"urn:test:a1\"()", o2);
	             fail();
	         } catch (InternalReasonerException expected) {

	         }
	 

	    }

	    private Set<Map<Variable, Term>> executeQuery(String query, Ontology o)
	            throws Exception {
	        LogicalExpression qExpression = leFactory.createLogicalExpression(
	                query, o);
	        return wsmlReasoner.executeQuery((IRI) o.getIdentifier(), qExpression);
	    }
	    
	    public void testFlightReasoners() throws Exception{
	    	ontologyRegistration();
	    }

}
