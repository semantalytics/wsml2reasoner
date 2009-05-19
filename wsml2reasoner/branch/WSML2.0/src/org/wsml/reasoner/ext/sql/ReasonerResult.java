/**
 * Copyright (C) 2007 Digital Enterprise Research Institute (DERI), 
 * Leopold-Franzens-Universitaet Innsbruck, Technikerstrasse 21a, 
 * A-6020 Innsbruck. Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.wsml.reasoner.ext.sql;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

/**
 * A simple class to wrap up reasoner results.
 * 
 * @author Florian Fischer, florian.fischer@deri.at
 */
public class ReasonerResult {

    public ReasonerResult(Set<Map<Variable, Term>> resultSet) {
        result = resultSet;
    }

    public Set<Map<Variable, Term>> getResult() {
        return result;
    }
    
    public int size() {
    	return result.size();
    }

    protected Set<Map<Variable, Term>> result;
}
