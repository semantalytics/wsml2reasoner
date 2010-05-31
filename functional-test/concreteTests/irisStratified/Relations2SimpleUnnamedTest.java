package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRelations2SimpleUnnamed;

public class Relations2SimpleUnnamedTest extends AbstractRelations2SimpleUnnamed
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
