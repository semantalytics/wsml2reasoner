package concreteTests.pellet;

import org.wsml.reasoner.api.DLReasoner;

import abstractTests.dl.AbstractConceptSatisfiabilityCheck1;

public class ConceptSatisfiabilityCheck1Test extends AbstractConceptSatisfiabilityCheck1
{
	public DLReasoner getDLReasoner()
	{
		return Reasoner.get();
	}
}
