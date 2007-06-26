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

// TODO: do the builtins

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.BUILTIN;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.PROGRAM;
import static org.deri.iris.factory.Factory.TERM;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
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
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
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
import org.omwg.logicalexpression.Constants;
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
 * $Id: IrisFacade.java,v 1.17 2007-06-26 17:03:36 nathalie Exp $
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.17 $
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

	/** Map storing all the uri <-> program mappings. */
	private final Map<String, IProgram> progs = new HashMap<String, IProgram>();

	/** 
	 * Map to determine whether a rule for a conjunctive query in a program was 
	 * already created. The first map stores the ontologyId &gt;-&lt; conjunctive 
	 * query mappings.  The second map is build as follows: 
	 * <ul>
	 * <li>key = the <code>ConjunctiveQuery</code> object which was substituted</li>
	 * <li>value = the literal uesd to substitute the query</li>
	 * </ul>
	 */
	private Map<String, Map<ConjunctiveQuery, IQuery>> conjunktiveQueries = 
		new HashMap<String, Map<ConjunctiveQuery, IQuery>>();
	
	/**
	 * Records whether the ontology changed since the last calculation of the
	 * fixed point.
	 * <ul>
	 * <li>key = ontologyId</li>
	 * <li>value = <code>true</code> if changed, otherwise <code>false</code></li>
	 * </ul>
	 */
	private Map<String, Boolean> factsChanged = new HashMap<String, Boolean>();

	/**
	 * Records whether the ontology changed since the last calculation of the
	 * fixed point.
	 * <ul>
	 * <li>key = ontologyId</li>
	 * <li>value = <code>true</code> if changed, otherwise <code>false</code></li>
	 * </ul>
	 */
	private Map<String, Boolean> rulesChanged = new HashMap<String, Boolean>();

	/**
	 * Excutor to execute the queries.
	 * <ul>
	 * <li>key = ontologyId</li>
	 * <li>value = the executor</li>
	 * </ul>
	 */
	private final Map<String, IExecutor> executor = new HashMap<String, IExecutor>();

	public IrisFacade(final WSMO4JManager m) {
		DATA_FACTORY = m.getDataFactory();
		WSMO_FACTORY = m.getWSMOFactory();
		LOGIC_FACTORY = m.getLogicalExpressionFactory();
	}

	public IrisFacade() {
		this(new WSMO4JManager());
	}

	public synchronized void deregister(String ontologyURI)
			throws ExternalToolException {
		((IProgram) progs.get(ontologyURI)).resetProgram(); 
		progs.remove(ontologyURI);
		executor.remove(ontologyURI);
		conjunktiveQueries.remove(ontologyURI);
		rulesChanged.remove(ontologyURI);
		factsChanged.remove(ontologyURI);
	}

	public synchronized Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q,
			String ontologyURI) throws ExternalToolException {
		if (ontologyURI == null) {
			throw new NullPointerException("The ontology uri must not be null");
		}
		if (q == null) {
			throw new NullPointerException("The query must not be null");
		}
		if (!progs.containsKey(ontologyURI)) {
			throw new IllegalArgumentException("A program with the uri '" + 
					ontologyURI + "' has not been registered, yet.");
		}

		// constructing the query
		final List<ILiteral> body = new ArrayList<ILiteral>(q.getLiterals()
				.size());
		// converting the literals of the query
		for (final Literal l : q.getLiterals()) {
			body.add(literal2Literal(l));
		}
		
		// creating the query
		final IQuery query;
		if (body.size() > 1) { // we got an conjunctive query -> replace it
			IQuery conjQ = conjunktiveQueries.get(ontologyURI).get(q);
			if (conjQ == null) { // this query was never replaced before
				// getting all variables
				final Set<IVariable> vars = new HashSet<IVariable>();
				for (final ILiteral l : body) {
					vars.addAll(l.getTuple().getAllVariables());
				}
				// creating the new predicate and literal
				final ILiteral conjL = BASIC.createLiteral(true, 
						BASIC.createPredicate("_replacement_" + q.hashCode(), 
								vars.size()), 
						BASIC.createTuple(new ArrayList<ITerm>(vars)));
				// creating and adding the new rule
				progs.get(ontologyURI).addRule(BASIC.createRule(
						BASIC.createHead(conjL), BASIC.createBody(body)));
				rulesChanged.put(ontologyURI, true);
				// creating and adding the query
				conjQ = BASIC.createQuery(conjL);
				conjunktiveQueries.get(ontologyURI).put(q, conjQ);
				
			}
			query = conjQ;
		} else { // this is a normal query
			 query = BASIC.createQuery(body);
		}

		if (rulesChanged.get(ontologyURI)) { 
			// if there are new rules -> translate them all
			executor.put(ontologyURI, new Executor(
					progs.get(ontologyURI), new ExpressionEvaluator()));
		}
		if (factsChanged.get(ontologyURI) || rulesChanged.get(ontologyURI)) { 
			// if there are new facts or rules -> compute the fixed point
			executor.get(ontologyURI).execute();
		}
		rulesChanged.put(ontologyURI, false);
		factsChanged.put(ontologyURI, false);

		// constructing the result set
		final Set<ITuple> result = executor.get(ontologyURI).computeSubstitution(query);
		final Set<Map<Variable, Term>> res = new HashSet<Map<Variable, Term>>();
		final Map<IVariable, Integer> positions = determineVarPositions(query);
		for (final ITuple t : result) {
			final Map<Variable, Term> tmp = new HashMap<Variable, Term>();
			for (final Map.Entry<IVariable, Integer> e : positions.entrySet()) {
				tmp.put((Variable) irisTermConverter(e.getKey()), 
						irisTermConverter(t.getTerm(e.getValue())));
			}
			res.add(tmp);
		}

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
	
	/**
	 * Constructs a map at which indexes (columns (starting from 0)) to find the 
	 * subsitutions for a given variable in the returned relation of the executor. 
	 * @param q the query from where to gather the variables
	 * @return the map holding the variable -&gt; index mappings
	 * @throws NullPointerException if the query is <code>null</code>
	 */
	private static Map<IVariable, Integer> determineVarPositions(final IQuery q) {
		if (q == null) {
			throw new NullPointerException("The query must not be null");
		}
		final Map<IVariable, Integer> res = new HashMap<IVariable, Integer>();
		for (final IVariable v : q.getQueryVariables()) {
			res.put(v, termPos(q, v));
		}
		if (res.isEmpty()) {
			return Collections.EMPTY_MAP;
		}
		// normalize the positions, so that they start from 0 and are in sequence
		int j = 0;
		for (int i = Collections.min(res.values()), max = Collections.max(res.values()); i <= max; i++) {
			if (res.values().contains(i)) {
				for (final Map.Entry<IVariable, Integer> e : res.entrySet()) {
					if (e.getValue() == i) {
						res.put(e.getKey(), j++);
						break;
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * Searches a given query for the first occurence of a given term. Constructed 
	 * terms don't count in the index, but the terms inside of an constructed 
	 * term are.
	 * @param stack the query where to search
	 * @param needle the term for which to look for
	 * @return the index of <code>needle</code> in <code>stack</code>
	 * @throws NullPointerException if the stack is <code>null</code>
	 * @throws NullPointerException if the needle is <code>null</code>
	 */
	private static int termPos(final IQuery stack, final ITerm needle) {
		if (stack == null) {
			throw new NullPointerException("The stack must not be null");
		}
		if (needle == null) {
			throw new NullPointerException("The needle must not be null");
		}
		int pos = 0;
		for (final ILiteral l : stack.getQueryLiterals()) {
			for (final ITerm t : l.getTuple().getTerms()) {
				if (needle.equals(t)) {
					return pos;
				} else if (t instanceof IConstructedTerm) {
					final int[] res = termPos((IConstructedTerm) t, needle);
					if (res[0] == -1) { // not found
						pos += res[1];
					} else { // found
						return pos + res[1];
					}
				} else { 
					pos++;
				}
			}
		}
		return -1;
	}
	
	/**
	 * <p>
	 * Searches a constructed term for a given term. This method does it's search
	 * recursively.
	 * </p>
	 * <p>
	 * The returned array consists of two fields: the position of the found term 
	 * is written to index 0. it is -1 if the term couldn't be found. The index 1
	 * only has another value than -1 if index 0 is -1. At index 1 there is written
	 * how many terms are contained in this constructed term and it's sub terms.
	 * </p>
	 * @param stack the constructed term where to search
	 * @param needle the term for which to look for
	 * @return the position array
	 * @throws NullPointerException if the stack is <code>null</code>
	 * @throws NullPointerException if the needle is <code>null</code>
	 * @see #termPos(IQuery, ITerm)
	 */
	private static int[] termPos(final IConstructedTerm stack, final ITerm needle) {
		if (stack == null) {
			throw new NullPointerException("The stack must not be null");
		}
		if (needle == null) {
			throw new NullPointerException("The needle must not be null");
		}
		int pos = 0;
		for (final ITerm t : stack.getParameters()) {
			if (needle.equals(t)) {
					return new int[]{pos, -1};
			} else if (t instanceof IConstructedTerm) {
				final int[] res = termPos((IConstructedTerm) t, needle);
				if (res[0] == -1) { // not found
					pos += res[1];
				} else { // found
					res[0] += pos;
					return res;
				}
			} else {
				pos++;
			}
		}
		return new int[]{-1, pos};
	}

	public synchronized void register(String ontologyURI, Set<Rule> kb)
			throws ExternalToolException {
		if (ontologyURI == null) {
			throw new NullPointerException("The ontology uri must not be null");
		}
		if (kb == null) {
			throw new NullPointerException("The knowlebe base must not be null");
		}
		if (progs.containsKey(ontologyURI)) {
			deregister(ontologyURI);
		}
		
		final IProgram p = PROGRAM.createProgram();
		// translating all the rules
		for (final Rule r : kb) {
			if (r.isFact()) { // the rule is a fact
				p.addFact(literal2Atom(r.getHead()));
			} else { // the rule is an ordinary rule
				final List<ILiteral> body = new ArrayList<ILiteral>(r.getBody()
						.size());
				// converting the body of the rule
				for (final Literal l : r.getBody()) {
					body.add(literal2Literal(l));
				}
				p.addRule(BASIC.createRule(
						BASIC.createHead(literal2Literal(r.getHead())),
						BASIC.createBody(body)));
			}
		}
		
		this.factsChanged.put(ontologyURI, true);
		this.rulesChanged.put(ontologyURI, true);
		this.progs.put(ontologyURI, p);
		this.executor.put(ontologyURI, new Executor(p, new ExpressionEvaluator()));
		this.conjunktiveQueries.put(ontologyURI, new HashMap<ConjunctiveQuery, IQuery>());
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

		final String sym = l.getPredicateUri();
		// checking whether the predicate is a builtin
		if (sym.equals(Constants.EQUAL) || 
				sym.equals(Constants.NUMERIC_EQUAL) || 
				sym.equals(Constants.STRING_EQUAL) || 
				sym.equals(Constants.STRONG_EQUAL)) {
			return BUILTIN.createEqual(terms.get(0), terms.get(1));

		} else if (sym.equals(Constants.INEQUAL) || 
				sym.equals(Constants.NUMERIC_INEQUAL) || 
				sym.equals(Constants.STRING_INEQUAL)) {
			return BUILTIN.createUnequal(terms.get(0), terms.get(1));

		} else if (sym.equals(Constants.LESS_THAN)) {
			return BUILTIN.createLess(terms.get(0), terms.get(1));

		} else if (sym.equals(Constants.LESS_EQUAL)) {
			return BUILTIN.createLessEqual(terms.get(0), terms.get(1));

		} else if (sym.equals(Constants.GREATER_THAN)) {
			return BUILTIN.createGreater(terms.get(0), terms.get(1));

		} else if (sym.equals(Constants.GREATER_EQUAL)) {
			return BUILTIN.createGreaterEqual(terms.get(0), terms.get(1));

		} else if (sym.equals(Constants.NUMERIC_ADD)) {
			return BUILTIN.createAddBuiltin(terms.get(1), terms.get(2), terms.get(0));

		} else if (sym.equals(Constants.NUMERIC_SUB)) {
			return BUILTIN.createSubtractBuiltin(terms.get(1), terms.get(2), terms.get(0));

		} else if (sym.equals(Constants.NUMERIC_MUL)) {
			return BUILTIN.createMultiplyBuiltin(terms.get(1), terms.get(2), terms.get(0));

		} else if (sym.equals(Constants.NUMERIC_DIV)) {
			return BUILTIN.createDivideBuiltin(terms.get(1), terms.get(2), terms.get(0));
		}
		// return an ordinary atom
		return BASIC.createAtom(BASIC.createPredicate(sym, 
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
	 * Method to get a structured representation of a program.
	 * @param p the program
	 * @return the string representation
	 */
	private static String toString(final IProgram p) {
		if (p == null) {
			throw new NullPointerException("The program must not be null");
		}
		final String NL = System.getProperty("line.separator");
		final StringBuilder buffer = new StringBuilder();
		buffer.append("rules:").append(NL);
		for (final IRule r : p.getRules()) {
			buffer.append("\t").append(r).append(NL);
		}
		buffer.append("facts:").append(NL);
		for (final IPredicate pred : p.getPredicates()) {
			buffer.append("\t").append(pred).append(":").append(NL);
			for (final ITuple t : p.getFacts(pred)) {
				buffer.append("\t\t").append(t).append(NL);
			}
		}
		buffer.append("queries:").append(NL);
		for (final IQuery q : p.getQueries()) {
			buffer.append("\t").append(q).append(NL);
		}
		return buffer.toString();
	}
}
