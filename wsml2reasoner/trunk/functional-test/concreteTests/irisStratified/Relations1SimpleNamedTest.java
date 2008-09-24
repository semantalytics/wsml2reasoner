package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractRelations1SimpleNamed;

public class Relations1SimpleNamedTest extends AbstractRelations1SimpleNamed
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
