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
import org.apache.log4j.Logger;
import org.mindswap.pellet.owlapi.Reasoner;
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
 *  Committed by $Author: graham $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/builtin/pellet/PelletFacade.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.12 $ $Date: 2007-04-26 17:38:58 $
 */
public class PelletFacade implements DLReasonerFacade {
	
	private Reasoner reasoner = null;
    private Logger log = Logger.getLogger(PelletFacade.class);
	
	/**
     * Creates a facade object that allows to invoke the PELLET system for
     * performing reasoning tasks.
     */
    public PelletFacade() {
        super();
    }
    
    /**
     * Creates a new pellet based facade.
     * @param m the wsmo4j manager
     */
    public PelletFacade(final WSMO4JManager m, final Map<String, Object> config) {
    	this();
    }

    /**
     * Registers the OWL ontology at the PELLET reasoner.
     */
	public void register(OWLOntology owlOntology) 
			throws ExternalToolException {
        reasoner = new Reasoner();
		try {
			reasoner.setOntology(owlOntology);
            if (log.isDebugEnabled()) {
            	reasoner.getKB().printClassTree();
            }
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
	public void deRegister() 
			throws ExternalToolException {
		reasoner.getKB().clear();
		reasoner = null;
	}

	public boolean isConsistent() {
		return reasoner.isConsistent();
	}
	
	public boolean isConsistent(OWLDescription description) 
			throws OWLException {
		return reasoner.isConsistent(description);
	}
	
	
	public Set<OWLEntity> allClasses() {
		return reasoner.getClasses();
	}
	
	
	public Set<OWLEntity> allIndividuals() {
		return reasoner.getIndividuals();
	}
	
	
	public Set<OWLEntity> allProperties() {
		return reasoner.getProperties();
	}
	
	
	public Set<OWLEntity> allDataProperties() {
		return reasoner.getDataProperties();
	}
	
	
	public Set<OWLEntity> allObjectProperties() {
		return reasoner.getObjectProperties();
	}
	
	
	public Set<Set> descendantClassesOf(OWLDescription clazz) throws OWLException {
		return reasoner.descendantClassesOf(clazz);
	}
	
	
	public Set<Set> subClassesOf(OWLDescription clazz) throws OWLException {
		return reasoner.subClassesOf(clazz);
	}

	
	public Set<Set<OWLEntity>> ancestorClassesOf(OWLDescription clazz) throws OWLException {
		return reasoner.ancestorClassesOf(clazz);
	}
	
	
	public Set<Set> superClassesOf(OWLDescription clazz) throws OWLException {
		return reasoner.superClassesOf(clazz);
	}
	
	
	public Set<OWLEntity> equivalentClassesOf(OWLDescription clazz) throws OWLException {
		return reasoner.equivalentClassesOf(clazz);
	}
	
	public boolean isEquivalentClass(OWLDescription clazz1, OWLDescription clazz2) throws OWLException {
		return equivalentClassesOf(clazz1).contains(clazz2);
	}
	
	public boolean isSubClassOf(OWLDescription clazz1, OWLDescription clazz2) throws OWLException {
		return reasoner.isSubClassOf(clazz1, clazz2);
	}

	public boolean isInstanceOf(OWLIndividual individual, OWLDescription clazz) throws OWLException {
		return reasoner.isInstanceOf(individual, clazz);
	}
	
	
	public Set<OWLEntity> allInstancesOf(OWLClass clazz) throws OWLException {
		return reasoner.allInstancesOf(clazz);
	}
	
	
	public Set<Set> typesOf(OWLIndividual individual) throws OWLException {
		return reasoner.typesOf(individual);
	}
	
	
	public Set<Set> allTypesOf(OWLIndividual individual) throws OWLException {
		return reasoner.allTypesOf(individual);
	}
	
	
	public Set<Set> descendantPropertiesOf(OWLProperty property) throws OWLException {
		return reasoner.descendantPropertiesOf(property);
	}
	
	
	public Set<Set> subPropertiesOf(OWLProperty property) throws OWLException {
		return reasoner.subPropertiesOf(property);
	}
	
	
	public Set<Set> ancestorPropertiesOf(OWLProperty property) throws OWLException {
		return reasoner.ancestorPropertiesOf(property);
	}
	
	
	public Set<Set<OWLEntity>> superPropertiesOf(OWLProperty property) throws OWLException {
		return reasoner.superPropertiesOf(property);
	}
	
	
	public Set<OWLEntity> equivalentPropertiesOf(OWLProperty property) throws OWLException {
		return reasoner.equivalentPropertiesOf(property);
	}
	
	
	public Set<OWLEntity> inversePropertiesOf(OWLObjectProperty property)	throws OWLException {
		return reasoner.inversePropertiesOf(property);
	}
	
	
	public Set<OWLEntity> domainsOf(OWLProperty property) throws OWLException {
		return reasoner.domainsOf(property);
	}
	
	
	public Set<OWLEntity> rangesOf(OWLObjectProperty property) throws OWLException {
		return reasoner.rangesOf(property);
	}
	
	
	public Set<OWLConcreteDataTypeImpl> rangesOf(OWLDataProperty property) throws OWLException {
		return reasoner.rangesOf(property);
	}
	
	
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues(OWLIndividual individual) throws OWLException {
		return reasoner.getDataPropertyValues(individual);
	}
	
	
	public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues(OWLIndividual individual) throws OWLException {
		return reasoner.getObjectPropertyValues(individual);
	}
	
	
	public Map<OWLEntity, Set<OWLEntity>> getPropertyValues(OWLObjectProperty property) throws OWLException {
		return reasoner.getPropertyValues(property);
	}
	
	
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues(OWLDataProperty property) throws OWLException {
		return reasoner.getPropertyValues(property);
	}
	
	
	public boolean hasPropertyValue(OWLIndividual subject, OWLObjectProperty property, OWLIndividual object) throws OWLException {
		return reasoner.hasPropertyValue(subject, property, object);
	}
	
	
	public boolean hasPropertyValue(OWLIndividual subject, OWLDataProperty property, OWLDataValue object) throws OWLException {
		return reasoner.hasPropertyValue(subject, property, object);
	}
	
	
	public Set<OWLEntity> getObjectPropertyValues(OWLIndividual subject, OWLObjectProperty property) throws OWLException {
		return reasoner.getPropertyValues(subject, property);
	}
	
	
	public Set<OWLDataValue> getDataPropertyValues(OWLIndividual subject, OWLDataProperty property) throws OWLException{
		return reasoner.getPropertyValues(subject, property);
	}
	
	private Reasoner getReasoner() {
		return reasoner;
	}
	
	public OWLIndividual getObjectPropertyValue(OWLIndividual subject, OWLObjectProperty property) throws OWLException {
		return reasoner.getPropertyValue(subject, property);
	}
	
	public OWLDataValue getDataPropertyValue(OWLIndividual subject, OWLDataProperty property) throws OWLException{
		return reasoner.getPropertyValue(subject, property);
	}
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.11  2007/01/11 13:04:46  nathalie
 * removed unnecessary dependencies from pellet library
 *
 * Revision 1.10  2007/01/10 11:26:39  nathalie
 * fixed problem with equivalent classes
 *
 * Revision 1.9  2006/11/30 16:54:57  nathalie
 * added methods to get direct super-/sub-concepts and direct super-/sub-relations
 *
 * Revision 1.8  2006/09/19 13:47:28  nathalie
 * added example for using Pellet Logger printClassTree function
 *
 * Revision 1.7  2006/09/14 18:37:06  hlausen
 * enabled the print of class tree if pellet facade logging is set to DEBUG
 *
 * when register inconsitent ontologies the method should not throw the pellete exception but the wsml2reasoner inconsistency exception, however the inconsistencyexception class still needs some fix
 *
 * Revision 1.6  2006/08/31 12:36:00  nathalie
 * removed methods from WSMLDLReasoner interface to the WSMLReasoner interface. Replaced some methods by entails() and groundQuery() methods.
 *
 * Revision 1.5  2006/08/10 08:30:59  nathalie
 * added request for getting direct concept/concepts of an instance
 *
 * Revision 1.4  2006/08/08 10:14:27  nathalie
 * implemented support for registering multiple ontolgies at wsml-dl reasoner
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