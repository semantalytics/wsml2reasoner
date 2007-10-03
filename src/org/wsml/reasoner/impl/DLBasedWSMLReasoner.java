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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.semanticweb.owl.impl.model.OWLConcreteDataImpl;
import org.semanticweb.owl.impl.model.OWLConcreteDataTypeImpl;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.validation.SpeciesValidator;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.WSMLDL2OWLTransformer;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.builtin.kaon2.Kaon2DLFacade;
import org.wsml.reasoner.builtin.pellet.PelletFacade;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.dl.Relation2AttributeNormalizer;
import org.wsml.reasoner.transformation.dl.WSMLDLLogExprNormalizer;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.validator.WsmlValidator;

import uk.ac.man.cs.img.owl.validation.ValidatorLogger;

/**
 * A prototypical implementation of a WSML-DL reasoner.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: graham $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/impl/DLBasedWSMLReasoner.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.22 $ $Date: 2007-10-03 16:33:45 $
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
				builtInFacade = new PelletFacade();
				break;
			case KAON2:
				try {
					builtInFacade = new Kaon2DLFacade();
				} catch (OWLException e) {
					throw new InternalReasonerException(e);
				}
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
	
    public OWLOntology convertOntology(Ontology ontology) {
		SpeciesValidator owlValidator = null;
		
		// check if given WSML-DL ontology is valid
		WsmlValidator validator = Factory.createWsmlValidator(null);		
		boolean valid = validator.isValid(
				ontology, "http://www.wsmo.org/wsml/wsml-syntax/wsml-dl", 
				new Vector(), new Vector());
		if (!valid) {
			throw new RuntimeException ("The given WSML-DL ontology is not " +
					"valid! Please use a WSML Validator to check why this " +
					"is not valid WSML-DL (e.g. " +
					"http://tools.deri.org/wsml/validator/v1.2/");
		}
		if (ontology.getDefaultNamespace() != null) {
			ns = ontology.getDefaultNamespace().getIRI().toString();
		}

		// normalize ontology
        ontology = normalizeOntology(ontology);

        // transform ontology
		try {
			owlOntology = transformOntology(ontology);
			
			// check if resulting OWL DL ontology is valid
			owlValidator = 
				new uk.ac.man.cs.img.owl.validation.SpeciesValidator();	
			SpeciesValidatorReporter reporter = new ValidatorLogger();
			owlValidator.setReporter(reporter);
			valid = owlValidator.isOWLDL(owlOntology);
		} catch (OWLException e) {
			throw new RuntimeException ("Difficulties in building the OWL " +
					"ontology");
		} catch (URISyntaxException e) {
			throw new RuntimeException ("Difficulties in building the OWL " +
					"ontology");
		}
		if (!(builtInFacade instanceof PelletFacade) && !valid) {
			throw new RuntimeException("The transformed OWL DL ontology is " +
					"not valid! Please use an OWL Validator to check why " +
					"this is not OWL DL (e.g. " +
					"http://phoebus.cs.man.ac.uk:9999/OWL/Validator");
		}
        return owlOntology;
    }
	
	/*
	 * Method to normalize a WSML ontology
	 */
	protected Ontology normalizeOntology(Ontology ontology) {
		Ontology normalizedOntology;
		
        //in order to keep track of cyclic imports
        Set<Ontology> importedOntologies = new HashSet<Ontology>();
        
		// Replace relations, subRelations and relationinstances
		OntologyNormalizer normalizer = new Relation2AttributeNormalizer(
				wsmoManager);
		normalizedOntology = normalizer.normalize(ontology);
//      System.out.println("\n-------\n Ontology after simplification:\n" +
//      BaseDLReasonerTest.serializeOntology(normalizedOntology));

		
		// Convert conceptual syntax to logical expressions
		normalizer = new AxiomatizationNormalizer(wsmoManager, importedOntologies);
        normalizedOntology = normalizer.normalize(normalizedOntology);
//      System.out.println("\n-------\n Ontology after simplification:\n" +
//      BaseDLReasonerTest.serializeOntology(normalizedOntology));
        
        // Replace unnumbered anonymous identifiers and convert logical 
        // expressions
        normalizer = new WSMLDLLogExprNormalizer(wsmoManager);
		normalizedOntology = normalizer.normalize(normalizedOntology);
//      System.out.println("\n-------\n Ontology after simplification:\n" +
//      BaseDLReasonerTest.serializeOntology(normalizedOntology));
        
        return normalizedOntology;
	}
	
	/*
	 * Method to transform a WSML ontology into an OWL ontology.
	 */
	
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
		
		// Get a change visitor which will enact change events over the 
		// ontology
		changeVisitor = owlConnection.getChangeVisitor(owlOntology);

		// Set up the transformer
		transformer = new WSMLDL2OWLTransformer(owlOntology, owlDataFactory, 
				changeVisitor);
		owlOntology = transformer.transform(ontology);
		
		return owlOntology;
	}
	
	
	protected OWLDescription transformLogicalExpression(
			LogicalExpression logExpr) {
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
	
	public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException {
        boolean satisfiable = true;
        Set<IRI> ids = new HashSet<IRI>();
        
        // register ontologies
        try{
            registerOntologiesNoVerification(ontologies);
        }catch (Exception e){
            //@TODO do more clever!!!!
            e.printStackTrace();
            throw new InconsistencyException("Inconsistency detected");
        }
        
		// check satisfiability
        for (Ontology ontology : ontologies) {
        	ids.add((IRI) ontology.getIdentifier());
        	// throw exception if the ontology is not satisfiable
    		if (!isSatisfiable((IRI) ontology.getIdentifier())) {
    			satisfiable = false;
    		}
        }
        if (!satisfiable) {
        	for (IRI id : ids) {
	        	try {
					builtInFacade.deRegister(id.toString());
				} catch (ExternalToolException e) {
					throw new InconsistencyException("Given ontology is not " +
							"satisfiable. The ontology could not be " +
							"deregistered at the built-in reasoner");
				}
				throw new InconsistencyException("Given ontology is not " +
						"satisfiable");
        	}
        }
	}

	public void registerOntology(Ontology ontology) throws InconsistencyException{	
		Set<Ontology> ontologies = new HashSet<Ontology>();
		ontologies.add(ontology);
		registerOntologies(ontologies);
	}
	
	public void registerOntologiesNoVerification(Set<Ontology> ontologies) {
		for(Ontology ontology : ontologies) {
			owlOntology = convertOntology(ontology);
			
			// Register the ontology at the built-in reasoner:
			try {
				builtInFacade.register(owlOntology);
			} catch (ExternalToolException e) {
				e.printStackTrace();
                throw new IllegalArgumentException(
                        "This ontology could not be registered at the " +
                        "built-in reasoner", e);
				
			}
		}
	}

	public void registerOntologyNoVerification(Ontology ontology) {
		Set<Ontology> ontologies = new HashSet<Ontology>();
		ontologies.add(ontology);
		registerOntologiesNoVerification(ontologies);
	}

	public void deRegisterOntology(Set<IRI> ontologyIDs) {
		for (IRI ontologyID : ontologyIDs) {
			try {
				builtInFacade.deRegister(ontologyID.toString());
			} catch (ExternalToolException e) {
				e.printStackTrace();
	            throw new IllegalArgumentException(
	                    "This ontology could not be deregistered at the " +
	                    "built-in reasoner", e);
			}
		}
	}
	
	public void deRegisterOntology(IRI ontologyID) {
		Set<IRI> ontologyIDs = new HashSet<IRI>();
		ontologyIDs.add(ontologyID);
		deRegisterOntology(ontologyIDs);
	}
	
	public boolean isSatisfiable(IRI ontologyID) {
		return builtInFacade.isConsistent(ontologyID.toString());
	}
	
	public Set<Concept> getAllConcepts(IRI ontologyID) {
		Set<Concept> elements = new HashSet<Concept>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allClasses(ontologyID.toString());
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.getConcept(wsmoFactory.createIRI(
							entity.getURI().toString())));
				} 
				else {
					elements.add(wsmoFactory.getConcept(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment())));
				}
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public Set<Instance> getAllInstances(IRI ontologyID) {
		Set<Instance> elements = new HashSet<Instance>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allIndividuals(
					ontologyID.toString());
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.getInstance(wsmoFactory.createIRI(
							entity.getURI().toString())));
				} 
				else {
					elements.add(wsmoFactory.getInstance(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment())));
				}
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}

	public Set<IRI> getAllAttributes(IRI ontologyID) {
		Set<IRI> elements = new HashSet<IRI>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allProperties(
					ontologyID.toString());
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.createIRI(
							entity.getURI().toString()));
				} 
				else {
					elements.add(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment()));
				}
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public Set<IRI> getAllConstraintAttributes(IRI ontologyID) {
		Set<IRI> elements = new HashSet<IRI>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allDataProperties(
					ontologyID.toString());
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.createIRI(
							entity.getURI().toString()));
				} 
				else {
					elements.add(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment()));
				}
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public Set<IRI> getAllInferenceAttributes(IRI ontologyID) {
		Set<IRI> elements = new HashSet<IRI>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allObjectProperties(
					ontologyID.toString());
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.createIRI(
							entity.getURI().toString()));
				} 
				else {
					elements.add(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment()));
				}
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
					ontologyID.toString(), 
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(
										entity.getURI().toString())));
					} 
					else {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(ns + 
										entity.getURI().getFragment())));
					}
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		} 
		return elements;
	}

	public Set<Concept> getDirectSubConcepts(IRI ontologyID, Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.subClassesOf(
					ontologyID.toString(), 
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(
										entity.getURI().toString())));
					} 
					else {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(ns + 
										entity.getURI().getFragment())));
					}
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
					ontologyID.toString(), owlDataFactory.getOWLClass(
							new URI(concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(
										entity.getURI().toString())));
					} 
					else {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(ns + 
										entity.getURI().getFragment())));
					}
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<Concept> getDirectSuperConcepts(IRI ontologyID, Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.superClassesOf(
					ontologyID.toString(), owlDataFactory.getOWLClass(
							new URI(concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(
										entity.getURI().toString())));
					} 
					else {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(ns + 
										entity.getURI().getFragment())));
					}
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<Concept> getEquivalentConcepts(IRI ontologyID, 
			Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<OWLEntity> set = builtInFacade.equivalentClassesOf(
					ontologyID.toString(), owlDataFactory.getOWLClass(
							new URI(concept.getIdentifier().toString())));
			for (OWLEntity entity : set) {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.getConcept(
							wsmoFactory.createIRI(
									entity.getURI().toString())));
				} 
				else {
					elements.add(wsmoFactory.getConcept(
							wsmoFactory.createIRI(ns + 
									entity.getURI().getFragment())));
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public boolean isEquivalentConcept(IRI ontologyID, Concept concept1, 
			Concept concept2) {
		try {
			return builtInFacade.isEquivalentClass(ontologyID.toString(), 
					owlDataFactory.getOWLClass(new URI(
							concept1.getIdentifier().toString())), 
							owlDataFactory.getOWLClass(new URI(
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
			return builtInFacade.isSubClassOf(ontologyID.toString(), 
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

	public boolean isMemberOf(IRI ontologyID, Instance instance, 
			Concept concept) {
		try {
			return builtInFacade.isInstanceOf(ontologyID.toString(), 
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
					ontologyID.toString(), owlDataFactory.getOWLClass(
							new URI(concept.getIdentifier().toString())));
			for (OWLEntity entity : set) {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.getInstance(
							wsmoFactory.createIRI(
									entity.getURI().toString())));
				} 
				else {
					elements.add(wsmoFactory.getInstance(
							wsmoFactory.createIRI(ns + 
									entity.getURI().getFragment())));
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}	
		return elements;
	}
	
	public Set<Concept> getDirectConcepts(IRI ontologyID, Instance instance) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.typesOf(ontologyID.toString(), 
					owlDataFactory.getOWLIndividual(new URI(
							instance.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) { 
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(
										entity.getURI().toString())));
					} 
					else {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(ns + 
										entity.getURI().getFragment())));
					}
				}
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
			Set<Set> set = builtInFacade.allTypesOf(ontologyID.toString(), 
					owlDataFactory.getOWLIndividual(new URI(
							instance.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) { 
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(
										entity.getURI().toString())));
					} 
					else {
						elements.add(wsmoFactory.getConcept(
								wsmoFactory.createIRI(ns + 
										entity.getURI().getFragment())));
					}
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}	
		return elements;
	}

	public Set<IRI> getSubRelations(IRI ontologyID, Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set> set = builtInFacade.descendantPropertiesOf(
					ontologyID.toString(), owlDataFactory.getOWLObjectProperty(
							new URI(attributeId.toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.createIRI(
								entity.getURI().toString()));
					} 
					else {
						elements.add(wsmoFactory.createIRI(
								ns + entity.getURI().getFragment()));
					}
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getDirectSubRelations(IRI ontologyID, Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set> set = builtInFacade.subPropertiesOf(
					ontologyID.toString(), owlDataFactory.getOWLObjectProperty(
							new URI(attributeId.toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.createIRI(
								entity.getURI().toString()));
					} 
					else {
						elements.add(wsmoFactory.createIRI(
								ns + entity.getURI().getFragment()));
					}
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getSuperRelations(IRI ontologyID, Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set> set = builtInFacade.ancestorPropertiesOf(
					ontologyID.toString(), owlDataFactory.getOWLObjectProperty(
							new URI(attributeId.toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.createIRI(
								entity.getURI().toString()));
					} 
					else {
						elements.add(wsmoFactory.createIRI(
								ns + entity.getURI().getFragment()));
					}
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getDirectSuperRelations(IRI ontologyID, Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set> set = builtInFacade.superPropertiesOf(
					ontologyID.toString(), owlDataFactory.getOWLObjectProperty(
							new URI(attributeId.toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						elements.add(wsmoFactory.createIRI(
								entity.getURI().toString()));
					} 
					else {
						elements.add(wsmoFactory.createIRI(
								ns + entity.getURI().getFragment()));
					}
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getEquivalentRelations(IRI ontologyID, 
			Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLEntity> set = builtInFacade.equivalentPropertiesOf(
					ontologyID.toString(), owlDataFactory.getOWLObjectProperty(
							new URI(attributeId.toString())));
			for (OWLEntity entity : set) {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.createIRI(
							entity.getURI().toString()));
				} 
				else {
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
	
	public Set<IRI> getInverseRelations(IRI ontologyID, 
			Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLEntity> set = builtInFacade.inversePropertiesOf(
					ontologyID.toString(), owlDataFactory.getOWLObjectProperty(
							new URI(attributeId.toString())));
			for (OWLEntity entity : set) {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.createIRI(
							entity.getURI().toString()));
				} 
				else {
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
	
	public Set<Concept> getConceptsOf(IRI ontologyID, Identifier attributeId) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<OWLEntity> set = builtInFacade.domainsOf(ontologyID.toString(), 
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())));
			for (OWLEntity entity : set) {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.getConcept(wsmoFactory.createIRI(
							entity.getURI().toString())));
				} 
				else {
					elements.add(wsmoFactory.getConcept(wsmoFactory.createIRI(
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
	
	public Set<IRI> getRangesOfInferingAttribute(IRI ontologyID, 
			Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLEntity> set = builtInFacade.rangesOf(ontologyID.toString(), 
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())));
			for (OWLEntity entity : set) {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.createIRI(
							entity.getURI().toString()));
				} 
				else {
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
	
	public Set<IRI> getRangesOfConstraintAttribute(IRI ontologyID, 
			Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLConcreteDataTypeImpl> set = builtInFacade.rangesOf(
					ontologyID.toString(), owlDataFactory.getOWLDataProperty(
							new URI(attributeId.toString())));
			for (OWLConcreteDataTypeImpl dataType : set) {
				if (ns == null || !dataType.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.createIRI(
							dataType.getURI().toString()));
				} 
				else {
					elements.add(wsmoFactory.createIRI(
							ns + dataType.getURI().getFragment()));
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<IRI, Set<Term>> getConstraintAttributeValues(IRI ontologyID, 
			Instance instance) {
		Map<IRI, Set<Term>> elements = new HashMap<IRI, Set<Term>>();
		try {
			Set<Entry<OWLEntity, Set<OWLConcreteDataImpl>>> entrySet = 
				builtInFacade.getDataPropertyValues(ontologyID.toString(), 
						owlDataFactory.getOWLIndividual(
								new URI(instance.getIdentifier().toString())))
								.entrySet();
			for (Entry<OWLEntity, Set<OWLConcreteDataImpl>> entry : entrySet) {
				Set<OWLConcreteDataImpl> set = entry.getValue();
				Set<Term> valueSet = new HashSet<Term>();
				for (OWLConcreteDataImpl data : set) {
					valueSet.add(getDataValue(data.getValue().toString(), 
							data.getURI().getFragment()));				
				}
				if (ns == null || !entry.getKey().getURI().toString().startsWith(ns)) {
					elements.put(wsmoFactory.createIRI(
							entry.getKey().getURI().toString()), valueSet);
				} 
				else {
					elements.put(wsmoFactory.createIRI(
							ns + entry.getKey().getURI().getFragment()), 
							valueSet);
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<IRI, Set<Term>> getInferingAttributeValues(IRI ontologyID, 
			Instance instance) {
		Map<IRI, Set<Term>> elements = new HashMap<IRI, Set<Term>>();
		try {
			Set<Entry<OWLEntity, Set<OWLEntity>>> entrySet = 
				builtInFacade.getObjectPropertyValues(ontologyID.toString(), 
						owlDataFactory.getOWLIndividual(new URI(
								instance.getIdentifier().toString()))).
								entrySet();
			for (Entry<OWLEntity, Set<OWLEntity>> entry : entrySet) {
				Set<OWLEntity> set = entry.getValue();
				Set<Term> IRISet = new HashSet<Term>();
				for (OWLEntity entity : set) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						IRISet.add(wsmoFactory.createIRI(
								entity.getURI().toString()));
					} 
					else {
						IRISet.add(wsmoFactory.createIRI(
								ns + entity.getURI().getFragment()));
					}
				}
				if (ns == null || !entry.getKey().getURI().toString().startsWith(ns)) {
					elements.put(wsmoFactory.createIRI(
							entry.getKey().getURI().toString()), IRISet);
				} 
				else {
					elements.put(wsmoFactory.createIRI(
							ns + entry.getKey().getURI().getFragment()), 
							IRISet);
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<Instance, Set<Term>> getInferingAttributeInstances(
			IRI ontologyID, Identifier attributeId) {
		Map<Instance, Set<Term>> elements = new HashMap<Instance, Set<Term>>();
		try {
			Set<Entry<OWLEntity, Set<OWLEntity>>> entrySet = 
				builtInFacade.getPropertyValues(ontologyID.toString(), 
						owlDataFactory.getOWLObjectProperty(new URI(
								attributeId.toString()))).entrySet();
			for (Entry<OWLEntity, Set<OWLEntity>> entry : entrySet) {
				Set<OWLEntity> set = entry.getValue();
				Set<Term> IRISet = new HashSet<Term>();
				for (OWLEntity entity : set) {
					if (ns == null || !entity.getURI().toString().startsWith(ns)) {
						IRISet.add(wsmoFactory.createIRI(
								entity.getURI().toString()));
					} 
					else {
						IRISet.add(wsmoFactory.createIRI(
								ns + entity.getURI().getFragment()));
					}
				}
				if (ns == null || !entry.getKey().getURI().toString().startsWith(ns)) {
					elements.put(wsmoFactory.getInstance(wsmoFactory.createIRI(
							entry.getKey().getURI().toString())), IRISet);
				} 
				else {
					elements.put(wsmoFactory.getInstance(wsmoFactory.createIRI(
							ns + entry.getKey().getURI().getFragment())), IRISet);
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<Instance, Set<Term>> getConstraintAttributeInstances(
			IRI ontologyID, Identifier attributeId) {
		Map<Instance, Set<Term>> elements = new HashMap<Instance, Set<Term>>();
		try {
			Set<Entry<OWLEntity, Set<OWLConcreteDataImpl>>> entrySet = 
				builtInFacade.getPropertyValues(ontologyID.toString(), 
						owlDataFactory.getOWLDataProperty(new URI(
								attributeId.toString()))).entrySet();
			for (Entry<OWLEntity, Set<OWLConcreteDataImpl>> entry : entrySet) {
				Set<OWLConcreteDataImpl> set = entry.getValue();
				Set<Term> valueSet = new HashSet<Term>();
				for (OWLConcreteDataImpl data : set) {					
					valueSet.add(getDataValue(data.getValue().toString(), 
								data.getURI().getFragment()));	
				}
				if (ns == null || !entry.getKey().getURI().toString().startsWith(ns)) {
					elements.put(wsmoFactory.getInstance(wsmoFactory.createIRI(
							entry.getKey().getURI().toString())), valueSet);
				} 
				else {
					elements.put(wsmoFactory.getInstance(wsmoFactory.createIRI(
							ns + entry.getKey().getURI().getFragment())), valueSet);
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	
	public Set getInferingAttributeValues(IRI ontologyID, 
			Instance subject, Identifier attributeId) {
		Set elements = new HashSet();
		try {
			Set<OWLEntity> set = builtInFacade.getObjectPropertyValues(
					ontologyID.toString(), owlDataFactory.getOWLIndividual(
							new URI(subject.getIdentifier().toString())),
							owlDataFactory.getOWLObjectProperty(
									new URI(attributeId.toString())));
			for (OWLEntity entity : set) {
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.createIRI(
							entity.getURI().toString()));
				} 
				else {
					elements.add(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment()));
				}
			}
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		} catch (InterruptedException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	/**
	 * @return data value of the given instance and attribute
	 */
	
	public Set getConstraintAttributeValues(IRI ontologyID, 
			Instance subject, Identifier attributeId) {
		Set elements = new HashSet();
		try {
			Set<OWLDataValue> set = builtInFacade.getDataPropertyValues(
					ontologyID.toString(),owlDataFactory.getOWLIndividual(
							new URI(subject.getIdentifier().toString())),
							owlDataFactory.getOWLDataProperty(
									new URI(attributeId.toString())));
			for (OWLDataValue dataValue : set)
				elements.add(dataValue.getValue().toString());
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		} catch (InterruptedException e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
//	public QueryResults executeQuery(IRI ontologyID, String query) {
//		throw new UnsupportedOperationException();
//		QueryResults results = null;
//		results = builtInFacade.evaluate(query);
//		return results;
//  }
	
	public Set<Map<Variable, Term>> executeQuery(IRI ontologyID, 
			LogicalExpression query) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method checks whether a given query (without variables) is true or 
	 * false. E.g. check whether Mary[hasChild hasValue Jack] is true or not.
	 * 
	 * @return true or false
	 */
	public boolean executeGroundQuery(IRI ontologyID, 
			LogicalExpression query) {
		if (query.toString().contains("?")) {
			throw new InternalReasonerException("A ground query may not contain " +
					"variables!");
		}
		if (query instanceof AttributeValueMolecule) {
			AttributeValueMolecule attr = (AttributeValueMolecule) query;
			if (attr.getRightParameter() instanceof DataValue) {
				return instanceHasConstraintAttributeValue(ontologyID, 
						wsmoFactory.createInstance((IRI) attr.getLeftParameter()), 
						(Identifier) attr.getAttribute(), 
						(DataValue) attr.getRightParameter());
			}
			else {
				return instanceHasInferingAttributeValue(ontologyID, 
						wsmoFactory.createInstance((IRI) attr.getLeftParameter()), 
						(Identifier) attr.getAttribute(), 
						wsmoFactory.createInstance((IRI) attr.getRightParameter()));
			}
		}
		else if (query instanceof MembershipMolecule) {
			MembershipMolecule attr = (MembershipMolecule) query;
			return isMemberOf(ontologyID, wsmoFactory.createInstance(
					(IRI) attr.getLeftParameter()), wsmoFactory.createConcept(
							(IRI) attr.getRightParameter()));
		}
		else if (query instanceof SubConceptMolecule) {
			SubConceptMolecule attr = (SubConceptMolecule) query;
			return isSubConceptOf(ontologyID, wsmoFactory.createConcept(
					(IRI) attr.getLeftParameter()), wsmoFactory.createConcept(
							(IRI) attr.getRightParameter()));
		}
		else {
			return entails(ontologyID, query);
		}
	}

	/**
	 * The method supports the following logical expressions as they are 
	 * allowed in formulae in WSML-DL:
	 * - Atom
	 * - MembershipMolecule
	 * - Conjunction
	 * - Disjunction
	 * - Negation
	 * - UniversalQuantification
	 * - ExistentialQuantification
	 * 
	 * @return true if the given expression is satisfiable, false otherwise
	 * @throws InternalReasonerException if a logical expression different
	 * 			than the ones mentionned above are given as input
	 */
	public boolean entails(IRI ontologyID, LogicalExpression expression) {
		OWLDescription des = transformLogicalExpression(expression);
		try {
			if (des != null) 
				return builtInFacade.isConsistent(ontologyID.toString(), des);
			else 
				throw new InternalReasonerException("This logical expression" +
						" is not supported for consistency check!");
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (InterruptedException e) {
			throw new InternalReasonerException(e);
		} 
	}

	public boolean entails(IRI ontologyID, 
			Set<LogicalExpression> expressions) {
		for (LogicalExpression e : expressions) {
			if (!entails(ontologyID, e))
                return false;
        }
        return true;
	}

	public Set<ConsistencyViolation> checkConsistency(IRI ontologyId) {
		throw new UnsupportedOperationException();
	}
	
	/*
	 * @return true if the given subject instance has an attribute value of the 
	 * 			given infering attribute with the given object instance as value
	 */
	private boolean instanceHasInferingAttributeValue(IRI ontologyID, 
			Instance subject, Identifier attributeId, Instance object) {		
		try {
			return builtInFacade.hasPropertyValue(ontologyID.toString(), 
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
		} catch (InterruptedException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	/*
	 * @return true if the given subject instance has an attribute value of the 
	 * 			given constraint attribute with the given object data value as value
	 */
	private boolean instanceHasConstraintAttributeValue(IRI ontologyID, 
			Instance subject, Identifier attributeId, DataValue object) {
		String objStr = object.getType().toString();
		try {
			return builtInFacade.hasPropertyValue(ontologyID.toString(), 
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
		} catch (InterruptedException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	private Term getDataValue(String value, String type) {
		Term val = null;
		if (type.equals("string")) {
			val = dataFactory.createWsmlString(value);
		}
		if (type.equals("integer")) {
			val = dataFactory.createWsmlInteger(value);
		}
		if (type.equals("decimal")) {
			val = dataFactory.createWsmlDecimal(value);
		}
		if (type.equals("float")) {
			val = dataFactory.createWsmlFloat(value);
		}
		if (type.equals("double")) {
			val = dataFactory.createWsmlDouble(value);
		}
//TODO: check transformation from anyURI, QName, duration, dateTime, time and so on to wsml datavalues!
		if (type.equals("anyURI")) {
			val = wsmoFactory.createIRI(value);
		}
		if (type.equals("QName")) {
//			val = null;
		}
		if (type.equals("boolean")) {
			val = dataFactory.createWsmlBoolean(value);
		}
		if (type.equals("duration")) {
//			val = dataFactory.createWsmlDuration(value);
		}
		if (type.equals("dateTime")) {
//			val = dataFactory.createWsmlDateTime(value);
		}
		if (type.equals("time")) {
//			val = dataFactory.createWsmlTime(value);
		}
		if (type.equals("date")) {
			int year = Integer.valueOf(value.substring(
					0, value.indexOf("-"))).intValue();
			int month = Integer.valueOf(value.substring(
					value.indexOf("-")+1, value.lastIndexOf("-"))).intValue();
			int day = Integer.valueOf(value.substring(
					value.lastIndexOf("-")+1, value.length())).intValue();
			val = dataFactory.createWsmlDate(new GregorianCalendar(year, month, day));
		}
		if (type.equals("gYearMonth")) {
//			val = dataFactory.createWsmlGregorianYearMonth(value);
		}
		if (type.equals("gYear")) {
			val = dataFactory.createWsmlGregorianYear(value);
		}
		if (type.equals("gMonthDay")) {
//			val = dataFactory.createWsmlGregorianMonthDay(value);
		}
		if (type.equals("gDay")) {
			val = dataFactory.createWsmlGregorianDay(value);
		}
		if (type.equals("gMonth")) {
			val = dataFactory.createWsmlGregorianMonth(value);
		}
		if (type.equals("hexBinary")) {
//			val = null;
		}
		if (type.equals("base64Binary")) {
//			val = dataFactory.createWsmlBase64Binary(value);
		}
		return val;
	}
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.21  2007-10-03 14:17:38  nathalie
 * added needed import
 *
 * Revision 1.20  2007-08-10 09:45:55  graham
 * renamed spass_plus_t to spass
 *
 * Revision 1.19  2007-08-08 14:48:44  graham
 * Additional parameter (importedOntologies) added to the AxiomatizationNormalizer constructor in order to handle cyclical imports
 *
 * Revision 1.18  2007-08-08 10:57:59  graham
 * Second stage of refactoring unit tests
 *
 * Revision 1.17  2007-04-26 17:39:14  graham
 * Fixed switch to new wsmo4j jars, switched to new IRIS (20070426) and jgrapht (v0.7.1) jars, and removed warning suppressions
 *
 * Revision 1.16  2007/03/01 11:37:32  nathalie
 * updated DLBasedWSMLReasoner according to the new WSMLReasoner interface
 *
 * Revision 1.15  2007/02/09 08:40:53  hlausen
 * DLFacade should be independent of libs of specific reasoner!!!!
 *
 * Revision 1.14  2007/01/11 13:04:47  nathalie
 * removed unnecessary dependencies from pellet library
 *
 * Revision 1.13  2007/01/10 11:50:39  nathalie
 * completed kaon2DLFacade
 *
 * Revision 1.12  2006/12/20 14:06:06  graham
 * organized imports
 *
 * Revision 1.11  2006/11/30 16:54:57  nathalie
 * added methods to get direct super-/sub-concepts and direct super-/sub-relations
 *
 * Revision 1.10  2006/09/14 18:37:05  hlausen
 * enabled the print of class tree if pellet facade logging is set to DEBUG
 *
 * when register inconsitent ontologies the method should not throw the pellete exception but the wsml2reasoner inconsistency exception, however the inconsistencyexception class still needs some fix
 *
 * Revision 1.9  2006/08/31 12:37:50  nathalie
 * removed methods from WSMLDLReasoner interface to the WSMLReasoner interface. Replaced some methods by entails() and groundQuery() methods. Also fixed a bug concerning a nullpointerexception caused by access on default namespace.
 *
 * Revision 1.8  2006/08/21 08:22:10  nathalie
 * changed create elements to get elements
 *
 * Revision 1.7  2006/08/10 08:30:59  nathalie
 * added request for getting direct concept/concepts of an instance
 *
 * Revision 1.6  2006/08/08 10:14:28  nathalie
 * implemented support for registering multiple ontolgies at wsml-dl reasoner
 *
 * Revision 1.5  2006/07/23 15:22:45  nathalie
 * changing treatment for non valid owl dl ontology
 *
 * Revision 1.4  2006/07/21 17:00:08  nathalie
 * fixed problem with wrong value output
 *
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