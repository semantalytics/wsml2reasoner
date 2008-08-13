package concreteTests.pellet;

import org.wsml.reasoner.api.DLReasoner;
import abstractTests.dl.AbstractOntologyWithCycles;


public class OntologyWithCycles extends AbstractOntologyWithCycles
{
	public DLReasoner getDLReasoner()
    {
	    return PelletHelper.getPellet();
    }
}
