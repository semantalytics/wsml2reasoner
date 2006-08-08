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

import org.mindswap.pellet.query.QueryResults;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.OWLOntology;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;


/**
 * An interface for invoking a WSML-DL reasoner with a particular reasoning task.
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 */
public interface WSMLDLReasoner extends WSMLReasoner{
	
	/**
	 * The method supports the following logical expressions as they are 
	 * allowed in formulae in WSML-DL:
	 * - MembershipMolecule
	 * - Conjunction
	 * - Disjunction
	 * - Negation
	 * - UniversalQuantification
	 * - ExistentialQuantification
	 * 
	 * @return true if the given expression is satisfiable, false otherwise
	 * @throws InternalReasonerException if a logical expression different
	 * 			than the ones mentionned above are given as input
	 */
	public boolean isConsistent(IRI ontologyID, LogicalExpression logExpr);
	
	/**
	 * @return true if the given concept is satisfiable, false otherwise
	 */
	public boolean isConsistent(IRI ontologyID, Concept concept);
	
	/**
	 * @return a set containing all instances from the registered ontology
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
	 * @return a set containing all identifiers subrelations of a given relation
	 */
	public Set<IRI> getSubRelations(IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return a set containing all identifiers of superrelations of a given relation
	 */
	public Set<IRI> getSuperRelations(IRI ontologyID, Identifier attributeId);
	
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
	public Map<IRI, Set<IRI>> getConstraintAttributeValues(IRI ontologyID, 
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
	public Map<Instance, Set<IRI>> getConstraintAttributeInstances(
			IRI ontologyID, Identifier attributeId);
	
	/**
	 * @return true if the given subject instance has an attribute value of the 
	 * 			given infering attribute with the given object instance as value
	 */
	public boolean instanceHasInferingAttributeValue(IRI ontologyID, 
			Instance subject, Identifier attributeId, Instance object);
	
	/**
	 * @return true if the given subject instance has an attribute value of the 
	 * 			given constraint attribute with the given object data value as value
	 */
	public boolean instanceHasConstraintAttributeValue(IRI ontologyID, 
			Instance subject, Identifier attributeId, DataValue object);
	
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
	
	/**
	 * Prints a class tree from the registered ontology.
	 */
	public void printClassTree(IRI ontologyID);
	
	/**
     * Returns information about the registered ontology. Among these information 
     * are the expressivity, the number of concepts, attributes and instances.
     * 
     * @return String containing information about the registered ontology
     */
	public String getInfo(IRI ontologyID);
	
	/**
     * Evaluates a given query on a particular external tool. This method is not 
     * supported yet!
     * 
     * @param query the query to be evaluated.
     * @return a set of Query Results
     * @throws UnsupportedOperationException 
     */
	public QueryResults executeQuery(IRI ontologyID, String query);
	
	/**
	 * Serializes a given OWL ontology to OWL abstract syntax.
	 * 
	 * @param owlOntology to be serialized
	 * @return String version of OWL ontology
	 */
	public String serialize2OWLAbstractSyntax(OWLOntology owlOntology) 
			throws RendererException;
	
	/**
	 * Serializes a given OWL ontology to XML-RDF syntax.
	 * 
	 * @param owlOntology to be serialized
	 * @return String version of OWL ontology
	 */
	public String serialize2OWLRDFSyntax(OWLOntology owlOntology);
	
	/**
	 * This method allows to extract the OWL ontology as string, even if the OWL 
	 * ontology is not valid owl dl. The serialized ontology could be validated at 
	 * an online validator as e.g. "http://phoebus.cs.man.ac.uk:9999/OWL/Validator"
	 * 
	 * @param ontology WSML DL ontology to be transformed to owl
	 * @return string version of transformed OWL ontology
	 */
	public String serializeWSML2OWL(Ontology ontology);
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/07/21 16:25:21  nathalie
 * completing the pellet reasoner integration
 *
 * Revision 1.2  2006/07/20 17:50:23  nathalie
 * integration of the pellet reasoner
 *
 *
 */