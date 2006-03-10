package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;

public class MaxCardinalityViolation extends CardinalityViolation {

    public MaxCardinalityViolation(Instance instance, Attribute attribute) {
        super(instance, attribute);
    }

}
