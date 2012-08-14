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
package org.wsml.reasoner.builtin.streamingiris;

import static at.sti2.streamingiris.factory.Factory.BASIC;
import static org.wsml.reasoner.TransformerPredicates.PRED_HAS_VALUE;
import static org.wsml.reasoner.TransformerPredicates.PRED_MEMBER_OF;
import static org.wsml.reasoner.TransformerPredicates.PRED_SUB_CONCEPT_OF;
import static org.wsml.reasoner.builtin.streamingiris.LiteralHelper.literal2Atom;
import static org.wsml.reasoner.builtin.streamingiris.LiteralHelper.literal2Literal;
import static org.wsml.reasoner.builtin.streamingiris.TermHelper.convertTermFromIrisToWsmo4j;
import static org.wsml.reasoner.builtin.streamingiris.TermHelper.convertTermFromWsmo4jToIris;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.deri.wsmo4j.io.serializer.wsml.SerializeWSMLTermsVisitor;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.StreamingDatalogReasonerFacade;
import org.wsml.reasoner.api.data.ExternalDataSource;
import org.wsml.reasoner.api.data.ExternalDataSource.HasValue;
import org.wsml.reasoner.api.data.ExternalDataSource.MemberOf;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstraintReplacementNormalizer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.InverseImplicationNormalizer;
import org.wsml.reasoner.transformation.LloydToporNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.factory.FactoryContainer;

import at.sti2.streamingiris.Configuration;
import at.sti2.streamingiris.EvaluationException;
import at.sti2.streamingiris.KnowledgeBaseFactory;
import at.sti2.streamingiris.api.IKnowledgeBase;
import at.sti2.streamingiris.api.basics.IAtom;
import at.sti2.streamingiris.api.basics.ILiteral;
import at.sti2.streamingiris.api.basics.IPredicate;
import at.sti2.streamingiris.api.basics.IQuery;
import at.sti2.streamingiris.api.basics.IRule;
import at.sti2.streamingiris.api.basics.ITuple;
import at.sti2.streamingiris.api.builtins.IBuiltinAtom;
import at.sti2.streamingiris.api.terms.ITerm;
import at.sti2.streamingiris.api.terms.IVariable;
import at.sti2.streamingiris.builtins.TrueBuiltin;
import at.sti2.streamingiris.facts.IDataSource;
import at.sti2.streamingiris.querycontainment.QueryContainment;
import at.sti2.streamingiris.storage.IRelation;
import at.sti2.streamingiris.storage.simple.SimpleRelationFactory;

/**
 * The base class for all facades based on the iris reasoner.
 */
public abstract class AbstractStreamingIrisFacade implements
		StreamingDatalogReasonerFacade {

	Logger logger = Logger.getLogger(getClass());

	/**
	 * This is the key value this facade will look for to get the external data
	 * source. The value for this Map.Entry should be a map containing
	 * <code>ontologyUri(String)->Collection&lt;ExternalDatasource&gt;</code>.
	 */
	public static final String EXTERNAL_DATA_SOURCE = "iris.external.source";

	/** Factory Container */
	private final FactoryContainer factory;

	/** knowledge-base. */
	private IKnowledgeBase prog;

	/** The knowledge base that is used when the reasoner will be started. */
	private Set<Rule> knowledgeBase;

	private QueryContainment queryCont = null;

	/** Map that contains the variable mapping from the query containment check. */
	private IRelation QCResult = new SimpleRelationFactory().createRelation();

	/**
	 * The external data sources.
	 */
	private final Collection<ExternalDataSource> sources;

	private static final boolean DO_THE_QC_HACK = true;

	/**
	 * This thread listens for input that is streamed.
	 */
	private StreamingIrisInputServer inputThread;

	/**
	 * This map links the query with the thread that is listening for the
	 * results of this query coming from streaming iris.
	 */
	private Map<IQuery, StreamingIrisOutputServer> streamingIrisOutputServers;

	/**
	 * This map links the query to the sockets that are listening for results.
	 */
	private Map<IQuery, List<String>> queryListenerMap;

	/**
	 * This map links the sockets with the output streamer that are sending the
	 * results to the socket.
	 */
	private Map<String, OutputStreamer> outputStreamers;

	private long executionInterval;

	private Ontology ontology;

	private Map<IQuery, LogicalExpression> queryMap;

	public AbstractStreamingIrisFacade(final FactoryContainer factory,
			final Map<String, Object> config) {
		this.factory = factory;

		// retrieving the data source
		final Object ds = (config != null) ? config.get(EXTERNAL_DATA_SOURCE)
				: null;
		if ((ds != null) && (ds instanceof Collection)) {
			sources = (Collection<ExternalDataSource>) ds;
		} else {
			sources = new ArrayList<ExternalDataSource>();
		}

		knowledgeBase = new HashSet<Rule>();
		queryListenerMap = new HashMap<IQuery, List<String>>();
		streamingIrisOutputServers = new HashMap<IQuery, StreamingIrisOutputServer>();
		outputStreamers = new HashMap<String, OutputStreamer>();
		queryMap = new HashMap<IQuery, LogicalExpression>();
	}

	@Override
	public synchronized void startReasoner(Ontology ontology,
			Map<String, Object> configuration) throws ExternalToolException {
		this.ontology = ontology;
		Set<Rule> kb = convertOntologyToKnowledgeBase(ontology);

		startReasoner(kb, configuration);
	}

	private Set<Rule> convertOntologyToKnowledgeBase(Ontology ontology) {
		Set<Entity> entities = new HashSet<Entity>();

		entities.addAll(ontology.listConcepts());
		entities.addAll(ontology.listInstances());
		entities.addAll(ontology.listRelations());
		entities.addAll(ontology.listRelationInstances());
		entities.addAll(ontology.listAxioms());

		Set<Rule> kb = new HashSet<Rule>();
		kb.addAll(convertEntities(entities));
		return kb;
	}

	private synchronized void startReasoner(Set<Rule> kb,
			Map<String, Object> configuration) throws ExternalToolException {
		if (kb != null) {
			knowledgeBase = kb;
		}

		if (prog != null) {
			throw new ExternalToolException("Reasoner already running!");
		}

		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		List<IRule> rules = new ArrayList<IRule>();

		// translating all the rules
		for (final Rule r : knowledgeBase) {
			/*
			 * FIXME Implement this in a better way.
			 * 
			 * The following code is an evil quick hack to get query containment
			 * working. More precisely, to get the functional test
			 * AbstractQueryContainment1.testSubclassQueryContainment working.
			 */
			if (r.getHead().getPredicateUri().equals(PRED_SUB_CONCEPT_OF)
					&& r.isFact() && DO_THE_QC_HACK) {
				final List<ILiteral> head = new ArrayList<ILiteral>(1);
				final List<ILiteral> body = new ArrayList<ILiteral>(r.getBody()
						.size());
				// converting the head of the rule
				head.add(literal2Literal(r.getHead(), true));
				// converting the body of the rule
				IBuiltinAtom trueAtom = new TrueBuiltin(new ITerm[0]);
				ILiteral trueLiteral = BASIC.createLiteral(true, trueAtom);
				body.add(trueLiteral);
				rules.add(BASIC.createRule(head, body));
				/* </hack> */
				// logger.debug("Added RULE: " + BASIC.createRule(head, body));
			} else if (r.isFact()) { // the rule is a fact
				IAtom atom = literal2Atom(r.getHead(), true);
				IPredicate pred = atom.getPredicate();

				IRelation relation = facts.get(atom.getPredicate());
				if (relation == null) {
					relation = new SimpleRelationFactory().createRelation();
					facts.put(pred, relation);
				}
				relation.add(atom.getTuple());
				// logger.debug("Added FACT: " + atom.getPredicate() + " "
				// + atom.getTuple());
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
				// logger.debug("Added RULE: " + BASIC.createRule(head, body));
			}
		}
		// add the wsml-member-of rules for primitive data types
		// Removed. See bug 2248622
		// for (final IRule r : getWsmlMemberOfRules()) {
		// rules.add(r);
		// }

		final Configuration kbConfiguration = KnowledgeBaseFactory
				.getDefaultConfiguration();

		// add the data sources
		for (final ExternalDataSource ext : sources) {
			kbConfiguration.externalDataSources
					.add(new StreamingIrisDataSource(ext));
		}

		configureStreamingIris(kbConfiguration);

		this.executionInterval = kbConfiguration.executionIntervallMilliseconds;

		try {
			prog = KnowledgeBaseFactory.createKnowledgeBase(facts, rules,
					kbConfiguration);
		} catch (EvaluationException e) {

			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		int inputPort = 0;
		if (configuration.containsKey("inputPort")) {
			inputPort = (Integer) configuration.get("inputPort");
		}

		inputThread = new StreamingIrisInputServer(this, factory, inputPort);
		inputThread.start();
	}

	public synchronized void deregister() throws ExternalToolException {
		knowledgeBase = new HashSet<Rule>();
	}

	public void shutdownReasoner() {
		// Shut down the reasoner.
		prog.shutdown();

		// Shut down the input streamer.
		inputThread.shutdown();

		// Shut down the output streamers.
		for (StreamingIrisOutputServer streamer : streamingIrisOutputServers
				.values()) {
			streamer.interrupt();
			// if (!streamer.shutdown())
			// logger.error("IIrisOutputStreamer could not be shut down!");
			// else
			// logger.info("IIrisOutputStreamer shut down!");
		}

	}

	public void register(Set<Rule> kb) throws ExternalToolException {
		knowledgeBase = kb;
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

		IRelation executionResult;
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

	public synchronized void registerQueryListener(
			ConjunctiveQuery datalogQuery, LogicalExpression query,
			String host, int port) {
		if (datalogQuery == null) {
			throw new NullPointerException("The query must not be null");
		}
		if (prog == null) {
			throw new InternalReasonerException(
					"A program has not been registered");
		}

		// constructing the query -- i.e. rule with no head
		final List<ILiteral> body = new ArrayList<ILiteral>(datalogQuery
				.getLiterals().size());
		// converting the literals of the query
		for (final Literal l : datalogQuery.getLiterals()) {
			body.add(literal2Literal(l, false));
		}

		// create query
		final IQuery q = BASIC.createQuery(body);

		queryMap.put(q, query);

		String hostPortPair = host + ":" + port;

		if (!queryListenerMap.containsKey(q)) {
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add(hostPortPair);
			queryListenerMap.put(q, arrayList);
			StreamingIrisOutputServer streamingIrisOutputServer = new StreamingIrisOutputServer(
					this, q, prog, executionInterval);
			streamingIrisOutputServer.start();
			streamingIrisOutputServers.put(q, streamingIrisOutputServer);
		} else {
			queryListenerMap.get(q).add(hostPortPair);
		}

		OutputStreamer outputStreamer = new OutputStreamer(host, port);
		outputStreamers.put(hostPortPair, outputStreamer);
	}

	public void sendResults(IQuery query, IRelation result,
			List<IVariable> variableBindings) {
		if (queryListenerMap.containsKey(query)) {
			final Set<Map<Variable, Term>> res = new HashSet<Map<Variable, Term>>();

			for (int i = 0; i < result.size(); ++i) {
				ITuple t = result.get(i);

				assert t.size() == variableBindings.size();

				Map<Variable, Term> tmp = new HashMap<Variable, Term>();

				for (int pos = 0; pos < t.size(); ++pos) {
					IVariable variable = variableBindings.get(pos);
					ITerm term = t.get(pos);

					tmp.put((Variable) convertTermFromIrisToWsmo4j(variable,
							factory),
							convertTermFromIrisToWsmo4j(term, factory));
				}

				res.add(tmp);
			}

			StringBuilder results = new StringBuilder();
			String queryString = queryMap.get(query).toString();
			String resultString;

			for (Map<Variable, Term> vBinding : res) {
				resultString = queryString;
				for (Variable var : vBinding.keySet()) {
					String value = termToString(vBinding.get(var));
					resultString = resultString.replace(var.toString(), value);
				}
				results.append(resultString);
				results.append("\n");
			}

			String stream = results.toString();

			for (String pair : queryListenerMap.get(query)) {
				outputStreamers.get(pair).stream(stream);
			}
		}

	}

	public void sendResults(IQuery query, Map<IPredicate, IRelation> facts) {
		IRelation relation;
		IPredicate predicate;

		// convert facts to wsml to string
		for (Entry<IPredicate, IRelation> entry : facts.entrySet()) {
			relation = entry.getValue();
			predicate = entry.getKey();

			for (int i = 0; i < relation.size(); i++) {
				logger.debug(predicate + " " + relation.get(i));
			}

			logger.debug("IRelation: " + relation);

			final Set<Map<Variable, Term>> res = new HashSet<Map<Variable, Term>>();

			// for (int i = 0; i < relation.size(); ++i) {
			// ITuple t = relation.get(i);
			//
			// assert t.size() == variableBindings.size();
			//
			// Map<Variable, Term> tmp = new HashMap<Variable, Term>();
			//
			// for (int pos = 0; pos < t.size(); ++pos) {
			// IVariable variable = variableBindings.get(pos);
			// ITerm term = t.get(pos);
			//
			// tmp.put((Variable) convertTermFromIrisToWsmo4j(variable,
			// factory),
			// convertTermFromIrisToWsmo4j(term, factory));
			// }
			//
			// res.add(tmp);
			// }

			// TermHelper.convertTermFromIrisToWsmo4j(t, factory);
		}

		if (queryListenerMap.containsKey(query)) {
			for (String pair : queryListenerMap.get(query)) {
				outputStreamers.get(pair).stream(facts.toString());
			}
		}
	}

	private String termToString(Term t) {
		SerializeWSMLTermsVisitor v = new SerializeWSMLTermsVisitor(ontology);
		t.accept(v);
		return v.getSerializedObject();
	}

	public void deregisterQueryListener(ConjunctiveQuery q, String host,
			int port) {
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

		String hostPortString = host + ":" + port;

		// stop the output streamer
		if (outputStreamers.containsKey(hostPortString)) {
			synchronized (outputStreamers) {
				outputStreamers.get(hostPortString).shutdown();
				outputStreamers.remove(hostPortString);
			}
		}

		// remove the hostPortString from the list of listeners
		if (queryListenerMap.containsKey(query)) {
			List<String> list = queryListenerMap.get(query);
			if (list.contains(hostPortString)) {
				list.remove(hostPortString);
				if (list.isEmpty()) {
					synchronized (queryListenerMap) {
						queryListenerMap.remove(query);
					}
					synchronized (streamingIrisOutputServers) {
						// streamingIrisOutputServers.get(query).shutdown();
						streamingIrisOutputServers.get(query).interrupt();
						streamingIrisOutputServers.remove(query);
					}
					queryMap.remove(query);
				}
			}
		}
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

	public void addFacts(Set<Rule> kb) {
		Map<IPredicate, IRelation> facts = getFactsFromKnowledgeBase(kb);

		logger.debug("Adding facts:");
		for (Entry<IPredicate, IRelation> fact : facts.entrySet()) {
			logger.debug(fact);
		}

		try {
			prog.addFacts(facts);
		} catch (EvaluationException e) {
			e.printStackTrace();
		}
	}

	public void addFacts(Ontology ontology) {
		Set<Rule> kb = convertOntologyToKnowledgeBase(ontology);
		addFacts(kb);
	}

	public Map<IPredicate, IRelation> getFactsFromOntology(Ontology ontology) {
		Set<Rule> kb = convertOntologyToKnowledgeBase(ontology);
		return getFactsFromKnowledgeBase(kb);
	}

	private Map<IPredicate, IRelation> getFactsFromKnowledgeBase(Set<Rule> kb) {
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();

		// translating all the facts
		for (final Rule r : kb) {
			if (r.isFact()) { // the rule is a fact
				IAtom atom = literal2Atom(r.getHead(), true);
				IPredicate pred = atom.getPredicate();

				IRelation relation = facts.get(atom.getPredicate());
				if (relation == null) {
					relation = new SimpleRelationFactory().createRelation();
					facts.put(pred, relation);
				}
				relation.add(atom.getTuple());
			}
		}

		return facts;
	}

	private Set<Rule> convertEntities(Set<Entity> theEntities) {
		Set<Rule> p = new HashSet<Rule>();

		// We don't do this any more.
		// Set<Entity> entities = handleAttributeInheritance(theEntities);
		Set<Entity> entities = theEntities;

		// Convert conceptual syntax to logical expressions
		OntologyNormalizer normalizer = new AxiomatizationNormalizer(factory);
		entities = normalizer.normalizeEntities(entities);

		Set<Axiom> axioms = new HashSet<Axiom>();
		for (Entity e : entities) {
			if (e instanceof Axiom) {
				axioms.add((Axiom) e);
			}
		}

		// Convert constraints to support debugging
		normalizer = new ConstraintReplacementNormalizer(factory);
		axioms = normalizer.normalizeAxioms(axioms);

		// Simplify axioms
		normalizer = new ConstructReductionNormalizer(factory);
		axioms = normalizer.normalizeAxioms(axioms);

		// Apply InverseImplicationTransformation (wsml-rule)
		normalizer = new InverseImplicationNormalizer(factory);
		axioms = normalizer.normalizeAxioms(axioms);

		// Apply Lloyd-Topor rules to get Datalog-compatible LEs
		normalizer = new LloydToporNormalizer(factory);
		axioms = normalizer.normalizeAxioms(axioms);

		org.wsml.reasoner.WSML2DatalogTransformer wsml2datalog = new org.wsml.reasoner.WSML2DatalogTransformer(
				factory);
		Set<org.omwg.logicalexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logicalexpression.LogicalExpression>();
		for (Axiom a : axioms) {
			lExprs.addAll(a.listDefinitions());
		}

		p = wsml2datalog.transform(lExprs);
		p.addAll(wsml2datalog.generateAuxilliaryRules());

		return p;
	}

	public int getInputPort() {
		if (inputThread.isAlive()) {
			return inputThread.getPort();
		} else {
			return 0;
		}
	}

	public FactoryContainer getFactory() {
		return factory;
	}

	/**
	 * Create the IRIS configuration object.
	 * 
	 * @return
	 */
	protected abstract void configureStreamingIris(Configuration configuration);

	/**
	 * Wrapper for the w2r datasource to the iris datasource.
	 */
	private class StreamingIrisDataSource implements IDataSource {

		/** Predicate for the iris member-of facts. */
		private final IPredicate memberOf = BASIC.createPredicate(
				PRED_MEMBER_OF, 2);

		/** Predicate for the iris has-value facts. */
		private final IPredicate hasValue = BASIC.createPredicate(
				PRED_HAS_VALUE, 3);

		/** Datasource from where to get the values from. */
		private final ExternalDataSource source;

		public StreamingIrisDataSource(final ExternalDataSource source) {
			if (source == null) {
				throw new IllegalArgumentException(
						"The source must not be null");
			}
			this.source = source;
		}

		public void get(IPredicate p, ITuple from, ITuple to, IRelation r) {
			// TODO: from and to can't be used by iris atm, so we leave it out
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
