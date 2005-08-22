package org.deri.wsml.reasoner.impl;

import org.deri.wsml.reasoner.api.Request;
import org.deri.wsml.reasoner.api.Result;

/**
 * A dummy result implementation for the cases, where nothing can be returned
 * (e.g. at ontology registration)
 * 
 * @author Gabor Nagypal (FZI)
 * 
 */
public class VoidResult implements Result {

    private Request request;

    public VoidResult(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

}
