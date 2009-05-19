package concreteTests.kaon2;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractAxiom7FlightSafeRules;

public class Axiom7FlightSafeRulesTest extends AbstractAxiom7FlightSafeRules
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
