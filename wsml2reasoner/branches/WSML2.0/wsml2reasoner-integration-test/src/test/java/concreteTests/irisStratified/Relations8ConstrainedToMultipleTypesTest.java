package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRelations8ConstrainedToMultipleTypes;

public class Relations8ConstrainedToMultipleTypesTest extends AbstractRelations8ConstrainedToMultipleTypes
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
