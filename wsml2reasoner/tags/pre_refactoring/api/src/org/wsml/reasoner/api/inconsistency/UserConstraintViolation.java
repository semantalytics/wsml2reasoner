package org.wsml.reasoner.api.inconsistency;



public class UserConstraintViolation extends ConsistencyViolation {
    
    public UserConstraintViolation() {
        super();
    }
    
    public String toString(){
        return "Unnamed user Axiom is violated!";
    }
}
