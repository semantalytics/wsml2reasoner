package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;

public class TypeViolation extends ConsistencyViolation {
    
    private Instance sourceInstance;

    private Instance targetInstance;

    private Attribute attribute;

    private Concept expectedType;

    public Attribute getAttribute() {
        return attribute;
    }

    public Concept getExpectedType() {
        return expectedType;
    }

    public Instance getSourceInstance() {
        return sourceInstance;
    }
   
    public Instance getTargetInstance() {
        return targetInstance;
    }

    public TypeViolation(Instance sourceInstance, Instance targetInstance, Attribute attribute, Concept expectedType) {
        this.sourceInstance = sourceInstance;
        this.targetInstance = targetInstance;
        this.attribute = attribute;
        this.expectedType = expectedType;
    }

}
