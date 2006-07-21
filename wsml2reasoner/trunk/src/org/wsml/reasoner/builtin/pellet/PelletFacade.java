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
import org.wsml.reasoner.impl.WSMO4JManager;

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
 * @version $Revision: 1.3 $ $Date: 2006-07-21 16:25:21 $
 */
public class PelletFacade implements DLReasonerFacade {
	
	protected Reasoner reasoner = null;
	
	/**
     * Creates a facade object that allows to invoke the PELLET system for
     * performing reasoning tasks.
     */
    public PelletFacade(WSMO4JManager wsmoManager) {
        super();
        reasoner = new Reasoner();
    }

    /**
     * Registers the OWL ontology at the PELLET reasoner.
     */
	public void register(OWLOntology owlOntology) throws ExternalToolException {
		try {
			reasoner.getKB().clear();
			reasoner.setOntology(owlOntology);
		} catch (OWLException e) {
			 e.printStackTrace();
	         throw new ExternalToolException(
	         		"PELLET ontology registration problem.");
		}
		reasoner.getKB().realize();
	}

	/**
	 * The Knowledge base, that PELLET derived from this OWL 
	 * ontology is cleared.
	 */
	public void deRegister(OWLOntology owlOntology) {
		reasoner.getKB().clear();
	}

	public boolean isConsistent() {
		return reasoner.isConsistent();
	}
	
	public boolean isConsistent(OWLDescription clazz) 
			throws OWLException {
		return reasoner.isConsistent(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allClasses() {
		return reasoner.getClasses();
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allIndividuals() {
		return reasoner.getIndividuals();
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allProperties() {
		return reasoner.getProperties();
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allDataProperties() {
		return reasoner.getDataProperties();
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allObjectProperties() {
		return reasoner.getObjectProperties();
	}
	
	@SuppressWarnings("unchecked")
	public Set<Set> descendantClassesOf(OWLDescription clazz) 
			throws OWLException {
		return reasoner.descendantClassesOf(clazz);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> ancestorClassesOf(OWLDescription clazz) 
			throws OWLException {
		return reasoner.ancestorClassesOf(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> equivalentClassesOf(OWLDescription clazz) 
			throws OWLException {
		return reasoner.equivalentClassesOf(clazz);
	}
	
	public boolean isEquivalentClass(OWLDescription clazz1, 
			OWLDescription clazz2) throws OWLException {
		return reasoner.isEquivalentClass(clazz1, clazz2);
	}
	
	public boolean isSubClassOf(OWLDescription clazz1, OWLDescription clazz2) 
			throws OWLException {
		return reasoner.isSubClassOf(clazz1, clazz2);
	}

	public boolean isInstanceOf(OWLIndividual individual, OWLDescription clazz) 
			throws OWLException {
		return reasoner.isInstanceOf(individual, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allInstancesOf(OWLClass clazz) throws OWLException {
		return reasoner.allInstancesOf(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Set> allTypesOf(OWLIndividual individual) throws OWLException {
		return reasoner.allTypesOf(individual);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Set> descendantPropertiesOf(OWLProperty property) 
			throws OWLException {
		return reasoner.descendantPropertiesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Set> ancestorPropertiesOf(OWLProperty property) 
			throws OWLException {
		return reasoner.ancestorPropertiesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> equivalentPropertiesOf(OWLProperty property) 
			throws OWLException {
		return reasoner.equivalentPropertiesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> inversePropertiesOf(OWLObjectProperty property)
			throws OWLException {
		return reasoner.inversePropertiesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> domainsOf(OWLProperty property) 
			throws OWLException {
		return reasoner.domainsOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> rangesOf(OWLObjectProperty property)
			throws OWLException {
		return reasoner.rangesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLConcreteDataTypeImpl> rangesOf(OWLDataProperty property)
			throws OWLException {
		return reasoner.rangesOf(property);
	}
	
	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues(
			OWLIndividual individual) throws OWLException {
		return reasoner.getDataPropertyValues(individual);
	}
	
	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues(
			OWLIndividual individual) throws OWLException {
		return reasoner.getObjectPropertyValues(individual);
	}
	
	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLEntity>> getPropertyValues(
			OWLObjectProperty property) throws OWLException {
		return reasoner.getPropertyValues(property);
	}
	
	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues(
			OWLDataProperty property) throws OWLException {
		return reasoner.getPropertyValues(property);
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasPropertyValue(OWLIndividual subject,
			OWLObjectProperty property, OWLIndividual object) throws OWLException {
		return reasoner.hasPropertyValue(subject, property, object);
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasPropertyValue(OWLIndividual subject,
			OWLDataProperty property, OWLDataValue object) throws OWLException {
		return reasoner.hasPropertyValue(subject, property, object);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLEntity> getObjectPropertyValues(OWLIndividual subject, 
			OWLObjectProperty property) throws OWLException {
		return reasoner.getPropertyValues(subject, property);
	}
	
	@SuppressWarnings("unchecked")
	public Set<OWLDataValue> getDataPropertyValues(OWLIndividual subject, 
			OWLDataProperty property) throws OWLException{
		return reasoner.getPropertyValues(subject, property);
	}
	
	public OWLIndividual getObjectPropertyValue(OWLIndividual subject, 
			OWLObjectProperty property) throws OWLException {
		return reasoner.getPropertyValue(subject, property);
	}
	
	public OWLDataValue getDataPropertyValue(OWLIndividual subject, 
			OWLDataProperty property) throws OWLException{
		return reasoner.getPropertyValue(subject, property);
	}
	
	public void printClassTree() {
		reasoner.getKB().printClassTree();
	}
	
	public String getInfo() {
		return reasoner.getKB().getInfo();
	}
	
	public QueryResults evaluate(String queryString) {
		throw new UnsupportedOperationException();
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
 * Revision 1.2  2006/07/20 17:50:23  nathalie
 * integration of the pellet reasoner
 *
 * Revision 1.1  2006/07/18 08:21:01  nathalie
 * adding wsml dl reasoner interface,
 * transformation from wsml dl to owl-dl
 *
 *
 */