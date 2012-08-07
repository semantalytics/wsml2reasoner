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

package org.wsml.reasoner;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;

/**
 * This interface represents a facade to various streaming datalog engines that
 * allows to perform a query answering request, e.g. Streaming IRIS.
 * 
 * For each such system a specific facade must be implemented to integrate the
 * component into the system.
 * 
 * @author Norbert Lanzanasto, STI Innsbruck
 */
public interface StreamingDatalogReasonerFacade {

	/**
	 * Registers the ontology at the external reasoner
	 * 
	 * @param ontology
	 *            the ontology
	 * @param configuration
	 *            the configuration for the reasoner
	 * @throws ExternalToolException
	 */
	public void startReasoner(Ontology ontology,
			Map<String, Object> configuration) throws ExternalToolException;

	/**
	 * Shuts down the reasoner and all corresponding threads.
	 */
	public void shutdownReasoner();

	/**
	 * Registers the Datalog knowledge base representing the ontology at the
	 * external reasoner (deleting previously registered knowledge bases).
	 * 
	 * @param ontologyURI
	 *            the orginal logical ontology URI
	 * @param kb
	 *            the knowledge base describing the ontology
	 * @throws ExternalToolException
	 *             if some exception happens during ontology registration
	 */
	public void register(Set<Rule> kb) throws ExternalToolException;

	/**
	 * Removes the ontology from the external reasoner
	 * 
	 * @param ontologyURI
	 *            the original logical ontology URI
	 * @throws ExternalToolException
	 *             if exception happens during ontology removal
	 */
	public void deregister() throws ExternalToolException;

	/**
	 * Returns the port of the socket where the reasoner is listening for input.
	 * 
	 * @return The port of the input socket.
	 */
	public int getInputPort();

	/**
	 * Adds facts to the knowledge base.
	 * 
	 * @param The
	 *            facts.
	 */
	public void addFacts(Set<Rule> facts);

	/**
	 * Adds facts from this ontology to the knowledge base.
	 * 
	 * @param ontology
	 *            The ontology.
	 */
	public void addFacts(Ontology ontology);

	/**
	 * Evaluates a given query on a particular external tool.
	 * 
	 * @param q
	 *            the query to be evaluated.
	 * @param ontologyURI
	 *            the orginal logical ontology URI
	 * @return a set of variable bindings (map with variables as keys, and the
	 *         bindings: IRIs or DataValues as values)
	 * @throws ExternalToolException
	 *             in case that some error occurs during the execution of the
	 *             query
	 */
	public Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q)
			throws ExternalToolException;

	/**
	 * Register a query and a listener.
	 * 
	 * @param datalogQuery
	 *            The datalog query.
	 * @param query
	 *            The WSML query.
	 * @param host
	 *            The host of the listener.
	 * @param port
	 *            The port of the listener.
	 */
	public void registerQueryListener(ConjunctiveQuery datalogQuery,
			LogicalExpression query, String host, int port);

	/**
	 * Deregister a query and a listener.
	 * 
	 * @param datalogQuery
	 *            The datalog query.
	 * @param host
	 *            The host of the listener.
	 * @param port
	 *            The port of the listener.
	 */
	public void deregisterQueryListener(ConjunctiveQuery datalogQuery,
			String host, int port);

	/**
	 * Checks whether query1 is contained within query2.
	 * 
	 * @param query1
	 *            the query that may be contained within query2.
	 * @param query2
	 *            the query that may contain query1.
	 * @param ontologyURI
	 *            the orginal logical ontology URI
	 * @return true when query1 is contained within query2, false otherwise.
	 */
	public boolean checkQueryContainment(ConjunctiveQuery query1,
			ConjunctiveQuery query2);

	/**
	 * Checks whether query1 is contained within query2 and returns the
	 * resulting variable mapping
	 * 
	 * @param query1
	 *            the query that may be contained within query2.
	 * @param query2
	 *            the query that may contain query1.
	 * @param ontologyURI
	 *            the orginal logical ontology URI
	 * @return a set of variable bindings (map with variables as keys, and the
	 *         bindings: IRIs or DataValues as values)
	 * @throws ExternalToolException
	 */
	public Set<Map<Variable, Term>> getQueryContainment(
			ConjunctiveQuery query1, ConjunctiveQuery query2)
			throws ExternalToolException;

}
