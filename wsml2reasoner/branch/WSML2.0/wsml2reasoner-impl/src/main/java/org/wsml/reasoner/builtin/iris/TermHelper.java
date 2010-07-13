package org.wsml.reasoner.builtin.iris;

import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.api.terms.concrete.IAnyURI;
import org.deri.iris.api.terms.concrete.IBase64Binary;
import org.deri.iris.api.terms.concrete.IBooleanTerm;
import org.deri.iris.api.terms.concrete.IByteTerm;
import org.deri.iris.api.terms.concrete.IDateTerm;
import org.deri.iris.api.terms.concrete.IDateTime;
import org.deri.iris.api.terms.concrete.IDateTimeStamp;
import org.deri.iris.api.terms.concrete.IDayTimeDuration;
import org.deri.iris.api.terms.concrete.IDecimalTerm;
import org.deri.iris.api.terms.concrete.IDoubleTerm;
import org.deri.iris.api.terms.concrete.IDuration;
import org.deri.iris.api.terms.concrete.IENTITY;
import org.deri.iris.api.terms.concrete.IFloatTerm;
import org.deri.iris.api.terms.concrete.IGDay;
import org.deri.iris.api.terms.concrete.IGMonth;
import org.deri.iris.api.terms.concrete.IGMonthDay;
import org.deri.iris.api.terms.concrete.IGYear;
import org.deri.iris.api.terms.concrete.IGYearMonth;
import org.deri.iris.api.terms.concrete.IHexBinary;
import org.deri.iris.api.terms.concrete.IID;
import org.deri.iris.api.terms.concrete.IIDREF;
import org.deri.iris.api.terms.concrete.IIntTerm;
import org.deri.iris.api.terms.concrete.IIntegerTerm;
import org.deri.iris.api.terms.concrete.IIri;
import org.deri.iris.api.terms.concrete.ILanguage;
import org.deri.iris.api.terms.concrete.ILongTerm;
import org.deri.iris.api.terms.concrete.INCName;
import org.deri.iris.api.terms.concrete.INMTOKEN;
import org.deri.iris.api.terms.concrete.INOTATION;
import org.deri.iris.api.terms.concrete.IName;
import org.deri.iris.api.terms.concrete.INegativeInteger;
import org.deri.iris.api.terms.concrete.INonNegativeInteger;
import org.deri.iris.api.terms.concrete.INonPositiveInteger;
import org.deri.iris.api.terms.concrete.INormalizedString;
import org.deri.iris.api.terms.concrete.IPlainLiteral;
import org.deri.iris.api.terms.concrete.IPositiveInteger;
import org.deri.iris.api.terms.concrete.IShortTerm;
import org.deri.iris.api.terms.concrete.ISqName;
import org.deri.iris.api.terms.concrete.ITime;
import org.deri.iris.api.terms.concrete.IToken;
import org.deri.iris.api.terms.concrete.IUnsignedByte;
import org.deri.iris.api.terms.concrete.IUnsignedInt;
import org.deri.iris.api.terms.concrete.IUnsignedLong;
import org.deri.iris.api.terms.concrete.IUnsignedShort;
import org.deri.iris.api.terms.concrete.IXMLLiteral;
import org.deri.iris.api.terms.concrete.IYearMonthDuration;
import org.deri.iris.terms.concrete.SqName;
import org.omwg.logicalexpression.terms.BuiltInConstructedTerm;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.RDFDataType;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.omwg.ontology.XmlSchemaDataType;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class TermHelper {

	private TermHelper() {
	}

	/**
	 * Converts a iris term to an wsmo term
	 * 
	 * @param t
	 *            the iris term
	 * @return the converted wsmo term
	 * @throws NullPointerException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	public static Term convertTermFromIrisToWsmo4j(final ITerm t, FactoryContainer container) {
		if (t == null) {
			throw new IllegalArgumentException("The term must not be null");
		}
		if (container == null) {
			throw new IllegalArgumentException("The factory container must not be null");
		}

		WsmoFactory wsmoFactory = container.getWsmoFactory();
		LogicalExpressionFactory leFactory = container.getLogicalExpressionFactory();

		/*
		 * subinterfaces of IStringTerm have to be handeled before the IStringTerm block
		 */
		if (t instanceof IIri) {
			return wsmoFactory.createIRI(((IIri) t).getValue());
		} else if (t instanceof IVariable) {
			return leFactory.createVariable(((IVariable) t).getValue());
		} else if (t instanceof IConstructedTerm) {
			final IConstructedTerm ct = (IConstructedTerm) t;
			final List<Term> terms = new ArrayList<Term>(ct.getValue().size());
			for (final ITerm term : ct.getValue()) {
				terms.add(convertTermFromIrisToWsmo4j(term, container));
			}
			return leFactory.createConstructedTerm(wsmoFactory.createIRI(ct.getFunctionSymbol()), terms);
		} else {
			DataValue dataValue = convertDataValueFromIrisToWsmo4j(t, container);
			if (dataValue != null)
				return dataValue;
		}

		throw new IllegalArgumentException("Can't convert a term of type " + t.getClass().getName());
	}

	/**
	 * Converts a iris concrete term to an wsmo data value
	 * 
	 * @param t
	 *            the iris concrete term
	 * @return the converted wsmo data value
	 * @throws NullPointerException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	public static DataValue convertDataValueFromIrisToWsmo4j(final ITerm t, FactoryContainer container) {
		if (t == null) {
			throw new IllegalArgumentException("The term must not be null");
		}
		if (container == null) {
			throw new IllegalArgumentException("The factory container must not be null");
		}

		DataFactory dataFactory = container.getXmlDataFactory();

		/*
		 * subinterfaces of IStringTerm have to be handeled before the IStringTerm block
		 */
		if (t instanceof IAnyURI) {
			return dataFactory.createAnyURI(((IAnyURI) t).getValue());
		} else if (t instanceof IBase64Binary) {
			return dataFactory.createBase64Binary(((IBase64Binary) t).getValue().getBytes());
		} else if (t instanceof IBooleanTerm) {
			return dataFactory.createBoolean(((IBooleanTerm) t).getValue());
		} else if (t instanceof IDateTerm) {
			final IDateTerm dt = (IDateTerm) t;
			int[] tzData = getTZData(dt.getTimeZone());
			return dataFactory.createDate(dt.getYear(), dt.getMonth(), dt.getDay(), tzData[0], tzData[1]);
		} else if (t instanceof IDateTimeStamp) {
			final IDateTimeStamp dt = (IDateTimeStamp) t;
			int[] tzData = getTZData(dt.getTimeZone());
			return dataFactory.createDateTimeStamp(dt.getYear(), dt.getMonth(), dt.getDay(), 
					dt.getHour(), dt.getMinute(), dt.getDecimalSecond(),
					tzData[0], tzData[1]);
		} else if (t instanceof IDateTime) {
			final IDateTime dt = (IDateTime) t;
			int[] tzData = getTZData(dt.getTimeZone());
			return dataFactory.createDateTime(dt.getYear(), dt.getMonth(), dt.getDay(), 
					dt.getHour(), dt.getMinute(), dt.getDecimalSecond(),
					tzData[0], tzData[1]);
		} else if (t instanceof IYearMonthDuration) {
			final IYearMonthDuration dt = (IYearMonthDuration) t;
			
			List<Integer> values = new ArrayList<Integer>();
			values.add(dt.getYear());
			values.add(dt.getMonth());
			
			// negative => special treatment
			boolean signSet = dt.isPositive();
			// attach negative sign to first non-zero value
			if (!signSet) {
				for (int i = 0; i < values.size(); i++) {
					Integer value = values.get(i);
					if (value > 0) {
						signSet = true;
						values.set(i, value * -1);
						break;
					}
				}
			}
			return dataFactory.createYearMonthDuration(values.get(0), values.get(1));
		} else if (t instanceof IDayTimeDuration) {
			final IDayTimeDuration dt = (IDayTimeDuration) t;
			
			List<Integer> values = new ArrayList<Integer>();
			values.add(dt.getDay());
			values.add(dt.getHour());
			values.add(dt.getMinute());
			
			// getSeconds discards fractional part => handle seconds differently
			double seconds = dt.getDecimalSecond();
			
			// negative => special treatment
			boolean signSet = dt.isPositive();
			// attach negative sign to first non-zero value
			if (!signSet) {
				for (int i = 0; i < values.size(); i++) {
					Integer value = values.get(i);
					if (value > 0) {
						signSet = true;
						values.set(i, value * -1);
						break;
					}
				}
				if (!signSet) {
					seconds *= -1;
				}
			}
			return dataFactory.createDayTimeDuration(values.get(0), values.get(1), values.get(2), seconds);
		} else if (t instanceof IDuration) {
			// it is essential that IDuration comes after IDayTimeDuration and IYearMonthDuration
			// since those interfaces are extend IDuration
			final IDuration dt = (IDuration) t;
			
			List<Integer> values = new ArrayList<Integer>();
			values.add(dt.getYear());
			values.add(dt.getMonth());
			values.add(dt.getDay());
			values.add(dt.getHour());
			values.add(dt.getMinute());
			
			// getSeconds discards fractional part => handle seconds differently
			double seconds = dt.getDecimalSecond();
			
			// negative => special treatment
			boolean signSet = dt.isPositive();
			// attach negative sign to first non-zero value
			if (!signSet) {
				for (int i = 0; i < values.size(); i++) {
					Integer value = values.get(i);
					if (value > 0) {
						signSet = true;
						values.set(i, value * -1);
						break;
					}
				}
				if (!signSet) {
					seconds *= -1;
				}
			}
			return dataFactory.createDuration(values.get(0), values.get(1), values.get(2), values.get(3), values.get(4), seconds);
		} else if (t instanceof IGDay) {
			return dataFactory.createGregorianDay(((IGDay) t).getDay());
		} else if (t instanceof IGMonth) {
			return dataFactory.createGregorianMonth(((IGMonth) t).getMonth());
		} else if (t instanceof IGMonthDay) {
			final IGMonthDay md = (IGMonthDay) t;
			return dataFactory.createGregorianMonthDay(md.getMonth(), md.getDay());
		} else if (t instanceof IGYear) {
			return dataFactory.createGregorianYear(((IGYear) t).getYear());
		} else if (t instanceof IGYearMonth) {
			final IGYearMonth md = (IGYearMonth) t;
			return dataFactory.createGregorianYearMonth(md.getYear(), md.getMonth());
		} else if (t instanceof IHexBinary) {
			return dataFactory.createHexBinary(((IHexBinary) t).getValue().getBytes());
		} else if (t instanceof INOTATION) {
			String namespace = ((SqName) t).getNamespace().getValue();
			String localPart = ((SqName) t).getName();
			
			return dataFactory.createNotation(namespace, localPart);
			
		// TODO PrecisionDecimal is missing
			
		} else if (t instanceof ISqName) {
			String namespace = ((SqName) t).getNamespace().getValue();
			String localPart = ((SqName) t).getName();
			
			return dataFactory.createQName(namespace, localPart);
		} else if (t instanceof INMTOKEN) {
			return dataFactory.createNMTOKEN(((IStringTerm) t).getValue());
		} else if (t instanceof IIDREF) {
			return dataFactory.createIDREF(((IStringTerm) t).getValue());
		} else if (t instanceof IID) {
			return dataFactory.createID(((IStringTerm) t).getValue());
		} else if (t instanceof IENTITY) {
			return dataFactory.createENTITY(((IStringTerm) t).getValue());
		} else if (t instanceof INCName) {
			return dataFactory.createNCName(((IStringTerm) t).getValue());
		} else if (t instanceof IName) {
			return dataFactory.createName(((IStringTerm) t).getValue());
		} else if (t instanceof ILanguage) {
			return dataFactory.createLanguage(((IStringTerm) t).getValue());
		} else if (t instanceof IToken) {
			return dataFactory.createToken(((IStringTerm) t).getValue());
		} else if (t instanceof INormalizedString) {
			return dataFactory.createNormalizedString(((IStringTerm) t).getValue());
		} else if (t instanceof IStringTerm) {
			return dataFactory.createString(((IStringTerm) t).getValue());
			
		} else if (t instanceof ITime) {
			final ITime time = (ITime) t;
			int[] tzData = getTZData(time.getTimeZone());
			return dataFactory.createTime(time.getHour(), time.getMinute(), time.getDecimalSecond(),
					tzData[0], tzData[1]);
			
		// Numeric
		} else if (t instanceof IFloatTerm) {
			return dataFactory.createFloat(((IFloatTerm) t).getValue().floatValue());
		} else if (t instanceof IDoubleTerm) {
			return dataFactory.createDouble(((IDoubleTerm) t).getValue().doubleValue());
		} else if (t instanceof IByteTerm) {
			return dataFactory.createByte(t.getValue().toString());
		} else if (t instanceof IShortTerm) {
			return dataFactory.createShort(t.getValue().toString());
		} else if (t instanceof IIntTerm) {
			return dataFactory.createInt(t.getValue().toString());
		} else if (t instanceof ILongTerm) {
			return dataFactory.createLong(t.getValue().toString());
			
		} else if (t instanceof INegativeInteger) {
			return dataFactory.createNegativeInteger(t.getValue().toString());
		} else if (t instanceof INonPositiveInteger) {
			return dataFactory.createNonPositiveInteger(t.getValue().toString());
			
		} else if (t instanceof IUnsignedByte) {
			return dataFactory.createUnsignedByte(t.getValue().toString());
		} else if (t instanceof IUnsignedShort) {
			return dataFactory.createUnsignedShort(t.getValue().toString());
		} else if (t instanceof IUnsignedInt) {
			return dataFactory.createUnsignedInt(t.getValue().toString());
		} else if (t instanceof IUnsignedLong) {
			return dataFactory.createUnsignedLong(t.getValue().toString());
			
		} else if (t instanceof IPositiveInteger) {
			return dataFactory.createPositiveInteger(t.getValue().toString());
		} else if (t instanceof INonNegativeInteger) {
			return dataFactory.createNonNegativeInteger(t.getValue().toString());
			
		} else if (t instanceof IIntegerTerm) {
			return dataFactory.createInteger(t.getValue().toString());
		} else if (t instanceof IDecimalTerm) {
			return dataFactory.createDecimal(((IDecimalTerm) t).toString());

		
		// RDF
		} else if (t instanceof IXMLLiteral) {
			// checks if there is a language string
			String lang;
			if (((IXMLLiteral) t).getLang() == null) {
				lang = "";
			} else {
				lang = ((IXMLLiteral) t).getLang();
			}
			return dataFactory.createXMLLiteral(((IXMLLiteral) t).getString(), lang);
		} else if (t instanceof IPlainLiteral) {
			// checks if there is a language string
			String lang;
			if (((IPlainLiteral) t).getLang() == null) {
				lang = "";
			} else {
				lang = ((IPlainLiteral) t).getLang();
			}
			return dataFactory.createPlainLiteral(((IPlainLiteral) t).getString(), lang);
		} 

		throw new IllegalArgumentException("Can't convert a term of type " + t.getClass().getName());
	}

	/**
	 * Calculates the timezone hours and timezone minutes for a given timezone.
	 * 
	 * @param t
	 *            the timezone for which to calculate the hours and minutes
	 * @return an array with the hours at index 0 and minutes at index 1
	 * @throws NullPointerException
	 *             if the timezone is {@code null}
	 */
	static int[] getTZData(final TimeZone t) {
		if (t == null) {
			throw new NullPointerException("The TimeZone must not be null");
		}
		return new int[] { t.getRawOffset() / 3600000, t.getRawOffset() % 3600000 / 60000 };
	}

	/**
	 * Converts a wsmo4j term to an iris term
	 * 
	 * @param t
	 *            the wsmo4j term
	 * @return the converted iris term
	 */
	public static ITerm convertTermFromWsmo4jToIris(final Term t) {
		if (t == null) {
			throw new NullPointerException("The term must not be null");
		} else if (t instanceof BuiltInConstructedTerm) {
			// TODO: builtins are skipped at the moment
		} else if (t instanceof ConstructedTerm) {
			// System.out.println("CONSTRUCTED TERM: " + t);
			final ConstructedTerm ct = (ConstructedTerm) t;
			final List<ITerm> terms = new ArrayList<ITerm>(ct.getArity());
			for (final Term term : (List<Term>) ct.listParameters()) {
				terms.add(convertTermFromWsmo4jToIris(term));
			}
			return TERM.createConstruct(ct.getFunctionSymbol().toString(), terms);
		} else if (t instanceof DataValue) {
			// System.out.println("DATAVALUE: " + t);
			return convertWsmo4jDataValueToIrisTerm((DataValue) t);
		} else if (t instanceof IRI) {
			// System.out.println("IRI: " + t);
			return CONCRETE.createIri(t.toString());
		} else if (t instanceof Variable) {
			// System.out.println("VARIABLE: " + t);
			return TERM.createVariable(((Variable) t).getName());
		} else if (t instanceof Identifier) {
			// System.out.println("IDENTIFIER: " + t);
			// i doubt we got something analogous in iris -> exception
		} else if (t instanceof NumberedAnonymousID) {
			// System.out.println("NUMBEREDANONYMOUSID: " + t);
			// i doubt we got something analogous in iris -> exception
		} else if (t instanceof UnnumberedAnonymousID) {
			// System.out.println("UNNUMBEREDANONYMOUSID: " + t);
			// i doubt we got something analogous in iris -> exception
		}
		throw new IllegalArgumentException("Can't convert a term of type " + t.getClass().getName());
	}

	/**
	 * Converts a wsmo4j DataValue to an iris ITerm.
	 * 
	 * @param v
	 *            the wsmo4j value to convert
	 * @return the corresponding ITerm implementation
	 */
	public static IConcreteTerm convertWsmo4jDataValueToIrisTerm(final DataValue v) {
		if (v == null) {
			throw new NullPointerException("The data value must not be null");
		}
		final String t = v.getType().getIdentifier().toString();
		if (t.equals(WsmlDataType.WSML_BASE64BINARY) || t.equals(XmlSchemaDataType.XSD_BASE64BINARY)) {
			return CONCRETE.createBase64Binary((String) v.getValue());
		} else if (t.equals(WsmlDataType.WSML_BOOLEAN) || t.equals(XmlSchemaDataType.XSD_BOOLEAN)) {
			return CONCRETE.createBoolean((Boolean) v.getValue());
		} else if (t.equals(WsmlDataType.WSML_DATE) || t.equals(XmlSchemaDataType.XSD_DATE)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			int length = cv.getArity();
			return CONCRETE.createDate(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2), length > 3 ? getIntFromValue(cv, 3)
					: 0, length > 4 ? getIntFromValue(cv, 4) : 0);
		} else if (t.equals(WsmlDataType.WSML_DATETIME) || t.equals(XmlSchemaDataType.XSD_DATETIME)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			int length = cv.getArity();
			return CONCRETE.createDateTime(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2), getIntFromValue(cv, 3),
					getIntFromValue(cv, 4), getDoubleFromValue(cv, 5), length > 6 ? getIntFromValue(cv, 6) : 0, length > 7 ? getIntFromValue(cv, 7)
							: 0);
		} else if (t.equals(WsmlDataType.WSML_TIME) || t.equals(XmlSchemaDataType.XSD_TIME)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			int length = cv.getArity();
			return CONCRETE.createTime(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getDoubleFromValue(cv, 2), length > 3 ? getIntFromValue(cv, 3)
					: 0, length > 4 ? getIntFromValue(cv, 4) : 0);
		} else if (t.equals(WsmlDataType.WSML_DECIMAL) || t.equals(XmlSchemaDataType.XSD_DECIMAL)) {
			return CONCRETE.createDecimal((BigDecimal) v.getValue());
		} else if (t.equals(WsmlDataType.WSML_DOUBLE) || t.equals(XmlSchemaDataType.XSD_DOUBLE)) {
			return CONCRETE.createDouble((Double) v.getValue());
		} else if (t.equals(WsmlDataType.WSML_DURATION) || t.equals(XmlSchemaDataType.XSD_DURATION)) {
			final ComplexDataValue cv = (ComplexDataValue) v;

			int signum = 0;
			List<Integer> values = new ArrayList<Integer>();
			for (int i = 0; i <= 4; ++i) {
				int value = getIntFromValue(cv, i);
				values.add(Math.abs(value)); // Add absolute value

				if (signum == 0) { // signum not set yet
					if (value > 0) {
						signum = +1;
					} else if (value < 0) {
						signum = -1;
					}
				}
			}
			double seconds = getDoubleFromValue(cv, 5);
			if (signum == 0) { // signum not set yet
				if (seconds > 0.0) {
					signum = +1;
				} else if (seconds < 0.0) {
					signum = -1;
				}
			}
			seconds = Math.abs(seconds); // Add absolute value

			return CONCRETE.createDuration(signum >= 0, values.get(0), values.get(1), values.get(2), values.get(3), values.get(4), seconds);
		} else if (t.equals(WsmlDataType.WSML_FLOAT) || t.equals(XmlSchemaDataType.XSD_FLOAT)) {
			return CONCRETE.createFloat((Float) v.getValue());
		} else if (t.equals(WsmlDataType.WSML_GDAY) || t.equals(XmlSchemaDataType.XSD_GDAY)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGDay(getIntFromValue(cv, 0));
		} else if (t.equals(WsmlDataType.WSML_GMONTH) || t.equals(XmlSchemaDataType.XSD_GMONTH)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGMonth(getIntFromValue(cv, 0));
		} else if (t.equals(WsmlDataType.WSML_GMONTHDAY) || t.equals(XmlSchemaDataType.XSD_GMONTHDAY)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGMonthDay(getIntFromValue(cv, 0), getIntFromValue(cv, 1));
		} else if (t.equals(WsmlDataType.WSML_GYEAR) || t.equals(XmlSchemaDataType.XSD_GYEAR)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGYear(getIntFromValue(cv, 0));
		} else if (t.equals(WsmlDataType.WSML_GYEARMONTH) || t.equals(XmlSchemaDataType.XSD_GYEARMONTH)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGYearMonth(getIntFromValue(cv, 0), getIntFromValue(cv, 1));
		} else if (t.equals(WsmlDataType.WSML_HEXBINARY) || t.equals(XmlSchemaDataType.XSD_HEXBINARY)) {
			return CONCRETE.createHexBinary((String) v.getValue());
		} else if (t.equals(WsmlDataType.WSML_INTEGER) || t.equals(XmlSchemaDataType.XSD_INTEGER)) {
			return CONCRETE.createInteger((BigInteger) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_DAYTIMEDURATION)) {
			final ComplexDataValue cv = (ComplexDataValue) v;

			int signum = 0;
			List<Integer> values = new ArrayList<Integer>();
			for (int i = 0; i <= 2; ++i) {
				int value = getIntFromValue(cv, i);
				values.add(Math.abs(value)); // Add absolute value

				if (signum == 0) { // signum not set yet
					if (value > 0) {
						signum = +1;
					} else if (value < 0) {
						signum = -1;
					}
				}
			}
			double seconds = getDoubleFromValue(cv, 3);
			if (signum == 0) { // signum not set yet
				if (seconds > 0.0) {
					signum = +1;
				} else if (seconds < 0.0) {
					signum = -1;
				}
			}
			seconds = Math.abs(seconds); // Add absolute value

			return CONCRETE.createDayTimeDuration(signum >= 0, values.get(0), values.get(1), values.get(2), seconds);
		} else if (t.equals(XmlSchemaDataType.XSD_YEARMONTHDURATION)) {
			final ComplexDataValue cv = (ComplexDataValue) v;

			int signum = 0;
			List<Integer> values = new ArrayList<Integer>();
			for (int i = 0; i <= 1; ++i) {
				int value = getIntFromValue(cv, i);
				values.add(Math.abs(value)); // Add absolute value

				if (signum == 0) { // signum not set yet
					if (value > 0) {
						signum = +1;
					} else if (value < 0) {
						signum = -1;
					}
				}
			}

			return CONCRETE.createYearMonthDuration(signum >= 0, values.get(0), values.get(1));
		} else if (t.equals(XmlSchemaDataType.XSD_LONG)) {
			return CONCRETE.createLong((Long) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_INT)) {
			return CONCRETE.createInt((Integer) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_SHORT)) {
			return CONCRETE.createShort((Short) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_BYTE)) {
			return CONCRETE.createByte((Byte) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_NONPOSITIVEINTEGER)) {
			return CONCRETE.createNonPositiveInteger((BigInteger) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_NONNEGATIVEINTEGER)) {
			return CONCRETE.createNonNegativeInteger((BigInteger) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_NEGATIVEINTEGER)) {
			return CONCRETE.createNegativeInteger((BigInteger) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_POSITIVEINTEGER)) {
			return CONCRETE.createPositiveInteger((BigInteger) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_UNSIGNEDLONG)) {
			return CONCRETE.createUnsignedLong((BigInteger) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_UNSIGNEDINT)) {
			return CONCRETE.createUnsignedInt((Long) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_UNSIGNEDSHORT)) {
			return CONCRETE.createUnsignedShort((Integer) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_UNSIGNEDBYTE)) {
			return CONCRETE.createUnsignedByte((Short) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_NORMALIZEDSTRING)) {
			return CONCRETE.createNormalizedString((String) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_STRING)) {
			return TERM.createString((String) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_NAME)) {
			return CONCRETE.createName((String) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_TOKEN)) {
			return CONCRETE.createToken((String) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_NMTOKEN)) {
			return CONCRETE.createNMTOKEN((String) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_NCNAME)) {
			return CONCRETE.createNCName((String) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_LANGUAGE)) {
			return CONCRETE.createLanguage((String) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_DATETIMESTAMP)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createDateTimeStamp(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2), getIntFromValue(cv, 3),
					getIntFromValue(cv, 4), getDoubleFromValue(cv, 5), getIntFromValue(cv, 6), getIntFromValue(cv, 7));
		} else if (t.equals(XmlSchemaDataType.XSD_ANYURI)) {
			URI uri = null;
			try {
				String stringValue = (String) v.getValue();
				uri = new URI(stringValue);
			} catch (URISyntaxException e) {
				throw new InternalReasonerException(e);
			}

			return CONCRETE.createAnyURI(uri);
		} else if (t.equals(XmlSchemaDataType.XSD_QNAME)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createQName(getStringFromValue(cv, 0), getStringFromValue(cv, 1));
		} else if (t.equals(XmlSchemaDataType.XSD_NOTATION)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createNOTATION(getStringFromValue(cv, 0), getStringFromValue(cv, 1));
		} else if (t.equals(XmlSchemaDataType.XSD_ID)) {
			return CONCRETE.createID((String) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_IDREF)) {
			return CONCRETE.createIDREF((String) v.getValue());
		} else if (t.equals(XmlSchemaDataType.XSD_ENTITY)) {
			return CONCRETE.createEntity((String) v.getValue());
		}

		// RDF Datatypes
		else if (t.equals(RDFDataType.RDF_PLAINLITERAL)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createPlainLiteral(getStringFromValue(cv, 0), getStringFromValue(cv, 1));
		} else if (t.equals(RDFDataType.RDF_XMLLITERAL)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createXMLLiteral(getStringFromValue(cv, 0), getStringFromValue(cv, 1));
		}
		throw new IllegalArgumentException("Can't convert a value of type " + t);
	}

	/**
	 * Returns the integer value of a ComplexDataValue at a given position.
	 * 
	 * @param value
	 *            the complex data value from where to get the int
	 * @param pos
	 *            the index of the integer
	 * @return the extracted and converted integer
	 */
	private static int getIntFromValue(final ComplexDataValue value, int pos) {
		assert value != null;
		assert pos >= 0;

		return Integer.parseInt(getFieldValue(value, pos));
	}

	/**
	 * Returns the BigInteger value of a ComplexDataValue at a given position.
	 * 
	 * @param value
	 *            the complex data value from where to get the BigInteger
	 * @param pos
	 *            the index of the integer
	 * @return the extracted and converted BigInteger
	 */
	private static BigInteger getBigIntegerFromValue(final ComplexDataValue value, int pos) {
		assert value != null;
		assert pos >= 0;

		return new BigInteger(getFieldValue(value, pos).toString());
	}

	/**
	 * Returns the long value of a ComplexDataValue at a given position.
	 * 
	 * @param value
	 *            the complex data value from where to get the long
	 * @param pos
	 *            the index of the integer
	 * @return the extracted and converted long
	 */
	private static Long getLongFromValue(final ComplexDataValue value, int pos) {
		assert value != null;
		assert pos >= 0;

		return new Long(getFieldValue(value, pos).toString());
	}

	/**
	 * Returns the String value of a ComplexDataValue at a given position.
	 * 
	 * @param value
	 *            the complex data value from where to get the String
	 * @param pos
	 *            the index of the String
	 * @return the extracted and converted String
	 */
	private static String getStringFromValue(final ComplexDataValue value, int pos) {
		assert value != null;
		assert pos >= 0;

		return new String(getFieldValue(value, pos).toString());
	}

	/**
	 * Get a double value from the specified position.
	 * 
	 * @param value
	 *            The complex data value from which the double is extracted.
	 * @param pos
	 *            The zero-basd index of the desired value.
	 * @return
	 */
	private static double getDoubleFromValue(final ComplexDataValue value, int pos) {
		assert value != null;
		assert pos >= 0;

		return Double.parseDouble(getFieldValue(value, pos));
	}

	/**
	 * Get a field of a complex value.
	 * 
	 * @param value
	 *            The complex value
	 * @param pos
	 *            The position of the file (zero-based index)
	 * @return The string-ised field value.
	 */
	private static String getFieldValue(final ComplexDataValue value, int pos) {
		assert value != null;
		assert pos >= 0;

		return value.getArgumentValue((byte) pos).getValue().toString();
	}

}
