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
		// // return an ordinary atom
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
		// NOTE mp: not longer supported - RIF Built-ins replace them mostly
		//
		
		ITerm[] t = toArray(sortListForIRIS(terms));
		switch (wsmlBuiltIn) {
		case EQUAL:
			return BUILTIN.createEqual(terms.get(0), terms.get(1));
		case DATE_EQUAL:
			return BUILTIN.createDateEqual(t);
		case DATE_GREATER_THAN:
			return BUILTIN.createDateGreater(t);
		case DATE_INEQUAL:
			return BUILTIN.createDateNotEqual(t);
		case DATE_LESS_THAN:
			return BUILTIN.createDateLess(t);
		case DATETIME_EQUAL:
			return BUILTIN.createDateTimeEqual(t);
		case DATETIME_GREATER_THAN:
			return BUILTIN.createDateTimeGreater(t);
		case DATETIME_INEQUAL:
			return BUILTIN.createDateTimeNotEqual(t);
		case DATETIME_LESS_THAN:
			return BUILTIN.createDateTimeLess(t);
		case DAYTIMEDURATION_EQUAL: // TODO mp: implement
//			return BUILTIN.createdaytimeduration
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case DAYTIMEDURATION_GREATER_THAN:
			return BUILTIN.createDayTimeDurationGreater(t);
		case DAYTIMEDURATION_LESS_THAN:
			return BUILTIN.createDayTimeDurationLess(t);
		case DURATION_EQUAL:
			return BUILTIN.createDurationEqual(t);
		case DURATION_INEQUAL:
			return BUILTIN.createDurationNotEqual(t);
		case GDAY_EQUAL:  // TODO mp: implement
//			return BUILTIN.createdaytimeduration
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case GMONTH_EQUAL:// TODO mp: implement
//			return BUILTIN.createdaytimeduration
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case GMONTHDAY_EQUAL: // TODO mp: implement
//			return BUILTIN.createdaytimeduration
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case GREATER_EQUAL:
			return BUILTIN.createGreaterEqual(t[0], t[1]);
//			return BUILTIN.createGreaterEqual(terms.get(0), terms.get(1));
		case GREATER_THAN:
			return BUILTIN.createGreaterEqual(t[0], t[1]);
		case GYEAR_EQUAL: // TODO mp: implement
//			return BUILTIN.createG
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case GYEARMONTH_EQUAL: // TODO mp: implement
//			return BUILTIN.createG
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case INEQUAL:  // TODO mp: implement?
//			return BUILTIN.createno
		case LESS_EQUAL:
			return BUILTIN.createLessEqual(t[0], t[1]);
		case LESS_THAN:
			return BUILTIN.createLess(t[0], t[1]);
		case NUMERIC_ADD:
			return BUILTIN.createNumericAdd(t);
		case NUMERIC_DIVIDE:
			return BUILTIN.createNumericDivide(t);
		case NUMERIC_EQUAL:
			return BUILTIN.createNumericEqual(t);
		case NUMERIC_GREATER_THAN:
			return BUILTIN.createNumericGreater(t);
		case NUMERIC_INEQUAL:
			return BUILTIN.createNumericNotEqual(t);
		case NUMERIC_LESS_THAN:
			return BUILTIN.createNumericLess(t);
		case NUMERIC_MULTIPLY:
			return BUILTIN.createNumericMultiply(t);
		case NUMERIC_SUBTRACT:
			return BUILTIN.createNumericSubtract(t);
		case STRING_EQUAL: // TODO mp : implement ? String 
//			return BUILTIN.createstringe
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case STRING_INEQUAL: // TODO mp : implement ? String 
//			return BUILTIN.createStringNotEqual
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case TIME_EQUAL:
			return BUILTIN.createTimeEqual(t);
		case TIME_GREATER_THAN:
			return BUILTIN.createTimeGreater(t);
		case TIME_INEQUAL:
			return BUILTIN.createTimeNotEqual(t);
		case TIME_LESS_THAN:
			return BUILTIN.createTimeLess(t);
		case YEARMONTHDURATION_EQUAL: // TODO mp: implement
//			return BUILTIN.createYearMonthDurationGreater(terms)
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case YEARMONTHDURATION_GREATER_THAN:
			return BUILTIN.createYearMonthDurationGreater(t);
		case YEARMONTHDURATION_LESS_THAN:
			return BUILTIN.createYearMonthDurationLess(t);
			
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
		ITerm[] t = toArray(sortListForIRIS(terms));
		switch (rifbuiltIn) {
		case ADD_DAYTIMEDURATIONS: // TODO mp: needed?
			return BUILTIN.createAddDayTimeDurationToDateTime(t);
		case ADD_DAYTIMEDURATION_TO_DATE:
			return BUILTIN.createAddDayTimeDurationToDate(t);
		case ADD_DAYTIMEDURATION_TO_TIME:
			return BUILTIN.createAddDayTimeDurationToTime(t);
		case ADD_DAYTIMEDURATION_TO_DATETIME:
			return BUILTIN.createAddDayTimeDurationToDateTime(t);
		case ADD_YEARMONTHDURATIONS: // TODO mp: needed?
			return BUILTIN.createAddYearMonthDurationToDateTime(t);
		case ADD_YEARMONTHDURATION_TO_DATE:
			return BUILTIN.createAddYearMonthDurationToDate(t);
		case ADD_YEARMONTHDURATION_TO_DATETIME:
			return BUILTIN.createAddYearMonthDurationToDateTime(t);
		case BOOLEAN_EQUAL:
			return BUILTIN.createBooleanEqual(t);
//	    case BOOLEAN_NOT_EQUAL: // TODO mp: not implemented
//			return BUILTIN.(t);
			// break;
		case BOOLEAN_GREATER_THAN:
			return BUILTIN.createBooleanGreater(t);
		case BOOLEAN_LESS_THAN:
			return BUILTIN.createBooleanLess(t);
			// RIF String builtins
		case COMPARE:
			return BUILTIN.createStringCompare(t);
		case CONCAT:
			return BUILTIN.createStringConcat(t);
			// case CONCATENATE // TODO needed?
			// return
			// BUILTIN.createStringConcatenate(t);
		case CONTAINS:
			return BUILTIN.createStringContains(t);
		case DATE_EQUAL:
			return BUILTIN.createDateEqual(t);
		case DATE_NOT_EQUAL:
			return BUILTIN.createDateNotEqual(t);
		case DATE_GREATER_THAN:
			return BUILTIN.createDateGreater(t);
		case DATE_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createDateGreaterEqual(t);
		case DATE_LESS_THAN:
			return BUILTIN.createDateLess(t);
		case DATE_LESS_THAN_OR_EQUAL:
			return BUILTIN.createDateLessEqual(t);
		case DATETIME_EQUAL:
			return BUILTIN.createDateTimeEqual(t);
		case DATETIME_NOT_EQUAL:
			return BUILTIN.createDateTimeNotEqual(t);
		case DATETIME_GREATER_THAN:
			return BUILTIN.createDateTimeGreater(t);
		case DATETIME_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createDateTimeGreaterEqual(t);
		case DATETIME_LESS_THAN:
			return BUILTIN.createDateTimeLess(t);
		case DATETIME_LESS_THAN_OR_EQUAL:
			return BUILTIN.createDateTimeLessEqual(t);
		case DAY_FROM_DATE:
			return BUILTIN.createDayFromDate(t);
		case DAY_FROM_DATETIME:
			return BUILTIN.createDayFromDateTime(t);
		case DAYS_FROM_DURATION:
			return BUILTIN.createDaysFromDuration(t);
		case DAYTIMEDURATION_GREATER_THAN:
			return BUILTIN.createDayTimeDurationGreater(t);
		case DAYTIMEDURATION_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createDayTimeDurationGreaterEqual(t);
		case DAYTIMEDURATION_LESS_THAN:
			return BUILTIN.createDayTimeDurationLess(t);
		case DAYTIMEDURATION_LESS_THAN_OR_EQUAL:
			return BUILTIN.createDayTimeDurationLessEqual(t);
		case DISTINCT_VALUES: // TODO mp: needed ? check
			// return BUILTIN
			// .createDistinctValues(t);
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case DIVIDE_DAYTIMEDURATION:
			return BUILTIN.createDayTimeDurationDivide(t);
		case DIVIDE_DAYTIMEDURATION_BY_DAYTIMEDURATION:
			return BUILTIN.createDayTimeDurationDivideByDayTimeDuration(t);
		case DIVIDE_YEARMONTHDURATION:
			return BUILTIN.createYearMonthDurationDivide(t);
		case DIVIDE_YEARMONTHDURATION_BY_YEARMONTHDURATION:
			return BUILTIN.createYearMonthDurationDivideByYearMonthDuration(t);
		case DURATION_EQUAL:
			return BUILTIN.createDurationEqual(t);
		case DURATION_NOT_EQUAL:
			return BUILTIN.createDurationNotEqual(t);
		case ENCODE_FOR_URI: // TODO mp: needed ? check
			// return BUILTIN.create
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case ENDS_WITH:
			return BUILTIN.createStringEndsWith(t);
		case ESCAPE_HTML_URI: // TODO mp: needed ? check
			// return BUILTIN.create
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case EXCEPT: // TODO mp: needed ? check
			// return BUILTIN.createExactE(t);
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case GET: // TODO mp: needed ? check
			// return BUILTIN.create
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case HOURS_FROM_DATETIME:
			return BUILTIN.createHoursFromDateTime(t);
		case HOURS_FROM_DURATION:
			return BUILTIN.createHoursFromDuration(t);
		case HOURS_FROM_TIME:
			return BUILTIN.createHoursFromTime(t);
		case INDEX_OF:// TODO mp: needed ? check
			// return BUILTIN.create
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case INSERT_BEFORE: // TODO mp: check
			// return BUILTIN.createStringReplace(terms)
		case INTERSECT: // TODO mp: needed ? check
			// return BUILTIN.create
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IRI_STRING:
			return BUILTIN.createIriString(t);
		case IRI_TO_URI: // TODO mp: needed ? check
			// return BUILTIN.createI
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IS_LIST: // TODO mp: needed ? check
			// return BUILTIN.createI
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IS_LITERAL_ANYURI:
			return BUILTIN.createIsAnyURI(t);
		case IS_LITERAL_BASE64BINARY:
			return BUILTIN.createIsBase64Binary(t);
		case IS_LITERAL_BOOLEAN:
			return BUILTIN.createIsBoolean(t);
		case IS_LITERAL_BYTE:
			return BUILTIN.createIsByte(t);
		case IS_LITERAL_DATE:
			return BUILTIN.createIsDate(t);
		case IS_LITERAL_DATETIME:
			return BUILTIN.createIsDateTime(t);
		case IS_LITERAL_DATETIMESTAMP:
			return BUILTIN.createIsDateTimeStamp(t);
		case IS_LITERAL_DAYTIMEDURATION:
			return BUILTIN.createIsDayTimeDuration(t);
		case IS_LITERAL_DECIMAL:
			return BUILTIN.createIsDecimal(t);
		case IS_LITERAL_DOUBLE:
			return BUILTIN.createIsDouble(t);
		case IS_LITERAL_FLOAT:
			return BUILTIN.createIsFloat(t);
		case IS_LITERAL_HEXBINARY:
			return BUILTIN.createIsHexBinary(t);
		case IS_LITERAL_INT:
			return BUILTIN.createIsInt(t);
		case IS_LITERAL_INTEGER:
			return BUILTIN.createIsInteger(t);
		case IS_LITERAL_LANGUAGE:
			return BUILTIN.createIsLanguage(t);
		case IS_LITERAL_LONG:
			return BUILTIN.createIsLong(t);
		case IS_LITERAL_NAME:
			return BUILTIN.createIsName(t);
		case IS_LITERAL_NCNAME:
			return BUILTIN.createIsNCName(t);
		case IS_LITERAL_NEGATIVEINTEGER:
			return BUILTIN.createIsNegativeInteger(t);
		case IS_LITERAL_NMTOKEN:
			return BUILTIN.createIsNMTOKEN(t);
		case IS_LITERAL_NONNEGATIVEINTEGER:
			return BUILTIN.createIsNonNegativeInteger(t);
		case IS_LITERAL_NONPOSITIVEINTEGER:
			return BUILTIN.createIsNonPositiveInteger(t);
		case IS_LITERAL_NORMALIZEDSTRING:
			return BUILTIN.createIsNormalizedString(t);
		case IS_LITERAL_NOT_ANYURI:
			return BUILTIN.createIsNotAnyURI(t);
		case IS_LITERAL_NOT_BASE64BINARY:
			return BUILTIN.createIsNotBase64Binary(t);
		case IS_LITERAL_NOT_BOOLEAN:
			return BUILTIN.createIsNotBoolean(t);
		case IS_LITERAL_NOT_BYTE:
			return BUILTIN.createIsNotByte(t);
		case IS_LITERAL_NOT_DATE:
			return BUILTIN.createIsNotDate(t);
		case IS_LITERAL_NOT_DATETIME:
			return BUILTIN.createIsNotDateTime(t);
		case IS_LITERAL_NOT_DATETIMESTAMP:
			return BUILTIN.createIsNotDateTimeStamp(t);
		case IS_LITERAL_NOT_DAYTIMEDURATION:
			return BUILTIN.createIsNotDayTimeDuration(t);
		case IS_LITERAL_NOT_DECIMAL:
			return BUILTIN.createIsNotDecimal(t);
		case IS_LITERAL_NOT_DOUBLE:
			return BUILTIN.createIsNotDouble(t);
		case IS_LITERAL_NOT_FLOAT:
			return BUILTIN.createIsNotFloat(t);
		case IS_LITERAL_NOT_HEXBINARY:
			return BUILTIN.createIsNotHexBinary(t);
		case IS_LITERAL_NOT_INTEGER:
			return BUILTIN.createIsNotInteger(t);
//		case IS_LITERAL_NOT_INT: // TODO mp: implement
//			// return BUILTIN.createIsNotInt(t);
		case IS_LITERAL_NOT_LANGUAGE:
			return BUILTIN.createIsNotLanguage(t);
		case IS_LITERAL_NOT_LONG:
			return BUILTIN.createIsNotLong(t);
		case IS_LITERAL_NOT_NAME:
			return BUILTIN.createIsNotName(t);
		case IS_LITERAL_NOT_NCNAME:
			return BUILTIN.createIsNotNCName(t);
		case IS_LITERAL_NOT_NEGATIVEINTEGER:
			return BUILTIN.createIsNotNegativeInteger(t);
		case IS_LITERAL_NOT_NMTOKEN:
			return BUILTIN.createIsNotNMTOKEN(t);
		case IS_LITERAL_NOT_NONNEGATIVEINTEGER:
			return BUILTIN.createIsNotNonNegativeInteger(t);
		case IS_LITERAL_NOT_NONPOSITIVEINTEGER:
			return BUILTIN.createIsNotNonPositiveInteger(t);
		case IS_LITERAL_NOT_NORMALIZEDSTRING:
			return BUILTIN.createIsNotNormalizedString(t);
		case IS_LITERAL_NOT_PLAINLITERAL: // TODO mp : implement
			// return BUILTIN.createIsNotPlainLiteral(t);
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IS_LITERAL_NOT_POSITIVEINTEGER:
			return BUILTIN.createIsNotPositiveInteger(t);
		case IS_LITERAL_NOT_SHORT:
			return BUILTIN.createIsNotShort(t);
		case IS_LITERAL_NOT_STRING:
			return BUILTIN.createIsNotString(t);
		case IS_LITERAL_NOT_TIME:
			return BUILTIN.createIsNotTime(t);
		case IS_LITERAL_NOT_TOKEN:
			return BUILTIN.createIsNotToken(t);
		case IS_LITERAL_NOT_UNSIGNEDBYTE:
			return BUILTIN.createIsNotUnsignedByte(t);
		case IS_LITERAL_NOT_UNSIGNEDINT:
			return BUILTIN.createIsNotUnsignedInt(t);
		case IS_LITERAL_NOT_UNSIGNEDLONG:
			return BUILTIN.createIsNotUnsignedLong(t);
		case IS_LITERAL_NOT_UNSIGNEDSHORT:
			return BUILTIN.createIsNotUnsignedShort(t);
		case IS_LITERAL_NOT_XMLLITERAL:
			return BUILTIN.createIsNotXMLLiteral(t);
		case IS_LITERAL_NOT_YEARMONTHDURATION:
			return BUILTIN.createIsNotYearMonthDuration(t);
		case IS_LITERAL_PLAINLITERAL: // TODO mp : implement
//			return BUILTIN.crea
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case IS_LITERAL_POSITIVEINTEGER:
			return BUILTIN.createIsPositiveInteger(t);
		case IS_LITERAL_SHORT:
			return BUILTIN.createIsShort(t);
		case IS_LITERAL_STRING:
			return BUILTIN.createIsString(t);
		case IS_LITERAL_TIME:
			return BUILTIN.createIsTime(t);
		case IS_LITERAL_TOKEN:
			return BUILTIN.createIsToken(t);
		case IS_LITERAL_UNSIGNEDBYTE:
			return BUILTIN.createIsUnsignedByte(t);
		case IS_LITERAL_UNSIGNEDINT:
			return BUILTIN.createIsUnsignedInt(t);
		case IS_LITERAL_UNSIGNEDLONG:
			return BUILTIN.createIsUnsignedLong(t);
		case IS_LITERAL_UNSIGNEDSHORT:
			return BUILTIN.createIsUnsignedShort(t);
		case IS_LITERAL_XMLLITERAL:
			return BUILTIN.createIsXMLLiteral(t);
		case IS_LITERAL_YEARMONTHDURATION:
			return BUILTIN.createIsYearMonthDuration(t);
		case LANG_FROM_PLAINLITERAL: 				// TODO mp: error
			return BUILTIN.createLangFromText(t);
//		case LIST_CONTAINS: // TODO mp: implement?
//			// return BUILTIN.create
//			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
//					BASIC.createTuple(terms));
//		case LITERAL_NOT_IDENTICAL: // TODO mp: implement?
//			// return BUILTIN.createl
//			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
//					BASIC.createTuple(terms));
//		case LOWER_CASE:
//			return BUILTIN.createStringToLower(t);
//		case MAKE_LISTS: // TODO mp: implement?
//			// return BUILTIN.createMa
//			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
//					BASIC.createTuple(terms));
//		case MATCHES:
//			return BUILTIN.createStringMatches(t);
//		case MATCHES_LANGUAGE_RANGE: // TODO mp: implement?
//			// return BUILTIN.creatematch
//			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
//					BASIC.createTuple(terms));
		case MINUTES_FROM_DATETIME:
			return BUILTIN.createMinutesFromDateTime(t);
		case MINUTES_FROM_DURATION:
			return BUILTIN.createMinutesFromDuration(t);
		case MINUTES_FROM_TIME:
			return BUILTIN.createMinutesFromTime(t);
		case MONTH_FROM_DATE:
			return BUILTIN.createMonthFromDate(t);
		case MONTH_FROM_DATETIME:
			return BUILTIN.createMonthFromDate(t);
		case MONTHS_FROM_DURATION:
			return BUILTIN.createMonthsFromDuration(t);
		case MULTIPLY_DAYTIMEDURATION: // TODO mp: implement?
			// return BUILTIN.createMultiplyBuiltin(, t1, t2)
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case MULTIPLY_YEARMONTHDURATION:
			// return BUILTIN.createMultiplyBuiltin(t0, t1, t2)
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case NOT: // TODO mp: implement?
			// return BUILTIN.createNot
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		
		case NUMERIC_ADD:
			return BUILTIN.createNumericAdd(t);
		case NUMERIC_DIVIDE:
			return BUILTIN.createNumericDivide(t);
		case NUMERIC_EQUAL:
			return BUILTIN.createNumericEqual(t);
		case NUMERIC_GREATER_THAN:
			return BUILTIN.createNumericGreater(t);
		case NUMERIC_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createNumericGreaterEqual(t);
		case NUMERIC_INTEGER_DIVIDE:
			return BUILTIN.createNumericIntegerDivide(t);
		case NUMERIC_LESS_THAN:
			return BUILTIN.createNumericLess(t);
		case NUMERIC_LESS_THAN_OR_EQUAL:
			return BUILTIN.createNumericLessEqual(t);
		case NUMERIC_MOD:
			return BUILTIN.createNumericModulus(t);
		case NUMERIC_MULTIPLY:
			return BUILTIN.createNumericMultiply(t);
		case NUMERIC_NOT_EQUAL:
			return BUILTIN.createNumericNotEqual(t);
		case NUMERIC_SUBTRACT:
			return BUILTIN.createNumericSubtract(t);
		case PLAINLITERAL_COMPARE:						// TODO mp: implement?
			// return BUILTIN.create
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case PLAINLITERAL_FROM_STRING_LANG: 			// TODO mp: error
			return BUILTIN.createTextFromStringLang(t);
		case PLAINLITERAL_LENGTH:// TODO mp: implement?
			// return BUILTIN.create
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case REMOVE: // TODO mp : implement
			// return BUILTIN.createStringReplace(terms)
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case REPLACE:
			return BUILTIN.createStringReplace(t);
		case REVERSE: // TODO mp : implement
			// return BUILTIN.createStringr
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case SECONDS_FROM_DATETIME:
			return BUILTIN.createSecondsFromDateTime(t);
		case SECONDS_FROM_DURATION:
			return BUILTIN.createSecondsFromDuration(t);
		case SECONDS_FROM_TIME:
			return BUILTIN.createSecondsFromTime(t);
		case STARTS_WITH:
			return BUILTIN.createStringStartsWith(t);
		case STRING_FROM_PLAINLITERAL: 				// TODO mp: error
			return BUILTIN.createStringFromText(t);
		case STRING_JOIN:
			return BUILTIN.createStringJoin(t);
		case STRING_LENGTH:
			return BUILTIN.createStringLength(t);
		case SUBLIST: // TODO mp: implement?
			// return BUILTIN.createsubl
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case SUBSTRING:
			return BUILTIN.createStringSubstring(t);
		case SUBSTRING_AFTER:
			return BUILTIN.createStringSubstringAfter(t);
		case SUBSTRING_BEFORE:
			return BUILTIN.createStringSubstringBefore(t);
		case SUBTRACT_DATES: // TODO mp: implement
			// return BUILTIN.createSubtractBuiltin(t0, t1, t2)
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case SUBTRACT_DATETIMES: 
			return BUILTIN.createDateTimeSubtract(t);
		case SUBTRACT_DAYTIMEDURATION_FROM_DATE:
			return BUILTIN.createSubtractDayTimeDurationFromDate(t);
		case SUBTRACT_DAYTIMEDURATION_FROM_DATETIME:
			return BUILTIN.createSubtractDayTimeDurationFromDateTime(t);
		case SUBTRACT_DAYTIMEDURATION_FROM_TIME:
			return BUILTIN.createSubtractDayTimeDurationFromTime(t);
		case SUBTRACT_DAYTIMEDURATIONS: 
			return BUILTIN.createDayTimeDurationSubtract(t);
		case SUBTRACT_TIMES: 
			return BUILTIN.createTimeSubtract(t);
		case SUBTRACT_YEARMONTHDURATION_FROM_DATE:
			return BUILTIN.createSubtractYearMonthDurationFromDate(t);
		case SUBTRACT_YEARMONTHDURATION_FROM_DATETIME:
			return BUILTIN.createSubtractYearMonthDurationFromDateTime(t);
		case SUBTRACT_YEARMONTHDURATIONS:
			return BUILTIN.createYearMonthDurationSubtract(t);
		case TIME_EQUAL:
			return BUILTIN.createTimeEqual(t);
		case TIME_GREATER_THAN:
			return BUILTIN.createTimeGreater(t);
		case TIME_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createTimeGreaterEqual(t);
		case TIME_LESS_THAN:
			return BUILTIN.createTimeLess(t);
		case TIME_LESS_THAN_OR_EQUAL:
			return BUILTIN.createTimeLessEqual(t);
		case TIME_NOT_EQUAL:
			return BUILTIN.createTimeNotEqual(t);
		case TIMEZONE_FROM_DATE:
			return BUILTIN.createTimezoneFromDate(t);
		case TIMEZONE_FROM_DATETIME:
			return BUILTIN.createTimezoneFromDateTime(t);
		case TIMEZONE_FROM_TIME:
			return BUILTIN.createTimezoneFromTime(t);
		case UNION: // TODO mp: implement
			// return BUILTIN.create
			return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()),
					BASIC.createTuple(terms));
		case UPPER_CASE:
			return BUILTIN.createStringToUpper(t);
		case XMLLITERAL_EQUAL:
			return BUILTIN.createXMLLiteralEqual(t);
		case XMLLITERAL_NOT_EQUAL:
			return BUILTIN.createXMLLiteralNotEqual(t);
		case YEAR_FROM_DATE:
			return BUILTIN.createYearFromDate(t);
		case YEAR_FROM_DATETIME:
			return BUILTIN.createYearFromDateTime(t);
		case YEARMONTHDURATION_GREATER_THAN:
			return BUILTIN.createYearMonthDurationGreater(t);
		case YEARMONTHDURATION_GREATER_THAN_OR_EQUAL:
			return BUILTIN.createYearMonthDurationGreaterEqual(t);
		case YEARMONTHDURATION_LESS_THAN:
			return BUILTIN.createYearMonthDurationLess(t);
		case YEARMONTHDURATION_LESS_THAN_OR_EQUAL:
			return BUILTIN.createYearMonthDurationLessEqual(t);
		case YEARS_FROM_DURATION:
			return BUILTIN.createYearsFromDuration(t);
			// TODO mp: some more builtins ? : yearmonthdurationadd,...?
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
