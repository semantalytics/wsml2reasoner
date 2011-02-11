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
package org.wsml.reasoner.api.inconsistency;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;

public abstract class CardinalityViolation extends ConsistencyViolation {

    private Term term;

    private Attribute attribute;

    public Attribute getAttribute() {
        return attribute;
    }

    public Term getTerm() {
        return term;
    }

    public CardinalityViolation(Term term, Attribute attribute) {
        super();
        this.term = term;
        this.attribute = attribute;
    }

    public String toString() {
        return "Cardinality violation on attribute: " + attribute.getIdentifier() + " violating instance " + term.toString();
    }

}
