/**
 * 
 */
package concreteTests.irisStratified;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.ReasonerHelper;
import helper.Results;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

public class RIFBuiltin02Test extends TestCase {

	protected static final String ONTOLOGY_FILE = "files/BuiltinRIFTest01.wsml";

	protected LPReasoner reasoner;

	public RIFBuiltin02Test() {
		reasoner = ReasonerHelper
				.getLPReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
	}

	public void test_something() throws Exception {
		// String query = "booleanGreaterThanTest(?x)";
		// String query = "isDateTimeTest(?x)";
		// String query = "stringLengthTest(?x)";
		// String query = "upperCaseTest(?x)";
		// String query = "lowerCaseTest(?x)";
		// String query = "yearFromDateTime(?x)";
		// String query = "monthFromDateTime(?x)";
		// String query = "dayFromDateTest(?x)";
		// String query = "yearsFromDurationTest(?x)";
		// String query = "timezoneFromDateTimeTest(?x)";
		// String query = "addYearMonthDurationsTest(?x)";
		// String query = "dateTimeLessThanTest(?x)";

		ArrayList<String> queries = new ArrayList<String>();
		// toTest.add(new String("dateTimeEqualTest(?x)"));
		// toTest.add(new String("dateTimeLessThanTest(?x)"));
		// toTest.add(new String("dateTimeGreaterThanTest(?x)"));
		//		
		// toTest.add(new String("dateEqualTest(?x)"));
		// toTest.add(new String("dateLessThanTest(?x)"));
		// toTest.add(new String("dateGreaterThanTest(?x)"));
		//		
		// toTest.add(new String("timeEqualTest(?x)"));
		// toTest.add(new String("timeLessThanTest(?x)"));
		// toTest.add(new String("timeGreaterThanTest(?x)"));
		//		
		// toTest.add(new String("durationEqualTest(?x)"));
		//		
		// toTest.add(new String("dayTimeDurationLessThanTest(?x)"));
		// toTest.add(new String("dayTimeDurationGreaterThanTest(?x)"));
		//		
		// toTest.add(new String("yearMonthDurationLessThanTest(?x)"));
		// toTest.add(new String("yearMonthDurationGreaterThanTest(?x)"));
		//		
		// // toTest.add(new String("dateTimeNotEqualTest(?x)")); // TODO mp
		// error ?
		// toTest.add(new String("dateTimeLessThanOrEqualTest(?x)"));
		// toTest.add(new String("dateTimeGreaterThanOrEqualTest(?x)"));
		//		
		// toTest.add(new String("dateNotEqualTest(?x)"));
		// toTest.add(new String("dateLessThanOrEqualTest(?x)"));
		// toTest.add(new String("dateGreaterThanOrEqualTest(?x)"));
		//		
		// toTest.add(new String("timeNotEqualTest(?x)"));
		// toTest.add(new String("timeLessThanOrEqualTest(?x)"));
		// toTest.add(new String("timeGreaterThanOrEqualTest(?x)"));
		//		
		// // toTest.add(new String("durationNotEqualTest(?x)")); // TODO error
		//		
		// toTest.add(new String("dayTimeDurationLessThanOrEqualTest(?x)"));
		// toTest.add(new String("dayTimeDurationGreaterThanOrEqualTest(?x)"));
		//		
		// toTest.add(new String("yearMonthDurationLessThanOrEqualTest(?x)"));
		// toTest.add(new
		// String("yearMonthDurationGreaterThanOrEqualTest(?x)"));
		//		
		// toTest.add(new String("xmlLiteralEqualTest(?x)"));
		// toTest.add(new String("xmlLiteralNotEqualTest(?x)")); // TODO error

		// queries.add("booleanEqualTest(?x)");
		// queries.add("booleanLessThanTest(?x)");
		// queries.add("booleanGreaterThanTest(?x)");

		// queries.add("concatTest(?x)");
		// queries.add("stringJoinTest(?x)");
		// queries.add("subStringTest(?x)");
		// queries.add("stringLengthTest(?x)");
		// queries.add("upperCaseTest(?x)");
		// queries.add("lowerCaseTest(?x)");
		// queries.add("encodeForUriTest(?x)");
		// queries.add("iriToUriTest(?x)");
		// queries.add("escapeHtmlUri(?x)");
		// queries.add("substringBeforeTest(?x)");
		// queries.add("substringAfterTest(?x)");
		// queries.add("replaceTest(?x)");
		// queries.add("containsTest(?x)");
		// queries.add("startsWithTest(?x)");
		// queries.add("endsWithTest(?x)");
		// queries.add("matches(?x)");

		// queries.add("monthFromDateTest(?x)");
		// queries.add("dayFromDateTest(?x)");
		// queries.add("hoursFromDateTest(?x)");
		// queries.add("minutesFromDateTimeTest(?x)");
		// queries.add("secondsFromDateTimeTest(?x)");

		// queries.add("isLongTest(?x)");
		// queries.add("isShortTest(?x)");
		// queries.add("isByteTest(?x)");
		queries.add("isNonPositiveIntegerTest(?x)");

		// queries.add("isLanguageTest(?x)");
		// queries.add("isNameTest(?x)");
		// queries.add("isNCNameTest(?x)");
		// queries.add("isNMTokenTest(?x)");
		// queries.add("isNotNonNegativeIntegerTest(?x)");

		// toTest.add(new String("plainLiteralFromStringLangTest(?x)")); // TODO
		// error
		// toTest.add(new String("stringFromPlainLiteralTest(?x)"));
		// toTest.add(new String("langFromPlainLiteralTest(?x)"));

		queries.add("isUnsignedShortTest(?x)");
		queries.add("isUnsignedByteTest(?x)");
		// queries.add("isNonPositiveIntegerTest(?x)");
		queries.add("isDateTimeStampTest(?x)");
		queries.add("isNormalizedStringTest(?x)");
		queries.add("isTokenTest(?x)");
		queries.add("isLanguageTest(?x)");
		queries.add("isNameTest(?x)");
		queries.add("isNCNameTest(?x)");
		// queries.add("isNMTokenTest(?x)");

		for (String query : queries) {
			checkSTD(query);
		}
	}

	// WSML BUILTIN TESTS
	// SECTION
	// public void testToDouble() throws Exception {
	//
	// String query = "toDoubleTest(?result, ?x)";
	// checkSTD(query);
	// }
	//
	// public void testToString() throws Exception {
	//
	// String query = "toStringTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToDecimal() throws Exception {
	//
	// String query = "toDecimalTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToBoolean() throws Exception {
	//
	// String query = "toBooleanTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToInteger() throws Exception {
	//
	// String query = "toIntegerTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToBase64Binary() throws Exception {
	//
	// String query = "toBase64BinaryTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToDate() throws Exception {
	//
	// String query = "toDateTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToDateTime() throws Exception {
	//
	// String query = "toDateTimeTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToDuration() throws Exception {
	//
	// String query = "toDurationTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToFloat() throws Exception {
	//
	// String query = "toFloatTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToGDay() throws Exception {
	//
	// String query = "toGDayTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToGMonth() throws Exception {
	//
	// String query = "toGMonthTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToGMonthDay() throws Exception {
	//
	// String query = "toGMonthDayTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToGYear() throws Exception {
	//
	// String query = "toGYearTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToGYearMonth() throws Exception {
	//
	// String query = "toGYearMonthTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToHexBinary() throws Exception {
	//
	// String query = "toHexBinaryTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToTime() throws Exception {
	//
	// String query = "toTimeText(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToDayTimeDuration() throws Exception {
	//
	// String query = "toDayTimeDurationTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToYearMonthDuration() throws Exception {
	//
	// String query = "toYearMonthDurationTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToText() throws Exception {
	//
	// String query = "toTextTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testToXMLLiteral() throws Exception {
	//
	// String query = "toXMLLiteralTest(?x)";
	// checkSTD(query);
	// }
	//
	// // SECTION
	// public void testNumericModulus() throws Exception {
	//
	// String query = "numericModulusTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringCompare() throws Exception {
	//
	// String query = "stringCompareTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringEqual() throws Exception {
	//
	// String query = "stringEqualTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringConcat() throws Exception {
	//
	// String query = "stringConcatTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringJoin() throws Exception {
	//
	// String query = "stringJoinTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringSubstring() throws Exception {
	//
	// String query = "stringSubstringTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringLength() throws Exception {
	//
	// String query = "stringLengthTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringToUpper() throws Exception {
	//
	// String query = "stringToUpperTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringToLower() throws Exception {
	//
	// String query = "stringToLowerTest(?x)";
	// checkSTD(query);
	// }
	//
	// // SECTION [F]
	// public void testStringURIEncode() throws Exception {
	//
	// String query = "stringUriEncodeTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringIritToUri() throws Exception {
	//
	// String query = "stringIriToUriTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringEscapeHtmlUri() throws Exception {
	//
	// String query = "stringEscapeHtmlUriTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringSubstringBefore() throws Exception {
	//
	// String query = "stringSubstringBeforeTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringSubstringAfter() throws Exception {
	//
	// String query = "stringSubstringAfterTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringReplace() throws Exception {
	//
	// String query = "stringReplaceTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringContains() throws Exception {
	//
	// String query = "stringContainsTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringStartsWith() throws Exception {
	//
	// String query = "stringStartsWithTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringEndsWith() throws Exception {
	//
	// String query = "stringEndsWithTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringMatches() throws Exception {
	//
	// String query = "stringMatchesTest(?x)";
	// checkSTD(query);
	// }
	//
	// // SECTION [G]
	// public void testYearPart() throws Exception {
	//
	// String query = "yearPartTest1(?x)";
	// checkSTD(query);
	//
	// query = "yearPartTest2(?x)";
	// checkSTD(query);
	//
	// query = "yearPartTest3(?x)";
	// checkSTD(query);
	// }
	//
	// public void testMonthPart() throws Exception {
	//
	// String query = "monthPartTest1(?x)";
	// checkSTD(query);
	//
	// query = "monthPartTest2(?x)";
	// checkSTD(query);
	//
	// query = "monthPartTest3(?x)";
	// checkSTD(query);
	// }
	//
	// public void testDayPart() throws Exception {
	//
	// String query = "dayPartTest1(?x)";
	// checkSTD(query);
	//
	// query = "dayPartTest2(?x)";
	// checkSTD(query);
	//
	// query = "dayPartTest3(?x)";
	// checkSTD(query);
	// }
	//
	// public void testHourPart() throws Exception {
	//
	// String query = "hourPartTest1(?x)";
	// checkSTD(query);
	//
	// query = "hourPartTest2(?x)";
	// checkSTD(query);
	//
	// query = "hourPartTest3(?x)";
	// checkSTD(query);
	// }
	//
	// public void testMinutePart() throws Exception {
	//
	// String query = "minutePartTest1(?x)";
	// checkSTD(query);
	//
	// query = "minutePartTest2(?x)";
	// checkSTD(query);
	//
	// query = "minutePartTest3(?x)";
	// checkSTD(query);
	// }
	//
	// public void testSecondPart() throws Exception {
	//
	// String query = "secondPartTest1(?x)";
	// checkSTD(query);
	//
	// query = "secondPartTest2(?x)";
	// checkSTD(query);
	//
	// query = "secondPartTest3(?x)";
	// checkSTD(query);
	// }
	//
	// public void testTimezonePart() throws Exception {
	//
	// String query = "timezonePartTest1(?x)";
	// checkSTD(query);
	//
	// query = "timezonePartTest2(?x)";
	// checkSTD(query);
	//
	// query = "timezonePartTest3(?x)";
	// checkSTD(query);
	// }
	//
	// // SECTION [H]
	// public void testTextFromStringLang() throws Exception {
	//
	// String query = "textFromStringLangTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testTextFromString() throws Exception {
	//
	// String query = "textFromStringTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testStringFromText() throws Exception {
	//
	// String query = "stringFromTextTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testLangFromText() throws Exception {
	//
	// String query = "langFromTextTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testTextCompare() throws Exception {
	//
	// String query = "textCompareTest(?x)";
	// checkSTD(query);
	// }
	//
	// public void testTextLength() throws Exception {
	//
	// String query = "textLengthTest(?x)";
	// checkSTD(query);
	// }

	/**
	 * Helper to test the queries. Queries should always return 2 values.
	 * 
	 * @param query
	 * @throws Exception
	 */
	private void checkSTD(String query) throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri("http://example.com/builtin01#B"));
		r.addBinding(Results.iri("http://example.com/builtin01#A"));

		LPHelper.outputON();

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}

}
