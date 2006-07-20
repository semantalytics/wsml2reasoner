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

import java.util.Set;

import org.mindswap.pellet.query.QueryResults;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;


/**
 * An interface for invoking a WSML-DL reasoner with a particular reasoning task.
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 */
public interface WSMLDLReasoner extends WSMLReasoner{
	
	/**
	 * @return true if the given expression is satisfiable, false otherwise
	 */
	public boolean isConsistent(LogicalExpression logExpr);
	
	/**
	 * @return true if the given concept is satisfiable, false otherwise
	 */
	public boolean isConsistent(Concept concept);
	
	/**
	 * @return a set containing all instances from the registered ontology
	 */
	public Set<Concept> getAllConcepts();
	
	/**
	 * @return a set containing all instances from the registered ontology
	 */
	public Set<Instance> getAllInstances();
	
	/**
	 * @return a set containing all attributes from the registered ontology
	 */
	public Set<IRI> getAllAttributes();
	
	/**
	 * @return a set containing all concepts equivalent to the given concept
	 */
	public Set<Concept> getEquivalentConcepts(Concept concept);
	
	/**
	 * @return true if the two given concepts are equivalent, false otherwise
	 */
	public boolean isEquivalentConcept(Concept concept1, Concept concept2);
	
	/**
	 * @return a set containing all subrelations of a given relation
	 */
	public Set<IRI> getSubRelations(Identifier attributeId);
	
	/**
	 * @return a set containing all superrelations of a given relation
	 */
	public Set<IRI> getSuperRelations(Identifier attributeId);
	
	/**
	 * @return a set containing the identifiers of all attributes 
	 * 			equivalent to the given attribute
	 */
	public Set<IRI> getEquivalentAttributes(Identifier attributeId);
	
	/**
	 * Prints a class tree from the registered ontology.
	 */
	public void printClassTree();
	
	/**
     * Returns information about the registered ontology. Among these information 
     * are the expressivity, the number of concepts, attributes and instances.
     * 
     * @return String containing information about the registered ontology
     */
	public String getInfo();
	
	/**
     * Evaluates a given query on a particular external tool. This method is not 
     * supported yet!
     * 
     * @param query the query to be evaluated.
     * @return a set of Query Results
     * @throws UnsupportedOperationException 
     */
	public QueryResults executeQuery(String query);
	
}
/*
 * $Log: not supported by cvs2svn $
 *
 */