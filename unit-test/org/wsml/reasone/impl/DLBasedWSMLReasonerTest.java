/**
 * 
 */
package org.wsml.reasone.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DLBasedWSMLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.common.IRI;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import junit.framework.TestCase;


public class DLBasedWSMLReasonerTest extends TestCase {
	
	protected DLBasedWSMLReasoner reasoner;
	protected WSMO4JManager wsmoManager;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Ontology ontology;
	
	
	public DLBasedWSMLReasonerTest() {
		super();
		
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager(); 
		wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.PELLET);
        
        reasoner =  (DLBasedWSMLReasoner) DefaultWSMLReasonerFactory.getFactory().createDLReasoner(params);
		
	}
	
	public void test() throws ParserException, SynchronisationException, InvalidModelException, InconsistencyException {
		
	    Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom01"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\""));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\""));
		
		ontology.addAxiom(axiom1);
		
		reasoner.registerOntology(ontology);
		
		Set <IRI> set = reasoner.getAllAttributes();
		for(IRI iri : set){
			System.out.println(iri.toString());
		}
		
		
	}
	
	
	
	

}
