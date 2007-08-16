/**
 * 
 */
package org.wsml.reasoner.builtin.iris;


import java.util.Arrays;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.IConcreteFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.IConstructedTerm;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

import base.BaseReasonerTest;

/**
 * <p>
 * Tests for the iris facade.
 * </p>
 * <p>
 * $Id: IrisFacadeTest.java,v 1.5 2007-08-16 18:22:43 graham Exp $
 * </p>
 * 
 * @author Richard Pöttler, richard dot poettler at deri dot org
 * @version $Revision: 1.5 $
 */
public class IrisFacadeTest extends TestCase {
	
	BuiltInReasoner previous;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		previous = BaseReasonerTest.reasoner;
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		BaseReasonerTest.resetReasoner(previous);
        System.gc();
	}

	public void testGetTZData() {
		assertTrue("result must be tzHour=1, tzMin=0", Arrays.equals(new int[] {
				1, 0 }, IrisFacade.getTZData(TimeZone.getTimeZone("GMT+1"))));
		;
		assertTrue("result must be tzHour=0, tzMin=0", Arrays.equals(new int[] {
				0, 0 }, IrisFacade.getTZData(TimeZone.getTimeZone("GMT"))));
		;
		assertTrue("result must be tzHour=-10, tzMin=30", Arrays.equals(
				new int[] { -10, -30 }, IrisFacade.getTZData(TimeZone
						.getTimeZone("GMT-10:30"))));
		;
	}

	public void testWsmoTermConverter() {
		final DataFactory DF = org.wsmo.factory.Factory.createDataFactory(null);
		final WsmoFactory WF = org.wsmo.factory.Factory.createWsmoFactory(null);
		final LogicalExpressionFactory LF = org.wsmo.factory.Factory
				.createLogicalExpressionFactory(null);
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		// test variable
		assertEquals(TF.createVariable("asdf"), IrisFacade.wsmoTermConverter(LF
				.createVariable("asdf")));
		// test iri
		assertEquals(CF.createIri("http://my.iri"), IrisFacade
				.wsmoTermConverter(WF.createIRI("http://my.iri")));
		
		// test constructed term
		final ConstructedTerm wc = LF.createConstructedTerm(WF
				.createIRI("http://constr"), Arrays.asList((Term)DF
				.createWsmlString("a"), LF.createConstructedTerm(WF
				.createIRI("http://inner"), Arrays.asList((Term)DF
				.createWsmlString("b"))), (Term)DF.createWsmlString("c")));
		final IConstructedTerm ic = TF.createConstruct("http://constr", TF
				.createString("a"), TF.createConstruct("http://inner", TF
				.createString("b")), TF.createString("c"));
		assertEquals(ic, IrisFacade.wsmoTermConverter(wc));
		// test builtins
		// TODO: not implemented yet
		// test datavalues
		// datavalues got their own test.

	}

	public void testIrisTermConverter() {
		final WsmoFactory WF = org.wsmo.factory.Factory.createWsmoFactory(null);
		final DataFactory DF = org.wsmo.factory.Factory.createDataFactory(null);
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final LogicalExpressionFactory LF = org.wsmo.factory.Factory
				.createLogicalExpressionFactory(null);
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		final IrisFacade IF = new IrisFacade();

		// test constructed
		final ConstructedTerm wc = LF.createConstructedTerm(WF
				.createIRI("http://constr"), Arrays.asList((Term)DF
				.createWsmlString("a"), LF.createConstructedTerm(WF
				.createIRI("http://inner"), Arrays.asList((Term)DF
				.createWsmlString("b"))), (Term)DF.createWsmlString("c")));
		final IConstructedTerm ic = TF.createConstruct("http://constr", TF
				.createString("a"), TF.createConstruct("http://inner", TF
				.createString("b")), TF.createString("c"));
		assertEquals(wc, IF.irisTermConverter(ic));
		// test string
		assertEquals(DF.createWsmlString("asdf"), IF.irisTermConverter(TF
				.createString("asdf")));
		// test variable
		assertEquals(LF.createVariable("asdf"), IF.irisTermConverter(TF
				.createVariable("asdf")));
		// test Base64Binary
		assertEquals(DF.createWsmlBase64Binary("asdf".getBytes()), IF
				.irisTermConverter(CF.createBase64Binary("asdf")));
		// test boolean
		assertEquals(DF.createWsmlBoolean("true"), IF.irisTermConverter(CF
				.createBoolean(true)));
		// test date
		assertEquals(DF.createWsmlDate(2007, 1, 20, 0, 0), IF
				.irisTermConverter(CF.createDate(2007, 1, 20)));
		// test datetime
		assertEquals(DF.createWsmlDateTime(2007, 1, 20, 13, 45, 11, 0, 0), IF
				.irisTermConverter(CF.createDateTime(2007, 1, 20, 13, 45, 11,
						0, 0)));
		// test decimal
		assertEquals(DF.createWsmlDecimal("3.1415"), IF.irisTermConverter(CF
				.createDecimal(3.1415d)));
		// test double
		assertEquals(DF.createWsmlDouble("3.1415"), IF.irisTermConverter(CF
				.createDouble(3.1415d)));
		// test duration
		assertEquals(DF.createWsmlDuration(5, 3, 5, 12, 16, 11), IF
				.irisTermConverter(CF.createDuration(5, 3, 5, 12, 16, 11)));
		// test float
		assertEquals(DF.createWsmlFloat("3.1415"), IF.irisTermConverter(CF
				.createFloat(3.1415f)));
		// test gday
		assertEquals(DF.createWsmlGregorianDay(15), IF.irisTermConverter(CF
				.createGDay(15)));
		// test gmonth
		assertEquals(DF.createWsmlGregorianMonth(2), IF.irisTermConverter(CF
				.createGMonth(2)));
		// test gmonthday
		assertEquals(DF.createWsmlGregorianMonthDay(5, 15), IF
				.irisTermConverter(CF.createGMonthDay(5, 15)));
		// test gyear
		assertEquals(DF.createWsmlGregorianYear(2010), IF.irisTermConverter(CF
				.createGYear(2010)));
		// test gyearmonth
		assertEquals(DF.createWsmlGregorianYearMonth(2010, 5), IF
				.irisTermConverter(CF.createGYearMonth(2010, 5)));
		// test hexbinary
		assertEquals(DF.creatWsmlHexBinary("15AB".getBytes()), IF
				.irisTermConverter(CF.createHexBinary("15AB")));
		// test integer
		assertEquals(DF.createWsmlInteger("15"), IF.irisTermConverter(CF
				.createInteger(15)));
		// test iri
		assertEquals(WF.createIRI("http://my.iri"), IF.irisTermConverter(CF
				.createIri("http://my.iri")));
		// test sqname
		// there is no sqname in wsmo4j
	}

	public void testLiteral2Atom() {
		final DataFactory DF = org.wsmo.factory.Factory.createDataFactory(null);
		final IBasicFactory BF = org.deri.iris.factory.Factory.BASIC;
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final LogicalExpressionFactory LF = org.wsmo.factory.Factory
				.createLogicalExpressionFactory(null);

		assertEquals(BF.createAtom(BF.createPredicate("lit", 3), BF
				.createTuple(TF.createString("a"), TF.createVariable("b"), TF
						.createString("c"))), IrisFacade
				.literal2Atom(new Literal(true, "lit",
						DF.createWsmlString("a"), LF.createVariable("b"), DF
								.createWsmlString("c"))));
	}

	public void testLiteral2Literal() {
		final DataFactory DF = org.wsmo.factory.Factory.createDataFactory(null);
		final IBasicFactory BF = org.deri.iris.factory.Factory.BASIC;
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final LogicalExpressionFactory LF = org.wsmo.factory.Factory
				.createLogicalExpressionFactory(null);

		assertEquals(BF.createLiteral(false, BF.createPredicate("lit", 3), BF
				.createTuple(TF.createString("a"), TF.createVariable("b"), TF
						.createString("c"))), IrisFacade
				.literal2Literal(new Literal(false, "lit", DF
						.createWsmlString("a"), LF.createVariable("b"), DF
						.createWsmlString("c"))));
		assertEquals(BF.createLiteral(true, BF.createPredicate("lit", 3), BF
				.createTuple(TF.createString("a"), TF.createVariable("b"), TF
						.createString("c"))), IrisFacade
				.literal2Literal(new Literal(true, "lit", DF
						.createWsmlString("a"), LF.createVariable("b"), DF
						.createWsmlString("c"))));
	}

	public void testDavaValueConverter() {
		final DataFactory DF = org.wsmo.factory.Factory.createDataFactory(null);
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		// test base64binary
		assertEquals(CF.createBase64Binary("asdfxQ=="), IrisFacade
				.dataValueConverter(DF.createWsmlBase64Binary("asdfxQ=="
						.getBytes())));
		// test boolean
		assertEquals(CF.createBoolean(true), IrisFacade.dataValueConverter(DF
				.createWsmlBoolean(true)));
		// test date
		assertEquals(CF.createDate(2007, 1, 8), IrisFacade
				.dataValueConverter(DF.createWsmlDate(2007, 1, 8, 0, 0)));
		// test datetime
		assertEquals(CF.createDateTime(2007, 1, 8, 13, 15, 22, 1, 0),
				IrisFacade.dataValueConverter(DF.createWsmlDateTime(2007, 1, 8,
						13, 15, 22, 1, 0)));
		// test decimal
		assertEquals(CF.createDecimal(1.3498), IrisFacade.dataValueConverter(DF
				.createWsmlDecimal("1.3498")));
		// test double
		assertEquals(CF.createDouble(1.3498), IrisFacade.dataValueConverter(DF
				.createWsmlDouble("1.3498")));
		// test duration
		assertEquals(CF.createDuration(2007, 1, 8, 13, 15, 22), IrisFacade
				.dataValueConverter(DF.createWsmlDuration(2007, 1, 8, 13, 15,
						22)));
		// test float
		assertEquals(CF.createFloat(1.3498f), IrisFacade.dataValueConverter(DF
				.createWsmlFloat("1.3498")));
		// test gday
		assertEquals(CF.createGDay(15), IrisFacade.dataValueConverter(DF
				.createWsmlGregorianDay(15)));
		// test gmonth
		assertEquals(CF.createGMonth(10), IrisFacade.dataValueConverter(DF
				.createWsmlGregorianMonth(10)));
		// test gmonthday
		assertEquals(CF.createGMonthDay(10, 4), IrisFacade
				.dataValueConverter(DF.createWsmlGregorianMonthDay(10, 4)));
		// test gyear
		assertEquals(CF.createGYear(2007), IrisFacade.dataValueConverter(DF
				.createWsmlGregorianYear(2007)));
		// test gyearmonth
		assertEquals(CF.createGYearMonth(2007, 10), IrisFacade
				.dataValueConverter(DF.createWsmlGregorianYearMonth(2007, 10)));
		// test base64binary
		assertEquals(CF.createHexBinary("12AF"), IrisFacade
				.dataValueConverter(DF.creatWsmlHexBinary("12AF".getBytes())));
		// test int
		assertEquals(CF.createInteger(23), IrisFacade.dataValueConverter(DF
				.createWsmlInteger("23")));
		// test iri
		// iri is a term and not a datavalue!
		// test sqname
		// i'm sorry, but i never saw something like an sqname in wsmo4j...
		// test string
		assertEquals(TF.createString("asdf"), IrisFacade.dataValueConverter(DF
				.createWsmlString("asdf")));
		// test time
		// TODO: i'm sorry again, but time isn't implemented in iris at the
		// moment
	}
}
