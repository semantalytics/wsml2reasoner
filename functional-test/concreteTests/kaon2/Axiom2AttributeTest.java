package concreteTests.kaon2;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractAxiom2Attribute;

public class Axiom2AttributeTest extends AbstractAxiom2Attribute
{
	public LPReasoner getLPReasoner()
	{
		return Kaon2Helper.getReasoner();
	}
}
