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
import org.omwg.ontology.Axiom;
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
import org.wsml.reasoner.DLReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.WSMLDL2OWLTransformer;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.builtin.pellet.PelletFacade;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.dl.Relation2AttributeNormalizer;
import org.wsml.reasoner.transformation.dl.WSMLDLLogExprNormalizer;
import org.wsmo.common.Entity;
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
 * @version $Revision: 1.24 $ $Date: 2007-11-19 19:20:10 $
 */
public class DLBasedWSMLReasoner implements WSMLDLReasoner{

	protected DLReasonerFacade builtInFacade = null;
	
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
	
	private int allowImports = 0;

	private boolean disableConsitencyCheck = false;
	
	 
	public DLBasedWSMLReasoner(BuiltInReasoner builtInType,
			WSMO4JManager wsmoManager) {
		this.wsmoManager = wsmoManager;
		wsmoFactory = this.wsmoManager.getWSMOFactory();
		leFactory = this.wsmoManager.getLogicalExpressionFactory();
		dataFactory = this.wsmoManager.getDataFactory();
		switch (builtInType) {
			case PELLET:
				builtInFacade = createFacade(builtInType.getFacadeClass());
				break;
			case KAON2:
			case KAON2DL:
				builtInFacade = createFacade(BuiltInReasoner.KAON2DL.getFacadeClass());
				break;
			default:
				throw new UnsupportedOperationException("Reasoning with "
						+ builtInType.toString() + " is not supported yet!");
			}
	    } 

	/**
	 * Instantiates a new reasoner facade of the given class. The facade must 
	 * have a constructor taking no arguments.
	 * @param className the class of the facade
	 * @return the newly instantiated facade
     * @throws InternalReasonerException if something went wrong while 
     * instantiating the reasoner
	 */
    private DLReasonerFacade createFacade(String className) throws InternalReasonerException {
    	assert className != null: "The classname must not be null";

		try {
			return (org.wsml.reasoner.DLReasonerFacade) Class.forName(className).getConstructor().newInstance();
		} catch (Exception e) {
			throw new InternalReasonerException("Couldn't use the constructor for " + className + " taking no arguments", e);
		}
    }
    
    public void setDisableConsitencyCheck(boolean check){
        this.disableConsitencyCheck = check;
    }
    
	public void setAllowImports(int allowOntoImports){
		this.allowImports = allowOntoImports;
	}
    
	public OWLOntology createOWLOntology(Ontology theOntology) {
		Set <Entity> entities = new HashSet <Entity> ();
		entities.addAll(theOntology.listConcepts());
		entities.addAll(theOntology.listInstances());
		entities.addAll(theOntology.listRelations());
		entities.addAll(theOntology.listRelationInstances());
		entities.addAll(theOntology.listAxioms());
		return createOWLOntology(entities);
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
   public OWLOntology createOWLOntology(Set<Entity> theEntities) {
		// normalize ontology
        Set <Axiom> axioms = convertEntities(theEntities);

        // transform ontology
        boolean valid = false;
		try {
			owlOntology = transformOntology(axioms);
			SpeciesValidator owlValidator = new uk.ac.man.cs.img.owl.validation.SpeciesValidator();
			owlValidator.setReporter(new ValidatorLogger());
			valid = owlValidator.isOWLDL(owlOntology);
		} catch (Exception e) {
			throw new RuntimeException ("Difficulties in building the OWL ontology : ", e);
		}
		if (!(builtInFacade instanceof PelletFacade) && !valid) { //TODO: Ask Nathalie
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
	protected Set <Axiom> convertEntities(Set <Entity> theEntities) {
		// Replace relations, subRelations and relationinstances
		OntologyNormalizer normalizer = new Relation2AttributeNormalizer(wsmoManager);
        theEntities = normalizer.normalizeEntities(theEntities);
		
		// Convert conceptual syntax to logical expressions
		normalizer = new AxiomatizationNormalizer(wsmoManager);
		theEntities = normalizer.normalizeEntities(theEntities);
        
		Set <Axiom> axioms = new HashSet <Axiom> ();
        for (Entity e : theEntities){
        	if (e instanceof Axiom){
        		axioms.add((Axiom) e);
        	}
        }
		
        // Replace unnumbered anonymous identifiers and convert logical expressions
        normalizer = new WSMLDLLogExprNormalizer(wsmoManager);
        axioms = normalizer.normalizeAxioms(axioms);
        
        return axioms;
	}
	
	private URI createUniqueURI() throws URISyntaxException
	{
		StringBuilder uriString = new StringBuilder();
		uriString.append( 'a' );
		byte[] bytes = new java.rmi.dgc.VMID().toString().getBytes();
		
		for( byte b : bytes )
		{
			uriString.append( Integer.toString( b, 16 ) );
		}
		return new URI( uriString.toString() );
	}
	
	/*
	 * Method to transform a WSML ontology into an OWL ontology.
	 */
	
	protected OWLOntology transformOntology(Set <Axiom> theAxioms) throws OWLException, URISyntaxException {
		// Set up the implementation class
		Map <Object, Object> parameters = new HashMap <Object, Object>();
		parameters.put(OWLManager.OWL_CONNECTION, "org.semanticweb.owl.impl.model.OWLConnectionImpl");
		owlConnection = OWLManager.getOWLConnection(parameters);
		
		// Get the OWL Data Factory
		owlDataFactory = owlConnection.getDataFactory();
		
		// Get an OWL ontology
		URI uri = createUniqueURI();

		owlOntology = owlConnection.createOntology(uri, uri);
		
		// Get a change visitor which will enact change events over the 
		// ontology
		changeVisitor = owlConnection.getChangeVisitor(owlOntology);

		// Set up the transformer
		transformer = new WSMLDL2OWLTransformer(owlOntology, owlDataFactory, 
				changeVisitor);
		owlOntology = transformer.transform(theAxioms);
		
		return owlOntology;
	}
	
	
	protected OWLDescription transformLogicalExpression(LogicalExpression logExpr) {
		// Set up the implementation class
		Map <Object, Object> parameters = new HashMap <Object, Object> ();
		parameters.put(OWLManager.OWL_CONNECTION, "org.semanticweb.owl.impl.model.OWLConnectionImpl");
		try {
			owlConnection = OWLManager.getOWLConnection(parameters); // TODO Check with Nathalie - whz is this done everytime this method is called
			owlDataFactory = owlConnection.getDataFactory(); // TODO Check
																// with Nathalie
																// - whz is this
																// done
																// everytime
																// this method
																// is called
			return transformer.transform(logExpr);
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public void registerOntology(Ontology ontology) throws InconsistencyException{	
		Set<Ontology> ontologies = new HashSet<Ontology>();
		ontologies.add(ontology);
		registerOntologies(ontologies);
	}
	
	public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException {
		if(allowImports == 0){
    		ontologies = getAllOntologies(ontologies);
    	}
		
		WsmlValidator validator = Factory.createWsmlValidator(null);
		for (Ontology ontology : ontologies){
			if (!validator.isValid(ontology, "http://www.wsmo.org/wsml/wsml-syntax/wsml-dl", new Vector(), new Vector())){
				throw new RuntimeException ("The given WSML-DL ontology is not " +
						"valid! Please use a WSML Validator to check why this " +
						"is not valid WSML-DL (e.g. " +
						"http://tools.deri.org/wsml/validator/v1.2/");
			}
		}
		
//		TODO: this does not handle imported ontologies
//		if (ontology.getDefaultNamespace() != null) {
//			ns = ontology.getDefaultNamespace().getIRI().toString();
//		}
			
    	Set <Entity> entities = new HashSet <Entity>();
        for (Ontology o : ontologies) {
        	entities.addAll(o.listConcepts());
        	entities.addAll(o.listInstances());
        	entities.addAll(o.listRelations());
        	entities.addAll(o.listRelationInstances());
        	entities.addAll(o.listAxioms());
        }
        registerEntities(entities);
	}
	
	public void registerEntities(Set<Entity> theEntities) throws InconsistencyException{
    	registerEntitiesNoVerification(theEntities);
		if (!disableConsitencyCheck && !isSatisfiable()) {
			deRegister();
			throw new InconsistencyException("Given ontology is not satisfiable");
        }
    }
	
	public void registerOntologyNoVerification(Ontology ontology) {
		Set <Entity> entities = new HashSet <Entity> ();
		entities.addAll(ontology.listConcepts());
    	entities.addAll(ontology.listInstances());
    	entities.addAll(ontology.listRelations());
    	entities.addAll(ontology.listRelationInstances());
    	entities.addAll(ontology.listAxioms());
        registerEntitiesNoVerification(entities);
	}
	
	public void registerEntitiesNoVerification( Set<Entity> theEntities )
    {
		owlOntology = createOWLOntology(theEntities);
		
		try {
			builtInFacade.register(owlOntology);
		} 
		catch (ExternalToolException e) {
            throw new IllegalArgumentException("This ontology could not be registered at the built-in reasoner", e);
		}    
    }

	private Set<Ontology> getAllOntologies(Set <Ontology> ontologies){
    	Set <Ontology> result = new HashSet <Ontology> ();
    	for (Ontology o : ontologies){
    		result.add(o);
    		getAllOntologies(o, result);
    	}
    	return result;
    }

	private void getAllOntologies( Ontology o, Set <Ontology> ontologies){
	    for (Ontology imported : o.listOntologies()){
	    	if (!ontologies.contains( imported )){
	    		ontologies.add(imported);
	    		getAllOntologies(imported, ontologies);
	    	}
	    }
    }
	
	public void deRegister() {
		try {
			builtInFacade.deRegister();
		} 
		catch (ExternalToolException e) {
			e.printStackTrace();
            throw new IllegalArgumentException("This ontology could not be deregistered at the built-in reasoner", e);
		}
	}
	
	public boolean isSatisfiable() {
		return builtInFacade.isConsistent();
	}
	
	private Instance getWSMOInstance(OWLEntity theOWLEntity) throws OWLException{
		return wsmoFactory.getInstance(getWSMOIRI(theOWLEntity));
    }
	
	private Concept getWSMOConcept(OWLEntity theOWLEntity) throws OWLException{
		return wsmoFactory.getConcept(getWSMOIRI(theOWLEntity));
    }
	
	private IRI getWSMOIRI(OWLEntity theOWLEntity) throws OWLException{
		if (ns == null || !theOWLEntity.getURI().toString().startsWith(ns)) {
			return wsmoFactory.createIRI(theOWLEntity.getURI().toString());
		} 
		return wsmoFactory.createIRI(ns + theOWLEntity.getURI().getFragment());
    }
	
	public Set<Concept> getAllConcepts() {
		Set<Concept> elements = new HashSet<Concept>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allClasses();
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				elements.add(getWSMOConcept(entity));
				
			} 
			catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public Set<Instance> getAllInstances() {
		Set<Instance> elements = new HashSet<Instance>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allIndividuals();
		} 
		catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				elements.add(getWSMOInstance(entity));
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}

	public Set<IRI> getAllAttributes() {
		Set<IRI> elements = new HashSet<IRI>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allProperties();
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				elements.add(getWSMOIRI(entity));
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}

	public Set<IRI> getAllConstraintAttributes() {
		Set<IRI> elements = new HashSet<IRI>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allDataProperties();
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				elements.add(getWSMOIRI(entity));
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public Set<IRI> getAllInferenceAttributes() {
		Set<IRI> elements = new HashSet<IRI>();
		Set<OWLEntity> set;
		try {
			set = builtInFacade.allObjectProperties();
		} catch (OWLException e) {
			throw new InternalReasonerException(e);
		} catch (URISyntaxException e) {
			throw new InternalReasonerException(e);
		}
		for (OWLEntity entity : set) {
			try {
				elements.add(getWSMOIRI(entity));
			} catch (OWLException e) {
				throw new InternalReasonerException(e);
			}
		}
		return elements;
	}
	
	public Set<Concept> getSubConcepts(Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.descendantClassesOf(
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					elements.add(getWSMOConcept(entity));
				}
			}
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		} 
		return elements;
	}

	public Set<Concept> getDirectSubConcepts(Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.subClassesOf( 
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					elements.add(getWSMOConcept(entity));
				}
			}
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		} 
		return elements;
	}
	
	public Set<Concept> getSuperConcepts(Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set<OWLEntity>> set = builtInFacade.ancestorClassesOf(
					owlDataFactory.getOWLClass(
							new URI(concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					elements.add(getWSMOConcept(entity));
				}
			}
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<Concept> getDirectSuperConcepts(Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.superClassesOf(
					owlDataFactory.getOWLClass(
							new URI(concept.getIdentifier().toString())));
			for (Set<OWLEntity> set2 : set) {	
				for (OWLEntity entity : set2) {
					elements.add(getWSMOConcept(entity));
				}
			}
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<Concept> getEquivalentConcepts(Concept concept) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<OWLEntity> set = builtInFacade.equivalentClassesOf(
					owlDataFactory.getOWLClass(
							new URI(concept.getIdentifier().toString())));
			for (OWLEntity entity : set) {
				elements.add(getWSMOConcept(entity));
			}
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public boolean isEquivalentConcept(Concept concept1, 
			Concept concept2) {
		try {
			return builtInFacade.isEquivalentClass(
					owlDataFactory.getOWLClass(new URI(
							concept1.getIdentifier().toString())), 
							owlDataFactory.getOWLClass(new URI(
									concept2.getIdentifier().toString())));
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public boolean isSubConceptOf(Concept subConcept, 
			Concept superConcept) {
		try {
			return builtInFacade.isSubClassOf( 
					owlDataFactory.getOWLClass(new URI(
							subConcept.getIdentifier().toString())), 
					owlDataFactory.getOWLClass(new URI(
							superConcept.getIdentifier().toString())));
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
	}

	public boolean isMemberOf(Instance instance, 
			Concept concept) {
		try {
			return builtInFacade.isInstanceOf(
					owlDataFactory.getOWLIndividual(new URI(
							instance.getIdentifier().toString())), 
					owlDataFactory.getOWLClass(new URI(
							concept.getIdentifier().toString())));
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
	}
	
	public Set<Instance> getInstances(Concept concept) {
		Set<Instance> elements = new HashSet<Instance>();
		try {
			Set<OWLEntity> set = builtInFacade.allInstancesOf(
					owlDataFactory.getOWLClass(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}	
		return elements;
	}
	
	public Set<Concept> getDirectConcepts(Instance instance) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.typesOf(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}	
		return elements;
	}
	
	public Set<Concept> getConcepts(Instance instance) {
		Set<Concept> elements = new HashSet<Concept>();
		try {
			Set<Set> set = builtInFacade.allTypesOf(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}	
		return elements;
	}

	public Set<IRI> getSubRelations(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set> set = builtInFacade.descendantPropertiesOf(
					owlDataFactory.getOWLObjectProperty(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getDirectSubRelations(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set> set = builtInFacade.subPropertiesOf(
					owlDataFactory.getOWLObjectProperty(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getSuperRelations(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set> set = builtInFacade.ancestorPropertiesOf(
					owlDataFactory.getOWLObjectProperty(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getDirectSuperRelations(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<Set<OWLEntity>> set = builtInFacade.superPropertiesOf(
					owlDataFactory.getOWLObjectProperty(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getEquivalentRelations( 
			Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLEntity> set = builtInFacade.equivalentPropertiesOf(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getInverseRelations(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLEntity> set = builtInFacade.inversePropertiesOf(
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
		} catch (Exception e) {
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
				if (ns == null || !entity.getURI().toString().startsWith(ns)) {
					elements.add(wsmoFactory.getConcept(wsmoFactory.createIRI(
							entity.getURI().toString())));
				} 
				else {
					elements.add(wsmoFactory.getConcept(wsmoFactory.createIRI(
							ns + entity.getURI().getFragment())));
				}
			}
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getRangesOfInferingAttribute(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLEntity> set = builtInFacade.rangesOf(owlDataFactory.getOWLObjectProperty(new URI(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Set<IRI> getRangesOfConstraintAttribute(Identifier attributeId) {
		Set<IRI> elements = new HashSet<IRI>();
		try {
			Set<OWLConcreteDataTypeImpl> set = builtInFacade.rangesOf(
					owlDataFactory.getOWLDataProperty(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<IRI, Set<Term>> getConstraintAttributeValues(Instance instance) {
		Map<IRI, Set<Term>> elements = new HashMap<IRI, Set<Term>>();
		try {
			Set<Entry<OWLEntity, Set<OWLConcreteDataImpl>>> entrySet = 
				builtInFacade.getDataPropertyValues(owlDataFactory.getOWLIndividual(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<IRI, Set<Term>> getInferingAttributeValues(Instance instance) {
		Map<IRI, Set<Term>> elements = new HashMap<IRI, Set<Term>>();
		try {
			Set<Entry<OWLEntity, Set<OWLEntity>>> entrySet = 
				builtInFacade.getObjectPropertyValues(owlDataFactory.getOWLIndividual(new URI(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<Instance, Set<Term>> getInferingAttributeInstances(
			Identifier attributeId) {
		Map<Instance, Set<Term>> elements = new HashMap<Instance, Set<Term>>();
		try {
			Set<Entry<OWLEntity, Set<OWLEntity>>> entrySet = 
				builtInFacade.getPropertyValues(owlDataFactory.getOWLObjectProperty(new URI(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	public Map<Instance, Set<Term>> getConstraintAttributeInstances(
			Identifier attributeId) {
		Map<Instance, Set<Term>> elements = new HashMap<Instance, Set<Term>>();
		try {
			Set<Entry<OWLEntity, Set<OWLConcreteDataImpl>>> entrySet = 
				builtInFacade.getPropertyValues(owlDataFactory.getOWLDataProperty(new URI(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	
	public Set<Object> getInferingAttributeValues(Instance subject, Identifier attributeId) {
		Set<Object> elements = new HashSet<Object>();
		try {
			Set<OWLEntity> set = builtInFacade.getObjectPropertyValues(
					owlDataFactory.getOWLIndividual(
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
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return elements;
	}
	
	/**
	 * @return data value of the given instance and attribute
	 */
	
	public Set<Object> getConstraintAttributeValues(Instance subject, Identifier attributeId) {
		Set<Object> elements = new HashSet<Object>();
		try {
			Set<OWLDataValue> set = builtInFacade.getDataPropertyValues(
					owlDataFactory.getOWLIndividual(
							new URI(subject.getIdentifier().toString())),
							owlDataFactory.getOWLDataProperty(
									new URI(attributeId.toString())));
			for (OWLDataValue dataValue : set)
				elements.add(dataValue.getValue().toString());
		} catch (Exception e) {
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
	
	public Set<Map<Variable, Term>> executeQuery(LogicalExpression query) {
		throw new UnsupportedOperationException();
	}
	
	public boolean checkQueryContainment(LogicalExpression query1,
			LogicalExpression query2) {
		throw new UnsupportedOperationException("This method is not implemented");
	}
	
	public Set<Map<Variable, Term>> getQueryContainment(LogicalExpression query1,
			LogicalExpression query2) {
		throw new UnsupportedOperationException("This method is not implemented");
	}

	/**
	 * This method checks whether a given query (without variables) is true or 
	 * false. E.g. check whether Mary[hasChild hasValue Jack] is true or not.
	 * 
	 * @return true or false
	 */
	public boolean executeGroundQuery(LogicalExpression query) {
		if (query.toString().contains("?")) {
			throw new InternalReasonerException("A ground query may not contain " +
					"variables!");
		}
		if (query instanceof AttributeValueMolecule) {
			AttributeValueMolecule attr = (AttributeValueMolecule) query;
			if (attr.getRightParameter() instanceof DataValue) {
				return instanceHasConstraintAttributeValue(
						wsmoFactory.createInstance((IRI) attr.getLeftParameter()), 
						(Identifier) attr.getAttribute(), 
						(DataValue) attr.getRightParameter());
			}
			else {
				return instanceHasInferingAttributeValue(
						wsmoFactory.createInstance((IRI) attr.getLeftParameter()), 
						(Identifier) attr.getAttribute(), 
						wsmoFactory.createInstance((IRI) attr.getRightParameter()));
			}
		}
		else if (query instanceof MembershipMolecule) {
			MembershipMolecule attr = (MembershipMolecule) query;
			return isMemberOf(wsmoFactory.createInstance(
					(IRI) attr.getLeftParameter()), wsmoFactory.createConcept(
							(IRI) attr.getRightParameter()));
		}
		else if (query instanceof SubConceptMolecule) {
			SubConceptMolecule attr = (SubConceptMolecule) query;
			return isSubConceptOf(wsmoFactory.createConcept(
					(IRI) attr.getLeftParameter()), wsmoFactory.createConcept(
							(IRI) attr.getRightParameter()));
		}
		else {
			return entails(query);
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
	public boolean entails(LogicalExpression expression) {
		OWLDescription des = transformLogicalExpression(expression);
		try {
			if (des != null) 
				return builtInFacade.isConsistent(des);
			else 
				throw new InternalReasonerException("This logical expression" +
						" is not supported for consistency check!");
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		} 
	}

	public boolean entails(
			Set<LogicalExpression> expressions) {
		for (LogicalExpression e : expressions) {
			if (!entails(e))
                return false;
        }
        return true;
	}

	public Set<ConsistencyViolation> checkConsistency() {
		throw new UnsupportedOperationException();
	}
	
	/*
	 * @return true if the given subject instance has an attribute value of the 
	 * 			given infering attribute with the given object instance as value
	 */
	private boolean instanceHasInferingAttributeValue(Instance subject, Identifier attributeId, Instance object) {		
		try {
			return builtInFacade.hasPropertyValue(
					owlDataFactory.getOWLIndividual(new URI(
							subject.getIdentifier().toString())), 
					owlDataFactory.getOWLObjectProperty(new URI(
							attributeId.toString())),
					owlDataFactory.getOWLIndividual(new URI(
							object.getIdentifier().toString())));
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
	}
	
	/*
	 * @return true if the given subject instance has an attribute value of the 
	 * 			given constraint attribute with the given object data value as value
	 */
	private boolean instanceHasConstraintAttributeValue(
					Instance subject, Identifier attributeId, DataValue object) {
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
		} catch (Exception e) {
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

	public Set<ConsistencyViolation> checkConsistency(IRI ontologyID) {
		return checkConsistency();
	}

	public boolean checkQueryContainment(LogicalExpression query1,
			LogicalExpression query2, IRI ontologyID) {
		return checkQueryContainment(query1, query2);
	}

	public void deRegisterOntology(IRI ontologyID) {
		deRegister();
	}

	public void deRegisterOntology(Set<IRI> ontologyIDs) {
		deRegister();
	}

	public boolean entails(IRI ontologyID, LogicalExpression expression) {
		return entails(expression);
	}

	public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions) {
		return entails(expressions);
	}

	public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query) {
		return executeGroundQuery(query);
	}

	public Set<Map<Variable, Term>> executeQuery(IRI ontologyID,
			LogicalExpression query) {
		return executeQuery(query);
	}

	public Set<IRI> getAllAttributes(IRI ontologyID) {
		return getAllAttributes();
	}

	public Set<Concept> getAllConcepts(IRI ontologyID) {
		return getAllConcepts();
	}

	public Set<IRI> getAllConstraintAttributes(IRI ontologyID) {
		return getAllConstraintAttributes();
	}

	public Set<IRI> getAllInferenceAttributes(IRI ontologyID) {
		return getAllInferenceAttributes();
	}

	public Set<Instance> getAllInstances(IRI ontologyID) {
		return getAllInstances();
	}

	public Set<Concept> getConcepts(IRI ontologyID, Instance instance) {
		return getConcepts(instance);
	}

	public Set<Concept> getConceptsOf(IRI ontologyID, Identifier attributeId) {
		return getConceptsOf(attributeId);
	}

	public Map<Instance, Set<Term>> getConstraintAttributeInstances(
			IRI ontologyID, Identifier attributeId) {
		return getConstraintAttributeInstances(attributeId);
	}

	public Set getConstraintAttributeValues(IRI ontologyID, Instance subject,
			Identifier attributeId) {
		return getConstraintAttributeValues(subject, attributeId);
	}

	public Map<IRI, Set<Term>> getConstraintAttributeValues(IRI ontologyID,
			Instance instance) {
		return getConstraintAttributeValues(instance);
	}

	public Set<Concept> getDirectConcepts(IRI ontologyID, Instance instance) {
		return getDirectConcepts(instance);
	}

	public Set<Concept> getDirectSubConcepts(IRI ontologyID, Concept concept) {
		return getDirectSubConcepts(concept);
	}

	public Set<IRI> getDirectSubRelations(IRI ontologyID, Identifier attributeId) {
		return getDirectSubRelations(attributeId);
	}

	public Set<Concept> getDirectSuperConcepts(IRI ontologyID, Concept concept) {
		return getDirectSuperConcepts(concept);
	}

	public Set<IRI> getDirectSuperRelations(IRI ontologyID,
			Identifier attributeId) {
		return getDirectSuperRelations(attributeId);
	}

	public Set<Concept> getEquivalentConcepts(IRI ontologyID, Concept concept) {
		return getEquivalentConcepts(concept);
	}

	public Set<IRI> getEquivalentRelations(IRI ontologyID,
			Identifier attributeId) {
		return getEquivalentRelations(attributeId);
	}

	public Map<Instance, Set<Term>> getInferingAttributeInstances(
			IRI ontologyID, Identifier attributeId) {
		return getInferingAttributeInstances(attributeId);
	}

	public Set getInferingAttributeValues(IRI ontologyID, Instance subject,
			Identifier attributeId) {
		return getInferingAttributeValues(subject, attributeId);
	}

	public Map<IRI, Set<Term>> getInferingAttributeValues(IRI ontologyID,
			Instance instance) {
		return getInferingAttributeValues(instance);
	}

	public Set<Instance> getInstances(IRI ontologyID, Concept concept) {
		return getInstances(concept);
	}

	public Set<IRI> getInverseRelations(IRI ontologyID, Identifier attributeId) {
		return getInverseRelations(attributeId);
	}

	public Set<Map<Variable, Term>> getQueryContainment(
			LogicalExpression query1, LogicalExpression query2, IRI ontologyID) {
		return getQueryContainment(query1, query2);
	}

	public Set<IRI> getRangesOfConstraintAttribute(IRI ontologyID,
			Identifier attributeId) {
		return getRangesOfConstraintAttribute(attributeId);
	}

	public Set<IRI> getRangesOfInferingAttribute(IRI ontologyID,
			Identifier attributeId) {
		return getRangesOfInferingAttribute(attributeId);
	}

	public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept) {
		return getSubConcepts(concept);
	}

	public Set<IRI> getSubRelations(IRI ontologyID, Identifier attributeId) {
		return getSubRelations(attributeId);
	}

	public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept) {
		return getSubConcepts(concept);
	}

	public Set<IRI> getSuperRelations(IRI ontologyID, Identifier attributeId) {
		return getSuperRelations(attributeId);
	}

	public boolean isEquivalentConcept(IRI ontologyID, Concept concept1,
			Concept concept2) {
		return isEquivalentConcept(concept1, concept2);
	}

	public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept) {
		return isMemberOf(instance, concept);
	}

	public boolean isSatisfiable(IRI ontologyID) {
		return isSatisfiable();
	}

	public boolean isSubConceptOf(IRI ontologyID, Concept subConcept,
			Concept superConcept) {
		return isSubConceptOf(subConcept, superConcept);
	}
	
	
}
