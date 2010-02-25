package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRelations6WsmlDataTypes;

public class Relations6WsmlDataTypesTest extends AbstractRelations6WsmlDataTypes
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
