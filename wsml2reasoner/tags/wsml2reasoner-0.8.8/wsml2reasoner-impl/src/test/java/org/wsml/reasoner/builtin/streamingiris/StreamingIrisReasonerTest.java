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
package org.wsml.reasoner.builtin.streamingiris;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.junit.Test;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.StreamingLPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.builtin.streamingiris.WSMLQueryStore.Namespaces;
import org.wsml.reasoner.builtin.streamingiris.WSMLQueryStore.Query;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;

/**
 * Test for Streaming IRIS
 * 
 * @author Norbert Lanzanasto, STI Innsbruck
 */
public class StreamingIrisReasonerTest {

	@Test
	public void doTestRun() throws Exception {

		// load the ontologies
		List<Ontology> ontologies = loadOntologies("Notification.wsml");

		// initialize the reasoner
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
				WSMLReasonerFactory.BuiltInReasoner.STREAMING_IRIS_STRATIFIED);
		StreamingLPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
				.createStreamingFlightReasoner(params);

		// define query
		String testQueryString = "?x memberOf _\""
				+ Namespaces.notification.getNamespace().getIRI().toString()
				+ "flooded\"";
		;
		String queryString = WSMLQueryStore
				.selectPredefinedQuery(Query.get_hazard_level_description);

		LogicalExpression query = new WsmlLogicalExpressionParser(ontologies
				.iterator().next()).parse(testQueryString);

		// create the configuration
		Map<String, Object> configuration = new HashMap<String, Object>();
		configuration.put("inputPort", 45821);

		// start reasoner
		reasoner.startReasoner(ontologies, configuration);

		// start a listener thread
		ServerSocket server = new ServerSocket(0);
		Wsml2ReasonerListener wsml2ReasonerListener = new Wsml2ReasonerListener(
				server);
		wsml2ReasonerListener.start();

		// register query listener
		reasoner.registerQueryListener(query, "localhost",
				server.getLocalPort());

		// start input streamer
		new Wsml2ReasonerInputStreamer(
				"45821",
				"D:\\workspaces\\workspace_iris\\WSML2.0\\wsml2reasoner-impl\\src\\test\\resources\\ExampleEvents.wsml");

		// Thread.sleep(1000);
		//
		// // start input streamer
		// new Wsml2ReasonerInputStreamer(
		// "45821",
		// "D:\\workspaces\\workspace_iris\\WSML2.0\\wsml2reasoner-impl\\src\\test\\resources\\ExampleEvents2.wsml");

		// wait some time
		Thread.sleep(100000);

		// deregister query listener
		reasoner.deregisterQueryListener(query, "localhost",
				server.getLocalPort());

		// stop the listener thread
		wsml2ReasonerListener.interrupt();

		Thread.sleep(5000);

		// shut down the reasoner
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
	private List<Ontology> loadOntologies(String file) {
		ImportingWSMLParser wsmlParser = new ImportingWSMLParser();
		Ontology ontology;
		Ontology notificationOntology = null;
		List<Ontology> ontologies = new ArrayList<Ontology>();

		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(file);
		try {
			final TopEntity[] identifiable = wsmlParser
					.parse(new InputStreamReader(is));

			for (TopEntity entity : identifiable) {
				if (entity instanceof Ontology) {
					ontology = (Ontology) entity;
					if (ontology.getDefaultNamespace().getIRI().toString()
							.startsWith("http://purl.org/ifgi/notification")) {
						notificationOntology = ontology;
					}
					ontologies.add(ontology);
				}
			}
			if (notificationOntology == null) {
				throw new RuntimeException(
						"No Notification ontology found in input.");
			}

			return ontologies;

		} catch (Exception e) {
			System.out.println("Unable to parse ontology: " + e.getMessage());
			return null;
		}
	}

}
