package org.wsml.reasoner.api.inconsistency;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;

public abstract class CardinalityViolation extends ConsistencyViolation {

    private Term term;

    private Attribute attribute;

    public Attribute getAttribute() {
        return attribute;
    }

    public Term getTerm() {
        return term;
    }

    public CardinalityViolation(Term term, Attribute attribute) {
        super();
        this.term = term;
        this.attribute = attribute;
    }

    public String toString() {
        return "Cardinality violation on attribute: " + attribute.getIdentifier() + " violating instance " + term.toString();
    }

}
