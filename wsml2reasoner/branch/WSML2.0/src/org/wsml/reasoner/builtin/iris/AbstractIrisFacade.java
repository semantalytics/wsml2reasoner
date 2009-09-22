/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
import org.deri.iris.api.terms.concrete.IIri;
import org.deri.iris.api.terms.concrete.ISqName;
import org.deri.iris.api.terms.concrete.IText;
import org.deri.iris.api.terms.concrete.ITime;
import org.deri.iris.api.terms.concrete.IXMLLiteral;
import org.deri.iris.api.terms.concrete.IYearMonthDuration;
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
import org.deri.iris.builtins.datatype.IsStringBuiltin;
import org.deri.iris.builtins.datatype.IsTextBuiltin;
import org.deri.iris.builtins.datatype.IsTimeBuiltin;
import org.deri.iris.builtins.datatype.IsXMLLiteralBuiltin;
import org.deri.iris.builtins.datatype.IsYearMonthDurationBuiltin;
import org.deri.iris.facts.IDataSource;
import org.deri.iris.querycontainment.QueryContainment;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import org.omwg.logicalexpression.Constants;
import org.omwg.logicalexpression.terms.BuiltInConstructedTerm;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.RDFDataType;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.omwg.ontology.XmlSchemaDataType;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DatalogReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.WSML2DatalogTransformer;
import org.wsml.reasoner.api.data.ExternalDataSource;
import org.wsml.reasoner.api.data.ExternalDataSource.HasValue;
import org.wsml.reasoner.api.data.ExternalDataSource.MemberOf;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsmo.common.BuiltIn;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.common.WSML;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * The base class for all facades based on the iris reasoner.
 */
public abstract class AbstractIrisFacade implements DatalogReasonerFacade {

    /**
     * This is the key value this facade will look for to get the external data
     * source. The value for this Map.Entry should be a map containing
     * <code>ontologyUri(String)->Collection&lt;ExternalDatasource&gt;</code>.
     */
    public static final String EXTERNAL_DATA_SOURCE = "iris.external.source";

    /** Factory to create the DataValues. */
    private final DataFactory DATA_FACTORY;

    /** Factory to create the DataValues. */
    private final LogicalExpressionFactory LOGIC_FACTORY;

    /** Factory to create the wsmo objects. */
    private final WsmoFactory WSMO_FACTORY;

    /** knowledge-base. */
    private org.deri.iris.api.IKnowledgeBase prog;

    private QueryContainment queryCont = null;

    /** Map that contains the variable mapping from the query containment check. */
    private org.deri.iris.storage.IRelation QCResult = new SimpleRelationFactory().createRelation();

    /**
     * The external data sources.
     */
    private final Collection<ExternalDataSource> sources;
    
    /**
     *  New WSML builtin predicates from  D3.1.4 Defining the features of the WSML-Rule v2.0 language
	 *	 (Not in WSMO-api BuiltIns.)
     */
	private final static String IS_PREFIX = WSML.WSML_NAMESPACE + "is"; // + DATATYPE				// RIF : pred:isLiteralOfType
	final static String IS_DOUBLE = IS_PREFIX + "Double";
	final static String IS_STRING = IS_PREFIX + "String";
	final static String IS_DECIMAL = IS_PREFIX + "Decimal";
	final static String IS_BOOLEAN = IS_PREFIX + "Boolean";
	final static String IS_INTEGER = IS_PREFIX + "Integer";
	final static String IS_BASE64BINARY = IS_PREFIX + "Base64Binary";
	final static String IS_DATE = IS_PREFIX + "Date";
	final static String IS_DATETIME = IS_PREFIX + "DateTime";
	final static String IS_DURATION = IS_PREFIX + "Duration";
	final static String IS_FLOAT = IS_PREFIX + "Float";
	final static String IS_GDAY = IS_PREFIX + "GDay";
	final static String IS_GMONTH = IS_PREFIX + "GMonth";
	final static String IS_GMONTHDAY = IS_PREFIX + "GMonthDay";
	final static String IS_GYEAR = IS_PREFIX + "GYear";
	final static String IS_GYEARMONTH= IS_PREFIX + "GYearMonth";
	final static String IS_HEXBINARY = IS_PREFIX + "HexBinary";
	final static String IS_TIME = IS_PREFIX + "Time";
	final static String IS_DAYTIME_DURATION = IS_PREFIX + "DayTimeDuration";
	final static String IS_YEAR_MONTH_DURATION = IS_PREFIX + "YearMonthDuration";
	final static String IS_IRI = IS_PREFIX + "Iri";
	final static String IS_TEXT = IS_PREFIX + "Text";
	final static String IS_XML_LITERAL = IS_PREFIX + "XMLLiteral";

	public AbstractIrisFacade(final FactoryContainer factory, final Map<String, Object> config) {
        DATA_FACTORY = factory.getXmlDataFactory();
        WSMO_FACTORY = factory.getWsmoFactory();
        LOGIC_FACTORY = factory.getLogicalExpressionFactory();

        // retrieving the data source
        final Object ds = (config != null) ? config.get(EXTERNAL_DATA_SOURCE) : null;
        if ((ds != null) && (ds instanceof Collection<?>)) {
            sources = (Collection<ExternalDataSource>) ds;
        }
        else {
            sources = new ArrayList<ExternalDataSource>();
        }
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
            body.add(literal2Literal(l, false));
        }

        // create query
        final IQuery query = BASIC.createQuery(body);

        org.deri.iris.storage.IRelation executionResult;
        List<IVariable> variableBindings = new ArrayList<IVariable>();
        try {
            executionResult = prog.execute(query, variableBindings);
        }
        catch (EvaluationException e2) {
            throw new ExternalToolException(e2.getMessage());
        }

        final Set<Map<Variable, Term>> res = new HashSet<Map<Variable, Term>>();

        for (int i = 0; i < executionResult.size(); ++i) {
            ITuple t = executionResult.get(i);

            assert t.size() == variableBindings.size();

            Map<Variable, Term> tmp = new HashMap<Variable, Term>();

            for (int pos = 0; pos < t.size(); ++pos) {
                IVariable variable = variableBindings.get(pos);
                ITerm term = t.get(pos);

                tmp.put((Variable) convertTermFromIrisToWsmo4j(variable), convertTermFromIrisToWsmo4j(term));
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
        final List<ILiteral> body = new ArrayList<ILiteral>(query1.getLiterals().size());

        // converting the literals of the query
        for (final Literal l : query1.getLiterals()) {
            body.add(literal2Literal(l, false));
        }

        final IQuery iQuery1 = BASIC.createQuery(body);

        // constructing query 2
        // final List<ILiteral> head2 = new ArrayList<ILiteral>(1);
        final List<ILiteral> body2 = new ArrayList<ILiteral>(query2.getLiterals().size());

        // converting the literals of the query
        for (final Literal l : query2.getLiterals()) {
            body2.add(literal2Literal(l, false));
        }

        // creating the query
        final IQuery iQuery2 = BASIC.createQuery(body2);

        // doing the query containment check
        queryCont = new QueryContainment(prog);

        // System.out.println("prog: " + prog);
        // System.out.println("query1: " + query1);
        // System.out.println("query2 : " + query2);
        boolean check = false;
        try {
            check = queryCont.checkQueryContainment(iQuery1, iQuery2);
        }
        catch (Exception e) {
            new ExternalToolException(e.getMessage());
        }

        return check;
    }

    public synchronized Set<Map<Variable, Term>> getQueryContainment(ConjunctiveQuery query1, ConjunctiveQuery query2) throws ExternalToolException {
    	// check query containment and get IRIS result set
    	if (checkQueryContainment(query1, query2)) {
    		QCResult = queryCont.getContainmentMappings();
	        // constructing the result set to return
	        final Set<Map<Variable,Term>> result = new HashSet<Map<Variable, Term>>();
	        // getting the variables from query2 in the execution order
	        List<IVariable> variableBindings = queryCont.getVariableBindings();
        
	        assert QCResult.size() > 0;
	        assert variableBindings.size() == QCResult.get(0).size();
	        
	        for (int i =0; i< QCResult.size(); i++){ ITuple t = QCResult.get(i);
	        final Map<Variable, Term> tmp = new HashMap<Variable, Term>();
	        for( int pos = 0; pos < t.size(); pos++){
	        	tmp.put( (Variable)convertTermFromIrisToWsmo4j(variableBindings.get(pos)),
	        					convertTermFromIrisToWsmo4j(t.get(pos))); } result.add(tmp);
	        }
	        QCResult = new SimpleRelationFactory().createRelation();
	        return result;
        }
        return new HashSet<Map<Variable, Term>>();

    }

    public synchronized void register(Set<Rule> kb) throws ExternalToolException {
        if (kb == null) {
            throw new IllegalArgumentException("The knowledge base must not be null");
        }
        if (prog != null) {
            deregister();
        }

        Map<IPredicate, org.deri.iris.storage.IRelation> facts = new HashMap<IPredicate, org.deri.iris.storage.IRelation>();
        List<IRule> rules = new ArrayList<IRule>();

        // translating all the rules
		for (final Rule r : kb) {
			if (r.isFact()) { // the rule is a fact
				IAtom atom = literal2Atom(r.getHead(), true);
				IPredicate pred = atom.getPredicate();
				// if its a fact with equal - it is transformed to a new rule
				// (e.g.: a=b. -> a=b impliedBy wsml#true)
				if (containsEqualBuiltin(r.getHead())) {
					IRule newRule = BASIC.createRule(Collections
							.singletonList(BASIC.createLiteral(true, atom)),
							Collections.singletonList(BASIC.createLiteral(true,
									BUILTIN.createTrue())));
					rules.add(newRule);
				} else {
					org.deri.iris.storage.IRelation relation = facts.get(atom
							.getPredicate());
					if (relation == null) {
						relation = new org.deri.iris.storage.simple.SimpleRelationFactory()
								.createRelation();
						facts.put(pred, relation);
					}
					relation.add(atom.getTuple());
				}
			} else { // the rule is an ordinary rule
				final List<ILiteral> head = new ArrayList<ILiteral>(1);
				final List<ILiteral> body = new ArrayList<ILiteral>(r.getBody()
						.size());
				// converting the head of the rule
				head.add(literal2Literal(r.getHead(), true));

				// converting the body of the rule
				for (final Literal l : r.getBody()) {
					body.add(literal2Literal(l, false));
				}
				rules.add(BASIC.createRule(head, body));
			}
		}
        // add the wsml-member-of rules for primitive data types
        // Removed. See bug 2248622
//        for (final IRule r : getWsmlMemberOfRules()) {
//            rules.add(r);
//        }

        final Configuration configuration = org.deri.iris.KnowledgeBaseFactory.getDefaultConfiguration();
        
        // add the data sources
        for (final ExternalDataSource ext : sources) {
            configuration.externalDataSources.add(new IrisDataSource(ext));
        }
        
        configureIris( configuration );

        try {
            prog = org.deri.iris.KnowledgeBaseFactory.createKnowledgeBase(facts, rules, configuration);
        }
        catch (EvaluationException e) {

            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * Create the IRIS configuration object.
     * @return
     */
    protected abstract void configureIris( Configuration configuration );
    
    /**
     * Converts a wsmo4j literal to an iris literal.
     * 
     * @param l the wsmo4j literal to convert
     * @return the iris literal
     */
    static ILiteral literal2Literal(final Literal l, boolean headLiteral) {
        assert l != null;
        assert (headLiteral && l.isPositive()) || ! headLiteral;

        return BASIC.createLiteral(l.isPositive(), literal2Atom(l, headLiteral));
    }

    /**
     * Converts a wsml reasoner atomic formula in a literal to an iris atom.
     * 
     * @param literal the wsmo4j literal to convert
     * @return the iris atom
     */
    static IAtom literal2Atom(Literal literal, boolean headLiteral) {
//    	System.out.println("Literal : " + literal);
    	assert literal != null;
    	
    	String sym = literal.getPredicateUri();
    	Term[] inTerms = literal.getTerms();
    	
        assert sym != null;
        assert inTerms != null;
        
        final List<ITerm> terms = new ArrayList<ITerm>(inTerms.length);
        // convert the terms of the literal
        for (final Term t : inTerms) {
            terms.add(convertTermFromWsmo4jToIris(t));
        }
        
        IAtom atom =checkBuiltin(headLiteral ,sym, terms);
        
        // TODO Handle built-ins with terms representing built-ins.
        // An example for this is: wsml#equal(wsml#numericAdd(1, 1), 2).
        
        return atom;
    }
    
    
    private static IAtom checkBuiltin(boolean headLiteral, String sym, List<ITerm> terms) {
        // check whether the predicate is a builtin
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
			return BUILTIN.createEqual(terms.get(0), terms.get(1));
		}
		else if (sym.equals(BuiltIn.INEQUAL.getFullName())
				|| sym.equals(BuiltIn.NUMERIC_INEQUAL.getFullName())
				|| sym.equals(BuiltIn.STRING_INEQUAL.getFullName())
				|| sym.equals(BuiltIn.DATE_INEQUAL.getFullName())
				|| sym.equals(BuiltIn.TIME_INEQUAL.getFullName())
				|| sym.equals(BuiltIn.DATETIME_INEQUAL.getFullName())) {
			return BUILTIN.createUnequal(terms.get(0), terms.get(1));
		}
		else if (sym.equals(BuiltIn.LESS_THAN.getFullName())
				|| sym.equals(BuiltIn.DATE_LESS_THAN.getFullName())
				|| sym.equals(BuiltIn.TIME_LESS_THAN.getFullName())
				|| sym.equals(BuiltIn.DATETIME_LESS_THAN.getFullName())
				|| sym.equals(BuiltIn.DAYTIMEDURATION_LESS_THAN.getFullName())
				|| sym.endsWith(BuiltIn.YEARMONTHDURATION_LESS_THAN
						.getFullName())) {
			return BUILTIN.createLess(terms.get(0), terms.get(1));
		}
		else if (sym.equals(BuiltIn.LESS_EQUAL.getFullName())) {
			return BUILTIN.createLessEqual(terms.get(0), terms.get(1));
		} else if (sym.equals(BuiltIn.GREATER_THAN.getFullName())
				|| sym.equals(BuiltIn.DATE_GREATER_THAN.getFullName())
				|| sym.equals(BuiltIn.TIME_GREATER_THAN.getFullName())
				|| sym.equals(BuiltIn.DATETIME_GREATER_THAN.getFullName())
				|| sym.equals(BuiltIn.DAYTIMEDURATION_GREATER_THAN.getFullName())
				|| sym.equals(BuiltIn.YEARMONTHDURATION_GREATER_THAN.getFullName())) {
			return BUILTIN.createGreater(terms.get(0), terms.get(1));
		}
        else if (sym.equals(BuiltIn.GREATER_EQUAL.getFullName())) {
            return BUILTIN.createGreaterEqual(terms.get(0), terms.get(1));
        }
        else if (sym.equals(BuiltIn.NUMERIC_ADD.getFullName())) {
            return BUILTIN.createAddBuiltin(terms.get(1), terms.get(2), terms.get(0));
        }
        else if (sym.equals(BuiltIn.NUMERIC_SUBTRACT.getFullName())) {
            return BUILTIN.createSubtractBuiltin(terms.get(1), terms.get(2), terms.get(0));
        }
        else if (sym.equals(BuiltIn.NUMERIC_MULTIPLY.getFullName())) {
            return BUILTIN.createMultiplyBuiltin(terms.get(1), terms.get(2), terms.get(0));
        }
        else if (sym.equals(BuiltIn.NUMERIC_DIVIDE.getFullName())) {
            return BUILTIN.createDivideBuiltin(terms.get(1), terms.get(2), terms.get(0));
        }
        else if (sym.equals(BuiltIn.IS_DATATYPE.getFullName())) {
        	return BUILTIN.createIsDatatype(toArray(terms));
        }
        else if (sym.equals(BuiltIn.IS_NOT_DATATYPE.getFullName())) {
        	return BUILTIN.createIsNotDatatype(toArray(terms));
        }
//        else if (sym.equals(HAS_DATATYPE)) { // TODO check
//        	return BUILTIN.createHasDatatype(toArray(terms));
//        }
        else if (sym.equals(Constants.TRUE) || sym.equals(Constants.UNIV_TRUE) ){
        	return BUILTIN.createTrue();
        }
        else if (sym.equals(Constants.FALSE) || sym.equals(Constants.UNIV_FALSE) ){
        	return BUILTIN.createFalse();
        }
        else if (sym.equals(BuiltIn.NUMERIC_MODULUS.getFullName())) { // check is done by normal Modulus
        	return BUILTIN.createNumericModulus(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_COMPARE.getFullName())) {
        	return BUILTIN.createStringCompare(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_CONCAT.getFullName())) {
        	return BUILTIN.createStringConcat(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_JOIN.getFullName())) {
        	return BUILTIN.createStringJoin(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_SUBSTRING.getFullName())) {
        	return BUILTIN.createStringSubstring(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_LENGTH.getFullName())) {
        	return BUILTIN.createStringLength(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_TO_UPPER.getFullName())) {
        	return BUILTIN.createStringToUpper(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_TO_LOWER.getFullName())) {
        	return BUILTIN.createStringToLower(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_URI_ENCODE.getFullName())) {
        	return BUILTIN.createStringUriEncode(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_IRI_TO_URI.getFullName())) {
        	return BUILTIN.createStringIriToUri(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_ESCAPE_HTML_URI.getFullName())) {
        	return BUILTIN.createStringEscapeHtmlUri(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_SUBSTRING_BEFORE.getFullName())) {
        	return BUILTIN.createStringSubstringBefore(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_SUBSTRING_AFTER.getFullName())) {
        	return BUILTIN.createStringSubstringAfter(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_REPLACE.getFullName())) {
        	return BUILTIN.createStringReplace(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_CONTAINS.getFullName())) {
        	return BUILTIN.createStringContains(toArray(terms));
        }
        else if (sym.equals(BuiltIn.STRING_STARTS_WITH.getFullName())) {
        	return BUILTIN.createStringStartsWith(toArray(terms));
        }
        else if (sym.equals(BuiltIn.STRING_ENDS_WITH.getFullName())) {
        	return BUILTIN.createStringEndsWith(toArray(terms));
        }
        else if (sym.equals(BuiltIn.STRING_MATCHES.getFullName())) {
        	return BUILTIN.createStringMatches(toArray(terms));
        }
        else if (sym.equals(BuiltIn.YEAR_PART.getFullName())) {
        	return BUILTIN.createYearPart(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.MONTH_PART.getFullName())) {
        	return BUILTIN.createMonthPart(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.DAY_PART.getFullName())) {
        	return BUILTIN.createDayPart(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.HOUR_PART.getFullName())) {
        	return BUILTIN.createHourPart(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.MINUTE_PART.getFullName())) {
        	return BUILTIN.createMinutePart(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.SECOND_PART.getFullName())) {
        	return BUILTIN.createSecondPart(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TIMEZONE_PART.getFullName())) {
        	return BUILTIN.createTimezonePart(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TEXT_FROM_STRING_LANG.getFullName())) {
        	return BUILTIN.createTextFromStringLang(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TEXT_FROM_STRING.getFullName())) {
        	return BUILTIN.createTextFromStringLang(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.STRING_FROM_TEXT.getFullName())) {
        	return BUILTIN.createStringFromText(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.LANG_FROM_TEXT.getFullName())) {
        	return BUILTIN.createLangFromText(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TEXT_COMPARE.getFullName())) {
        	return BUILTIN.createTextCompare(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_BASE64.getFullName())) {
        	return BUILTIN.createToBase64Binary(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_BOOLEAN.getFullName())) {
        	return BUILTIN.createToBoolean(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_DATE.getFullName())) {
        	return BUILTIN.createToDate(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_DATETIME.getFullName())) {
        	return BUILTIN.createToDateTime(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_DAYTIMEDURATION.getFullName())) {
        	return BUILTIN.createToDayTimeDuration(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_DECIMAL.getFullName())) {
        	return BUILTIN.createToDecimal(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_DOUBLE.getFullName())) {
        	return BUILTIN.createToDouble(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_DURATION.getFullName())) {
        	return BUILTIN.createToDuration(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_FLOAT.getFullName())) {
        	return BUILTIN.createToFloat(toArray(sortListForIRIS(terms)));
        }
		// TODO  mp: problems: no return values by to<datatype> builtins where duration or time is needed!
        else if (sym.equals(BuiltIn.TO_GDAY.getFullName())) {
        	return BUILTIN.createToGDay(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_GMONTH.getFullName())) {
        	return BUILTIN.createToGMonth(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_GMONTHDAY.getFullName())) {
        	return BUILTIN.createToGMonthDay(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_GYEAR.getFullName())) {
        	return BUILTIN.createToGYear(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_GYEARMONTH.getFullName())) {
        	return BUILTIN.createToGYearMonth(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_HEXBINARY.getFullName())) {
        	return BUILTIN.createToHexBinary(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_INTEGER.getFullName())) {
        	return BUILTIN.createToInteger(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_IRI.getFullName())) {
        	return BUILTIN.createToIRI(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_STRING.getFullName())) {
        	return BUILTIN.createToString(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_TEXT.getFullName())) {
        	return BUILTIN.createToText(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_TIME.getFullName())) {
        	return BUILTIN.createToTime(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_XMLLITERAL.getFullName())) {
        	return BUILTIN.createToXMLLiteral(toArray(sortListForIRIS(terms)));
        }
        else if (sym.equals(BuiltIn.TO_YEARMONTHDURATION.getFullName())) {
        	return BUILTIN.createToYearMonthDuration(toArray(sortListForIRIS(terms)));
        }
        // the is-datatype-things
        else if (sym.equals(BuiltIn.IS_DATATYPE.getFullName())) {
        	return BUILTIN.createIsDatatype(toArray(terms));
        }
        else if (sym.equals(BuiltIn.IS_NOT_DATATYPE.getFullName())) {
        	return BUILTIN.createIsNotDatatype(toArray(terms));
        }
        else if (sym.equals(IS_BASE64BINARY)) {
        	return BUILTIN.createIsBase64Binary(toArray(terms));
        }
        else if (sym.equals(IS_BOOLEAN)) {
        	return BUILTIN.createIsBoolean(toArray(terms));
        }
        else if (sym.equals(IS_DATE)) {
        	return BUILTIN.createIsDate(toArray(terms));
        }
        else if (sym.equals(IS_DATETIME)) {
        	return BUILTIN.createIsDateTime(toArray(terms));
        }
        else if (sym.equals(IS_DAYTIME_DURATION)) {
        	return BUILTIN.createIsDayTimeDuration(toArray(terms));
        }
        else if (sym.equals(IS_DECIMAL)) {
        	return BUILTIN.createIsDecimal(toArray(terms));
        }
        else if (sym.equals(IS_DOUBLE)) {
        	return BUILTIN.createIsDouble(toArray(terms));
        }
        else if (sym.equals(IS_DURATION)) {
        	return BUILTIN.createIsDuration(toArray(terms));
        }
        else if (sym.equals(IS_FLOAT)) {
        	return BUILTIN.createIsFloat(toArray(terms));
        }
        else if (sym.equals(IS_GDAY)) {
        	return BUILTIN.createIsGDay(toArray(terms));
        }
        else if (sym.equals(IS_GMONTH)) {
        	return BUILTIN.createIsGMonth(toArray(terms));
        }
        else if (sym.equals(IS_GMONTHDAY)) {
        	return BUILTIN.createIsGMonthDay(toArray(terms));
        }
        else if (sym.equals(IS_GYEAR)) {
        	return BUILTIN.createIsGYear(toArray(terms));
        }
        else if (sym.equals(IS_GYEARMONTH)) {
        	return BUILTIN.createIsGYearMonth(toArray(terms));
        }
        else if (sym.equals(IS_HEXBINARY)) {
        	return BUILTIN.createIsHexBinary(toArray(terms));
        }
        else if (sym.equals(IS_INTEGER)) {
        	return BUILTIN.createIsInteger(toArray(terms));
        }
        else if (sym.equals(IS_IRI)) {
        	return BUILTIN.createIsIRI(toArray(terms));
        }
        else if (sym.equals(IS_STRING)) {
        	return BUILTIN.createIsString(toArray(terms));
        }
        else if (sym.equals(IS_TEXT)) {
        	return BUILTIN.createIsText(toArray(terms));
        }
        else if (sym.equals(IS_TIME)) {
        	return BUILTIN.createIsTime(toArray(terms));
        }
        else if (sym.equals(IS_XML_LITERAL)) {
        	return BUILTIN.createIsXMLLiteral(toArray(terms));
        }
        else if (sym.equals(IS_YEAR_MONTH_DURATION)) {
        	return BUILTIN.createIsYearMonthDuration(toArray(terms));
        }
        else if( ! headLiteral && sym.equals( WSML2DatalogTransformer.PRED_MEMBER_OF ) ) {
        	// Special case! Look for wsml-member-of( ?x, wsml#<datatype> )
        	// and change it to one of IRIS's IS_XXXXX() built-ins
        	
        	// We only do this for rule body predicates
        	if( terms.size() == 2 ) {
        		ITerm t0 = terms.get(0);
        		ITerm t1 = terms.get(1);
        		
//        		if( t0 instanceof IVariable && t1 instanceof IIri ) {
           		if( t1 instanceof IIri ) {
        			IIri iri = (IIri) t1;
        			String type = iri.getValue();
        			if( type.equals( WsmlDataType.WSML_STRING ) || type.equals( XmlSchemaDataType.XSD_STRING ) )
        				return new IsStringBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_DECIMAL ) || type.equals( XmlSchemaDataType.XSD_DECIMAL ) )
        				return new IsDecimalBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_DOUBLE ) || type.equals( XmlSchemaDataType.XSD_DOUBLE ) )
        				return new IsDoubleBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_BOOLEAN ) || type.equals( XmlSchemaDataType.XSD_BOOLEAN ) )
        				return new IsBooleanBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_INTEGER ) || type.equals( XmlSchemaDataType.XSD_INTEGER ) )
        				return new IsIntegerBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_BASE64BINARY ) || type.equals( XmlSchemaDataType.XSD_BASE64BINARY ) )
        				return new IsBase64BinaryBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_DATE ) || type.equals( XmlSchemaDataType.XSD_DATE ) )
        				return new IsDateBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_DATETIME ) || type.equals( XmlSchemaDataType.XSD_DATETIME ) )
        				return new IsDateTimeBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_DURATION ) || type.equals( XmlSchemaDataType.XSD_DURATION ) )
        				return new IsDurationBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_FLOAT ) || type.equals( XmlSchemaDataType.XSD_FLOAT ) )
        				return new IsFloatBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_GDAY ) || type.equals( XmlSchemaDataType.XSD_GDAY ) )
        				return new IsGDayBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_GMONTH ) || type.equals( XmlSchemaDataType.XSD_GMONTH ) )
        				return new IsGMonthBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_GMONTHDAY ) || type.equals( XmlSchemaDataType.XSD_GMONTHDAY ) )
        				return new IsGMonthDayBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_GYEAR ) || type.equals( XmlSchemaDataType.XSD_GYEAR ) )
        				return new IsGYearBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_GYEARMONTH ) || type.equals( XmlSchemaDataType.XSD_GYEARMONTH ) )
        				return new IsGYearMonthBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_HEXBINARY ) || type.equals( XmlSchemaDataType.XSD_HEXBINARY ) )
        				return new IsHexBinaryBuiltin( t0 );
        			else if( type.equals( WsmlDataType.WSML_TIME ) || type.equals( XmlSchemaDataType.XSD_TIME ) )
        				return new IsTimeBuiltin( t0 );
        			// new XSDs
        			else if( type.equals( XmlSchemaDataType.XSD_YEARMONTHDURATION ) )  
        				return new IsYearMonthDurationBuiltin( t0 );
        			else if( type.equals( XmlSchemaDataType.XSD_DAYTIMEDURATION ) )  
        				return new IsDayTimeDurationBuiltin( t0 );
        			// RDF 
        			else if( type.equals( RDFDataType.RDF_TEXT ) )  {
        				return new IsTextBuiltin( t0 );
        			}
        			else if( type.equals( RDFDataType.RDF_XMLLITERAL ) )  
        				return new IsXMLLiteralBuiltin( t0 );
        			
        		}
        	}
        	// If none of these then drop through to normal atom processing.
        }
        // return an ordinary atom
        return BASIC.createAtom(BASIC.createPredicate(sym, terms.size()), BASIC.createTuple(terms));
	}

	/**
	 * Converts a wsmo4j term to an iris term
	 * 
	 * @param t the wsmo4j term
	 * @return the converted iris term
	 */
	static ITerm convertTermFromWsmo4jToIris(final Term t) {
		if (t == null) {
			throw new NullPointerException("The term must not be null");
		} else if (t instanceof BuiltInConstructedTerm) {
            // TODO: builtins are left out at the moment
		} else if (t instanceof ConstructedTerm) {
			// System.out.println("CONSTRUCTED TERM: " + t);
			final ConstructedTerm ct = (ConstructedTerm) t;
			final List<ITerm> terms = new ArrayList<ITerm>(ct.getArity());
			for (final Term term : (List<Term>) ct.listParameters()) {
				terms.add(convertTermFromWsmo4jToIris(term));
			}
			return TERM.createConstruct(ct.getFunctionSymbol().toString(),
					terms);
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
		throw new IllegalArgumentException("Can't convert a term of type "
				+ t.getClass().getName());
	}

    /**
     * Converts a wsmo4j DataValue to an iris ITerm.
     * 
     * @param v the wsmo4j value to convert
     * @return the corresponding ITerm implementation
     */
    static ITerm convertWsmo4jDataValueToIrisTerm(final DataValue v) {
        if (v == null) {
            throw new NullPointerException("The data value must not be null");
        }
        final String t = v.getType().getIdentifier().toString();
        if (t.equals(WsmlDataType.WSML_BASE64BINARY) || t.equals(XmlSchemaDataType.XSD_BASE64BINARY)) {
            return CONCRETE.createBase64Binary(v.getValue().toString());
        }
        else if (t.equals(WsmlDataType.WSML_BOOLEAN) || t.equals(XmlSchemaDataType.XSD_BOOLEAN)) {
            return CONCRETE.createBoolean(Boolean.valueOf(v.getValue().toString()));
        }
        else if (t.equals(WsmlDataType.WSML_DATE) || t.equals(XmlSchemaDataType.XSD_DATE)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            int length = cv.getArity();
            return CONCRETE.createDate(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2),
            				length > 3 ? getIntFromValue(cv, 3) : 0,
            	            length > 4 ? getIntFromValue(cv, 4) : 0);
        }
        else if (t.equals(WsmlDataType.WSML_DATETIME) || t.equals(XmlSchemaDataType.XSD_DATETIME)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            int length = cv.getArity();
            return CONCRETE.createDateTime(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2),
            				getIntFromValue(cv, 3), getIntFromValue(cv, 4), getDoubleFromValue(cv, 5),
            				length > 6 ? getIntFromValue(cv, 6) : 0,
            				length > 7 ? getIntFromValue(cv, 7) : 0);
        }
        else if (t.equals(WsmlDataType.WSML_TIME) || t.equals(XmlSchemaDataType.XSD_TIME)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            int length = cv.getArity();
            return CONCRETE.createTime(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getDoubleFromValue(cv, 2),
            				length > 3 ? getIntFromValue(cv, 3) : 0,
							length > 4 ? getIntFromValue(cv, 4) : 0);
        }
        else if (t.equals(WsmlDataType.WSML_DECIMAL) || t.equals(XmlSchemaDataType.XSD_DECIMAL)) {
            return CONCRETE.createDecimal(Double.parseDouble(v.getValue().toString()));
        }
        else if (t.equals(WsmlDataType.WSML_DOUBLE) || t.equals(XmlSchemaDataType.XSD_DOUBLE)) {
            return CONCRETE.createDouble(Double.parseDouble(v.getValue().toString()));
        }
        else if (t.equals(WsmlDataType.WSML_DURATION) || t.equals(XmlSchemaDataType.XSD_DURATION)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createDuration( true, getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2),
            				getIntFromValue(cv, 3), getIntFromValue(cv, 4), getDoubleFromValue(cv, 5) );
        }
        else if (t.equals(WsmlDataType.WSML_FLOAT) || t.equals(XmlSchemaDataType.XSD_FLOAT)) {
            return CONCRETE.createFloat(Float.parseFloat(v.getValue().toString()));
        }
        else if (t.equals(WsmlDataType.WSML_GDAY) || t.equals(XmlSchemaDataType.XSD_GDAY)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGDay(getIntFromValue(cv, 0));
        }
        else if (t.equals(WsmlDataType.WSML_GMONTH) || t.equals(XmlSchemaDataType.XSD_GMONTH)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGMonth(getIntFromValue(cv, 0));
        }
        else if (t.equals(WsmlDataType.WSML_GMONTHDAY) || t.equals(XmlSchemaDataType.XSD_GMONTHDAY)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGMonthDay(getIntFromValue(cv, 0), getIntFromValue(cv, 1));
        }
        else if (t.equals(WsmlDataType.WSML_GYEAR) || t.equals(XmlSchemaDataType.XSD_GYEAR)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGYear(getIntFromValue(cv, 0));
        }
        else if (t.equals(WsmlDataType.WSML_GYEARMONTH) || t.equals(XmlSchemaDataType.XSD_GYEARMONTH)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGYearMonth(getIntFromValue(cv, 0), getIntFromValue(cv, 1));
        }
        else if (t.equals(WsmlDataType.WSML_HEXBINARY) || t.equals(XmlSchemaDataType.XSD_HEXBINARY)) {
            return CONCRETE.createHexBinary(v.getValue().toString());
        }
        else if (t.equals(WsmlDataType.WSML_INTEGER) || t.equals(XmlSchemaDataType.XSD_INTEGER)) {
            return CONCRETE.createInteger(Integer.parseInt(v.toString()));
        }
        else if (t.equals(XmlSchemaDataType.XSD_DAYTIMEDURATION)) {
        	 final ComplexDataValue cv = (ComplexDataValue) v;
             return CONCRETE.createDayTimeDuration(true, getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2),
            				getDoubleFromValue(cv, 3));
        }
        else if (t.equals(XmlSchemaDataType.XSD_YEARMONTHDURATION)) {
       	 	final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createYearMonthDuration(true, getIntFromValue(cv, 0), getIntFromValue(cv, 1));
        }
        else if (t.equals(WsmlDataType.WSML_STRING) || t.equals(XmlSchemaDataType.XSD_STRING)) {
            return TERM.createString(v.toString());
        }
        // RDF Datatypes
        else if (t.equals(RDFDataType.RDF_TEXT)) {
        	final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createText(getStringFromValue(cv, 0), getStringFromValue(cv, 1));
        }
        else if (t.equals(RDFDataType.RDF_XMLLITERAL)) {
        	final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createXMLLiteral(getStringFromValue(cv, 0), getStringFromValue(cv, 1));
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
    private static int getIntFromValue(final ComplexDataValue value, int pos) {
        assert value != null;
        assert pos >= 0;

        return Integer.parseInt(getFieldValue(value, pos));
    }
    
    /**
     * Returns the String value of a ComplexDataValue at a given position.
     * 
     * @param value the complex data value from where to get the String
     * @param pos the index of the String
     * @return the extracted and converted String
     */
    private static String getStringFromValue(final ComplexDataValue value, int pos) {
        assert value != null;
        assert pos >= 0;

        return new String(getFieldValue(value, pos).toString());
    }

    /**
     * Get a double value from the specified position.
     * @param value The complex data value from which the double is extracted.
     * @param pos The zero-basd index of the desired value.
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
    Term convertTermFromIrisToWsmo4j(final ITerm t) {
        if (t == null) {
            throw new NullPointerException("The term must not be null");
        }
        /*
         * subinterfaces of IStringTerm have to be handeled before the
         * IStringTerm block
         */
        if (t instanceof IBase64Binary) {
            return DATA_FACTORY.createBase64Binary(((IBase64Binary) t).getValue().getBytes());
        }
        else if (t instanceof IHexBinary) {
            return DATA_FACTORY.createHexBinary(((IHexBinary) t).getValue().getBytes());
        }
        else if (t instanceof IIri) {
            return WSMO_FACTORY.createIRI(((IIri) t).getValue());
        }
        else if (t instanceof IStringTerm) {
            return DATA_FACTORY.createString(((IStringTerm) t).getValue());
        }
        else if (t instanceof IVariable) {
            return LOGIC_FACTORY.createVariable(((IVariable) t).getValue());
        }
        else if (t instanceof IConstructedTerm) {
            final IConstructedTerm ct = (IConstructedTerm) t;
            final List<Term> terms = new ArrayList<Term>(ct.getValue().size());
            for (final ITerm term : ct.getValue()) {
                terms.add(convertTermFromIrisToWsmo4j(term));
            }
            return LOGIC_FACTORY.createConstructedTerm(WSMO_FACTORY.createIRI(ct.getFunctionSymbol()), terms);
        }
        else if (t instanceof IBooleanTerm) {
            return DATA_FACTORY.createBoolean(((IBooleanTerm) t).getValue());
        }
        else if (t instanceof IDateTerm) {
            final IDateTerm dt = (IDateTerm) t;
            int[] tzData = getTZData(dt.getTimeZone());
            return DATA_FACTORY.createDate(dt.getYear(), dt.getMonth(), dt.getDay(), tzData[0], tzData[1]);
        }
        else if (t instanceof IDateTime) {
            final IDateTime dt = (IDateTime) t;
            int[] tzData = getTZData(dt.getTimeZone());
            return DATA_FACTORY.createDateTime(dt.getYear(), dt.getMonth(), dt.getDay(),
            				dt.getHour(), dt.getMinute(), (float) dt.getDecimalSecond(), tzData[0], tzData[1]); 
            // TODO gigi: I introduced the float cast, check if this is correct
        }
        else if (t instanceof ITime) {
            final ITime time = (ITime) t;
            int[] tzData = getTZData(time.getTimeZone());
            return DATA_FACTORY.createTime(time.getHour(), time.getMinute(), (float) time.getDecimalSecond(), 
            // TODO gigi: I introduced the float cast, check if this is correct
            				tzData[0], tzData[1]);
        }
        else if (t instanceof IDecimalTerm) {
            return DATA_FACTORY.createDecimal(new BigDecimal(((IDecimalTerm) t).toString()));
        }
        else if (t instanceof IDoubleTerm) {
            return DATA_FACTORY.createDouble(((IDoubleTerm) t).getValue());
        }
        else if (t instanceof IDuration) {
            final IDuration dt = (IDuration) t;
            return DATA_FACTORY.createDuration( dt.getYear(), dt.getMonth(), dt.getDay(), dt.getHour(), dt.getMinute(), dt.getDecimalSecond());
        }
        else if (t instanceof IFloatTerm) {
            return DATA_FACTORY.createFloat(((IFloatTerm) t).getValue());
        }
        else if (t instanceof IGDay) {
            return DATA_FACTORY.createGregorianDay(((IGDay) t).getDay());
        }
        else if (t instanceof IGMonth) {
            return DATA_FACTORY.createGregorianMonth(((IGMonth) t).getMonth());
        }
        else if (t instanceof IGMonthDay) {
            final IGMonthDay md = (IGMonthDay) t;
            return DATA_FACTORY.createGregorianMonthDay(md.getMonth(), md.getDay());
        }
        else if (t instanceof IGYear) {
            return DATA_FACTORY.createGregorianYear(((IGYear) t).getYear());
        }
        else if (t instanceof IGYearMonth) {
            final IGYearMonth md = (IGYearMonth) t;
            return DATA_FACTORY.createGregorianYearMonth(md.getYear(), md.getMonth());
        }
        else if (t instanceof IIntegerTerm) {
            return DATA_FACTORY.createInteger(new BigInteger(t.getValue().toString()));
        }
        else if (t instanceof IYearMonthDuration) {
        	return DATA_FACTORY.createYearMonthDuration( ((IYearMonthDuration) t).getYear(), ((IYearMonthDuration) t).getMonth());
        }
        else if (t instanceof IDayTimeDuration) {
       	 	return DATA_FACTORY.createDayTimeDuration(((IDayTimeDuration) t).getDay(), ((IDayTimeDuration) t).getHour(), ((IDayTimeDuration) t).getMinute(), ((IDayTimeDuration) t).getSecond());
        }
        else if (t instanceof IXMLLiteral) {
        	// checks if there is a language string
			String lang;
			if (((IXMLLiteral) t).getLang() == null) {
				lang = "";
			} else {
				lang = ((IXMLLiteral) t).getLang();
			}
			return DATA_FACTORY.createXMLLiteral(((IXMLLiteral) t).getString(),
					lang);
        }
        else if (t instanceof IText) {
        	// checks if there is a language string
        	String lang;
			if (((IText) t).getLang() == null) {
				lang = "";
			} else {
				lang = ((IText) t).getLang();
			}
        	return DATA_FACTORY.createText(((IText) t).getString(),lang);
        }
        else if (t instanceof ISqName) {
            // couldn't find this type in wsmo4j
        }
        throw new IllegalArgumentException("Can't convert a term of type " + t.getClass().getName());
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

   // ****** REMOVED. SEE BUG 2248622 **********
//    /**
//     * Returns the rules for the wsml-member-of rules.
//     * 
//     * @return the wsml-member-of rules
//     */
//    private static Set<IRule> getWsmlMemberOfRules() {
//        final Set<IRule> res = new HashSet<IRule>();
//        final IPredicate WSML_MEBER_OF = BASIC.createPredicate(WSML2DatalogTransformer.PRED_MEMBER_OF, 2);
//        final IVariable X = TERM.createVariable("X");
//        final IVariable Y = TERM.createVariable("Y");
//        final IVariable Z = TERM.createVariable("Z");
//        final ILiteral hasValue = BASIC.createLiteral(true, BASIC.createPredicate(WSML2DatalogTransformer.PRED_HAS_VALUE, 3), BASIC.createTuple(Y, Z, X));
//        final List<ILiteral> body = new ArrayList<ILiteral>();
//        final List<ILiteral> head = new ArrayList<ILiteral>();
//        // rules for member of string
//        head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_STRING))));
//        body.add(hasValue);
//        body.add(BASIC.createLiteral(true, new IsStringBuiltin(X)));
//        res.add(BASIC.createRule(head, body));
//        head.clear();
//        body.clear();
//        // rules for member of integer
//        head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_INTEGER))));
//        body.add(hasValue);
//        body.add(BASIC.createLiteral(true, new IsIntegerBuiltin(X)));
//        res.add(BASIC.createRule(head, body));
//        head.clear();
//        body.clear();
//        // rules for member of decimal
//        head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_DECIMAL))));
//        body.add(hasValue);
//        body.add(BASIC.createLiteral(true, new IsDecimalBuiltin(X)));
//        res.add(BASIC.createRule(head, body));
//        head.clear();
//        body.clear();
//        // rules for member of boolean
//        head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_BOOLEAN))));
//        body.add(hasValue);
//        body.add(BASIC.createLiteral(true, new IsBooleanBuiltin(X)));
//        res.add(BASIC.createRule(head, body));
//        head.clear();
//        body.clear();
//        // rules for member of date
//        head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_DATE))));
//        body.add(hasValue);
//        body.add(BASIC.createLiteral(true, new IsDateBuiltin(X)));
//        res.add(BASIC.createRule(head, body));
//        head.clear();
//        body.clear();
//        // rules for member of dateTime
//        head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X, CONCRETE.createIri(WsmlDataType.WSML_DATETIME))));
//        body.add(hasValue);
//        body.add(BASIC.createLiteral(true, new IsDateTimeBuiltin(X)));
//        res.add(BASIC.createRule(head, body));
//        head.clear();
//        body.clear();
//        return res;
//    }

    /**
     * Wrapper for the w2r datasource to the iris datasource.
     */
    private class IrisDataSource implements IDataSource {

        /** Predicate for the iris member-of facts. */
        private final IPredicate memberOf = BASIC.createPredicate(WSML2DatalogTransformer.PRED_MEMBER_OF, 2);

        /** Predicate for the iris has-value facts. */
        private final IPredicate hasValue = BASIC.createPredicate(WSML2DatalogTransformer.PRED_HAS_VALUE, 3);

        /** Data source from where to get the values from. */
        private final ExternalDataSource source;

        public IrisDataSource(final ExternalDataSource source) {
            if (source == null) {
                throw new IllegalArgumentException("The source must not be null");
            }
            this.source = source;
        }

        public void get(IPredicate p, ITuple from, ITuple to, IRelation r) {
            // TODO: from and to can't be used by iris atom, so we leave it out
            // for the moment
            if (p == null) {
                throw new IllegalArgumentException("The predicate must not be null");
            }
            if (r == null) {
                throw new IllegalArgumentException("The relation must not be null");
            }

            if (p.equals(memberOf)) {
                for (final MemberOf mo : source.memberOf(null, null)) {
                    r.add(BASIC.createTuple(convertTermFromWsmo4jToIris(mo.getId()), convertTermFromWsmo4jToIris(mo.getConcept())));
                }
            }
            else if (p.equals(hasValue)) {
                for (final HasValue hv : source.hasValue(null, null, null)) {
                    r.add(BASIC.createTuple(convertTermFromWsmo4jToIris(hv.getId()), convertTermFromWsmo4jToIris(hv.getName()), convertTermFromWsmo4jToIris(hv.getValue())));
                }
            }
        }
    }
    
    /**
     * 
     * 
     * @param terms
     * @return
     */
    private static ITerm[] toArray(List<ITerm> terms) {
    	ITerm[] array = new ITerm[terms.size()];
    	return terms.toArray(array);
    }
    
	/**
	 * Checks if a literal contains an equal statement.
	 * 
	 * @param literal
	 * @return true if the Literal contains an equal statement (equal,
	 *         equal_date,...), else false.
	 */
	private static boolean containsEqualBuiltin(Literal literal) {
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
		return false;
	}
	
	/**
	 *  Changes the order of the terms for IRIS. 
	 *  The first entry becomes the last one. 
	 *  http://www.w3.org/2005/rules/wg/wiki/List_of_functions_and_operators
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
