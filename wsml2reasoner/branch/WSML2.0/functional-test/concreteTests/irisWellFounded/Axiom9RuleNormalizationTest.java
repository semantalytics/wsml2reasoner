package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractAxiom9RuleNormalization;

public class Axiom9RuleNormalizationTest extends AbstractAxiom9RuleNormalization
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
