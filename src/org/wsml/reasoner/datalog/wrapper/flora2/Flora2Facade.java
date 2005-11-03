/**
 * WSML Reasoner Implementation based on FLORA2.
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

package org.wsml.reasoner.datalog.wrapper.flora2;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsml.reasoner.datalog.ConjunctiveQuery;
import org.wsml.reasoner.datalog.DataTypeValue;
import org.wsml.reasoner.datalog.DatalogException;
import org.wsml.reasoner.datalog.Literal;
import org.wsml.reasoner.datalog.Program;
import org.wsml.reasoner.datalog.QueryResult;
import org.wsml.reasoner.datalog.Rule;
import org.wsml.reasoner.datalog.Term;
import org.wsml.reasoner.datalog.Variable;
import org.wsml.reasoner.datalog.wrapper.DatalogReasonerFacade;
import org.wsml.reasoner.datalog.wrapper.DefaultSymbolFactory;
import org.wsml.reasoner.datalog.wrapper.ExternalToolException;
import org.wsml.reasoner.datalog.wrapper.SymbolFactory;
import org.wsml.reasoner.datalog.wrapper.SymbolMap;
import org.wsml.reasoner.datalog.wrapper.UnsupportedFeatureException;
import org.wsml.reasoner.impl.VariableBindingImpl;

import com.declarativa.interprolog.TermModel;
import com.ontotext.flora2.XSBFlora;

/**
 * Integrates the Flora2 system into the WSML Core/Flight Reasoner framework for
 * query answering.
 * 
 * @author Ioan Toma, DERI Innsbruck, Austria
 */
public class Flora2Facade implements DatalogReasonerFacade {

	private Map<String, StringBuffer> registeredKbs = new HashMap<String, StringBuffer>();

	private Logger logger = Logger
			.getLogger("org.wsml.reasoner.wsmlcore.wrapper.flora2");
	{
		logger.setLevel(Level.OFF);
	}

	private org.wsml.reasoner.datalog.Query query;

	private XSBFlora floraReasoner = null;

	private String[] queryVarNamesSequence;

	private SymbolMap symbTransfomer = new SymbolMap(new DefaultSymbolFactory());

	static final String RESULT_PREDICATE_NAME = "flora2_result";

	private StringBuffer floraProgText = new StringBuffer();

	public XSBFlora getFlora2Reasoner() {
		return floraReasoner;
	}

	/**
	 * Evaluates a Query on a Datalog knowledgebase
	 */
	public QueryResult evaluate(ConjunctiveQuery q, String ontologyUri)
			throws ExternalToolException {

		QueryResult result = new QueryResult(q);
		try {
			query = q;

			// Translate the query itself
			String queryStr = translateQuery(q);

			if (queryStr.indexOf('.') != -1) {
				queryStr = queryStr.replace('.', ' ');
			}
			Object[] bindings = floraReasoner.FLogicCommand(queryStr + ".");

			// show results
			for (int i = 0; i < bindings.length; i++) {
				VariableBinding newVarBinding = new VariableBindingImpl();
				VariableBinding resBinding = new VariableBindingImpl();

				TermModel tm = (TermModel) bindings[i];

				resBinding = processTermModel(tm);

				Iterator it = resBinding.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next().toString();
					String varName = symbTransfomer.convertToWSML(key);
					String varValue = symbTransfomer.convertToWSML(resBinding
							.get(key));
					newVarBinding.put(varName, varValue);
					result.getVariableBindings().add(newVarBinding);
				}
			}
		} catch (UnsupportedFeatureException e) {
			e.printStackTrace();
		}
		return result;
	}

	private VariableBinding processTermModel(TermModel tm) {
		VariableBinding result = new VariableBindingImpl();
		String str = tm.toString();
		str = str.substring(1, str.length() - 1);
		StringTokenizer strToken = new StringTokenizer(str, ",");
		while (strToken.hasMoreTokens()) {
			String element = strToken.nextToken();
			if (element.charAt(0) == '('
					&& element.charAt(element.length() - 1) == ')') {
				element = element.substring(1, element.length() - 1);
			}
			StringTokenizer strToken1 = new StringTokenizer(element, "=");
			result.put(strToken1.nextToken(), strToken1.nextToken());
		}
		return result;
	}

	/**
	 * Translate a knowledgebase
	 * 
	 * @param p -
	 *            the datalog program that constitutes the knowledgebase
	 */
	private String translateKnowledgebase(org.wsml.reasoner.datalog.Program p)
			throws ExternalToolException {
		logger.info("Translate knowledgebase :\n" + p);
		String result = "";
		if (p == null) {
			logger.info("KB is not referenced. Assume empty KB.");
			return result;
		}

		for (org.wsml.reasoner.datalog.Rule r : p) {
			result += translateRule(r);
		}
		logger.info("---- The FLORA2 program is:\n" + floraProgText);

		return result;
	}

	/**
	 * Translate the query
	 * 
	 * @param p -
	 *            the datalog program that constitutes the knowledgebase
	 */
	private String translateQuery(ConjunctiveQuery q)
			throws ExternalToolException {
		logger.info("Translate query :" + q);
		String result = "";
		// Derive and store the sequence of variables that defines the output
		// tuples from the query
		List<Variable> bodyVars = q.getVariables();
		queryVarNamesSequence = new String[bodyVars.size()];
		Term[] predArgs = new Term[bodyVars.size()];
		int i = 0;
		for (Variable v : bodyVars) {
			queryVarNamesSequence[i] = v.getSymbol();
			predArgs[i] = v;
			i++;
		}

		logger.log(Level.FINE, "Sequence of variables in query is: ",
				queryVarNamesSequence);

		try {

			Literal head = new Literal(new org.wsml.reasoner.datalog.Predicate(
					RESULT_PREDICATE_NAME, queryVarNamesSequence.length),
					predArgs);

			Rule resultDef = new Rule(head, q.getLiterals());
			logger.info("Converted query to rule:" + resultDef);

			result = translateBody(resultDef);

		} catch (DatalogException d) {
			// Never happens
			d.printStackTrace();
		}
		return result;
	}

	/**
	 * Translate the body of a datalog rule
	 */
	private String translateBody(Rule r) throws ExternalToolException {
		String result = "";

		int i = 1;
		List<Literal> body = r.getBody();
		for (Literal bl : body) {
			result += translateLiteral(bl);
			if (i < body.size()) {
				result += ", ";
			}
			i++;
		}

		result += ".";
		return result;
	}

	/**
	 * Translate a datalog rule
	 */
	private String translateRule(Rule r) throws ExternalToolException {

		String result = "";

		if (r.isFact()) {
			result = translateLiteral(r.getHead());
		} else if (r.isConstraint()) {
			result = ":- ";
		} else {
			// normal rule with head and body: care about head
			result += translateLiteral(r.getHead());
			result += " :- ";
		}

		// Care about body
		int i = 1;
		List<Literal> body = r.getBody();
		for (Literal bl : body) {
			result += translateLiteral(bl);
			if (i < body.size()) {
				result += ", ";
			}
			i++;
		}

		result += ".";

		floraProgText.append(result + "\n");

		return result;
	}

	private String translateLiteral(org.wsml.reasoner.datalog.Literal l)
			throws ExternalToolException {
		if (l == null)
			return null;

		String result = "";

		Term[] args = l.getArguments();

		if (l.getSymbol().getSymbolName()
				.equalsIgnoreCase("wsml-subconcept-of")) {
			result += translateTerm(args[0]) + "::" + translateTerm(args[1]);
		} else

		if (l.getSymbol().getSymbolName().equalsIgnoreCase("wsml-member-of")) {
			result += translateTerm(args[0]) + ":" + translateTerm(args[1]);
		} else

		if (l.getSymbol().getSymbolName().equalsIgnoreCase("wsml-of-type")) {
			result += translateTerm(args[0]) + "[" + translateTerm(args[1])
					+ "=>>" + translateTerm(args[2]) + "]";
		} else

		if (l.getSymbol().getSymbolName().equalsIgnoreCase("wsml-implies-type")) {
			result += translateTerm(args[0]) + "[" + translateTerm(args[1])
					+ "*=>>" + translateTerm(args[2]) + "]";
		} else

		if (l.getSymbol().getSymbolName().equalsIgnoreCase("wsml-has-value")) {
			result += translateTerm(args[0]) + "[" + translateTerm(args[1])
					+ "->>" + translateTerm(args[2]) + "]";
		} else {

			try {
				result += symbTransfomer.convertToTool(l.getSymbol());
				result += "(";

				int i = 1;

				for (Term arg : args) {
					result += translateTerm(arg);
					if (i < args.length) {
						result += ", ";
					}
					i++;
				}
				result += ")";

			} catch (UnsupportedFeatureException ufe) {
				throw new ExternalToolException(
						"Can not convert query to tool: " + l.toString(), ufe,
						query);
			}
		}
		return result;
	}

	private String translateTerm(Term t) throws ExternalToolException {
		String result = "";
		try {
			if (t.getClass().equals(org.wsml.reasoner.datalog.Variable.class)) {
				result += symbTransfomer
						.convertToTool((org.wsml.reasoner.datalog.Variable) t);
			}
			if (t.getClass().equals(org.wsml.reasoner.datalog.Constant.class)) {
				result += symbTransfomer
						.convertToTool((org.wsml.reasoner.datalog.Constant) t);
			}
			if (t.getClass().equals(
					org.wsml.reasoner.datalog.DataTypeValue.class)) {
				result += symbTransfomer.convertToTool((DataTypeValue) t);
			}
		} catch (UnsupportedFeatureException ufe) {
			throw new ExternalToolException("Can not convert query to tool: "
					+ t.toString(), ufe, query);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.wsmlcore.wrapper.DatalogReasonerFacade#useSymbolFactory(org.wsml.reasoner.wsmlcore.wrapper.SymbolFactory)
	 */
	public void useSymbolFactory(SymbolFactory sf) {
		// do nothing
	}

	private String getPathToXsbBin(String path) {
		String result = "";
		// test if is windows or not
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
			result = path + File.separator + "config" + File.separator
					+ "i686-pc-linux-gnu" + File.separator + "bin";
		} else {
			result = path + File.separator + "config" + File.separator
					+ "x86-pc-windows" + File.separator + "bin";
		}
		return result;
	}

	public void register(String URI, Program kb) throws ExternalToolException {
		logger.info(" REGISTER: " + URI);
		if (floraReasoner == null) {
			Flora2Settings flora2Settings = new Flora2Settings();
			String pathToXSB = flora2Settings.getXsbPath();
			XSBFlora.sFloraRootDir = flora2Settings.getFloraPath();
			floraReasoner = new XSBFlora(getPathToXsbBin(pathToXSB));
		}
		try {
			String rules = translateKnowledgebase(kb);
			registeredKbs.put(URI, new StringBuffer(rules));
			loadProgramInFlora2(rules);
		} catch (Exception e) {
			throw new ExternalToolException("Cannot register entity in FLORA2",
					e, null);
		}
	}

	public void unregister(String URI) throws ExternalToolException {
		logger.info(" UREGISTER: " + URI);
		if (floraReasoner == null) {
			Flora2Settings flora2Settings = new Flora2Settings();
			String pathToXSB = flora2Settings.getXsbPath();
			XSBFlora.sFloraRootDir = flora2Settings.getFloraPath();
			floraReasoner = new XSBFlora(getPathToXsbBin(pathToXSB));
		}
		try {
			StringBuffer kb = new StringBuffer();
			kb = registeredKbs.get(URI);
			if (kb != null) {
				logger.info("The program deleted from FLORA2 is:\n" + kb);
				unloadProgramFromFlora2(kb.toString());
				registeredKbs.remove(URI);
			}
		} catch (Exception e) {
			throw new ExternalToolException(
					"Cannot unregister entity from FLORA2", e, null);
		}
	}

	/**
	 * Insert the set of rules into Flora2
	 * 
	 * @param rules
	 */
	private void loadProgramInFlora2(String rules) {

		StringTokenizer strToken = new StringTokenizer(rules, ".");

		while (strToken.hasMoreTokens()) {
			String rule = strToken.nextToken();

			if (rule.indexOf(":-") != -1) {
				// TODO constraints are ignored for the moment
				if (rule.indexOf(":-") == 0) {
					break;
				}
				// if is a rule
				floraReasoner.FLogicCommand("insertrule_a{" + rule + "}.");
				// logger.info("Execute command: insertrule_a{" + rule + "}.");
			} else {
				// if is a fact
				floraReasoner.FLogicCommand("insert{" + rule + "}.");
				// logger.info("Execute command: insert{" + rule + "}.");
			}
		}
	}

	/**
	 * Retract the set of rules from Flora2
	 * 
	 * @param rules
	 */
	private void unloadProgramFromFlora2(String rules) {

		StringTokenizer strToken = new StringTokenizer(rules, ".");

		while (strToken.hasMoreTokens()) {
			String rule = strToken.nextToken();

			if (rule.indexOf(":-") != -1) {
				// TODO constraint are ignored for the moment
				if (rule.indexOf(":-") == 0) {
					break;
				}
				// if is a rule
				floraReasoner.FLogicCommand("deleterule_a{" + rule + "}.");
				// logger.info("Execute command: deleterule_a{" + rule + "}.");
			} else {
				// if is a fact
				floraReasoner.FLogicCommand("delete{" + rule + "}.");
				// logger.info("Execute command: delete{" + rule + "}.");
			}
		}
	}

}
