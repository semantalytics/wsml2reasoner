package org.wsml.reasoner.api.inconsistency;

import java.util.Set;

public class InconsistencyException extends RuntimeException {

    private static final long serialVersionUID = 7674954034000599115L;
    
    private Set<ConsistencyViolation> violations;
    
    public InconsistencyException(Set<ConsistencyViolation> violations) {
        this.violations = violations;
    }

    public Set<ConsistencyViolation> getViolations() {
        return violations;
    }

}
