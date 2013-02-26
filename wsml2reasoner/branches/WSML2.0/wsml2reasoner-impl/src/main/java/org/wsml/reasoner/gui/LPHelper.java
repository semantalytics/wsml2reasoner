package org.wsml.reasoner.gui;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.wsml.ParserException;

/**
 * Helper utilities for DL based tests.
 */
public class LPHelper {

	public static Set<Map<Variable, Term>> executeQuery(Ontology ontology,
			String query, LPReasoner reasoner) throws ParserException,
			InconsistencyException {
		Set<Ontology> ontologies = new HashSet<Ontology>();
		ontologies.add(ontology);

		return executeQuery(ontologies, query, reasoner);
	}

	public static Set<Map<Variable, Term>> executeQuery(
			Set<Ontology> ontologies, String query, LPReasoner reasoner)
			throws ParserException, InconsistencyException {
		reasoner.registerOntologies(ontologies);

		LogicalExpression qExpression = new WsmlLogicalExpressionParser(
				ontologies.iterator().next()).parse(query);

		// System.out.println("Executing query string '" + query + "'");
		// System.out.println("Executing query LE: '" + OntologyHelper.toString(
		// ontology, qExpression ) + "'");

		return reasoner.executeQuery(qExpression);
	}

}
