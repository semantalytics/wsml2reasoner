/**
 * 
 */
package org.wsml.reasoner.builtin.iris;


import java.util.Arrays;
import java.util.HashMap;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.IConcreteFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.IConstructedTerm;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * <p>
 * Tests for the iris facade.
 * </p>
 */
public class IrisFacadeTest extends TestCase {
	
	public void testGetTZData() {
		assertTrue("result must be tzHour=1, tzMin=0", Arrays.equals(new int[] {
				1, 0 }, IrisStratifiedFacade.getTZData(TimeZone.getTimeZone("GMT+1"))));
		;
		assertTrue("result must be tzHour=0, tzMin=0", Arrays.equals(new int[] {
				0, 0 }, IrisStratifiedFacade.getTZData(TimeZone.getTimeZone("GMT"))));
		;
		assertTrue("result must be tzHour=-10, tzMin=30", Arrays.equals(
				new int[] { -10, -30 }, IrisStratifiedFacade.getTZData(TimeZone
						.getTimeZone("GMT-10:30"))));
		;
	}

	public void testConvertTermFromWsmo4jToIris() {
		final DataFactory DF = org.wsmo.factory.Factory.createDataFactory(null);
		final WsmoFactory WF = org.wsmo.factory.Factory.createWsmoFactory(null);
		final LogicalExpressionFactory LF = org.wsmo.factory.Factory
				.createLogicalExpressionFactory(null);
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		// test variable
		assertEquals(TF.createVariable("asdf"), IrisStratifiedFacade.convertTermFromWsmo4jToIris(LF
				.createVariable("asdf")));
		// test iri
		assertEquals(CF.createIri("http://my.iri"), IrisStratifiedFacade
				.convertTermFromWsmo4jToIris(WF.createIRI("http://my.iri")));
		
		// test constructed term
		final ConstructedTerm wc = LF.createConstructedTerm(WF
				.createIRI("http://constr"), Arrays.asList((Term)DF
				.createWsmlString("a"), LF.createConstructedTerm(WF
				.createIRI("http://inner"), Arrays.asList((Term)DF
				.createWsmlString("b"))), (Term)DF.createWsmlString("c")));
		final IConstructedTerm ic = TF.createConstruct("http://constr", TF
				.createString("a"), TF.createConstruct("http://inner", TF
				.createString("b")), TF.createString("c"));
		assertEquals(ic, IrisStratifiedFacade.convertTermFromWsmo4jToIris(wc));
		// test builtins
		// TODO: not implemented yet
		// test datavalues
		// datavalues got their own test.

	}

	public void testConvertTermFromIrisToWsmo4j() {
		final WsmoFactory WF = org.wsmo.factory.Factory.createWsmoFactory(null);
		final DataFactory DF = org.wsmo.factory.Factory.createDataFactory(null);
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final LogicalExpressionFactory LF = org.wsmo.factory.Factory
				.createLogicalExpressionFactory(null);
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		final IrisStratifiedFacade IF = new IrisStratifiedFacade( new WSMO4JManager(), new HashMap<String, Object>() );

		// test constructed
		final ConstructedTerm wc = LF.createConstructedTerm(WF
				.createIRI("http://constr"), Arrays.asList((Term)DF
				.createWsmlString("a"), LF.createConstructedTerm(WF
				.createIRI("http://inner"), Arrays.asList((Term)DF
				.createWsmlString("b"))), (Term)DF.createWsmlString("c")));
		final IConstructedTerm ic = TF.createConstruct("http://constr", TF
				.createString("a"), TF.createConstruct("http://inner", TF
				.createString("b")), TF.createString("c"));
		assertEquals(wc, IF.convertTermFromIrisToWsmo4j(ic));
		// test string
		assertEquals(DF.createWsmlString("asdf"), IF.convertTermFromIrisToWsmo4j(TF
				.createString("asdf")));
		// test variable
		assertEquals(LF.createVariable("asdf"), IF.convertTermFromIrisToWsmo4j(TF
				.createVariable("asdf")));
		// test Base64Binary
		assertEquals(DF.createWsmlBase64Binary("asdf".getBytes()), IF
				.convertTermFromIrisToWsmo4j(CF.createBase64Binary("asdf")));
		// test boolean
		assertEquals(DF.createWsmlBoolean("true"), IF.convertTermFromIrisToWsmo4j(CF
				.createBoolean(true)));
		// test date
		assertEquals(DF.createWsmlDate(2007, 1, 20, 0, 0), IF
				.convertTermFromIrisToWsmo4j(CF.createDate(2007, 1, 20)));
		// test datetime
		assertEquals(DF.createWsmlDateTime(2007, 1, 20, 13, 45, 11, 0, 0), IF
				.convertTermFromIrisToWsmo4j(CF.createDateTime(2007, 1, 20, 13, 45, 11,
						0, 0)));
		// test decimal
		assertEquals(DF.createWsmlDecimal("3.1415"), IF.convertTermFromIrisToWsmo4j(CF
				.createDecimal(3.1415d)));
		// test double
		assertEquals(DF.createWsmlDouble("3.1415"), IF.convertTermFromIrisToWsmo4j(CF
				.createDouble(3.1415d)));
		// test duration
//		assertEquals(DF.createWsmlDuration(5, 3, 5, 12, 16, 11), IF
//				.irisTermConverter(CF.createDuration(5, 12, 16, 11)));
		// test float
		assertEquals(DF.createWsmlFloat("3.1415"), IF.convertTermFromIrisToWsmo4j(CF
				.createFloat(3.1415f)));
		// test gday
		assertEquals(DF.createWsmlGregorianDay(15), IF.convertTermFromIrisToWsmo4j(CF
				.createGDay(15)));
		// test gmonth
		assertEquals(DF.createWsmlGregorianMonth(2), IF.convertTermFromIrisToWsmo4j(CF
				.createGMonth(2)));
		// test gmonthday
		assertEquals(DF.createWsmlGregorianMonthDay(5, 15), IF
				.convertTermFromIrisToWsmo4j(CF.createGMonthDay(5, 15)));
		// test gyear
		assertEquals(DF.createWsmlGregorianYear(2010), IF.convertTermFromIrisToWsmo4j(CF
				.createGYear(2010)));
		// test gyearmonth
		assertEquals(DF.createWsmlGregorianYearMonth(2010, 5), IF
				.convertTermFromIrisToWsmo4j(CF.createGYearMonth(2010, 5)));
		// test hexbinary
		assertEquals(DF.creatWsmlHexBinary("15AB".getBytes()), IF
				.convertTermFromIrisToWsmo4j(CF.createHexBinary("15AB")));
		// test integer
		assertEquals(DF.createWsmlInteger("15"), IF.convertTermFromIrisToWsmo4j(CF
				.createInteger(15)));
		// test iri
		assertEquals(WF.createIRI("http://my.iri"), IF.convertTermFromIrisToWsmo4j(CF
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
						.createString("c"))), IrisStratifiedFacade
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
						.createString("c"))), IrisStratifiedFacade
				.literal2Literal(new Literal(false, "lit", DF
						.createWsmlString("a"), LF.createVariable("b"), DF
						.createWsmlString("c"))));
		assertEquals(BF.createLiteral(true, BF.createPredicate("lit", 3), BF
				.createTuple(TF.createString("a"), TF.createVariable("b"), TF
						.createString("c"))), IrisStratifiedFacade
				.literal2Literal(new Literal(true, "lit", DF
						.createWsmlString("a"), LF.createVariable("b"), DF
						.createWsmlString("c"))));
	}

	public void testConvertWsmo4jDataValueToIrisTerm() {
		final DataFactory DF = org.wsmo.factory.Factory.createDataFactory(null);
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		// test base64binary
		assertEquals(CF.createBase64Binary("asdfxQ=="), IrisStratifiedFacade
				.convertWsmo4jDataValueToIrisTerm(DF.createWsmlBase64Binary("asdfxQ=="
						.getBytes())));
		// test boolean
		assertEquals(CF.createBoolean(true), IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF
				.createWsmlBoolean(true)));
		// test date
		assertEquals(CF.createDate(2007, 1, 8), IrisStratifiedFacade
				.convertWsmo4jDataValueToIrisTerm(DF.createWsmlDate(2007, 1, 8, 0, 0)));
		// test datetime
		assertEquals(CF.createDateTime(2007, 1, 8, 13, 15, 22, 1, 0),
				IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF.createWsmlDateTime(2007, 1, 8,
						13, 15, 22, 1, 0)));
		// test decimal
		assertEquals(CF.createDecimal(1.3498), IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF
				.createWsmlDecimal("1.3498")));
		// test double
		assertEquals(CF.createDouble(1.3498), IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF
				.createWsmlDouble("1.3498")));
		// test duration
//		assertEquals(CF.createDuration(8, 13, 15, 22), IrisFacade
//				.dataValueConverter(DF.createWsmlDuration(2007, 1, 8, 13, 15,
//						22)));
		// test float
		assertEquals(CF.createFloat(1.3498f), IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF
				.createWsmlFloat("1.3498")));
		// test gday
		assertEquals(CF.createGDay(15), IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF
				.createWsmlGregorianDay(15)));
		// test gmonth
		assertEquals(CF.createGMonth(10), IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF
				.createWsmlGregorianMonth(10)));
		// test gmonthday
		assertEquals(CF.createGMonthDay(10, 4), IrisStratifiedFacade
				.convertWsmo4jDataValueToIrisTerm(DF.createWsmlGregorianMonthDay(10, 4)));
		// test gyear
		assertEquals(CF.createGYear(2007), IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF
				.createWsmlGregorianYear(2007)));
		// test gyearmonth
		assertEquals(CF.createGYearMonth(2007, 10), IrisStratifiedFacade
				.convertWsmo4jDataValueToIrisTerm(DF.createWsmlGregorianYearMonth(2007, 10)));
		// test base64binary
		assertEquals(CF.createHexBinary("12AF"), IrisStratifiedFacade
				.convertWsmo4jDataValueToIrisTerm(DF.creatWsmlHexBinary("12AF".getBytes())));
		// test int
		assertEquals(CF.createInteger(23), IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF
				.createWsmlInteger("23")));
		// test iri
		// iri is a term and not a datavalue!
		// test sqname
		// i'm sorry, but i never saw something like an sqname in wsmo4j...
		// test string
		assertEquals(TF.createString("asdf"), IrisStratifiedFacade.convertWsmo4jDataValueToIrisTerm(DF
				.createWsmlString("asdf")));
		// test time
		// TODO: i'm sorry again, but time isn't implemented in iris at the
		// moment
	}
}
