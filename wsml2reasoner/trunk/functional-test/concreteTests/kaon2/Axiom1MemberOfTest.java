package concreteTests.kaon2;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractAxiom1MemberOf;

public class Axiom1MemberOfTest extends AbstractAxiom1MemberOf
{
	public LPReasoner getLPReasoner()
	{
		return Kaon2Helper.getReasoner();
	}
}
