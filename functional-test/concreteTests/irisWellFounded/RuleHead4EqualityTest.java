package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRuleHead4EqualityTest;

public class RuleHead4EqualityTest extends AbstractRuleHead4EqualityTest{

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}
}
