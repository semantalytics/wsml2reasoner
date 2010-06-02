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
import org.omwg.ontology.RDFDataType;
import org.omwg.ontology.WsmlDataType;
import org.omwg.ontology.XmlSchemaDataType;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.WSML2DatalogTransformer;
import org.wsmo.common.BuiltIn;
import org.wsmo.common.RifBuiltIn;

public class IrisFacadeHelper {

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

		} else {
			if (wsmlBuiltIn == null) {
				// Is not a built-in - return an ordinary term
				return BASIC.createAtom(BASIC
						.createPredicate(sym, terms.size()), BASIC
						.createTuple(terms));
			} else if (!headLiteral
					&& sym.equals(WSML2DatalogTransformer.PRED_MEMBER_OF)) {
				return checkWSMLmemberOf(headLiteral, sym, terms);
			}

		}
		return getWSMLbuiltin(headLiteral, sym, terms, wsmlBuiltIn);
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
		
		// terms as array for built-ins with boolean output.
		ITerm[] wsmlTerms = toArray(terms);
		// in wsml returnvalue is on first position - in iris on last position.
		ITerm[] sortedTerms = toArray(sortListForIRIS(terms));
		
		switch (wsmlBuiltIn) {
		case EQUAL:
			return BUILTIN.createEqual(terms.get(0), terms.get(1));
		case DATE_EQUAL:
			return BUILTIN.createDateEqual(wsmlTerms);
		case DATE_GREATER_THAN:
			return BUILTIN.createDateGreater(wsmlTerms);
		case DATE_INEQUAL:
			return BUILTIN.createDateNotEqual(wsmlTerms);
		case DATE_LESS_THAN:
			return BUILTIN.createDateLess(wsmlTerms);
		case DATETIME_EQUAL:
			return BUILTIN.createDateTimeEqual(wsmlTerms);
		case DATETIME_GREATER_THAN:
			return BUILTIN.createDateTimeGreater(wsmlTerms);
		case DATETIME_INEQUAL:
			return BUILTIN.createDateTimeNotEqual(wsmlTerms);
		case DATETIME_LESS_THAN:
			return BUILTIN.createDateTimeLess(sortedTerms);
		case DAYTIMEDURATION_EQUAL: 
			 // TODO mp: is duration ok?
			return BUILTIN.createDurationEqual(sortedTerms);
		case DAYTIMEDURATION_GREATER_THAN:
			return BUILTIN.createDayTimeDurationGreater(wsmlTerms);
		case DAYTIMEDURATION_LESS_THAN:
			return BUILTIN.createDayTimeDurationLess(wsmlTerms);
		case DURATION_EQUAL:
			return BUILTIN.createDurationEqual(wsmlTerms);
		case DURATION_INEQUAL:
			return BUILTIN.createDurationNotEqual(wsmlTerms);
		case GDAY_EQUAL: 
			 // TODO mp: is duration ok?
			return BUILTIN.createDurationEqual(sortedTerms);
		case GMONTH_EQUAL:
			 // TODO mp: is duration ok?
			return BUILTIN.createDurationEqual(sortedTerms);
		case GMONTHDAY_EQUAL: 
			 // TODO mp: is duration ok?
			return BUILTIN.createDurationEqual(sortedTerms);
		case GREATER_EQUAL:
			return BUILTIN.createGreaterEqual(terms.get(0), terms.get(1));
		case GREATER_THAN:
			return BUILTIN.createGreater(terms.get(0), terms.get(1));
		case GYEAR_EQUAL: 
			 // TODO mp: is duration ok?
			return BUILTIN.createDurationEqual(sortedTerms);
		case GYEARMONTH_EQUAL: 
			 // TODO mp: is duration ok?
			return BUILTIN.createDurationEqual(sortedTerms);
		case INEQUAL: 
			// TODO mp: 
			return BUILTIN.createUnequal(terms.get(0), terms.get(1));
		case LESS_EQUAL:
			return BUILTIN.createLessEqual(wsmlTerms[0], wsmlTerms[1]);
		case LESS_THAN:
			return BUILTIN.createLess(wsmlTerms[0], wsmlTerms[1]);
		case NUMERIC_ADD:
			return BUILTIN.createNumericAdd(sortedTerms);
		case NUMERIC_DIVIDE:
			return BUILTIN.createNumericDivide(sortedTerms);
		case NUMERIC_MODULUS:
			return BUILTIN.createNumericModulus(sortedTerms);
		case NUMERIC_EQUAL:
			return BUILTIN.createNumericEqual(wsmlTerms);
		case NUMERIC_GREATER_THAN:
			return BUILTIN.createNumericGreater(wsmlTerms);
		case NUMERIC_INEQUAL:
			return BUILTIN.createNumericNotEqual(wsmlTerms);
		case NUMERIC_LESS_THAN:
			return BUILTIN.createNumericLess(wsmlTerms);
		case NUMERIC_MULTIPLY:
			return BUILTIN.createNumericMultiply(sortedTerms);
		case NUMERIC_SUBTRACT:
			return BUILTIN.createNumericSubtract(sortedTerms);
		case STRING_EQUAL: 
			// TODO mp check
			return BUILTIN.createEqual(terms.get(0), terms.get(1));
		case STRING_INEQUAL: 
			// TODO mp: 
			return BUILTIN.createUnequal(terms.get(0), terms.get(1));
		case TIME_EQUAL:
			return BUILTIN.createTimeEqual(wsmlTerms);
		case TIME_GREATER_THAN:
			return BUILTIN.createTimeGreater(wsmlTerms);
		case TIME_INEQUAL:
			return BUILTIN.createTimeNotEqual(wsmlTerms);
		case TIME_LESS_THAN:
			return BUILTIN.createTimeLess(wsmlTerms);
		case YEARMONTHDURATION_EQUAL: 
			// TODO mp: is duration ok?
			return BUILTIN.createDurationEqual(sortedTerms);
		case YEARMONTHDURATION_GREATER_THAN:
			return BUILTIN.createYearMonthDurationGreater(wsmlTerms);
		case YEARMONTHDURATION_LESS_THAN:
			return BUILTIN.createYearMonthDurationLess(wsmlTerms);
		case HAS_DATATYPE:
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
			// TO_DATATYPE part
			// TODO mp : to_datatype : sorted or wsmlTerms?
		case TO_BASE64:
			return BUILTIN.createToBase64Binary(sortedTerms);
		case TO_BOOLEAN:
			return BUILTIN.createToBoolean(sortedTerms);
		case TO_DATE:
			return BUILTIN.createToDate(sortedTerms);
		case TO_DATETIME:
			return BUILTIN.createToDateTime(sortedTerms);
		case TO_DAYTIMEDURATION:
			return BUILTIN.createToDayTimeDuration(sortedTerms);
		case TO_DECIMAL:
			return BUILTIN.createToDecimal(sortedTerms);
		case TO_DOUBLE:
			return BUILTIN.createToDouble(sortedTerms);
		case TO_DURATION:
			return BUILTIN.createToDuration(sortedTerms);
		case TO_FLOAT:
			return BUILTIN.createToFloat(sortedTerms);
		case TO_GDAY:
			return BUILTIN.createToGDay(sortedTerms);
		case TO_GMONTH:
			return BUILTIN.createToGMonth(sortedTerms);
		case TO_GMONTHDAY:
			return BUILTIN.createToGMonthDay(sortedTerms);
		case TO_GYEAR:
			return BUILTIN.createToGYear(sortedTerms);
		case TO_GYEARMONTH:
			return BUILTIN.createToGYearMonth(sortedTerms);
		case TO_HEXBINARY:
			return BUILTIN.createToHexBinary(sortedTerms);
		case TO_INTEGER:
			return BUILTIN.createToInteger(sortedTerms);
		case TO_IRI:
			return BUILTIN.createToIRI(sortedTerms);
		case TO_STRING:
			return BUILTIN.createToString(sortedTerms);
		case TO_TEXT:
			// TODO mp: ToPlainLiteral
			return BUILTIN.createToString(sortedTerms);
		case TO_TIME:
			return BUILTIN.createToTime(sortedTerms);
		case TO_XMLLITERAL:
			return BUILTIN.createToXMLLiteral(sortedTerms);
		case TO_YEARMONTHDURATION:
			return BUILTIN.createToYearMonthDuration(sortedTerms);
		// end of TO_DATATYPE part
		case DAY_PART:
			return BUILTIN.createDayPart(sortedTerms);
		case HOUR_PART:
			return BUILTIN.createHourPart(sortedTerms);
		case YEAR_PART:
			return BUILTIN.createYearPart(sortedTerms);
		case TIMEZONE_PART:
			return BUILTIN.createTimezonePart(sortedTerms);
		case SECOND_PART:
			return BUILTIN.createSecondPart(sortedTerms);
		case MINUTE_PART:
			return BUILTIN.createMinutePart(sortedTerms);
		case MONTH_PART:
			return BUILTIN.createMonthPart(sortedTerms);
		case TEXT_COMPARE:
			return BUILTIN.createTextCompare(sortedTerms);
		case TEXT_EQUAL:
			// TODO mp check
			return BUILTIN.createEqual(terms.get(0), terms.get(1));
		case TEXT_INEQUAL:
			// TODO mp check
			return BUILTIN.createUnequal(terms.get(0), terms.get(1));
		case TEXT_FROM_STRING:
			return BUILTIN.createTextFromString(sortedTerms);
		case TEXT_FROM_STRING_LANG:
			return BUILTIN.createTextFromStringLang(sortedTerms);
		case TEXT_LENGTH:
			return BUILTIN.createTextLength(sortedTerms);
		case LANG_FROM_TEXT:
			return BUILTIN.createLangFromText(sortedTerms);
		case STRING_FROM_TEXT:
			return BUILTIN.createStringFromText(sortedTerms);
		// IS_DATATYPE part
		case IS_BASE64BINARY:
			return BUILTIN.createIsBase64Binary(sortedTerms);
		case IS_BOOLEAN:
			return BUILTIN.createIsBoolean(sortedTerms);
		case IS_DATATYPE:
			// TODO mp check
			return BUILTIN.createIsDatatype(sortedTerms);
		case IS_DATE:
			return BUILTIN.createIsDate(sortedTerms);
		case IS_DATETIME:
			return BUILTIN.createIsDateTime(sortedTerms);
		case IS_DAYTIME_DURATION:
			// TODO mp check:
			return BUILTIN.createIsDuration(sortedTerms);
		case IS_DAYTIMEDURATION:  // TODO the same as before
			// TODO mp check:
			return BUILTIN.createIsDuration(sortedTerms);
		case IS_DECIMAL:
			return BUILTIN.createIsDecimal(sortedTerms);
		case IS_DOUBLE:
			return BUILTIN.createIsDouble(sortedTerms);
		case IS_DURATION:
			return BUILTIN.createIsDuration(sortedTerms);
		case IS_FLOAT:
			return BUILTIN.createIsFloat(sortedTerms);
		case IS_GDAY:
			return BUILTIN.createIsGDay(sortedTerms);
		case IS_GMONTH:
			return BUILTIN.createIsGMonth(sortedTerms);
		case IS_GMONTHDAY:
			return BUILTIN.createIsGMonthDay(sortedTerms);
		case IS_GYEAR:
			return BUILTIN.createIsGYear(sortedTerms);
		case IS_GYEARMONTH:
			return BUILTIN.createIsGYearMonth(sortedTerms);
		case IS_HEXBINARY:
			return BUILTIN.createIsHexBinary(sortedTerms);
		case IS_INTEGER:
			return BUILTIN.createIsInteger(sortedTerms);
		case IS_IRI:
			return BUILTIN.createIsIRI(sortedTerms);
		case IS_NOT_DATATYPE:
			// TODO mp check
			return BUILTIN.createIsNotDatatype(sortedTerms);
		case IS_STRING:
			return BUILTIN.createIsString(sortedTerms);
		case IS_TEXT:
			return BUILTIN.createIsText(sortedTerms);
		case IS_TIME:
			return BUILTIN.createIsTime(sortedTerms);
		case IS_XMLLITERAL:
			return BUILTIN.createIsXMLLiteral(sortedTerms);
		case IS_YEAR_MONTH_DURATION:
			// TODO mp the same:
			return BUILTIN.createIsYearMonthDuration(sortedTerms);
		case IS_YEARMONTHDURATION:
			return BUILTIN.createIsYearMonthDuration(sortedTerms);
		
		default:
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
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
		
		// terms as array for built-ins with boolean output.
		ITerm[] wsmlTerms = toArray(terms);
		// in wsml returnvalue is on first position - in iris on last position.
		ITerm[] sortedTerms = toArray(sortListForIRIS(terms));
		
		switch (rifbuiltIn) {
		case ADD_DAYTIMEDURATIONS: 
			// TODO mp: needed?
			return BUILTIN.createAddDayTimeDurationToDateTime(sortedTerms);
		case ADD_DAYTIMEDURATION_TO_DATE:
			return BUILTIN.createAddDayTimeDurationToDate(sortedTerms);
		case ADD_DAYTIMEDURATION_TO_TIME:
			return BUILTIN.createAddDayTimeDurationToTime(sortedTerms);
		case ADD_DAYTIMEDURATION_TO_DATETIME:
			return BUILTIN.createAddDayTimeDurationToDateTime(sortedTerms);
		case ADD_YEARMONTHDURATIONS: // TODO mp: needed?
			return BUILTIN.createAddYearMonthDurationToDateTime(sortedTerms);
		case ADD_YEARMONTHDURATION_TO_DATE:
			return BUILTIN.createAddYearMonthDurationToDate(sortedTerms);
		case ADD_YEARMONTHDURATION_TO_DATETIME:
			return BUILTIN.createAddYearMonthDurationToDateTime(sortedTerms);
		case BOOLEAN_EQUAL:
			return BUILTIN.createBooleanEqual(wsmlTerms);
//	    case BOOLEAN_NOT_EQUAL: 
			// TODO mp 
//			return BUILTIN.createBooleanNot(wsmlTerms);
		case BOOLEAN_GREATER_THAN:
			return BUILTIN.createBooleanGreater(wsmlTerms);
		case BOOLEAN_LESS_THAN:
			return BUILTIN.createBooleanLess(wsmlTerms);
			
			// RIF String builtins
		case COMPARE:
			return BUILTIN.createStringCompare(wsmlTerms);
		case CONCAT:
			return BUILTIN.createStringConcat(wsmlTerms);
		case CONCATENATE: 
			// TODO mp needed?
//			 return BUILTIN.createStringConcatenate(wsmlTerms);
			return BUILTIN.createStringConcat(wsmlTerms);
		case CONTAINS:
			return BUILTIN.createStringContains(wsmlTerms);
		case DATE_EQUAL:
			return BUILTIN.createDateEqual(wsmlTerms);
		case DATE_NOT_EQUAL:
			return BUILTIN.createDateNotEqual(wsmlTerms);
		case DATE_GREATER_THAN:
			return BUILTIN.createDateGreater(wsmlTerms);
		case DATE_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createDateGreaterEqual(wsmlTerms);
		case DATE_LESS_THAN:
			return BUILTIN.createDateLess(wsmlTerms);
		case DATE_LESS_THAN_OR_EQUAL:
			return BUILTIN.createDateLessEqual(wsmlTerms);
		case DATETIME_EQUAL:
			return BUILTIN.createDateTimeEqual(wsmlTerms);
		case DATETIME_NOT_EQUAL:
			return BUILTIN.createDateTimeNotEqual(wsmlTerms);
		case DATETIME_GREATER_THAN:
			return BUILTIN.createDateTimeGreater(wsmlTerms);
		case DATETIME_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createDateTimeGreaterEqual(wsmlTerms);
		case DATETIME_LESS_THAN:
			return BUILTIN.createDateTimeLess(wsmlTerms);
		case DATETIME_LESS_THAN_OR_EQUAL:
			return BUILTIN.createDateTimeLessEqual(wsmlTerms);
		case DAY_FROM_DATE:
			return BUILTIN.createDayFromDate(sortedTerms);
		case DAY_FROM_DATETIME:
			return BUILTIN.createDayFromDateTime(sortedTerms);
		case DAYS_FROM_DURATION:
			return BUILTIN.createDaysFromDuration(sortedTerms);
		case DAYTIMEDURATION_GREATER_THAN:
			return BUILTIN.createDayTimeDurationGreater(wsmlTerms);
		case DAYTIMEDURATION_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createDayTimeDurationGreaterEqual(wsmlTerms);
		case DAYTIMEDURATION_LESS_THAN:
			return BUILTIN.createDayTimeDurationLess(wsmlTerms);
		case DAYTIMEDURATION_LESS_THAN_OR_EQUAL:
			return BUILTIN.createDayTimeDurationLessEqual(wsmlTerms);
		case DISTINCT_VALUES: 
			// TODO mp
//			return BUILTIN.createDistinctValues(wsmlTerms);
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case DIVIDE_DAYTIMEDURATION:
			return BUILTIN.createDayTimeDurationDivide(sortedTerms);
		case DIVIDE_DAYTIMEDURATION_BY_DAYTIMEDURATION:
			return BUILTIN.createDayTimeDurationDivideByDayTimeDuration(sortedTerms);
		case DIVIDE_YEARMONTHDURATION:
			return BUILTIN.createYearMonthDurationDivide(sortedTerms);
		case DIVIDE_YEARMONTHDURATION_BY_YEARMONTHDURATION:
			return BUILTIN.createYearMonthDurationDivideByYearMonthDuration(sortedTerms);
		case DURATION_EQUAL:
			return BUILTIN.createDurationEqual(wsmlTerms);
		case DURATION_NOT_EQUAL:
			return BUILTIN.createDurationNotEqual(wsmlTerms);
		case ENCODE_FOR_URI: 
			// TODO mp: needed 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case ENDS_WITH:
			return BUILTIN.createStringEndsWith(sortedTerms);
		case ESCAPE_HTML_URI: 
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case EXCEPT: 
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case GET: 
			// TODO mp:
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case HOURS_FROM_DATETIME:
			return BUILTIN.createHoursFromDateTime(sortedTerms);
		case HOURS_FROM_DURATION:
			return BUILTIN.createHoursFromDuration(sortedTerms);
		case HOURS_FROM_TIME:
			return BUILTIN.createHoursFromTime(sortedTerms);
		case INDEX_OF:
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case INSERT_BEFORE: 
			// TODO mp: check
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case INTERSECT: 
			// TODO mp:
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IRI_STRING:
			return BUILTIN.createIriString(sortedTerms);
		case IRI_TO_URI: 
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IS_LIST: 
			// TODO mp
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IS_LITERAL_ANYURI:
			return BUILTIN.createIsAnyURI(wsmlTerms);
		case IS_LITERAL_BASE64BINARY:
			return BUILTIN.createIsBase64Binary(wsmlTerms);
		case IS_LITERAL_BOOLEAN:
			return BUILTIN.createIsBoolean(wsmlTerms);
		case IS_LITERAL_BYTE:
			return BUILTIN.createIsByte(wsmlTerms);
		case IS_LITERAL_DATE:
			return BUILTIN.createIsDate(wsmlTerms);
		case IS_LITERAL_DATETIME:
			return BUILTIN.createIsDateTime(wsmlTerms);
		case IS_LITERAL_DATETIMESTAMP:
			return BUILTIN.createIsDateTimeStamp(wsmlTerms);
		case IS_LITERAL_DAYTIMEDURATION:
			return BUILTIN.createIsDayTimeDuration(wsmlTerms);
		case IS_LITERAL_DECIMAL:
			return BUILTIN.createIsDecimal(wsmlTerms);
		case IS_LITERAL_DOUBLE:
			return BUILTIN.createIsDouble(wsmlTerms);
		case IS_LITERAL_FLOAT:
			return BUILTIN.createIsFloat(wsmlTerms);
		case IS_LITERAL_HEXBINARY:
			return BUILTIN.createIsHexBinary(wsmlTerms);
		case IS_LITERAL_INT:
			return BUILTIN.createIsInt(wsmlTerms);
		case IS_LITERAL_INTEGER:
			return BUILTIN.createIsInteger(wsmlTerms);
		case IS_LITERAL_LANGUAGE:
			return BUILTIN.createIsLanguage(wsmlTerms);
		case IS_LITERAL_LONG:
			return BUILTIN.createIsLong(wsmlTerms);
		case IS_LITERAL_NAME:
			return BUILTIN.createIsName(wsmlTerms);
		case IS_LITERAL_NCNAME:
			return BUILTIN.createIsNCName(wsmlTerms);
		case IS_LITERAL_NEGATIVEINTEGER:
			return BUILTIN.createIsNegativeInteger(wsmlTerms);
		case IS_LITERAL_NMTOKEN:
			return BUILTIN.createIsNMTOKEN(wsmlTerms);
		case IS_LITERAL_NONNEGATIVEINTEGER:
			return BUILTIN.createIsNonNegativeInteger(wsmlTerms);
		case IS_LITERAL_NONPOSITIVEINTEGER:
			return BUILTIN.createIsNonPositiveInteger(wsmlTerms);
		case IS_LITERAL_NORMALIZEDSTRING:
			return BUILTIN.createIsNormalizedString(wsmlTerms);
		case IS_LITERAL_NOT_ANYURI:
			return BUILTIN.createIsNotAnyURI(wsmlTerms);
		case IS_LITERAL_NOT_BASE64BINARY:
			return BUILTIN.createIsNotBase64Binary(wsmlTerms);
		case IS_LITERAL_NOT_BOOLEAN:
			return BUILTIN.createIsNotBoolean(wsmlTerms);
		case IS_LITERAL_NOT_BYTE:
			return BUILTIN.createIsNotByte(wsmlTerms);
		case IS_LITERAL_NOT_DATE:
			return BUILTIN.createIsNotDate(wsmlTerms);
		case IS_LITERAL_NOT_DATETIME:
			return BUILTIN.createIsNotDateTime(wsmlTerms);
		case IS_LITERAL_NOT_DATETIMESTAMP:
			return BUILTIN.createIsNotDateTimeStamp(wsmlTerms);
		case IS_LITERAL_NOT_DAYTIMEDURATION:
			return BUILTIN.createIsNotDayTimeDuration(wsmlTerms);
		case IS_LITERAL_NOT_DECIMAL:
			return BUILTIN.createIsNotDecimal(wsmlTerms);
		case IS_LITERAL_NOT_DOUBLE:
			return BUILTIN.createIsNotDouble(wsmlTerms);
		case IS_LITERAL_NOT_FLOAT:
			return BUILTIN.createIsNotFloat(wsmlTerms);
		case IS_LITERAL_NOT_HEXBINARY:
			return BUILTIN.createIsNotHexBinary(wsmlTerms);
		case IS_LITERAL_NOT_INTEGER:
			return BUILTIN.createIsNotInteger(wsmlTerms);
//		case IS_LITERAL_NOT_INT:
//			// TODO mp: implement
//			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
//					BASIC.createTuple(terms));
		case IS_LITERAL_NOT_LANGUAGE:
			return BUILTIN.createIsNotLanguage(wsmlTerms);
		case IS_LITERAL_NOT_LONG:
			return BUILTIN.createIsNotLong(wsmlTerms);
		case IS_LITERAL_NOT_NAME:
			return BUILTIN.createIsNotName(wsmlTerms);
		case IS_LITERAL_NOT_NCNAME:
			return BUILTIN.createIsNotNCName(wsmlTerms);
		case IS_LITERAL_NOT_NEGATIVEINTEGER:
			return BUILTIN.createIsNotNegativeInteger(wsmlTerms);
		case IS_LITERAL_NOT_NMTOKEN:
			return BUILTIN.createIsNotNMTOKEN(wsmlTerms);
		case IS_LITERAL_NOT_NONNEGATIVEINTEGER:
			return BUILTIN.createIsNotNonNegativeInteger(wsmlTerms);
		case IS_LITERAL_NOT_NONPOSITIVEINTEGER:
			return BUILTIN.createIsNotNonPositiveInteger(wsmlTerms);
		case IS_LITERAL_NOT_NORMALIZEDSTRING:
			return BUILTIN.createIsNotNormalizedString(wsmlTerms);
		case IS_LITERAL_NOT_PLAINLITERAL:
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IS_LITERAL_NOT_POSITIVEINTEGER:
			return BUILTIN.createIsNotPositiveInteger(wsmlTerms);
		case IS_LITERAL_NOT_SHORT:
			return BUILTIN.createIsNotShort(wsmlTerms);
		case IS_LITERAL_NOT_STRING:
			return BUILTIN.createIsNotString(wsmlTerms);
		case IS_LITERAL_NOT_TIME:
			return BUILTIN.createIsNotTime(wsmlTerms);
		case IS_LITERAL_NOT_TOKEN:
			return BUILTIN.createIsNotToken(wsmlTerms);
		case IS_LITERAL_NOT_UNSIGNEDBYTE:
			return BUILTIN.createIsNotUnsignedByte(wsmlTerms);
		case IS_LITERAL_NOT_UNSIGNEDINT:
			return BUILTIN.createIsNotUnsignedInt(wsmlTerms);
		case IS_LITERAL_NOT_UNSIGNEDLONG:
			return BUILTIN.createIsNotUnsignedLong(wsmlTerms);
		case IS_LITERAL_NOT_UNSIGNEDSHORT:
			return BUILTIN.createIsNotUnsignedShort(wsmlTerms);
		case IS_LITERAL_NOT_XMLLITERAL:
			return BUILTIN.createIsNotXMLLiteral(wsmlTerms);
		case IS_LITERAL_NOT_YEARMONTHDURATION:
			return BUILTIN.createIsNotYearMonthDuration(wsmlTerms);
		case IS_LITERAL_PLAINLITERAL:
			// TODO mp:
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IS_LITERAL_POSITIVEINTEGER:
			return BUILTIN.createIsPositiveInteger(wsmlTerms);
		case IS_LITERAL_SHORT:
			return BUILTIN.createIsShort(wsmlTerms);
		case IS_LITERAL_STRING:
			return BUILTIN.createIsString(wsmlTerms);
		case IS_LITERAL_TIME:
			return BUILTIN.createIsTime(wsmlTerms);
		case IS_LITERAL_TOKEN:
			return BUILTIN.createIsToken(wsmlTerms);
		case IS_LITERAL_UNSIGNEDBYTE:
			return BUILTIN.createIsUnsignedByte(wsmlTerms);
		case IS_LITERAL_UNSIGNEDINT:
			return BUILTIN.createIsUnsignedInt(wsmlTerms);
		case IS_LITERAL_UNSIGNEDLONG:
			return BUILTIN.createIsUnsignedLong(wsmlTerms);
		case IS_LITERAL_UNSIGNEDSHORT:
			return BUILTIN.createIsUnsignedShort(wsmlTerms);
		case IS_LITERAL_XMLLITERAL:
			return BUILTIN.createIsXMLLiteral(wsmlTerms);
		case IS_LITERAL_YEARMONTHDURATION:
			return BUILTIN.createIsYearMonthDuration(wsmlTerms);
		case LANG_FROM_PLAINLITERAL: 				
			// TODO mp ?
			return BUILTIN.createLangFromText(sortedTerms);
		case LIST_CONTAINS: 
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case LITERAL_NOT_IDENTICAL: 
			// TODO mp:
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case LOWER_CASE:
			return BUILTIN.createStringToLower(sortedTerms);
		case MAKE_LISTS: 
			// TODO mp: ?
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case MATCHES:
			return BUILTIN.createStringMatches(sortedTerms);
		case MATCHES_LANGUAGE_RANGE: 
			// TODO mp: implement?
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case MINUTES_FROM_DATETIME:
			return BUILTIN.createMinutesFromDateTime(sortedTerms);
		case MINUTES_FROM_DURATION:
			return BUILTIN.createMinutesFromDuration(sortedTerms);
		case MINUTES_FROM_TIME:
			return BUILTIN.createMinutesFromTime(sortedTerms);
		case MONTH_FROM_DATE:
			return BUILTIN.createMonthFromDate(sortedTerms);
		case MONTH_FROM_DATETIME:
			return BUILTIN.createMonthFromDate(sortedTerms);
		case MONTHS_FROM_DURATION:
			return BUILTIN.createMonthsFromDuration(sortedTerms);
		case MULTIPLY_DAYTIMEDURATION: 
			// TODO mp:
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case MULTIPLY_YEARMONTHDURATION:
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case NOT:
			// TODO mp:
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case NUMERIC_ADD:
			return BUILTIN.createNumericAdd(sortedTerms);
		case NUMERIC_DIVIDE:
			return BUILTIN.createNumericDivide(sortedTerms);
		case NUMERIC_EQUAL:
			return BUILTIN.createNumericEqual(sortedTerms);
		case NUMERIC_GREATER_THAN:
			return BUILTIN.createNumericGreater(wsmlTerms);
		case NUMERIC_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createNumericGreaterEqual(wsmlTerms);
		case NUMERIC_INTEGER_DIVIDE:
			return BUILTIN.createNumericIntegerDivide(wsmlTerms);
		case NUMERIC_LESS_THAN:
			return BUILTIN.createNumericLess(wsmlTerms);
		case NUMERIC_LESS_THAN_OR_EQUAL:
			return BUILTIN.createNumericLessEqual(wsmlTerms);
		case NUMERIC_MOD:
			return BUILTIN.createNumericModulus(sortedTerms);
		case NUMERIC_MULTIPLY:
			return BUILTIN.createNumericMultiply(sortedTerms);
		case NUMERIC_NOT_EQUAL:
			return BUILTIN.createNumericNotEqual(wsmlTerms);
		case NUMERIC_SUBTRACT:
			return BUILTIN.createNumericSubtract(sortedTerms);
		case PLAINLITERAL_COMPARE:						
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case PLAINLITERAL_FROM_STRING_LANG: 			
			// TODO mp: 
			return BUILTIN.createTextFromStringLang(sortedTerms);
		case PLAINLITERAL_LENGTH:
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case REMOVE: 
			// TODO mp
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case REPLACE:
			return BUILTIN.createStringReplace(sortedTerms);
		case REVERSE: 
			// TODO mp : implement
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case SECONDS_FROM_DATETIME:
			return BUILTIN.createSecondsFromDateTime(sortedTerms);
		case SECONDS_FROM_DURATION:
			return BUILTIN.createSecondsFromDuration(sortedTerms);
		case SECONDS_FROM_TIME:
			return BUILTIN.createSecondsFromTime(sortedTerms);
		case STARTS_WITH:
			return BUILTIN.createStringStartsWith(sortedTerms);
		case STRING_FROM_PLAINLITERAL: 				// TODO mp: error
			return BUILTIN.createStringFromText(sortedTerms);
		case STRING_JOIN:
			return BUILTIN.createStringJoin(sortedTerms);
		case STRING_LENGTH:
			return BUILTIN.createStringLength(sortedTerms);
		case SUBLIST: 
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case SUBSTRING:
			return BUILTIN.createStringSubstring(sortedTerms);
		case SUBSTRING_AFTER:
			return BUILTIN.createStringSubstringAfter(sortedTerms);
		case SUBSTRING_BEFORE:
			return BUILTIN.createStringSubstringBefore(sortedTerms);
		case SUBTRACT_DATES: 
			// TODO mp: 
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case SUBTRACT_DATETIMES: 
			return BUILTIN.createDateTimeSubtract(sortedTerms);
		case SUBTRACT_DAYTIMEDURATION_FROM_DATE:
			return BUILTIN.createSubtractDayTimeDurationFromDate(sortedTerms);
		case SUBTRACT_DAYTIMEDURATION_FROM_DATETIME:
			return BUILTIN.createSubtractDayTimeDurationFromDateTime(sortedTerms);
		case SUBTRACT_DAYTIMEDURATION_FROM_TIME:
			return BUILTIN.createSubtractDayTimeDurationFromTime(sortedTerms);
		case SUBTRACT_DAYTIMEDURATIONS: 
			return BUILTIN.createDayTimeDurationSubtract(sortedTerms);
		case SUBTRACT_TIMES: 
			return BUILTIN.createTimeSubtract(sortedTerms);
		case SUBTRACT_YEARMONTHDURATION_FROM_DATE:
			return BUILTIN.createSubtractYearMonthDurationFromDate(sortedTerms);
		case SUBTRACT_YEARMONTHDURATION_FROM_DATETIME:
			return BUILTIN.createSubtractYearMonthDurationFromDateTime(sortedTerms);
		case SUBTRACT_YEARMONTHDURATIONS:
			return BUILTIN.createYearMonthDurationSubtract(sortedTerms);
		case TIME_EQUAL:
			return BUILTIN.createTimeEqual(wsmlTerms);
		case TIME_GREATER_THAN:
			return BUILTIN.createTimeGreater(wsmlTerms);
		case TIME_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createTimeGreaterEqual(wsmlTerms);
		case TIME_LESS_THAN:
			return BUILTIN.createTimeLess(wsmlTerms);
		case TIME_LESS_THAN_OR_EQUAL:
			return BUILTIN.createTimeLessEqual(wsmlTerms);
		case TIME_NOT_EQUAL:
			return BUILTIN.createTimeNotEqual(wsmlTerms);
		case TIMEZONE_FROM_DATE:
			return BUILTIN.createTimezoneFromDate(sortedTerms);
		case TIMEZONE_FROM_DATETIME:
			return BUILTIN.createTimezoneFromDateTime(sortedTerms);
		case TIMEZONE_FROM_TIME:
			return BUILTIN.createTimezoneFromTime(sortedTerms);
		case UNION: 
			// TODO mp
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case UPPER_CASE:
			return BUILTIN.createStringToUpper(sortedTerms);
		case XMLLITERAL_EQUAL:
			return BUILTIN.createXMLLiteralEqual(wsmlTerms);
		case XMLLITERAL_NOT_EQUAL:
			return BUILTIN.createXMLLiteralNotEqual(wsmlTerms);
		case YEAR_FROM_DATE:
			return BUILTIN.createYearFromDate(sortedTerms);
		case YEAR_FROM_DATETIME:
			return BUILTIN.createYearFromDateTime(sortedTerms);
		case YEARMONTHDURATION_GREATER_THAN:
			return BUILTIN.createYearMonthDurationGreater(wsmlTerms);
		case YEARMONTHDURATION_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createYearMonthDurationGreaterEqual(wsmlTerms);
		case YEARMONTHDURATION_LESS_THAN:
			return BUILTIN.createYearMonthDurationLess(wsmlTerms);
		case YEARMONTHDURATION_LESS_THAN_OR_EQUAL:
			return BUILTIN.createYearMonthDurationLessEqual(wsmlTerms);
		case YEARS_FROM_DURATION:
			return BUILTIN.createYearsFromDuration(sortedTerms);
			// TODO mp: some more builtins ? 
		default:
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
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
				else if (type.equals(RDFDataType.RDF_TEXT)) {
					return new IsPlainLiteralBuiltin(t0);
				} else if (type.equals(RDFDataType.RDF_XMLLITERAL))
					return new IsXMLLiteralBuiltin(t0);
			}

		}
		return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()), BASIC
				.createTuple(terms));
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
