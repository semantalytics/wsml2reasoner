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
package org.wsml.reasoner.transformation.le.lloydtopor;

import org.wsml.reasoner.transformation.le.Rules;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;

/**
 * This singleton class represents a set of transformation rules for splitting
 * complex LP-rules, constraints and facts into simple datalog-style rule,
 * according to the Lloyd-Topor transformation.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class LloydToporRules extends Rules <TransformationRule>{
    
    public LloydToporRules(FactoryContainer wsmoManager) {
        addRule(new SplitDisjunctiveBody(wsmoManager));
        addRule(new SplitConstraint(wsmoManager));
        addRule(new SplitConjunctiveHead(wsmoManager));
        addRule(new TransformNestedImplication(wsmoManager));
        addRule(new SplitConjunction());
        addRule(new TransformImplication(wsmoManager));
    }
}
