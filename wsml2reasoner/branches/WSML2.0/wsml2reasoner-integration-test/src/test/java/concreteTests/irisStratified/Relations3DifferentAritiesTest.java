package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRelations3DifferentArities;

public class Relations3DifferentAritiesTest extends AbstractRelations3DifferentArities
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
