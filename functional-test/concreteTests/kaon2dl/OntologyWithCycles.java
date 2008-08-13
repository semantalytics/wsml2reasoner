package concreteTests.kaon2dl;

import org.wsml.reasoner.api.DLReasoner;
import abstractTests.dl.AbstractOntologyWithCycles;

public class OntologyWithCycles extends AbstractOntologyWithCycles
{
	public DLReasoner getDLReasoner()
    {
	    return Kaon2Helper.getReasoner();
    }
}
