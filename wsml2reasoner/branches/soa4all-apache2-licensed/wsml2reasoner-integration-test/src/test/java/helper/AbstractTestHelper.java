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
package helper;

import java.util.Map;
import java.util.Set;

import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.Literal;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class AbstractTestHelper {

	protected static final FactoryContainer FACTORY = new WsmlFactoryContainer();
	
	protected static final WsmoFactory WF = FACTORY.getWsmoFactory();
	protected static final IBasicFactory BF = org.deri.iris.factory.Factory.BASIC;
	protected static final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
	
	// TODO gigi: probably won't work as expected since the factories changed and are now stateful
	protected static WsmoFactory wsmoFactory = FACTORY.getWsmoFactory();
	protected static DataFactory wsmlDataFactory = FACTORY.getXmlDataFactory();
	protected static DataFactory xmlDataFactory = FACTORY.getXmlDataFactory();
	protected static final LogicalExpressionFactory LF = FACTORY.getLogicalExpressionFactory();


	public static Term createIRI(String iriName) {
		return WF.createIRI(iriName);
	}
	
	public static Literal createLiteral(boolean isPositive, String predicateUri, Term... terms) {
		Literal wsmlLiteral = new Literal(isPositive, predicateUri, terms);
		return wsmlLiteral;
	}
	
	public static Variable createVariable(String name){
		return LF.createVariable(name);
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
