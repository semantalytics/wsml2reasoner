package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRuleHead11EqualityTest;

public class RuleHead11EqualityTest extends AbstractRuleHead11EqualityTest{

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}
}
