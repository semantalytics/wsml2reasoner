package org.wsml.reasoner.api.inconsistency;

import org.wsmo.common.IRI;

public class NamedUserConstraintViolation extends UserConstraintViolation {

    private IRI axiomId;

    public NamedUserConstraintViolation(IRI axiomId) {
        this.axiomId = axiomId;
    }

    public IRI getAxiomId() {
        return axiomId;
    }
    
}
