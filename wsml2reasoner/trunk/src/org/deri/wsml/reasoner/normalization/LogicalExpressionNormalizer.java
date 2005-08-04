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

package org.deri.wsml.reasoner.normalization;

import java.util.*;

import org.omwg.ontology.*;

/**
 * An interface for normalization steps on logical expressions in WSML.
 * All classes that perform such normalizations on logical expression should
 * implement this interface.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface LogicalExpressionNormalizer extends WSMLNormalizer {

    /**
     * Applies the normalization step to the given set of logical expressions.
     * Class can assume that the overall set of logical expression that is relevant
     * to the normalization step is included in the given set. Subsequent calls do
     * not belong to the same context.
     * 
     * Subclasses define application specific normalization steps that are needed in 
     * a specific context. 
     * 
     * @param lExpressions - a set of logical expressions that have to be normalized in some way.
     * @return a set of logical expression which represent the normalized form of the set LExpressions.
     */
    public Set<LogicalExpression> normalize(Set<LogicalExpression> lExpressions);
    
    
}
