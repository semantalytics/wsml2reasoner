/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wsml.reasoner.builtin.iris;

import java.util.Arrays;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.IConcreteFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.IConstructedTerm;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.Literal;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * <p>
 * Tests for the iris facade.
 * </p>
 */
public class IrisFacadeTest extends TestCase {

	FactoryContainer FACTORY;

	public void testGetTZData() {
		assertTrue("result must be tzSign=1, tzHour=1, tzMin=0", Arrays.equals(new int[] { 1, 1, 0 }, TermHelper.getTZData(TimeZone.getTimeZone("GMT+1"))));
		;
		assertTrue("result must be tzSign=0, tzHour=0, tzMin=0", Arrays.equals(new int[] { 0, 0, 0 }, TermHelper.getTZData(TimeZone.getTimeZone("GMT"))));
		;
		assertTrue("result must be tzSign=-1, tzHour=10, tzMin=30", Arrays.equals(new int[] { -1, 10, 30 }, TermHelper.getTZData(TimeZone
				.getTimeZone("GMT-10:30"))));
		;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		FACTORY = new WsmlFactoryContainer();
	}

	public void testConvertTermFromWsmo4jToIris() {
		final WsmoFactory WF = FACTORY.getWsmoFactory();
		final DataFactory DF = FACTORY.getXmlDataFactory();
		final LogicalExpressionFactory LF = FACTORY.getLogicalExpressionFactory();
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		// test variable
		assertEquals(TF.createVariable("asdf"), TermHelper.convertTermFromWsmo4jToIris(LF.createVariable("asdf")));
		// test iri
		assertEquals(CF.createIri("http://my.iri"), TermHelper.convertTermFromWsmo4jToIris(WF.createIRI("http://my.iri")));

		// test constructed term
		final ConstructedTerm wc = LF.createConstructedTerm(WF.createIRI("http://constr"), Arrays.asList((Term) DF.createString("a"), LF
				.createConstructedTerm(WF.createIRI("http://inner"), Arrays.asList((Term) DF.createString("b"))), (Term) DF.createString("c")));
		final IConstructedTerm ic = TF.createConstruct("http://constr", TF.createString("a"), TF
				.createConstruct("http://inner", TF.createString("b")), TF.createString("c"));
		assertEquals(ic, TermHelper.convertTermFromWsmo4jToIris(wc));
		// test builtins
		// TODO: not implemented yet
		// test datavalues
		// datavalues got their own test.

	}

	public void testConvertTermFromIrisToWsmo4j() {
		final WsmoFactory WF = FACTORY.getWsmoFactory();
		final DataFactory DF = FACTORY.getXmlDataFactory();
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final LogicalExpressionFactory LF = FACTORY.getLogicalExpressionFactory();
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		// test constructed
		final ConstructedTerm wc = LF.createConstructedTerm(WF.createIRI("http://constr"), Arrays.asList((Term) DF.createString("a"), LF
				.createConstructedTerm(WF.createIRI("http://inner"), Arrays.asList((Term) DF.createString("b"))), (Term) DF.createString("c")));
		final IConstructedTerm ic = TF.createConstruct("http://constr", TF.createString("a"), TF
				.createConstruct("http://inner", TF.createString("b")), TF.createString("c"));
		assertEquals(wc, TermHelper.convertTermFromIrisToWsmo4j(ic, FACTORY));
		// test string
		assertEquals(DF.createString("asdf"), TermHelper.convertTermFromIrisToWsmo4j(TF.createString("asdf"), FACTORY));
		// test variable
		assertEquals(LF.createVariable("asdf"), TermHelper.convertTermFromIrisToWsmo4j(TF.createVariable("asdf"), FACTORY));
		// test Base64Binary
		assertEquals(DF.createBase64Binary("asdf".getBytes()), TermHelper.convertTermFromIrisToWsmo4j(CF.createBase64Binary("asdf"), FACTORY));
		// test boolean
		assertEquals(DF.createBoolean("true"), TermHelper.convertTermFromIrisToWsmo4j(CF.createBoolean(true), FACTORY));
		// test date
		assertEquals(DF.createDate(2007, 1, 20, 0, 0, 0), TermHelper.convertTermFromIrisToWsmo4j(CF.createDate(2007, 1, 20), FACTORY));
		// test datetime
		assertEquals(DF.createDateTime(2007, 1, 20, 13, 45, 11, 0, 0, 0), TermHelper.convertTermFromIrisToWsmo4j(
				CF.createDateTime(2007, 1, 20, 13, 45, 11, 0, 0, 0), FACTORY));
		// test decimal
		assertEquals(DF.createDecimal("3.1415"), TermHelper.convertTermFromIrisToWsmo4j(CF.createDecimal(3.1415d), FACTORY));
		// test double
		assertEquals(DF.createDouble("3.1415"), TermHelper.convertTermFromIrisToWsmo4j(CF.createDouble(3.1415d), FACTORY));
		// test duration
		assertEquals(DF.createDuration(+1, 5, 3, 5, 12, 16, 11.0), TermHelper.convertTermFromIrisToWsmo4j(
				CF.createDuration(true, 5, 3, 5, 12, 16, 11.0), FACTORY));
		assertEquals(DF.createDuration(-1, 5, 3, 5, 12, 16, 11.0), TermHelper.convertTermFromIrisToWsmo4j(CF
				.createDuration(false, 5, 3, 5, 12, 16, 11.0), FACTORY));
		assertEquals(DF.createDuration(-1, 0, 3, 5, 12, 16, 11.0), TermHelper.convertTermFromIrisToWsmo4j(CF
				.createDuration(false, 0, 3, 5, 12, 16, 11.0), FACTORY));
		// test daytime-duration
		assertEquals(DF.createDayTimeDuration(1, 5, 12, 16, 11.0), TermHelper.convertTermFromIrisToWsmo4j(CF
				.createDayTimeDuration(true, 5, 12, 16, 11.0), FACTORY));
		assertEquals(DF.createDayTimeDuration(-1, 5, 12, 16, 11.0), TermHelper.convertTermFromIrisToWsmo4j(
				CF.createDayTimeDuration(false, 5, 12, 16, 11.0), FACTORY));
		assertEquals(DF.createDayTimeDuration(-1, 0, 12, 16, 11.0), TermHelper.convertTermFromIrisToWsmo4j(
				CF.createDayTimeDuration(false, 0, 12, 16, 11.0), FACTORY));
		// test yearmonth-duration
		assertEquals(DF.createYearMonthDuration(1, 5, 3), TermHelper.convertTermFromIrisToWsmo4j(CF.createYearMonthDuration(true, 5, 3), FACTORY));
		assertEquals(DF.createYearMonthDuration(-1, 5, 3), TermHelper.convertTermFromIrisToWsmo4j(CF.createYearMonthDuration(false, 5, 3), FACTORY));
		assertEquals(DF.createYearMonthDuration(-1, 0, 3), TermHelper.convertTermFromIrisToWsmo4j(CF.createYearMonthDuration(false, 0, 3), FACTORY));
		// test float
		assertEquals(DF.createFloat("3.1415"), TermHelper.convertTermFromIrisToWsmo4j(CF.createFloat(3.1415f), FACTORY));
		// test gday
		assertEquals(DF.createGregorianDay(15), TermHelper.convertTermFromIrisToWsmo4j(CF.createGDay(15), FACTORY));
		// test gmonth
		assertEquals(DF.createGregorianMonth(2), TermHelper.convertTermFromIrisToWsmo4j(CF.createGMonth(2), FACTORY));
		// test gmonthday
		assertEquals(DF.createGregorianMonthDay(5, 15), TermHelper.convertTermFromIrisToWsmo4j(CF.createGMonthDay(5, 15), FACTORY));
		// test gyear
		assertEquals(DF.createGregorianYear(2010), TermHelper.convertTermFromIrisToWsmo4j(CF.createGYear(2010), FACTORY));
		// test gyearmonth
		assertEquals(DF.createGregorianYearMonth(2010, 5), TermHelper.convertTermFromIrisToWsmo4j(CF.createGYearMonth(2010, 5), FACTORY));
		// test hexbinary
		assertEquals(DF.createHexBinary("15AB".getBytes()), TermHelper.convertTermFromIrisToWsmo4j(CF.createHexBinary("15AB"), FACTORY));
		// test integer
		assertEquals(DF.createInteger("15"), TermHelper.convertTermFromIrisToWsmo4j(CF.createInteger(15), FACTORY));
		// test iri
		assertEquals(WF.createIRI("http://my.iri"), TermHelper.convertTermFromIrisToWsmo4j(CF.createIri("http://my.iri"), FACTORY));
		// test sqname
		// there is no sqname in wsmo4j
	}

	public void testBuiltinsExtended() {
		final DataFactory DF = FACTORY.getXmlDataFactory();
		// final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		assertEquals(DF.createGregorianDay(1), TermHelper.convertTermFromIrisToWsmo4j(CF.createGDay(1), FACTORY));
	}

	public void testLiteral2Atom() {
		final DataFactory DF = FACTORY.getXmlDataFactory();
		final LogicalExpressionFactory LF = FACTORY.getLogicalExpressionFactory();
		final IBasicFactory BF = org.deri.iris.factory.Factory.BASIC;
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;

		Literal wsmlLiteral = new Literal(true, "lit", DF.createString("a"), LF.createVariable("b"), DF.createString("c"));

		IAtom expected = BF.createAtom(BF.createPredicate("lit", 3), BF.createTuple(TF.createString("a"), TF.createVariable("b"), TF
				.createString("c")));
		assertEquals(expected, LiteralHelper.literal2Atom(wsmlLiteral, false));
	}

	public void testLiteral2Literal() {
		final IBasicFactory BF = org.deri.iris.factory.Factory.BASIC;
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final DataFactory DF = FACTORY.getXmlDataFactory();
		final LogicalExpressionFactory LF = FACTORY.getLogicalExpressionFactory();

		assertEquals(BF.createLiteral(false, BF.createPredicate("lit", 3), BF.createTuple(TF.createString("a"), TF.createVariable("b"), TF
				.createString("c"))), LiteralHelper.literal2Literal(new Literal(false, "lit", DF.createString("a"), LF.createVariable("b"), DF
				.createString("c")), false));
		assertEquals(BF.createLiteral(true, BF.createPredicate("lit", 3), BF.createTuple(TF.createString("a"), TF.createVariable("b"), TF
				.createString("c"))), LiteralHelper.literal2Literal(new Literal(true, "lit", DF.createString("a"), LF.createVariable("b"), DF
				.createString("c")), false));
	}

	public void testConvertWsmo4jDataValueToIrisTerm() {
		final DataFactory DF = FACTORY.getXmlDataFactory();
		final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
		final IConcreteFactory CF = org.deri.iris.factory.Factory.CONCRETE;

		// test base64binary
		assertEquals(CF.createBase64Binary("asdfxQ=="), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createBase64Binary("asdfxQ==".getBytes())));
		// test boolean
		assertEquals(CF.createBoolean(true), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createBoolean(true)));
		// test date
		assertEquals(CF.createDate(2007, 1, 8), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDate(2007, 1, 8, 0, 0, 0)));
		// test datetime
		assertEquals(CF.createDateTime(2007, 1, 8, 13, 15, 22, 1, 0), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDateTime(2007, 1, 8, 13,
				15, 22, 1, 1, 0)));
		// test decimal
		assertEquals(CF.createDecimal(1.3498), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDecimal("1.3498")));
		// test double
		assertEquals(CF.createDouble(1.3498), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDouble("1.3498")));
		// test duration
		assertEquals(CF.createDuration(true, 2007, 1, 8, 13, 15, 22), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDuration(+1, 2007, 1, 8, 13,
				15, 22)));
		assertEquals(CF.createDuration(false, 2007, 1, 8, 13, 15, 22), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDuration(-1, 2007, 1, 8, 13,
				15, 22)));
		assertEquals(CF.createDuration(false, 0, 1, 8, 13, 15, 22), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDuration(-1, 0, 1, 8, 13, 15,
				22)));
		// test daytime-duration
		assertEquals(CF.createDayTimeDuration(true, 8, 13, 15, 22), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDayTimeDuration(+1, 8, 13, 15,
				22)));
		assertEquals(CF.createDayTimeDuration(false, 8, 13, 15, 22), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDayTimeDuration(-1, 8, 13, 15,
				22)));
		assertEquals(CF.createDayTimeDuration(false, 0, 13, 15, 22), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createDayTimeDuration(-1, 0, 13, 15,
				22)));
		// test yearmonth-duration
		assertEquals(CF.createYearMonthDuration(true, 2007, 1), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createYearMonthDuration(+1, 2007, 1)));
		assertEquals(CF.createYearMonthDuration(false, 2007, 1), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createYearMonthDuration(-1, 2007, 1)));
		assertEquals(CF.createYearMonthDuration(false, 0, 1), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createYearMonthDuration(-1, 0, 1)));
		assertEquals(CF.createYearMonthDuration(false, 0, 0), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createYearMonthDuration(0, 0, 0)));
		assertEquals(CF.createYearMonthDuration(true, 0, 0), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createYearMonthDuration(0, 0, 0)));
		// test float
		assertEquals(CF.createFloat(1.3498f), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createFloat("1.3498")));
		// test gday
		assertEquals(CF.createGDay(15), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createGregorianDay(15)));
		// test gmonth
		assertEquals(CF.createGMonth(10), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createGregorianMonth(10)));
		// test gmonthday
		assertEquals(CF.createGMonthDay(10, 4), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createGregorianMonthDay(10, 4)));
		// test gyear
		assertEquals(CF.createGYear(2007), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createGregorianYear(2007)));
		// test gyearmonth
		assertEquals(CF.createGYearMonth(2007, 10), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createGregorianYearMonth(2007, 10)));
		// test base64binary
		assertEquals(CF.createHexBinary("12AF"), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createHexBinary("12AF".getBytes())));
		// test int
		assertEquals(CF.createInteger(23), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createInteger("23")));
		// test iri
		// iri is a term and not a datavalue!
		// test sqname
		// i'm sorry, but i never saw something like an sqname in wsmo4j...
		// test string
		assertEquals(TF.createString("asdf"), TermHelper.convertWsmo4jDataValueToIrisTerm(DF.createString("asdf")));
		// test time
		// TODO: i'm sorry again, but time isn't implemented in iris at the
		// moment

		// FIXME test all datatypes!!
	}

}
