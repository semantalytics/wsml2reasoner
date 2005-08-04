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

package org.deri.wsml.reasoner.api;

/**
 * Represents the result that is computed for some CheckLogicalEntailment request.
 * Essentially, the result can be of three types:
 * 
 * - The logical entailment could be proven and thus holdes (assuming correctness of the reasoner)
 * - The logical entailment could be disproven and thus does not hold (again assuming correctness)
 * - The logical entailment could neither be proven or disproven due to some resource limit (usually
 *   time or space)
 *    
 * @author Uwe Keller, DERI Innsbruck
 */
public interface CheckLogicalEntailmentResult extends Result {

    public enum EntailmentResult {STATEMENT_ENTAILED, STATEMENT_NOT_ENTAILED, UNKNOWN};
    
    public EntailmentResult getResult();
}
