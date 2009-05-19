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
package org.wsml.reasoner.transformation.le.inverseimplicationreduction;

import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;

/**
 * This singleton class represents a set of normalization rules for replacing
 * left-side conjunctions and right-side disjunctions within left-implications
 * in logical expressions.
 * 
 * <pre>
 *   Created on July 3rd, 2006
 *   Committed by $Author: nathalie $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/transformation/le/InverseImplicationReductionRules.java,v $,
 * </pre>
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.3 $ $Date: 2006-08-08 12:47:54 $
 */
public class InverseImplicationReductionRules extends Rules <NormalizationRule>{

    public InverseImplicationReductionRules(WSMO4JManager wsmoManager) {
        addRule(new InvImplLeftConjunctionReplacementRule(wsmoManager));
        addRule(new InvImplRightDisjunctionReplacementRule(wsmoManager));
    }
}