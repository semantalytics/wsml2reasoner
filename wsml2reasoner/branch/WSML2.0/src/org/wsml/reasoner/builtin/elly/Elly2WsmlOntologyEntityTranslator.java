package org.wsml.reasoner.builtin.elly;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TimeZone;

import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IBase64Binary;
import org.deri.iris.api.terms.concrete.IBooleanTerm;
import org.deri.iris.api.terms.concrete.IDateTerm;
import org.deri.iris.api.terms.concrete.IDateTime;
import org.deri.iris.api.terms.concrete.IDayTimeDuration;
import org.deri.iris.api.terms.concrete.IDecimalTerm;
import org.deri.iris.api.terms.concrete.IDoubleTerm;
import org.deri.iris.api.terms.concrete.IDuration;
import org.deri.iris.api.terms.concrete.IFloatTerm;
import org.deri.iris.api.terms.concrete.IGDay;
import org.deri.iris.api.terms.concrete.IGMonth;
import org.deri.iris.api.terms.concrete.IGMonthDay;
import org.deri.iris.api.terms.concrete.IGYear;
import org.deri.iris.api.terms.concrete.IGYearMonth;
import org.deri.iris.api.terms.concrete.IHexBinary;
import org.deri.iris.api.terms.concrete.IIntegerTerm;
import org.deri.iris.api.terms.concrete.ISqName;
import org.deri.iris.api.terms.concrete.IText;
import org.deri.iris.api.terms.concrete.ITime;
import org.deri.iris.api.terms.concrete.IXMLLiteral;
import org.deri.iris.api.terms.concrete.IYearMonthDuration;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicDescription;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.factory.FactoryContainer;

public class Elly2WsmlOntologyEntityTranslator {

	private final FactoryContainer container;

	public Elly2WsmlOntologyEntityTranslator() {
		this(new WsmlFactoryContainer());
	}

	public Elly2WsmlOntologyEntityTranslator(FactoryContainer container) {
		this.container = container;
	}

	public Concept createConcept(IAtomicConcept concept) {
		Identifier identifier = createIRI(concept);
		return container.getWsmoFactory().createConcept(identifier);
	}

	private String asString(IAtomicDescription description) {
		return description.getPredicate().toString();
	}

	public Instance createInstance(IIndividual individual) {
		Identifier identifier = container.getWsmoFactory().createIRI(asString(individual));
		return container.getWsmoFactory().createInstance(identifier);
	}

	private String asString(ITerm term) {
		return term.toString();
	}

	public IRI createIRI(IAtomicDescription description) {
		return container.getWsmoFactory().createIRI(asString(description));
	}

	/**
	 * Converts a iris term to an wsmo term
	 * 
	 * @param t
	 *            the iris term
	 * @return the converted wsmo term
	 * @throws IllegalArgumentException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	public Term createTerm(ITerm t) {
		Term term = _createTerm(t);
		if (term == null) {
			term = _createDataValue(t);
		}
		
		if (term == null)
			throw new IllegalArgumentException("Can't convert a term of type " + t.getClass().getName());
		else
			return term;
	}

	/**
	 * Converts a iris term to an wsmo term
	 * 
	 * @param t
	 *            the iris term
	 * @return the converted wsmo term
	 * @throws IllegalArgumentException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	public DataValue createDataValue(ITerm t) {
		DataValue dataValue = _createDataValue(t);
		
		if (dataValue == null)
			throw new IllegalArgumentException("Can't convert a term of type " + t.getClass().getName());
		else
			return dataValue;
	}

	/**
	 * Converts an elly term to an wsmo term
	 * 
	 * @param t
	 *            the elly term
	 * @return the converted wsmo term
	 * @throws IllegalArgumentException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	private Term _createTerm(ITerm t) {
		if (t == null) {
			throw new IllegalArgumentException("The term must not be null");
		}

		if (t instanceof IIndividual) {
			return container.getWsmoFactory().createIRI(((IIndividual) t).getValue());
		}
		return null;
	}

	/**
	 * Converts a iris term to an wsmo term
	 * 
	 * @param t
	 *            the iris term
	 * @return the converted wsmo term
	 * @throws IllegalArgumentException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	private DataValue _createDataValue(ITerm t) {
		if (t == null) {
			throw new IllegalArgumentException("The term must not be null");
		}
		/*
		 * subinterfaces of IStringTerm have to be handeled before the
		 * IStringTerm block
		 */
		if (t instanceof IBase64Binary) {
			return container.getXmlDataFactory().createBase64Binary(((IBase64Binary) t).getValue().getBytes());
		} else if (t instanceof IHexBinary) {
			return container.getXmlDataFactory().createHexBinary(((IHexBinary) t).getValue().getBytes());
		} else if (t instanceof IStringTerm) {
			return container.getXmlDataFactory().createString(((IStringTerm) t).getValue());
		} else if (t instanceof IBooleanTerm) {
			return container.getXmlDataFactory().createBoolean(((IBooleanTerm) t).getValue());
		} else if (t instanceof IDateTerm) {
			final IDateTerm dt = (IDateTerm) t;
			int[] tzData = getTZData(dt.getTimeZone());
			return container.getXmlDataFactory().createDate(dt.getYear(), dt.getMonth(), dt.getDay(), tzData[0],
					tzData[1]);
		} else if (t instanceof IDateTime) {
			final IDateTime dt = (IDateTime) t;
			int[] tzData = getTZData(dt.getTimeZone());
			return container.getXmlDataFactory().createDateTime(dt.getYear(), dt.getMonth(), dt.getDay(), dt.getHour(),
					dt.getMinute(), (float) dt.getDecimalSecond(), tzData[0], tzData[1]);
		} else if (t instanceof ITime) {
			final ITime time = (ITime) t;
			int[] tzData = getTZData(time.getTimeZone());
			return container.getXmlDataFactory().createTime(time.getHour(), time.getMinute(),
					(float) time.getDecimalSecond(),
					tzData[0], tzData[1]);
		} else if (t instanceof IDecimalTerm) {
			return container.getXmlDataFactory().createDecimal(new BigDecimal(((IDecimalTerm) t).toString()));
		} else if (t instanceof IDoubleTerm) {
			return container.getXmlDataFactory().createDouble(((IDoubleTerm) t).getValue());
		}
		else if (t instanceof IDuration) {
			final IDuration dt = (IDuration) t;
			return container.getXmlDataFactory().createDuration(dt.getYear(), dt.getMonth(), dt.getDay(), dt.getHour(),
					dt.getMinute(), dt.getDecimalSecond());
		} else if (t instanceof IFloatTerm) {
			return container.getXmlDataFactory().createFloat(((IFloatTerm) t).getValue());
		} else if (t instanceof IGDay) {
			return container.getXmlDataFactory().createGregorianDay(((IGDay) t).getDay());
		} else if (t instanceof IGMonth) {
			return container.getXmlDataFactory().createGregorianMonth(((IGMonth) t).getMonth());
		} else if (t instanceof IGMonthDay) {
			final IGMonthDay md = (IGMonthDay) t;
			return container.getXmlDataFactory().createGregorianMonthDay(md.getMonth(), md.getDay());
		} else if (t instanceof IGYear) {
			return container.getXmlDataFactory().createGregorianYear(((IGYear) t).getYear());
		} else if (t instanceof IGYearMonth) {
			final IGYearMonth md = (IGYearMonth) t;
			return container.getXmlDataFactory().createGregorianYearMonth(md.getYear(), md.getMonth());
		} else if (t instanceof IIntegerTerm) {
			return container.getXmlDataFactory().createInteger(new BigInteger(t.getValue().toString()));
		} else if (t instanceof IYearMonthDuration) {
			return container.getXmlDataFactory().createYearMonthDuration(((IYearMonthDuration) t).getYear(),
					((IYearMonthDuration) t).getMonth());
		} else if (t instanceof IDayTimeDuration) {
			return container.getXmlDataFactory().createDayTimeDuration(((IDayTimeDuration) t).getDay(),
					((IDayTimeDuration) t).getHour(), ((IDayTimeDuration) t).getMinute(),
					((IDayTimeDuration) t).getSecond());
		} else if (t instanceof IXMLLiteral) {
			// checks if there is a language string
			String lang;
			if (((IXMLLiteral) t).getLang() == null) {
				lang = "";
			} else {
				lang = ((IXMLLiteral) t).getLang();
			}
			return container.getXmlDataFactory().createXMLLiteral(((IXMLLiteral) t).getString(), lang);
		} else if (t instanceof IText) {
			// checks if there is a language string
			String lang;
			if (((IText) t).getLang() == null) {
				lang = "";
			} else {
				lang = ((IText) t).getLang();
			}
			return container.getXmlDataFactory().createText(((IText) t).getString(), lang);
		} else if (t instanceof ISqName) {
			// couldn't find this type in wsmo4j
		}
		
		return null;
	}

	/**
	 * Calculates the timezone hours and timezone minutes for a given timezone
	 * 
	 * @param t
	 *            the timezon for which to calculate the hours and minutes
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

}
