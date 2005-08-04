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

/**
 * 
 * Represents a term that occurs in a rule.
 * 
 * In a sense these classes in this package are very similar to WSML
 * logical expressions, nevertheless they provide functionality that
 * is more specific to an Logic Programming Framework than the classes
 * that are currently part of the WSMO4j Logical Exrpression API.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public abstract class Term {
    
    protected String symb;
        
    public String getSymbol(){
        return symb;
    }
    
    /**
     * @return the set of variables that are contained in this term in the order
     * that they occur in the term.
     */
    abstract List<Variable> getVariables();

}
