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

import java.util.Set;

import org.wsml.reasoner.api.Result;

/**
 * Represents the result that is computed for some query answering request.
 * Essentially, the result can be seen as a set of tuples of constants.
 * The single constants represent objects in the respective ontologies
 * to which the query answering request refered.
 * 
 * Implementations of this interface should support asynchronous reading
 * of the resulting tuples whenever this is possible to allow early fetching
 * of results for client applications.
 * This must be done in an iterator implementation for this class.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface QueryAnsweringResult extends Result, Set<VariableBinding> {

  
}
