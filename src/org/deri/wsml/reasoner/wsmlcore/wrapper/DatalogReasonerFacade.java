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

package org.deri.wsml.reasoner.wsmlcore.wrapper;

import org.deri.wsml.reasoner.wsmlcore.datalog.*;

/**
 * This interface represents a facade to various datalog engines that allows to
 * perform a query answering request, e.g. DLV, KAON2.
 * 
 * For each such system a specific facade must be implemented to integrate the
 * component into the system.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public interface DatalogReasonerFacade {

    /**
     * Registers the Datalog knowledge base representing the ontology at the external reasoner 
     * @param ontologyURI the orginal logical ontology URI
     * @param kb the knowledge base describing the ontology
     * @throws ExternalToolException if some exception happens during ontology registration
     */
    public void register(String ontologyURI, Program kb) throws ExternalToolException;

    /**
     * Evaluates a given query on a particular external tool.
     * 
     * @param q
     *            the query to be evaluated. The query contains a reference to
     *            the knowledgebase against which the query is posed.
     * @return an object that represents the query result, i.e. a list of
     *         variable bindings for the query.
     * @throws ExternalToolException
     *             in case that some error occurs during the execution of the
     *             query
     */
    public QueryResult evaluate(ConjunctiveQuery q, String ontologyURI)
            throws ExternalToolException;

    /**
     * Sets the SymbolFactory that is used during evaluation for mapping symbols
     * between datalog representation and the syntax that is allowed by the
     * particular external tool.
     * 
     * @param sf -
     *            the symbol factory to be used which delivers valid symbols for
     *            the tool.
     */
    public void useSymbolFactory(SymbolFactory sf);

}
