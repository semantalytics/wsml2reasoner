package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractRelations4ImportsOntologyNormal;

public class Relations4ImportsOntologyNormal extends AbstractRelations4ImportsOntologyNormal
{
	public LPReasoner getLPReasoner()
	{
		return IrisHelper.getReasoner();
	}
}
