package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractAxiom6RuleImplicationsInBody;

public class Axiom6RuleImplicationsInBodyTest extends AbstractAxiom6RuleImplicationsInBody
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
