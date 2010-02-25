package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead11EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "files/equal11_inHeadIRIS.wsml";
	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual() throws Exception {

		String query = "?instance memberOf C1";

		Results r = new Results("instance");
		r.addBinding(Results.iri("http://simple11#B"));
		r.addBinding(Results.iri("http://simple11#A"));
		
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}
}
