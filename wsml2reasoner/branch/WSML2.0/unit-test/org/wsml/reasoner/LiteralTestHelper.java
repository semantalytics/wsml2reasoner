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
package org.wsml.reasoner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class LiteralTestHelper {

	
	protected static final WsmoFactory WF = FactoryImpl.getInstance().createWsmoFactory();
	protected static final DataFactory DF = FactoryImpl.getInstance().createWsmlDataFactory(WF);
	protected static final IBasicFactory BF = org.deri.iris.factory.Factory.BASIC;
	protected static final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
	protected static final DataFactory XF = FactoryImpl.getInstance().createXmlDataFactory(WF);
	protected static final LogicalExpressionFactory LF = FactoryImpl.getInstance().createLogicalExpressionFactory(WF, DF, XF);

	public static Literal createSimplePosLiteral(String name) {
		return createLiteral(true, name, new String[0]);
	}

	public static Literal createSimpleNegLiteral(String name) {
		return createLiteral(false, name, new String[0]);
	}

	public static Literal createPosLiteral(String wsmlString,
			String... iriNames) {
		return createLiteral(true, wsmlString, iriNames);
	}

	public static Literal createNegLiteral(String wsmlString,
			String... iriNames) {
		return createLiteral(false, wsmlString, iriNames);
	}

	public static Literal createLiteral(boolean isPositive, String predicate,
			String... iriNames) {
		Term[] terms;
		terms = new Term[iriNames.length];
		int i = 0;
		for (String str : iriNames) {
			terms[i] = WF.createIRI(str);
			i++;
		}
		Literal h = new Literal(isPositive, predicate, terms);
		return h;
	}

	public static Term createSimpleTerm(String iriName) {
		return WF.createIRI(iriName);
	}

	public static Rule createRule(Literal head, Literal... body) {
		LinkedList<Literal> bodylist = new LinkedList<Literal>();
		for (Literal l : body) {
			bodylist.add(l);
		}
		return new Rule(head, bodylist);
	}

	public static Rule createRule(Literal head, List<Literal> body) {
		return new Rule(head, body);
	}

	public static Term createIRI(String iriName) {
		return WF.createIRI(iriName);
	}
	
	public static IStringTerm createString(String str){
		return TF.createString(str);
	}
	
	public static SimpleDataValue createWsmlString(String str){
		return DF.createString(str);
	}
	
	public static ILiteral createLiteral(boolean isPositive,
			IPredicate pred, ITuple tuple) {
		return BF.createLiteral(isPositive, pred, tuple);
	}

	public static Literal createLiteral(boolean isPositive,
			String predicateUri, Term... terms) {
		Literal wsmlLiteral = new Literal(isPositive, predicateUri, terms);
		return wsmlLiteral;
	}

	public static IVariable createIVariable(String name) {
		return TF.createVariable(name);
	}
	
	public static Variable createVariable(String name) {
		return LF.createVariable(name);
	}
	
	public static IPredicate createPredicate(String symbol, int arity){
		return BF.createPredicate(symbol, arity);
	}
	
	public static ITuple createTuple(ITerm ... terms){
		return BF.createTuple(terms);
	}
	
	public static ITuple createTuple(List<ITerm> list){
		return BF.createTuple(list);
	}
	
	public static IAtom createAtom(IPredicate pred, ITuple tuple) {
		return BF.createAtom(pred, tuple);
	}

	public static boolean checkIsIn(Set<Map<Variable, Term>> result,
			String varName, Term expected) {
		for (Map<Variable, Term> vBinding : result) {
			for (Variable var : vBinding.keySet()) {
				if (var.toString().equals(varName)) {
					if ((vBinding.get(var).equals(expected))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void printResult(Set<Map<Variable, Term>> result, String query) {
		// print out the results:
		System.out.println("The query '" + query
				+ "' has the following results: ");
		for (Map<Variable, Term> vBinding : result) {
			for (Variable var : vBinding.keySet()) {
				System.out.print(var + ": " + (vBinding.get(var)) + "\t ");
			}
			System.out.println();
		}
		System.out.println();
	}

}
