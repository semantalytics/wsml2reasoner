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

import org.omwg.logicalexpression.terms.Term;
import org.wsmo.factory.WsmoFactory;

public class LiteralTestHelper {

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
		WsmoFactory wf = org.wsmo.factory.Factory.createWsmoFactory(null);
		Term[] terms;
		terms = new Term[iriNames.length];
		int i = 0;
		for (String str : iriNames) {
			terms[i] = wf.createIRI(str);
			i++;
		}
		Literal h = new Literal(isPositive, predicate, terms);
		return h;
	}
	
	public static Term createSimpleTerm(String iriName) {
		WsmoFactory wf = org.wsmo.factory.Factory.createWsmoFactory(null);
		return wf.createIRI(iriName);
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

}
