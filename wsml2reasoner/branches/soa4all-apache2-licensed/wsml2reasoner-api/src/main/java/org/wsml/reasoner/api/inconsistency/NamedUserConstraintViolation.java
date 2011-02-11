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

import org.omwg.ontology.Axiom;
import org.wsmo.common.TopEntity;

public class NamedUserConstraintViolation extends UserConstraintViolation {

    private Axiom axiom;

    public NamedUserConstraintViolation(Axiom axiom) {
        super();
        this.axiom = axiom;
    }

    public Axiom getAxiom() {
        return axiom;
    }

    public String toString() {
        TopEntity te = axiom.getOntology();
        return "User constraint Violation due to axiom: " + toString(axiom.getIdentifier(), te);
    }

}
