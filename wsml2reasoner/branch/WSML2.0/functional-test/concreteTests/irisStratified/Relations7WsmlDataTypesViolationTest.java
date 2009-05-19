package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractRelations7WsmlDataTypesViolation;

public class Relations7WsmlDataTypesViolationTest extends AbstractRelations7WsmlDataTypesViolation
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
