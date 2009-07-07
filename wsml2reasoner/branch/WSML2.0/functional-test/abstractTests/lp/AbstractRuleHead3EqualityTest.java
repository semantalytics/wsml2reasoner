package abstractTests.lp;

import helper.AbstractTestHelper;
import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead3EqualityTest extends TestCase implements
		LP {

	
	protected static final String ONTOLOGY_FILE = "files/equal3_inHeadIRIS.wsml";

	protected LPReasoner reasoner;
	
	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}
	
	
	public void testExample04() throws Exception {

		String query = "?x memberOf C1";
		
		Set<Map<Variable, Term>> result = LPHelper.executeQuery(OntologyHelper
		.loadOntology(ONTOLOGY_FILE), query,reasoner);
		AbstractTestHelper.printResult(result, query);
		
		Results r = new Results("x");
		r.addBinding( Results.iri("http://simple#a") );
		r.addBinding( Results.iri("http://simple#b"));
		
		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	
	
	}
	
	public void testExample05() throws Exception {

		String query = "?x[?n hasValue ?y] ";
		
		Set<Map<Variable, Term>> result = LPHelper.executeQuery(OntologyHelper
		.loadOntology(ONTOLOGY_FILE), query,reasoner);
		AbstractTestHelper.printResult(result, query);
		
		Results r = new Results("n","y","x");
		r.addBinding( Results.iri("http://simple#name"), Results.iri("http://simple#aa"), Results.iri("http://simple#a") );
		r.addBinding( Results.iri("http://simple#name"), Results.iri("http://simple#bb"), Results.iri("http://simple#b") );
		
		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	
		
	
	}
	
	


	
}
