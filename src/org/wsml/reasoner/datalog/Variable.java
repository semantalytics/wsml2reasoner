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

package org.wsml.reasoner.datalog;

import java.util.ArrayList;
import java.util.List;

public class Variable extends Term {

    public Variable(String varName) {
        this.symb = varName;
    }

    /**
     * @return the set of variables that are contained in this term
     */
    List<Variable> getVariables() {
        List<Variable> result = new ArrayList<Variable>();
        result.add(this);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        Variable v = (Variable) obj;
        return (symb == v.symb || (symb != null && symb.equals(v.symb)));
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == symb ? 0 : symb.hashCode());
        return hash;
    }

    public String toString() {
        String result = this.getSymbol();
        return result;
    }
}
