package variant.flight;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.deri.wsmo4j.io.serializer.wsml.SerializeWSMLTermsVisitor;
import org.deri.wsmo4j.io.serializer.wsml.WSMLSerializerImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;
import org.wsmo.wsml.Serializer;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/**
 * This is a test class which runs a few queries to be used with the
 * nobel.wsml ontology. 
 * 
 * @author Darko Anicic, DERI Innsbruck
 * @date 24.08.2007 16:54:07
 */
public class NobelTest {
	
	private WsmlFactoryContainer wsmoManager = null;

	private Parser wsmlParser = null;

	private LogicalExpressionParser leParser;

	// Location of ontolgy
	static String ontLoc = null;
	
	/**
	 * Query execution
	 * 
	 * @param args
	 *            none expected
	 */
	public static void main(String[] args) {
		NobelTest ex = new NobelTest();

		try {
			String query = createQuery(8);
			ex.runProgram(ontLoc, query);
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The method offers a few queries to be used with the nobel.wsml ontology.
	 * 
	 * @param queryNo
	 *            Number of a query to be executed
	 * @return WSML query represented as string
	 * @throws Exception
	 */
	private static String createQuery(int queryNo) throws Exception {
		ontLoc = "files/nobel.wsml";
		
		String queryString = null;

		// Choose a query:
		switch (queryNo) {
		case 1:
			// Retrieve all things that are memberOf some other things:
			queryString = "?x memberOf ?y";
			break;
		case 2:
			// Retrieve all subjects (research areas) for which the Nobel prize
			// is awarded:
			queryString = "?x memberOf nobel_prize";
			break;
		case 3:
			// Retrieve a list of countries that Nobel prize winners come from:
			queryString = "?x memberOf country";
			break;
		case 4:
			// Retrieve IDs of Nobel prize winners from South Korea:
			queryString = "?winner[hasCountry hasValue South_Korea]";
			break;
		case 5:
			// Retrieve names of Nobel prize winners from South Korea:
			queryString = "?winner[hasCountry hasValue South_Korea] and ?winner[hasName hasValue ?winnerName]";
			break;
		case 6:
			// Retrieve all Nobel prize winners who got the prize after 1950:
			queryString = "?winners[hasYear hasValue ?year] and ?year > 1950";
			break;
		case 7:
			// Retrieve all names of Nobel prize winners who got the prize after
			// 1950:
			queryString = "?winner[hasName hasValue ?winnerName] and "
					+ "?winner[hasYear hasValue ?year] and ?year > 1950.";
			break;
		case 8:
			// Retrieve all names of Nobel prize winners who are from South
			// Korea and got the prize after 1950:
			queryString = "?winner[hasName hasValue ?winnerName] and "
					+ "?winner[hasCountry hasValue South_Korea] and "
					+ "?winner[hasYear hasValue ?year] and ?year > 1950.";
			break;
		default:
			throw new IllegalArgumentException(
				"Please specify query number between 1 and 8!");
		}
		return queryString;
	}

	/**
	 * This method demonstrates all steps required for answering a query using
	 * WSML2Reasoner framework and IRIS.
	 * 
	 * @param ontologyLocation
	 *            Location of Ontology
	 * @param query
	 *            Query to be answered.
	 * @throws Exception
	 */
	public void runProgram(String ontologyLocation, String query)
			throws Exception {
		long t0_start = System.currentTimeMillis();

		// set up WSMO4J factories
		setUpFactories();

		// Parse and load ontology
		Ontology o = parseAndLoadOntology(ontologyLocation);

		// Print ontology
		printOntology(o);

		LPReasoner reasoner = getReasoner();
		// Register ontology
		reasoner.registerOntology(o);

		// Execute query request
		System.out.println("Query: " + query);
		Set<Map<Variable, Term>> result = performQuery(reasoner, o, query);

		// print out the results:
		printOutResult(result, o);

		// Measure the time of execution
		long t0_end = System.currentTimeMillis();
		long t0 = t0_end - t0_start;
		System.out.println("(" + t0 + "ms)");
		System.out.println("Finished!");
	}

	/**
	 * Sets up factories for creating WSML elements
	 */
	private void setUpFactories() {
		wsmoManager = new WsmlFactoryContainer();
    	wsmlParser = new WsmlParser();
    	leParser = new WsmlLogicalExpressionParser();
	}

	/**
	 * Utility Method to get the object model of a wsml ontology.
	 * 
	 * @param ontologyFile
	 *            location of source file (It will be attemted to be loaded from
	 *            current class path)
	 * @return object model of ontology at file location
	 */
	private Ontology parseAndLoadOntology(String ontologyFile)
			throws IOException, ParserException, InvalidModelException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(
				ontologyFile);
		try {
			final TopEntity[] identifiable = wsmlParser
					.parse(new InputStreamReader(is));
			if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
				System.out.println("Ontology parsed");
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
	 * Prints out an ontology written in WSML
	 * 
	 * @param o
	 *            Ontology to be printed out
	 */
	private void printOntology(Ontology o) {
		// Set up serializer
		Serializer ontologySerializer = new WSMLSerializerImpl();
		System.out.println("WSML Ontology:\n");
		StringWriter sw = new StringWriter();
		try {
			ontologySerializer.serialize(new TopEntity[] { o }, sw);
		} catch (IOException e) {
			System.out.println("Unable to serialize ontology! ");
			e.printStackTrace();
		}
		System.out.println(sw.toString());
		System.out.println("--------------\n\n");
	}

	/**
	 * Get a reasoner
	 * 
	 * @return Reasoner
	 */
	private LPReasoner getReasoner() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
				WSMLReasonerFactory.BuiltInReasoner.KAON2);

		LPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(params);
		return reasoner;
	}

	/**
	 * Executes a query.
	 * 
	 * @param reasoner
	 *            Reasoner which will execute the query
	 * @param o
	 *            Ontology over which the query will be answered
	 * @param queryString
	 *            WSML query represented as a string
	 * @return Result after the query evaluation
	 * @throws Exception
	 */
	private Set<Map<Variable, Term>> performQuery(LPReasoner reasoner,
			Ontology o, String queryString) throws Exception {
		LogicalExpression query = leParser.parse(queryString);
		// Executes query request
		Set<Map<Variable, Term>> result = reasoner.executeQuery(query);

		return result;
	}

	/**
	 * Utility method to print out the query result
	 * 
	 * @param result
	 *            The query result
	 * @param o
	 *            Ontology from which the result has been derived.
	 */
	private void printOutResult(Set<Map<Variable, Term>> result, Ontology o) {
		for (Map<Variable, Term> vBinding : result) {
			for (Variable var : vBinding.keySet()) {
				System.out.print(var + ": "
						+ termToString(vBinding.get(var), o) + "\t ");
			}
			System.out.println();
		}
		System.out.println("Number of Results: " + result.size());
	}

	/**
	 * Utility method to convert a term to string.
	 * 
	 * @param t
	 *            Term to be converted to string.
	 * @param o
	 *            Ontology which is the source of the term
	 * @return String representation of the term.
	 */
	private String termToString(Term t, Ontology o) {
		SerializeWSMLTermsVisitor v = new SerializeWSMLTermsVisitor(o);
		t.accept(v);
		return v.getSerializedObject();
	}
}

