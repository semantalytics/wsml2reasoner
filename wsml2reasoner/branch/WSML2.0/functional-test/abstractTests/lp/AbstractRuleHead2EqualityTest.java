package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead2EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "files/equal2_inHeadIRIS.wsml";

	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual() throws Exception {

		String query = "p(?x)";

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple#c"));
		r.addBinding(Results.iri("http://simple#b"));
		r.addBinding(Results.iri("http://simple#d"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

		// Set<Map<Variable, Term>> result =
		// LPHelper.executeQuery(OntologyHelper
		// .loadOntology(ONTOLOGY_FILE_02), query, reasoner);
		//
		// AbstractTestHelper.printResult(result, query);
		//
		// assertTrue(AbstractTestHelper.checkIsIn(result, "?x",
		// AbstractTestHelper.createIRI("http://simple#c")));
		// assertTrue(AbstractTestHelper.checkIsIn(result, "?x",
		// AbstractTestHelper.createIRI("http://simple#b")));
		// assertTrue(AbstractTestHelper.checkIsIn(result, "?x",
		// AbstractTestHelper.createIRI("http://simple#d")));
	}

}
