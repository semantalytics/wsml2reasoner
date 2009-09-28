package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead8EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "files/equal8_inHeadIRIS.wsml";
	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual() throws Exception {

		String query = "p(?x)";

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple8#A"));
		r.addBinding(Results.iri("http://simple8#B"));
		r.addBinding(Results.iri("http://simple8#C"));
		r.addBinding(Results.iri("http://simple8#D"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}
}
