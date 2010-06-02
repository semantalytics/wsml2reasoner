package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

/**
 * To test rif data types see:
 * http://www.w3.org/2005/rules/wiki/DTB#Primitive_Datatypes
 */
public abstract class AbstractRIFBuiltinTest extends TestCase implements LP {

	protected static final String ONTOLOGY_FILE = "BuiltinRIFTest.wsml";

	protected LPReasoner reasoner;

	protected static String query;

	protected LinkedList<String> queries;

	protected static boolean OUTPUT = false;

	public AbstractRIFBuiltinTest() {
		reasoner = getLPReasoner();
		if (OUTPUT) {
			LPHelper.outputON();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		queries = new LinkedList<String>();
		query = new String("");
	}

	// 
	public void testFile() throws Exception {
		query = "m(?x)";
		checkSTD(query);
	}

	// rif is-literal built-ins
	public void testisAnyURITest() throws Exception {
		query = "isAnyURITest(?x)";
		checkSTD(query);
	}

	public void testisBase64Binary() throws Exception {
		query = "isBase64BinaryTest(?x)";
		checkSTD(query);
	}

	public void testisBoolean() throws Exception {
		query = "isBooleanTest(?x)";
		checkSTD(query);
	}

	public void testisDate() throws Exception {
		query = "isDateTest(?x)";
		checkSTD(query);
	}

	public void testisDateTime() throws Exception {
		query = "isDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testisDateTimeStamp() throws Exception {
		query = "isDateTimeStampTest(?x)";
		checkSTD(query);
	}

	public void testisDouble() throws Exception {
		query = "isDoubleTest(?x)";
		checkSTD(query);
	}

	public void testisFloat() throws Exception {
		query = "isFloatTest(?x)";
		checkSTD(query);
	}

	public void testisHexBinary() throws Exception {
		query = "isHexBinaryTest(?x)";
		checkSTD(query);
	}

	public void testisDecimal() throws Exception {
		query = "isDecimalTest(?x)";
		checkSTD(query);
	}

	public void testisInteger() throws Exception {
		query = "isIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisLong() throws Exception {
		query = "isLongTest(?x)";
		checkSTD(query);
	}

	public void testisShort() throws Exception {
		query = "isShortTest(?x)";
		checkSTD(query);
	}

	public void testisByte() throws Exception {
		query = "isByteTest(?x)";
		checkSTD(query);
	}

	public void testisNonNegativeInteger() throws Exception {
		query = "isNonNegativeIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisPositiveInteger() throws Exception {
		query = "isPositiveIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisUnsignedLong() throws Exception {
		query = "isUnsignedLongTest(?x)";
		checkSTD(query);
	}

	public void testisUnsignedInt() throws Exception {
		query = "isUnsignedIntTest(?x)";
		checkSTD(query);
	}

	public void testisUnsignedShort() throws Exception {
		query = "isUnsignedShortTest(?x)";
		checkSTD(query);
	}

	public void testisUnsignedByte() throws Exception {
		query = "isUnsignedByteTest(?x)";
		checkSTD(query);
	}

	public void testisNonPositiveInteger() throws Exception {
		query = "isNonPositiveIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisNegativeInteger() throws Exception {
		query = "isNegativeIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisPlainLiteral() throws Exception {
		query = "isPlainLiteralTest(?x)";
		checkSTD(query);
	}

	public void testisStringTest() throws Exception {
		query = "isStringTest(?x)";
		checkSTD(query);
	}

	public void testisNormalizedString() throws Exception {
		query = "isNormalizedStringTest(?x)";
		checkSTD(query);
	}

	public void testisToken() throws Exception {
		query = "isTokenTest(?x)";
		checkSTD(query);
	}

	public void testisLanguage() throws Exception {
		query = "isLanguageTest(?x)";
		checkSTD(query);
	}

	public void testisName() throws Exception {
		query = "isNameTest(?x)";
		checkSTD(query);
	}

	public void testisNCName() throws Exception {
		query = "isNCNameTest(?x)";
		checkSTD(query);
	}

	public void testisNMToken() throws Exception {
		query = "isNMTokenTest(?x)";
		checkSTD(query);
	}

	public void testisTime() throws Exception {
		query = "isTimeTest(?x)";
		checkSTD(query);
	}

	public void testisDayTimeDuration() throws Exception {
		query = "isDayTimeDurationTest(?x)";
		checkSTD(query);
	}

	public void testisYearMonthDuration() throws Exception {
		query = "isYearMonthDurationTest(?x)";
		checkSTD(query);
	}

	public void testisXMLLiteral() throws Exception {
		query = "isXMLLiteralTest(?x)";
		checkSTD(query);
	}

	// rif is-not-literal built-ins
	public void testisNotAnyURITest() throws Exception {
		query = "isNotAnyURITest(?x)";
		checkSTD(query);
	}

	public void testisNotBase64Binary() throws Exception {
		query = "isNotBase64BinaryTest(?x)";
		checkSTD(query);
	}

	public void testisNotBoolean() throws Exception {
		query = "isNotBooleanTest(?x)";
		checkSTD(query);
	}

	public void testisNotDate() throws Exception {
		query = "isNotDateTest(?x)";
		checkSTD(query);
	}

	public void testisNotDateTime() throws Exception {
		query = "isNotDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testisNotDateTimeStamp() throws Exception {
		query = "isNotDateTimeStampTest(?x)";
		checkSTD(query);
	}

	public void testisNotDouble() throws Exception {
		query = "isNotDoubleTest(?x)";
		checkSTD(query);
	}

	public void testisNotFloat() throws Exception {
		query = "isNotFloatTest(?x)";
		checkSTD(query);
	}

	public void testisNotHexBinary() throws Exception {
		query = "isNotHexBinaryTest(?x)";
		checkSTD(query);
	}

	public void testisNotDecimal() throws Exception {
		query = "isNotDecimalTest(?x)";
		checkSTD(query);
	}

	public void testisNotInteger() throws Exception {
		query = "isNotIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisNotLong() throws Exception {
		query = "isNotLongTest(?x)";
		checkSTD(query);
	}

	public void testisNotShort() throws Exception {
		query = "isNotShortTest(?x)";
		checkSTD(query);
	}

	public void testisNotByte() throws Exception {
		query = "isNotByteTest(?x)";
		checkSTD(query);
	}

	public void testisNotNonNegativeInteger() throws Exception {
		query = "isNotNonNegativeIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisNotPositiveInteger() throws Exception {
		query = "isNotPositiveIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisNotUnsignedLong() throws Exception {
		query = "isNotUnsignedLongTest(?x)";
		checkSTD(query);
	}

	public void testisNotUnsignedInt() throws Exception {
		query = "isNotUnsignedIntTest(?x)";
		checkSTD(query);
	}

	public void testisNotUnsignedShort() throws Exception {
		query = "isNotUnsignedShortTest(?x)";
		checkSTD(query);
	}

	public void testisNotUnsignedByte() throws Exception {
		query = "isNotUnsignedByteTest(?x)";
		checkSTD(query);
	}

	public void testisNotNonPositiveInteger() throws Exception {
		query = "isNotNonPositiveIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisNotNegativeInteger() throws Exception {
		query = "isNotNegativeIntegerTest(?x)";
		checkSTD(query);
	}

	public void testisNotPlainLiteral() throws Exception {
		query = "isNotPlainLiteralTest(?x)";
		checkSTD(query);
	}

	public void testisNotStringTest() throws Exception {
		query = "isNotStringTest(?x)";
		checkSTD(query);
	}

	public void testisNotNormalizedString() throws Exception {
		query = "isNotNormalizedStringTest(?x)";
		checkSTD(query);
	}

	public void testisNotToken() throws Exception {
		query = "isNotTokenTest(?x)";
		checkSTD(query);
	}

	public void testisNotLanguage() throws Exception {
		query = "isNotLanguageTest(?x)";
		checkSTD(query);
	}

	public void testisNotName() throws Exception {
		query = "isNotNameTest(?x)";
		checkSTD(query);
	}

	public void testisNotNCName() throws Exception {
		query = "isNotNCNameTest(?x)";
		checkSTD(query);
	}

	public void testisNotNMToken() throws Exception {
		query = "isNotNMTokenTest(?x)";
		checkSTD(query);
	}

	public void testisNotTime() throws Exception {
		query = "isNotTimeTest(?x)";
		checkSTD(query);
	}

	public void testisNotDayTimeDuration() throws Exception {
		query = "isNotDayTimeDurationTest(?x)";
		checkSTD(query);
	}

	public void testisNotYearMonthDuration() throws Exception {
		query = "isNotYearMonthDurationTest(?x)";
		checkSTD(query);
	}

	public void testisNotXMLLiteral() throws Exception {
		query = "isNotXMLLiteralTest(?x)";
		checkSTD(query);
	}

	// rif numerics built-ins
	public void testnumericAdd() throws Exception {
		query = "numericAddTest(?x)";
		checkSTD(query);
	}

	public void testnumericSubtract() throws Exception {
		query = "numericSubtractTest(?x)";
		checkSTD(query);
	}

	public void testnumericMultiply() throws Exception {
		query = "numericMultiplyTest(?x)";
		checkSTD(query);
	}

	public void testnumericDivide() throws Exception {
		query = "numericDivideTest(?x)";
		checkSTD(query);
	}

	public void testnumericIntegerDivide() throws Exception {
		query = "numericIntegerDivideTest(?x)";
		checkSTD(query);
	}

	public void testnumericMod() throws Exception {
		query = "numericModTest(?x)";
		checkSTD(query);
	}

	public void testnumericEqual() throws Exception {
		query = "numericEqualTest(?x)";
		checkSTD(query);
	}

	public void testnumericLessThan() throws Exception {
		query = "numericLessThanTest(?x)";
		checkSTD(query);
	}

	public void testnumericGreaterThan() throws Exception {
		query = "numericGreaterThanTest(?x)";
		checkSTD(query);
	}

	public void testnumericNotEqual() throws Exception {
		query = "numericNotEqualTest(?x)";
		checkSTD(query);
	}

	public void testnumericLessThanOrEqual() throws Exception {
		query = "numericLessThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testnumericGreaterThanOrEqual() throws Exception {
		query = "numericGreaterThanOrEqualTest(?x)";
		checkSTD(query);
	}

	// rif boolean built-ins
	public void testnot() throws Exception {
		query = "notTest(?x)";
		checkSTD(query);
	}

	public void testbooleanEqual() throws Exception {
		query = "booleanEqualTest(?x)";
		checkSTD(query);
	}

	public void testbooleanLessThan() throws Exception {
		query = "booleanLessThanTest(?x)";
		checkSTD(query);
	}

	public void testbooleanGreaterThan() throws Exception {
		query = "booleanGreaterThanTest(?x)";
		checkSTD(query);
	}

	// rif String built-ins
	public void testcompare() throws Exception {
		query = "compareTest(?x)";
		checkSTD(query);
	}

	public void testconcat() throws Exception {
		query = "concatTest(?x)";
		checkSTD(query);
	}

	public void teststringJoin() throws Exception {
		query = "stringJoinTest(?x)";
		checkSTD(query);
	}

	public void testsubString() throws Exception {
		query = "subStringTest(?x)";
		checkSTD(query);
	}

	public void teststringLength() throws Exception {
		query = "stringLengthTest(?x)";
		checkSTD(query);
	}

	public void testupperCase() throws Exception {
		query = "upperCaseTest(?x)";
		checkSTD(query);
	}

	public void testlowerCase() throws Exception {
		query = "lowerCaseTest(?x)";
		checkSTD(query);
	}

	public void testencodeForUri() throws Exception {
		query = "encodeForUriTest(?x)";
		checkSTD(query);
	}

	public void testiriToUri() throws Exception {
		query = "iriToUriTest(?x)";
		checkSTD(query);
	}

	public void testescapeHtmlUri() throws Exception {
		query = "escapeHtmlUriTest(?x)";
		checkSTD(query);
	}

	public void testsubstringBefore() throws Exception {
		query = "substringBeforeTest(?x)";
		checkSTD(query);
	}

	public void testsubstringAfter() throws Exception {
		query = "substringAfterTest(?x)";
		checkSTD(query);
	}

	public void testreplace() throws Exception {
		query = "replaceTest(?x)";
		checkSTD(query);
	}

	public void testcontains() throws Exception {
		query = "containsTest(?x)";
		checkSTD(query);
	}

	public void teststartsWith() throws Exception {
		query = "startsWithTest(?x)";
		checkSTD(query);
	}

	public void testendsWith() throws Exception {
		query = "endsWithTest(?x)";
		checkSTD(query);
	}

	public void testmatches() throws Exception {
		query = "matchesTest(?x)";
		checkSTD(query);
	}

	// rif date time built-ins
	public void testyearFromDateTime() throws Exception {
		query = "yearFromDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testmonthFromDateTime() throws Exception {
		query = "monthFromDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testdayFromDateTime() throws Exception {
		query = "dayFromDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testhoursFromDateTime() throws Exception {
		query = "hoursFromDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testminutesFromDateTime() throws Exception {
		query = "minutesFromDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testsecondsFromDateTime() throws Exception {
		query = "secondsFromDateTimeTest(?x)";
		checkSTD(query);
	}

	// rif date part built-ins
	public void testyearFromDate() throws Exception {
		query = "yearFromDateTest(?x)";
		checkSTD(query);
	}

	public void testmonthFromDate() throws Exception {
		query = "monthFromDateTest(?x)";
		checkSTD(query);
	}

	public void testdayFromDate() throws Exception {
		query = "dayFromDateTest(?x)";
		checkSTD(query);
	}

	public void testhoursFromDate() throws Exception {
		query = "hoursFromDateTest(?x)";
		checkSTD(query);
	}

	public void testminutesFromDate() throws Exception {
		query = "minutesFromDateTest(?x)";
		checkSTD(query);
	}

	public void testsecondsFromDate() throws Exception {
		query = "secondsFromDateTest(?x)";
		checkSTD(query);
	}

	// rif duration built-ins
	public void yearsFromDuration() throws Exception {
		query = "yearsFromDurationTest(?x)";
		checkSTD(query);
	}

	public void testmonthsFromDuration() throws Exception {
		query = "monthsFromDurationTest(?x)";
		checkSTD(query);
	}

	public void testdaysFromDuration() throws Exception {
		query = "daysFromDurationTest(?x)";
		checkSTD(query);
	}

	public void testhoursFromDuration() throws Exception {
		query = "hoursFromDurationTest(?x)";
		checkSTD(query);
	}

	public void testminutesFromDuration() throws Exception {
		query = "minutesFromDurationTest(?x)";
		checkSTD(query);
	}

	public void testsecondsFromDuration() throws Exception {
		query = "secondsFromDurationTest(?x)";
		checkSTD(query);
	}

	// rif timezone built-ins
	public void testtimezoneFromDateTime() throws Exception {
		query = "timezoneFromDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testtimezoneFromDate() throws Exception {
		query = "timezoneFromDateTest(?x)";
		checkSTD(query);
	}

	public void testtimezoneFromTime() throws Exception {
		query = "timezoneFromTimeTest(?x)";
		checkSTD(query);
	}

	// rif date time manipulation built-ins
	public void testsubtractDateTimes() throws Exception {
		query = "subtractDateTimesTest(?x)";
		checkSTD(query);
	}

	public void testsubtractDates() throws Exception {
		query = "subtractDatesTest(?x)";
		checkSTD(query);
	}

	public void testsubtractTimes() throws Exception {
		query = "subtractTimesTest(?x)";
		checkSTD(query);
	}

	// rif year month duration manipulation built-ins
	public void testaddYearMonthDurations() throws Exception {
		query = "addYearMonthDurationsTest(?x)";
		checkSTD(query);
	}

	public void testsubtractYearMonthDurations() throws Exception {
		query = "subtractYearMonthDurationsTest(?x)";
		checkSTD(query);
	}

	public void testmultiplyYearMonthDuration() throws Exception {
		query = "multiplyYearMonthDurationTest(?x)";
		checkSTD(query);
	}

	public void testdivideYearMonthDuration() throws Exception {
		query = "divideYearMonthDurationTest(?x)";
		checkSTD(query);
	}

	public void testdivideYearMonthDurationByYearMonthDuration()
			throws Exception {
		query = "divideYearMonthDurationByYearMonthDurationTest(?x)";
		checkSTD(query);
	}

	// rif day time duration manipulation built-ins
	public void testaddDayTimeDurations() throws Exception {
		query = "addDayTimeDurationsTest(?x)";
		checkSTD(query);
	}

	public void testsubtractDayTimeDurations() throws Exception {
		query = "subtractDayTimeDurationsTest(?x)";
		checkSTD(query);
	}

	public void testmultiplyDayTimeDuration() throws Exception {
		query = "multiplyDayTimeDurationTest(?x)";
		checkSTD(query);
	}

	public void testdivideDayTimeDuration() throws Exception {
		query = "divideDayTimeDurationTest(?x)";
		checkSTD(query);
	}

	public void testdivideDayTimeDurationBydayTimeDuration() throws Exception {
		query = "divideDayTimeDurationBydayTimeDurationTest(?x)";
		checkSTD(query);
	}

	// rif year month date time manipulation built-ins
	public void testaddYearMonthDurationToDateTime() throws Exception {
		query = "addYearMonthDurationToDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testaddYearMonthDurationToDate() throws Exception {
		query = "addYearMonthDurationToDateTest(?x)";
		checkSTD(query);
	}

	public void testaddDayTimeDurationToDateTime() throws Exception {
		query = "addDayTimeDurationToDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testaddDayTimeDurationToDate() throws Exception {
		query = "addDayTimeDurationToDateTest(?x)";
		checkSTD(query);
	}

	public void testaddDayTimeDurationToTime() throws Exception {
		query = "addDayTimeDurationToTimeTest(?x)";
		checkSTD(query);
	}

	public void testsubtractYearMonthDurationFromDateTime() throws Exception {
		query = "subtractYearMonthDurationFromDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testsubtractYearMonthDurationFromDate() throws Exception {
		query = "subtractYearMonthDurationFromDateTest(?x)";
		checkSTD(query);
	}

	public void testsubtractDayTimeDurationFromDateTime() throws Exception {
		query = "subtractDayTimeDurationFromDateTimeTest(?x)";
		checkSTD(query);
	}

	public void testsubtractDayTimeDurationFromDate() throws Exception {
		query = "subtractDayTimeDurationFromDateTest(?x)";
		checkSTD(query);
	}

	public void testsubtractDayTimeDurationFromTime() throws Exception {
		query = "subtractDayTimeDurationFromTimeTest(?x)";
		checkSTD(query);
	}

	// rif date time boolean built-ins
	public void testdateTimeEqual() throws Exception {
		query = "dateTimeEqualTest(?x)";
		checkSTD(query);
	}

	public void testdateTimeLessThan() throws Exception {
		query = "dateTimeLessThanTest(?x)";
		checkSTD(query);
	}

	public void testdateTimeGreaterThan() throws Exception {
		query = "dateTimeGreaterThanTest(?x)";
		checkSTD(query);
	}

	// rif date boolean built-ins
	public void testdateEqual() throws Exception {
		query = "dateEqualTest(?x)";
		checkSTD(query);
	}

	public void testdateLessThan() throws Exception {
		query = "dateLessThanTest(?x)";
		checkSTD(query);
	}

	public void testdateGreaterThan() throws Exception {
		query = "dateGreaterThanTest(?x)";
		checkSTD(query);
	}

	// rif time boolean built-ins
	public void testtimeEqual() throws Exception {
		query = "timeEqualTest(?x)";
		checkSTD(query);
	}

	public void testtimeLessThan() throws Exception {
		query = "timeLessThanTest(?x)";
		checkSTD(query);
	}

	public void testtimeGreaterThan() throws Exception {
		query = "timeGreaterThanTest(?x)";
		checkSTD(query);
	}

	// rif boolean functions built-ins
	public void testdurationEqual() throws Exception {
		query = "durationEqualTest(?x)";
		checkSTD(query);
	}

	public void testdayTimeDurationLessThan() throws Exception {
		query = "dayTimeDurationLessThanTest(?x)";
		checkSTD(query);
	}

	public void testdayTimeDurationGreaterThan() throws Exception {
		query = "dayTimeDurationGreaterThanTest(?x)";
		checkSTD(query);
	}

	public void testyearMonthDurationLessThan() throws Exception {
		query = "yearMonthDurationLessThanTest(?x)";
		checkSTD(query);
	}

	public void testyearMonthDurationGreaterThan() throws Exception {
		query = "yearMonthDurationGreaterThanTest(?x)";
		checkSTD(query);
	}

	public void testdateTimeNotEqual() throws Exception {
		query = "dateTimeNotEqualTest(?x)";
		checkSTD(query);
	}

	public void testdateTimeLessThanOrEqual() throws Exception {
		query = "dateTimeLessThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testdateTimeGreaterThanOrEqual() throws Exception {
		query = "dateTimeGreaterThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testdateNotEqual() throws Exception {
		query = "dateNotEqualTest(?x)";
		checkSTD(query);
	}

	public void testdateLessThanOrEqual() throws Exception {
		query = "dateLessThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testdateGreaterThanOrEqual() throws Exception {
		query = "dateGreaterThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testtimeNotEqual() throws Exception {
		query = "timeNotEqualTest(?x)";
		checkSTD(query);
	}

	public void testtimeLessThanOrEqual() throws Exception {
		query = "timeLessThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testtimeGreaterThanOrEqual() throws Exception {
		query = "timeGreaterThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testdurationNotEqual() throws Exception {
		query = "durationNotEqualTest(?x)";
		checkSTD(query);
	}

	public void testdayTimeDurationLessThanOrEqual() throws Exception {
		query = "dayTimeDurationLessThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testdayTimeDurationGreaterThanOrEqual() throws Exception {
		query = "dayTimeDurationGreaterThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testyearMonthDurationLessThanOrEqual() throws Exception {
		query = "yearMonthDurationLessThanOrEqualTest(?x)";
		checkSTD(query);
	}

	public void testyearMonthDurationGreaterThanOrEqual() throws Exception {
		query = "yearMonthDurationGreaterThanOrEqualTest(?x)";
		checkSTD(query);
	}

	// xml-literal built-ins
	public void testxmlLiteralEqual() throws Exception {
		query = "xmlLiteralEqualTest(?x)";
		checkSTD(query);
	}

	public void testxmlLiteralNotEqual() throws Exception {
		query = "xmlLiteralNotEqualTest(?x)";
		checkSTD(query);
	}

	// rif plain-literal built-ins
	public void testplainLiteralFromStringLang() throws Exception {
		query = "plainLiteralFromStringLangTest(?x)";
		checkSTD(query);
	}

	public void teststringFromPlainLiteral() throws Exception {
		query = "stringFromPlainLiteralTest(?x)";
		checkSTD(query);
	}

	public void testlangFromPlainLiteral() throws Exception {
		query = "langFromPlainLiteralTest(?x)";
		checkSTD(query);
	}

	/**
	 * Helper to test a list of queries
	 * 
	 * @throws Exception
	 */
	protected void checkList(LinkedList<String> queries) throws Exception {

		if (OUTPUT) {
			System.out.println("Checking " + queries.size() + " queries...");
		}

		for (String q : queries) {
			checkSTD(q);
		}
	}

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

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}

}
