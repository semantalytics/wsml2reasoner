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

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.BUILTIN;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
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
import org.deri.iris.builtins.IsBooleanBuiltin;
import org.deri.iris.builtins.IsDateBuiltin;
import org.deri.iris.builtins.IsDateTimeBuiltin;
import org.deri.iris.builtins.IsDecimalBuiltin;
import org.deri.iris.builtins.IsIntegerBuiltin;
import org.deri.iris.builtins.IsStringBuiltin;
import org.deri.iris.facts.IDataSource;
import org.deri.iris.querycontainment.QueryContainment;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;
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
import org.wsml.reasoner.WSML2DatalogTransformer;
import org.wsml.reasoner.api.ExternalDataSource;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.ExternalDataSource.HasValue;
import org.wsml.reasoner.api.ExternalDataSource.MemberOf;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.common.WSML;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * <p>
 * The wsmo4j interface for the iris reasoner.
 * </p>
 * <p>
 * $Id: IrisFacade.java,v 1.24 2007-11-13 17:36:11 nathalie Exp $
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.24 $
 */
public class IrisFacade implements DatalogReasonerFacade {

	/** 
	 * This is the key value this facade will look for to get the external 
	 * data source. The value for this Map.Entry should be a map containing 
	 * <code>ontologyUri(String)->Collection&lt;ExternalDatasource&gt;</code>.
	 */
	public static final String EXTERNAL_DATA_SOURCE = "iris.external.source";

	/** Factory to create the DataValues. */
	private final DataFactory DATA_FACTORY;

	/** Factory to create the DataValues. */
	private final LogicalExpressionFactory LOGIC_FACTORY;

	/** Factory to create the wsmo objects. */
	private final WsmoFactory WSMO_FACTORY;

	/** Pattern to convert a wsmo duration to an iris one. */
	private static final Pattern DURATION_PATTERN = Pattern
			.compile("P(\\d{4})Y(\\d{1,2})M(\\d{1,2})DT(\\d{1,2})H(\\d{1,2})M(\\d{1,2})S");

	/** knowledge-base. */
	private org.deri.iris.api.IKnowledgeBase prog;

	private QueryContainment queryCont = null;
	

	/** Map that contains the variable mapping from the query containment check. */
	private org.deri.iris.storage.IRelation QCResult = new SimpleRelationFactory().createRelation();
	
	private final String wsmlVariant;
	
	/**
	 * The external data sources.
	 */
	private final Collection<ExternalDataSource> sources;

	public IrisFacade(final WSMO4JManager m, final Map<String, Object> config) {
		DATA_FACTORY = m.getDataFactory();
		WSMO_FACTORY = m.getWSMOFactory();
		LOGIC_FACTORY = m.getLogicalExpressionFactory();

		String variant = null;
    	if( config != null )
    		variant = (String) config.get( WSMLReasonerFactory.PARAM_WSML_VARIANT );

    	wsmlVariant = variant == null ? WSML.WSML_FLIGHT : variant;

		// retrieving the data source
		final Object ds = (config != null) ? config.get(EXTERNAL_DATA_SOURCE) : null;
		if ((ds != null) && (ds instanceof Collection)) {
			sources = (Collection<ExternalDataSource>) ds;
		} else {
			sources = Collections.EMPTY_LIST;
		}
	}

	public IrisFacade() {
		this(new WSMO4JManager(), null);
	}

	public synchronized void deregister() throws ExternalToolException {
		prog = null;
	}

	public synchronized Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q) throws ExternalToolException {
		if (q == null) {
			throw new NullPointerException("The query must not be null");
		}
		if (prog == null) {
			throw new InternalReasonerException("A program has not been registered");
		}

		// constructing the query -- i.e. rule with no head
		final List<ILiteral> body = new ArrayList<ILiteral>(q.getLiterals().size());
		// converting the literals of the query
		for (final Literal l : q.getLiterals()) {
			body.add(literal2Literal(l));
		}
		
		// create query
		final IQuery query = BASIC.createQuery(body);
		
		org.deri.iris.storage.IRelation executionResult;
		List<IVariable> variableBindings = new ArrayList<IVariable>();
		try {
			executionResult = prog.execute(query, variableBindings);
		} catch (EvaluationException e2) {
			throw new ExternalToolException(e2.getMessage());
		}

		final Set<Map<Variable, Term>> res = new HashSet<Map<Variable, Term>>();
		
		for (int i =0; i< executionResult.size(); ++i)
		{
			ITuple t = executionResult.get(i);
			
			assert t.size() == variableBindings.size();

			Map<Variable, Term> tmp = new HashMap<Variable, Term>();
			
			for( int pos = 0; pos < t.size(); ++pos )
			{
				IVariable variable = variableBindings.get( pos );
				ITerm term = t.get( pos );

				tmp.put( (Variable) irisTermConverter(variable), irisTermConverter(term));
			}
			
			res.add(tmp);
		}
		
		return res;
	}
	
	public synchronized boolean checkQueryContainment(ConjunctiveQuery query1, ConjunctiveQuery query2) {
		if (query1 == null || query2 == null) {
			throw new NullPointerException("The queries must not be null");
		}
		if (prog == null) {
			throw new InternalReasonerException("A program has not been registered");
		}

		// constructing query 1, the query to be frozen in IRIS 
		final List<ILiteral> body = new ArrayList<ILiteral>(
				query1.getLiterals().size());
		
		// converting the literals of the query
		for (final Literal l : query1.getLiterals()) {
			body.add(literal2Literal(l));
		}
		
		final IQuery iQuery1 = BASIC.createQuery(body);
		
		// constructing query 2 
		//final List<ILiteral> head2 = new ArrayList<ILiteral>(1);
		final List<ILiteral> body2 = new ArrayList<ILiteral>(
				query2.getLiterals().size());
		
		// converting the literals of the query
		for (final Literal l : query2.getLiterals()) {
			body2.add(literal2Literal(l));
		}
		
		// creating the query
		final IQuery iQuery2 = BASIC.createQuery(body2);

		// doing the query containment check
		queryCont = new QueryContainment(prog);
		
//		System.out.println("prog: " + prog);
//		System.out.println("query1: " + query1);
//		System.out.println("query2 : " + query2);
		boolean check = false;
		try {
			check = queryCont.checkQueryContainment(iQuery1, iQuery2);
		} catch (Exception e) {
			new ExternalToolException(e.getMessage());
		}

		return check;
	}
	
	private List<IVariable> extractUniqueVariables( IQuery query )
	{
		List<IVariable> result = new ArrayList<IVariable>();
		
		for( ILiteral literal : query.getLiterals() )
		{
			for( ITerm term : literal.getAtom().getTuple() )
			{
				if( term instanceof IVariable )
				{
					IVariable variable = (IVariable) term;
					
					if( ! result.contains( variable ) )
						result.add( variable );
				}
			}
		}
		
		return result;
	}
	
	public synchronized Set<Map<Variable, Term>> getQueryContainment(
			ConjunctiveQuery query1, ConjunctiveQuery query2) 
			throws ExternalToolException {
		
		// check query containment and get IRIS result set
		if (checkQueryContainment(query1, query2)) {
			QCResult = queryCont.getContainmentMappings();
		
			// constructing the result set to return
			final Set<Map<Variable, Term>> result = new HashSet<Map<Variable, Term>>();
		
			// getting the variables from query2 in the execution order
			List<IVariable> variableBindings = queryCont.getVariableBindings();
		
			assert QCResult.size() > 0;
			assert variableBindings.size() == QCResult.get(0).size();
		
			for (int i =0; i< QCResult.size(); i++){
				ITuple t = QCResult.get(i);
				final Map<Variable, Term> tmp = new HashMap<Variable, Term>();
				for( int pos = 0; pos < t.size(); pos++){
					tmp.put( (Variable)irisTermConverter(variableBindings.get(pos)), irisTermConverter(t.get(pos)));
				}
				result.add(tmp);
			}	
			QCResult = new SimpleRelationFactory().createRelation();
			return result;
		}
		return new HashSet<Map<Variable, Term>>();
	}

	
	public synchronized void register(Set<Rule> kb) throws ExternalToolException {
		if (kb == null) {
			throw new NullPointerException("The knowlebe base must not be null");
		}
		if (prog != null) {
			deregister();
		}
		
		Map<IPredicate, org.deri.iris.storage.IRelation> facts = new HashMap<IPredicate, org.deri.iris.storage.IRelation>();
		List<IRule> rules = new ArrayList<IRule>();
		final Configuration configuration = org.deri.iris.KnowledgeBaseFactory.getDefaultConfiguration();
		
		// translating all the rules
		for (final Rule r : kb) {
			if (r.isFact()) { // the rule is a fact
				IAtom atom = literal2Atom(r.getHead());
				IPredicate pred = atom.getPredicate();

				org.deri.iris.storage.IRelation relation = facts.get(atom.getPredicate() );
				if( relation == null ){
					relation = new org.deri.iris.storage.simple.SimpleRelationFactory().createRelation();
					facts.put( pred, relation );
				}
				relation.add(atom.getTuple());
			} else { // the rule is an ordinary rule
				final List<ILiteral> head = new ArrayList<ILiteral>(1);
				final List<ILiteral> body = new ArrayList<ILiteral>(r.getBody()
						.size());
				// converting the head of the rule
				head.add(literal2Literal(r.getHead()));
				// converting the body of the rule
				for (final Literal l : r.getBody()) {
					body.add(literal2Literal(l));
				}
				rules.add(BASIC.createRule(head, body));
			}
		}
		// add the wsml-member-of rules
		for (final IRule r : getWsmlMemberOfRules()) {
			rules.add(r);
		}
		// add the data sources
		for (final ExternalDataSource ext : sources) {
			configuration.externalDataSources.add(new IrisDataSource(ext));
		}
		
		try {
			// If reasoning with WSML-Rule, we need unsafe rule support and well-founded semantics
			if( wsmlVariant.equals( WSML.WSML_RULE ) )
			{
				configuration.ruleSafetyProcessor = new org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor();
				configuration.evaluationStrategyFactory = new org.deri.iris.evaluation.wellfounded.WellFoundedEvaluationStrategyFactory();
			}
			
			prog = org.deri.iris.KnowledgeBaseFactory.createKnowledgeBase( facts, rules, configuration );
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException( e.getMessage() );
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
					(int)Double.parseDouble( getFieldValue( cv, 5) ),
					getIntFromValue(cv, 6),
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
			final ComplexDataValue cv = (ComplexDataValue) v;
			return CONCRETE.createTime(
					getIntFromValue(cv, 0), getIntFromValue(cv, 1),
					(int)Double.parseDouble( getFieldValue( cv, 2) ), // seconds
					getIntFromValue(cv, 3),
					getIntFromValue(cv, 4));
		}
		throw new IllegalArgumentException("Can't convert a value of type " + t);
	}

	/**
	 * Returns the integer value of a ComplexDataValue at a given position.
	 * 
	 * @param value the complex data value from where to get the int
	 * @param pos the index of the integer
	 * @return the extracted and converted integer
	 */
	static private int getIntFromValue(final ComplexDataValue value, int pos) {
		assert value != null;
		assert pos >= 0;

		return Integer.parseInt( getFieldValue( value, pos ) );
	}

	/**
	 * Get a field of a complex value.
	 * @param value The complex value
	 * @param pos The position of the file (zero-based index)
	 * @return The string-ised field value.
	 */
	static private String getFieldValue(final ComplexDataValue value, int pos) {
		assert value != null;
		assert pos >= 0;

		return value.getArgumentValue((byte) pos).getValue().toString();
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
			final List<Term> terms = new ArrayList<Term>(ct.getValue().size());
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
			return DATA_FACTORY.createWsmlDuration(0, 0,
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
	 * Returns the rules for the wsml-member-of rules.
	 * @return the wsml-member-of rules
	 */
	private static Set<IRule> getWsmlMemberOfRules() {
		final Set<IRule> res = new HashSet<IRule>();
		final IPredicate WSML_MEBER_OF = 
			BASIC.createPredicate(WSML2DatalogTransformer.PRED_MEMBER_OF, 2);
		final IVariable X = TERM.createVariable("X");
		final IVariable Y = TERM.createVariable("Y");
		final IVariable Z = TERM.createVariable("Z");
		final ILiteral hasValue = 
			BASIC.createLiteral(true, 
					BASIC.createPredicate(WSML2DatalogTransformer.PRED_HAS_VALUE, 3), 
					BASIC.createTuple(Y, Z, X));
		final List<ILiteral> body = new ArrayList<ILiteral>();
		final List<ILiteral> head = new ArrayList<ILiteral>();
		// rules for member of string
		head.add(BASIC.createLiteral(true, WSML_MEBER_OF, 
				BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_STRING))));
		body.add(hasValue);
		body.add(BASIC.createLiteral(true, new IsStringBuiltin(X)));
		res.add(BASIC.createRule(head, body));
		head.clear();
		body.clear();
		// rules for member of integer
		head.add(BASIC.createLiteral(true, WSML_MEBER_OF, 
				BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_INTEGER))));
		body.add(hasValue);
		body.add(BASIC.createLiteral(true, new IsIntegerBuiltin(X)));
		res.add(BASIC.createRule(head, body));
		head.clear();
		body.clear();
		// rules for member of decimal
		head.add(BASIC.createLiteral(true, WSML_MEBER_OF, 
				BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_DECIMAL))));
		body.add(hasValue);
		body.add(BASIC.createLiteral(true, new IsDecimalBuiltin(X)));
		res.add(BASIC.createRule(head, body));
		head.clear();
		body.clear();
		// rules for member of boolean
		head.add(BASIC.createLiteral(true, WSML_MEBER_OF, 
				BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_BOOLEAN))));
		body.add(hasValue);
		body.add(BASIC.createLiteral(true, new IsBooleanBuiltin(X)));
		res.add(BASIC.createRule(head, body));
		head.clear();
		body.clear();
		// rules for member of date
		head.add(BASIC.createLiteral(true, WSML_MEBER_OF, 
				BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_DATE))));
		body.add(hasValue);
		body.add(BASIC.createLiteral(true, new IsDateBuiltin(X)));
		res.add(BASIC.createRule(head, body));
		head.clear();
		body.clear();
		// rules for member of dateTime
		head.add(BASIC.createLiteral(true, WSML_MEBER_OF, 
				BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_DATETIME))));
		body.add(hasValue);
		body.add(BASIC.createLiteral(true, new IsDateTimeBuiltin(X)));
		res.add(BASIC.createRule(head, body));
		head.clear();
		body.clear();
		return res;
	}

	/**
	 * Wrapper for the w2r datasource to the iris datasource.
	 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
	 */
	private class IrisDataSource implements IDataSource {

		/** Predicate for the iris memeber-of facts. */
		private final IPredicate memberOf = 
			BASIC.createPredicate(WSML2DatalogTransformer.PRED_MEMBER_OF, 2);

		/** Predicate for the iris has-value facts. */
		private final IPredicate hasValue = 
			BASIC.createPredicate(WSML2DatalogTransformer.PRED_HAS_VALUE, 3);

		/** Datasource from where to get the values from. */
		private final ExternalDataSource source;

		public IrisDataSource(final ExternalDataSource source) {
			if (source == null) {
				throw new IllegalArgumentException("The source must not be null");
			}
			this.source = source;
		}

		public void get(IPredicate p, ITuple from, ITuple to, IRelation r) {
			// TODO: from and to can't be used by iris atm, so we leave it out for the moment
			if (p == null) {
				throw new IllegalArgumentException("The predicate must not be null");
			}
			if (r == null) {
				throw new IllegalArgumentException("The relation must not be null");
			}

			if (p.equals(memberOf)) {
				for (final MemberOf mo : source.memberOf(null, null)) {
					r.add(BASIC.createTuple(wsmoTermConverter(mo.getId()), 
							wsmoTermConverter(mo.getConcept())));
				}
			} else if (p.equals(hasValue)) {
				for (final HasValue hv : source.hasValue(null, null, null)) {
					r.add(BASIC.createTuple(wsmoTermConverter(hv.getId()), 
							wsmoTermConverter(hv.getName()), 
							wsmoTermConverter(hv.getValue())));
				}
			}
		}
	}
}
