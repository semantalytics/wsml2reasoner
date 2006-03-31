package org.wsml.reasoner.api.inconsistency;

import org.wsmo.common.IRI;


public class UserConstraintViolation extends ConsistencyViolation {
    
    public UserConstraintViolation(IRI ontologyIri) {
        super(ontologyIri);
    }
    
}
