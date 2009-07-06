package abstractTests.lp;

import helper.AbstractTestHelper;
import helper.LPHelper;
import helper.OntologyHelper;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.ParserException;

import abstractTests.LP;

public abstract class AbstractRuleHead5EqualityTest extends TestCase implements
		LP {

	
	protected static final String ONTOLOGY_FILE = "files/equal5_inHeadIRIS.wsml";

	protected LPReasoner reasoner;
	
	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}
	
	public void testExample() throws ParserException, InconsistencyException,
	IOException, InvalidModelException {

//		String query = "?x = ?y :- wsml#true.";
		String query = "?x[?y hasValue ?z]";
		
		Set<Map<Variable, Term>> result = LPHelper.executeQuery(OntologyHelper
		.loadOntology(ONTOLOGY_FILE), query,reasoner);

		
		AbstractTestHelper.printResult(result, query);
	
	}
	
	public void testExample2() throws ParserException, InconsistencyException,
	IOException, InvalidModelException {

		String query = "p(?x)";
		
		Set<Map<Variable, Term>> result = LPHelper.executeQuery(OntologyHelper
		.loadOntology(ONTOLOGY_FILE), query,reasoner);

		// should also write ?x: http://simple#A1	
		AbstractTestHelper.printResult(result, query);
	
	}
	
	
	
	
}
