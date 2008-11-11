package concreteTests.mins;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractAxiom8FlightUnSafeRules;

public class Axiom8FlightUnSafeRulesTest extends AbstractAxiom8FlightUnSafeRules
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
