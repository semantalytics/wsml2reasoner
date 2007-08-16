/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package variant.dl;

import java.io.StringWriter;
import java.util.HashMap;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.semanticweb.owl.model.OWLOntology;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.DLBasedWSMLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.serializer.owl.OWLSerializer;
import org.wsml.reasoner.serializer.owl.OWLSerializerImpl;

import base.BaseReasonerTest;
import framework.normalization.BaseNormalizationTest;


/**
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 *
 */
public class DataValuesTest extends BaseNormalizationTest {

	protected DLBasedWSMLReasoner dlReasoner;
	
	protected BuiltInReasoner previous;
	
	protected OWLSerializer serializer;
	
	protected HashMap<String, String> prefs; 
	
	protected StringWriter writer;
	
	protected Ontology ontology;
	
	protected OWLOntology owlOntology;
	
	protected Axiom axiom;
	
	protected String ns = "http://ex.org#";
	
	protected void setUp() throws Exception {
        super.setUp();
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont" + System.currentTimeMillis()));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
        axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom" + System.currentTimeMillis()));
        ontology.addAxiom(axiom);
        previous = BaseReasonerTest.reasoner;
        dlReasoner = new DLBasedWSMLReasoner(WSMLReasonerFactory.BuiltInReasoner.PELLET, 
        		new WSMO4JManager());
        serializer = new OWLSerializerImpl();
        writer = new StringWriter();
        prefs = new HashMap<String, String>();
        prefs.put(OWLSerializer.OWL_SERIALIZER, OWLSerializer.OWL_ABSTRACT);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        BaseReasonerTest.resetReasoner(previous);
        ontology = null;
        owlOntology = null;
        System.gc();
    }
	
    /**
     * This test checks for transformation of wsml _string datavalue.
     */
    public void testString() throws Exception {
    	String s = "Mary[hasName hasValue _string(\"Mary Jones\")].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"Mary Jones\"^^<http://www.w3.org/2001/XMLSchema#string>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _decimal datavalue.
     */
    public void testDecimal() throws Exception {
    	String s = "A[hasDecimal hasValue _decimal(-1.5)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasDecimal \"-1.5\"^^<http://www.w3.org/2001/XMLSchema#decimal>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _integer datavalue.
     */
    public void testInteger() throws Exception {
    	String s = "Mary[hasAge hasValue _integer(31)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasAge \"31\"^^<http://www.w3.org/2001/XMLSchema#integer>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _float datavalue.
     */
    public void testFloat() throws Exception {
    	String s = "A[hasFloat hasValue _float(\"-60.5e-3\")].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);

        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasFloat \"-0.0605\"^^<http://www.w3.org/2001/XMLSchema#float>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _double datavalue.
     */
    public void testDouble() throws Exception {
    	String s = "A[hasDouble hasValue _double(\"58.5E-5\")].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
					"value(a:hasDouble \"5.85E-4\"^^<http://www.w3.org/2001/XMLSchema#double>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _iri datavalue.
     */
    public void testIRI() throws Exception {
    	String s = "Mary[hasLocation hasValue _iri(_\"http://www.example.com/tests#testOntology\")].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasLocation b:testOntology))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _sqname datavalue.
     */
    public void testSqname() throws Exception {
    	ontology.addNamespace(wsmoFactory.createNamespace("b", 
    			wsmoFactory.createIRI("http://www.example.com/tests#")));
    	String s = "A[hasSQName hasValue _sqname(\"b\", \"testOntology\")].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasSQName b:testOntology))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _boolean datavalue.
     */
    public void testBoolean() throws Exception {
    	String s = "Mary[isWoman hasValue _boolean(\"true\")].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:isWoman \"true\"^^<http://www.w3.org/2001/XMLSchema#boolean>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _duration datavalue.
     */
    public void testDuration() throws Exception {
    	String s = "A[hasDuration hasValue _duration(66, 2, 3, 10, 20, 10)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasDuration \"P66Y2M3DT10H20M10S\"^^<http://www.w3.org/2001/XMLSchema#duration>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _dateTime datavalue.
     */
    public void testDateTime() throws Exception {
    	String s = "Mary[hasBirthday hasValue _dateTime(1977, 02, 07, 10, 20, 10, 12, 30)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasBirthday \"1977-02-07T10:20:10GMT+00:12\"^^<http://www.w3.org/2001/XMLSchema#dateTime>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _dateTime datavalue.
     */
    public void testDateTimeShort() throws Exception {
    	String s = "Mary[hasBirthday hasValue _dateTime(1977, 02, 07, 10, 20, 10)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasBirthday \"1977-02-07T10:20:10\"^^<http://www.w3.org/2001/XMLSchema#dateTime>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _time datavalue.
     */
    public void testTime() throws Exception {
    	String s = "Mary[hasName hasValue _time(10, 20, 10, 12, 30)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"10:20:10GMT+00:12\"^^<http://www.w3.org/2001/XMLSchema#time>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _time datavalue.
     */
    public void testTimeShort() throws Exception {
    	String s = "Mary[hasName hasValue _time(10, 20, 10)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"10:20:10\"^^<http://www.w3.org/2001/XMLSchema#time>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _date datavalue.
     */
    public void testDate() throws Exception {
    	String s = "Mary[hasBirthday hasValue _date(1967, 08, 16, 12, 30)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasBirthday \"1967-08-16GMT+00:12\"^^<http://www.w3.org/2001/XMLSchema#date>))"));
        axiom.removeDefinition(le);
    }

    /**
     * This test checks for transformation of wsml _date datavalue.
     */
    public void testDateShort() throws Exception {
    	String s = "Mary[hasBirthday hasValue _date(1967, 08, 16)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasBirthday \"1967-08-16\"^^<http://www.w3.org/2001/XMLSchema#date>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _gyearmonth datavalue.
     */
    public void testGyearmonth() throws Exception {
    	String s = "Mary[hasName hasValue _gyearmonth(1977, 02)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"1977-02\"^^<http://www.w3.org/2001/XMLSchema#gYearMonth>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _gyear datavalue.
     */
    public void testGyear() throws Exception {
    	String s = "Mary[hasName hasValue _gyear(1977)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"1977\"^^<http://www.w3.org/2001/XMLSchema#gYear>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _gmonthday datavalue.
     */
    public void testGmonthday() throws Exception {
    	String s = "Mary[hasName hasValue _gmonthday(02, 07)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"02-07\"^^<http://www.w3.org/2001/XMLSchema#gMonthDay>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _gday datavalue.
     */
    public void testGday() throws Exception {
    	String s = "Mary[hasName hasValue _gday(07)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"7\"^^<http://www.w3.org/2001/XMLSchema#gDay>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _gmonth datavalue.
     */
    public void testGmonth() throws Exception {
    	String s = "Mary[hasName hasValue _gmonth(02)].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"2\"^^<http://www.w3.org/2001/XMLSchema#gMonth>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _hexbinary datavalue.
     */
    public void testHexbinary() throws Exception {
    	String s = "Mary[hasName hasValue _hexbinary(\"0FB7\")].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"0FB7\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>))"));
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml _base64binary datavalue.
     */
    public void testBase64binary() throws Exception {
    	String s = "Mary[hasName hasValue _base64binary(\"R01G0DdhNgAPAPAAAAACTYyPq\")].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		serializer.serialize(owlOntology, writer, prefs);
//		System.out.println(writer.toString());
		assertTrue(writer.toString().contains(
				"value(a:hasName \"R01G0DdhNgAPAPAAAAACTYyPq\"^^<http://www.w3.org/2001/XMLSchema#base64Binary>))"));
        axiom.removeDefinition(le);
    }
    
}
