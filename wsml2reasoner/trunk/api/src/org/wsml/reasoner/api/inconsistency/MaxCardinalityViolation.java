package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;

public class MaxCardinalityViolation extends CardinalityViolation {

    public MaxCardinalityViolation(IRI ontologyIri, Instance instance, Attribute attribute) {
        super(ontologyIri, instance, attribute);
    }
    
    public String toString(){
        TopEntity te = getInstance().getOntology();
        return "Maximum cardinality violation on instance: " + 
                toString(getInstance(),te) +
                " attribute " + toString(getAttribute().getIdentifier(),te) +
                " has more then " +getAttribute().getMaxCardinality() +" value(s) ";  
    }

}
