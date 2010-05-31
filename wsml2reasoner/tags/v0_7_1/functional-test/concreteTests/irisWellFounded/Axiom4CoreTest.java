package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.WSMLReasoner;

import abstractTests.lp.AbstractAxiom4Core;

public class Axiom4CoreTest extends AbstractAxiom4Core
{
	public WSMLReasoner getReasoner()
	{
		return Reasoner.get();
	}
}
