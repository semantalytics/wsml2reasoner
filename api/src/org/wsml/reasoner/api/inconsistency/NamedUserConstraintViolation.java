package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Axiom;
import org.wsmo.common.IRI;

public class NamedUserConstraintViolation extends UserConstraintViolation {

    private Axiom axiom;

    public NamedUserConstraintViolation(IRI ontologyIri, Axiom axiom) {
        super(ontologyIri);
        this.axiom = axiom;
    }

    public Axiom getAxiom() {
        return axiom;
    }
    
    public String toString(){
        return "User constraint Violation due to axiom : " + axiom.getIdentifier();  
    }
    
}
