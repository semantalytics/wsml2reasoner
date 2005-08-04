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

package org.deri.wsml.reasoner.impl;

import java.util.Set;

import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.ontology.Ontology;

public class QueryAnsweringRequestImpl implements QueryAnsweringRequest {

    private LogicalExpression theQueryExpression;
    private QueryAnsweringMode mode;
    private long maxNumberOfResults;
    private Set<Ontology> ontologies;
    
    
    
    /**
     * Creates a query answering request object that refers to a set of ontologies
     * as the respective knowledgebase and a logical expression that describes the 
     * objects that are to be retrieved in the knowledgebase. 
     * 
     * @param ontologies - the set of ontologies to which the query refers to
     * @param expression - the logical expression that describes the properties of the
     *                     desired answers
     */
    
    public QueryAnsweringRequestImpl(Set<Ontology> ontologies, LogicalExpression expression) {
        this.ontologies = ontologies;
        theQueryExpression = expression;
        
        mode = QueryAnsweringMode.FIND_ALL_RESULTS;
        maxNumberOfResults = -1;
    }

    public LogicalExpression getQuery() {
        return theQueryExpression;
    }

    public QueryAnsweringMode getMode() {
        return mode;
    }

    public long getMaxNumberOfResults() {
        return maxNumberOfResults;
    }

    public Set<Ontology> getOntologies() {
        return ontologies;
    }
    
}
