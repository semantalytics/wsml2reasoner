package org.wsml.reasoner.api.inconsistency;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Instance;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;

public class MaxCardinalityViolation extends CardinalityViolation {

	private TopEntity te;
	
    public MaxCardinalityViolation(IRI ontologyIri, Term term, Attribute attribute, TopEntity te) {
        super(ontologyIri, term, attribute);
        this.te = te;
    }
    
    public String toString(){
        //TopEntity te = getInstance().getOntology();
        return "Maximum cardinality violation on instance: " + 
                toString(getTerm(),te) +
                " attribute " + toString(getAttribute().getIdentifier(),te) +
                " has more then " +getAttribute().getMaxCardinality() +" value(s) ";  
    }

}
