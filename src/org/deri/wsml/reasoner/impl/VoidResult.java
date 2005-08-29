/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
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
