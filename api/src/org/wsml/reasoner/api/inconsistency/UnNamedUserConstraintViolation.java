package org.wsml.reasoner.api.inconsistency;

import org.wsmo.common.*;

public class UnNamedUserConstraintViolation extends UserConstraintViolation {


    public UnNamedUserConstraintViolation(IRI ontologyIri) {
        super(ontologyIri);
    }

    public String toString(){
        return "User constraint Violation due to anonymous axiom (for more details you must name your axioms)";  
    }
    
}
