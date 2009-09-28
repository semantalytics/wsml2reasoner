package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRelations4ImportsOntologyReversed;

public class Relations4ImportsOntologyReversedTest extends AbstractRelations4ImportsOntologyReversed
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}
}
