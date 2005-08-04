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

package org.deri.wsml.reasoner.wsmlcore.datalog;

import java.util.*;
import org.deri.wsml.reasoner.api.queryanswering.*;

/**
 * Represents the result of a query against a Datalog knowledge base (or
 * a datalog program respectively)
 * 
 * A answer to a (datalog) query is considered to be a list of variable bindings, i.e.
 * maps from variables to constants.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class QueryResult {
        
    /** the variable bindings that make up the query answer. The Map takes varnames as keys and strings as values */
    private Set<VariableBinding> varBindings;
    
    /** the query to which this result refers to */
    private Query query;
    
    public QueryResult(Query q){
        // Create an empty variable binding.
        varBindings = new LinkedHashSet<VariableBinding>();
        query = q;
    }
    
    /** returns the variable bindings that the query answer consists of */
    public Set<VariableBinding> getVariableBindings(){
        return varBindings;
    }

    /**
     * @return Returns the query.
     */
    public Query getQuery() {
        return query;
    }

    
    
    
    
    
}
