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
package org.wsml.reasoner;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.WsmlDataType;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DatalogBasedWSMLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.common.IRI;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.ontology.InstanceImpl;

import junit.framework.TestCase;

public class DLUtilitiesTest extends TestCase {

	protected DLUtilities dlUtils;
	protected DatalogBasedWSMLReasoner reasoner;
	protected String ns = "http://ex.org#";
	protected Ontology ontology;
	protected WSMO4JManager wsmoManager;
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected DataFactory dataFactory;

	public DLUtilitiesTest() {
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		wsmoManager = new WSMO4JManager();
		wsmoFactory = wsmoManager.getWSMOFactory();
		dataFactory = wsmoManager.getDataFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
        
        Concept conceptWithoutAnyAttr =  wsmoFactory.createConcept( wsmoFactory.createIRI( ns + "conceptWithoutAttribute" ) );
        conceptWithoutAnyAttr.setOntology(ontology);
        ontology.addConcept(conceptWithoutAnyAttr);
        
        Concept badConcept = wsmoFactory.createConcept( wsmoFactory.createIRI( ns + "concept01" ) );
        badConcept.setOntology(ontology);
        ontology.addConcept(badConcept);
        
        Attribute badAttribute = badConcept.createAttribute(wsmoFactory.createIRI(ns + "a"));
		badAttribute.addType(dataFactory.createWsmlDataType(WsmlDataType.WSML_STRING));
		
		Instance i = new InstanceImpl(wsmoFactory.createIRI(ns + "aa"));
		i.addConcept(badConcept);
		i.addAttributeValue( wsmoFactory.createIRI( ns + "a" ), dataFactory.createWsmlInteger( new BigInteger("3")) );
		
		ontology.addInstance(i);
        
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
				WSMLReasonerFactory.BuiltInReasoner.IRIS_WELL_FOUNDED);
		reasoner = (DatalogBasedWSMLReasoner) DefaultWSMLReasonerFactory
				.getFactory().createFlightReasoner(params);
		
		reasoner.registerOntology(ontology);
		dlUtils = new DLUtilities(reasoner, wsmoManager);
	}
	
	public void testGetAllConcepts(){

		Set<Concept> s = dlUtils.getAllConcepts();
		//[ 2003873 ] Concept with no relatives not returned from getAllConcepts()
		// 
		assertEquals(2,s.size());
		for(Concept c :s ){
			System.out.println(c.toString());
		}
	}
	
	public void testGetAllInstances(){
		Set<Instance> s = dlUtils.getAllInstances();
		assertEquals(1, s.size());
	}
	
	public void testgetAllAttributes() {
		Set<IRI> s = dlUtils.getAllAttributes();
		assertEquals(1,s.size());
	}
	
	public void testgetAttributes() {
		Set<IRI> s1 = dlUtils.getAllConstraintAttributes();
		Set<IRI> s2 = dlUtils.getAllInferenceAttributes();
		assertEquals(0,s1.size());
		assertEquals(1,s2.size());
	}
	
	public void testGetConcepts() {
		Instance i = new InstanceImpl(wsmoFactory.createIRI(ns + "aa"));
		Set<Concept> s = dlUtils.getConcepts(i);
		assertEquals(1,s.size());
		for(Concept c : s) {
			assertEquals(  wsmoFactory.createIRI( ns + "concept01" ), c.getIdentifier());
		}
	}
	
	public void testGetConceptsOf() {
		Set<Concept> s = dlUtils.getConceptsOf(wsmoFactory.createIRI( ns + "a" ));
		assertEquals(1,s.size());
		for(Concept c : s) {
			assertEquals(  wsmoFactory.createIRI( ns + "concept01" ), c.getIdentifier());
		}

	}
	
	public void testIsSatisfiable() throws SynchronisationException, InvalidModelException, InconsistencyException, ParserException {
		
		assertTrue(dlUtils.isSatisfiable());
		
		reasoner.deRegister();
		
		Ontology ontologyUnSatisfiable = wsmoFactory.createOntology(wsmoFactory.createIRI(ns
				+ "AboutHumans"));
		ontologyUnSatisfiable.setDefaultNamespace(wsmoFactory.createIRI(ns));

		Concept humanConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns
				+ "urn://Human"));
		Attribute hasRelativeAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns
				+ "urn://hasRelative"));
		hasRelativeAttr.setSymmetric(true);
		hasRelativeAttr.addType(humanConcept);
		
		Concept locationConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns
				+ "urn://Location"));

		Attribute hasParentAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns
				+ "urn://hasParent"));
		hasParentAttr.addType(humanConcept);
		

	    Attribute livesAtAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns
				+ "urn://livesAt"));
		livesAtAttr.addType(locationConcept);

		Instance person1 = wsmoFactory.createInstance(wsmoFactory
				.createIRI("urn://Person1"), humanConcept);
		Instance person2 = wsmoFactory.createInstance(wsmoFactory
				.createIRI("urn://Person2"), humanConcept);
		
		Instance location1 = wsmoFactory.createInstance(wsmoFactory
				.createIRI("urn://Location01"), locationConcept);

		person1.addAttributeValue(hasParentAttr.getIdentifier(), location1);
		person1.addAttributeValue(hasParentAttr.getIdentifier(), wsmoFactory
				.createInstance(wsmoFactory.createAnonymousID()));

		Axiom person1LivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "urn://person1LivesSomewhere"));

		ontologyUnSatisfiable.addConcept(humanConcept);
		ontologyUnSatisfiable.addConcept(locationConcept);
		ontologyUnSatisfiable.addInstance(location1);
		ontologyUnSatisfiable.addInstance(person1);
		ontologyUnSatisfiable.addInstance(person2);
		ontologyUnSatisfiable.addAxiom(person1LivesAx);

		reasoner.registerOntology(ontologyUnSatisfiable);
		dlUtils = new DLUtilities(reasoner, wsmoManager);	
		
//		Set<Concept> s = dlUtils.getAllConcepts();
//		System.out.println(s.size());
//		for(Concept c : s ){
//			System.out.println(c.toString());
//		}
		assertTrue(dlUtils.isSatisfiable());
		reasoner.deRegister();
		reasoner.registerOntology(ontology);
	}

}
