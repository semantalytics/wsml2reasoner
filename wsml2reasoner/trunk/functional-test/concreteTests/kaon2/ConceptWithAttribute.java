package concreteTests.kaon2;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractConceptWithAttribute;

public class ConceptWithAttribute extends AbstractConceptWithAttribute
{
	public LPReasoner getLPReasoner()
	{
		return Kaon2Helper.getReasoner();
	}
}
