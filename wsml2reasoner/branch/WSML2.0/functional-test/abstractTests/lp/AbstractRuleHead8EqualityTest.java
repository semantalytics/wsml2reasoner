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

		// Set<Map<Variable, Term>> result =
		// LPHelper.executeQuery(OntologyHelper
		// .loadOntology(ONTOLOGY_FILE), query, reasoner);
		// AbstractTestHelper.printResult(result, query);

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple#A"));
		r.addBinding(Results.iri("http://simple#B"));
		r.addBinding(Results.iri("http://simple#C"));
		r.addBinding(Results.iri("http://simple#D"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}
}
