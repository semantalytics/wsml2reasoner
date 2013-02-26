package org.wsml.reasoner.transformation.le.inverseimplicationreduction;

import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;
import org.wsmo.factory.FactoryContainer;

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
public class InverseImplicationReductionRules extends Rules<NormalizationRule> {

	public InverseImplicationReductionRules(FactoryContainer wsmoManager) {
		addRule(new InvImplLeftConjunctionReplacementRule(wsmoManager));
		addRule(new InvImplRightDisjunctionReplacementRule(wsmoManager));
	}
}