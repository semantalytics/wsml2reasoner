package org.wsml.reasoner.api;

import org.wsmo.common.IRI;

public interface WSMLFlightReasoner extends WSMLReasoner{
    public boolean isSatisfiable(IRI ontologyID);
}
