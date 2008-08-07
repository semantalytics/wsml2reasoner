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
package org.wsml.reasoner.transformation.le;

import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;

/**
 * A logical expression transformer transforms a single logical expression into a set of resulting logical expressions. The
 * result should be created such that the original expression remains unchanged.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public interface LogicalExpressionTransformer
{
    /**
     * This method transforms a single logical expression into a set of resulting expressions.
     * @param expression
     * @return a set of logical expression resulting from the transformation
     */
    public Set<LogicalExpression> transform(LogicalExpression expression);
}
