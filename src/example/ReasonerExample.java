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
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.wsml.reasoner.impl.*;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.*;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Holger Lausen, DERI Innsbruck
 */
public class ReasonerExample {

	/**
	 * @param args
	 *            none expected
	 */
	public static void main(String[] args) throws Exception {
		ReasonerExample ex = new ReasonerExample();
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
		//Ontology exampleOntology = loadOntology("example/humanOntology.wsml");
        Ontology exampleOntology = loadOntology("example/task.wsml");
		if (exampleOntology == null)
			return;
		LogicalExpressionFactory leFactory = WSMO4JManager
				.getLogicalExpressionFactory();

        String queryString;
        queryString="?hq memberOf Human";
        queryString="?x memberOf Man";
        queryString="Lisa [hasRelative hasValue ?relative]";
        queryString="?x subConceptOf ?y";
        queryString="?x memberOf Child";
        queryString="?x[hasBirthYear hasValue ?age]";
        queryString="Man";

        LogicalExpression query = leFactory
            .createLogicalExpression(queryString, exampleOntology);
		QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
				exampleOntology.getIdentifier().toString(), query);

		// get A reasoner
		WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
				.getWSMLFlightReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);

		// Register ontology
		reasoner.registerOntology(exampleOntology);

		// Execute query request
		Set<Map<Variable, Term>> result = reasoner.executeQuery(
				(IRI) exampleOntology.getIdentifier(), query);

		// print out the results:
		System.out.println("The query '" + query
				+ "' has the following results:");
		for (Map<Variable, Term> vBinding : result) {
			for (Variable var : vBinding.keySet()) {
				System.out.print(var + ": " + vBinding.get(var) + "; ");
			}
			System.out.println();
		}
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
		LogicalExpressionFactory leFactory = WSMO4JManager
				.getLogicalExpressionFactory();
		WsmoFactory wsmoFactory = WSMO4JManager.getWSMOFactory();
		Parser wsmlParser = Factory.createParser(null);

		InputStream is = this.getClass().getClassLoader().getResourceAsStream(
				file);
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

	/**
	 * small utility method for debugging
	 * 
	 * @param ont
	 *            ontology to be serialized to string
	 * @return string representation of ontology
	 */
	private String toString(Ontology ont) {
		Serializer wsmlSerializer = Factory.createSerializer(null);

		StringBuffer str = new StringBuffer();
		wsmlSerializer.serialize(new TopEntity[] { ont }, str);
		return str.toString();
	}
}
