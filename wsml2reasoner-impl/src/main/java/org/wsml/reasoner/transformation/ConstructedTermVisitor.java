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

package org.wsml.reasoner.transformation;

import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.TermVisitor;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.wsmo.common.IRI;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;

/**
 * A simple stub implementation of the term Visitor interface. Real
 * functionality should be added in subclasses.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class ConstructedTermVisitor implements TermVisitor {

    public void visit(ConstructedTerm arg0) {
        for (int i = 0; i < arg0.getArity(); i++) {
            arg0.getParameter(i).accept(this);
        }
    }

    public void visit(Variable arg0) {
    }

    public void visit(SimpleDataValue arg0) {
    }

    public void visit(ComplexDataValue arg0) {
    }

    public void visit(UnnumberedAnonymousID arg0) {
    }

    public void visit(NumberedAnonymousID arg0) {
    }

    public void visit(IRI arg0) {
    }
}
