/**
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.wsml.reasoner.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.FOLReasoner.EntailmentType;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class FOLBasedWSMLReasonerTest extends TestCase {
	
	protected FOLBasedWSMLReasoner reasoner;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Ontology ontology;

	public FOLBasedWSMLReasonerTest() {
		super();
	}

	
	protected void setUp() throws Exception {
		super.setUp();
		Factory factory = new FactoryImpl(); 
		wsmoFactory = factory.getWsmoFactory();
        leFactory = factory.getLogicalExpressionFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));	
        
        Concept humanConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns+ "urn://Human"));
        
        ontology.addConcept(humanConcept);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.TPTP);
        reasoner = (FOLBasedWSMLReasoner) DefaultWSMLReasonerFactory.getFactory().createFOLReasoner(params);	
        reasoner.registerOntology(ontology);
        
	}
	
	public void testRegisterOntologyNoVerification() {
		reasoner.deRegister();
		reasoner.registerOntologyNoVerification(ontology);
	}
	
	public void testCheckEntailment() throws ParserException, InconsistencyException{
		
		EntailmentType t1 = reasoner.checkEntailment(LETestHelper.buildLE("?x subConceptOf _\"urn:a\""));
		System.out.println(t1.toString());
	}
	
	public void testCheckConsistency() throws InconsistencyException {
		reasoner.registerOntology(ontology);
		Set <ConsistencyViolation> set = reasoner.checkConsistency();
		System.out.println(set.size());
		System.out.println("");
	}
	
	
	
	

}
