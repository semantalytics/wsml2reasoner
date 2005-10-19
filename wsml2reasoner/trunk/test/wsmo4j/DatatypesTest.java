package wsmo4j;

import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.Constants;
import org.omwg.logicalexpression.Implication;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.BuiltInConstructedTerm;
import org.omwg.ontology.*;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

import test.BaseTest;

public class DatatypesTest extends TestCase {

    private static final String ONTOLOGY_FILE = "examples/datatypes.wsml";

    private static final String NS = "urn:datatypes:test#";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DatatypesTest.class);
    }

    private Ontology o = null;

    public void testLoadOntology() throws Exception {
        org.wsmo.factory.LogicalExpressionFactory leFactory = WSMO4JManager
                .getLogicalExpressionFactory();

        WsmoFactory f = WSMO4JManager.getWSMOFactory();

        // Set up WSML parser

        Map<String, Object> parserProperties = new HashMap<String, Object>();
        parserProperties.put(Parser.PARSER_WSMO_FACTORY, f);
        parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);

        parserProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLParserImpl");

        Parser wsmlparserimpl = org.wsmo.factory.Factory
                .createParser(parserProperties);

        // Set up serializer

        Map<String, String> serializerProperties = new HashMap<String, String>();
        serializerProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLSerializerImpl");

        Serializer ontologySerializer = org.wsmo.factory.Factory
                .createSerializer(serializerProperties);

        // Read simple ontology from file
        final Reader ontoReader = BaseTest.getReaderForFile(ONTOLOGY_FILE);
        final TopEntity[] identifiable = wsmlparserimpl.parse(ontoReader);
        if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
            System.out.println("Succesfully parsed ontology");
            o = (Ontology) identifiable[0];
        } else {
            fail("Parsed failed");
        }

        // Print ontology in WSML

        System.out.println("WSML Ontology:\n");
        StringWriter sw = new StringWriter();
        ontologySerializer.serialize(new TopEntity[] { o }, sw);
        System.out.println(sw.toString());
        System.out.println("--------------\n\n");

        Concept c = o.findConcept(f.createIRI(NS + "myConcept"));
        assertNotNull(c);
        // intAttr1
        Attribute intA1 = c.findAttribute(f.createIRI(NS + "intAttr1"));
        assertNotNull(intA1);
        assertTrue(intA1.isConstraining());
        Set types = intA1.listTypes();
        assertEquals(types.size(), 1);
        WsmlDataType intA1Range = (WsmlDataType) types.iterator().next();
        assertTrue(intA1Range instanceof SimpleDataType);
        assertEquals(intA1Range.getIRI().toString(), WsmlDataType.WSML_INTEGER);
        // intAttr2
        Attribute intA2 = c.findAttribute(f.createIRI(NS + "intAttr2"));
        assertNotNull(intA2);
        assertTrue(intA2.isConstraining());
        types = intA2.listTypes();
        assertEquals(types.size(), 1);
        WsmlDataType intA2Range = (WsmlDataType) types.iterator().next();
        assertTrue(intA2Range instanceof SimpleDataType);
        assertEquals(intA2Range.getIRI().toString(), WsmlDataType.WSML_INTEGER);
        // intAttr3
        Attribute intA3 = c.findAttribute(f.createIRI(NS + "intAttr3"));
        assertNotNull(intA3);
        assertTrue(intA3.isConstraining());
        types = intA3.listTypes();
        assertEquals(types.size(), 1);
        WsmlDataType intA3Range = (WsmlDataType) types.iterator().next();
        assertTrue(intA3Range instanceof SimpleDataType);
        assertEquals(intA3Range.getIRI().toString(), WsmlDataType.WSML_INTEGER);
        // sumAttr
        Attribute sumA = c.findAttribute(f.createIRI(NS + "sumAttr"));
        assertNotNull(sumA);
        assertTrue(sumA.isConstraining());
        types = sumA.listTypes();
        assertEquals(types.size(), 1);
        WsmlDataType sumARange = (WsmlDataType) types.iterator().next();
        assertTrue(sumARange instanceof SimpleDataType);
        assertEquals(sumARange.getIRI().toString(), WsmlDataType.WSML_INTEGER);
        // strAttr
        Attribute strA = c.findAttribute(f.createIRI(NS + "strAttr"));
        assertNotNull(strA);
        assertTrue(strA.isConstraining());
        types = strA.listTypes();
        assertEquals(types.size(), 1);
        WsmlDataType strARange = (WsmlDataType) types.iterator().next();
        assertTrue(strARange instanceof SimpleDataType);
        assertEquals(strARange.getIRI().toString(), WsmlDataType.WSML_STRING);
        // strAttr
        Attribute decA = c.findAttribute(f.createIRI(NS + "decAttr"));
        assertNotNull(decA);
        assertTrue(decA.isConstraining());
        types = decA.listTypes();
        assertEquals(types.size(), 1);
        WsmlDataType decARange = (WsmlDataType) types.iterator().next();
        assertTrue(decARange instanceof SimpleDataType);
        assertEquals(decARange.getIRI().toString(), WsmlDataType.WSML_DECIMAL);
        // dateAttr
        Attribute dateA = c.findAttribute(f.createIRI(NS + "dateAttr"));
        assertNotNull(dateA);
        assertTrue(dateA.isConstraining());
        types = dateA.listTypes();
        assertEquals(types.size(), 1);
        WsmlDataType dateARange = (WsmlDataType) types.iterator().next();
        assertTrue(dateARange instanceof ComplexDataType);
        assertEquals(dateARange.getIRI().toString(), WsmlDataType.WSML_DATE);
        // axiom
        Axiom axiom = o.findAxiom(f.createIRI(NS + "intSum"));
        assertNotNull(axiom);
        Set les = axiom.listDefinitions();
        assertEquals(les.size(), 1);
        LogicalExpression le = (LogicalExpression) les.iterator().next();
        assertTrue(le instanceof Implication);
        Implication impl = (Implication) le;
        AttributeValueMolecule attrVal = (AttributeValueMolecule) impl
                .getRightOperand();
        BuiltInConstructedTerm add1 = (BuiltInConstructedTerm) attrVal
                .getRightParameter();
        assertEquals(add1.getFunctionSymbol().toString(), Constants.NUMERIC_ADD);
        assertEquals(add1.getArity(), 2);
        Variable a3 = (Variable) add1.getParameter(1);
        assertEquals(a3.getName(), "a3");
        BuiltInConstructedTerm add2 = (BuiltInConstructedTerm) add1
                .getParameter(0);
        assertEquals(add2.getFunctionSymbol().toString(), Constants.NUMERIC_ADD);
        assertEquals(add2.getArity(), 2);
        Variable a1 = (Variable) add2.getParameter(0);
        assertEquals(a1.getName(), "a1");
        Variable a2 = (Variable) add2.getParameter(1);
        assertEquals(a2.getName(), "a2");
        // instance
        Instance dummy = o.findInstance(f.createIRI(NS + "dummy"));
        assertNotNull(dummy);
        Set concepts = dummy.listConcepts();
        assertEquals(concepts.size(), 1);
        assertEquals(((Concept) concepts.iterator().next()).getIdentifier()
                .toString(), NS + "myConcept");
        // intAttr1 value
        Set attrVals = dummy.listAttributeValues(intA1);
        assertEquals(attrVals.size(), 1);
        SimpleDataValue simpleVal = (SimpleDataValue) attrVals.iterator()
                .next();
        assertEquals(simpleVal.getType().getIRI().toString(),
                WsmlDataType.WSML_INTEGER);
        assertTrue(simpleVal.getValue() instanceof BigInteger);
        assertEquals(simpleVal.getValue(), new BigInteger("1"));
        // strAttr value
        attrVals = dummy.listAttributeValues(strA);
        assertEquals(attrVals.size(), 1);
        simpleVal = (SimpleDataValue) attrVals.iterator().next();
        assertEquals(simpleVal.getType().getIRI().toString(),
                WsmlDataType.WSML_STRING);
        assertTrue(simpleVal.getValue() instanceof String);
        assertEquals(simpleVal.getValue(), "blah");
        // decAttr value
        attrVals = dummy.listAttributeValues(decA);
        assertEquals(attrVals.size(), 1);
        simpleVal = (SimpleDataValue) attrVals.iterator().next();
        assertEquals(simpleVal.getType().getIRI().toString(),
                WsmlDataType.WSML_DECIMAL);
        assertTrue(simpleVal.getValue() instanceof BigDecimal);
        assertEquals(simpleVal.getValue(), new BigDecimal("5.0"));
        // dateAttr value
        attrVals = dummy.listAttributeValues(dateA);
        assertEquals(attrVals.size(), 1);
        ComplexDataValue complexVal = (ComplexDataValue) attrVals.iterator()
                .next();
        assertEquals(complexVal.getType().getIRI().toString(),
                WsmlDataType.WSML_DATE);
        assertTrue(complexVal.getValue() instanceof Calendar);
        assertEquals(complexVal.getArity(), 3);
        Calendar date = (Calendar) complexVal.getValue();
        assertEquals(date.get(Calendar.YEAR),2005);
        assertEquals(date.get(Calendar.MONTH),Calendar.OCTOBER);
        assertEquals(date.get(Calendar.DAY_OF_MONTH),19);
        simpleVal =  complexVal.getArgumentValue((byte) 0);
        assertEquals(simpleVal.getType().getIRI().toString(),
                WsmlDataType.WSML_INTEGER);
        assertTrue(simpleVal.getValue() instanceof BigInteger);
        assertEquals(simpleVal.getValue(), new BigInteger("2005"));

    }

}
