package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;

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

    public AttributeTypeViolation(Instance instance, Value violatingValue, Attribute attribute, Type expectedType) {
        this.instance = instance;
        this.violatingValue = violatingValue;
        this.attribute = attribute;
        this.expectedType = expectedType;
    }

}
