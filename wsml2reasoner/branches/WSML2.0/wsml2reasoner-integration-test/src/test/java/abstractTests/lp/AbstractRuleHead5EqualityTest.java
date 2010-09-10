package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead5EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "equal5_inHeadIRIS.wsml";

	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual() throws Exception {
		String query = "?x[?y hasValue ?z]";

		Results r = new Results("z", "y", "x");
		r.addBinding(Results.iri("http://simple5#aa"), Results
				.iri("http://simple5#other"), Results.iri("http://simple5#A1"));
		r.addBinding(Results.iri("http://simple5#a"), Results
				.iri("http://simple5#some"), Results.iri("http://simple5#A1"));
		r.addBinding(Results.iri("http://simple5#a"), Results
				.iri("http://simple5#some"), Results.iri("http://simple5#B1"));
		r.addBinding(Results.iri("http://simple5#bb"), Results
				.iri("http://simple5#other"), Results.iri("http://simple5#B1"));
		r.addBinding(Results.iri("http://simple5#bb"), Results
				.iri("http://simple5#other"), Results.iri("http://simple5#A1"));
		r.addBinding(Results.iri("http://simple5#aa"), Results
				.iri("http://simple5#other"), Results.iri("http://simple5#B1"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

	}

	public void testEqual2() throws Exception {

		String query = "p(?x)";

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple5#B1"));
		r.addBinding(Results.iri("http://simple5#A1"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

	}

}
