package org.wsml.reasoner.api;

import org.wsmo.common.IRI;

public interface WSMLCoreReasoner extends WSMLReasoner{
    public boolean entails(IRI baseOntologyID, IRI consequenceOntologyID);
}
