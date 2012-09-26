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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsmo.common.IRI;

/**
 * An interface for invoking a WSML-DL reasoner with a particular reasoning
 * task.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public interface StreamingLPReasoner extends WSMLReasoner {

	/**
	 * Registers the ontologies at the external reasoner and starts the
	 * reasoner.
	 * 
	 * @param ontology
	 *            the ontology
	 * @param configuration
	 *            the configuration for the reasoner
	 */
	public void startReasoner(List<Ontology> ontologies,
			Map<String, Object> configuration);

	/**
	 * Shuts down the reasoner and all corresponding threads.
	 * 
	 * @throws ExternalToolException
	 *             If the reasoner is already shut down.
	 */
	public void shutdownReasoner();

	/**
	 * Returns the port of the socket where the reasoner is listening for input.
	 * 
	 * @return The port of the input socket.
	 * @throws ExternalToolException
	 *             If the reasoner is not running.
	 */
	public int getInputPort();

	/**
	 * @return a set of violation objects, or an empty set, if the ontology is
	 *         consistent (satisfiable)
	 */
	public Set<ConsistencyViolation> checkConsistency();

	/**
	 * This method checks for query containment, i.e. it checks for whether one
	 * query is contained within another query. The query containment is checked
	 * using the 'Frozen Facts' algorithm (This algorithm is presented in
	 * Ramakrishnan, R., Y. Sagiv, J. D. Ullman and M. Y. Vardi (1989).
	 * Proof-Tree Transformation Theorems and their Applications. 8th ACM
	 * Symposium on Principles of Database Systems, pp. 172 - 181, Philadelphia)
	 * within the reasoning engine IRIS. </p>
	 * <p>
	 * The query containment check can only be performed over positive queries
	 * that do not contain built-ins and disjunctions.
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
	 *            the query that may contain query1.
	 * @param ontologyID
	 *            the orginal logical ontology URI
	 * @return true if query1 is contained within query2, false otherwise.
	 */
	public boolean checkQueryContainment(LogicalExpression query1,
			LogicalExpression query2);

	/**
	 * Check whether query1 is contained within query2 and return the resulting
	 * variable mapping.
	 * 
	 * @param query1
	 *            the query that may be contained within query2.
	 * @param query2
	 *            the query that may contain query1.
	 * @param ontologyID
	 *            the orginal logical ontology URI
	 * @return Set containing the resulting variable mapping, mapping variables
	 *         to terms
	 * @see WSMLReasoner#checkQueryContainment(LogicalExpression,
	 *      LogicalExpression, IRI)
	 */
	public Set<Map<Variable, Term>> getQueryContainment(
			LogicalExpression query1, LogicalExpression query2);

	/**
	 * Registers a query listener.
	 * 
	 * @param query
	 *            A WSML logical expression with or without variables.
	 * @param host
	 *            The host of the socket that listens for results.
	 * @param port
	 *            The port of the socket that listens for results.
	 */
	public void registerQueryListener(LogicalExpression query, String host,
			int port);

	/**
	 * Deregisters a query listener.
	 * 
	 * @param query
	 *            A WSML logical expression with or without variables.
	 * @param host
	 *            The host of the socket that listens for results.
	 * @param port
	 *            The port of the socket that listens for results.
	 */
	public void deregisterQueryListener(LogicalExpression query, String host,
			int port);

	/**
	 * Execute a query.
	 * 
	 * @param query
	 *            A WSML logical expression with or without variables.
	 * @return The variable bindings.
	 */
	public Set<Map<Variable, Term>> executeQuery(LogicalExpression query);

	/**
	 * Ask if a given logical expression is satisfied. The given query is
	 * executed and if one or more results are returned then the method returns
	 * true.
	 * 
	 * @param query
	 *            The logical expression to test.
	 * @return true, if the logical expression can be satisfied.
	 */
	public boolean ask(LogicalExpression query);
}
