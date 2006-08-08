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
package org.wsml.reasoner.builtin.pellet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.owlapi.Reasoner;
import org.mindswap.pellet.query.QueryResults;
import org.semanticweb.owl.impl.model.OWLConcreteDataImpl;
import org.semanticweb.owl.impl.model.OWLConcreteDataTypeImpl;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLProperty;
import org.wsml.reasoner.DLReasonerFacade;
import org.wsml.reasoner.ExternalToolException;

/**
 * Integrates the PELLET reasoner system into the WSML-DL reasoner framework.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/builtin/pellet/PelletFacade.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.4 $ $Date: 2006-08-08 10:14:27 $
 */
public class PelletFacade implements DLReasonerFacade {
	
	protected Reasoner reasoner = null;
	
	private Map<String, Reasoner> registeredOntologies = null;
	
	/**
     * Creates a facade object that allows to invoke the PELLET system for
     * performing reasoning tasks.
     */
    public PelletFacade() {
        super();
        registeredOntologies = new HashMap<String, Reasoner>();
    }

    /**
     * Registers the OWL ontology at the PELLET reasoner.
     */
	public void register(OWLOntology owlOntology) 
			throws ExternalToolException {
        reasoner = new Reasoner();
		try {
			reasoner.setOntology(owlOntology);
			registeredOntologies.put(owlOntology.getURI().toString(), 
					reasoner);
		} catch (OWLException e) {
			 e.printStackTrace();
	         throw new ExternalToolException(
	         		"PELLET ontology registration problem.");
		}
	}

	/**
	 * The Knowledge base, that PELLET derived from this OWL 
	 * ontology is cleared.
	 * @throws ExternalToolException 
	 */
	public void deRegister(String ontologyURI) 
			throws ExternalToolException {
		reasoner = getReasoner(ontologyURI);
		reasoner.getKB().clear();
		registeredOntologies.remove(ontologyURI);
	}

	public boolean isConsistent(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		return reasoner.isConsistent();
	}
	
	public boolean isConsistent(String ontologyURI, OWLDescription clazz) 
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.isConsistent(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allClasses(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getClasses();
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allIndividuals(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getIndividuals();
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allProperties(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getProperties();
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allDataProperties(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getDataProperties();
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allObjectProperties(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getObjectProperties();
	}
	
	@SuppressWarnings("unchecked")
	public Set<Set> descendantClassesOf(String ontologyURI, 
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.descendantClassesOf(clazz);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> ancestorClassesOf(String ontologyURI, 
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.ancestorClassesOf(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> equivalentClassesOf(String ontologyURI, 
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.equivalentClassesOf(clazz);
	}
	
	public boolean isEquivalentClass(String ontologyURI, OWLDescription clazz1, 
			OWLDescription clazz2) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.isEquivalentClass(clazz1, clazz2);
	}
	
	public boolean isSubClassOf(String ontologyURI, OWLDescription clazz1, 
			OWLDescription clazz2) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.isSubClassOf(clazz1, clazz2);
	}

	public boolean isInstanceOf(String ontologyURI, OWLIndividual individual, 
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.isInstanceOf(individual, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allInstancesOf(String ontologyURI, OWLClass clazz) 
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.allInstancesOf(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Set> allTypesOf(String ontologyURI, OWLIndividual individual) 
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.allTypesOf(individual);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Set> descendantPropertiesOf(String ontologyURI, 
			OWLProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.descendantPropertiesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Set> ancestorPropertiesOf(String ontologyURI, 
			OWLProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.ancestorPropertiesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> equivalentPropertiesOf(String ontologyURI, 
			OWLProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.equivalentPropertiesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> inversePropertiesOf(String ontologyURI, 
			OWLObjectProperty property)	throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.inversePropertiesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> domainsOf(String ontologyURI, OWLProperty property) 
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.domainsOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> rangesOf(String ontologyURI, 
			OWLObjectProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.rangesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLConcreteDataTypeImpl> rangesOf(String ontologyURI, 
			OWLDataProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.rangesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues(
			String ontologyURI, OWLIndividual individual) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getDataPropertyValues(individual);
	}
	
	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues(
			String ontologyURI, OWLIndividual individual) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getObjectPropertyValues(individual);
	}
	
	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLEntity>> getPropertyValues(String ontologyURI, 
			OWLObjectProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getPropertyValues(property);
	}
	
	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues(
			String ontologyURI, OWLDataProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getPropertyValues(property);
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasPropertyValue(String ontologyURI, OWLIndividual subject,
			OWLObjectProperty property, OWLIndividual object) 
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.hasPropertyValue(subject, property, object);
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasPropertyValue(String ontologyURI, OWLIndividual subject,
			OWLDataProperty property, OWLDataValue object) 
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.hasPropertyValue(subject, property, object);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> getObjectPropertyValues(String ontologyURI, 
			OWLIndividual subject, OWLObjectProperty property) 
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getPropertyValues(subject, property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLDataValue> getDataPropertyValues(String ontologyURI, 
			OWLIndividual subject, OWLDataProperty property) 
			throws OWLException{
		reasoner = getReasoner(ontologyURI);
		return reasoner.getPropertyValues(subject, property);
	}
	
	public OWLIndividual getObjectPropertyValue(String ontologyURI, 
			OWLIndividual subject, OWLObjectProperty property) 
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getPropertyValue(subject, property);
	}
	
	public OWLDataValue getDataPropertyValue(String ontologyURI, 
			OWLIndividual subject, OWLDataProperty property) 
			throws OWLException{
		reasoner = getReasoner(ontologyURI);
		return reasoner.getPropertyValue(subject, property);
	}
	
	public void printClassTree(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		reasoner.getKB().printClassTree();
	}
	
	public String getInfo(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		return reasoner.getKB().getInfo();
	}
	
	private Reasoner getReasoner(String ontologyURI) {
		if (registeredOntologies.containsKey(ontologyURI)) {
			return registeredOntologies.get(ontologyURI);
		}
		else {
			try {
				throw new ExternalToolException("Ontology is not registrated!");
			} catch (ExternalToolException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public QueryResults evaluate(String ontologyURI, String queryString) {
		throw new UnsupportedOperationException();
//		reasoner = registeredOntologies.get(ontologyURI);
//		QueryResults results = null;
//		Set set = reasoner.getKB().getObjectProperties();
//		Iterator it = set.iterator();
//		while (it.hasNext())
//			System.out.println(it.next().toString());
//		results = QueryEngine.execSPARQL(queryString, reasoner.getKB());
//		return results;
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