package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;


public class MinCardinalityViolation extends CardinalityViolation {

    public MinCardinalityViolation(Instance instance, Attribute attribute) {
        super(instance, attribute);
    }
    
}
