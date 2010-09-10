package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractAxiom5RuleFreeVariable;

public class Axiom5RuleFreeVariableTest extends AbstractAxiom5RuleFreeVariable
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
