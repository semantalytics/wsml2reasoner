package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead2EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE_2 = "files/equal2_inHeadIRIS.wsml";

	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual() throws Exception {

		String query = "p(?x)";

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple2#c"));
		r.addBinding(Results.iri("http://simple2#b"));
		r.addBinding(Results.iri("http://simple2#d"));

//		LPHelper.output = true;
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE_2), query, r.get(), reasoner);

	}

}
