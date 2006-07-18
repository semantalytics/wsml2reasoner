/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Austria.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package org.wsml.reasoner.impl;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.deri.wsmo4j.validator.WsmlValidatorImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.*;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.io.abstract_syntax.Renderer;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.util.*;
import org.wsml.reasoner.WSMLDL2OWLTransformer;
import org.wsml.reasoner.api.*;
import org.wsml.reasoner.api.inconsistency.*;
import org.wsml.reasoner.builtin.pellet.PelletFacade;
import org.wsml.reasoner.transformation.*;
import org.wsml.reasoner.transformation.dl.*;
import org.wsmo.common.IRI;
import org.wsmo.factory.*;
import org.wsmo.validator.WsmlValidator;

import wsml2reasoner.normalization.WSMLNormalizationTest;

/**
 * A prototypical implementation of a WSML-DL reasoner.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/impl/DLBasedWSMLReasoner.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2006-07-18 08:21:01 $
 */
public class DLBasedWSMLReasoner implements WSMLDLReasoner{

	protected org.wsml.reasoner.DLReasonerFacade builtInFacade = null;
	
	protected WsmoFactory wsmoFactory = null;

	protected LogicalExpressionFactory leFactory = null;

	protected WSMO4JManager wsmoManager = null;
	
	protected OWLConnection owlConnection = null;
	
	protected OWLDataFactory owlDataFactory = null;
	
	protected OWLOntology owlOntology = null;
	
	protected ChangeVisitor changeVisitor = null;
	
	protected WSMLDL2OWLTransformer transformer = null;
	
	 
	public DLBasedWSMLReasoner(WSMLReasonerFactory.BuiltInReasoner builtInType,
			WSMO4JManager wsmoManager) {
		this.wsmoManager = wsmoManager;
		wsmoFactory = this.wsmoManager.getWSMOFactory();
		leFactory = this.wsmoManager.getLogicalExpressionFactory();
		switch (builtInType) {
//			case KAON2:
//				builtInFacade = new Kaon2Facade(wsmoManager);
//				break;
			case PELLET:
				builtInFacade = new PelletFacade(wsmoManager);
				break;
			default:
				throw new UnsupportedOperationException("Reasoning with "
						+ builtInType.toString() + " is not supported yet!");
			}
	    } 

	/**
	 * Method to convert a WSML ontology to an OWL ontology. The given 
	 * WSML-DL ontology is first checked for validity, then normalized and 
	 * finally translated to OWL DL.
	 * 
	 * @param o Ontology to be converted
	 * @return OWLOntology
	 * @throws RuntimeException in the case the given WSML-DL ontology is not
	 * 			valid
	 */
	@SuppressWarnings("unchecked")
    public OWLOntology convertOntology(Ontology ontology) 
			throws OWLException, URISyntaxException {
		// check if given WSML-DL ontology is valid
		WsmlValidator validator = new WsmlValidatorImpl();
		boolean valid = validator.isValid(
				ontology, "http://www.wsmo.org/wsml/wsml-syntax/wsml-dl", 
				new Vector(), new Vector());
		if (!valid) {
			throw new RuntimeException ("The given WSML-DL ontology is not valid!");
		}
//System.out.println(WSMLNormalizationTest.serializeOntology(ontology));
		// normalize ontology
        ontology = normalizeOntology(ontology);
//System.out.println(WSMLNormalizationTest.serializeOntology(ontology));
        // transform ontology
        OWLOntology owlOntology = transformOntology(ontology);
        return owlOntology;
    }
	
	/*
	 * Method to normalize a WSML ontology
	 */
	private Ontology normalizeOntology(Ontology ontology) {
		Ontology normalizedOntology;
		
        // TODO Check whether ontology import is currently handled
			
		// Replace relations, subRelations and relationinstances
		OntologyNormalizer normalizer = new Relation2AttributeNormalizer(wsmoManager);
		normalizedOntology = normalizer.normalize(ontology);
//      System.out.println("\n-------\n Ontology after simplification:\n" +
//      WSMLNormalizationTest.serializeOntology(normalizedOntology));

		
//		 Convert conceptual syntax to logical expressions
		normalizer = new AxiomatizationNormalizer(wsmoManager);
        normalizedOntology = normalizer.normalize(normalizedOntology);
//      System.out.println("\n-------\n Ontology after simplification:\n" +
//      WSMLNormalizationTest.serializeOntology(normalizedOntology));
        
        // Replace unnumbered anonymous identifiers and convert logical expressions
        normalizer = new WSMLDLLogExprNormalizer(wsmoManager);
		normalizedOntology = normalizer.normalize(normalizedOntology);
//      System.out.println("\n-------\n Ontology after simplification:\n" +
//      WSMLNormalizationTest.serializeOntology(normalizedOntology));
        
        return normalizedOntology;
	}
	
	/*
	 * Method to transform a WSML ontology into an OWL ontology.
	 */
	@SuppressWarnings("unchecked")
	private OWLOntology transformOntology(Ontology ontology) 
			throws OWLException, URISyntaxException {
		// Set up the implementation class
		Map parameters = new HashMap();
		parameters.put(OWLManager.OWL_CONNECTION, 
		"org.semanticweb.owl.impl.model.OWLConnectionImpl");
		owlConnection = OWLManager.getOWLConnection(parameters);
		
		// Get the OWL Data Factory
		owlDataFactory = owlConnection.getDataFactory();
		
		// Get an OWL ontology
		String ontologyId = ontology.getIdentifier().toString();
		ontologyId = ontologyId.substring(0, ontologyId.indexOf("-as-axioms"));
		URI uri = new URI(ontologyId);
		owlOntology = owlConnection.createOntology(uri, uri);
		
		// Get a change visitor which will enact change events over the ontology
		changeVisitor = owlConnection.getChangeVisitor(owlOntology);

		// Set up the transformer
		transformer = new WSMLDL2OWLTransformer(owlOntology, owlDataFactory, changeVisitor);
		owlOntology = transformer.transform(ontology);
		
		return owlOntology;
	}
	
	public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException {
		// TODO Auto-generated method stub
		
	}

	public void registerOntology(Ontology ontology) throws InconsistencyException {
		// TODO Auto-generated method stub
		
	}

	public void registerOntologiesNoVerification(Set<Ontology> ontologies) {
		// TODO Auto-generated method stub
		
	}

	public void registerOntologyNoVerification(Ontology ontology) {
		// TODO Auto-generated method stub
		
	}

	public void deRegisterOntology(IRI ontologyID) {
		// TODO Auto-generated method stub
		
	}

	public void deRegisterOntology(Set<IRI> ontologyIDs) {
		// TODO Auto-generated method stub
		
	}

	public Set<Map<Variable, Term>> executeQuery(IRI ontologyID, LogicalExpression query) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean entails(IRI ontologyID, LogicalExpression expression) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubConceptOf(IRI ontologyID, Concept subConcept, Concept superConcept) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Instance> getInstances(IRI ontologyID, Concept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Concept> getConcepts(IRI ontologyID, Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSatisfiable(IRI ontologyID) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set<ConsistencyViolation> checkConsistency(IRI ontologyID) {
		// TODO Auto-generated method stub
		return null;
	}

	public String serialize2OWLAbstractSyntax(OWLOntology owlOntology) 
			throws RendererException {
		Renderer renderer = new Renderer();
    	StringWriter writer = new StringWriter();
    	renderer.renderOntology(owlOntology, writer);
        return writer.toString();
	}
	
	public String serialize2OWLRDFSyntax(OWLOntology owlOntology) 
			throws RendererException {
		org.semanticweb.owl.io.owl_rdf.Renderer renderer = 
			new org.semanticweb.owl.io.owl_rdf.Renderer();
    	StringWriter writer = new StringWriter();
    	renderer.renderOntology(owlOntology, writer);
        return writer.toString();
	}
}
/*
 * $Log: not supported by cvs2svn $
 *
 */