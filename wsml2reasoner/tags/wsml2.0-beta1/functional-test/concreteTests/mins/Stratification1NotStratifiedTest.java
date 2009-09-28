package concreteTests.mins;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractStratification1NotStratified;

public class Stratification1NotStratifiedTest extends AbstractStratification1NotStratified
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
