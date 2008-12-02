package concreteTests.kaon2;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractAxiom3Flight;

public class Axiom3FlightTest extends AbstractAxiom3Flight
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
