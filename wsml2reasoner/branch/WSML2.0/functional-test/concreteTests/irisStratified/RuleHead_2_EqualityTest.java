package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRuleHead2EqualityTest;

public class RuleHead_2_EqualityTest extends AbstractRuleHead2EqualityTest{

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}
}
