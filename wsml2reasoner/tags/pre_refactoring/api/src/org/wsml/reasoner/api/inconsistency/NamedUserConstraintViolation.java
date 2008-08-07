package org.wsml.reasoner.api.inconsistency;

import org.omwg.ontology.Axiom;
import org.wsmo.common.TopEntity;

public class NamedUserConstraintViolation extends UserConstraintViolation {

    private Axiom axiom;

    public NamedUserConstraintViolation(Axiom axiom) {
        super();
        this.axiom = axiom;
    }

    public Axiom getAxiom() {
        return axiom;
    }
    
    public String toString(){
        TopEntity te=axiom.getOntology();
        return "User constraint Violation due to axiom: " + 
                toString(axiom.getIdentifier(),te);  
    }
    
}
