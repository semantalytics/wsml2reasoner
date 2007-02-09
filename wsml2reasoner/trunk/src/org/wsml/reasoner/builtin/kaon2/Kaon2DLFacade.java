/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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
package org.wsml.reasoner.builtin.kaon2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.owl.axioms.ClassMember;
import org.semanticweb.kaon2.api.owl.axioms.DataPropertyMember;
import org.semanticweb.kaon2.api.owl.axioms.DataPropertyRange;
import org.semanticweb.kaon2.api.owl.axioms.EquivalentClasses;
import org.semanticweb.kaon2.api.owl.axioms.EquivalentDataProperties;
import org.semanticweb.kaon2.api.owl.axioms.EquivalentObjectProperties;
import org.semanticweb.kaon2.api.owl.axioms.InverseObjectProperties;
import org.semanticweb.kaon2.api.owl.axioms.ObjectPropertyDomain;
import org.semanticweb.kaon2.api.owl.axioms.ObjectPropertyMember;
import org.semanticweb.kaon2.api.owl.axioms.ObjectPropertyRange;
import org.semanticweb.kaon2.api.owl.axioms.SubClassOf;
import org.semanticweb.kaon2.api.owl.axioms.SubDataPropertyOf;
import org.semanticweb.kaon2.api.owl.axioms.SubObjectPropertyOf;
import org.semanticweb.kaon2.api.owl.elements.DataProperty;
import org.semanticweb.kaon2.api.owl.elements.Description;
import org.semanticweb.kaon2.api.owl.elements.Individual;
import org.semanticweb.kaon2.api.owl.elements.OWLClass;
import org.semanticweb.kaon2.api.owl.elements.ObjectProperty;
import org.semanticweb.kaon2.api.reasoner.Query;
import org.semanticweb.kaon2.api.reasoner.Reasoner;
import org.semanticweb.owl.impl.model.OWLConcreteDataImpl;
import org.semanticweb.owl.impl.model.OWLConcreteDataTypeImpl;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLManager;
import org.wsml.reasoner.DLReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.serializer.owl.OWLSerializerImpl;

/**
 * Integrates the KAON2 reasoner system into the WSML-DL reasoner framework.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: hlausen $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/builtin/kaon2/Kaon2DLFacade.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck;
 * 		   Holger Lausen, DERI Innsbruck
 * @version $Revision: 1.4 $ $Date: 2007-02-09 08:40:53 $
 */
public class Kaon2DLFacade implements DLReasonerFacade {

    private Reasoner reasoner = null;
    
    private Logger log = Logger.getLogger(Kaon2DLFacade.class);

    private Ontology ontology = null;
    
    private Map<String, Reasoner> registeredOntologies = null;
    
    private OWLDataFactory owlDataFactory = null;

	/**
     * Creates a facade object that allows to invoke the KAON2 system for
     * performing reasoning tasks.
	 * @throws OWLException 
     */
    public Kaon2DLFacade() throws OWLException {
        super();
        Map<Object, String> parameters = new HashMap<Object, String>();
		parameters.put(OWLManager.OWL_CONNECTION, 
				"org.semanticweb.owl.impl.model.OWLConnectionImpl");
        owlDataFactory = OWLManager.getOWLConnection(parameters).getDataFactory();
        registeredOntologies = new HashMap<String, Reasoner>();
    }

    /**
     * Registers the OWL ontology at the KAON2 reasoner.
     */
	public void register(OWLOntology owlOntology)
			throws ExternalToolException {
        StringBuffer buf = new StringBuffer();
        try {
            new OWLSerializerImpl().serialize(owlOntology, buf);
        } catch (RendererException e) {
            log.error(e);
            throw new ExternalToolException("could not serialize ontology as owl",e);
        }
        InputStream in = new ByteArrayInputStream(buf.toString().getBytes());
//System.out.println(buf);

        KAON2Connection connection = KAON2Manager.newConnection();
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(KAON2Connection.LOAD_FROM_INPUT_STREAM, in);
        try {
        	ontology = connection.openOntology(owlOntology.getURI().toString(), m);    	
			Reasoner reasoner = ontology.createReasoner();
			registeredOntologies.put(owlOntology.getURI().toString(),
					reasoner);
        } catch (Exception e) {
            log.error(e);
            throw new ExternalToolException("could not register ontology with KAON",e);
        }
	}

	/**
	 * The Knowledge base, that KAON2 derived from this OWL ontology is cleared.
	 * @throws ExternalToolException
	 */
	public void deRegister(String ontologyURI)
			throws ExternalToolException {
		reasoner = getReasoner(ontologyURI);
		reasoner.dispose();
		registeredOntologies.remove(ontologyURI);
	}

	public boolean isConsistent(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		try {
            return reasoner.isSatisfiable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	@SuppressWarnings("unchecked")
	public boolean isConsistent(String ontologyURI, OWLDescription description)
			throws OWLException, InterruptedException {
        try {
		reasoner = getReasoner(ontologyURI);
//System.out.println(description.getClass().getName());
		Description des = null;
		if (description.getClass().getName().toString().equals("org.semanticweb.owl.impl.model.OWLClassImpl")) {
                des = KAON2Manager.factory().description(description.toString().
                		substring(description.toString().indexOf("]")+2), Namespaces.INSTANCE);
		}
		else if (description.getClass().getName().toString().equals("org.semanticweb.owl.impl.model.OWLAndImpl")){
			Iterator<OWLDescription> it = ((OWLAnd) description).getOperands().iterator();
			Vector<OWLDescription> list = new Vector<OWLDescription>();
			while (it.hasNext()) {
				list.add(it.next());
			}
			Description arg1 = KAON2Manager.factory().description(list.elementAt(0).toString()
					.substring(list.elementAt(0).toString().indexOf("]")+2), Namespaces.INSTANCE);
			Description arg2 = KAON2Manager.factory().description(list.elementAt(1).toString()
					.substring(list.elementAt(1).toString().indexOf("]")+2), Namespaces.INSTANCE);
			des = KAON2Manager.factory().objectAnd(arg1, arg2);
		}
		return reasoner.isSatisfiable(des);
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allClasses(String ontologyURI) 
			throws  OWLException, URISyntaxException {
        try{
		reasoner = getReasoner(ontologyURI);
		
		Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
        Request<OWLClass> entityRequest = ontology.createEntityRequest(OWLClass.class);
        Set<OWLClass> classAxioms = entityRequest.get();
        for (OWLClass axiom : classAxioms) {
        	OWLEntity entity = owlDataFactory.getOWLClass(new URI(axiom.toString()));
        	resultSet.add(entity);
        }
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allIndividuals(String ontologyURI) throws  OWLException, URISyntaxException {
        try{
		reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
        Request<Individual> entityRequest = ontology.createEntityRequest(Individual.class);
        Set<Individual> individualsAxioms = entityRequest.get();
        for (Individual axiom : individualsAxioms) {
        	OWLEntity entity = owlDataFactory.getOWLIndividual(new URI(axiom.toString()));
        	resultSet.add(entity);
        }
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allProperties(String ontologyURI) 
			throws  OWLException, URISyntaxException {
		reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
        resultSet = allDataProperties(ontologyURI);
        resultSet.addAll(allObjectProperties(ontologyURI));
		return resultSet;
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allDataProperties(String ontologyURI) 
			throws  OWLException, URISyntaxException {
        try{
        reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
        Request<DataProperty> entityRequest = ontology.createEntityRequest(DataProperty.class);
        Set<DataProperty> dataPropertyAxioms = entityRequest.get();
        for (DataProperty axiom : dataPropertyAxioms) {
        	OWLEntity entity = owlDataFactory.getOWLDataProperty(new URI(axiom.toString()));
        	resultSet.add(entity);
        }
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allObjectProperties(String ontologyURI) 
			throws  OWLException, URISyntaxException {
		
        try{
        reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
        Request<ObjectProperty> entityRequest = ontology.createEntityRequest(ObjectProperty.class);
        Set<ObjectProperty> objectPropertyAxioms = entityRequest.get();
        for (ObjectProperty axiom : objectPropertyAxioms) {
        	OWLEntity entity = owlDataFactory.getOWLObjectProperty(new URI(axiom.toString()));
        	resultSet.add(entity);
        }
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<Set> descendantClassesOf(String ontologyURI, OWLDescription clazz) 
			throws OWLException,  URISyntaxException {
        try{
            reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		
		Request<SubClassOf> subClassOfRequest = ontology.createAxiomRequest(SubClassOf.class);
		OWLClass owlClass = KAON2Manager.factory().owlClass(clazz.toString().substring(
				clazz.toString().indexOf("]")+2));
        subClassOfRequest.setCondition("superDescription", owlClass);
		Set<SubClassOf> subClassOfAxioms = subClassOfRequest.get();
		for (SubClassOf axiom : subClassOfAxioms) {
			if (axiom.getSubDescription() instanceof OWLClass) {
				OWLEntity entity = owlDataFactory.getOWLClass(
						new URI(axiom.getSubDescription().toString()));
				entitySet.add(entity);
				addSubConcepts(ontologyURI, entity, entitySet);
			}
		}
		if (entitySet.contains(clazz)) {
			entitySet.remove(clazz);
		}
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}
	
	@SuppressWarnings("unchecked")
	public Set<Set> subClassesOf(String ontologyURI, OWLDescription clazz) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();		
		Request<SubClassOf> subClassOfRequest = ontology.createAxiomRequest(SubClassOf.class);
		OWLClass owlClass = KAON2Manager.factory().owlClass(clazz.toString().substring(
				clazz.toString().indexOf("]")+2));
        subClassOfRequest.setCondition("superDescription", owlClass);
		Set<SubClassOf> subClassOfAxioms = subClassOfRequest.get();
		for (SubClassOf axiom : subClassOfAxioms) {
			if (axiom.getSubDescription() instanceof OWLClass) {
				OWLEntity entity = owlDataFactory.getOWLClass(
						new URI(axiom.getSubDescription().toString()));
				entitySet.add(entity);
			}
		}
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<Set> ancestorClassesOf(String ontologyURI, OWLDescription clazz) 
			throws OWLException,  URISyntaxException {
        try{

            reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		
		Request<SubClassOf> subClassOfRequest = ontology.createAxiomRequest(SubClassOf.class);
		OWLClass owlClass = KAON2Manager.factory().owlClass(clazz.toString().substring(
				clazz.toString().indexOf("]")+2));
        subClassOfRequest.setCondition("subDescription", owlClass);
		Set<SubClassOf> subClassOfAxioms = subClassOfRequest.get();
		OWLEntity entity = null;
		for (SubClassOf axiom : subClassOfAxioms) {
			if (axiom.getSuperDescription() instanceof OWLClass) {
				entity = owlDataFactory.getOWLClass(
						new URI(axiom.getSuperDescription().toString()));
				entitySet.add(entity);
				addSuperConcepts(ontologyURI, entity, entitySet);
			}
		}
		if (entitySet.contains(clazz)) {
			entitySet.remove(clazz);
		}
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<Set> superClassesOf(String ontologyURI, OWLDescription clazz) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		
		Request<SubClassOf> subClassOfRequest = ontology.createAxiomRequest(SubClassOf.class);
		OWLClass owlClass = KAON2Manager.factory().owlClass(clazz.toString().substring(
				clazz.toString().indexOf("]")+2));
        subClassOfRequest.setCondition("subDescription", owlClass);
		Set<SubClassOf> subClassOfAxioms = subClassOfRequest.get();
		for (SubClassOf axiom : subClassOfAxioms) {
			if (axiom.getSuperDescription() instanceof OWLClass) {
				OWLEntity entity = owlDataFactory.getOWLClass(
						new URI(axiom.getSuperDescription().toString()));
				entitySet.add(entity);
			}
		}
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> equivalentClassesOf(String ontologyURI, OWLDescription clazz) 
			throws OWLException,  URISyntaxException {
        try{
            reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> resultSet = new HashSet<OWLEntity>();	
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		Request<EquivalentClasses> equivClassesRequest = ontology.createAxiomRequest(EquivalentClasses.class);
		Set<EquivalentClasses> equivClassesAxiom = equivClassesRequest.get();
		for (EquivalentClasses axiom : equivClassesAxiom) {
			Set<Description> descriptionSet = axiom.getDescriptions();
			Iterator<Description> it = descriptionSet.iterator();
			while (it.hasNext()) {
				OWLEntity entity = owlDataFactory.getOWLClass(
						new URI(it.next().toString()));
				entitySet.add(entity);
			}
			if (entitySet.contains(clazz)) {
				Iterator<OWLEntity> it2 = entitySet.iterator();
				while (it2.hasNext()) {
					OWLEntity next = it2.next();
					if (! next.equals(clazz))
						resultSet.add(next);
				}
			}
			entitySet.clear();
		}
		
		Set<Set> set = subClassesOf(ontologyURI, clazz);
		for (Set<OWLEntity> set2 : set) {	
			for (OWLEntity entity : set2) {
				Set<Set> set3 = subClassesOf(ontologyURI, 
						owlDataFactory.getOWLClass(new URI(entity.toString().substring(
								entity.toString().indexOf("]")+2))));
				for (Set<OWLEntity> set4 : set3) {
					if (set4.contains(clazz)) {
						resultSet.add(entity);
					}
				}
			}
		}
		
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	public boolean isEquivalentClass(String ontologyURI, OWLDescription clazz1,
			OWLDescription clazz2) 
			throws OWLException,  URISyntaxException {
		reasoner = getReasoner(ontologyURI);
        return equivalentClassesOf(ontologyURI, clazz1).contains(clazz2);
	}

	public boolean isSubClassOf(String ontologyURI, OWLDescription clazz1,
			OWLDescription clazz2) throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		Request<SubClassOf> subClassOfRequest = ontology.createAxiomRequest(SubClassOf.class);
		OWLClass owlClass1 = KAON2Manager.factory().owlClass(clazz1.toString().substring(
				clazz1.toString().indexOf("]")+2));
        subClassOfRequest.setCondition("subDescription", owlClass1);
        OWLClass owlClass2 = KAON2Manager.factory().owlClass(clazz2.toString().substring(
				clazz2.toString().indexOf("]")+2));
        subClassOfRequest.setCondition("superDescription", owlClass2);
		Set<SubClassOf> subClassOfAxioms = subClassOfRequest.get();
		for (SubClassOf axiom : subClassOfAxioms) {
			if (axiom.getSubDescription() instanceof OWLClass) {
				OWLEntity entity = owlDataFactory.getOWLClass(
						new URI(axiom.getSubDescription().toString()));
				entitySet.add(entity);
			}
		}
		if (entitySet.size() > 0) {
			return true;
		}
		else {
			return false;
		}
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	public boolean isInstanceOf(String ontologyURI, OWLIndividual individual,
			OWLDescription clazz) throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();	
		Request<ClassMember> memberOfRequest = ontology.createAxiomRequest(ClassMember.class);
		OWLClass owlClass = KAON2Manager.factory().owlClass(clazz.toString().substring(
				clazz.toString().indexOf("]")+2));
		memberOfRequest.setCondition("description", owlClass);
		Individual owlIndividual = KAON2Manager.factory().individual(
				individual.toString().substring(individual.toString().indexOf("]")+2));
		memberOfRequest.setCondition("individual", owlIndividual);
		Set<ClassMember> memberOfAxioms = memberOfRequest.get();
		for (ClassMember axiom : memberOfAxioms) {
			OWLEntity entity = owlDataFactory.getOWLClass(
					new URI(axiom.getIndividual().toString()));
			entitySet.add(entity);
		}
		if (entitySet.size() > 0) {
			return true;
		}
		else {
			return false;
		}
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allInstancesOf(String ontologyURI, org.semanticweb.owl.model.OWLClass clazz)
			throws OWLException,  URISyntaxException {
        try{
        reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();	
		Request<ClassMember> memberOfRequest = ontology.createAxiomRequest(ClassMember.class);
		OWLClass owlClass = KAON2Manager.factory().owlClass(clazz.toString().substring(
				clazz.toString().indexOf("]")+2));
		memberOfRequest.setCondition("description", owlClass);
		Set<ClassMember> memberOfAxioms = memberOfRequest.get();
		for (ClassMember axiom : memberOfAxioms) {
			OWLEntity entity = owlDataFactory.getOWLClass(
					new URI(axiom.getIndividual().toString()));
			entitySet.add(entity);
		}
		return entitySet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<Set> typesOf(String ontologyURI, OWLIndividual individual)
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();	
		Request<ClassMember> memberOfRequest = ontology.createAxiomRequest(ClassMember.class);
		Individual owlIndividual = KAON2Manager.factory().individual(
				individual.toString().substring(individual.toString().indexOf("]")+2));
		memberOfRequest.setCondition("individual", owlIndividual);
		Set<ClassMember> memberOfAxioms = memberOfRequest.get();
		for (ClassMember axiom : memberOfAxioms) {
			OWLEntity entity = owlDataFactory.getOWLClass(
					new URI(axiom.getDescription().toString()));
			entitySet.add(entity);
		}
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<Set> allTypesOf(String ontologyURI, OWLIndividual individual)
			throws OWLException,  URISyntaxException {
        try{
            reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();	
		Request<ClassMember> memberOfRequest = ontology.createAxiomRequest(ClassMember.class);
		Individual owlIndividual = KAON2Manager.factory().individual(
				individual.toString().substring(individual.toString().indexOf("]")+2));
		memberOfRequest.setCondition("individual", owlIndividual);
		Set<ClassMember> memberOfAxioms = memberOfRequest.get();
		for (ClassMember axiom : memberOfAxioms) {
			OWLEntity entity = owlDataFactory.getOWLClass(
					new URI(axiom.getDescription().toString()));
			entitySet.add(entity);
			addSuperConcepts(ontologyURI, entity, entitySet);
		}
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<Set> descendantPropertiesOf(String ontologyURI,
			OWLProperty property) 
			throws OWLException,  URISyntaxException {
        try{
            reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		
		Request<SubObjectPropertyOf> subObjectPropertyOfRequest = 
			ontology.createAxiomRequest(SubObjectPropertyOf.class);
		ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
				property.toString().substring(property.toString().indexOf("]")+2));
//		subObjectPropertyOfRequest.setCondition("superObjectProperty", owlObjectProperty);
		Set<SubObjectPropertyOf> subObjectPropertyOfAxioms = subObjectPropertyOfRequest.get();
		for (SubObjectPropertyOf axiom : subObjectPropertyOfAxioms) {
			if (axiom.getSuperObjectProperty().equals(owlObjectProperty) && 
					axiom.getSubObjectProperty() instanceof ObjectProperty) {
				OWLEntity entity = owlDataFactory.getOWLObjectProperty(
						new URI(axiom.getSubObjectProperty().toString()));
				entitySet.add(entity);
				addSubProperties(ontologyURI, entity, entitySet);
			}
		}
		Request<SubDataPropertyOf> subDataPropertyOfRequest = 
			ontology.createAxiomRequest(SubDataPropertyOf.class);
		DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
				property.toString().substring(property.toString().indexOf("]")+2));
//		subDataPropertyOfRequest.setCondition("superDataProperty", owlDataProperty);
		Set<SubDataPropertyOf> subDataPropertyOfAxioms = subDataPropertyOfRequest.get();
		for (SubDataPropertyOf axiom : subDataPropertyOfAxioms) {
			if (axiom.getSuperDataProperty().equals(owlDataProperty) && 
					axiom.getSubDataProperty() instanceof DataProperty) {
				OWLEntity entity = owlDataFactory.getOWLDataProperty(
						new URI(axiom.getSubDataProperty().toString()));
				entitySet.add(entity);
				addSubProperties(ontologyURI, entity, entitySet);
			}
		}
		if (entitySet.contains(property)) {
			entitySet.remove(property);
		}
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<Set> subPropertiesOf(String ontologyURI,
			OWLProperty property) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		
		Request<SubObjectPropertyOf> subObjectPropertyOfRequest = 
			ontology.createAxiomRequest(SubObjectPropertyOf.class);
		ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
				property.toString().substring(property.toString().indexOf("]")+2));
//		subObjectPropertyOfRequest.setCondition("superObjectProperty", owlObjectProperty);
		Set<SubObjectPropertyOf> subObjectPropertyOfAxioms = subObjectPropertyOfRequest.get();
		for (SubObjectPropertyOf axiom : subObjectPropertyOfAxioms) {
			if (axiom.getSuperObjectProperty().equals(owlObjectProperty) && 
					axiom.getSubObjectProperty() instanceof ObjectProperty) {
				OWLEntity entity = owlDataFactory.getOWLObjectProperty(
						new URI(axiom.getSubObjectProperty().toString()));
				entitySet.add(entity);
			}
		}
		Request<SubDataPropertyOf> subDataPropertyOfRequest = 
			ontology.createAxiomRequest(SubDataPropertyOf.class);
		DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
				property.toString().substring(property.toString().indexOf("]")+2));
//		subDataPropertyOfRequest.setCondition("superDataProperty", owlDataProperty);
		Set<SubDataPropertyOf> subDataPropertyOfAxioms = subDataPropertyOfRequest.get();
		for (SubDataPropertyOf axiom : subDataPropertyOfAxioms) {
			if (axiom.getSuperDataProperty().equals(owlDataProperty) && 
					axiom.getSubDataProperty() instanceof DataProperty) {
				OWLEntity entity = owlDataFactory.getOWLDataProperty(
						new URI(axiom.getSubDataProperty().toString()));
				entitySet.add(entity);
			}
		}
		
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<Set> ancestorPropertiesOf(String ontologyURI,
			OWLProperty property) 
			throws OWLException,  URISyntaxException {
        try{
            reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		
		Request<SubObjectPropertyOf> subObjectPropertyOfRequest = 
			ontology.createAxiomRequest(SubObjectPropertyOf.class);
		ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
				property.toString().substring(property.toString().indexOf("]")+2));
//		subObjectPropertyOfRequest.setCondition("subObjectProperty", owlObjectProperty);
		Set<SubObjectPropertyOf> subObjectPropertyOfAxioms = subObjectPropertyOfRequest.get();
		for (SubObjectPropertyOf axiom : subObjectPropertyOfAxioms) {
			if (axiom.getSubObjectProperty().equals(owlObjectProperty) && 
					axiom.getSuperObjectProperty() instanceof ObjectProperty) {
				OWLEntity entity = owlDataFactory.getOWLObjectProperty(
						new URI(axiom.getSuperObjectProperty().toString()));
				entitySet.add(entity);
				addSuperProperties(ontologyURI, entity, entitySet);
			}
		}
		Request<SubDataPropertyOf> subDataPropertyOfRequest = 
			ontology.createAxiomRequest(SubDataPropertyOf.class);
		DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
				property.toString().substring(property.toString().indexOf("]")+2));
//		subDataPropertyOfRequest.setCondition("subDataProperty", owlDataProperty);
		Set<SubDataPropertyOf> subDataPropertyOfAxioms = subDataPropertyOfRequest.get();
		for (SubDataPropertyOf axiom : subDataPropertyOfAxioms) {
			if (axiom.getSubDataProperty().equals(owlDataProperty) && 
					axiom.getSuperDataProperty() instanceof DataProperty) {
				OWLEntity entity = owlDataFactory.getOWLDataProperty(
						new URI(axiom.getSuperDataProperty().toString()));
				entitySet.add(entity);
				addSuperProperties(ontologyURI, entity, entitySet);
			}
		}
		if (entitySet.contains(property)) {
			entitySet.remove(property);
		}
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<Set> superPropertiesOf(String ontologyURI,
			OWLProperty property) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<Set> resultSet = new HashSet<Set>();
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		
		Request<SubObjectPropertyOf> subObjectPropertyOfRequest = 
			ontology.createAxiomRequest(SubObjectPropertyOf.class);
		ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
				property.toString().substring(property.toString().indexOf("]")+2));
//		subObjectPropertyOfRequest.setCondition("subObjectProperty", owlObjectProperty);
		Set<SubObjectPropertyOf> subObjectPropertyOfAxioms = subObjectPropertyOfRequest.get();
		for (SubObjectPropertyOf axiom : subObjectPropertyOfAxioms) {
			if (axiom.getSubObjectProperty().equals(owlObjectProperty) && 
					axiom.getSuperObjectProperty() instanceof ObjectProperty) {
				OWLEntity entity = owlDataFactory.getOWLObjectProperty(
						new URI(axiom.getSuperObjectProperty().toString()));
				entitySet.add(entity);
			}
		}
		Request<SubDataPropertyOf> subDataPropertyOfRequest = 
			ontology.createAxiomRequest(SubDataPropertyOf.class);
		DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
				property.toString().substring(property.toString().indexOf("]")+2));
//		subDataPropertyOfRequest.setCondition("subDataProperty", owlDataProperty);
		Set<SubDataPropertyOf> subDataPropertyOfAxioms = subDataPropertyOfRequest.get();
		for (SubDataPropertyOf axiom : subDataPropertyOfAxioms) {
			if (axiom.getSubDataProperty().equals(owlDataProperty) && 
					axiom.getSuperDataProperty() instanceof DataProperty) {
				OWLEntity entity = owlDataFactory.getOWLDataProperty(
						new URI(axiom.getSuperDataProperty().toString()));
				entitySet.add(entity);
			}
		}
		resultSet.add(entitySet);
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> equivalentPropertiesOf(String ontologyURI,
			OWLProperty property) throws OWLException,  URISyntaxException {
        try{
                reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> resultSet = new HashSet<OWLEntity>();	
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		Request<EquivalentObjectProperties> equivObjectPropertiesRequest = 
			ontology.createAxiomRequest(EquivalentObjectProperties.class);
		Set<EquivalentObjectProperties> equivObjectPropertiesAxiom = equivObjectPropertiesRequest.get();
		for (EquivalentObjectProperties axiom : equivObjectPropertiesAxiom) {
			Set<ObjectProperty> objectPropertySet = axiom.getObjectProperties();
			Iterator<ObjectProperty> it = objectPropertySet.iterator();
			while (it.hasNext()) {
				OWLEntity entity = owlDataFactory.getOWLObjectProperty(
						new URI(it.next().toString()));
				entitySet.add(entity);
			}
			if (entitySet.contains(property)) {
				Iterator<OWLEntity> it2 = entitySet.iterator();
				while (it2.hasNext()) {
					OWLEntity next = it2.next();
					if (! next.equals(property))
						resultSet.add(next);
				}
			}
			entitySet.clear();
		}
		Request<EquivalentDataProperties> equivDataPropertiesRequest = 
			ontology.createAxiomRequest(EquivalentDataProperties.class);
		Set<EquivalentDataProperties> equivDataPropertiesAxiom = equivDataPropertiesRequest.get();
		for (EquivalentDataProperties axiom : equivDataPropertiesAxiom) {
			Set<DataProperty> dataPropertySet = axiom.getDataProperties();
			Iterator<DataProperty> it = dataPropertySet.iterator();
			while (it.hasNext()) {
				OWLEntity entity = owlDataFactory.getOWLDataProperty(
						new URI(it.next().toString()));
				entitySet.add(entity);
			}
			if (entitySet.contains(property)) {
				Iterator<OWLEntity> it2 = entitySet.iterator();
				while (it2.hasNext()) {
					OWLEntity next = it2.next();
					if (! next.equals(property))
						resultSet.add(next);
				}
			}
			entitySet.clear();
		}
		
		Set<Set> set = descendantPropertiesOf(ontologyURI, property);
		for (Set<OWLEntity> set2 : set) {	
			for (OWLEntity entity : set2) {
				Set<Set> set3 = descendantPropertiesOf(ontologyURI, 
						owlDataFactory.getOWLObjectProperty(new URI(entity.toString().substring(
								entity.toString().indexOf("]")+2))));
				for (Set<OWLEntity> set4 : set3) {
					if (set4.contains(property)) {
						resultSet.add(entity);
					}
				}
			}
		}
		
		return resultSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> inversePropertiesOf(String ontologyURI,
			OWLObjectProperty property)	throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);	
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		Request<InverseObjectProperties> inverseObjectPropertiesRequest = 
			ontology.createAxiomRequest(InverseObjectProperties.class);
		ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
				property.toString().substring(property.toString().indexOf("]")+2));
		inverseObjectPropertiesRequest.setCondition("first", owlObjectProperty);
		Set<InverseObjectProperties> inverseObjectPropertiesAxiom = 
			inverseObjectPropertiesRequest.get();
		for (InverseObjectProperties axiom : inverseObjectPropertiesAxiom) {
			entitySet.add(owlDataFactory.getOWLObjectProperty(
					new URI(axiom.getSecond().toString())));
		}
		Request<InverseObjectProperties> inverseObjectPropertiesRequest2 = 
			ontology.createAxiomRequest(InverseObjectProperties.class);
		inverseObjectPropertiesRequest2.setCondition("second", owlObjectProperty);
		Set<InverseObjectProperties> inverseObjectPropertiesAxiom2 = 
			inverseObjectPropertiesRequest2.get();
		for (InverseObjectProperties axiom : inverseObjectPropertiesAxiom2) {
			entitySet.add(owlDataFactory.getOWLObjectProperty(
					new URI(axiom.getSecond().toString())));
		}
		return entitySet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> domainsOf(String ontologyURI, OWLProperty property)
			throws OWLException,  URISyntaxException {
        try{
            reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		Request<ObjectPropertyDomain> attributeRequest = 
			ontology.createAxiomRequest(ObjectPropertyDomain.class);
		ObjectProperty owlObjectProperty = KAON2Manager.factory().
				objectProperty(property.toString().substring(
						property.toString().indexOf("]")+2));
		attributeRequest.setCondition("objectProperty", owlObjectProperty);
		Set<ObjectPropertyDomain> objectPropertyDomainAxiom = attributeRequest.get();
		for (ObjectPropertyDomain axiom : objectPropertyDomainAxiom) {
			entitySet.add(owlDataFactory.getOWLClass(new URI(
					axiom.getDomain().toString())));
		}
		return entitySet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> rangesOf(String ontologyURI, OWLObjectProperty property) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		Request<ObjectPropertyRange> attributeRequest = 
			ontology.createAxiomRequest(ObjectPropertyRange.class);
		ObjectProperty owlObjectProperty = KAON2Manager.factory().
				objectProperty(property.toString().substring(
						property.toString().indexOf("]")+2));
		attributeRequest.setCondition("objectProperty", owlObjectProperty);
		Set<ObjectPropertyRange> objectPropertyRangeAxiom = attributeRequest.get();
		for (ObjectPropertyRange axiom : objectPropertyRangeAxiom) {
			entitySet.add(owlDataFactory.getOWLClass(new URI(
					axiom.getRange().toString())));
		}
		return entitySet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLConcreteDataTypeImpl> rangesOf(String ontologyURI, 
			OWLDataProperty property) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<OWLConcreteDataTypeImpl> dataTypeSet = 
			new HashSet<OWLConcreteDataTypeImpl>();
		Request<DataPropertyRange> attributeRequest = 
			ontology.createAxiomRequest(DataPropertyRange.class);
		DataProperty owlDataProperty = KAON2Manager.factory().
				dataProperty(property.toString().substring(
						property.toString().indexOf("]")+2));
		attributeRequest.setCondition("dataProperty", owlDataProperty);
		Set<DataPropertyRange> dataPropertyRangeAxiom = attributeRequest.get();
		for (DataPropertyRange axiom : dataPropertyRangeAxiom) {
			OWLDataType dataType = owlDataFactory.getOWLConcreteDataType(
					new URI(Namespaces.XSD_NS + axiom.getRange().toString().
							substring(axiom.getRange().toString().indexOf(":")+1)));
			dataTypeSet.add((OWLConcreteDataTypeImpl) dataType);
		}
		return dataTypeSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues(
			String ontologyURI, OWLIndividual individual) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
        Map<OWLEntity, Set<OWLEntity>> resultMap = 
        	new HashMap<OWLEntity, Set<OWLEntity>>();
		Individual owlIndividual = KAON2Manager.factory().individual(
				individual.toString().substring(
						individual.toString().indexOf("]")+2));
		Set<Entry<ObjectProperty, Set<Individual>>> entrySet = 
			owlIndividual.getObjectPropertyValues(ontology).entrySet();
		for (Entry<ObjectProperty, Set<Individual>> entry : entrySet) {
			ObjectProperty property = entry.getKey();
			Set<Individual> set = entry.getValue();
			for (Individual ind : set) {
				Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
				entitySet.add(owlDataFactory.getOWLIndividual(
						new URI(ind.toString())));
				resultMap.put(owlDataFactory.getOWLObjectProperty(
						new URI(property.toString())), entitySet);
			}
		}
		return resultMap;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}
	
	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues(
			String ontologyURI, OWLIndividual individual) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Map<OWLEntity, Set<OWLConcreteDataImpl>> resultMap = 
        	new HashMap<OWLEntity, Set<OWLConcreteDataImpl>>();
		Individual owlIndividual = KAON2Manager.factory().individual(
				individual.toString().substring(
						individual.toString().indexOf("]")+2));
		Set<Entry<DataProperty, Set<Object>>> entrySet = 
			owlIndividual.getDataPropertyValues(ontology).entrySet();
		for (Entry<DataProperty, Set<Object>> entry : entrySet) {
			DataProperty property = entry.getKey();
			Set<Object> set = entry.getValue();
			for (Object obj : set) {
				Set<OWLConcreteDataImpl> dataTypeSet = new HashSet<OWLConcreteDataImpl>();
				OWLDataValue dataValue = null;
				if (obj instanceof String) {
					dataValue = owlDataFactory.getOWLConcreteData(
							new URI(Namespaces.XSD_NS + "string"), 
							null, obj);
				}
				else if (obj instanceof Integer) {
					dataValue = owlDataFactory.getOWLConcreteData(
							new URI(Namespaces.XSD_NS + "integer"), 
							null, obj);	
				}
				dataTypeSet.add((OWLConcreteDataImpl) dataValue);
				resultMap.put(owlDataFactory.getOWLDataProperty(
						new URI(property.toString())), dataTypeSet);
			}
		}
		return resultMap;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }
	}

	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLEntity>> getPropertyValues(
			String ontologyURI,	OWLObjectProperty property) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Map<OWLEntity, Set<OWLEntity>> resultMap = 
        	new HashMap<OWLEntity, Set<OWLEntity>>();
		ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
				property.toString().substring(
						property.toString().indexOf("]")+2));
		Set<ObjectPropertyMember> individuals = 
			owlObjectProperty.getObjectPropertyMembers(ontology);
		for (ObjectPropertyMember member : individuals) {
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			entitySet.add(owlDataFactory.getOWLIndividual(
					new URI(member.getTargetIndividual().toString())));
			resultMap.put(owlDataFactory.getOWLIndividual(
					new URI(member.getSourceIndividual().toString())), 
					entitySet);
		}
		return resultMap;        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues(
			String ontologyURI, OWLDataProperty property) 
			throws OWLException,  URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Map<OWLEntity, Set<OWLConcreteDataImpl>> resultMap = 
        	new HashMap<OWLEntity, Set<OWLConcreteDataImpl>>();
		DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
				property.toString().substring(
						property.toString().indexOf("]")+2));
		Set<DataPropertyMember> individuals = 
			owlDataProperty.getDataPropertyMembers(ontology);
		for (DataPropertyMember member : individuals) {
			Set<OWLConcreteDataImpl> dataTypeSet = 
				new HashSet<OWLConcreteDataImpl>();
			if (member.getTargetValue() instanceof String) {
				dataTypeSet.add((OWLConcreteDataImpl) owlDataFactory.
						getOWLConcreteData(new URI(Namespaces.XSD_NS + "string"), 
								null, member.getTargetValue()));
			}
			else if (member.getTargetValue() instanceof Integer) {
				dataTypeSet.add((OWLConcreteDataImpl) owlDataFactory.
						getOWLConcreteData(new URI(Namespaces.XSD_NS + "integer"), 
								null, member.getTargetValue()));
			}
			resultMap.put(owlDataFactory.getOWLIndividual(
					new URI(member.getSourceIndividual().toString())), 
					dataTypeSet);
		}
		return resultMap;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public boolean hasPropertyValue(String ontologyURI, OWLIndividual subject,
			OWLObjectProperty property, OWLIndividual object)
			throws OWLException,  InterruptedException {
        try{
reasoner = getReasoner(ontologyURI);
		Query query = reasoner.createQuery(Namespaces.INSTANCE, "ASK " +
				"{ <" + subject.toString().substring(subject.toString().indexOf("]")+2) + 
				"> <" + property.toString().substring(property.toString().indexOf("]")+2) + 
				"> <" + object.toString().substring(object.toString().indexOf("]")+2) + "> }");
		query.open();
		int size = query.getNumberOfTuples();
		query.close();
		return size > 0;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public boolean hasPropertyValue(String ontologyURI, OWLIndividual subject,
			OWLDataProperty property, OWLDataValue object)
			throws OWLException,  InterruptedException {
        try{
reasoner = getReasoner(ontologyURI);
		Query query = null;
		if (object.getValue() instanceof String) {
			query = reasoner.createQuery(Namespaces.INSTANCE, "ASK " +
					"{ <" + subject.toString().substring(subject.toString().indexOf("]")+2) + 
					"> <" + property.toString().substring(property.toString().indexOf("]")+2) + 
					"> \"" + object.getValue().toString() + "\" }");
		}
		else if (object.getValue() instanceof Integer || 
				object.getValue() instanceof BigInteger) {
			query = reasoner.createQuery(Namespaces.INSTANCE, "ASK " +
					"{ <" + subject.toString().substring(subject.toString().indexOf("]")+2) + 
					"> <" + property.toString().substring(property.toString().indexOf("]")+2) + 
					"> " + object.getValue().toString() + " }");
		}
		query.open();
		int size = query.getNumberOfTuples();
		query.close();
		return size > 0;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> getObjectPropertyValues(String ontologyURI,
			OWLIndividual subject, OWLObjectProperty property)
			throws OWLException,  InterruptedException, URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		Query query = reasoner.createQuery(Namespaces.INSTANCE, "Select ?x WHERE " +
				"{ <" + subject.toString().substring(subject.toString().indexOf("]")+2) + 
				"> <" + property.toString().substring(property.toString().indexOf("]")+2) + 
				"> ?x }");
		query.open();
		while (!query.afterLast()) {
			Object[] tupleBuffer = query.tupleBuffer();
			entitySet.add(owlDataFactory.getOWLIndividual(new URI(tupleBuffer[0].toString())));
			query.next();
		}
		query.close();
		return entitySet;        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	@SuppressWarnings("unchecked")
	public Set<OWLDataValue> getDataPropertyValues(String ontologyURI,
			OWLIndividual subject, OWLDataProperty property)
			throws OWLException,  InterruptedException, URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Set<OWLDataValue> dataValueSet = new HashSet<OWLDataValue>();
		Query query = reasoner.createQuery(Namespaces.INSTANCE, "Select ?x WHERE " +
				"{ <" + subject.toString().substring(subject.toString().indexOf("]")+2) + 
				"> <" + property.toString().substring(property.toString().indexOf("]")+2) + 
				"> ?x }");
		query.open();
		while (!query.afterLast()) {
			Object[] tupleBuffer = query.tupleBuffer();
			if (tupleBuffer[0] instanceof String) {
				dataValueSet.add(owlDataFactory.getOWLConcreteData(new URI(
						Namespaces.XSD_NS + "string"), null, tupleBuffer[0]));
			}
			else if (tupleBuffer[0] instanceof Integer || 
					tupleBuffer[0] instanceof BigInteger) {
				dataValueSet.add(owlDataFactory.getOWLConcreteData(new URI(
						Namespaces.XSD_NS + "integer"), null, tupleBuffer[0]));
			}
			query.next();
		}
		query.close();
		return dataValueSet;
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	public OWLIndividual getObjectPropertyValue(String ontologyURI,
			OWLIndividual subject, OWLObjectProperty property)
			throws OWLException,  InterruptedException, URISyntaxException {
        try{
reasoner = getReasoner(ontologyURI);
		Query query = reasoner.createQuery(Namespaces.INSTANCE, "Select ?x WHERE " +
				"{ <" + subject.toString().substring(subject.toString().indexOf("]")+2) + 
				"> <" + property.toString().substring(property.toString().indexOf("]")+2) + 
				"> ?x }");
		query.open();
		Object[] tupleBuffer = query.tupleBuffer();
		query.close();
		return owlDataFactory.getOWLIndividual(new URI(tupleBuffer[0].toString()));
        } catch (KAON2Exception e) {
            throw new OWLException("KAON2ERROR",e);
        }

	}

	public OWLDataValue getDataPropertyValue(String ontologyURI,
			OWLIndividual subject, OWLDataProperty property)
			throws OWLException,  InterruptedException, URISyntaxException {
            try{
reasoner = getReasoner(ontologyURI);
		Query query = reasoner.createQuery(Namespaces.INSTANCE, "Select ?x WHERE " +
				"{ <" + subject.toString().substring(subject.toString().indexOf("]")+2) + 
				"> <" + property.toString().substring(property.toString().indexOf("]")+2) + 
				"> ?x }");
		query.open();
		Object[] tupleBuffer = query.tupleBuffer();
		query.close();
		OWLDataValue dataValue = null;
		if (tupleBuffer[0] instanceof String) {
			dataValue = owlDataFactory.getOWLConcreteData(new URI(
					Namespaces.XSD_NS + "string"), null, tupleBuffer[0]);
		}
		else if (tupleBuffer[0] instanceof Integer ||
				tupleBuffer[0] instanceof BigInteger) {
			dataValue = owlDataFactory.getOWLConcreteData(new URI(
					Namespaces.XSD_NS + "integer"), null, tupleBuffer[0]);
		}
		return dataValue;
            } catch (KAON2Exception e) {
                throw new OWLException("KAON2ERROR",e);
            }
        

	}

//	public QueryResults evaluate(String ontologyURI, String queryString) {
//		throw new UnsupportedOperationException("not yet");
//		reasoner = registeredOntologies.get(ontologyURI);
//		QueryResults results = null;
//		Set set = reasoner.getKB().getObjectProperties();
//		Iterator it = set.iterator();
//		while (it.hasNext())
//			System.out.println(it.next().toString());
//		results = QueryEngine.execSPARQL(queryString, reasoner.getKB());
//		return results;
//	}
	
	@SuppressWarnings("unchecked")
	private Set<OWLEntity> addSubConcepts(String ontologyURI, OWLEntity entity, 
			Set<OWLEntity> entitySet) 
			throws OWLException,  URISyntaxException {
		Set<Set> set = subClassesOf(ontologyURI, 
				(org.semanticweb.owl.model.OWLClass) entity);
		Set<OWLEntity> set2 = set.iterator().next();
		for (OWLEntity ent : set2) {
			if (! entitySet.contains(ent)) {
				entitySet.addAll(set.iterator().next());
				addSubConcepts(ontologyURI, ent, entitySet);
			}
		}
		return entitySet;
	}
	
	@SuppressWarnings("unchecked")
	private Set<OWLEntity> addSuperConcepts(String ontologyURI, OWLEntity entity, 
			Set<OWLEntity> entitySet) 
			throws OWLException,  URISyntaxException {
		Set<Set> set = superClassesOf(ontologyURI, 
				(org.semanticweb.owl.model.OWLClass) entity);
		Set<OWLEntity> set2 = set.iterator().next();
		for (OWLEntity ent : set2) {
			if (! entitySet.contains(ent)) {
				entitySet.addAll(set.iterator().next());
				addSuperConcepts(ontologyURI, ent, entitySet);
			}
		}
		return entitySet;
	}
	
	@SuppressWarnings("unchecked")
	private Set<OWLEntity> addSubProperties(String ontologyURI, OWLEntity entity, 
			Set<OWLEntity> entitySet) 
			throws OWLException,  URISyntaxException {
		Set<Set> set = subPropertiesOf(ontologyURI, 
				(org.semanticweb.owl.model.OWLProperty) entity);
		Set<OWLEntity> set2 = set.iterator().next();
		for (OWLEntity ent : set2) {
			if (! entitySet.contains(ent)) {
				entitySet.addAll(set.iterator().next());
				addSubProperties(ontologyURI, ent, entitySet);
			}
		}
		return entitySet;
	}
	
	@SuppressWarnings("unchecked")
	private Set<OWLEntity> addSuperProperties(String ontologyURI, OWLEntity entity, 
			Set<OWLEntity> entitySet) 
			throws OWLException,  URISyntaxException {
		Set<Set> set = superPropertiesOf(ontologyURI, 
				(org.semanticweb.owl.model.OWLProperty) entity);
		Set<OWLEntity> set2 = set.iterator().next();
		for (OWLEntity ent : set2) {
			if (! entitySet.contains(ent)) {
				entitySet.addAll(set.iterator().next());
				addSuperProperties(ontologyURI, ent, entitySet);
			}
		}
		return entitySet;
	}
	
	private Reasoner getReasoner(String ontologyURI) {
		if (registeredOntologies.containsKey(ontologyURI)) {
			return registeredOntologies.get(ontologyURI);
		}
		else {
			try {
				throw new ExternalToolException("Ontology is not registered!");
			} catch (ExternalToolException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2007/01/30 13:49:05  nathalie
 * fixed hack with iri as substring containing http://
 *
 * Revision 1.2  2007/01/11 13:04:47  nathalie
 * removed unnecessary dependencies from pellet library
 *
 * Revision 1.1  2007/01/10 11:50:39  nathalie
 * completed kaon2DLFacade
 *
 * Revision 1.1  2007/01/02 11:30:50  hlausen
 * some testing...
 */