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
package org.wsml.reasoner;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

/**
 * This interface represents a facade to various DL engines that allows to
 * perform a reasoning request, e.g. PELLET, KAON2.
 * 
 * For each such system a specific facade must be implemented to integrate the
 * component into the system.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/DLReasonerFacade.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2006-07-18 08:21:01 $
 */
public interface DLReasonerFacade {

	/**
     * Registers the DL knowledge base representing the ontology at the
     * external reasoner
     * 
     * @param ontologyURI the orginal logical ontology URI
     * @param kb the knowledge base describing the ontology
     * @throws ExternalToolException if some exception happens during ontology 
     *         registration
     */
    public void register(String ontologyURI, Set<Rule> kb)
            throws ExternalToolException;

    /**
     * Removes the ontology from the external reasoner
     * 
     * @param ontologyURI the original logical ontology URI
     * @throws ExternalToolException if exception happens during ontology removal
     */
    public void deregister(String ontologyURI) throws ExternalToolException;

    /**
     * Evaluates a given query on a particular external tool.
     * 
     * @param q the query to be evaluated.
     * @return a set of variable bindings (map with variables as keys, and the
     *         bindings: IRIs or DataValues as values)
     * @throws ExternalToolException in case that some error occurs during the 
     *         execution of the query
     */
    public Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q,
            String ontologyURI) throws ExternalToolException;
	
}
