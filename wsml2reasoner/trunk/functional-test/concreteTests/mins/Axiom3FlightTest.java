package concreteTests.mins;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractAxiom3Flight;

public class Axiom3FlightTest extends AbstractAxiom3Flight
{
	public LPReasoner getLPReasoner()
	{
		return MinsHelper.getReasoner();
	}
}
