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

/**
 * To test the new feature of wsml 2.0 core of the new feature
 * "instance equivalence", which allows the definition that two instances are
 * equivalent.
 */
public abstract class AbstractWSMLCore_2_0Example extends TestCase implements
		LP {

	private static final String ONTOLOGY_FILE = "files/example_wsml-core-2.0.wsml";

	private static final String QUERY = "ElBarto[name hasValue ?name, ageInYears hasValue ?age]";

	public AbstractWSMLCore_2_0Example() {
		super();
	}

	/**
	 * Who is "El Barto"? In axiom "thisIsElBarto" we make use of the new
	 * feature "instance equivalence", which allows the definition that two
	 * instances are equivalent.
	 * 
	 * @throws Exception
	 */
	public void test_instance_equivalence() throws Exception {
		Results r = new Results("name", "age");
		r.addBinding(Results._string("El Barto"), Results._integer(10));
		r.addBinding(Results._string("Bart Simpson"), Results._integer(10));

		LPHelper.outputON();
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), QUERY, r.get(), getLPReasoner());
	}

}
