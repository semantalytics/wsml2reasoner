package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;


public class MinCardinalityViolation extends CardinalityViolation {

    public MinCardinalityViolation(IRI ontologyIri, Instance instance, Attribute attribute) {
        super(ontologyIri, instance, attribute);
    }
    
    public String toString(){
        TopEntity te = getInstance().getOntology();
        return "Minimum cardinality violation on instance: " + 
                toString(getInstance(),te) +
                " attribute " + toString(getAttribute().getIdentifier(),te) +
                " should have " +getAttribute().getMinCardinality() +" value(s) ";  
    }
    
}
