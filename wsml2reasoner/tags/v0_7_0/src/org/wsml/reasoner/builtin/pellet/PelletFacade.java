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

/**
 * Integrates the PELLET reasoner system into the WSML-DL reasoner framework.
 * 
 * <pre>
 *   Created on July 3rd, 2006
 *   Committed by $Author: graham $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/builtin/pellet/PelletFacade.java,v $,
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
     * Registers the OWL ontology at the PELLET reasoner.
     */
    public void register(OWLOntology owlOntology) throws ExternalToolException {
        reasoner = new Reasoner();
        try {
            reasoner.setOntology(owlOntology);
            if (log.isDebugEnabled()) {
                reasoner.getKB().printClassTree();
            }
        }
        catch (OWLException e) {
            e.printStackTrace();
            throw new ExternalToolException("PELLET ontology registration problem.");
        }
    }

    /**
     * The Knowledge base, that PELLET derived from this OWL ontology is
     * cleared.
     * 
     * @throws ExternalToolException
     */
    public void deRegister() throws ExternalToolException {
        reasoner.getKB().clear();
        reasoner = null;
    }

    public boolean isConsistent() {
        return reasoner.isConsistent();
    }

    public boolean isConsistent(OWLDescription description) throws OWLException {
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

    public Set<Set<?>> descendantClassesOf(OWLDescription clazz) throws OWLException {
        return reasoner.descendantClassesOf(clazz);
    }

    public Set<Set<?>> subClassesOf(OWLDescription clazz) throws OWLException {
        return reasoner.subClassesOf(clazz);
    }

    public Set<Set<OWLEntity>> ancestorClassesOf(OWLDescription clazz) throws OWLException {
        return reasoner.ancestorClassesOf(clazz);
    }

    public Set<Set<?>> superClassesOf(OWLDescription clazz) throws OWLException {
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

    public Set<Set<?>> typesOf(OWLIndividual individual) throws OWLException {
        return reasoner.typesOf(individual);
    }

    public Set<Set<?>> allTypesOf(OWLIndividual individual) throws OWLException {
        return reasoner.allTypesOf(individual);
    }

    public Set<Set<?>> descendantPropertiesOf(OWLProperty property) throws OWLException {
        return reasoner.descendantPropertiesOf(property);
    }

    public Set<Set<?>> subPropertiesOf(OWLProperty property) throws OWLException {
        return reasoner.subPropertiesOf(property);
    }

    public Set<Set<?>> ancestorPropertiesOf(OWLProperty property) throws OWLException {
        return reasoner.ancestorPropertiesOf(property);
    }

    public Set<Set<OWLEntity>> superPropertiesOf(OWLProperty property) throws OWLException {
        return reasoner.superPropertiesOf(property);
    }

    public Set<OWLEntity> equivalentPropertiesOf(OWLProperty property) throws OWLException {
        return reasoner.equivalentPropertiesOf(property);
    }

    public Set<OWLEntity> inversePropertiesOf(OWLObjectProperty property) throws OWLException {
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

    public Set<OWLDataValue> getDataPropertyValues(OWLIndividual subject, OWLDataProperty property) throws OWLException {
        return reasoner.getPropertyValues(subject, property);
    }

    public OWLIndividual getObjectPropertyValue(OWLIndividual subject, OWLObjectProperty property) throws OWLException {
        return reasoner.getPropertyValue(subject, property);
    }

    public OWLDataValue getDataPropertyValue(OWLIndividual subject, OWLDataProperty property) throws OWLException {
        return reasoner.getPropertyValue(subject, property);
    }
}