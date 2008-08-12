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
package org.wsml.reasoner.transformation.le.negationpush;

import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;

/**
 * This singleton class represents a set of normalization rules for pushing
 * negation-as-failure operators inside a logical expression into its
 * sub-expressions, such that the remaining occurrences of negation are all
 * atomic.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class NegationPushRules extends Rules <NormalizationRule>{

    public NegationPushRules(WSMO4JManager wsmoManager) {
        addRule(new DoubleNegationRule());
        addRule(new NegateConjunctionRule(wsmoManager));
        addRule(new NegateDisjunctionRule(wsmoManager));
    }
}
