package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead6EqualityTest extends TestCase implements
		LP {

	
	protected static final String ONTOLOGY_FILE = "files/equal6_inHeadIRIS.wsml";

	protected LPReasoner reasoner;
	
	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}
	
	public void testExample() throws Exception {

		String query = "p(?x)";
		
//		Set<Map<Variable, Term>> result = LPHelper.executeQuery(OntologyHelper
//		.loadOntology(ONTOLOGY_FILE), query,reasoner);
//		AbstractTestHelper.printResult(result, query);
		
		Results r = new Results("x");
		r.addBinding( Results.iri("http://simple#B1") );
		r.addBinding( Results.iri("http://simple#A1") );
		
		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}
}