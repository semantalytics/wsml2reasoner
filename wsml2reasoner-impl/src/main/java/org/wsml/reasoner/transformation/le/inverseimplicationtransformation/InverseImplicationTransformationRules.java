package org.wsml.reasoner.transformation.le.inverseimplicationtransformation;

import org.wsml.reasoner.transformation.le.Rules;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;

/**
 * 
 * This rule is unsafe and just for WSML-Rule - where implies in body is
 * allowed.
 */
public class InverseImplicationTransformationRules extends
		Rules<TransformationRule> {

	public InverseImplicationTransformationRules(FactoryContainer wsmoManager) {
		addRule(new SplitInvImplicationBodyRule(wsmoManager));
	}

}
