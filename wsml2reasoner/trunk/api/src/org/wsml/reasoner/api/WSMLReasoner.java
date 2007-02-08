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

package org.wsml.reasoner.api;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.*;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;

/**
 * An interface for invoking a WSML reasoner with a particular reasoning task.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface WSMLReasoner {
	
    public void registerOntologies(Set<Ontology> ontologies)
            throws InconsistencyException;

    /**
     * Registers the ontology. If the ontology is already registered, updates
     * the ontology content.
     * 
     * @param ontology
     */
    public void registerOntology(Ontology ontology)
            throws InconsistencyException;

    public void registerOntologiesNoVerification(Set<Ontology> ontologies);

    /**
     * Registers the ontology. If the ontology is already registered, updates
     * the ontology content.
     * 
     * @param ontology
     */
    public void registerOntologyNoVerification(Ontology ontology);

    /**
     * Deregisters the ontology. Any further request using this ontologyID will
     * result in an exception.
     * 
     * @param ontologyID
     */
    public void deRegisterOntology(IRI ontologyID);

    public void deRegisterOntology(Set<IRI> ontologyIDs);

    public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query);

    public Set<Map<Variable, Term>> executeQuery(IRI ontologyID,
            LogicalExpression query);
    
    public boolean entails(IRI ontologyID, LogicalExpression expression);

    public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions);
    
    public boolean isSatisfiable(IRI ontologyID);

    /**
     * @return a set of violation objects, or an empty set, if the ontology is
     *         consistent (satisfiable)
     */
    public Set<ConsistencyViolation> checkConsistency(IRI ontologyID);

    /**
     * @return true if 'subConcept' is a subconcept of 'superConcept', false otherwise
     */
    public boolean isSubConceptOf(IRI ontologyID, Concept subConcept,
            Concept superConcept);

    /**
     * @return true if 'instance' is a member of 'concept', false otherwise
     */
    public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept);

    /**
     * @return a set containing all subconcepts of the given concept
     */
    public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept);
    
    /**
     * @return a set containing all direct subconcepts of the given concept
     */
    public Set<Concept> getDirectSubConcepts(IRI ontologyID, Concept concept);

    /**
     * @return a set containing all superconcepts of the given concept
     */
    public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept);
    
    /**
     * @return a set containing all direct superconcepts of the given concept
     */
    public Set<Concept> getDirectSuperConcepts(IRI ontologyID, Concept concept);

    /**
     * @return a set containing all instances of a given concept
     */
    public Set<Instance> getInstances(IRI ontologyID, Concept concept);

    /**
     * @return a set containing all concepts of a given instance
     */
    public Set<Concept> getConcepts(IRI ontologyID, Instance instance);
    
    /**
	 * @return a set containing all concepts from the registered ontology
	 */
	public Set<Concept> getAllConcepts(IRI ontologyID);
	
	/**
	 * @return a set containing all instances from the registered ontology
	 */
	public Set<Instance> getAllInstances(IRI ontologyID);
	
	/**
	 * @return a set containing all attributes from the registered ontology
	 */
	public Set<IRI> getAllAttributes(IRI ontologyID);
	
	/**
	 * @return a set containing all constraining attributes from the registered ontology
	 */
	public Set<IRI> getAllConstraintAttributes(IRI ontologyID);
	
	/**
	 * @return a set containing all infering attributes from the registered ontology
	 */
	public Set<IRI> getAllInferenceAttributes(IRI ontologyID);

	/**
	 * @return a set containing all concepts equivalent to the given concept
	 */
	public Set<Concept> getEquivalentConcepts(IRI ontologyID, Concept concept);
	
	/**
	 * @return true if the two given concepts are equivalent, false otherwise
	 */
	public boolean isEquivalentConcept(IRI ontologyID, Concept concept1, 
			Concept concept2);
	
	/**
     * @return a set with all direct concepts of a given instance.
     */
	public Set<Concept> getDirectConcepts(IRI ontologyID, Instance instance);
	
	/**
	 * @return a set containing all identifiers of subrelations of a given relation
	 */
	public Set<IRI> getSubRelations(IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return a set containing all identifiers of direct subrelations of a given relation
	 */
	public Set<IRI> getDirectSubRelations(IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return a set containing all identifiers of superrelations of a given relation
	 */
	public Set<IRI> getSuperRelations(IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return a set containing all identifiers of direct superrelations of a given relation
	 */
	public Set<IRI> getDirectSuperRelations(IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return a set containing the identifiers of all relations/attributes 
	 * 			equivalent to the given relation/attribute
	 */
	public Set<IRI> getEquivalentRelations(IRI ontologyID, 
			Identifier attributeId);
	
	/**
	 * @return a set containing the identifiers of all relations/attributes 
	 * 			inverse to the given relation/attribute
	 */
	public Set<IRI> getInverseRelations(IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return a set containing the concepts of the given attribute
	 */
	public Set<Concept> getConceptsOf(IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return a set containing the ranges of the given infering attribute
	 */
	public Set<IRI> getRangesOfInferingAttribute(IRI ontologyID, 
			Identifier attributeId);
	
	/**
	 * @return a set containing the ranges of the given constraint attribute
	 */
	public Set<IRI> getRangesOfConstraintAttribute(IRI ontologyID, 
			Identifier attributeId);
	
	/**
	 * @return a map containing all infering attributes of a specified instance 
	 * 			and for each a set containing all its values
	 */
	public Map<IRI, Set<IRI>> getInferingAttributeValues(IRI ontologyID, 
			Instance instance);
	
	/**
	 * @return a map containing all constraint attributes of a specified instance 
	 *			and for each a set containing all its values
	 */
	public Map<IRI, Set<Term>> getConstraintAttributeValues(IRI ontologyID, 
			Instance instance);
	
	/**
	 * @return a map containing all instances who have values for a specified 
	 * 			infering attribute and for each a set containing all its values
	 */
	public Map<Instance, Set<IRI>> getInferingAttributeInstances(
			IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return a map containing all instances who have values for a specified 
	 * 			constraint attribute and for each a set containing all its values
	 */
	public Map<Instance, Set<Term>> getConstraintAttributeInstances(
			IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return instance value of the given instance and infering attribute
	 */
	public Instance getInferingAttributeValue(IRI ontologyID, Instance subject, 
			Identifier attributeId);
	
	/**
	 * @return data value of the given instance and constraint attribute
	 */
	public String getConstraintAttributeValue(IRI ontologyID, Instance subject, 
			Identifier attributeId);
	
	/**
	 * @return set containing instance values of the given instance and 
	 * 			infering attribute
	 */
	public Set<Instance> getInferingAttributeValues(IRI ontologyID, 
			Instance subject, Identifier attributeId);
	
	/**
	 * @return set containing data values of the given instance and 
	 * 			constraint attribute
	 */
	public Set<String> getConstraintAttributeValues(IRI ontologyID, 
			Instance subject, Identifier attributeId);

}
