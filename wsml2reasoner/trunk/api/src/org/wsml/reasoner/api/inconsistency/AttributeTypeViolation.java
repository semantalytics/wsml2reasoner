package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;
import org.wsmo.common.IRI;

public class AttributeTypeViolation extends ConsistencyViolation {
    
    private Instance instance;

    private Value violatingValue;

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
   
    public Value getViolatingValue() {
        return violatingValue;
    }

    public AttributeTypeViolation(IRI ontologyIri, Instance instance, Value violatingValue, Attribute attribute, Type expectedType) {
        super(ontologyIri);
        this.instance = instance;
        this.violatingValue = violatingValue;
        this.attribute = attribute;
        this.expectedType = expectedType;
    }
    
    public String toString(){
        return "AttributeTypeViolation due to instance: " + instance.getIdentifier() +
            " expected type: "+expectedType+ " found value: " +
            violatingValue + " at attribute: " + attribute.getIdentifier();  
    }


}
