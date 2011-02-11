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

package org.wsml.reasoner.impl;

import java.util.HashSet;
import java.util.Set;

import org.omwg.ontology.Variable;
import org.wsml.reasoner.transformation.ConstructedTermVisitor;

/**
 * Collects all variables in the given term.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class TermVariableVisitor extends ConstructedTermVisitor {

    private Set<Variable> freeVars;

    public TermVariableVisitor() {
        super();
        reset();
    }

    /**
     * Resets the internal state of the visitor such that the object can be
     * reused across several calls in the same thread.
     * 
     */
    public void reset() {
        freeVars = new HashSet<Variable>();
    }

    /**
     * Not used!
     * 
     * @return null.
     */
    public Object getSerializedObject() {
        return null;
    }

    /**
     * Returns the set of all variables in the visited term.
     * 
     * @return FreeVar(t)
     */
    public Set<Variable> getVariables() {
        return freeVars;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wsml.reasoner.normalization.PostfixOrderTermVisitor#handleVariable(org.omwg.ontology.Variable)
     */
    @Override
    public void visit(Variable arg0) {
        freeVars.add(arg0);
    }

}
