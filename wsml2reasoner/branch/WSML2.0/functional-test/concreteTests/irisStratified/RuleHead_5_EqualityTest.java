package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRuleHead5EqualityTest;

public class RuleHead_5_EqualityTest extends AbstractRuleHead5EqualityTest{

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}
}
