package org.wsml.reasoner.api.inconsistency;

import org.wsmo.common.IRI;

public class ConsistencyViolation {
    
    private IRI ontologyIRI;

    public IRI getOntologyIRI() {
        return ontologyIRI;
    }

    public ConsistencyViolation(IRI ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
    }

}
