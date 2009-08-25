package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead4EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "files/equal4_inHeadIRIS.wsml";

	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual() throws Exception {

		String query = "?x[?n1 hasValue ?y]";

		Results r = new Results("n1", "x", "y");
		r.addBinding(Results.iri("http://simple4#name"), Results
				.iri("http://simple4#a"), Results.iri("http://simple4#aName"));
		r.addBinding(Results.iri("http://simple4#name"), Results
				.iri("http://simple4#b"), Results.iri("http://simple4#aName"));

//		LPHelper.output = true;
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

	}

	public void testEqual1() throws Exception {

		String query = "p(?x)";

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple4#a"));
		r.addBinding(Results.iri("http://simple4#b"));

		LPHelper.outputON();

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

	}

}
