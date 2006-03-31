package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;
import org.wsmo.common.IRI;

public class MaxCardinalityViolation extends CardinalityViolation {

    public MaxCardinalityViolation(IRI ontologyIri, Instance instance, Attribute attribute) {
        super(ontologyIri, instance, attribute);
    }

}
