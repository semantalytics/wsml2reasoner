package example;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.deri.wsmo4j.io.serializer.wsml.SerializeWSMLTermsVisitor;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
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
public class IrisReasonerExample {

	/**
	 * @param args
	 *            none expected
	 */
	public static void main(String[] args) {
		IrisReasonerExample ex = new IrisReasonerExample();
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
				WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
		LPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
				.createFlightReasoner(params);

		// Register ontology
		reasoner.registerOntology(exampleOntology);
		// reasoner.registerOntologyNoVerification(exampleOntology);

		// Execute query request
		Set<Map<Variable, Term>> result = reasoner.executeQuery(query);

		// print out the results:
		System.out.println("The query\n'" + query
				+ "'\nhas the following results:");
		for (Map<Variable, Term> vBinding : result) {
			for (Variable var : vBinding.keySet()) {
				System.out.print(var + ": "
						+ termToString(vBinding.get(var), exampleOntology)
						+ "\t ");
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