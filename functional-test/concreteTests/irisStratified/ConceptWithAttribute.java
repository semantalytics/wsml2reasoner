package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractConceptWithAttribute;

public class ConceptWithAttribute extends AbstractConceptWithAttribute
{
	public LPReasoner getLPReasoner()
	{
		return IrisHelper.getReasoner();
	}
}
