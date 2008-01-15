package org.wsml.reasoner.ext.sql;

import java.io.File;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ext.sql.DatatypeVisitor;
import org.wsml.reasoner.ext.sql.Entry;
import org.wsml.reasoner.ext.sql.ReasonerResult;
import org.wsml.reasoner.ext.sql.WSMLReasonerFacade;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;

import com.ontotext.wsmo4j.common.IRIImpl;

public class VisitorDataTypeTest extends TestCase {

	private DatatypeVisitor visitor = new DatatypeVisitor();
	private WSMLReasonerFacade facade = new WSMLReasonerFacade();		
	private String testOntologyIRI;
	private DataFactory df = org.wsmo.factory.Factory.createDataFactory(null);
	
	public VisitorDataTypeTest() {
		File file = new File("test/files/lordOfTheRings.wsml");
		URL ontoTestURL;
		try {
			ontoTestURL = file.toURI().toURL();
			testOntologyIRI = ontoTestURL.toString();
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}					
	}

	public void testVisitIRI() {		
		IRI testIRI = new IRIImpl(testOntologyIRI);
		testIRI.accept(visitor);
		Entry res = visitor.getMapping();
		assertEquals(true, res.getClassMapping().equals(String.class));
		assertEquals(true, res.getClassMapping().equals(res.getValue().getClass()) );
		assertEquals(true, testOntologyIRI.equals(res.getValue()) );
		
		IRI testIRI1 = new IRIImpl("http://www.wsmo.org/2004/wsml#someID");
		testIRI1.accept(visitor);
		res = visitor.getMapping();
		assertEquals(true, res.getClassMapping().equals(String.class));
		assertEquals(true, res.getClassMapping().equals(res.getValue().getClass()));
		assertEquals(true, testIRI1.toString().equals(res.getValue()) );				
	}

	public void testVisitComplexDataValue() {		
		ComplexDataValue wsmlFloat = df.createWsmlFloat("34.45");
		wsmlFloat.accept(visitor);
		Entry m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof BigDecimal);
		assertEquals(true, m.getClassMapping().equals(BigDecimal.class));
		assertEquals(true, m.getValue().equals( BigDecimal.valueOf(new Float("34.45")) ));
		
		ComplexDataValue wsmlDouble = df.createWsmlDouble("34.455456");
		wsmlDouble.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof BigDecimal);
		assertEquals(true, m.getClassMapping().equals(BigDecimal.class));
		assertEquals(true, m.getValue().equals( BigDecimal.valueOf(new Double("34.455456")) ));
		
		ComplexDataValue wsmlBoolean = df.createWsmlBoolean("true");
		wsmlBoolean.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof Boolean);
		assertEquals(true, m.getClassMapping().equals(Boolean.class));
		assertEquals(true, m.getValue().equals( new Boolean("true") ));
		
		ComplexDataValue wsmlDuration = df.createWsmlDuration(1982, 12, 13, 10, 34, 31);
		wsmlDuration.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof String);
		assertEquals(true, m.getClassMapping().equals(String.class));
		assertEquals(true, m.getValue().equals( new String("P1982Y12M13DT10H34M31S") )); //strange format	
			
		ComplexDataValue wsmlYearMonth = df.createWsmlGregorianYearMonth(1980, 11);
		wsmlYearMonth.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof Calendar);
		assertEquals(true, m.getClassMapping().equals(Calendar.class));
				
		ComplexDataValue wsmlMonthDay = df.createWsmlGregorianMonthDay(11, 3);
		wsmlMonthDay.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof Calendar);
		assertEquals(true, m.getClassMapping().equals(Calendar.class));
		
		ComplexDataValue wsmlGregorianDay = df.createWsmlGregorianDay(20);
		wsmlGregorianDay.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof Calendar);
		assertEquals(true, m.getClassMapping().equals(Calendar.class));
		
		ComplexDataValue wsmlGregorianMonth = df.createWsmlGregorianMonth(1);
		wsmlGregorianMonth.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof Calendar);
		assertEquals(true, m.getClassMapping().equals(Calendar.class));
		
		ComplexDataValue wsmlGregorianYear = df.createWsmlGregorianYear(1981);
		wsmlGregorianYear.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof Calendar);
		assertEquals(true, m.getClassMapping().equals(Calendar.class));
		
		ComplexDataValue wsmlTime = df.createWsmlTime(22, 32, 58, 0, 0);
		wsmlTime.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof Calendar);
		assertEquals(true, m.getClassMapping().equals(Calendar.class));
		
		ComplexDataValue wsmlDate = df.createWsmlDate(1999, 12, 30, 0, 0);
		wsmlDate.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof Calendar);
		assertEquals(true, m.getClassMapping().equals(Calendar.class));
		
		ComplexDataValue wsmlDateTime = df.createWsmlDateTime(1997, 1, 2, 13, 1, 12, 0, 0);
		wsmlDateTime.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof Calendar);
		assertEquals(true, m.getClassMapping().equals(Calendar.class));
		
		//complextDataTypes.put(WsmlDataType.WSML_SQNAME, String.class);				 					
	}
	
	public void testVisitSimpleDataValue() {	
		SimpleDataValue wsmlString = df.createWsmlString("testString");
		wsmlString.accept(visitor);
		Entry m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof String);
		assertEquals(true, m.getClassMapping().equals(String.class));
		assertEquals(true, m.getValue().equals("testString"));
		
		SimpleDataValue wsmlDecimal = df.createWsmlDecimal("34.2");
		wsmlDecimal.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof BigDecimal);
		assertEquals(true, m.getClassMapping().equals(BigDecimal.class));
		assertEquals(true, m.getValue().equals( new BigDecimal("34.2") ));
		
		SimpleDataValue wsmlInteger = df.createWsmlInteger("2");
		wsmlInteger.accept(visitor);
		m = visitor.getMapping();
		assertEquals(true, m.getValue() instanceof BigDecimal);
		assertEquals(true, m.getClassMapping().equals(BigDecimal.class));	
		assertEquals(true, m.getValue().equals( new BigDecimal("2") ));
	}	
	
	public void functionalTest() {
		try {
			ReasonerResult rs = facade.executeWsmlQuery("?x memberOf ?y", testOntologyIRI);
		
			for (Map<Variable, Term> vBinding : rs.result) {
				   for (Variable var : vBinding.keySet()) {
					   vBinding.get(var).accept(visitor);
					   Entry res = visitor.getMapping();
					   Term t = vBinding.get(var);
					   if(t instanceof IRI)
					   {						
						   assertEquals(true, res.getClassMapping().equals(res.getValue().getClass()));
						   assertEquals(true, res.getClassMapping().equals(String.class));
					   }
		            }
			}				
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage()); 
		}			
	}
}
