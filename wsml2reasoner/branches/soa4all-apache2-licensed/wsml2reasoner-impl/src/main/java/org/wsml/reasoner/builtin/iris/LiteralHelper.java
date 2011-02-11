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

import static org.deri.iris.factory.Factory.BASIC;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.terms.ITerm;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.Literal;

public class LiteralHelper {
	
	private LiteralHelper() {}
	
	/**
	 * Converts a wsmo4j literal to an iris literal.
	 * 
	 * @param l the wsmo4j literal to convert
	 * @return the iris literal
	 */
	static ILiteral literal2Literal(final Literal l, boolean headLiteral) {
		assert l != null;
		assert (headLiteral && l.isPositive()) || !headLiteral;

		return BASIC
				.createLiteral(l.isPositive(), literal2Atom(l, headLiteral));
	}

	/**
	 * Converts a wsml reasoner atomic formula in a literal to an iris atom.
	 * 
	 * @param literal the wsmo4j literal to convert
	 * @return the iris atom
	 */
	static IAtom literal2Atom(Literal literal, boolean headLiteral) {
		// System.out.println("Literal : " + literal);
		assert literal != null;

		String sym = literal.getPredicateUri();
		Term[] inTerms = literal.getTerms();

		assert sym != null;
		assert inTerms != null;

		final List<ITerm> terms = new ArrayList<ITerm>(inTerms.length);
		// convert the terms of the literal
		for (final Term t : inTerms) {
			terms.add(
					TermHelper.convertTermFromWsmo4jToIris(t));
		}

		IAtom atom = BuiltinHelper.checkBuiltin(headLiteral, sym, terms);

		// TODO Handle built-ins with terms representing built-ins.
		// An example for this is: wsml#equal(wsml#numericAdd(1, 1), 2).

		return atom;
	}

}
