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

import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Type;
import org.wsmo.common.TopEntity;

public class AttributeTypeViolation extends ConsistencyViolation {

    private Instance instance;

    private ConstructedTerm ctInstance;

    private Ontology ontology;

    private Term violatingValue;

    private Attribute attribute;

    private Type expectedType;

    public Attribute getAttribute() {
        return attribute;
    }

    public Type getExpectedType() {
        return expectedType;
    }

    public Instance getInstance() {
        return instance;
    }

    public Term getViolatingValue() {
        return violatingValue;
    }

    public AttributeTypeViolation(Instance instance, Term violatingValue, Attribute attribute, Type expectedType) {
        super();
        this.instance = instance;
        this.violatingValue = violatingValue;
        this.attribute = attribute;
        this.expectedType = expectedType;
    }

    public AttributeTypeViolation(ConstructedTerm ctInstance, Term violatingValue, Attribute attribute, Type expectedType) {
        super();
        this.ctInstance = ctInstance;
        this.violatingValue = violatingValue;
        this.attribute = attribute;
        this.expectedType = expectedType;
    }

    public String toString() {
        if (instance != null) {
            TopEntity te = instance.getOntology();
            return "AttributeTypeViolation due to instance: " + toString(instance.getIdentifier(), te) + " expected type: " + toString(expectedType, te) + " found value: " + toString(violatingValue, te) + " at attribute: " + toString(attribute.getIdentifier(), te);
        }
        else {
            TopEntity te = ontology;
            return "AttributeTypeViolation due to instance: " + toString(ctInstance, te) + " expected type: " + toString(expectedType, te) + " found value: " + toString(violatingValue, te) + " at attribute: " + toString(attribute.getIdentifier(), te);
        }
    }
}
