package org.wsml.reasoner.ext.sql;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.WsmoFactory;

import com.ontotext.wsmo4j.common.IRIImpl;

public class VisitorDataTypeTest extends TestCase {

	private DatatypeVisitor visitor = new DatatypeVisitor();
	private WsmoFactory wsmoFactory = FactoryImpl.getInstance().createWsmoFactory();
	private DataFactory df = FactoryImpl.getInstance().createWsmlDataFactory(wsmoFactory );
	
	public void testVisitIRI() {		
		IRI testIRI1 = new IRIImpl("http://www.wsmo.org/2004/wsml#someID");
		testIRI1.accept(visitor);
		Entry res = visitor.getMapping();
		assertEquals(true, res.getClassMapping().equals(String.class));
		assertEquals(true, res.getClassMapping().equals(res.getValue().getClass()));
		assertEquals(true, testIRI1.toString().equals(res.getValue()) );				
	}

	public void testVisitComplexDataValue() {		
		ComplexDataValue wsmlFloat = df.createFloat("34.45");
		wsmlFloat.accept(visitor);
		Entry m = visitor.getMapping();
		assertTrue(m.getValue() instanceof BigDecimal);
		assertEquals(BigDecimal.class, m.getClassMapping());
		assertEquals(new BigDecimal("34.45"), m.getValue());
		
		ComplexDataValue wsmlDouble = df.createDouble("34.455456");
		wsmlDouble.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof BigDecimal);
		assertEquals(BigDecimal.class, m.getClassMapping());
		assertEquals(new BigDecimal("34.455456"), m.getValue());
		
		ComplexDataValue wsmlBoolean = df.createBoolean("true");
		wsmlBoolean.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof Boolean);
		assertEquals(Boolean.class, m.getClassMapping());
		assertEquals(new Boolean("true"), m.getValue());
		
//		FIXME duration support
//		ComplexDataValue wsmlDuration = df.createWsmlDuration(1982, 12, 13, 10, 34, 31);
//		wsmlDuration.accept(visitor);
//		m = visitor.getMapping();
//		assertTrue(m.getValue() instanceof String);
//		assertEquals(String.class, m.getClassMapping());
		
		// Durations can have floating point seconds!
		assertTrue(m.getValue().equals( new String("P1982Y12M13DT10H34M31S") ) || m.getValue().equals( new String("P1982Y12M13DT10H34M31.0S")));	
			
		ComplexDataValue wsmlYearMonth = df.createGregorianYearMonth(1980, 11);
		wsmlYearMonth.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof java.sql.Timestamp);
		assertEquals(java.sql.Timestamp.class, m.getClassMapping());
				
		ComplexDataValue wsmlMonthDay = df.createGregorianMonthDay(11, 3);
		wsmlMonthDay.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof java.sql.Timestamp);
		assertEquals(java.sql.Timestamp.class, m.getClassMapping());
		
		ComplexDataValue wsmlGregorianDay = df.createGregorianDay(20);
		wsmlGregorianDay.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof java.sql.Timestamp);
		assertEquals(java.sql.Timestamp.class, m.getClassMapping());
		
		ComplexDataValue wsmlGregorianMonth = df.createGregorianMonth(1);
		wsmlGregorianMonth.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof java.sql.Timestamp);
		assertEquals(java.sql.Timestamp.class, m.getClassMapping());
		
		ComplexDataValue wsmlGregorianYear = df.createGregorianYear(1981);
		wsmlGregorianYear.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof java.sql.Timestamp);
		assertEquals(java.sql.Timestamp.class, m.getClassMapping());
		
		ComplexDataValue wsmlTime = df.createTime(22, 32, 58, 0, 0);
		wsmlTime.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof java.sql.Timestamp);
		assertEquals(java.sql.Timestamp.class, m.getClassMapping());
		
		ComplexDataValue wsmlDate = df.createDate(1999, 12, 30, 0, 0);
		wsmlDate.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof java.sql.Timestamp);
		assertEquals(java.sql.Timestamp.class, m.getClassMapping());
		
		ComplexDataValue wsmlDateTime = df.createDateTime(1997, 1, 2, 13, 1, 12, 0, 0);
		wsmlDateTime.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof java.sql.Timestamp);
		assertEquals(java.sql.Timestamp.class, m.getClassMapping());			 					
	}
	
	public void testVisitSimpleDataValue() {	
		SimpleDataValue wsmlString = df.createString("testString");
		wsmlString.accept(visitor);
		Entry m = visitor.getMapping();
		assertTrue(m.getValue() instanceof String);
		assertEquals(String.class, m.getClassMapping());
		assertEquals("testString", m.getValue());
		
		SimpleDataValue wsmlDecimal = df.createDecimal("34.2");
		wsmlDecimal.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof BigDecimal);
		assertEquals(BigDecimal.class, m.getClassMapping());
		assertEquals(new BigDecimal("34.2"), m.getValue());
		
		SimpleDataValue wsmlInteger = df.createInteger("2");
		wsmlInteger.accept(visitor);
		m = visitor.getMapping();
		assertTrue(m.getValue() instanceof BigDecimal);
		assertEquals(BigDecimal.class, m.getClassMapping());	
		assertEquals(new BigDecimal("2"), m.getValue());
	}	
}
