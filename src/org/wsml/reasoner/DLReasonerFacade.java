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
package org.wsml.reasoner;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

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

/**
 * This interface represents a facade to various DL engines that allows to
 * perform a reasoning request, e.g. PELLET, KAON2.
 * 
 * For each such system a specific facade must be implemented to integrate the
 * component into the system.
 * 
 * <pre>
 *   Created on July 3rd, 2006
 *   Committed by $Author: nathalie $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/DLReasonerFacade.java,v $,
 * </pre>
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.11 $ $Date: 2007-03-01 11:33:06 $
 */
public interface DLReasonerFacade {

    /**
     * Registers the OWL-DL ontology at the external reasoner.
     * 
     * @param owlOntology
     *            the OWL-DL ontology
     * @throws OWLException
     */
    public void register(OWLOntology owlOntology) throws ExternalToolException;

    /**
     * Removes the ontology from the external reasoner.
     * 
     * @param ontologyURI
     *            the original logical ontology URI
     * @throws ExternalToolException
     *             if exception happens during ontology removal
     */
    public void deRegister() throws ExternalToolException;

    /**
     * @return true if the given ontology is consistent, false otherwise.
     */
    public boolean isConsistent();

    /**
     * @return true if the given OWL Description is satisfiable, false
     *         otherwise.
     * @throws InterruptedException
     * @throws OWLException
     */
    public boolean isConsistent(OWLDescription description) throws OWLException, InterruptedException;

    /**
     * @return a set containing all classes from the loaded ontology
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<OWLEntity> allClasses() throws OWLException, URISyntaxException;

    /**
     * @return a set containing all individuals from the loaded ontology
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<OWLEntity> allIndividuals() throws OWLException, URISyntaxException;

    /**
     * @return a set containing all properties from the loaded ontology
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<OWLEntity> allProperties() throws OWLException, URISyntaxException;

    /**
     * @return a set containing all data properties from the loaded ontology
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<OWLEntity> allDataProperties() throws OWLException, URISyntaxException;

    /**
     * @return a set containing all object properties from the loaded ontology
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<OWLEntity> allObjectProperties() throws OWLException, URISyntaxException;

    /**
     * @return a set containing all subclasses of a given class
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<Set> descendantClassesOf(OWLDescription clazz) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all direct subclasses of a given class
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<Set> subClassesOf(OWLDescription clazz) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all superclasses of a given class
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<Set<OWLEntity>> ancestorClassesOf(OWLDescription clazz) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all direct superclasses of a given class
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<Set> superClassesOf(OWLDescription clazz) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all classes equivalent to the given class
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<OWLEntity> equivalentClassesOf(OWLDescription clazz) throws OWLException, URISyntaxException;

    /**
     * @return true if the two given OWL descriptions are equivalent
     * @throws URISyntaxException
     * @throws OWLException
     */
    public boolean isEquivalentClass(OWLDescription clazz1, OWLDescription clazz2) throws OWLException, URISyntaxException;

    /**
     * @return true if the given clazz2 is a subClass of clazz1
     * @throws OWLException
     * @throws URISyntaxException
     */
    public boolean isSubClassOf(OWLDescription clazz1, OWLDescription clazz2) throws OWLException, URISyntaxException;

    /**
     * @return true if the given individual is an instance of clazz
     * @throws OWLException
     * @throws URISyntaxException
     */
    public boolean isInstanceOf(OWLIndividual individual, OWLDescription clazz) throws OWLException, URISyntaxException;

    /**
     * @return a set all instances of a given OWL class
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<OWLEntity> allInstancesOf(OWLClass clazz) throws OWLException, URISyntaxException;

    /**
     * @return a set with all direct concepts of a given OWL individual
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<Set> typesOf(OWLIndividual individual) throws OWLException, URISyntaxException;

    /**
     * @return a set with all (also indirect) concepts of a given OWL individual
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<Set> allTypesOf(OWLIndividual individual) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all subproperties of a given OWL property
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<Set> descendantPropertiesOf(OWLProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all direct subproperties of a given OWL property
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<Set> subPropertiesOf(OWLProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all superproperties of a given OWL property
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<Set> ancestorPropertiesOf(OWLProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all direct superproperties of a given OWL
     *         property
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<Set<OWLEntity>> superPropertiesOf(OWLProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all properties equivalent to the given property
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<OWLEntity> equivalentPropertiesOf(OWLProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a set containing all properties inverse to the given property
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<OWLEntity> inversePropertiesOf(OWLObjectProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a set containing the domains of the given property
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<OWLEntity> domainsOf(OWLProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a set containing the ranges of the given property
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<OWLEntity> rangesOf(OWLObjectProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a set containing the ranges of the given property
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Set<OWLConcreteDataTypeImpl> rangesOf(OWLDataProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a map containing all data properties and for each a set
     *         containing all its values
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues(OWLIndividual individual) throws OWLException, URISyntaxException;

    /**
     * @return a map containing all object properties and for each a set
     *         containing all its values
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues(OWLIndividual individual) throws OWLException, URISyntaxException;

    /**
     * @return a map containing all individuals who have values for a specified
     *         object property and for each a set containing all its values
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Map<OWLEntity, Set<OWLEntity>> getPropertyValues(OWLObjectProperty property) throws OWLException, URISyntaxException;

    /**
     * @return a map containing all individuals who have values for a specified
     *         data property and for each a set containing all its values
     * @throws URISyntaxException
     * @throws OWLException
     */
    public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues(OWLDataProperty property) throws OWLException, URISyntaxException;

    /**
     * @return true if the given subject individual has the given object
     *         property with the given object individual as value
     * @throws OWLException
     * @throws InterruptedException
     */
    public boolean hasPropertyValue(OWLIndividual subject, OWLObjectProperty property, OWLIndividual object) throws OWLException, InterruptedException;

    /**
     * @return true if the given subject individual has the given data property
     *         with the given data value as value
     * @throws InterruptedException
     * @throws OWLException
     */
    public boolean hasPropertyValue(OWLIndividual subject, OWLDataProperty property, OWLDataValue object) throws OWLException, InterruptedException;

    /**
     * @return individual value of the given individual and object property
     * @throws InterruptedException
     * @throws OWLException
     * @throws URISyntaxException
     */
    public OWLIndividual getObjectPropertyValue(OWLIndividual subject, OWLObjectProperty property) throws OWLException, InterruptedException, URISyntaxException;

    /**
     * @return data value of the given individual and data property
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws OWLException
     */
    public OWLDataValue getDataPropertyValue(OWLIndividual subject, OWLDataProperty property) throws OWLException, InterruptedException, URISyntaxException;

    /**
     * @return set containing individual values of the given individual and
     *         object property
     * @throws InterruptedException
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<OWLEntity> getObjectPropertyValues(OWLIndividual subject, OWLObjectProperty property) throws OWLException, InterruptedException, URISyntaxException;

    /**
     * @return set containing data values of the given individual and data
     *         property
     * @throws InterruptedException
     * @throws OWLException
     * @throws URISyntaxException
     */
    public Set<OWLDataValue> getDataPropertyValues(OWLIndividual subject, OWLDataProperty property) throws OWLException, InterruptedException, URISyntaxException;

    // /**
    // * Evaluates a given query on a particular external tool.
    // *
    // * @param query the query to be evaluated.
    // * @return a set of Query Results
    // */
    // public QueryResults evaluate(String ontologyURI, String query);

}
/*
 * $Log: not supported by cvs2svn $ Revision 1.10 2007/02/09 08:40:54 hlausen
 * DLFacade should be independent of libs of specific reasoner!!!!
 * 
 * Revision 1.9 2007/01/11 13:04:46 nathalie removed unnecessary dependencies
 * from pellet library
 * 
 * Revision 1.8 2007/01/10 11:50:39 nathalie completed kaon2DLFacade
 * 
 * Revision 1.7 2006/11/30 16:54:57 nathalie added methods to get direct
 * super-/sub-concepts and direct super-/sub-relations
 * 
 * Revision 1.6 2006/08/31 12:36:00 nathalie removed methods from WSMLDLReasoner
 * interface to the WSMLReasoner interface. Replaced some methods by entails()
 * and groundQuery() methods.
 * 
 * Revision 1.5 2006/08/10 08:31:00 nathalie added request for getting direct
 * concept/concepts of an instance
 * 
 * Revision 1.4 2006/08/08 10:14:28 nathalie implemented support for registering
 * multiple ontolgies at wsml-dl reasoner
 * 
 * Revision 1.3 2006/07/21 16:25:21 nathalie completing the pellet reasoner
 * integration
 * 
 * Revision 1.2 2006/07/20 17:50:23 nathalie integration of the pellet reasoner
 * 
 * 
 */