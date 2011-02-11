/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/**
 * 
 * A Example class for reasoning. SOA4ALL Reasoning Framework Report July 2010
 * 
 */
public class Example {

	public static void main(String[] args) throws IOException, ParserException,
			InvalidModelException, InconsistencyException {
		
		// Create a parser and parse the example ontology file.
		// For simplicity we do not take care of exceptions at the moment.
		Parser wsmlParser = new WsmlParser();
		InputStream is = wsmlParser.getClass().getClassLoader()
				.getResourceAsStream("example/instance-equality.wsml");
		TopEntity[] identifiable = wsmlParser.parse(new InputStreamReader(is));

		// We can be sure here, that we only parse a single ontology.
		Ontology ontology = (Ontology) identifiable[0];

		// Create a query, that should bind x to both instances A and B.
		String queryString = "p(?x)";

		// Define the desired reasoner by setting the corresponding values in
		// the parameters. Here IRIS reasoner with well-founded semantics is
		// used.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.IRIS_WELL_FOUNDED);

		
		// Instantiate the desired reasoner using the default reasoner factory.
		LPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
				.createRuleReasoner(params);

		// Register the ontology.
		reasoner.registerOntology(ontology);

		// Transform the query in string form to a logical expression object.
		LogicalExpression query = new WsmlLogicalExpressionParser(ontology)
				.parse(queryString);

		// Execute query request and assign the result to 'bindings'
		Set<Map<Variable, Term>> bindings = reasoner.executeQuery(query);

		System.out.println(bindings);
	}

}
