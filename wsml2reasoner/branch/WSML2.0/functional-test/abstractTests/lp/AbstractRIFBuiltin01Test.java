package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

/**
 * To test rif data types
 * see: http://www.w3.org/2005/rules/wiki/DTB#Primitive_Datatypes
 *
 */
public abstract class AbstractRIFBuiltin01Test extends TestCase implements LP {

	protected static final String ONTOLOGY_FILE = "files/BuiltinRIFTest01.wsml";
	protected LPReasoner reasoner;

	public AbstractRIFBuiltin01Test() {
		reasoner = getLPReasoner();
//		LPHelper.outputON();
	}
	
	// SECTION [A]
	public void test_file() throws Exception {

		String query = "m(?x)";
		checkSTD(query);
	}
	
	public void test_add() throws Exception {
		String query = "numericAddTest(?x)";
		checkSTD(query);
	}
	
	// SECTION [B]
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
	public void test_isGYearMonth() throws Exception {

		String query = "isGYearMonthTest(?x)";
		checkSTD(query);
	}
	public void test_isHexBinary() throws Exception {

		String query = "isHexBinaryTest(?x)";
		checkSTD(query);
	}
	public void test_isTime() throws Exception {

		String query = "isTimeTest(?x)";
		checkSTD(query);
	}
	public void test_isDayTimeDuration() throws Exception {

		String query = "isDayTimeDurationTest(?x)";
		checkSTD(query);
	}
	public void test_isYearMonthDuration() throws Exception {

		String query = "isYearMonthDurationTest(?x)";
		checkSTD(query);
	}
	public void test_isText() throws Exception {

		String query = "isTextTest(?x)";
		checkSTD(query);
	}
	public void test_isXMLLiteral() throws Exception {

		String query = "isXMLLiteralTest(?x)";
		checkSTD(query);
	}
	public void test_StringEqual() throws Exception {

		String query = "stringEqualTest(?x)";
		checkSTD(query);
	}
	
	// SECTION [C]
	public void test_toDouble() throws Exception {

		String query = "toDoubleTest(?x)";
		checkSTD(query);
	}
	public void test_toString() throws Exception {

		String query = "toStringTest(?x)";
		checkSTD(query);
	}
	public void test_toDecimal() throws Exception {

		String query = "toDecimalTest(?x)";
		checkSTD(query);
	}
	public void test_toBoolean() throws Exception {

		String query = "toBooleanTest(?x)";
		checkSTD(query);
	}
	public void test_toInteger() throws Exception {

		String query = "toIntegerTest(?x)";
		checkSTD(query);
	}
	public void test_toBase64Binary() throws Exception {

		String query = "toBase64BinaryTest(?x)";
		checkSTD(query);
	}
	public void test_toDate() throws Exception {

		String query = "toDateTest(?x)";
		checkSTD(query);
	}
	public void test_toDateTime() throws Exception {

		String query = "toDateTimeTest(?x)";
		checkSTD(query);
	}
	public void test_toDuration() throws Exception {

		String query = "toDurationTest(?x)";
		checkSTD(query);
	}
	public void test_toFloat() throws Exception {

		String query = "toFloatTest(?x)";
		checkSTD(query);
	}
	public void test_toGDay() throws Exception {

		String query = "toGDayTest(?x)";
		checkSTD(query);
	}
	public void test_toGMonth() throws Exception {

		String query = "toGMonthTest(?x)";
		checkSTD(query);
	}
	public void test_toGMonthDay() throws Exception {

		String query = "toGMonthDayTest(?x)";
		checkSTD(query);
	}
	public void test_toGYear() throws Exception {

		String query = "toGYearTest(?x)";
		checkSTD(query);
	}
	public void test_toGYearMonth() throws Exception {

		String query = "toGYearMonthTest(?x)";
		checkSTD(query);
	}
	public void test_toHexBinary() throws Exception {

		String query = "toHexBinaryTest(?x)";
		checkSTD(query);
	}
	public void test_toTime() throws Exception {

		String query = "toTimeText(?x)";
		checkSTD(query);
	}
	public void test_toDayTimeDuration() throws Exception {

		String query = "toDayTimeDurationTest(?x)";
		checkSTD(query);
	}
	public void test_toYearMonthDuration() throws Exception {

		String query = "toYearMonthDurationTest(?x)";
		checkSTD(query);
	}
	public void test_toText() throws Exception {

		String query = "toTextTest(?x)";
		checkSTD(query);
	}
	public void test_toXMLLiteral() throws Exception {

		String query = "toXMLLiteralTest(?x)";
		checkSTD(query);
	}
	
	// SECTION [D]
	public void test_numericModulus() throws Exception {

		String query = "numericModulusTest(?x)";
		checkSTD(query);
	}
	public void test_stringCompare() throws Exception {

		String query = "stringCompareTest(?x)";
		checkSTD(query);
	}
	public void test_stringEqual() throws Exception {

		String query = "stringEqualTest(?x)";
		checkSTD(query);
	}
	public void test_stringConcat() throws Exception {

		String query = "stringConcatTest(?x)";
		checkSTD(query);
	}
	public void test_stringJoin() throws Exception {

		String query = "stringJoinTest(?x)";
		checkSTD(query);
	}
	public void test_stringSubstring() throws Exception {

		String query = "stringSubstringTest(?x)";
		checkSTD(query);
	}
	public void test_stringLength() throws Exception {

		String query = "stringLengthTest(?x)";
		checkSTD(query);
	}
	public void test_stringToUpper() throws Exception {

		String query = "stringToUpperTest(?x)";
		checkSTD(query);
	}
	public void test_stringToLower() throws Exception {

		String query = "stringToLowerTest(?x)";
		checkSTD(query);
	}

	//SECTION [E]
	public void test_stringURIEncode() throws Exception {

		String query = "stringUriEncodeTest(?x)";
		checkSTD(query);
	}
	public void test_stringIritToUri() throws Exception {

		String query = "stringIriToUriTest(?x)";
		checkSTD(query);
	}
	public void test_stringEscapeHtmlUri() throws Exception {

		String query = "stringEscapeHtmlUriTest(?x)";
		checkSTD(query);
	}
	public void test_stringSubstringBefore() throws Exception {

		String query = "stringSubstringBeforeTest(?x)";
		checkSTD(query);
	}
	public void test_stringSubstringAfter() throws Exception {

		String query = "stringSubstringAfterTest(?x)";
		checkSTD(query);
	}
	public void test_stringReplace() throws Exception {

		String query = "stringReplaceTest(?x)";
		checkSTD(query);
	}
	public void test_stringContains() throws Exception {

		String query = "stringContainsTest(?x)";
		checkSTD(query);
	}
	public void test_stringStartsWith() throws Exception {

		String query = "stringStartsWithTest(?x)";
		checkSTD(query);
	}
	public void test_stringEndsWith() throws Exception {

		String query = "stringEndsWithTest(?x)";
		checkSTD(query);
	}
	public void test_stringMatches() throws Exception {

		String query = "stringMatchesTest(?x)";
		checkSTD(query);
	}
	
	// SECTION [F]
	public void test_yearPart() throws Exception {

		String query = "yearPartTest1(?x)";
		checkSTD(query);
		
		query = "yearPartTest2(?x)";
		checkSTD(query);
		
		query = "yearPartTest3(?x)";
		checkSTD(query);
	}
	public void test_monthPart() throws Exception {

		String query = "monthPartTest1(?x)";
		checkSTD(query);
		
		query = "monthPartTest2(?x)";
		checkSTD(query);
		
		query = "monthPartTest3(?x)";
		checkSTD(query);
	}
	public void test_dayPart() throws Exception {

		String query = "dayPartTest1(?x)";
		checkSTD(query);
		
		query = "dayPartTest2(?x)";
		checkSTD(query);
		
		query = "dayPartTest3(?x)";
		checkSTD(query);
	}
	public void test_hourPart() throws Exception {

		String query = "hourPartTest1(?x)";
		checkSTD(query);
		
		query = "hourPartTest2(?x)";
		checkSTD(query);
		
		query = "hourPartTest3(?x)";
		checkSTD(query);
	}
	public void test_minutePart() throws Exception {

		String query = "minutePartTest1(?x)";
		checkSTD(query);
		
		query = "minutePartTest2(?x)";
		checkSTD(query);
		
		query = "minutePartTest3(?x)";
		checkSTD(query);
	}
	public void test_secondPart() throws Exception {

		String query = "secondPartTest1(?x)";
		checkSTD(query);
		
		query = "secondPartTest2(?x)";
		checkSTD(query);
		
		query = "secondPartTest3(?x)";
		checkSTD(query);
	}
	public void test_timezonePart() throws Exception {

		String query = "timezonePartTest1(?x)";
		checkSTD(query);
		
//		query = "timezonePartTest2(?x)";
//		checkSTD(query);
//		
//		query = "timezonePartTest3(?x)";
//		checkSTD(query);
	}
	
	// SECTION [G]
	public void test_textFromStringLang() throws Exception {

		String query = "textFromStringLangTest(?x)";
		checkSTD(query);
	}
	public void test_textFromString() throws Exception {

		String query = "textFromStringTest(?x)";
		checkSTD(query);
	}
	public void test_stringFromText() throws Exception {

		String query = "stringFromTextTest(?x)";
		checkSTD(query);
	}
	public void test_langFromText() throws Exception {

		String query = "langFromTextTest(?x)";
		checkSTD(query);
	}
	public void test_textCompare() throws Exception {

		String query = "textCompareTest(?x)";
		checkSTD(query);
	}
	public void test_textLength() throws Exception {

		String query = "textLengthTest(?x)";
		checkSTD(query);
	}
	
	/**
	 * Helper to test the queries.
	 * Queries should always return 2 values.
	 * 
	 * @param query
	 * @throws Exception
	 */
	private void checkSTD(String query) throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri("http://example.com/builtin01#B"));
		r.addBinding(Results.iri("http://example.com/builtin01#A"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}

}
