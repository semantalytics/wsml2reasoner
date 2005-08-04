/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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

import org.deri.wsml.reasoner.api.*;
import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.deri.wsml.reasoner.wsmlcore.QueryAnsweringReasoner;
import org.deri.wsml.reasoner.wsmlcore.wrapper.*;

/**
 * A prototypical implementation of a reasoner for WSML Core.
 * 
 * At present the implementation only supports the following reasoning tasks:
 *   - Query answering
 *   
 * @author Uwe Keller, DERI Innsbruck
 */
public class WSMLCoreReasonerImpl implements WSMLReasoner {

    public Result execute(Request req) throws UnsupportedOperationException, IllegalArgumentException {
        
        if (req instanceof QueryAnsweringRequest){
            
            QueryAnsweringReasoner qaReasoner = new QueryAnsweringReasoner();
            try {
                return qaReasoner.execute((QueryAnsweringRequest) req);
            } catch (ExternalToolException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Given query could not be deal with by the external tool!", e);
            }
        
        } else {
            // Everything else is currently not supported!
            throw new UnsupportedOperationException("Requested reasoning ["+req.getClass().toString()+"] task is currently not supported by class " + getClass().toString());
        }
    
    }

}
