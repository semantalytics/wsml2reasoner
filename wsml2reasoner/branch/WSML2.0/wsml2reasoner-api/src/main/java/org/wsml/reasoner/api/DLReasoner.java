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

    public boolean isSatisfiable();
    
	/**
	 * This method does satisifiability testing on the given logical expression 
	 * that must identify a (possible) set of instances.
	 * The Logical Expression must not be a rule or contain implications.
	 * 
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
	 * @return true if the given expression is consistent, false otherwise
	 * @throws InternalReasonerException if a logical expression different
	 * 			than the ones mentioned above are given as input
	 */
	public boolean isConceptSatisfiable(LogicalExpression expression);

    /**
     * @return true if 'subConcept' is a subconcept of 'superConcept', false
     *         otherwise
     */
    public boolean isSubConceptOf(Concept subConcept, Concept superConcept);

    /**
     * @return true if 'instance' is a member of 'concept', false otherwise
     */
    public boolean isMemberOf(Instance instance, Concept concept);

    /**
     * Please note that the results of this query differ depending on whether a
     * Datalog or a DL reasoner is used: - Datalog: the method returns all
     * subconcepts, including equivalent concepts! - DL: the method returns all
     * subconcepts, except the equivalent concepts!
     * 
     * @return a set containing all subconcepts of the given concept
     */
    public Set<Concept> getSubConcepts(Concept concept);

    /**
     * Please note that the results of this query differ depending on whether a
     * Datalog or a DL reasoner is used: - Datalog: because Datalog does not
     * support equivalence queries, the method returns an empty set in case
     * there is a cycle like e.g. the following: - school subConceptOf place -
     * university subConceptOf school - place subConceptOf university - DL: the
     * method returns all direct subconcepts, except the equivalent concepts!
     * 
     * @return a set containing all direct subconcepts of the given concept
     */
    public Set<Concept> getDirectSubConcepts(Concept concept);

    /**
     * Please note that the results of this query differ depending on whether a
     * Datalog or a DL reasoner is used: - Datalog: the method returns all
     * superconcepts, including equivalent concepts! - DL: the method returns
     * all superconcepts, except the equivalent concepts!
     * 
     * @return a set containing all superconcepts of the given concept
     */
    public Set<Concept> getSuperConcepts(Concept concept);

    /**
     * Please note that the results of this query differ depending on whether a
     * Datalog or a DL reasoner is used: - Datalog: because Datalog does not
     * support equivalence queries, the method returns an empty set in case
     * there is a cycle like e.g. the following: - school subConceptOf place -
     * university subConceptOf school - place subConceptOf university - DL: the
     * method returns all direct superconcepts, except the equivalent concepts!
     * 
     * @return a set containing all direct superconcepts of the given concept
     */
    public Set<Concept> getDirectSuperConcepts(Concept concept);

    /**
     * @return a set containing all instances of a given concept
     */
    public Set<Instance> getInstances(Concept concept);

    /**
     * @return a set containing all concepts of a given instance
     */
    public Set<Concept> getConcepts(Instance instance);

    /**
     * @return a set containing all concepts from the registered ontology
     */
    public Set<Concept> getAllConcepts();

    /**
     * @return a set containing all instances from the registered ontology
     */
    public Set<Instance> getAllInstances();

    /**
     * Please note that this method returns in Datalog only attributes that: -
     * are explicitly defined as inferring or constraining attributes - have
     * been assigned a value
     * 
     * The DL reasoner also returns attributes that have not been defined
     * explicitly and that have no values assigned to.
     * 
     * @return a set containing all attributes from the registered ontology
     */
    public Set<IRI> getAllAttributes();

    /**
     * @return a set containing all constraining attributes from the registered
     *         ontology
     */
    public Set<IRI> getAllConstraintAttributes();

    /**
     * @return a set containing all infering attributes from the registered
     *         ontology
     */
    public Set<IRI> getAllInferenceAttributes();

    /**
     * This method is not supported at the Datalog Reasoners!
     * 
     * @return a set containing all concepts equivalent to the given concept
     */
    public Set<Concept> getEquivalentConcepts(Concept concept);

    /**
     * This method is not supported at the Datalog Reasoners!
     * 
     * @return true if the two given concepts are equivalent, false otherwise
     */
    public boolean isEquivalentConcept(Concept concept1, Concept concept2);

    /**
     * Please note that this method returns all direct concepts of a given
     * instance, except for direct concepts that are equivalent to indirect
     * concepts.
     * 
     * @return a set with all direct concepts of a given instance.
     */
    public Set<Concept> getDirectConcepts(Instance instance);

    /**
     * This method is not supported at the Datalog Reasoners!
     * 
     * @return a set containing all identifiers of subrelations of a given
     *         relation
     */
    public Set<IRI> getSubRelations(Identifier attributeId);

    /**
     * This method is not supported at the Datalog Reasoners!
     * 
     * @return a set containing all identifiers of direct subrelations of a
     *         given relation
     */
    public Set<IRI> getDirectSubRelations(Identifier attributeId);

    /**
     * This method is not supported at the Datalog Reasoners!
     * 
     * @return a set containing all identifiers of superrelations of a given
     *         relation
     */
    public Set<IRI> getSuperRelations(Identifier attributeId);

    /**
     * This method is not supported at the Datalog Reasoners!
     * 
     * @return a set containing all identifiers of direct superrelations of a
     *         given relation
     */
    public Set<IRI> getDirectSuperRelations(Identifier attributeId);

    /**
     * This method is not supported at the Datalog Reasoners!
     * 
     * @return a set containing the identifiers of all relations/attributes
     *         equivalent to the given relation/attribute
     */
    public Set<IRI> getEquivalentRelations(Identifier attributeId);

    /**
     * This method is not supported at the Datalog Reasoners!
     * 
     * @return a set containing the identifiers of all relations/attributes
     *         inverse to the given relation/attribute
     */
    public Set<IRI> getInverseRelations(Identifier attributeId);

    /**
     * @return a set containing the concepts of the given attribute
     */
    public Set<Concept> getConceptsOf(Identifier attributeId);

    /**
     * @return a set containing the ranges of the given infering attribute
     */
    public Set<Type> getRangesOfInferingAttribute(Identifier attributeId);

    /**
     * @return a set containing the ranges of the given constraint attribute
     */
    public Set<DataType> getRangesOfConstraintAttribute(Identifier attributeId);

    /**
     * @return a map containing all infering attributes of a specified instance
     *         and for each a set containing all its values
     */
    public Map<IRI, Set<Term>> getInferingAttributeValues(Instance instance);

    /**
     * @return a map containing all constraint attributes of a specified
     *         instance and for each a set containing all its values
     */
    public Map<IRI, Set<DataValue>> getConstraintAttributeValues(Instance instance);

    /**
     * @return a map containing all instances who have values for a specified
     *         infering attribute and for each a set containing all its values
     */
    public Map<Instance, Set<Term>> getInferingAttributeInstances(Identifier attributeId);

    /**
     * @return a map containing all instances who have values for a specified
     *         constraint attribute and for each a set containing all its values
     */
    public Map<Instance, Set<DataValue>> getConstraintAttributeInstances(Identifier attributeId);

    /**
     * @return set containing instance values of the given instance and infering
     *         attribute
     */
    public Set<Term> getInferingAttributeValues(Instance subject, Identifier attributeId);

    /**
     * @return set containing data values of the given instance and constraint
     *         attribute
     */
    public Set<DataValue> getConstraintAttributeValues(Instance subject, Identifier attributeId);
}