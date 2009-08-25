package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRIFBuiltin01Test extends TestCase implements LP {

	protected static final String ONTOLOGY_FILE = "files/BuiltinRIFTest01.wsml";
	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();

		LPHelper.outputON();
	}
	
	public void test_isDecimal() throws Exception {

		String query = "isDecimalTest(?x)";
		checkSTD(query);
	}

	public void test_isString() throws Exception {

		String query = "isStringTest(?x)";
		checkSTD(query);
	}

	public void test_isDouble() throws Exception {

		String query = "isDoubleTest(?x)";
		checkSTD(query);
	}
	
	public void test_isBoolean() throws Exception {

		String query = "isBooleanTest(?x)";
		checkSTD(query);
	}
	
	public void test_isInteger() throws Exception {

		String query = "isIntegerTest(?x)";
		checkSTD(query);
	}
	
	public void test_isBase64Binary() throws Exception {

		String query = "isBase64Binary(?x)";
		checkSTD(query);
	}
	
	public void test_isDate() throws Exception {

		String query = "isDateTest(?x)";
		checkSTD(query);
	}
	
	public void test_isDuration() throws Exception {

		String query = "isDurationTest(?x)";
		checkSTD(query);
	}
	
	public void test_isDateTime() throws Exception {

		String query = "isDateTimeTest(?x)";
		checkSTD(query);
	}
	
	public void test_isFloat() throws Exception {

		String query = "isFloatTest(?x)";
		checkSTD(query);
	}
	
	public void test_isGDay() throws Exception {

		String query = "isGDayTest(?x)";
		checkSTD(query);
	}
	
	
// TODO DOES NOT WORK YET !!!
	public void test_StringEqual() throws Exception {

		String query = "stringEqualTest(?x)";
		checkSTD(query);
	}

	public void test_stringCompare() throws Exception {

		String query = "stringCompareTest(?x)";
		checkSTD(query);
	}

	public void test_stringConcat() throws Exception {

		String query = "stringConcatTest(?x)";
		checkSTD(query);
	}

	public void test_gyearEqual() throws Exception {

		String query = "gyearEqualTest(?x)";
		checkSTD(query);
	}

	public void test_numericModulus() throws Exception {

		String query = "numericModulusTest(?x)";
		checkSTD(query);
	}

	private void checkSTD(String query) throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri("http://builtin01#B"));
		r.addBinding(Results.iri("http://builtin01#A"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}

}
