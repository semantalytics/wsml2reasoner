package concreteTests.kaon2dl;

import org.wsml.reasoner.api.DLReasoner;
import abstractTests.dl.AbstractOntologyWithCycles;

public class OntologyWithCycles extends AbstractOntologyWithCycles
{
	public DLReasoner getReasoner()
    {
	    return Kaon2Helper.getKaon2DL();
    }
}
