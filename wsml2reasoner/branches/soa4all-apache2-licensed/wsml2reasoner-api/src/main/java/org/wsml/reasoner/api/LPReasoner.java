/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wsml.reasoner.api;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;

/**
 * An interface for invoking a WSML-LP reasoner with a particular reasoning
 * task.
 * 
 * @author grahen
 * 
 */
public interface LPReasoner extends WSMLReasoner {

    /**
     * Check the consistency of the reasoner instance. 
     * 
     * TODO wanwin does this really check the consistency of the reasoner 
     * 
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
     * within the reasoning engine IRIS.
     * </p>
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
     * @return true if query1 is contained within query2, false otherwise.
     */
    public boolean checkQueryContainment(LogicalExpression query1, LogicalExpression query2);

    /**
     * Check whether query1 is contained within query2 and return the resulting
     * variable mapping.
     * 
     * @param query1
     *            the query that may be contained within query2.
     * @param query2
     *            the query that may contain query1.
     * @return Set containing the resulting variable mapping, mapping variables
     *         to terms
     */
    public Set<Map<Variable, Term>> getQueryContainment(LogicalExpression query1, LogicalExpression query2);

    /**
     * Execute a query.
     * 
     * @param query A WSML logical expression with or without variables.
     * @return The variable bindings.
     */
    public Set<Map<Variable, Term>> executeQuery(LogicalExpression query);

    /**
     * Ask if a given logical expression is satisfied.
     * The given query is executed and if one or more results are returned then the method returns true.
     * 
     * @param query The logical expression to test. 
     * @return true, if the logical expression can be satisfied.
     */
    public boolean ask(LogicalExpression query);
}
