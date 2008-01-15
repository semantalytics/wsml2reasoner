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

package org.wsml.reasoner.impl;

import java.util.HashSet;
import java.util.Set;

import org.omwg.ontology.Variable;
import org.wsml.reasoner.transformation.TermVisitor;

/**
 * Collects all variables in the given term.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class TermVariableVisitor extends TermVisitor {

    private Set<Variable> freeVars;
    
    
    public TermVariableVisitor() {
        super();
        reset();
    }

    /**
     * Resets the internal state of the visitor such that the object
     * can be reused across several calls in the same thread.
     *
     */
    public void reset(){
        freeVars = new HashSet<Variable>();
    }
    
    /**
     * Not used!
     * @return null.
     */
    public Object getSerializedObject() {
        return null;
    }
    
    /**
     * Returns the set of all variables in the visited term.
     * @return FreeVar(t)
     */
    public Set<Variable> getVariables(){
        Set<Variable> result = freeVars; // not copied!
        return freeVars;
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderTermVisitor#handleVariable(org.omwg.ontology.Variable)
     */
    @Override
    public void visitVariable(Variable arg0) {
       freeVars.add(arg0);
    }
    
    
       

}
