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

package org.wsml.reasoner.api.queryanswering;

import org.omwg.logexpression.LogicalExpression;
import org.wsml.reasoner.api.OntologyBasedRequest;

/** 
 *  An interface for reasoning request that refer to 
 *  a specific reasoning task, namely QueryAnswering.
 *  
 *  Given such a request, reasoners should return objects that are
 *  instances that implement the interface QueryAnsweringResult.
 *  
 * @author Uwe Keller, DERI Innsbruck
 */
public interface QueryAnsweringRequest extends OntologyBasedRequest {

    public enum QueryAnsweringMode {FIND_ALL_RESULTS, FIND_NO_MORE_THAN};
    
    public LogicalExpression getQuery();
    public QueryAnsweringMode getMode();
    public long getMaxNumberOfResults();
    public String getOntologyUri();
   
}
