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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.deri.wsmo4j.io.serializer.wsml.SerializeWSMLTermsVisitor;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.StreamingLPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;
import org.wsmo.wsml.Parser;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Holger Lausen, DERI Innsbruck
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

	/**
	 * loads an Ontology and performs sample query
	 */
	public void doTestRun() throws Exception {
		Ontology exampleOntology = loadOntology("simpleOntology.wsml");
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

		// Start reasoner
		reasoner.startReasoner(exampleOntology, configuration);

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
	 * Utility Method to get the object model of a wsml ontology
	 * 
	 * @param file
	 *            location of source file (It will be attemted to be loaded from
	 *            current class path)
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

	private String termToString(Term t, Ontology o) {
		SerializeWSMLTermsVisitor v = new SerializeWSMLTermsVisitor(o);
		t.accept(v);
		return v.getSerializedObject();
	}
}
