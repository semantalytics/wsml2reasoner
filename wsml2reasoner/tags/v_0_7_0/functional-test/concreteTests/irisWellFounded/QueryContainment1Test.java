package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractQueryContainment1;

public class QueryContainment1Test extends AbstractQueryContainment1
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
