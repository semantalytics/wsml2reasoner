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
package org.wsml.reasoner.transformation.le;

import org.omwg.logicalexpression.LogicalExpression;

/**
 * A modification rule for a logical expression specifies the behaviour of how
 * to perform modifications to this expression. For a given logical expression
 * le it can decide whether this behaviour is applicable to le.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public interface LEModificationRule {
    /**
     * This method decides whether the behaviour specified by this modification
     * rule is applicable to a given logical expression.
     * 
     * @param expression
     * @return true if the rule is applicable, falsr otherwise
     */
    public boolean isApplicable(LogicalExpression expression);
}
