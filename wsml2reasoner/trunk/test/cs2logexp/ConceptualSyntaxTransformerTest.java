package cs2logexp;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.deri.wsml.reasoner.normalization.ConceptualSyntax2LogicalExpressionNormalizer;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

public class ConceptualSyntaxTransformerTest extends TestCase
{
	protected static int ontologyCount = 1;
	
	protected ConceptualSyntax2LogicalExpressionNormalizer transformer;
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		wsmoFactory = Factory.createWsmoFactory(null);
		HashMap createParams = new HashMap();
		createParams.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
		leFactory = (LogicalExpressionFactory)Factory.createLogicalExpressionFactory(createParams);
		transformer = new ConceptualSyntax2LogicalExpressionNormalizer();//new OntologyTransformer();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		wsmoFactory = Factory.createWsmoFactory(null);
		
	}

	public void testOfTypeInMolecules() throws Exception
	{
		//create test ontology (concept Pizza topping ofType Topping):
		Ontology ontology = createOntology();
	    Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI("Pizza"));
	    Attribute attribute = wsmoFactory.createAttribute(concept, wsmoFactory.createIRI("topping"));
	    attribute.setConstraining(true);
	    attribute.addType(wsmoFactory.createConcept(wsmoFactory.createIRI("Topping")));
	    ontology.addConcept(concept);
	    
	    //transform ontology into logical expressions:
	    Ontology leOntology = transformer.normalize(ontology);
	    
	    //test whether produced expression is correct
	    // by means of regular expressions matched against serialized result ontology:
		StringBuffer buf = new StringBuffer();
		Serializer serializer = Factory.createSerializer(null);
		serializer.serialize(new TopEntity[] { leOntology }, buf);
		String leString = buf.toString();
	    Pattern pattern = Pattern.compile("\\?x\\[.*topping.*hasValue.*\\?(\\w+)\\]\\s*and\\s*naf\\s*\\?\\1\\s*memberOf\\s*..Topping",Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(leString);
	    assertTrue(matcher.find());
	}
	
	public void testMinCard() throws Exception
	{
	    //create test ontology (concept MinTriplePizza topping ofType (3 *) Topping):
		Ontology ontology = createOntology();
	    Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI("MinTriplePizza"));
	    Attribute attribute = wsmoFactory.createAttribute(concept, wsmoFactory.createIRI("toppingMin"));
	    attribute.setConstraining(true);
	    attribute.setMinCardinality(3);
	    attribute.addType(wsmoFactory.createConcept(wsmoFactory.createIRI("ToppingMin")));
	    ontology.addConcept(concept);
	    
	    //transform ontology into logical expressions:
	    Ontology leOntology = transformer.normalize(ontology);
	    
	    //test whether produced expression is correct
	    // by means of regular expressions matched against serialized result ontology:
		StringBuffer buf = new StringBuffer();
		Serializer serializer = Factory.createSerializer(null);
		serializer.serialize(new TopEntity[] { leOntology }, buf);
		String leString = buf.toString();
	    Pattern pattern = Pattern.compile("toppingMin.?\\s*hasValue\\s*\\{\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*\\}.*and\\s*\\?\\3\\s*\\!=\\s*\\?\\2\\s*and\\s*\\?\\s*\\3\\s*\\!=\\s*\\?\\1\\s*and\\s*\\?\\2\\s*\\!=\\s*\\?\\1\\s*\\.",Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(leString);
	    assertTrue(matcher.find());
	}
	
	public void testMaxCard() throws Exception
	{
	    //create test ontology (concept MaxTriplePizza topping ofType (0 3) Topping):
		Ontology ontology = createOntology();
	    Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI("MaxTriplePizza"));
	    Attribute attribute = wsmoFactory.createAttribute(concept, wsmoFactory.createIRI("toppingMax"));
	    attribute.setConstraining(true);
	    attribute.setMaxCardinality(3);
	    attribute.addType(wsmoFactory.createConcept(wsmoFactory.createIRI("ToppingMax")));
	    ontology.addConcept(concept);
	    
	    //transform ontology into logical expressions:
	    Ontology leOntology = transformer.normalize(ontology);
	    
	    //test whether produced expression is correct
	    // by means of regular expressions matched against serialized result ontology:
		StringBuffer buf = new StringBuffer();
		Serializer serializer = Factory.createSerializer(null);
		serializer.serialize(new TopEntity[] { leOntology }, buf);
		String leString = buf.toString();
		Pattern pattern = Pattern.compile("toppingMax.?\\s*hasValue\\s*\\{\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*\\}.*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*.",Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(leString);
	    assertTrue(matcher.find());
	}
/*	
	and ?x[_"toppingMax" hasValue {?y3, ?y2, ?y4, ?y1}]
	       and ?y1 != ?y2
	       and ?y1 != ?y3
	       and ?y1 != ?y4
	       and ?y2 != ?y3
	       and ?y2 != ?y4
	       and ?y3 != ?y4. 
*/
	
	private Ontology parseOntology(String fileName) throws Exception
	{
		Map createParams = new HashMap();
		createParams.put(Parser.PARSER_WSMO_FACTORY, wsmoFactory);
		createParams.put(Parser.PARSER_LE_FACTORY, leFactory);
		Parser parser = Factory.createParser(createParams);
		InputStream stream = new FileInputStream(fileName);
		assertNotNull("access test WSML file", stream);
		return (Ontology)parser.parse(new InputStreamReader(stream))[0];
	}
	
	private Ontology createOntology()
	{
		int ontologyNumber = ontologyCount++;
		Ontology ontology = wsmoFactory.createOntology(wsmoFactory.createIRI("http://mu.org#ont"+Integer.toString(ontologyNumber)));
	    ontology.setDefaultNamespace(wsmoFactory.createIRI("http://mu."+Integer.toString(ontologyNumber)+".org#"));
	    return ontology;
	}
}
