package org.wsml.reasoner.api.inconsistency;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.wsmo.common.TopEntity;


public class MinCardinalityViolation extends CardinalityViolation {

	private TopEntity te;
	
    public MinCardinalityViolation(Term term, Attribute attribute, TopEntity te) {
    	super(term, attribute);
    	this.te = te;
    }
    
    public String toString(){
        return "Minimum cardinality violation on instance: " + 
                toString(getTerm(),te) +
                " attribute " + toString(getAttribute().getIdentifier(),te) +
                " should have " +getAttribute().getMinCardinality() +" value(s) ";  
    }
    
}
