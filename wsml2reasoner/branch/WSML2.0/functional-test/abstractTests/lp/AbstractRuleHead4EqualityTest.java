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

public abstract class AbstractRuleHead4EqualityTest extends TestCase implements
		LP {

	
	protected static final String ONTOLOGY_FILE_03 = "files/equal4_inHeadIRIS.wsml";

	protected LPReasoner reasoner;
	
	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}
	
	
	public void testExample() throws ParserException, InconsistencyException,
	IOException, InvalidModelException {

		String query = "?x[?n1 hasValue ?y]";
		
		Set<Map<Variable, Term>> result = LPHelper.executeQuery(OntologyHelper
		.loadOntology(ONTOLOGY_FILE_03), query, reasoner);

		
		AbstractTestHelper.printResult(result, query);
	
	}

	
}
