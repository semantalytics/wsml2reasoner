package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead5EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "files/equal5_inHeadIRIS.wsml";

	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testExample() throws Exception {
		String query = "?x[?y hasValue ?z]";

		// Set<Map<Variable, Term>> result =
		// LPHelper.executeQuery(OntologyHelper
		// .loadOntology(ONTOLOGY_FILE), query,reasoner);
		// AbstractTestHelper.printResult(result, query);

		Results r = new Results("z", "y", "x");
		r.addBinding(Results.iri("http://simple#aa"), Results
				.iri("http://simple#other"), Results.iri("http://simple#A1"));
		r.addBinding(Results.iri("http://simple#a"), Results
				.iri("http://simple#some"), Results.iri("http://simple#A1"));
		r.addBinding(Results.iri("http://simple#a"), Results
				.iri("http://simple#some"), Results.iri("http://simple#B1"));
		r.addBinding(Results.iri("http://simple#bb"), Results
				.iri("http://simple#other"), Results.iri("http://simple#B1"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

	}

	public void testExample2() throws Exception {

		String query = "p(?x)";

		// Set<Map<Variable, Term>> result =
		// LPHelper.executeQuery(OntologyHelper
		// .loadOntology(ONTOLOGY_FILE), query,reasoner);
		// // should also write ?x: http://simple#A1
		// AbstractTestHelper.printResult(result, query);

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple#B1"));
		r.addBinding(Results.iri("http://simple#A1"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

	}

}
