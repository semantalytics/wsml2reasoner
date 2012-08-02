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
import static org.wsml.reasoner.TransformerPredicates.PRED_HAS_VALUE;
import static org.wsml.reasoner.TransformerPredicates.PRED_MEMBER_OF;
import static org.wsml.reasoner.builtin.iris.BuiltinHelper.containsEqualBuiltin;
import static org.wsml.reasoner.builtin.iris.LiteralHelper.literal2Atom;
import static org.wsml.reasoner.builtin.iris.LiteralHelper.literal2Literal;
import static org.wsml.reasoner.builtin.iris.TermHelper.convertTermFromIrisToWsmo4j;
import static org.wsml.reasoner.builtin.iris.TermHelper.convertTermFromWsmo4jToIris;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.facts.IDataSource;
import org.deri.iris.querycontainment.QueryContainment;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DatalogReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.api.data.ExternalDataSource;
import org.wsml.reasoner.api.data.ExternalDataSource.HasValue;
import org.wsml.reasoner.api.data.ExternalDataSource.MemberOf;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsmo.factory.FactoryContainer;

/**
 * The base class for all facades based on the iris reasoner.
 */
public abstract class AbstractIrisFacade implements DatalogReasonerFacade {

	/**
	 * This is the key value this facade will look for to get the external data
	 * source. The value for this Map.Entry should be a map containing
	 * <code>ontologyUri(String)->Collection&lt;ExternalDataSource&gt;</code>.
	 */
	public static final String EXTERNAL_DATA_SOURCE = "iris.external.source";

	/** Factory Container */
	private final FactoryContainer factory;

	/** knowledge-base. */
	private org.deri.iris.api.IKnowledgeBase prog;

	private QueryContainment queryCont = null;

	/** Map that contains the variable mapping from the query containment check. */
	private org.deri.iris.storage.IRelation QCResult = new SimpleRelationFactory()
			.createRelation();

	/**
	 * The external data sources.
	 */
	private final Collection<ExternalDataSource> sources;

	public AbstractIrisFacade(final FactoryContainer factory,
			final Map<String, Object> config) {
		this.factory = factory;

		// retrieving the data source
		final Object ds = (config != null) ? config.get(EXTERNAL_DATA_SOURCE)
				: null;
		if ((ds != null) && (ds instanceof Collection<?>)) {
			sources = (Collection<ExternalDataSource>) ds;
		} else {
			sources = new ArrayList<ExternalDataSource>();
		}
	}

	public synchronized void deregister() throws ExternalToolException {
		prog = null;
	}

	public synchronized Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q)
			throws ExternalToolException {
		if (q == null) {
			throw new NullPointerException("The query must not be null");
		}
		if (prog == null) {
			throw new InternalReasonerException(
					"A program has not been registered");
		}

		// constructing the query -- i.e. rule with no head
		final List<ILiteral> body = new ArrayList<ILiteral>(q.getLiterals()
				.size());
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
		} catch (EvaluationException e2) {
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

				tmp.put((Variable) convertTermFromIrisToWsmo4j(variable,
						factory), convertTermFromIrisToWsmo4j(term, factory));
			}

			res.add(tmp);
		}

		return res;
	}

	public synchronized boolean checkQueryContainment(ConjunctiveQuery query1,
			ConjunctiveQuery query2) {
		if (query1 == null || query2 == null) {
			throw new NullPointerException("The queries must not be null");
		}
		if (prog == null) {
			throw new InternalReasonerException(
					"A program has not been registered");
		}

		// constructing query 1, the query to be frozen in IRIS
		final List<ILiteral> body = new ArrayList<ILiteral>(query1
				.getLiterals().size());

		// converting the literals of the query
		for (final Literal l : query1.getLiterals()) {
			body.add(literal2Literal(l, false));
		}

		final IQuery iQuery1 = BASIC.createQuery(body);

		// constructing query 2
		// final List<ILiteral> head2 = new ArrayList<ILiteral>(1);
		final List<ILiteral> body2 = new ArrayList<ILiteral>(query2
				.getLiterals().size());

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
		} catch (Exception e) {
			new ExternalToolException(e.getMessage());
		}

		return check;
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

			for (int i = 0; i < QCResult.size(); i++) {
				ITuple t = QCResult.get(i);
				final Map<Variable, Term> tmp = new HashMap<Variable, Term>();
				for (int pos = 0; pos < t.size(); pos++) {
					tmp.put((Variable) convertTermFromIrisToWsmo4j(
							variableBindings.get(pos), factory),
							convertTermFromIrisToWsmo4j(t.get(pos), factory));
				}
				result.add(tmp);
			}
			QCResult = new SimpleRelationFactory().createRelation();
			return result;
		}
		return new HashSet<Map<Variable, Term>>();

	}

	public synchronized void register(Set<Rule> kb)
			throws ExternalToolException {
		if (kb == null) {
			throw new IllegalArgumentException(
					"The knowledge base must not be null");
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
				// Check if the rule represents a fact with equality. If so, it
				// is transformed to a new rule, e.g. "a = b." is transformed to
				// "a = b :- true.".
				// TODO Maybe do this in the WSML2DatalogTransformer.
				if (containsEqualBuiltin(r.getHead())) {
					List<ILiteral> head = Collections.singletonList(BASIC
							.createLiteral(true, atom));

					List<ILiteral> body = Collections.singletonList(BASIC
							.createLiteral(true, BUILTIN.createTrue()));

					IRule newRule = BASIC.createRule(head, body);

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
		// for (final IRule r : getWsmlMemberOfRules()) {
		// rules.add(r);
		// }

		final Configuration configuration = org.deri.iris.KnowledgeBaseFactory
				.getDefaultConfiguration();

		// add the data sources
		for (final ExternalDataSource ext : sources) {
			configuration.externalDataSources.add(new IrisDataSource(ext));
		}

		configureIris(configuration);

		try {
			prog = org.deri.iris.KnowledgeBaseFactory.createKnowledgeBase(
					facts, rules, configuration);
		} catch (EvaluationException e) {

			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Create the IRIS configuration object.
	 * 
	 * @return
	 */
	protected abstract void configureIris(Configuration configuration);

	// ****** REMOVED. SEE BUG 2248622 **********
	// /**
	// * Returns the rules for the wsml-member-of rules.
	// *
	// * @return the wsml-member-of rules
	// */
	// private static Set<IRule> getWsmlMemberOfRules() {
	// final Set<IRule> res = new HashSet<IRule>();
	// final IPredicate WSML_MEBER_OF =
	// BASIC.createPredicate(WSML2DatalogTransformer.PRED_MEMBER_OF, 2);
	// final IVariable X = TERM.createVariable("X");
	// final IVariable Y = TERM.createVariable("Y");
	// final IVariable Z = TERM.createVariable("Z");
	// final ILiteral hasValue = BASIC.createLiteral(true,
	// BASIC.createPredicate(WSML2DatalogTransformer.PRED_HAS_VALUE, 3),
	// BASIC.createTuple(Y, Z, X));
	// final List<ILiteral> body = new ArrayList<ILiteral>();
	// final List<ILiteral> head = new ArrayList<ILiteral>();
	// // rules for member of string
	// head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X,
	// CONCRETE.createIri(WsmlDataType.WSML_STRING))));
	// body.add(hasValue);
	// body.add(BASIC.createLiteral(true, new IsStringBuiltin(X)));
	// res.add(BASIC.createRule(head, body));
	// head.clear();
	// body.clear();
	// // rules for member of integer
	// head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X,
	// CONCRETE.createIri(WsmlDataType.WSML_INTEGER))));
	// body.add(hasValue);
	// body.add(BASIC.createLiteral(true, new IsIntegerBuiltin(X)));
	// res.add(BASIC.createRule(head, body));
	// head.clear();
	// body.clear();
	// // rules for member of decimal
	// head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X,
	// CONCRETE.createIri(WsmlDataType.WSML_DECIMAL))));
	// body.add(hasValue);
	// body.add(BASIC.createLiteral(true, new IsDecimalBuiltin(X)));
	// res.add(BASIC.createRule(head, body));
	// head.clear();
	// body.clear();
	// // rules for member of boolean
	// head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X,
	// CONCRETE.createIri(WsmlDataType.WSML_BOOLEAN))));
	// body.add(hasValue);
	// body.add(BASIC.createLiteral(true, new IsBooleanBuiltin(X)));
	// res.add(BASIC.createRule(head, body));
	// head.clear();
	// body.clear();
	// // rules for member of date
	// head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X,
	// CONCRETE.createIri(WsmlDataType.WSML_DATE))));
	// body.add(hasValue);
	// body.add(BASIC.createLiteral(true, new IsDateBuiltin(X)));
	// res.add(BASIC.createRule(head, body));
	// head.clear();
	// body.clear();
	// // rules for member of dateTime
	// head.add(BASIC.createLiteral(true, WSML_MEBER_OF, BASIC.createTuple(X,
	// CONCRETE.createIri(WsmlDataType.WSML_DATETIME))));
	// body.add(hasValue);
	// body.add(BASIC.createLiteral(true, new IsDateTimeBuiltin(X)));
	// res.add(BASIC.createRule(head, body));
	// head.clear();
	// body.clear();
	// return res;
	// }

	/**
	 * Wrapper for the w2r datasource to the iris datasource.
	 */
	private class IrisDataSource implements IDataSource {

		/** Predicate for the iris member-of facts. */
		private final IPredicate memberOf = BASIC.createPredicate(
				PRED_MEMBER_OF, 2);

		/** Predicate for the iris has-value facts. */
		private final IPredicate hasValue = BASIC.createPredicate(
				PRED_HAS_VALUE, 3);

		/** Data source from where to get the values from. */
		private final ExternalDataSource source;

		public IrisDataSource(final ExternalDataSource source) {
			if (source == null) {
				throw new IllegalArgumentException(
						"The source must not be null");
			}
			this.source = source;
		}

		public void get(IPredicate p, ITuple from, ITuple to, IRelation r) {
			// TODO: from and to can't be used by iris atom, so we leave it out
			// for the moment
			if (p == null) {
				throw new IllegalArgumentException(
						"The predicate must not be null");
			}
			if (r == null) {
				throw new IllegalArgumentException(
						"The relation must not be null");
			}

			if (p.equals(memberOf)) {
				for (final MemberOf mo : source.memberOf(null, null)) {
					r.add(BASIC.createTuple(
							convertTermFromWsmo4jToIris(mo.getId()),
							convertTermFromWsmo4jToIris(mo.getConcept())));
				}
			} else if (p.equals(hasValue)) {
				for (final HasValue hv : source.hasValue(null, null, null)) {
					r.add(BASIC.createTuple(
							convertTermFromWsmo4jToIris(hv.getId()),
							convertTermFromWsmo4jToIris(hv.getName()),
							convertTermFromWsmo4jToIris(hv.getValue())));
				}
			}
		}
	}
}
