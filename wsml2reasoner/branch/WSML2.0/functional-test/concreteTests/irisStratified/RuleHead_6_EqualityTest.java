package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRuleHead6EqualityTest;

public class RuleHead_6_EqualityTest extends AbstractRuleHead6EqualityTest{

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}
}
