package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRelations5Example;

public class Relations5ExampleTest extends AbstractRelations5Example
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
