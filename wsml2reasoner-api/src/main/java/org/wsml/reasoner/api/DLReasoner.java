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
package org.wsml.reasoner.api;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataType;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Type;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;

/**
 * An interface for invoking a WSML-DL reasoner with a particular reasoning
 * task.
 * 
 */
public interface DLReasoner extends WSMLReasoner {

	/**
	 * TODO danwin has no parameters what is satisfyable
	 * 
	 * @return true if satisfiable
	 */
    public boolean isSatisfiable();
    
	/**
	 * This method does satisifiability testing on the given logical expression 
	 * that must identify a (possible) set of instances.
	 * The Logical Expression must not be a rule or contain implications.
	 * 
	 * TODO danwin documentation seems to be wrong
	 * 
	 * @param expression
	 * @return true if the given expression is consistent, false otherwise.
	 */
    boolean isEntailed( LogicalExpression expression );

    /**
     * This method does a "CONCEPT SATIFIABLITY TEST" on the given logical expression 
	 * that must identify a (possible) set of instances.
	 * It must be created using the following language elements:
	 * <br />
	 * - Atom<br />
	 * - MembershipMolecule<br />
	 * - Conjunction<br />
	 * - ExistentialQuantification<br />
	 * <br /> 
	 * and contain exactly one free variable (that represents the instances).
	 * 
	 * TODO danwin given expression is consistent 
	 * 
	 * @throws InternalReasonerException if a logical expression different
	 * 			than the ones mentioned above are given as input
     * @param expression
     * @return true if the given expression is consistent, false otherwise
     */
    public boolean isConceptSatisfiable(LogicalExpression expression);
    
    /**
     * Test if subConcept is sub concept of superConcept.
     * 
     * @param subConcept
     * @param superConcept
     * @return true if 'subConcept' is a sub concept of 'superConcept', false
     *         otherwise
     */
    public boolean isSubConceptOf(Concept subConcept, Concept superConcept);

    /**
     * Test if instance is member of concept.
     * 
     * @param instance
     * @param concept
     * @return true if 'instance' is a member of 'concept', false otherwise
     */
    public boolean isMemberOf(Instance instance, Concept concept);

    /**
     * Please note that the results of this query differs depending on whether a
     * Datalog or a DL reasoner is used: 
     * - Datalog: the method returns all sub concepts, including equivalent concepts! 
     * - DL: the method returns all sub concepts, except the equivalent concepts!
     * 
     * @param concept
     * @return a set containing all subconcepts of the given concept
     */
    public Set<Concept> getSubConcepts(Concept concept);

    /**
     * Please note that the results of this query differs depending on whether a
     * Datalog or a DL reasoner is used: 
     * - Datalog: because Datalog does not support equivalence queries, the method returns an empty set in case there is a cycle like 
     *   e.g. the following: - school subConceptOf place - university subConceptOf school - place subConceptOf university 
     * - DL: the method returns all direct sub concepts, except the equivalent concepts!
     * 
     * @param concept
     * @return a set containing all direct sub concepts of the given concept
     */
    public Set<Concept> getDirectSubConcepts(Concept concept);

    /**
     * Please note that the results of this query differs depending on whether a
     * Datalog or a DL reasoner is used: 
     * - Datalog: the method returns all superconcepts, including equivalent concepts! 
     * - DL: the method returns all superconcepts, except the equivalent concepts!
     * 
     * @param concept
     * @return a set containing all superconcepts of the given concept
     */
    public Set<Concept> getSuperConcepts(Concept concept);

    /**
     * Please note that the results of this query differs depending on whether a
     * Datalog or a DL reasoner is used: 
     * - Datalog: because Datalog does not support equivalence queries, the method returns an empty set in case there is a cycle like 
     *   e.g. the following: - school subConceptOf place - university subConceptOf school - place subConceptOf university 
     * - DL: the method returns all direct super concepts, except the equivalent concepts!
     * 
     * @param concept
     * @return a set containing all direct super concepts of the given concept
     */
    public Set<Concept> getDirectSuperConcepts(Concept concept);

    /**
     * Get instances of the given concept.
     * 
     * @param concept
     * @return a set containing all instances of a given concept
     */
    public Set<Instance> getInstances(Concept concept);

    /**
     * Get concepts of the given instance.
     * 
     * @param instance
     * @return a set containing all concepts of a given instance
     */
    public Set<Concept> getConcepts(Instance instance);

    /**
     * Get all concepts from the registered ontology.
     * 
     * @return a set containing all concepts from the registered ontology
     */
    public Set<Concept> getAllConcepts();

    /**
     * Get all instances from the registered ontology. 
     * 
     * @return a set containing all instances from the registered ontology
     */
    public Set<Instance> getAllInstances();

    /**
     * Please note that this method returns in Datalog only attributes that: 
     * - are explicitly defined as inferring or constraining attributes 
     * - have been assigned a value
     * 
     * The DL reasoner also returns attributes that have not been defined
     * explicitly and that have no values assigned to.
     * 
     * @return a set containing all attributes from the registered ontology
     */
    public Set<IRI> getAllAttributes();

    /**
     * Get all constraining attributes from the registered ontology.
     * 
     * @return a set containing all constraining attributes from the registered
     *         ontology
     */
    public Set<IRI> getAllConstraintAttributes();

    /**
     * Get all inferring attributes from the registered ontology.
     * 
     * @return a set containing all inferring attributes from the registered
     *         ontology
     */
    public Set<IRI> getAllInferenceAttributes();

    /**
     * Get all concepts equivalent to the given concept.
     * This method is not supported from the Datalog Reasoners!
     * 
     * @param concept
     * @return a set containing all concepts equivalent to the given concept
     */
    public Set<Concept> getEquivalentConcepts(Concept concept);

    /**
     * Check if two given concepts are equivalent.
     * This method is not supported from the Datalog Reasoners!
     * 
     * @return true if the two given concepts are equivalent, false otherwise
     */
    public boolean isEquivalentConcept(Concept concept1, Concept concept2);

    /**
     * Please note that this method returns all direct concepts of a given
     * instance, except for direct concepts that are equivalent to indirect
     * concepts.
     * 
     * @param instance
     * @return a set with all direct concepts of a given instance.
     */
    public Set<Concept> getDirectConcepts(Instance instance);

    /**
     * Get identifiers of sub relations of a given relation.
     * This method is not supported from the Datalog Reasoners!
     * 
     * @param attributeId
     * @return a set containing all identifiers of sub relations of a given
     *         relation
     */
    public Set<IRI> getSubRelations(Identifier attributeId);

    /**
	 * Get all identifiers of direct subrelations of a given relation.
     * This method is not supported from the Datalog Reasoners!
     * 
     * @param attributeId
     * @return a set containing all identifiers of direct sub relations of a
     *         given relation
     */
    public Set<IRI> getDirectSubRelations(Identifier attributeId);

    /**
     * Get all identifiers of super relations of a given relation.
     * This method is not supported from the Datalog Reasoners!
     * 
     * @param attributeId
     * @return a set containing all identifiers of superrelations of a given
     *         relation
     */
    public Set<IRI> getSuperRelations(Identifier attributeId);

    /**
     * Get all identifiers of direct super relations of a given relation.
     * This method is not supported from the Datalog Reasoners!
     * 
     * @param attributeId
     * @return a set containing all identifiers of direct superrelations of a
     *         given relation
     */
    public Set<IRI> getDirectSuperRelations(Identifier attributeId);

    /**
     * Get the identifiers of all relations/attributes equivalent to the given relation/attribute.
     * This method is not supported from the Datalog Reasoners!
     * 
     * @param attributeId
     * @return a set containing the identifiers of all relations/attributes
     *         equivalent to the given relation/attribute
     */
    public Set<IRI> getEquivalentRelations(Identifier attributeId);

    /**
     * Get the identifiers of all relations/attributes inverse to the given relation/attribute.
     * This method is not supported from the Datalog Reasoners!
     * 
     * @param attributeId
     * @return a set containing the identifiers of all relations/attributes
     *         inverse to the given relation/attribute
     */
    public Set<IRI> getInverseRelations(Identifier attributeId);

    /**
     * Get the concepts of the given attribute.
     * 
     * @param attributeId
     * @return a set containing the concepts of the given attribute
     */
    public Set<Concept> getConceptsOf(Identifier attributeId);

    /**
     * Get the ranges of the given inferring attribute.
     * 
     * @param attributeId
     * @return a set containing the ranges of the given inferring attribute
     */
    public Set<Type> getRangesOfInferingAttribute(Identifier attributeId);

    /**
     * Get the ranges of the given constraint attribute.
     * 
     * @param attributeId
     * @return a set containing the ranges of the given constraint attribute
     */
    public Set<DataType> getRangesOfConstraintAttribute(Identifier attributeId);

    /**
     * Get all inferring attributes of a specified instance and for each a set containing all its values.
     * 
     * @param instance
     * @return a map containing all inferring attributes of a specified instance
     *         and for each a set containing all its values
     */
    public Map<IRI, Set<Term>> getInferingAttributeValues(Instance instance);

    /**
     * Get all constraint attributes of a specified instance and for each a set containing all its values.
     * 
     * @param instance
     * @return a map containing all constraint attributes of a specified
     *         instance and for each a set containing all its values
     */
    public Map<IRI, Set<DataValue>> getConstraintAttributeValues(Instance instance);

    /**
     * Get all instances who have values for a specified inferring attribute and for each a set containing all its values.
     * 
     * @param attributeId
     * @return a map containing all instances who have values for a specified
     *         inferring attribute and for each a set containing all its values
     */
    public Map<Instance, Set<Term>> getInferingAttributeInstances(Identifier attributeId);

    /**
     * Get all instances who have values for a specified constraint attribute and for each a set containing all its values.
     * 
     * @param attributeId
     * @return a map containing all instances who have values for a specified
     *         constraint attribute and for each a set containing all its values
     */
    public Map<Instance, Set<DataValue>> getConstraintAttributeInstances(Identifier attributeId);

    /**
     * Get instance values of the given instance and inferring attribute.
     * 
     * @param subject
     * @param attributeId
     * @return set containing instance values of the given instance and inferring
     *         attribute
     */
    public Set<Term> getInferingAttributeValues(Instance subject, Identifier attributeId);

    /**
     * Get data values of the given instance and constraint attribute.
     * 
     * @param subject
     * @param attributeId
     * @return set containing data values of the given instance and constraint
     *         attribute
     */
    public Set<DataValue> getConstraintAttributeValues(Instance subject, Identifier attributeId);
}