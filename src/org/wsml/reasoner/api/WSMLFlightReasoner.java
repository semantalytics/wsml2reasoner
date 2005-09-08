package org.wsml.reasoner.api;

import java.util.Set;

import org.omwg.logexpression.LogicalExpression;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsmo.common.IRI;

public interface WSMLFlightReasoner extends WSMLReasoner{
    public boolean isSatisfiable(IRI ontologyID);
}
