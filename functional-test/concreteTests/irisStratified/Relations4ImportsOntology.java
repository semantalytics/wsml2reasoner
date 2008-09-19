package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractRelations4ImportsOntology;

public class Relations4ImportsOntology extends AbstractRelations4ImportsOntology
{
	public LPReasoner getLPReasoner()
	{
		return IrisHelper.getReasoner();
	}
}
