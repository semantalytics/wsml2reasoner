/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package org.wsml.reasoner.builtin.iris;

// TODO: at the moment there is only support for one ontology
// TODO: do the builtins

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.PROGRAM;
import static org.deri.iris.factory.Factory.TERM;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deri.iris.Executor;
import org.deri.iris.api.IExecutor;
import org.deri.iris.api.IProgram;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.api.terms.concrete.IBase64Binary;
import org.deri.iris.api.terms.concrete.IBooleanTerm;
import org.deri.iris.api.terms.concrete.IDateTerm;
import org.deri.iris.api.terms.concrete.IDateTime;
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
import org.deri.iris.api.terms.concrete.IIri;
import org.deri.iris.api.terms.concrete.ISqName;
import org.deri.iris.evaluation.algebra.ExpressionEvaluator;
import org.omwg.logicalexpression.terms.BuiltInConstructedTerm;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.NumberedAnonymousID;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DatalogReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * <p>
 * The wsmo4j interface for the iris reasoner.
 * </p>
 * <p>
 * $Id: IrisFacade.java,v 1.3 2007-02-26 18:37:42 graham Exp $
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.3 $
 */
public class IrisFacade implements DatalogReasonerFacade {

	/** Factory to create the DataValues. */
	private final DataFactory DATA_FACTORY;

	/** Factory to create the DataValues. */
	private final LogicalExpressionFactory LOGIC_FACTORY;

	/** Factory to create the wsmo objects. */
	private final WsmoFactory WSMO_FACTORY;

	/** Pattern to convert a wsmo duration to an iris one. */
	private static final Pattern DURATION_PATTERN = Pattern
			.compile("P(\\d{4})Y(\\d{1,2})M(\\d{1,2})DT(\\d{1,2})H(\\d{1,2})M(\\d{1,2})S");

	// TODO: in the end the programms should be stored in a may with ontology_id
	// -> programm entries
	/** The iris program to evaluate the queries. */
	private IProgram p = PROGRAM.createProgram();

	// TODO: in the end this should be stored in a may with ontology_id ->
	// changed entries
	/**
	 * Records whether the ontology changed since the last calculation of the
	 * fixed point.
	 */
	private boolean factsChanged = true;

	// TODO: in the end this should be stored in a may with ontology_id ->
	// changed entries
	/**
	 * Records whether the ontology changed since the last calculation of the
	 * fixed point.
	 */
	private boolean rulesChanged = true;

	// TODO: in the end this should be stored in a may with ontology_id ->
	// changed entries
	/** Excutor the execute the queries. */
	private IExecutor e = new Executor(p, new ExpressionEvaluator());

	public IrisFacade(final WSMO4JManager m) {
		DATA_FACTORY = m.getDataFactory();
		WSMO_FACTORY = m.getWSMOFactory();
		LOGIC_FACTORY = m.getLogicalExpressionFactory();
	}

	public IrisFacade() {
		this(new WSMO4JManager());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DatalogReasonerFacade#deregister(java.lang.String)
	 */
	public synchronized void deregister(String ontologyURI)
			throws ExternalToolException {
		// the ontologyURI is at the moment ignored, because at the moment
		// program only supports one instance per vm
		rulesChanged = true;
		factsChanged = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DatalogReasonerFacade#evaluate(org.wsml.reasoner.ConjunctiveQuery,
	 *      java.lang.String)
	 */
	public synchronized Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q,
			String ontologyURI) throws ExternalToolException {
		// the ontologyURI is at the moment ignored, because at the moment
		// program only supports one instance per vm
		if (q == null) {
			throw new NullPointerException("The query must not be null");
		}

		// constructing the query
		final List<ILiteral> body = new ArrayList<ILiteral>(q.getLiterals()
				.size());
		// converting the literals of the query
		for (final Literal l : q.getLiterals()) {
			body.add(literal2Literal(l));
		}
		// creating the query
		final IQuery query = BASIC.createQuery(body);

		// update the executor, if there has been something changed
		if (rulesChanged) { // if there are new rules -> translate them all
			e = new Executor(p, new ExpressionEvaluator());
		}
		if (factsChanged || rulesChanged) { // if there are new facts or rules
			// -> compute the fixed point
			e.execute();
		}
		rulesChanged = false;
		factsChanged = false;

		// constructing the result set
		final Set<ITuple> result = e.computeSubstitution(query);

		final Set<Map<Variable, Term>> res = new HashSet<Map<Variable, Term>>();
		final List<IVariable> qVars = query.getQueryVariables();
		for (final ITuple t : result) {
			final Map<Variable, Term> prep = new HashMap<Variable, Term>();
			for (final IVariable v : qVars) {
				// convert the var to an wsml one
				final Variable wsmlVar = (Variable) irisTermConverter(v);

				// searching for the index of the term to extract from the tuple
				final int[] idx = searchQueryForVar(query, v);
				if (idx.length < 2) { // if the variable couldn't be found ->
					// exception
					throw new IllegalArgumentException(
							"Couldn't find the variable (" + v + ") in query (" + q
									+ ").");
				}
				prep.put(wsmlVar,irisTermConverter(getTermForTuple(t, idx)));
			}	
			res.add(prep);
		}
		
			// translating and adding the terms to the result set
			/*for (final ITuple t : result) {
				res.add(Collections.singletonMap(wsmlVar,
						irisTermConverter(getTermForTuple(t, idx))));
			}
			*/
		
		/*for (final IVariable v : qVars) {
			// convert the var to an wsml one
			final Variable wsmlVar = (Variable) irisTermConverter(v);

			// searching for the index of the term to extract from the tuple
			final int[] idx = searchQueryForVar(query, v);
			if (idx.length < 2) { // it the variable couldn't be found ->
				// exception
				throw new IllegalArgumentException(
						"Couldn't find the variable (" + v + ") in query (" + q
								+ ").");
			}
			// translating and adding the terms to the result set
			for (final ITuple t : result) {
				res.add(Collections.singletonMap(wsmlVar,
						irisTermConverter(getTermForTuple(t, idx))));
			}
			
			for (final ITuple t : result){
				res.add(Collections.singletonMap(wsmlVar,
						irisTermConverter(getTermForTuple(t, i))));
				}
		}*/

		// BEHAVIOR IMITATIED FROM THE KAON FACADE
		// if there are no variables in the query, fill it with as many empty
		// map objects as the result size
		if (query.getQueryVariables().isEmpty()) {
			for (int i = 0, max = result.size(); i < max; i++) {
				res.add(new HashMap<Variable, Term>());
			}
		}

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DatalogReasonerFacade#register(java.lang.String,
	 *      java.util.Set)
	 */
	public synchronized void register(String ontologyURI, Set<Rule> kb)
			throws ExternalToolException {
		// the ontologyURI is at the moment ignored, because at the moment
		// program only supports one instance per vm
		if (kb == null) {
			throw new NullPointerException("The knowlebe base must not be null");
		}

		// translating all the rules
		for (final Rule r : kb) {
			if (r.isFact()) { // the rule is a fact
				if (p.addFact(literal2Atom(r.getHead()))) {
					factsChanged = true;
				}
			} else { // the rule is an ordinary rule
				final List<ILiteral> body = new ArrayList<ILiteral>(r.getBody()
						.size());
				// converting the body of the rule
				for (final Literal l : r.getBody()) {
					body.add(literal2Literal(l));
				}
				if (p.addRule(BASIC.createRule(BASIC
						.createHead(literal2Literal(r.getHead())), BASIC
						.createBody(body)))) {
					rulesChanged = true;
				}
			}
		}
	}

	/**
	 * Converts a wsmo4j literal to an iris literal.
	 * 
	 * @param l
	 *            the wsmo4j literal to convert
	 * @return the iris literal
	 * @throws NullPointerException
	 *             if the literal is {@code null}
	 */
	static ILiteral literal2Literal(final Literal l) {
		if (l == null) {
			throw new NullPointerException("The literal must not be null");
		}

		return BASIC.createLiteral(l.isPositive(), literal2Atom(l));
	}

	/**
	 * Converts a wsmo4j literal to an iris atom. Watch out, the sighn (whether
	 * it is positive, or not) will be ignored.
	 * 
	 * @param l
	 *            the wsmo4j literal to convert
	 * @return the iris atom
	 * @throws NullPointerException
	 *             if the literal is {@code null}
	 */
	static IAtom literal2Atom(final Literal l) {
		if (l == null) {
			throw new NullPointerException("The literal must not be null");
		}

		final List<ITerm> terms = new ArrayList<ITerm>(l.getTerms().length);
		// converting the terms of the literal
		for (final Term t : l.getTerms()) {
			terms.add(wsmoTermConverter(t));
		}
		return BASIC.createAtom(BASIC.createPredicate(l.getPredicateUri(),
				terms.size()), BASIC.createTuple(terms));
	}

	/**
	 * Converts a wsmo4j term to an iris term
	 * 
	 * @param t
	 *            the wsmo4j term
	 * @return the converted iris term
	 * @throws NullPointerException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	static ITerm wsmoTermConverter(final Term t) {
		if (t == null) {
			throw new NullPointerException("The term must not be null");
		}
		if (t instanceof BuiltInConstructedTerm) {
			// TODO: builtins are left out at the moment
		} else if (t instanceof ConstructedTerm) {
			final ConstructedTerm ct = (ConstructedTerm) t;
			final List<ITerm> terms = new ArrayList<ITerm>(ct.getArity());
			for (final Term term : (List<Term>) ct.listParameters()) {
				terms.add(wsmoTermConverter(term));
			}
			return TERM.createConstruct(ct.getFunctionSymbol().toString(),
					terms);
		} else if (t instanceof DataValue) {
			return dataValueConverter((DataValue) t);
		} else if (t instanceof IRI) {
			return CONCRETE.createIri(t.toString());
		} else if (t instanceof Variable) {
			return TERM.createVariable(((Variable) t).getName());
		} else if (t instanceof Identifier) {
			// i doupt we got something analogous in iris -> exception
		} else if (t instanceof NumberedAnonymousID) {
			// i doupt we got something analogous in iris -> exception
		} else if (t instanceof UnnumberedAnonymousID) {
			// i doupt we got something analogous in iris -> exception
		}
		throw new IllegalArgumentException("Can't convert a term of type "
				+ t.getClass().getName());
	}

	/**
	 * Converts a wsmo4j DataValue to an iris ITerm.
	 * 
	 * @param v
	 *            the wsmo4j value to convert
	 * @return the correspoinding ITerm implementation
	 * @throws NullPointerException
	 *             if the value is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couln't be converted
	 * @throws IllegalArgumentException
	 *             if the value was a duration and the duration string couldn't
	 *             be parsed
	 */
	static ITerm dataValueConverter(final DataValue v) {
		if (v == null) {
			throw new NullPointerException("The data value must not be null");
		}
		final String t = v.getType().getIRI().toString();
		if (t.equals(WsmlDataType.WSML_BASE64BINARY)) {
			return CONCRETE.createBase64Binary(v.getValue().toString());
		} else if (t.equals(WsmlDataType.WSML_BOOLEAN)) {
			return CONCRETE.createBoolean(Boolean.valueOf(v.getValue()
					.toString()));
		} else if (t.equals(WsmlDataType.WSML_DATE)) {
			// TODO: a date in wsml has timezone values, too, which are ignored
			// here.
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createDate(getIntFromValue(cv, 0), getIntFromValue(
					cv, 1), getIntFromValue(cv, 2));
		} else if (t.equals(WsmlDataType.WSML_DATETIME)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createDateTime(getIntFromValue(cv, 0),
					getIntFromValue(cv, 1), getIntFromValue(cv, 2),
					getIntFromValue(cv, 3), getIntFromValue(cv, 4),
					getIntFromValue(cv, 5), getIntFromValue(cv, 6),
					getIntFromValue(cv, 7));
		} else if (t.equals(WsmlDataType.WSML_DECIMAL)) {
			return CONCRETE.createDecimal(Double.parseDouble(v.getValue()
					.toString()));
		} else if (t.equals(WsmlDataType.WSML_DOUBLE)) {
			return CONCRETE.createDouble(Double.parseDouble(v.getValue()
					.toString()));
		} else if (t.equals(WsmlDataType.WSML_DURATION)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			final Matcher m = DURATION_PATTERN.matcher(cv.getArgumentValue(
					(byte) 0).getValue().toString());
			if (!m.matches()) {
				throw new IllegalArgumentException(
						"The duration string got the wrong format");
			}
			return CONCRETE.createDuration(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)),
					Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)),
					Integer.parseInt(m.group(6)));
		} else if (t.equals(WsmlDataType.WSML_FLOAT)) {
			return CONCRETE.createFloat(Float.parseFloat(v.getValue()
					.toString()));
		} else if (t.equals(WsmlDataType.WSML_GDAY)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGDay(getIntFromValue(cv, 0));
		} else if (t.equals(WsmlDataType.WSML_GMONTH)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGMonth(getIntFromValue(cv, 0));
		} else if (t.equals(WsmlDataType.WSML_GMONTHDAY)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGMonthDay(getIntFromValue(cv, 0),
					getIntFromValue(cv, 1));
		} else if (t.equals(WsmlDataType.WSML_GYEAR)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGYear(getIntFromValue(cv, 0));
		} else if (t.equals(WsmlDataType.WSML_GYEARMONTH)) {
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createGYearMonth(getIntFromValue(cv, 0),
					getIntFromValue(cv, 1));
		} else if (t.equals(WsmlDataType.WSML_HEXBINARY)) {
			return CONCRETE.createHexBinary(v.getValue().toString());
		} else if (t.equals(WsmlDataType.WSML_INTEGER)) {
			return CONCRETE.createInteger(Integer.parseInt(v.toString()));
		} else if (t.equals(WsmlDataType.WSML_IRI)) {
			// never created by the DataFactory, but nevermind...
			return CONCRETE.createIri(v.getValue().toString());
		} else if (t.equals(WsmlDataType.WSML_SQNAME)) {
			// somehow they seem not to exist/be created in wsmo4j
			return CONCRETE.createSqName(v.getValue().toString());
		} else if (t.equals(WsmlDataType.WSML_STRING)) {
			return TERM.createString(v.toString());
		} else if (t.equals(WsmlDataType.WSML_TIME)) {
			// TODO: the ITime interface is not implemented at the moment
		}
		throw new IllegalArgumentException("Can't convert a value of type " + t);
	}

	/**
	 * Returns the integer value of a ComplexDataValue at a given position.
	 * 
	 * @param v
	 *            the complex data value from where to get the int
	 * @param pos
	 *            the index of the integer
	 * @return the extracted and converted integer
	 * @throws NullPointerException
	 *             if the value is {@code null}
	 * @throws IllegalArgumentException
	 *             if the pos is smaller than 0
	 */
	static private int getIntFromValue(final ComplexDataValue v, int pos) {
		if (v == null) {
			throw new NullPointerException("The value must not be null");
		}
		if (pos < 0) {
			throw new IllegalArgumentException(
					"The position must be greater than 0, but was " + pos);
		}
		return Integer.parseInt(v.getArgumentValue((byte) pos).getValue()
				.toString());
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
	Term irisTermConverter(final ITerm t) {
		if (t == null) {
			throw new NullPointerException("The term must not be null");
		}
		/*
		 * subinterfaces of IStringTerm have to be handeled before the
		 * IStringTerm block
		 */
		if (t instanceof IBase64Binary) {
			return DATA_FACTORY.createWsmlBase64Binary(((IBase64Binary) t)
					.getValue().getBytes());
		} else if (t instanceof IHexBinary) {
			return DATA_FACTORY.creatWsmlHexBinary(((String) ((IHexBinary) t)
					.getValue()).getBytes());
		} else if (t instanceof IIri) {
			return WSMO_FACTORY.createIRI(((IIri) t).getValue());
		} else if (t instanceof IStringTerm) {
			return DATA_FACTORY.createWsmlString((String) ((IStringTerm) t)
					.getValue());
		} else if (t instanceof IVariable) {
			return LOGIC_FACTORY.createVariable((String) ((IVariable) t)
					.getValue());
		} else if (t instanceof IConstructedTerm) {
			final IConstructedTerm ct = (IConstructedTerm) t;
			final List<Term> terms = new ArrayList<Term>(ct.getArity());
			for (final ITerm term : ct.getValue()) {
				terms.add(irisTermConverter(term));
			}
			return LOGIC_FACTORY.createConstructedTerm(WSMO_FACTORY
					.createIRI(ct.getFunctionSymbol()), terms);
		} else if (t instanceof IBooleanTerm) {
			return DATA_FACTORY.createWsmlBoolean((Boolean) ((IBooleanTerm) t)
					.getValue());
		} else if (t instanceof IDateTerm) {
			final IDateTerm dt = (IDateTerm) t;
			// TODO: the IDateTerm at the moment doesn't support timezone
			return DATA_FACTORY.createWsmlDate(dt.getYear(), dt.getMonth(), dt
					.getDay(), 0, 0);
		} else if (t instanceof IDateTime) {
			final IDateTime dt = (IDateTime) t;
			// TODO: the IDateTerm at the moment doesn't support timezone
			int[] tzData = getTZData(dt.getTimeZone());
			return DATA_FACTORY.createWsmlDateTime(dt.getYear(), dt.getMonth(),
					dt.getDay(), dt.getHour(), dt.getMinute(), dt.getSecond(),
					tzData[0], tzData[1]);
		} else if (t instanceof IDecimalTerm) {
			return DATA_FACTORY.createWsmlDecimal(new BigDecimal(
					((IDecimalTerm) t).toString()));
		} else if (t instanceof IDoubleTerm) {
			return DATA_FACTORY.createWsmlDouble((Double) ((IDoubleTerm) t)
					.getValue());
		} else if (t instanceof IDuration) {
			final IDuration dt = (IDuration) t;
			return DATA_FACTORY.createWsmlDuration(dt.getYear(), dt.getMonth(),
					dt.getDay(), dt.getHour(), dt.getMinute(), dt.getSecond());
		} else if (t instanceof IFloatTerm) {
			return DATA_FACTORY.createWsmlFloat((Float) ((IFloatTerm) t)
					.getValue());
		} else if (t instanceof IGDay) {
			return DATA_FACTORY.createWsmlGregorianDay(((IGDay) t).getDay());
		} else if (t instanceof IGMonth) {
			return DATA_FACTORY.createWsmlGregorianMonth(((IGMonth) t)
					.getMonth());
		} else if (t instanceof IGMonthDay) {
			final IGMonthDay md = (IGMonthDay) t;
			return DATA_FACTORY.createWsmlGregorianMonthDay(md.getMonth(), md
					.getDay());
		} else if (t instanceof IGYear) {
			return DATA_FACTORY.createWsmlGregorianYear(((IGYear) t).getYear());
		} else if (t instanceof IGYearMonth) {
			final IGYearMonth md = (IGYearMonth) t;
			return DATA_FACTORY.createWsmlGregorianYearMonth(md.getYear(), md
					.getMonth());
		} else if (t instanceof IIntegerTerm) {
			return DATA_FACTORY.createWsmlInteger(new BigInteger(t.getValue()
					.toString()));
		} else if (t instanceof ISqName) {
			// couldn't find this type in wsmo4j
		}
		throw new IllegalArgumentException("Can't convert a term of type "
				+ t.getClass().getName());
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
		return new int[] { (int) t.getRawOffset() / 3600000,
				(int) t.getRawOffset() % 3600000 / 60000 };
	}

	/**
	 * <p>
	 * Searches a query for the position of a given variable.
	 * </p>
	 * <p>
	 * The returned index describes the path through the query to the given
	 * variable. The first index is the literal and the second the term where
	 * this variable is located. If the term is a constructed term, there migth
	 * be further indexes describing the path through the constructed terms. An
	 * empty array indicates that the variable couldn't be found.
	 * </p>
	 * <p>
	 * An index always starts counting from 0
	 * </p>
	 * <p>
	 * E.g. a returned index of {@code [2, 4, 3]} tells, that the variable is in
	 * the third literal. There it is in the fifth term, which is a constructed
	 * one, and there it is the fourth argument.
	 * <p>
	 * 
	 * @param q
	 *            the query where to search
	 * @param v
	 *            the variable for which to look for
	 * @return the index array as described above
	 * @throws NullPointerException
	 *             if the query is {@code null}
	 * @throws NullPointerException
	 *             if the variable is {@code null}
	 */
	private static int[] searchQueryForVar(final IQuery q, final IVariable v) {
		if (q == null) {
			throw new NullPointerException("The query must not be null");
		}
		if (v == null) {
			throw new NullPointerException("Variable must not be null");
		}

		int pos = 0;
		for (final ILiteral l : q.getQueryLiterals()) {
			int tPos = 0;
			for (final ITerm t : l.getTuple().getTerms()) {
				if (t instanceof IConstructedTerm) {
					final int[] res = searchConstructForVar(
							(IConstructedTerm) t, v);
					if (res.length > 0) {
						int[] ret = new int[res.length + 2];
						ret[0] = pos;
						ret[1] = tPos;
						System.arraycopy(res, 0, ret, 2, res.length);
						return ret;
					}
				} else if (t.equals(v)) {
					return new int[] { pos, tPos };
				}
				tPos++;
			}
			pos++;
		}

		return new int[] {};
	}

	/**
	 * <p>
	 * Searches a constructed term for the position of a given variable.
	 * </p>
	 * <p>
	 * For a explanation how the index is constructed, look at the
	 * {@link #searchQueryForVar(IQuery, IVariable)} documentation.
	 * </p>
	 * 
	 * @param c
	 *            the constructed ther where to search through
	 * @param v
	 *            the variable for which to look for
	 * @return the index describing where to find the variable
	 * @throws NullPointerException
	 *             if the constructed term is {@code null}
	 * @throws NullPointerException
	 *             if the variable is {@code null}
	 * @see #searchQueryForVar(IQuery, IVariable)
	 */
	private static int[] searchConstructForVar(final IConstructedTerm c,
			final IVariable v) {
		if (c == null) {
			throw new NullPointerException(
					"The constructed term must not be null");
		}
		if (v == null) {
			throw new NullPointerException("Variable must not be null");
		}

		int pos = 0;
		for (final ITerm t : c.getParameters()) {
			if (t instanceof IConstructedTerm) {
				final int[] res = searchConstructForVar((IConstructedTerm) t, v);
				if (res.length > 0) {
					int[] ret = new int[res.length + 1];
					ret[0] = pos;
					System.arraycopy(res, 0, ret, 1, res.length);
					return ret;
				}
			} else if (t.equals(v)) {
				return new int[] { pos };
			}
			pos++;
		}

		return new int[] {};
	}

	/**
	 * <p>
	 * Retrieves the term of a tuple at a given index.
	 * </p>
	 * <p>
	 * For a explanation how the index is constructed, look at the
	 * {@link #searchQueryForVar(IQuery, IVariable)} documentation.
	 * </p>
	 * 
	 * @param t
	 *            the tuple from where to extract the term
	 * @param i
	 *            the index where to find the term
	 * @return the extracted term
	 * @throws NullPointerException
	 *             if the tuple is {@code null}
	 * @throws NullPointerException
	 *             if the index is {@code null}
	 * @see #searchQueryForVar(IQuery, IVariable)
	 */
	private static ITerm getTermForTuple(final ITuple t, final int[] i) {
		if (t == null) {
			throw new NullPointerException("The tuple must not be null");
		}
		if (i == null) {
			throw new NullPointerException("The index must not be null");
		}

		// TODO: the first index will be ignored, because in iris only queries
		// with one literal are allowed at the moment, and so only one tuple
		// will be returned.

		final ITerm term = t.getTerm(i[1]);
		if (term instanceof IConstructedTerm) {
			return getTermFromConstruct((IConstructedTerm) term, i, 2);
		}
		return term;
	}

	/**
	 * <p>
	 * Retrieves the term of a constructed term at a given index.
	 * </p>
	 * <p>
	 * For a explanation how the index is constructed, look at the
	 * {@link #searchQueryForVar(IQuery, IVariable)} documentation.
	 * </p>
	 * 
	 * @param t
	 *            the constructed term from where to extract the term
	 * @param i
	 *            the index where to find the term
	 * @param cur
	 *            the current possition in the index which should be handeled
	 *            now.
	 * @return the extracted term
	 * @throws NullPointerException
	 *             if the tuple is {@code null}
	 * @throws NullPointerException
	 *             if the index is {@code null}
	 * @see #searchQueryForVar(IQuery, IVariable)
	 */
	private static ITerm getTermFromConstruct(final IConstructedTerm c,
			final int[] i, final int cur) {
		if (c == null) {
			throw new NullPointerException(
					"The constructed term must not be null");
		}
		if (i == null) {
			throw new NullPointerException("The index must not be null");
		}

		final ITerm t = c.getParameter(i[cur]);

		if (t instanceof IConstructedTerm) {
			if (i.length == cur + 1) {
				throw new IllegalArgumentException(
						"We got a constructed term, but no further indexes.");
			}
			return getTermFromConstruct((IConstructedTerm) t, i, cur + 1);
		}

		// TODO: thorw exception?
		assert i.length == cur + 1 : "We got a non-constructed term, but further inedes";

		return t;
	}
}