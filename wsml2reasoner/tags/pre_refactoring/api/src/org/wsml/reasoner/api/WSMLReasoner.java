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
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;

/**
 * An interface for invoking a WSML reasoner with a particular reasoning task.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface WSMLReasoner {
	
	/**
	 * Register all given ontologies with the reasoner.
	 * @param ontologies The ontologies to register in the same reasoning space.
	 * @throws InconsistencyException
	 */
    public void registerOntologies(Set<Ontology> ontologies)
            throws InconsistencyException;

    /**
     * Registers the ontology with the reasoner.
     * All currently registered contents of the reasoner are removed and replaced
     * with the elements of the new ontology.
     * 
     * @param ontology The ontology to register
     */
    public void registerOntology(Ontology ontology)
            throws InconsistencyException;

    public void registerEntitiesNoVerification(Set<Entity> ontologies);

    /**
     * Register some entities and do a consistency check.
     * @param theEntities The entities to register.
     * @throws InconsistencyException If a consistency violation is detected.
     */
    public void registerEntities(Set<Entity> theEntities) throws InconsistencyException;

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
    public void deRegister();

    /**
     * This method checks for query containment, i.e. it checks 
     * for whether one query is contained within another query. The 
     * query containment is checked using the 'Frozen Facts' algorithm
     * (This algorithm is presented in Ramakrishnan, R., Y. Sagiv, 
     * J. D. Ullman and M. Y. Vardi (1989). Proof-Tree Transformation 
     * Theorems and their Applications. 8th ACM Symposium on Principles 
     * of Database Systems, pp. 172 - 181, Philadelphia) within the 
     * reasoning engine IRIS.
     * </p>
     * <p>
     * The query containment check can only be performed over positive 
     * queries that do not contain built-ins and disjunctions.
     * </p>
     * <p>
     * Example: <br />
     * In the following Query1 is contained within Query2:<br />
     * Program: vehicle(?x) :- car(?x).<br />
     * Query1: car(?x).<br />
     * Query2: vehicle(?x).<br />
     * </p>
     * 
     * @param query1
     *            the query that may be contained within query2.
     * @param query2
     * 			  the query that may contain query1.
     * @param ontologyID
     *            the orginal logical ontology URI
     * @return true if query1 is contained within query2, false otherwise.
     */
    public boolean checkQueryContainment(LogicalExpression query1, LogicalExpression query2);
    
    /**
     * Check whether query1 is contained within query2 and return the 
     * resulting variable mapping.
     * 
     * @param query1
     *            the query that may be contained within query2.
     * @param query2
     * 			  the query that may contain query1.
     * @param ontologyID
     *            the orginal logical ontology URI
     * @return Set containing the resulting variable mapping, mapping 
     * 			  variables to terms
     * @see WSMLReasoner#checkQueryContainment(LogicalExpression, 
     * 			  LogicalExpression, IRI)
     */
    public Set<Map<Variable, Term>> getQueryContainment(LogicalExpression query1, LogicalExpression query2);
    
    public boolean executeGroundQuery(LogicalExpression query);

    public Set<Map<Variable, Term>> executeQuery(LogicalExpression query);
    
    public boolean entails(LogicalExpression expression);

    public boolean entails(Set<LogicalExpression> expressions);
    
    public boolean isSatisfiable();

    /**
     * @return a set of violation objects, or an empty set, if the ontology is
     *         consistent (satisfiable)
     */
    public Set<ConsistencyViolation> checkConsistency();

    /**
     * @return true if 'subConcept' is a subconcept of 'superConcept', false otherwise
     */
    public boolean isSubConceptOf(Concept subConcept, Concept superConcept);

    /**
     * @return true if 'instance' is a member of 'concept', false otherwise
     */
    public boolean isMemberOf(Instance instance, Concept concept);

    /**
     * Please note that the results of this query differ depending on whether 
     * a Datalog or a DL reasoner is used: 
     * - Datalog: the method returns all subconcepts, including equivalent 
     * 			  concepts!
     * - DL: the method returns all subconcepts, except the equivalent 
     * 		 concepts!
     * 
     * @return a set containing all subconcepts of the given concept
     */
    public Set<Concept> getSubConcepts(Concept concept);
    
    /**
     * Please note that the results of this query differ depending on whether 
     * a Datalog or a DL reasoner is used: 
     * - Datalog: because Datalog does not support equivalence queries, the 
     * 			  method returns an empty set in case there is a cycle like 
     * 			  e.g. the following:
     * 			  - school subConceptOf place
     * 			  - university subConceptOf school
     * 			  - place subConceptOf university
     * - DL: the method returns all direct subconcepts, except the equivalent 
     * 		 concepts!
     * 
     * @return a set containing all direct subconcepts of the given concept
     */
    public Set<Concept> getDirectSubConcepts(Concept concept);

    /**
     * Please note that the results of this query differ depending on whether 
     * a Datalog or a DL reasoner is used: 
     * - Datalog: the method returns all superconcepts, including equivalent 
     * 			  concepts!
     * - DL: the method returns all superconcepts, except the equivalent 
     * 		 concepts!
     * 
     * @return a set containing all superconcepts of the given concept
     */
    public Set<Concept> getSuperConcepts(Concept concept);
    
    /**
     * Please note that the results of this query differ depending on whether 
     * a Datalog or a DL reasoner is used: 
     * - Datalog: because Datalog does not support equivalence queries, the 
     * 			  method returns an empty set in case there is a cycle like 
     * 			  e.g. the following:
     * 			  - school subConceptOf place
     * 			  - university subConceptOf school
     * 			  - place subConceptOf university
     * - DL: the method returns all direct superconcepts, except the equivalent 
     * 		 concepts!
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
	 * @return a set containing all constraining attributes from the registered ontology
	 */
	public Set<IRI> getAllConstraintAttributes();
	
	/**
	 * @return a set containing all infering attributes from the registered ontology
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
	 * @return a set containing all identifiers of subrelations of a given relation
	 */
	public Set<IRI> getSubRelations(Identifier attributeId);
	
	/**
	 * This method is not supported at the Datalog Reasoners!
	 * 
	 * @return a set containing all identifiers of direct subrelations of a given relation
	 */
	public Set<IRI> getDirectSubRelations(Identifier attributeId);
	
	/**
	 * This method is not supported at the Datalog Reasoners!
	 * 
	 * @return a set containing all identifiers of superrelations of a given relation
	 */
	public Set<IRI> getSuperRelations(Identifier attributeId);
	
	/**
	 * This method is not supported at the Datalog Reasoners!
	 * 
	 * @return a set containing all identifiers of direct superrelations of a given relation
	 */
	public Set<IRI> getDirectSuperRelations(Identifier attributeId);
	
	/**
	 * This method is not supported at the Datalog Reasoners!
	 * 
	 * @return a set containing the identifiers of all relations/attributes 
	 * 			equivalent to the given relation/attribute
	 */
	public Set<IRI> getEquivalentRelations(Identifier attributeId);
	
	/**
	 * This method is not supported at the Datalog Reasoners!
	 * 
	 * @return a set containing the identifiers of all relations/attributes 
	 * 			inverse to the given relation/attribute
	 */
	public Set<IRI> getInverseRelations(Identifier attributeId);
	
	/**
	 * @return a set containing the concepts of the given attribute
	 */
	public Set<Concept> getConceptsOf(Identifier attributeId);
	
	/**
	 * @return a set containing the ranges of the given infering attribute
	 */
	public Set<IRI> getRangesOfInferingAttribute(Identifier attributeId);
	
	/**
	 * @return a set containing the ranges of the given constraint attribute
	 */
	public Set<IRI> getRangesOfConstraintAttribute(Identifier attributeId);
	
	/**
	 * @return a map containing all infering attributes of a specified instance 
	 * 			and for each a set containing all its values
	 */
	public Map<IRI, Set<Term>> getInferingAttributeValues(Instance instance);
	
	/**
	 * @return a map containing all constraint attributes of a specified instance 
	 *			and for each a set containing all its values
	 */
	public Map<IRI, Set<Term>> getConstraintAttributeValues(Instance instance);
	
	/**
	 * @return a map containing all instances who have values for a specified 
	 * 			infering attribute and for each a set containing all its values
	 */
	public Map<Instance, Set<Term>> getInferingAttributeInstances(Identifier attributeId);
	
	/**
	 * @return a map containing all instances who have values for a specified 
	 * 			constraint attribute and for each a set containing all its values
	 */
	public Map<Instance, Set<Term>> getConstraintAttributeInstances(Identifier attributeId);
	
	/**
	 * @return set containing instance values of the given instance and 
	 * 			infering attribute
	 */
	public Set getInferingAttributeValues(Instance subject, Identifier attributeId);
	
	/**
	 * @return set containing data values of the given instance and 
	 * 			constraint attribute
	 */
	public Set getConstraintAttributeValues(Instance subject, Identifier attributeId);

	
	/**@deprecated*/
	public void deRegisterOntology(IRI ontologyID);

	
	/**@deprecated*/
	public void deRegisterOntology(Set<IRI> ontologyIDs);

	
	/**@deprecated*/
	public boolean checkQueryContainment(LogicalExpression query1, 
	    		LogicalExpression query2, IRI ontologyID);
	
	
	/**@deprecated*/
    public Set<Map<Variable, Term>> getQueryContainment(LogicalExpression 
    		query1, LogicalExpression query2, IRI ontologyID);
    
    
    /**@deprecated*/
    public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query);

    
    /**@deprecated*/
    public Set<Map<Variable, Term>> executeQuery(IRI ontologyID,
            LogicalExpression query);
    
    
    /**@deprecated*/
    public boolean entails(IRI ontologyID, LogicalExpression expression);

    
    /**@deprecated*/
    public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions);
    
    
    /**@deprecated*/
    public boolean isSatisfiable(IRI ontologyID);

    
    /**@deprecated*/
    public Set<ConsistencyViolation> checkConsistency(IRI ontologyID);

    /**@deprecated*/
    public boolean isSubConceptOf(IRI ontologyID, Concept subConcept,
            Concept superConcept);

    
    /**@deprecated*/
    public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept);
    
    
    /**@deprecated*/
    public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept);
    
    
    /**@deprecated*/
    public Set<Concept> getDirectSubConcepts(IRI ontologyID, Concept concept);

    
    /**@deprecated*/
    public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept);
   
    
    /**@deprecated*/
    public Set<Concept> getDirectSuperConcepts(IRI ontologyID, Concept concept);

    
    /**@deprecated*/
    public Set<Instance> getInstances(IRI ontologyID, Concept concept);


    /**@deprecated*/
    public Set<Concept> getConcepts(IRI ontologyID, Instance instance);
    
   
    /**@deprecated*/
	public Set<Concept> getAllConcepts(IRI ontologyID);
	
	
	/**@deprecated*/
	public Set<Instance> getAllInstances(IRI ontologyID);
	
	
	/**@deprecated*/
	public Set<IRI> getAllAttributes(IRI ontologyID);
	
	
	/**@deprecated*/
	public Set<IRI> getAllConstraintAttributes(IRI ontologyID);
	
	
	/**@deprecated*/
	public Set<IRI> getAllInferenceAttributes(IRI ontologyID);

	
	/**@deprecated*/
	public Set<Concept> getEquivalentConcepts(IRI ontologyID, Concept concept);
	
	
	/**@deprecated*/
	public boolean isEquivalentConcept(IRI ontologyID, Concept concept1, 
			Concept concept2);
	
	
	/**@deprecated*/
	public Set<Concept> getDirectConcepts(IRI ontologyID, Instance instance);
	
	
	/**@deprecated*/
	public Set<IRI> getSubRelations(IRI ontologyID, Identifier attributeId);
	
	
	/**@deprecated*/
	public Set<IRI> getDirectSubRelations(IRI ontologyID, Identifier attributeId);
	
	
	/**@deprecated*/
	public Set<IRI> getSuperRelations(IRI ontologyID, Identifier attributeId);
	
	
	/**@deprecated*/
	public Set<IRI> getDirectSuperRelations(IRI ontologyID, Identifier attributeId);
	
	
	/**@deprecated*/
	public Set<IRI> getEquivalentRelations(IRI ontologyID, 
			Identifier attributeId);
	
	
	/**@deprecated*/
	public Set<IRI> getInverseRelations(IRI ontologyID, Identifier attributeId);
	
	
	/**@deprecated*/
	public Set<Concept> getConceptsOf(IRI ontologyID, Identifier attributeId);
	
	
	/**@deprecated*/
	public Set<IRI> getRangesOfInferingAttribute(IRI ontologyID, 
			Identifier attributeId);
	
	
	/**@deprecated*/
	public Set<IRI> getRangesOfConstraintAttribute(IRI ontologyID, 
			Identifier attributeId);
	
	
	/**@deprecated*/
	public Map<IRI, Set<Term>> getInferingAttributeValues(IRI ontologyID, 
			Instance instance);
	
	
	/**@deprecated*/
	public Map<IRI, Set<Term>> getConstraintAttributeValues(IRI ontologyID, 
			Instance instance);
	
	
	/**@deprecated*/
	public Map<Instance, Set<Term>> getInferingAttributeInstances(
			IRI ontologyID, Identifier attributeId);
	
	
	/**@deprecated*/
	public Map<Instance, Set<Term>> getConstraintAttributeInstances(
			IRI ontologyID, Identifier attributeId);
	
	
	/**@deprecated*/
	public Set getInferingAttributeValues(IRI ontologyID, 
			Instance subject, Identifier attributeId);

	
	/**@deprecated*/
	public Set getConstraintAttributeValues(IRI ontologyID, 
			Instance subject, Identifier attributeId);
		
}