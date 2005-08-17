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
	protected ConceptualSyntax2LogicalExpressionNormalizer transformer;
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Ontology ontology;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		wsmoFactory = Factory.createWsmoFactory(null);
		HashMap createParams = new HashMap();
		createParams.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
		leFactory = (LogicalExpressionFactory)Factory.createLogicalExpressionFactory(createParams);
		transformer = new ConceptualSyntax2LogicalExpressionNormalizer();//new OntologyTransformer();
		ontology = wsmoFactory.createOntology(wsmoFactory.createIRI("http://mu.org#ont"));
	    ontology.setDefaultNamespace(wsmoFactory.createIRI("http://mu.org#"));
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testOfTypeInMolecules() throws Exception
	{
	    //create test ontology (concept Pizza topping ofType Topping):
	    Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI("Pizza"));
	    Attribute attribute = wsmoFactory.createAttribute(concept, wsmoFactory.createIRI("topping"));
	    attribute.setConstraining(true);
	    attribute.addType(wsmoFactory.createConcept(wsmoFactory.createIRI("Topping")));
	    ontology.addConcept(concept);
	    
	    //transform ontology into logical expressions:
	    Ontology leOntology = transformer.normalize(ontology);
	    
	    //test whether produced expression is correct: 
		StringBuffer buf = new StringBuffer();
		Serializer serializer = Factory.createSerializer(null);
		serializer.serialize(new TopEntity[] { leOntology }, buf);
		String leString = buf.toString();
	    Pattern pattern = Pattern.compile("\\?x\\[.*topping.*hasValue.*\\?(\\w+)\\]\\s*and\\s*naf\\s*\\?\\1\\s*memberOf\\s*..Topping",Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(leString);
	    assertTrue(matcher.find());
	}
	
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
}
