package org.wsml.reasoner.ext.sql;

import java.math.BigDecimal;
import java.util.Calendar;
import junit.framework.TestCase;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import com.ontotext.wsmo4j.common.IRIImpl;

public class VisitorDataTypeTest extends TestCase {

	private DatatypeVisitor visitor = new DatatypeVisitor();
	private DataFactory df = org.wsmo.factory.Factory.createDataFactory(null);
	
	public void testVisitIRI() {		
		IRI testIRI1 = new IRIImpl("http://www.wsmo.org/2004/wsml#someID");
		testIRI1.accept(visitor);
		Entry res = visitor.getMapping();
		assertEquals(true, res.getClassMapping().equals(String.class));
		assertEquals(true, res.getClassMapping().equals(res.getValue().getClass()));
		assertEquals(true, testIRI1.toString().equals(res.getValue()) );				
	}

	public void testVisitComplexDataValue() {		
		ComplexDataValue wsmlFloat = df.createWsmlFloat("34.45");
		wsmlFloat.accept(visitor);
		Entry m = visitor.getMapping();
		assertTrue(m.getValue() instanceof BigDecimal);
		assertEquals(BigDecimal.class, m.getClassMapping());
		assertEquals(new BigDecimal("34.45"), m.getValue());
		
		ComplexDataValue wsmlDouble = df.createWsmlDouble("34.455456");
		wsmlDouble.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof BigDecimal);
		assertEquals(BigDecimal.class, m.getClassMapping());
		assertEquals(new BigDecimal("34.455456"), m.getValue());
		
		ComplexDataValue wsmlBoolean = df.createWsmlBoolean("true");
		wsmlBoolean.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Boolean);
		assertEquals(Boolean.class, m.getClassMapping());
		assertEquals(new Boolean("true"), m.getValue());
		
		ComplexDataValue wsmlDuration = df.createWsmlDuration(1982, 12, 13, 10, 34, 31);
		wsmlDuration.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof String);
		assertEquals(String.class, m.getClassMapping());
		
		// Durations can have floating point seconds!
		assertTrue(m.getValue().equals( new String("P1982Y12M13DT10H34M31S") ) || m.getValue().equals( new String("P1982Y12M13DT10H34M31.0S")));	
			
		ComplexDataValue wsmlYearMonth = df.createWsmlGregorianYearMonth(1980, 11);
		wsmlYearMonth.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Calendar);
		assertEquals(Calendar.class, m.getClassMapping());
				
		ComplexDataValue wsmlMonthDay = df.createWsmlGregorianMonthDay(11, 3);
		wsmlMonthDay.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Calendar);
		assertEquals(Calendar.class, m.getClassMapping());
		
		ComplexDataValue wsmlGregorianDay = df.createWsmlGregorianDay(20);
		wsmlGregorianDay.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Calendar);
		assertEquals(Calendar.class, m.getClassMapping());
		
		ComplexDataValue wsmlGregorianMonth = df.createWsmlGregorianMonth(1);
		wsmlGregorianMonth.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Calendar);
		assertEquals(Calendar.class, m.getClassMapping());
		
		ComplexDataValue wsmlGregorianYear = df.createWsmlGregorianYear(1981);
		wsmlGregorianYear.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Calendar);
		assertEquals(Calendar.class, m.getClassMapping());
		
		ComplexDataValue wsmlTime = df.createWsmlTime(22, 32, 58, 0, 0);
		wsmlTime.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Calendar);
		assertEquals(Calendar.class, m.getClassMapping());
		
		ComplexDataValue wsmlDate = df.createWsmlDate(1999, 12, 30, 0, 0);
		wsmlDate.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Calendar);
		assertEquals(Calendar.class, m.getClassMapping());
		
		ComplexDataValue wsmlDateTime = df.createWsmlDateTime(1997, 1, 2, 13, 1, 12, 0, 0);
		wsmlDateTime.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Calendar);
		assertEquals(Calendar.class, m.getClassMapping());
		
		//complextDataTypes.put(WsmlDataType.WSML_SQNAME, String.class);				 					
	}
	
	public void testVisitSimpleDataValue() {	
		SimpleDataValue wsmlString = df.createWsmlString("testString");
		wsmlString.accept(visitor);
		Entry m = visitor.getMapping();
		assertTrue(m.getValue() instanceof String);
		assertEquals(String.class, m.getClassMapping());
		assertEquals("testString", m.getValue());
		
		SimpleDataValue wsmlDecimal = df.createWsmlDecimal("34.2");
		wsmlDecimal.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof BigDecimal);
		assertEquals(BigDecimal.class, m.getClassMapping());
		assertEquals(new BigDecimal("34.2"), m.getValue());
		
		SimpleDataValue wsmlInteger = df.createWsmlInteger("2");
		wsmlInteger.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof BigDecimal);
		assertEquals(BigDecimal.class, m.getClassMapping());	
		assertEquals(new BigDecimal("2"), m.getValue());
	}	
}
