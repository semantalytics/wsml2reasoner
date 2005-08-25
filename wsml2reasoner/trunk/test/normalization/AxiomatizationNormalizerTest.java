package normalization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deri.wsml.reasoner.normalization.AxiomatizationNormalizer;
import org.deri.wsml.reasoner.normalization.OntologyNormalizer;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Serializer;

public class AxiomatizationNormalizerTest extends WSMLNormalizationTest
{
    protected OntologyNormalizer transformer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        transformer = new AxiomatizationNormalizer();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testOfTypeInMolecules() throws Exception
    {
        // create test ontology (concept Pizza topping ofType Topping):
        Ontology ontology = createOntology();
        Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI("Pizza"));
        Attribute attribute = wsmoFactory.createAttribute(concept, wsmoFactory.createIRI("topping"));
        attribute.setConstraining(true);
        attribute.addType(wsmoFactory.createConcept(wsmoFactory.createIRI("Topping")));
        ontology.addConcept(concept);

        // transform ontology into logical expressions:
        Ontology leOntology = transformer.normalize(ontology);

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String leString = serializeOntology(leOntology);
        Pattern pattern = Pattern.compile("\\?x\\[.*topping.*hasValue.*\\?(\\w+)\\]\\s*and\\s*naf\\s*\\?\\1\\s*memberOf\\s*..Topping", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(leString);
        assertTrue(matcher.find());
    }

    public void testMinCard() throws Exception
    {
        // create test ontology (concept MinTriplePizza topping ofType (3 *)
        // Topping):
        Ontology ontology = createOntology();
        Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI("MinTriplePizza"));
        Attribute attribute = wsmoFactory.createAttribute(concept, wsmoFactory.createIRI("toppingMin"));
        attribute.setConstraining(true);
        attribute.setMinCardinality(3);
        attribute.addType(wsmoFactory.createConcept(wsmoFactory.createIRI("ToppingMin")));
        ontology.addConcept(concept);

        // transform ontology into logical expressions:
        Ontology leOntology = transformer.normalize(ontology);

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String leString = serializeOntology(leOntology);
        Pattern pattern = Pattern
                .compile(
                        "toppingMin.?\\s*hasValue\\s*\\{\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*\\}.*and\\s*\\?\\3\\s*\\!=\\s*\\?\\2\\s*and\\s*\\?\\s*\\3\\s*\\!=\\s*\\?\\1\\s*and\\s*\\?\\2\\s*\\!=\\s*\\?\\1\\s*\\.",
                        Pattern.DOTALL);
        Matcher matcher = pattern.matcher(leString);
        assertTrue(matcher.find());
    }

    public void testMaxCard() throws Exception
    {
        // create test ontology (concept MaxTriplePizza topping ofType (0 3)
        // Topping):
        Ontology ontology = createOntology();
        Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI("MaxTriplePizza"));
        Attribute attribute = wsmoFactory.createAttribute(concept, wsmoFactory.createIRI("toppingMax"));
        attribute.setConstraining(true);
        attribute.setMaxCardinality(3);
        attribute.addType(wsmoFactory.createConcept(wsmoFactory.createIRI("ToppingMax")));
        ontology.addConcept(concept);

        // transform ontology into logical expressions:
        Ontology leOntology = transformer.normalize(ontology);

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String leString = serializeOntology(leOntology);
        Pattern pattern = Pattern
                .compile(
                        "toppingMax.?\\s*hasValue\\s*\\{\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*,\\s*\\?(\\w+)\\s*\\}.*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*and\\s*\\?(\\1|\\2|\\3|\\4)\\s*\\!=\\s*\\?(\\1|\\2|\\3|\\4)\\s*.",
                        Pattern.DOTALL);
        Matcher matcher = pattern.matcher(leString);
        assertTrue(matcher.find());
    }
}
