package normalization;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.deri.wsml.reasoner.normalization.LEConstructReductionNormalizer;
import org.deri.wsml.reasoner.normalization.LELLoydToporNormalizer;
import org.deri.wsml.reasoner.normalization.OntologyNormalizer;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

public class ConstructReductionNormalizerTest extends TestCase
{
    protected static int ontologyCount = 1;
    protected OntologyNormalizer normalizer;
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
        normalizer = new LEConstructReductionNormalizer();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNestedOperations() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("examples/constructs.wsml");

        // normalize ontology with the LEConstructReductionNormalizer:
        Ontology normOnt = normalizer.normalize(ontology);

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        StringBuffer buf = new StringBuffer();
        Serializer serializer = Factory.createSerializer(null);
        serializer.serialize(new TopEntity[] { normOnt }, buf);
        String normString = buf.toString();
System.out.println(normString);
        Pattern pattern = Pattern.compile("\\?x\\[.*topping.*hasValue.*\\?(\\w+)\\]\\s*and\\s*naf\\s*\\?\\1\\s*memberOf\\s*..Topping", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(normString);
        assertTrue(matcher.find());
    }

    private Ontology createOntology()
    {
        int ontologyNumber = ontologyCount++;
        Ontology ontology = wsmoFactory.createOntology(wsmoFactory.createIRI("http://mu.org#ont" + Integer.toString(ontologyNumber)));
        ontology.setDefaultNamespace(wsmoFactory.createIRI("http://mu." + Integer.toString(ontologyNumber) + ".org#"));
        return ontology;
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
