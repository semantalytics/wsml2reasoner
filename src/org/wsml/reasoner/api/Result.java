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

package org.wsml.reasoner.api;

/**
 * Instances of this class represent result of requests that have been sent
 * to a WSML reasoner. 
 * 
 * Various subclasses represent the information delivered by the single possible
 * and supported reasoning tasks and allow to access this information.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface Result {
    
    /**
     * @return the request to which this result belongs to.
     */
    public Request getRequest();
    

}
