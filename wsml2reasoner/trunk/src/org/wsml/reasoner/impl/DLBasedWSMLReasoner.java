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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.deri.wsmo4j.validator.WsmlValidatorImpl;
import org.mindswap.pellet.query.QueryResults;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.*;
import org.semanticweb.owl.impl.model.OWLConcreteDataImpl;
import org.semanticweb.owl.impl.model.OWLConcreteDataTypeImpl;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.io.abstract_syntax.Renderer;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.util.*;
import org.semanticweb.owl.validation.SpeciesValidator;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.WSMLDL2OWLTransformer;
import org.wsml.reasoner.api.*;
import org.wsml.reasoner.api.inconsistency.*;
import org.wsml.reasoner.builtin.pellet.PelletFacade;
import org.wsml.reasoner.transformation.*;
import org.wsml.reasoner.transformation.dl.*;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.factory.*;
import org.wsmo.validator.WsmlValidator;

import uk.ac.man.cs.img.owl.validation.ValidatorLogger;

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
 * @version $Revision: 1.4 $ $Date: 2006-07-21 17:00:08 $
 */
public class DLBasedWSMLReasoner implements WSMLDLReasoner{

	protected org.wsml.reasoner.DLReasonerFacade builtInFacade = null;
	
	protected WsmoFactory wsmoFactory = null;

	protected LogicalExpressionFactory leFactory = null;
	
	protected DataFactory dataFactory = null;

	protected WSMO4JManager wsmoManager = null;
	
	protected OWLConnection owlConnection = null;
	
	protected OWLDataFactory owlDataFactory = null;
	
	protected OWLOntology owlOntology = null;
	
	protected String ns = null;
	
	protected ChangeVisitor changeVisitor = null;
	
	protected WSMLDL2OWLTransformer transformer = null;
	
	 
	public DLBasedWSMLReasoner(WSMLReasonerFactory.BuiltInReasoner builtInType,
			WSMO4JManager wsmoManager) {
		this.wsmoManager = wsmoManager;
		wsmoFactory = this.wsmoManager.getWSMOFactory();
		leFactory = this.wsmoManager.getLogicalExpressionFactory();
		dataFactory = this.wsmoManager.getDataFactory();
		switch (builtInType) {
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
    public OWLOntology convertOntology(Ontology ontology) {
		SpeciesValidator owlValidator = null;
		
		// check if given WSML-DL ontology is valid
		WsmlValidator validator = new WsmlValidatorImpl();		
		boolean valid = validator.isValid(
				ontology, "http://www.wsmo.org/wsml/wsml-syntax/wsml-dl", 
				new Vector(), new Vector());
		if (!valid) {
			throw new RuntimeException ("The given WSML-DL ontology is not valid! Please " +
					"use a WSML Validator to check why this is not valid WSML-DL (e.g. " +
					"http://tools.deri.org/wsml/validator/v1.2/");
		}
		ns = ontology.getDefaultNamespace().getIRI().toString();

		// normalize ontology
        ontology = normalizeOntology(ontology);

        // transform ontology
		try {
			owlOntology = transformOntology(ontology);
			
			// check if resulting OWL DL ontology is valid
			owlValidator = new uk.ac.man.cs.img.owl.validation.SpeciesValidator();	
			SpeciesValidatorReporter reporter = new ValidatorLogger();
			owlValidator.setReporter(reporter);
			valid = owlValidator.isOWLDL(owlOntology);
		} catch (OWLException e) {
			throw new RuntimeException ("Difficulties in building the OWL ontology");
		} catch (URISyntaxException e) {
			throw new RuntimeException ("Difficulties in building the OWL ontology");
		}
		if (!valid) {
			throw new RuntimeException("The transformed OWL DL ontology is not valid! Please use an " +
					"OWL Validator to check why this is not OWL DL (e.g. " +
					"http://phoebus.cs.man.ac.uk:9999/OWL/Validator");
		}
        return owlOntology;
    }
	
	/*
	 * Method to normalize a WSML ontology
	 */
	protected Ontology normalizeOntology(Ontology ontology) {
		Ontology normalizedOntology;
			
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
	protected OWLOntology transformOntology(Ontology ontology) 
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
	
	@SuppressWarnings("unchecked")
	protected OWLDescription transformLogicalExpression(LogicalExpression logExpr) {
		// Set up the implementation class
		Map parameters = new HashMap();
		parameters.put(OWLManager.OWL_CONNECTION, 
				"org.semanticweb.owl.impl.model.OWLConnectionImpl");
		try {
			owlConnection = OWLManager.getOWLConnection(parameters);
		
			// Get the OWL Data Factory
			owlDataFactory = owlConnection.getDataFactory();	
		
			return transformer.transform(logExpr);
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public void registerOntology(Ontology ontology) {
		owlOntology = convertOntology(ontology);
		if (!isSatisfiable((IRI) ontology.getIdentifier())) {
			throw new RuntimeException("Given ontology is not satisfiable");
		}
		
		// Register the ontology at the built-in reasoner:
		if (builtInFacade instanceof PelletFacade) {
			try {
				builtInFacade.register(owlOntology);
			} catch (ExternalToolException e) {
				e.printStackTrace();
                throw new IllegalArgumentException(
                        "This ontology could not be registered at the built-in reasoner",
                        e);
			}
		} 
	}

	public void registerOntologyNoVerification(Ontology ontology) {
		owlOntology = convertOntology(ontology);
		
		// Register the ontology at the built-in reasoner:
		if (builtInFacade instanceof PelletFacade) {
			try {
				builtInFacade.register(owlOntology);
			} catch (ExternalToolException e) {
				e.printStackTrace();
                throw new IllegalArgumentException(
                        "This ontology could not be registered at the built-in reasoner",
                        e);
			}
		} 
	}

	public void deRegisterOntology(IRI ontologyID) {
		builtInFacade.deRegister(owlOntology);
	}
	
	public boolean isSatisfiable(IRI ontologyID) {
		return builtInFacade.isConsistent();
	}
	
	public Set<Concept> getAllConcepts() {
		Set<Concept> elements = new HashSet<Concept>();
		Set<OWLEntity> set = builtInFacade.allClasses();
		for (OWLEntity entity : set) {
			try {
				elements.add(wsmoFactory.createConcept(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment())));
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public boolean isConsistent(LogicalExpression logExpr) {
		OWLDescription des = transformLogicalExpression(logExpr);
		try {
			if (des != null) 
				return builtInFacade.isConsistent(des);
			else 
				throw new InternalReasonerException("This logical expression is " +
						"not supported for consistency check!");
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} 
	}

	public boolean isConsistent(Concept concept) {
		try {
			return builtInFacade.isConsistent(
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public Set<Instance> getAllInstances() {
		Set<Instance> elements = new HashSet<Instance>();
		Set<OWLEntity> set = builtInFacade.allIndividuals();
		for (OWLEntity entity : set) {
			try {
				elements.add(wsmoFactory.createInstance(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment())));
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}

	public Set<IRI> getAllAttributes() {
		Set<IRI> elements = new HashSet<IRI>();
		Set<OWLEntity> set = builtInFacade.allProperties();
		for (OWLEntity entity : set) {
			try {
				elements.add(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment()));
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public Set<IRI> getAllConstraintAttributes() {
		Set<IRI> elements = new HashSet<IRI>();
		Set<OWLEntity> set = builtInFacade.allDataProperties();
		for (OWLEntity entity : set) {
			try {
				elements.add(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment()));
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public Set<IRI> getAllInferenceAttributes() {
		Set<IRI> elements = new HashSet<IRI>();
		Set<OWLEntity> set = builtInFacade.allObjectProperties();
		for (OWLEntity entity : set) {
			try {
				elements.add(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment()));
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.descendantClassesOf(
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					elements.add(wsmoFactory.createConcept(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment())));
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}

	public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.ancestorClassesOf(
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					elements.add(wsmoFactory.createConcept(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment())));
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<Concept> getEquivalentConcepts(Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<OWLEntity> set = builtInFacade.equivalentClassesOf(
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
			for (OWLEntity entity : set) {
				elements.add(wsmoFactory.createConcept(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment())));
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public boolean isEquivalentConcept(Concept concept1, Concept concept2) {
		try {
			Set<OWLEntity> set = builtInFacade.equivalentClassesOf(
					owlDataFactory.getOWLClass(new URI(
							concept1.getIdentifier().toString())));
			return set.contains(owlDataFactory.getOWLClass(new URI(
					concept2.getIdentifier().toString())));
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public boolean isSubConceptOf(IRI ontologyID, Concept subConcept, 
			Concept superConcept) {
		try {
			return builtInFacade.isSubClassOf(
					owlDataFactory.getOWLClass(new URI(
							subConcept.getIdentifier().toString())), 
					owlDataFactory.getOWLClass(new URI(
							superConcept.getIdentifier().toString())));
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
	}

	public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept) {
		try {
			return builtInFacade.isInstanceOf(
					owlDataFactory.getOWLIndividual(new URI(
							instance.getIdentifier().toString())), 
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public Set<Instance> getInstances(IRI ontologyID, Concept concept) {
		Set<Instance> elements = new HashSet<Instance>();
		try {
			Set<OWLEntity> set = builtInFacade.allInstancesOf(
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
			for (OWLEntity entity : set) {
				elements.add(wsmoFactory.createInstance(
						wsmoFactory.createIRI(ns + entity.getURI().getFragment())));
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}	
		return elements;
	}

	public Set<Concept> getConcepts(IRI ontologyID, Instance instance) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.allTypesOf(
					owlDataFactory.getOWLIndividual(new URI(
							instance.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) { 
				for (OWLEntity entity : set2) {
					elements.add(wsmoFactory.createConcept(
							wsmoFactory.createIRI(ns + entity.getURI().getFragment())));
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}	
		return elements;
	}

	public Set<IRI> getSubRelations(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set> set = builtInFacade.descendantPropertiesOf(
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					elements.add(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment()));
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getSuperRelations(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set> set = builtInFacade.ancestorPropertiesOf(
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					elements.add(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment()));
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getEquivalentRelations(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLEntity> set = builtInFacade.equivalentPropertiesOf(
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())));
			for (OWLEntity entity : set) {
				elements.add(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment()));
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getInverseRelations(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLEntity> set = builtInFacade.inversePropertiesOf(
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())));
			for (OWLEntity entity : set) {
				elements.add(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment()));
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<Concept> getConceptsOf(Identifier attributeId) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<OWLEntity> set = builtInFacade.domainsOf(
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())));
			for (OWLEntity entity : set) {
				elements.add(wsmoFactory.createConcept(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment())));
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getRangesOfInferingAttribute(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLEntity> set = builtInFacade.rangesOf(
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())));
			for (OWLEntity entity : set) {
				elements.add(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment()));
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getRangesOfConstraintAttribute(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLConcreteDataTypeImpl> set = builtInFacade.rangesOf(
					owlDataFactory.getOWLDataProperty(new URI(
							attributeId.toString())));
			for (OWLConcreteDataTypeImpl dataType : set) {
				elements.add(wsmoFactory.createIRI(
						ns + dataType.getURI().getFragment()));
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<IRI, Set<IRI>> getConstraintAttributeValues(
			Instance instance) {
		Map<IRI, Set<IRI>> elements = new HashMap<IRI, Set<IRI>>();
		try {
			Set<Entry<OWLEntity, Set<OWLConcreteDataImpl>>> entrySet = 
				builtInFacade.getDataPropertyValues(owlDataFactory.
						getOWLIndividual(new URI(instance.getIdentifier().
								toString()))).entrySet();
			for (Entry<OWLEntity, Set<OWLConcreteDataImpl>> entry : entrySet) {
				Set<OWLConcreteDataImpl> set = entry.getValue();
				Set<IRI> IRISet = new HashSet<IRI>();
				for (OWLConcreteDataImpl data : set) {
					IRISet.add(wsmoFactory.createIRI(
							ns + data.getValue().toString()));
				}
				elements.put(wsmoFactory.createIRI(
						ns + entry.getKey().getURI().getFragment()), 
						IRISet);
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<IRI, Set<IRI>> getInferingAttributeValues(
			Instance instance) {
		Map<IRI, Set<IRI>> elements = new HashMap<IRI, Set<IRI>>();
		try {
			Set<Entry<OWLEntity, Set<OWLEntity>>> entrySet = 
				builtInFacade.getObjectPropertyValues(owlDataFactory.
						getOWLIndividual(new URI(instance.getIdentifier().
								toString()))).entrySet();
			for (Entry<OWLEntity, Set<OWLEntity>> entry : entrySet) {
				Set<OWLEntity> set = entry.getValue();
				Set<IRI> IRISet = new HashSet<IRI>();
				for (OWLEntity entity : set) {
					IRISet.add(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment()));
				}
				elements.put(wsmoFactory.createIRI(
						ns + entry.getKey().getURI().getFragment()), 
						IRISet);
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<Instance, Set<IRI>> getInferingAttributeInstances(
			Identifier attributeId) {
		Map<Instance, Set<IRI>> elements = new HashMap<Instance, Set<IRI>>();
		try {
			Set<Entry<OWLEntity, Set<OWLEntity>>> entrySet = 
				builtInFacade.getPropertyValues(owlDataFactory.
						getOWLObjectProperty(new URI(attributeId.
								toString()))).entrySet();
			for (Entry<OWLEntity, Set<OWLEntity>> entry : entrySet) {
				Set<OWLEntity> set = entry.getValue();
				Set<IRI> IRISet = new HashSet<IRI>();
				for (OWLEntity entity : set) {
					IRISet.add(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment()));
				}
				elements.put(wsmoFactory.createInstance(wsmoFactory.createIRI(
						ns + entry.getKey().getURI().getFragment())), IRISet);
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<Instance, Set<IRI>> getConstraintAttributeInstances(
			Identifier attributeId) {
		Map<Instance, Set<IRI>> elements = new HashMap<Instance, Set<IRI>>();
		try {
			Set<Entry<OWLEntity, Set<OWLConcreteDataImpl>>> entrySet = 
				builtInFacade.getPropertyValues(owlDataFactory.
						getOWLDataProperty(new URI(attributeId.
								toString()))).entrySet();
			for (Entry<OWLEntity, Set<OWLConcreteDataImpl>> entry : entrySet) {
				Set<OWLConcreteDataImpl> set = entry.getValue();
				Set<IRI> IRISet = new HashSet<IRI>();
				for (OWLConcreteDataImpl data : set) {
					IRISet.add(wsmoFactory.createIRI(
							ns + data.getValue().toString()));
				}
				elements.put(wsmoFactory.createInstance(wsmoFactory.createIRI(
						ns + entry.getKey().getURI().getFragment())), IRISet);
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public boolean instanceHasInferingAttributeValue(Instance subject, 
			Identifier attributeId, Instance object) {		
		try {
			return builtInFacade.hasPropertyValue(
					owlDataFactory.getOWLIndividual(new URI(
							subject.getIdentifier().toString())), 
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())),
					owlDataFactory.getOWLIndividual(new URI(
							object.getIdentifier().toString())));
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public boolean instanceHasConstraintAttributeValue(Instance subject, 
			Identifier attributeId, DataValue object) {
		String objStr = object.getType().toString();
		try {
			return builtInFacade.hasPropertyValue(
					owlDataFactory.getOWLIndividual(new URI(
							subject.getIdentifier().toString())), 
					owlDataFactory.getOWLDataProperty(new URI(
							attributeId.toString())),
					owlDataFactory.getOWLConcreteData(new URI(
							"http://www.w3.org/2001/XMLSchema" + 
							objStr.substring(objStr.indexOf("#"))), 
							null, object.getValue()));
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public Instance getInferingAttributeValue(Instance subject, 
			Identifier attributeId) {
		try {
			return wsmoFactory.createInstance(wsmoFactory.createIRI( ns +
					builtInFacade.getObjectPropertyValue(
							owlDataFactory.getOWLIndividual(new URI(
									subject.getIdentifier().toString())),
							owlDataFactory.getOWLObjectProperty(new URI(
									attributeId.toString()))).getURI().getFragment()));
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	/**
	 * @return data value of the given instance and attribute
	 */
	public String getConstraintAttributeValue(Instance subject, 
			Identifier attributeId) {
		try {
			return(builtInFacade.getDataPropertyValue(
							owlDataFactory.getOWLIndividual(new URI(
									subject.getIdentifier().toString())),
							owlDataFactory.getOWLDataProperty(new URI(
									attributeId.toString()))).getValue().toString());
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public Set<Instance> getInferingAttributeValues(Instance subject, 
			Identifier attributeId) {
		Set<Instance> elements = new HashSet<Instance>();
		try {
			Set<OWLEntity> set = builtInFacade.getObjectPropertyValues(
							owlDataFactory.getOWLIndividual(new URI(
									subject.getIdentifier().toString())),
							owlDataFactory.getOWLObjectProperty(new URI(
									attributeId.toString())));
			for (OWLEntity entity : set)
				elements.add(wsmoFactory.createInstance(wsmoFactory.createIRI(
						ns + entity.getURI().getFragment())));
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	/**
	 * @return data value of the given instance and attribute
	 */
	public Set<String> getConstraintAttributeValues(Instance subject, 
			Identifier attributeId) {
		Set<String> elements = new HashSet<String>();
		try {
			Set<OWLDataValue> set = builtInFacade.getDataPropertyValues(
							owlDataFactory.getOWLIndividual(new URI(
									subject.getIdentifier().toString())),
							owlDataFactory.getOWLDataProperty(new URI(
									attributeId.toString())));
			for (OWLDataValue dataValue : set)
				elements.add(dataValue.getValue().toString());
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public void printClassTree() {
		builtInFacade.printClassTree();
	}
	
	public String getInfo() {
		return builtInFacade.getInfo();
	}

	public String serialize2OWLAbstractSyntax(OWLOntology owlOntology) 
			throws RendererException {
		Renderer renderer = new Renderer();
    	StringWriter writer = new StringWriter();
    	try {
			renderer.renderOntology(owlOntology, writer);
		} catch (RendererException e) {
			throw new RuntimeException("Problems at serializing owl ontology");
		}
        return writer.toString();
	}
	
	public String serialize2OWLRDFSyntax(OWLOntology owlOntology) {
		org.semanticweb.owl.io.owl_rdf.Renderer renderer = 
			new org.semanticweb.owl.io.owl_rdf.Renderer();
    	StringWriter writer = new StringWriter();
    	try {
			renderer.renderOntology(owlOntology, writer);
		} catch (RendererException e) {
			throw new RuntimeException("Problems at serializing owl ontology");
		}
        return writer.toString();
	}
	
	/**
	 * This method allows to extract the OWL ontology as string, even if the OWL 
	 * ontology is not valid owl dl. The serialized ontology could be validated at 
	 * an online validator as e.g. "http://phoebus.cs.man.ac.uk:9999/OWL/Validator"
	 * @param ontology WSML DL ontology to be transformed to owl
	 * @return string version of transformed OWL ontology
	 */
	public String serializeWSML2OWL(Ontology ontology) {
		// check if given WSML-DL ontology is valid
		WsmlValidator validator = new WsmlValidatorImpl();
		boolean valid = validator.isValid(
				ontology, "http://www.wsmo.org/wsml/wsml-syntax/wsml-dl", 
				new Vector(), new Vector());
		if (!valid) {
			throw new RuntimeException ("The given WSML-DL ontology is not valid!");
		}
		// normalize ontology
        ontology = normalizeOntology(ontology);
        // transform ontology
		try {
			owlOntology = transformOntology(ontology);
		} catch (OWLException e) {
			throw new RuntimeException ("Difficulties in building the OWL ontology");
		} catch (URISyntaxException e) {
			throw new RuntimeException ("Difficulties in building the OWL ontology");
		}	
        return serialize2OWLRDFSyntax(owlOntology);
	}
	
	public QueryResults executeQuery(String query) {
		throw new UnsupportedOperationException();
//		QueryResults results = null;
//		results = builtInFacade.evaluate(query);
//		return results;
	}
	
	public void registerOntologies(Set<Ontology> ontologies) 
			throws InconsistencyException {
		throw new UnsupportedOperationException();
	}

	public void registerOntologiesNoVerification(Set<Ontology> ontologies) {
		throw new UnsupportedOperationException();
	}
	
	public void deRegisterOntology(Set<IRI> ontologyIDs) {
		throw new UnsupportedOperationException();
	}
	
	public Set<Map<Variable, Term>> executeQuery(IRI ontologyID, LogicalExpression query) {
		throw new UnsupportedOperationException();
	}

	public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query) {
		throw new UnsupportedOperationException();
	}

	public boolean entails(IRI ontologyID, LogicalExpression expression) {
		throw new UnsupportedOperationException();
	}

	public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions) {
		throw new UnsupportedOperationException();
	}

	public Set<ConsistencyViolation> checkConsistency(IRI ontologyId) {
		throw new UnsupportedOperationException();
	}
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/07/21 16:25:21  nathalie
 * completing the pellet reasoner integration
 *
 * Revision 1.2  2006/07/20 17:50:23  nathalie
 * integration of the pellet reasoner
 *
 * Revision 1.1  2006/07/18 08:21:01  nathalie
 * adding wsml dl reasoner interface,
 * transformation from wsml dl to owl-dl
 *
 *
 */