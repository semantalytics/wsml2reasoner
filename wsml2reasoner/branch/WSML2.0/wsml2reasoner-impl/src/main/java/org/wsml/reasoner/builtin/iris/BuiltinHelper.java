package org.wsml.reasoner.builtin.iris;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.BUILTIN;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IIri;
import org.deri.iris.builtins.datatype.IsBase64BinaryBuiltin;
import org.deri.iris.builtins.datatype.IsBooleanBuiltin;
import org.deri.iris.builtins.datatype.IsDateBuiltin;
import org.deri.iris.builtins.datatype.IsDateTimeBuiltin;
import org.deri.iris.builtins.datatype.IsDayTimeDurationBuiltin;
import org.deri.iris.builtins.datatype.IsDecimalBuiltin;
import org.deri.iris.builtins.datatype.IsDoubleBuiltin;
import org.deri.iris.builtins.datatype.IsDurationBuiltin;
import org.deri.iris.builtins.datatype.IsFloatBuiltin;
import org.deri.iris.builtins.datatype.IsGDayBuiltin;
import org.deri.iris.builtins.datatype.IsGMonthBuiltin;
import org.deri.iris.builtins.datatype.IsGMonthDayBuiltin;
import org.deri.iris.builtins.datatype.IsGYearBuiltin;
import org.deri.iris.builtins.datatype.IsGYearMonthBuiltin;
import org.deri.iris.builtins.datatype.IsHexBinaryBuiltin;
import org.deri.iris.builtins.datatype.IsIntegerBuiltin;
import org.deri.iris.builtins.datatype.IsPlainLiteralBuiltin;
import org.deri.iris.builtins.datatype.IsStringBuiltin;
import org.deri.iris.builtins.datatype.IsTimeBuiltin;
import org.deri.iris.builtins.datatype.IsXMLLiteralBuiltin;
import org.deri.iris.builtins.datatype.IsYearMonthDurationBuiltin;
import org.omwg.logicalexpression.Constants;
import org.omwg.ontology.RDFDataType;
import org.omwg.ontology.WsmlDataType;
import org.omwg.ontology.XmlSchemaDataType;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.WSML2DatalogTransformer;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsmo.common.BuiltIn;
import org.wsmo.common.RifBuiltIn;

public class BuiltinHelper {

	/**
	 * Returns the IRIS builtin
	 * 
	 * @param headLiteral
	 * @param sym
	 * @param terms
	 * @return iris builtin or an basic atom if no builtin fits.
	 */
	public static IAtom checkBuiltin(boolean headLiteral, String sym,
			List<ITerm> terms) {

		BuiltIn wsmlBuiltIn = BuiltIn.from(sym);

		RifBuiltIn rifBuiltIn = RifBuiltIn.from(sym);

		if (rifBuiltIn != null) {
			return getRIFBuiltin(headLiteral, sym, terms, rifBuiltIn);
		} 
		
		if (wsmlBuiltIn != null) {
			return getWSMLbuiltin(headLiteral, sym, terms, wsmlBuiltIn);
		}
		
		if (!headLiteral
				&& sym.equals(WSML2DatalogTransformer.PRED_MEMBER_OF)) {
			return checkWSMLmemberOf(headLiteral, sym, terms);
		}
		
		if (sym.equals(Constants.WSML_TRUE)) {
			return BUILTIN.createTrue();
		}
		if (sym.equals(Constants.WSML_FALSE)) {
			return BUILTIN.createFalse();
		}
		
				// Is not a built-in - return an ordinary term
		return BASIC.createAtom(
				BASIC.createPredicate(sym, terms.size()), 
				BASIC.createTuple(terms));

		// return an ordinary atom
		// return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
		// BASIC
		// .createTuple(terms));
	}

	/**
	 * Returns the iris builtin.
	 * 
	 * @param headLiteral
	 * @param sym
	 * @param terms
	 * @param wsmlBuiltIn
	 * @return
	 */
	private static IAtom getWSMLbuiltin(boolean headLiteral, String sym,
			List<ITerm> terms, BuiltIn wsmlBuiltIn) {
		//
		// WSML BuiltIn /////////////////////////////////////////
		// http://www.wsmo.org/TR/d16/d16.1/v1.0/#cha:built-ins
		// NOTE mp: not all longer supported - RIF Built-ins replace them mostly
		//
		
		// term array for boolean built-ins.
		ITerm[] booleanTerms = toArray(terms);
		
		// term array for functional built-ins:
		// in wsml return value is on first position - in iris on last position,
		// thus sort list for iris
		ITerm[] functionalTerms = toArray(sortListForIRIS(terms));
		
		switch (wsmlBuiltIn) {
		case EQUAL:
			return BUILTIN.createEqual(booleanTerms);
		case DATE_EQUAL:
			return BUILTIN.createDateEqual(booleanTerms);
		case DATE_GREATER_THAN:
			return BUILTIN.createDateGreater(booleanTerms);
		case DATE_INEQUAL:
			return BUILTIN.createDateNotEqual(booleanTerms);
		case DATE_LESS_THAN:
			return BUILTIN.createDateLess(booleanTerms);
		case DATETIME_EQUAL:
			return BUILTIN.createDateTimeEqual(booleanTerms);
		case DATETIME_GREATER_THAN:
			return BUILTIN.createDateTimeGreater(booleanTerms);
		case DATETIME_INEQUAL:
			return BUILTIN.createDateTimeNotEqual(booleanTerms);
		case DATETIME_LESS_THAN:
			return BUILTIN.createDateTimeLess(booleanTerms);
		case DAYTIMEDURATION_EQUAL: 
			return BUILTIN.createDurationEqual(booleanTerms);
		case DAYTIMEDURATION_GREATER_THAN:
			return BUILTIN.createDayTimeDurationGreater(booleanTerms);
		case DAYTIMEDURATION_LESS_THAN:
			return BUILTIN.createDayTimeDurationLess(booleanTerms);
		case DURATION_EQUAL:
			return BUILTIN.createDurationEqual(booleanTerms);
		case DURATION_INEQUAL:
			return BUILTIN.createDurationNotEqual(booleanTerms);
		case GDAY_EQUAL: 
			return BUILTIN.createNumericEqual(booleanTerms);
		case GMONTH_EQUAL:
			return BUILTIN.createNumericEqual(booleanTerms);
		case GMONTHDAY_EQUAL: 
			return BUILTIN.createNumericEqual(booleanTerms);
		case GREATER_EQUAL:
			return BUILTIN.createGreaterEqual(booleanTerms);
		case GREATER_THAN:
			return BUILTIN.createGreater(booleanTerms);
		case GYEAR_EQUAL: 
			return BUILTIN.createNumericEqual(booleanTerms);
		case GYEARMONTH_EQUAL: 
			return BUILTIN.createNumericEqual(booleanTerms);
		case INEQUAL: 
			return BUILTIN.createUnequal(booleanTerms);
		case LESS_EQUAL:
			return BUILTIN.createLessEqual(booleanTerms);
		case LESS_THAN:
			return BUILTIN.createLess(booleanTerms);
		case NUMERIC_ADD:
			return BUILTIN.createNumericAdd(functionalTerms);
		case NUMERIC_DIVIDE:
			return BUILTIN.createNumericDivide(functionalTerms);
		case NUMERIC_MODULUS:
			return BUILTIN.createNumericModulus(functionalTerms);
		case NUMERIC_EQUAL:
			return BUILTIN.createNumericEqual(booleanTerms);
		case NUMERIC_GREATER_THAN:
			return BUILTIN.createNumericGreater(booleanTerms);
		case NUMERIC_INEQUAL:
			return BUILTIN.createNumericNotEqual(booleanTerms);
		case NUMERIC_LESS_THAN:
			return BUILTIN.createNumericLess(booleanTerms);
		case NUMERIC_MULTIPLY:
			return BUILTIN.createNumericMultiply(functionalTerms);
		case NUMERIC_SUBTRACT:
			return BUILTIN.createNumericSubtract(functionalTerms);
		case STRING_EQUAL: 
			return BUILTIN.createEqual(booleanTerms);
		case STRING_INEQUAL: 
			return BUILTIN.createUnequal(booleanTerms);
		case TIME_EQUAL:
			return BUILTIN.createTimeEqual(booleanTerms);
		case TIME_GREATER_THAN:
			return BUILTIN.createTimeGreater(booleanTerms);
		case TIME_INEQUAL:
			return BUILTIN.createTimeNotEqual(booleanTerms);
		case TIME_LESS_THAN:
			return BUILTIN.createTimeLess(booleanTerms);
		case YEARMONTHDURATION_EQUAL: 
			return BUILTIN.createDurationEqual(booleanTerms);
		case YEARMONTHDURATION_GREATER_THAN:
			return BUILTIN.createYearMonthDurationGreater(booleanTerms);
		case YEARMONTHDURATION_LESS_THAN:
			return BUILTIN.createYearMonthDurationLess(booleanTerms);
		case HAS_DATATYPE:
			throw new InternalReasonerException("WSML Built-in: " + wsmlBuiltIn.getName() + " not yet supported!");
		case TO_BASE64:
			return BUILTIN.createToBase64Binary(functionalTerms);
		case TO_BOOLEAN:
			return BUILTIN.createToBoolean(functionalTerms);
		case TO_DATE:
			return BUILTIN.createToDate(functionalTerms);
		case TO_DATETIME:
			return BUILTIN.createToDateTime(functionalTerms);
		case TO_DAYTIMEDURATION:
			return BUILTIN.createToDayTimeDuration(functionalTerms);
		case TO_DECIMAL:
			return BUILTIN.createToDecimal(functionalTerms);
		case TO_DOUBLE:
			return BUILTIN.createToDouble(functionalTerms);
		case TO_DURATION:
			return BUILTIN.createToDuration(functionalTerms);
		case TO_FLOAT:
			return BUILTIN.createToFloat(functionalTerms);
		case TO_GDAY:
			return BUILTIN.createToGDay(functionalTerms);
		case TO_GMONTH:
			return BUILTIN.createToGMonth(functionalTerms);
		case TO_GMONTHDAY:
			return BUILTIN.createToGMonthDay(functionalTerms);
		case TO_GYEAR:
			return BUILTIN.createToGYear(functionalTerms);
		case TO_GYEARMONTH:
			return BUILTIN.createToGYearMonth(functionalTerms);
		case TO_HEXBINARY:
			return BUILTIN.createToHexBinary(functionalTerms);
		case TO_INTEGER:
			return BUILTIN.createToInteger(functionalTerms);
		case TO_IRI:
			return BUILTIN.createToIRI(functionalTerms);
		case TO_STRING:
			return BUILTIN.createToString(functionalTerms);
		case TO_TEXT:
			return BUILTIN.createToText(functionalTerms);
		case TO_TIME:
			return BUILTIN.createToTime(functionalTerms);
		case TO_XMLLITERAL:
			return BUILTIN.createToXMLLiteral(functionalTerms);
		case TO_YEARMONTHDURATION:
			return BUILTIN.createToYearMonthDuration(functionalTerms);
		// end of TO_DATATYPE part
		case DAY_PART:
			return BUILTIN.createDayPart(functionalTerms);
		case HOUR_PART:
			return BUILTIN.createHourPart(functionalTerms);
		case YEAR_PART:
			return BUILTIN.createYearPart(functionalTerms);
		case TIMEZONE_PART:
			return BUILTIN.createTimezonePart(functionalTerms);
		case SECOND_PART:
			return BUILTIN.createSecondPart(functionalTerms);
		case MINUTE_PART:
			return BUILTIN.createMinutePart(functionalTerms);
		case MONTH_PART:
			return BUILTIN.createMonthPart(functionalTerms);
		case TEXT_COMPARE:
			return BUILTIN.createTextCompare(functionalTerms);
		case TEXT_EQUAL:
			return BUILTIN.createEqual(booleanTerms);
		case TEXT_INEQUAL:
			return BUILTIN.createUnequal(booleanTerms);
		case TEXT_FROM_STRING:
			return BUILTIN.createTextFromString(functionalTerms);
		case TEXT_FROM_STRING_LANG:
			return BUILTIN.createTextFromStringLang(functionalTerms);
		case TEXT_LENGTH:
			return BUILTIN.createTextLength(functionalTerms);
		case LANG_FROM_TEXT:
			return BUILTIN.createLangFromText(functionalTerms);
		case STRING_FROM_TEXT:
			return BUILTIN.createStringFromText(functionalTerms);
		// IS_DATATYPE part
		case IS_BASE64BINARY:
			return BUILTIN.createIsBase64Binary(functionalTerms);
		case IS_BOOLEAN:
			return BUILTIN.createIsBoolean(functionalTerms);
		case IS_DATATYPE:
			return BUILTIN.createIsDatatype(functionalTerms);
		case IS_DATE:
			return BUILTIN.createIsDate(functionalTerms);
		case IS_DATETIME:
			return BUILTIN.createIsDateTime(functionalTerms);
		case IS_DAYTIMEDURATION:  
			return BUILTIN.createIsDayTimeDuration(functionalTerms);
		case IS_YEARMONTHDURATION:
			return BUILTIN.createIsYearMonthDuration(functionalTerms);
		case IS_DECIMAL:
			return BUILTIN.createIsDecimal(functionalTerms);
		case IS_DOUBLE:
			return BUILTIN.createIsDouble(functionalTerms);
		case IS_DURATION:
			return BUILTIN.createIsDuration(functionalTerms);
		case IS_FLOAT:
			return BUILTIN.createIsFloat(functionalTerms);
		case IS_GDAY:
			return BUILTIN.createIsGDay(functionalTerms);
		case IS_GMONTH:
			return BUILTIN.createIsGMonth(functionalTerms);
		case IS_GMONTHDAY:
			return BUILTIN.createIsGMonthDay(functionalTerms);
		case IS_GYEAR:
			return BUILTIN.createIsGYear(functionalTerms);
		case IS_GYEARMONTH:
			return BUILTIN.createIsGYearMonth(functionalTerms);
		case IS_HEXBINARY:
			return BUILTIN.createIsHexBinary(functionalTerms);
		case IS_INTEGER:
			return BUILTIN.createIsInteger(functionalTerms);
		case IS_IRI:
			return BUILTIN.createIsIRI(functionalTerms);
		case IS_NOT_DATATYPE:
			return BUILTIN.createIsNotDatatype(functionalTerms);
		case IS_STRING:
			return BUILTIN.createIsString(functionalTerms);
		case IS_PLAINLITERAL:
			return BUILTIN.createIsPlainLiteral(functionalTerms);
		case IS_TIME:
			return BUILTIN.createIsTime(functionalTerms);
		case IS_XMLLITERAL:
			return BUILTIN.createIsXMLLiteral(functionalTerms);
		
		default:
			throw new InternalReasonerException("WSML Built-in: " + wsmlBuiltIn.getName() + " not yet supported!");
//			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),BASIC.createTuple(terms));
		}

	}

	/**
	 * Returns the iris builtin.
	 * 
	 * @param headLiteral
	 * @param sym
	 * @param terms
	 * @param rifbuiltIn
	 * @return
	 */
	private static IAtom getRIFBuiltin(boolean headLiteral, String sym,
			List<ITerm> terms, RifBuiltIn rifbuiltIn) {
		//
		// RIF BuiltIn ///////////////////////////////////////////
		// http://www.w3.org/2005/rules/wiki/DTB
		// 
		
		// term array for boolean built-ins.
		ITerm[] booleanTerms = toArray(terms);
		
		// term array for functional built-ins:
		// in wsml return value is on first position - in iris on last position,
		// thus sort list for iris
		ITerm[] functionalTerms = toArray(sortListForIRIS(terms));
		
		switch (rifbuiltIn) {
		case ADD_DAYTIMEDURATIONS: 
			return BUILTIN.createAddDayTimeDurations(functionalTerms);
		case ADD_DAYTIMEDURATION_TO_DATE:
			return BUILTIN.createAddDayTimeDurationToDate(functionalTerms);
		case ADD_DAYTIMEDURATION_TO_TIME:
			return BUILTIN.createAddDayTimeDurationToTime(functionalTerms);
		case ADD_DAYTIMEDURATION_TO_DATETIME:
			return BUILTIN.createAddDayTimeDurationToDateTime(functionalTerms);
		case ADD_YEARMONTHDURATIONS:
			return BUILTIN.createAddYearMonthDurations(functionalTerms);
		case ADD_YEARMONTHDURATION_TO_DATE:
			return BUILTIN.createAddYearMonthDurationToDate(functionalTerms);
		case ADD_YEARMONTHDURATION_TO_DATETIME:
			return BUILTIN.createAddYearMonthDurationToDateTime(functionalTerms);
		case BOOLEAN_EQUAL:
			return BUILTIN.createBooleanEqual(booleanTerms);
		case BOOLEAN_GREATER_THAN:
			return BUILTIN.createBooleanGreater(booleanTerms);
		case BOOLEAN_LESS_THAN:
			return BUILTIN.createBooleanLess(booleanTerms);
			
			// RIF String builtins
		case COMPARE:
			return BUILTIN.createStringCompare(functionalTerms);
		case CONCAT:
			return BUILTIN.createStringConcat(functionalTerms);
		case CONCATENATE: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		case CONTAINS:
			return BUILTIN.createStringContains(booleanTerms);
		case DATE_EQUAL:
			return BUILTIN.createDateEqual(booleanTerms);
		case DATE_NOT_EQUAL:
			return BUILTIN.createDateNotEqual(booleanTerms);
		case DATE_GREATER_THAN:
			return BUILTIN.createDateGreater(booleanTerms);
		case DATE_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createDateGreaterEqual(booleanTerms);
		case DATE_LESS_THAN:
			return BUILTIN.createDateLess(booleanTerms);
		case DATE_LESS_THAN_OR_EQUAL:
			return BUILTIN.createDateLessEqual(booleanTerms);
		case DATETIME_EQUAL:
			return BUILTIN.createDateTimeEqual(booleanTerms);
		case DATETIME_NOT_EQUAL:
			return BUILTIN.createDateTimeNotEqual(booleanTerms);
		case DATETIME_GREATER_THAN:
			return BUILTIN.createDateTimeGreater(booleanTerms);
		case DATETIME_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createDateTimeGreaterEqual(booleanTerms);
		case DATETIME_LESS_THAN:
			return BUILTIN.createDateTimeLess(booleanTerms);
		case DATETIME_LESS_THAN_OR_EQUAL:
			return BUILTIN.createDateTimeLessEqual(booleanTerms);
		case DAY_FROM_DATE:
			return BUILTIN.createDayFromDate(functionalTerms);
		case DAY_FROM_DATETIME:
			return BUILTIN.createDayFromDateTime(functionalTerms);
		case DAYS_FROM_DURATION:
			return BUILTIN.createDaysFromDuration(functionalTerms);
		case DAYTIMEDURATION_GREATER_THAN:
			return BUILTIN.createDayTimeDurationGreater(booleanTerms);
		case DAYTIMEDURATION_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createDayTimeDurationGreaterEqual(booleanTerms);
		case DAYTIMEDURATION_LESS_THAN:
			return BUILTIN.createDayTimeDurationLess(booleanTerms);
		case DAYTIMEDURATION_LESS_THAN_OR_EQUAL:
			return BUILTIN.createDayTimeDurationLessEqual(booleanTerms);
		case DISTINCT_VALUES: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
//			return BUILTIN.createDistinctValues(wsmlTerms);
		case DIVIDE_DAYTIMEDURATION:
			return BUILTIN.createDayTimeDurationDivide(functionalTerms);
		case DIVIDE_DAYTIMEDURATION_BY_DAYTIMEDURATION:
			return BUILTIN.createDayTimeDurationDivideByDayTimeDuration(functionalTerms);
		case DIVIDE_YEARMONTHDURATION:
			return BUILTIN.createYearMonthDurationDivide(functionalTerms);
		case DIVIDE_YEARMONTHDURATION_BY_YEARMONTHDURATION:
			return BUILTIN.createYearMonthDurationDivideByYearMonthDuration(functionalTerms);
		case DURATION_EQUAL:
			return BUILTIN.createDurationEqual(booleanTerms);
		case DURATION_NOT_EQUAL:
			return BUILTIN.createDurationNotEqual(booleanTerms);
		case ENCODE_FOR_URI:
			return BUILTIN.createStringUriEncode(functionalTerms);
		case ENDS_WITH:
			return BUILTIN.createStringEndsWith(booleanTerms);
		case ESCAPE_HTML_URI:
			return BUILTIN.createStringEscapeHtmlUri(functionalTerms);
		case EXCEPT: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
//			return BUILTIN.createExceptValues(wsmlTerms);
		case GET: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
//			return BUILTIN.createGet(wsmlTerms);
		case HOURS_FROM_DATETIME:
			return BUILTIN.createHoursFromDateTime(functionalTerms);
		case HOURS_FROM_DURATION:
			return BUILTIN.createHoursFromDuration(functionalTerms);
		case HOURS_FROM_TIME:
			return BUILTIN.createHoursFromTime(functionalTerms);
		case INDEX_OF:
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
//			return BUILTIN.createIndexOf(functionalTerms);
		case INSERT_BEFORE: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
//			return BUILTIN.createInsertBefore(functionalTerms);
		case INTERSECT: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
//			return BUILTIN.createIntersect(functionalTerms);
		case IRI_STRING:
			return BUILTIN.createIriString(functionalTerms);
		case IRI_TO_URI:
			return BUILTIN.createStringIriToUri(functionalTerms);
		case IS_LIST: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
//			return BUILTIN.createIsList(functionalTerms);
		case IS_LITERAL_ANYURI:
			return BUILTIN.createIsAnyURI(booleanTerms);
		case IS_LITERAL_BASE64BINARY:
			return BUILTIN.createIsBase64Binary(booleanTerms);
		case IS_LITERAL_BOOLEAN:
			return BUILTIN.createIsBoolean(booleanTerms);
		case IS_LITERAL_BYTE:
			return BUILTIN.createIsByte(booleanTerms);
		case IS_LITERAL_DATE:
			return BUILTIN.createIsDate(booleanTerms);
		case IS_LITERAL_DATETIME:
			return BUILTIN.createIsDateTime(booleanTerms);
		case IS_LITERAL_DATETIMESTAMP:
			return BUILTIN.createIsDateTimeStamp(booleanTerms);
		case IS_LITERAL_DAYTIMEDURATION:
			return BUILTIN.createIsDayTimeDuration(booleanTerms);
		case IS_LITERAL_DECIMAL:
			return BUILTIN.createIsDecimal(booleanTerms);
		case IS_LITERAL_DOUBLE:
			return BUILTIN.createIsDouble(booleanTerms);
		case IS_LITERAL_FLOAT:
			return BUILTIN.createIsFloat(booleanTerms);
		case IS_LITERAL_HEXBINARY:
			return BUILTIN.createIsHexBinary(booleanTerms);
		case IS_LITERAL_INT:
			return BUILTIN.createIsInt(booleanTerms);
		case IS_LITERAL_INTEGER:
			return BUILTIN.createIsInteger(booleanTerms);
		case IS_LITERAL_LANGUAGE:
			return BUILTIN.createIsLanguage(booleanTerms);
		case IS_LITERAL_LONG:
			return BUILTIN.createIsLong(booleanTerms);
		case IS_LITERAL_NAME:
			return BUILTIN.createIsName(booleanTerms);
		case IS_LITERAL_NCNAME:
			return BUILTIN.createIsNCName(booleanTerms);
		case IS_LITERAL_NEGATIVEINTEGER:
			return BUILTIN.createIsNegativeInteger(booleanTerms);
		case IS_LITERAL_NMTOKEN:
			return BUILTIN.createIsNMTOKEN(booleanTerms);
		case IS_LITERAL_NONNEGATIVEINTEGER:
			return BUILTIN.createIsNonNegativeInteger(booleanTerms);
		case IS_LITERAL_NONPOSITIVEINTEGER:
			return BUILTIN.createIsNonPositiveInteger(booleanTerms);
		case IS_LITERAL_NORMALIZEDSTRING:
			return BUILTIN.createIsNormalizedString(booleanTerms);
		case IS_LITERAL_NOT_ANYURI:
			return BUILTIN.createIsNotAnyURI(booleanTerms);
		case IS_LITERAL_NOT_BASE64BINARY:
			return BUILTIN.createIsNotBase64Binary(booleanTerms);
		case IS_LITERAL_NOT_BOOLEAN:
			return BUILTIN.createIsNotBoolean(booleanTerms);
		case IS_LITERAL_NOT_BYTE:
			return BUILTIN.createIsNotByte(booleanTerms);
		case IS_LITERAL_NOT_DATE:
			return BUILTIN.createIsNotDate(booleanTerms);
		case IS_LITERAL_NOT_DATETIME:
			return BUILTIN.createIsNotDateTime(booleanTerms);
		case IS_LITERAL_NOT_DATETIMESTAMP:
			return BUILTIN.createIsNotDateTimeStamp(booleanTerms);
		case IS_LITERAL_NOT_DAYTIMEDURATION:
			return BUILTIN.createIsNotDayTimeDuration(booleanTerms);
		case IS_LITERAL_NOT_DECIMAL:
			return BUILTIN.createIsNotDecimal(booleanTerms);
		case IS_LITERAL_NOT_DOUBLE:
			return BUILTIN.createIsNotDouble(booleanTerms);
		case IS_LITERAL_NOT_FLOAT:
			return BUILTIN.createIsNotFloat(booleanTerms);
		case IS_LITERAL_NOT_HEXBINARY:
			return BUILTIN.createIsNotHexBinary(booleanTerms);
		case IS_LITERAL_NOT_INTEGER:
			return BUILTIN.createIsNotInteger(booleanTerms);
		case IS_LITERAL_NOT_INT:
			return BUILTIN.createIsNotInt(booleanTerms);
		case IS_LITERAL_NOT_LANGUAGE:
			return BUILTIN.createIsNotLanguage(booleanTerms);
		case IS_LITERAL_NOT_LONG:
			return BUILTIN.createIsNotLong(booleanTerms);
		case IS_LITERAL_NOT_NAME:
			return BUILTIN.createIsNotName(booleanTerms);
		case IS_LITERAL_NOT_NCNAME:
			return BUILTIN.createIsNotNCName(booleanTerms);
		case IS_LITERAL_NOT_NEGATIVEINTEGER:
			return BUILTIN.createIsNotNegativeInteger(booleanTerms);
		case IS_LITERAL_NOT_NMTOKEN:
			return BUILTIN.createIsNotNMTOKEN(booleanTerms);
		case IS_LITERAL_NOT_NONNEGATIVEINTEGER:
			return BUILTIN.createIsNotNonNegativeInteger(booleanTerms);
		case IS_LITERAL_NOT_NONPOSITIVEINTEGER:
			return BUILTIN.createIsNotNonPositiveInteger(booleanTerms);
		case IS_LITERAL_NOT_NORMALIZEDSTRING:
			return BUILTIN.createIsNotNormalizedString(booleanTerms);
		case IS_LITERAL_NOT_PLAINLITERAL:
			return BUILTIN.createIsNotPlainLiteral(booleanTerms);
		case IS_LITERAL_NOT_POSITIVEINTEGER:
			return BUILTIN.createIsNotPositiveInteger(booleanTerms);
		case IS_LITERAL_NOT_SHORT:
			return BUILTIN.createIsNotShort(booleanTerms);
		case IS_LITERAL_NOT_STRING:
			return BUILTIN.createIsNotString(booleanTerms);
		case IS_LITERAL_NOT_TIME:
			return BUILTIN.createIsNotTime(booleanTerms);
		case IS_LITERAL_NOT_TOKEN:
			return BUILTIN.createIsNotToken(booleanTerms);
		case IS_LITERAL_NOT_UNSIGNEDBYTE:
			return BUILTIN.createIsNotUnsignedByte(booleanTerms);
		case IS_LITERAL_NOT_UNSIGNEDINT:
			return BUILTIN.createIsNotUnsignedInt(booleanTerms);
		case IS_LITERAL_NOT_UNSIGNEDLONG:
			return BUILTIN.createIsNotUnsignedLong(booleanTerms);
		case IS_LITERAL_NOT_UNSIGNEDSHORT:
			return BUILTIN.createIsNotUnsignedShort(booleanTerms);
		case IS_LITERAL_NOT_XMLLITERAL:
			return BUILTIN.createIsNotXMLLiteral(booleanTerms);
		case IS_LITERAL_NOT_YEARMONTHDURATION:
			return BUILTIN.createIsNotYearMonthDuration(booleanTerms);
		case IS_LITERAL_PLAINLITERAL:
			return BUILTIN.createIsPlainLiteral(booleanTerms);
		case IS_LITERAL_POSITIVEINTEGER:
			return BUILTIN.createIsPositiveInteger(booleanTerms);
		case IS_LITERAL_SHORT:
			return BUILTIN.createIsShort(booleanTerms);
		case IS_LITERAL_STRING:
			return BUILTIN.createIsString(booleanTerms);
		case IS_LITERAL_TIME:
			return BUILTIN.createIsTime(booleanTerms);
		case IS_LITERAL_TOKEN:
			return BUILTIN.createIsToken(booleanTerms);
		case IS_LITERAL_UNSIGNEDBYTE:
			return BUILTIN.createIsUnsignedByte(booleanTerms);
		case IS_LITERAL_UNSIGNEDINT:
			return BUILTIN.createIsUnsignedInt(booleanTerms);
		case IS_LITERAL_UNSIGNEDLONG:
			return BUILTIN.createIsUnsignedLong(booleanTerms);
		case IS_LITERAL_UNSIGNEDSHORT:
			return BUILTIN.createIsUnsignedShort(booleanTerms);
		case IS_LITERAL_XMLLITERAL:
			return BUILTIN.createIsXMLLiteral(booleanTerms);
		case IS_LITERAL_YEARMONTHDURATION:
			return BUILTIN.createIsYearMonthDuration(booleanTerms);
		case LANG_FROM_PLAINLITERAL: 				
			return BUILTIN.createLangFromText(functionalTerms);
		case LIST_CONTAINS: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		case LITERAL_NOT_IDENTICAL: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		case LOWER_CASE:
			return BUILTIN.createStringToLower(functionalTerms);
		case MAKE_LISTS: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		case MATCHES:
			return BUILTIN.createStringMatches(booleanTerms);
		case MATCHES_LANGUAGE_RANGE: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		case MINUTES_FROM_DATETIME:
			return BUILTIN.createMinutesFromDateTime(functionalTerms);
		case MINUTES_FROM_DURATION:
			return BUILTIN.createMinutesFromDuration(functionalTerms);
		case MINUTES_FROM_TIME:
			return BUILTIN.createMinutesFromTime(functionalTerms);
		case MONTH_FROM_DATE:
			return BUILTIN.createMonthFromDate(functionalTerms);
		case MONTH_FROM_DATETIME:
			return BUILTIN.createMonthFromDateTime(functionalTerms);
		case MONTHS_FROM_DURATION:
			return BUILTIN.createMonthsFromDuration(functionalTerms);
		case MULTIPLY_DAYTIMEDURATION: 
			return BUILTIN.createDayTimeDurationMultiply(functionalTerms);
		case MULTIPLY_YEARMONTHDURATION:
			return BUILTIN.createYearMonthDurationMultiply(functionalTerms);
		case NOT:
			return BUILTIN.createBooleanNot(booleanTerms);
		case NUMERIC_ADD:
			return BUILTIN.createNumericAdd(functionalTerms);
		case NUMERIC_DIVIDE:
			return BUILTIN.createNumericDivide(functionalTerms);
		case NUMERIC_INTEGER_DIVIDE:
			return BUILTIN.createNumericIntegerDivide(functionalTerms);
		case NUMERIC_EQUAL:
			return BUILTIN.createNumericEqual(booleanTerms);
		case NUMERIC_GREATER_THAN:
			return BUILTIN.createNumericGreater(booleanTerms);
		case NUMERIC_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createNumericGreaterEqual(booleanTerms);
		case NUMERIC_LESS_THAN:
			return BUILTIN.createNumericLess(booleanTerms);
		case NUMERIC_LESS_THAN_OR_EQUAL:
			return BUILTIN.createNumericLessEqual(booleanTerms);
		case NUMERIC_MOD:
			return BUILTIN.createNumericModulus(functionalTerms);
		case NUMERIC_MULTIPLY:
			return BUILTIN.createNumericMultiply(functionalTerms);
		case NUMERIC_NOT_EQUAL:
			return BUILTIN.createNumericNotEqual(booleanTerms);
		case NUMERIC_SUBTRACT:
			return BUILTIN.createNumericSubtract(functionalTerms);
		case PLAINLITERAL_COMPARE:						
			return BUILTIN.createTextCompare(functionalTerms);
		case PLAINLITERAL_FROM_STRING_LANG: 			
			return BUILTIN.createLangFromText(functionalTerms); // FIXME different builtin!
		case PLAINLITERAL_LENGTH:
			return BUILTIN.createTextLength(functionalTerms);
		case REMOVE: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		case REPLACE:
			return BUILTIN.createStringReplace(functionalTerms);
		case REVERSE: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		case SECONDS_FROM_DATETIME:
			return BUILTIN.createSecondsFromDateTime(functionalTerms);
		case SECONDS_FROM_DURATION:
			return BUILTIN.createSecondsFromDuration(functionalTerms);
		case SECONDS_FROM_TIME:
			return BUILTIN.createSecondsFromTime(functionalTerms);
		case STARTS_WITH:
			return BUILTIN.createStringStartsWith(booleanTerms);
		case STRING_FROM_PLAINLITERAL: 				
			return BUILTIN.createStringFromText(functionalTerms);
		case STRING_JOIN:
			return BUILTIN.createStringJoin(functionalTerms);
		case STRING_LENGTH:
			return BUILTIN.createStringLength(functionalTerms);
		case SUBLIST: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		case SUBSTRING:
			return BUILTIN.createStringSubstring(functionalTerms);
		case SUBSTRING_AFTER:
			return BUILTIN.createStringSubstringAfter(functionalTerms);
		case SUBSTRING_BEFORE:
			return BUILTIN.createStringSubstringBefore(functionalTerms);
		case SUBTRACT_DATES:
			return BUILTIN.createDateSubtract(functionalTerms);
		case SUBTRACT_DATETIMES: 
			return BUILTIN.createDateTimeSubtract(functionalTerms);
		case SUBTRACT_DAYTIMEDURATION_FROM_DATE:
			return BUILTIN.createSubtractDayTimeDurationFromDate(functionalTerms);
		case SUBTRACT_DAYTIMEDURATION_FROM_DATETIME:
			return BUILTIN.createSubtractDayTimeDurationFromDateTime(functionalTerms);
		case SUBTRACT_DAYTIMEDURATION_FROM_TIME:
			return BUILTIN.createSubtractDayTimeDurationFromTime(functionalTerms);
		case SUBTRACT_DAYTIMEDURATIONS: 
			return BUILTIN.createDayTimeDurationSubtract(functionalTerms);
		case SUBTRACT_TIMES: 
			return BUILTIN.createTimeSubtract(functionalTerms);
		case SUBTRACT_YEARMONTHDURATION_FROM_DATE:
			return BUILTIN.createSubtractYearMonthDurationFromDate(functionalTerms);
		case SUBTRACT_YEARMONTHDURATION_FROM_DATETIME:
			return BUILTIN.createSubtractYearMonthDurationFromDateTime(functionalTerms);
		case SUBTRACT_YEARMONTHDURATIONS:
			return BUILTIN.createYearMonthDurationSubtract(functionalTerms);
		case TIME_EQUAL:
			return BUILTIN.createTimeEqual(booleanTerms);
		case TIME_GREATER_THAN:
			return BUILTIN.createTimeGreater(booleanTerms);
		case TIME_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createTimeGreaterEqual(booleanTerms);
		case TIME_LESS_THAN:
			return BUILTIN.createTimeLess(booleanTerms);
		case TIME_LESS_THAN_OR_EQUAL:
			return BUILTIN.createTimeLessEqual(booleanTerms);
		case TIME_NOT_EQUAL:
			return BUILTIN.createTimeNotEqual(booleanTerms);
		case TIMEZONE_FROM_DATE:
			return BUILTIN.createTimezoneFromDate(functionalTerms);
		case TIMEZONE_FROM_DATETIME:
			return BUILTIN.createTimezoneFromDateTime(functionalTerms);
		case TIMEZONE_FROM_TIME:
			return BUILTIN.createTimezoneFromTime(functionalTerms);
		case UNION: 
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		case UPPER_CASE:
			return BUILTIN.createStringToUpper(functionalTerms);
		case XMLLITERAL_EQUAL:
			return BUILTIN.createXMLLiteralEqual(booleanTerms);
		case XMLLITERAL_NOT_EQUAL:
			return BUILTIN.createXMLLiteralNotEqual(booleanTerms);
		case YEAR_FROM_DATE:
			return BUILTIN.createYearFromDate(functionalTerms);
		case YEAR_FROM_DATETIME:
			return BUILTIN.createYearFromDateTime(functionalTerms);
		case YEARMONTHDURATION_GREATER_THAN:
			return BUILTIN.createYearMonthDurationGreater(booleanTerms);
		case YEARMONTHDURATION_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createYearMonthDurationGreaterEqual(booleanTerms);
		case YEARMONTHDURATION_LESS_THAN:
			return BUILTIN.createYearMonthDurationLess(booleanTerms);
		case YEARMONTHDURATION_LESS_THAN_OR_EQUAL:
			return BUILTIN.createYearMonthDurationLessEqual(booleanTerms);
		case YEARS_FROM_DURATION:
			return BUILTIN.createYearsFromDuration(functionalTerms);
		default:
			throw new InternalReasonerException("RIF Built-in: " + rifbuiltIn.getName() + " not yet supported!");
		}
	}

	private static IAtom checkWSMLmemberOf(boolean headLiteral, String sym,
			List<ITerm> terms) {
		// Special case! Look for wsml-member-of( ?x, wsml#<datatype> )
		// and change it to one of IRIS's IS_XXXXX() built-ins

		// We only do this for rule body predicates
		if (terms.size() == 2) {
			ITerm t0 = terms.get(0);
			ITerm t1 = terms.get(1);

			// if( t0 instanceof IVariable && t1 instanceof IIri ) {
			if (t1 instanceof IIri) {
				IIri iri = (IIri) t1;
				String type = iri.getValue();
				if (type.equals(WsmlDataType.WSML_STRING)
						|| type.equals(XmlSchemaDataType.XSD_STRING))
					return new IsStringBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_DECIMAL)
						|| type.equals(XmlSchemaDataType.XSD_DECIMAL))
					return new IsDecimalBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_DOUBLE)
						|| type.equals(XmlSchemaDataType.XSD_DOUBLE))
					return new IsDoubleBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_BOOLEAN)
						|| type.equals(XmlSchemaDataType.XSD_BOOLEAN))
					return new IsBooleanBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_INTEGER)
						|| type.equals(XmlSchemaDataType.XSD_INTEGER))
					return new IsIntegerBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_BASE64BINARY)
						|| type.equals(XmlSchemaDataType.XSD_BASE64BINARY))
					return new IsBase64BinaryBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_DATE)
						|| type.equals(XmlSchemaDataType.XSD_DATE))
					return new IsDateBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_DATETIME)
						|| type.equals(XmlSchemaDataType.XSD_DATETIME))
					return new IsDateTimeBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_DURATION)
						|| type.equals(XmlSchemaDataType.XSD_DURATION))
					return new IsDurationBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_FLOAT)
						|| type.equals(XmlSchemaDataType.XSD_FLOAT))
					return new IsFloatBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_GDAY)
						|| type.equals(XmlSchemaDataType.XSD_GDAY))
					return new IsGDayBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_GMONTH)
						|| type.equals(XmlSchemaDataType.XSD_GMONTH))
					return new IsGMonthBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_GMONTHDAY)
						|| type.equals(XmlSchemaDataType.XSD_GMONTHDAY))
					return new IsGMonthDayBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_GYEAR)
						|| type.equals(XmlSchemaDataType.XSD_GYEAR))
					return new IsGYearBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_GYEARMONTH)
						|| type.equals(XmlSchemaDataType.XSD_GYEARMONTH))
					return new IsGYearMonthBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_HEXBINARY)
						|| type.equals(XmlSchemaDataType.XSD_HEXBINARY))
					return new IsHexBinaryBuiltin(t0);
				else if (type.equals(WsmlDataType.WSML_TIME)
						|| type.equals(XmlSchemaDataType.XSD_TIME))
					return new IsTimeBuiltin(t0);
				// new XSDs
				else if (type.equals(XmlSchemaDataType.XSD_YEARMONTHDURATION))
					return new IsYearMonthDurationBuiltin(t0);
				else if (type.equals(XmlSchemaDataType.XSD_DAYTIMEDURATION))
					return new IsDayTimeDurationBuiltin(t0);
				// RDF
				else if (type.equals(RDFDataType.RDF_PLAINLITERAL)) {
					return new IsPlainLiteralBuiltin(t0);
				} else if (type.equals(RDFDataType.RDF_XMLLITERAL))
					return new IsXMLLiteralBuiltin(t0);
			}

		}
		return BASIC.createAtom(
				BASIC.createPredicate(sym, terms.size()),
				BASIC.createTuple(terms));
	}

	/**
	 * Checks if a literal contains an equal statement.
	 * 
	 * @param literal
	 * @return true if the Literal contains an equal statement (equal,
	 *         equal_date,...), else false.
	 */
	public static boolean containsEqualBuiltin(Literal literal) {
		String sym = literal.getPredicateUri();
		assert sym != null;
		if (sym.equals(BuiltIn.EQUAL.getFullName())
				|| sym.equals(BuiltIn.NUMERIC_EQUAL.getFullName())
				|| sym.equals(BuiltIn.STRING_EQUAL.getFullName())
				|| sym.equals(BuiltIn.DATE_EQUAL.getFullName())
				|| sym.equals(BuiltIn.TIME_EQUAL.getFullName())
				|| sym.equals(BuiltIn.DATETIME_EQUAL.getFullName())
				|| sym.equals(BuiltIn.GYEAR_EQUAL.getFullName())
				|| sym.equals(BuiltIn.GYEARMONTH_EQUAL.getFullName())
				|| sym.equals(BuiltIn.GMONTHDAY_EQUAL.getFullName())
				|| sym.equals(BuiltIn.GDAY_EQUAL.getFullName())
				|| sym.equals(BuiltIn.GMONTH_EQUAL.getFullName())
				|| sym.equals(BuiltIn.DURATION_EQUAL.getFullName())) {
			return true;
		}
		if (sym.equals(RifBuiltIn.DATE_EQUAL.getFullName())
				|| sym.equals(RifBuiltIn.NUMERIC_EQUAL.getFullName())
				|| sym.equals(RifBuiltIn.BOOLEAN_EQUAL.getFullName())
				|| sym.equals(RifBuiltIn.DATETIME_EQUAL.getFullName())
				|| sym.equals(RifBuiltIn.DURATION_EQUAL.getFullName())
				|| sym.equals(RifBuiltIn.TIME_EQUAL.getFullName())
				|| sym.equals(RifBuiltIn.XMLLITERAL_EQUAL.getFullName())) {
			return true;
		}
		return false;
	}

	/**
	 * @param terms
	 * @return
	 */
	private static ITerm[] toArray(List<ITerm> terms) {
		ITerm[] array = new ITerm[terms.size()];
		return terms.toArray(array);
	}

	/**
	 * Changes the order of the terms for IRIS. The first entry becomes the last
	 * one. http://www.w3.org/2005/rules/wg/wiki/List_of_functions_and_operators
	 * 
	 * @param terms a list of terms in normal order.
	 * @return a list of terms where the first entry is the last one.
	 */
	private static List<ITerm> sortListForIRIS(List<ITerm> terms) {
		assert terms != null;
		List<ITerm> terms2 = new ArrayList<ITerm>();
		ITerm one = terms.get(0);
		for (int i = 1; i < terms.size(); i++) {
			terms2.add(terms.get(i));
		}
		terms2.add(one);
		return terms2;
	}

}
