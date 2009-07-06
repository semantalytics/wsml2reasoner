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

public abstract class AbstractRuleHead3EqualityTest extends TestCase implements
		LP {

	
	protected static final String ONTOLOGY_FILE_03 = "files/equal3_inHeadIRIS.wsml";

	protected LPReasoner reasoner;
	
	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}
	
	
	public void testExample04() throws ParserException, InconsistencyException,
	IOException, InvalidModelException {

		String query = "?x memberOf C1";
		
		Set<Map<Variable, Term>> result = LPHelper.executeQuery(OntologyHelper
		.loadOntology(ONTOLOGY_FILE_03), query,reasoner);

		
		AbstractTestHelper.printResult(result, query);
	
	}
	
	public void testExample05() throws ParserException, InconsistencyException,
	IOException, InvalidModelException {

		String query = "?x[?n hasValue ?y] ";
		
		Set<Map<Variable, Term>> result = LPHelper.executeQuery(OntologyHelper
		.loadOntology(ONTOLOGY_FILE_03), query,reasoner);

		
		AbstractTestHelper.printResult(result, query);
	
	}
	
	


	
}
