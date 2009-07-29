package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead10EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "files/equal10_inHeadIRIS.wsml";
	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual() throws Exception {

		String query = "?instance[?attribute hasValue ?value]";

		Results r = new Results("instance", "attribute", "value");
		

		// TODO check
		// if concepts set equal just 2 results - if instances set equal 4 results.
		LPHelper.outputON();  
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
		LPHelper.outputOFF();
	}
}
