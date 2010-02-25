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
package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LP;

public abstract class AbstractAttribute2ManyCardinalityConstraints extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "files/attribute2_many_cardinality_constraints.wsml";
	
	public void testAttributeInheritance() throws Exception {

		String ns = "http://example.com/attribute2#";

		Results r = new Results("y");
		r.addBinding(Results.iri(ns + "i2"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "i1[a1 hasValue ?y]", r.get(), getLPReasoner());
		
		r = new Results("x", "y", "z");
		for( int n = 1; n <= 250; ++n )
			r.addBinding(Results.iri(ns + "i1"), Results.iri(ns + "a" + n), Results.iri(ns + "i2"));
		
		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "?x[?y hasValue ?z]", r.get(), getLPReasoner());
	}
}
