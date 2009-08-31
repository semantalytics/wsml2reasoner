/**
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package abstractTests.dl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.LogExprParserTypedImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.OWLOntology;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.impl.DLBasedWSMLReasoner;
import org.wsml.reasoner.serializer.owl.OWLSerializer;
import org.wsml.reasoner.serializer.owl.OWLSerializerImpl;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import abstractTests.DL;


public abstract class AbstractDataValues extends TestCase implements DL {
	
	protected Factory factory;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected OWLSerializer serializer;
	protected Ontology ontology;
	protected Axiom axiom;



	 protected void setUp() throws Exception {
	     	super.setUp();
	        factory = new FactoryImpl();
			wsmoFactory = factory.getWsmoFactory();
			leFactory = factory.getLogicalExpressionFactory();
			ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ontologyTestValues"));
	        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
	        axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiomTestValues"));
	        ontology.addAxiom(axiom);
	        ontology.addNamespace(wsmoFactory.createNamespace("b", wsmoFactory.createIRI("http://www.example.com/tests#")));
	}
	 
	 /**
	 * This test checks for transformation of wsml _string datavalue.
	 */
	public void testString() throws InvalidModelException, ParserException, RendererException{
		String in = "A[hasName hasValue _string(\"StringValue\")].";
		String expected = "value(a:hasName \"StringValue\"^^<http://www.w3.org/2001/XMLSchema#string>)";
			
		testValue(in, expected);
	}
	
	 /**
     * This test checks for transformation of wsml _decimal datavalue.
     */
	public void testDecimal() throws InvalidModelException, ParserException, RendererException{
		String in = "A[hasDecimal hasValue _decimal(-1.5)].";
		String expected = "value(a:hasDecimal \"-1.5\"^^<http://www.w3.org/2001/XMLSchema#decimal>)";
			
		testValue(in, expected);
	}
	
	 /**
     * This test checks for transformation of wsml _integer datavalue.
     */
	public void testInteger() throws InvalidModelException, ParserException, RendererException{
		String in =  "A[hasInt hasValue _integer(77)].";
		String expected = "value(a:hasInt \"77\"^^<http://www.w3.org/2001/XMLSchema#integer>)";
			
		testValue(in, expected);
	}
	
	 /**
     * This test checks for transformation of wsml _double datavalue.
     */
	public void testDouble() throws InvalidModelException, ParserException, RendererException{
		String in =  "A[hasDouble hasValue _double(\"58.5E-5\")].";
		String expected = "value(a:hasDouble \"5.85E-4\"^^<http://www.w3.org/2001/XMLSchema#double>)";
			
		testValue(in, expected);
	}
	
	
	 /**
     * This test checks for transformation of wsml _float datavalue.
     */
	public void testFloat() throws InvalidModelException, ParserException, RendererException{
		String in =  "A[hasFloat hasValue _float(\"-60.5e-3\")].";
		String expected = "value(a:hasFloat \"-0.0605\"^^<http://www.w3.org/2001/XMLSchema#float>)";
			
		testValue(in, expected);
	}
	

	
	 /**
     * This test checks for transformation of wsml _iri datavalue.
     */
	public void testIRI() throws InvalidModelException, ParserException, RendererException{
		String in =  "A[hasLocation hasValue _iri(_\"http://www.example.com/tests#testOntology\")].";
		String expected = "value(a:hasLocation b:testOntology)";
			
		testValue(in, expected);
	}
	
	 /**
     * This test checks for transformation of wsml _sqname datavalue.
     */
	public void testSqname() throws InvalidModelException, ParserException, RendererException{
		
		String in =  "A[hasSQName hasValue _sqname(\"b\", \"testOntology\")].";
		String expected = "value(a:hasSQName b:testOntology)";
	
		testValue(in, expected);
	}
	
	 /**
     * This test checks for transformation of wsml _boolean datavalue.
     */
	public void testBoolean() throws InvalidModelException, ParserException, RendererException{
		String in =   "A[isData hasValue _boolean(\"true\")].";
		String expected = "value(a:isData \"true\"^^<http://www.w3.org/2001/XMLSchema#boolean>)";
			
		testValue(in, expected);
	}
	
	 /**
     * This test checks for transformation of wsml _duration datavalue.
     */
	public void testDuration() throws InvalidModelException, ParserException, RendererException{
		String in =   "A[hasDuration hasValue _duration(66, 2, 3, 10, 20, 10)].";
		String expected = "value(a:hasDuration \"P66Y2M3DT10H20M10S\"^^<http://www.w3.org/2001/XMLSchema#duration>)";
			
		testValue(in, expected);
	}
	
	 /**
     * This test checks for transformation of wsml _dateTime datavalue.
     */
	public void testDateTime() throws InvalidModelException, ParserException, RendererException{
		String in =   "A[hasDate hasValue _dateTime(1977, 02, 07, 10, 20, 10.0, 12, 30)].";
		String expected = "value(a:hasDate \"1977-02-07T10:20:10GMT+00:12\"^^<http://www.w3.org/2001/XMLSchema#dateTime>)";
			
		testValue(in, expected);
	}
	
	 /**
     * This test checks for transformation of wsml _dateTimeShort datavalue.
     */
	public void testDateTimeShort() throws InvalidModelException, ParserException, RendererException{
		String in =   "A[hasDate hasValue _dateTime(1977, 02, 07, 10, 20, 10.0)].";
		String expected = "value(a:hasDate \"1977-02-07T10:20:10\"^^<http://www.w3.org/2001/XMLSchema#dateTime>)";
			
		testValue(in, expected);
	}
	 
	/**
    * This test checks for transformation of wsml _time datavalue.
    */
	public void testTime() throws InvalidModelException, ParserException, RendererException{
		String in =   "A[hasName hasValue _time(10, 20, 10.0, 12, 30)].";
		String expected = "value(a:hasName \"10:20:10GMT+00:12\"^^<http://www.w3.org/2001/XMLSchema#time>)";
			
		testValue(in, expected);
	}
	
	/**
	* This test checks for transformation of wsml _timeShort datavalue.
	*/
	public void testTimeShort() throws InvalidModelException, ParserException, RendererException{
		String in =  "A[hasName hasValue _time(10, 20, 10.0)].";
		String expected = "value(a:hasName \"10:20:10\"^^<http://www.w3.org/2001/XMLSchema#time>)";
				
		testValue(in, expected);
	}
	
	/**
	* This test checks for transformation of wsml _date datavalue.
	*/
	public void testDate() throws InvalidModelException, ParserException, RendererException{
		String in = "A[hasDate hasValue _date(1967, 08, 16, 12, 30)].";
		String expected = "value(a:hasDate \"1967-08-16GMT+00:12\"^^<http://www.w3.org/2001/XMLSchema#date>)";
				
		testValue(in, expected);
	}
	
	/**
	* This test checks for transformation of wsml _dateShort datavalue.
	*/
	public void testDateShort() throws InvalidModelException, ParserException, RendererException{
		String in = "A[hasDate hasValue _date(1967, 08, 16)].";
		String expected = "value(a:hasDate \"1967-08-16\"^^<http://www.w3.org/2001/XMLSchema#date>)";
				
		testValue(in, expected);
	}
	
	/**
	* This test checks for transformation of wsml _gyearmonth datavalue.
	*/
	public void testGyearmonth() throws InvalidModelException, ParserException, RendererException{
		String in = "A[hasName hasValue _gyearmonth(1977, 02)].";
		String expected = "value(a:hasName \"1977-02\"^^<http://www.w3.org/2001/XMLSchema#gYearMonth>)";
				
		testValue(in, expected);
	}
	
	/**
	* This test checks for transformation of wsml _gyear datavalue.
	*/
	public void testGyear() throws InvalidModelException, ParserException, RendererException{
		String in =  "A[hasName hasValue _gyear(1977)].";
		String expected = "value(a:hasName \"1977\"^^<http://www.w3.org/2001/XMLSchema#gYear>)";
				
		testValue(in, expected);
	}
	

	/**
	* This test checks for transformation of wsml _gmonthday datavalue.
	*/
	public void testGmonthday() throws InvalidModelException, ParserException, RendererException{
		String in =  "A[hasName hasValue _gmonthday(02, 07)].";
		String expected = "value(a:hasName \"02-07\"^^<http://www.w3.org/2001/XMLSchema#gMonthDay>)";
				
		testValue(in, expected);
	}
	
	/**
	* This test checks for transformation of wsml _gday datavalue.
	*/
	public void testGday() throws InvalidModelException, ParserException, RendererException{
		String in =  "A[hasName hasValue _gday(07)].";
		String expected = "value(a:hasName \"7\"^^<http://www.w3.org/2001/XMLSchema#gDay>)";
				
		testValue(in, expected);
	}
	
	/**
	* This test checks for transformation of wsml _gmonth datavalue.
	*/
	public void testGmonth() throws InvalidModelException, ParserException, RendererException{
		String in = "A[hasName hasValue _gmonth(02)].";
		String expected = "value(a:hasName \"2\"^^<http://www.w3.org/2001/XMLSchema#gMonth>)";
				
		testValue(in, expected);
	}
	
	/**
	* This test checks for transformation of wsml _hexbinary datavalue.
	*/
	public void testHexbinary() throws InvalidModelException, ParserException, RendererException{
		String in = "A[hasName hasValue _hexbinary(\"0FB7\")].";
		String expected = "value(a:hasName \"0FB7\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>)";
				
		testValue(in, expected);
	}
	
	/**
	* This test checks for transformation of wsml _base64binary datavalue.
	*/
	public void testBase64binary() throws InvalidModelException, ParserException, RendererException{
		String in = "A[hasName hasValue _base64binary(\"R01G0DdhNgAPAPAAAAACTYyPq\")].";
		String expected = "value(a:hasName \"R01G0DdhNgAPAPAAAAACTYyPq\"^^<http://www.w3.org/2001/XMLSchema#base64Binary>)";
				
		testValue(in, expected);
	}
	

	private void testValue(String in, String expected) throws ParserException, RendererException, InvalidModelException{
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiomTestValues_" + (in.trim())));
	    ontology.addAxiom(axiom);
		
	    LogicalExpressionParser leParser = new LogExprParserTypedImpl();
		LogicalExpression le = leParser.parse(in);
        axiom.addDefinition(le);
        
        DLBasedWSMLReasoner reasoner = (DLBasedWSMLReasoner) this.getDLReasoner();
        OWLOntology owlOntology = reasoner.createOWLOntology(ontology);
        checkValue(owlOntology,expected );
        axiom.removeDefinition(le);  
        ontology.removeAxiom(axiom);
	}
	
	
	
	private void checkValue(OWLOntology owlOntology, String res ) throws RendererException {
		 StringWriter writer = new StringWriter();
		 Map<String, String> prefs = new HashMap<String, String>();
	     
		 prefs.put(OWLSerializer.OWL_SERIALIZER, OWLSerializer.OWL_ABSTRACT);
	     serializer = new OWLSerializerImpl();
	     serializer.serialize(owlOntology, writer, prefs);
	     
//	     if(!writer.toString().contains(res)) {
//		     System.out.println("- Expected: ");
//		     System.out.println("-" + res);
//		     System.out.println("- Actual: ");
//		     System.out.println(writer.toString());
//		     System.out.println("_______________________");
//	     }
	     
	     assertTrue(writer.toString().contains(res)); 

	}
	
	
	
	

}
