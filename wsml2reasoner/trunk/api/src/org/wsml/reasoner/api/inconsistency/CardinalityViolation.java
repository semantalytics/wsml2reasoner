package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;

public abstract class CardinalityViolation extends ConsistencyViolation {

    private Instance instance;

    private Attribute attribute;

    public Attribute getAttribute() {
        return attribute;
    }

    public Instance getInstance() {
        return instance;
    }

    public CardinalityViolation(Instance instance, Attribute attribute) {
        this.instance = instance;
        this.attribute = attribute;
    }

}
