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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DLBasedWSMLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.validator.ValidationError;
import org.wsmo.validator.ValidationWarning;
import org.wsmo.validator.WsmlValidator;
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
        
        // build up standard ontology
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "AboutHumans"));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
        
        Concept humanConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "urn://Human" ));
        Attribute hasRelativeAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns + "urn://hasRelative"));
//        hasRelativeAttr.setSymmetric(true);
        hasRelativeAttr.addType(humanConcept);
        Concept locationConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "urn://Location"));
        
        Attribute hasParentAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns + "urn://hasParent"));
        hasParentAttr.addType(humanConcept);
        
        Attribute livesAtAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns + "urn://livesAt"));
        livesAtAttr.addType(locationConcept);
        
        Instance person1 = wsmoFactory.createInstance(wsmoFactory.createIRI("urn://Person1"), humanConcept);
        Instance person2 = wsmoFactory.createInstance(wsmoFactory.createIRI("urn://Person2"), humanConcept);
        Instance person3 = wsmoFactory.createInstance(wsmoFactory.createIRI("urn://Person3"), humanConcept);
        person1.addAttributeValue(hasParentAttr.getIdentifier(), person2);
        person1.addAttributeValue(hasParentAttr.getIdentifier(), wsmoFactory.createInstance(wsmoFactory.createAnonymousID()));
        
        Axiom person1LivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "urn://person1LivesSomewhere"));
        person1LivesAx.addDefinition(leFactory.createConjunction(leFactory.createAttributeValue(person1.getIdentifier(), livesAtAttr.getIdentifier(), leFactory.createAnonymousID((byte)1)) , leFactory.createMemberShipMolecule(leFactory.createAnonymousID((byte)1),locationConcept.getIdentifier())));
        Axiom person2LivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "urn://person2LivesSomewhere"));
        person2LivesAx.addDefinition(leFactory.createConjunction(leFactory.createConjunction(leFactory.createConjunction(leFactory.createConjunction(leFactory.createAttributeValue(person2.getIdentifier(), livesAtAttr.getIdentifier(), leFactory.createAnonymousID((byte)1)), leFactory.createMemberShipMolecule(leFactory.createAnonymousID((byte)1), locationConcept.getIdentifier())), leFactory.createAttributeValue(person3.getIdentifier(),livesAtAttr.getIdentifier(), leFactory.createAnonymousID((byte)2))), leFactory.createMemberShipMolecule(leFactory.createAnonymousID((byte)2), locationConcept.getIdentifier())), leFactory.createAttributeValue(person3.getIdentifier(), livesAtAttr.getIdentifier(), wsmoFactory.createAnonymousID())));
        
        ontology.addConcept(humanConcept);
        ontology.addConcept(locationConcept);
        ontology.addInstance(person1);
        ontology.addInstance(person2);
        ontology.addAxiom(person1LivesAx);
        ontology.addAxiom(person2LivesAx);
        

        ontology.addConcept(humanConcept);
        System.out.println(ontology.listConcepts());
       
        
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.PELLET);
        
        reasoner =  (DLBasedWSMLReasoner) DefaultWSMLReasonerFactory.getFactory().createDLReasoner(params);
		
	}
	public void test01() {
		assertTrue(true);
	}
	
	public void test() throws ParserException, SynchronisationException, InvalidModelException, InconsistencyException {
		
		
		WsmlValidator validator = Factory.createWsmlValidator(null);
		ArrayList<ValidationError> arError = new ArrayList<ValidationError>();
		ArrayList<ValidationWarning> arWarn = new ArrayList<ValidationWarning>();
		validator.isValid(ontology, "http://www.wsmo.org/wsml/wsml-syntax/wsml-dl",arError ,arWarn );
		if(arError.size() != 0) {
			for(ValidationError er : arError) {
				System.out.println(er.toString());
			}
		}
		
		reasoner.registerOntology(ontology);
		
		Set <IRI> set = reasoner.getAllAttributes();
		for(IRI iri : set){
			System.out.println(iri.toString());
		}
		
		
	}
	
	
	
	

}
