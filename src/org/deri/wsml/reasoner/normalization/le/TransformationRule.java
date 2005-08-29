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
package org.deri.wsml.reasoner.normalization.le;

import java.util.Set;
import org.omwg.logexpression.LogicalExpression;

/**
 * A transformation rule specifies the behaviour of how to transform a single logical expression into a set of logical expressions.
 * @author Stephan Grimm, FZI Karlsruhe 
 */
public interface TransformationRule extends LEModificationRule
{
    /**
     * This method applies the transformation behaviour specified by this transformation rule to a logical expression.
     * @param expression
     * @return a set of logical expressions resulting from the transformation 
     */
    public Set<LogicalExpression> apply(LogicalExpression expression);
}