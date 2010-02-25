package org.wsml.reasoner.api.inconsistency;

import java.util.Set;

public class InconsistencyException extends Exception {

    private static final long serialVersionUID = 7674954034000599115L;

    private Set<ConsistencyViolation> violations = null;

    public InconsistencyException(Set<ConsistencyViolation> violations) {
        super("Consistency Violation detected! (first: " + violations.iterator().next() + ")");
        this.violations = violations;
    }

    public InconsistencyException(String msg) {
        super(msg);
    }

    public Set<ConsistencyViolation> getViolations() {
        return violations;
    }

}
