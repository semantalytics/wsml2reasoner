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
package example;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.StreamingLPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Norbert Lanzanasto, STI Innsbruck
 */
public class StreamingIrisReasonerExample {

	/**
	 * @param args
	 *            none expected
	 */
	public static void main(String[] args) {
		StreamingIrisReasonerExample ex = new StreamingIrisReasonerExample();
		try {
			ex.doTestRun();
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void doTestLoad() throws InterruptedException {
		Ontology ontology = loadOntology("simpleAxiomOntology.wsml");

		// Define the desired reasoner by setting the corresponding values in
		// the parameters. Here IRIS reasoner with well-founded semantics is
		// used.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
				WSMLReasonerFactory.BuiltInReasoner.STREAMING_IRIS_STRATIFIED);

		// Instantiate the desired reasoner using the default reasoner factory.
		StreamingLPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
				.createStreamingFlightReasoner(params);

		// create the configuration
		Map<String, Object> configuration = new HashMap<String, Object>();

		// Start reasoner
		reasoner.startReasoner(ontology, configuration);

		Thread.sleep(5000);

		// do not forget to shut down the reasoner
		reasoner.shutdownReasoner();
	}

	public void doTestRun2() throws IllegalArgumentException, ParserException,
			InconsistencyException, IOException, InterruptedException {
		Ontology ontology = loadOntology("instance-equality.wsml");

		// Create a query, that should bind x to both instances A and B.
		String queryString = "p(?x)";

		// Define the desired reasoner by setting the corresponding values in
		// the parameters. Here IRIS reasoner with well-founded semantics is
		// used.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
				WSMLReasonerFactory.BuiltInReasoner.STREAMING_IRIS_STRATIFIED);

		// Instantiate the desired reasoner using the default reasoner factory.
		StreamingLPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
				.createStreamingFlightReasoner(params);

		// Transform the query in string form to a logical expression object.
		LogicalExpression query = new WsmlLogicalExpressionParser(ontology)
				.parse(queryString);

		// create the configuration
		Map<String, Object> configuration = new HashMap<String, Object>();

		// Start reasoner
		reasoner.startReasoner(ontology, configuration);

		// Register query listener
		ServerSocket server = new ServerSocket(0);
		reasoner.registerQueryListener(query, "localhost",
				server.getLocalPort());

		// TODO: start input streamer

		Thread.sleep(13000);

		reasoner.deregisterQueryListener(query, "localhost",
				server.getLocalPort());
		server.close();

		Thread.sleep(5000);

		// do not forget to shut down the reasoner
		reasoner.shutdownReasoner();
	}

	/**
	 * loads an Ontology and performs sample query
	 */
	public void doTestRun() throws Exception {
		Ontology exampleOntology = loadOntology("exampleOntology.wsml");
		if (exampleOntology == null)
			return;

		String queryString = "?x memberOf ?y";
		// String queryString = "?x = ?y";
		// String queryString =
		// "?x = ?y :- ?x[name hasValue ?n1] and ?y[name hasValue ?n2] and ?n1=?n2.";

		LogicalExpression query = new WsmlLogicalExpressionParser(
				exampleOntology).parse(queryString);

		// get A reasoner
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
				WSMLReasonerFactory.BuiltInReasoner.STREAMING_IRIS_STRATIFIED);
		StreamingLPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
				.createStreamingFlightReasoner(params);

		// create the configuration
		Map<String, Object> configuration = new HashMap<String, Object>();
		configuration.put("inputPort", 45821);

		// Start reasoner
		reasoner.startReasoner(exampleOntology, configuration);

		// Register query listener
		ServerSocket server = new ServerSocket(0);
		Wsml2ReasonerListener wsml2ReasonerListener = new Wsml2ReasonerListener(
				server);

		wsml2ReasonerListener.start();

		reasoner.registerQueryListener(query, "localhost",
				server.getLocalPort());

		// start input streamer
		new Wsml2ReasonerInputStreamer(
				"45821",
				"D:\\workspaces\\workspace_wsml2reasoner\\WSML2.0\\wsml2reasoner-impl\\src\\main\\resources\\exampleEvents.txt");

		Thread.sleep(123000);

		reasoner.deregisterQueryListener(query, "localhost",
				server.getLocalPort());

		wsml2ReasonerListener.interrupt();

		Thread.sleep(5000);

		// do not forget to shut down the reasoner
		reasoner.shutdownReasoner();
	}

	/**
	 * Utility Method to get the object model of a wsml ontology
	 * 
	 * @param file
	 *            location of source file (It will be attempted to be loaded
	 *            from current class path)
	 * @return object model of ontology at file location
	 */
	private Ontology loadOntology(String file) {
		Parser wsmlParser = new WsmlParser();

		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(file);
		try {
			final TopEntity[] identifiable = wsmlParser
					.parse(new InputStreamReader(is));
			if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
				return (Ontology) identifiable[0];
			} else {
				System.out.println("First Element of file no ontology ");
				return null;
			}

		} catch (Exception e) {
			System.out.println("Unable to parse ontology: " + e.getMessage());
			return null;
		}

	}
}
