package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead1EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "files/equal1_inHeadIRIS.wsml";

	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testExample01() throws Exception {

		String query = "?x[name hasValue ?n1] and ?y[name hasValue ?n2] and ?n1=?n2.";

		Results r = new Results("x", "y");
		r.addBinding(Results.iri("http://simple#x1"), Results
				.iri("http://simple#x1"));
		r.addBinding(Results.iri("http://simple#x1"), Results
				.iri("http://simple#x2"));
		r.addBinding(Results.iri("http://simple#x2"), Results
				.iri("http://simple#x1"));
		r.addBinding(Results.iri("http://simple#x2"), Results
				.iri("http://simple#x2"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}

	public void testExample02() throws Exception {

		String query = "?x memberOf Y";

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple#x1"));
		r.addBinding(Results.iri("http://simple#x2"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

	}

}
