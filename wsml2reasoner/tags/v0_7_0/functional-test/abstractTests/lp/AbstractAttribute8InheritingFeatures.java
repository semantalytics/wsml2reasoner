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

public abstract class AbstractAttribute8InheritingFeatures extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "files/attribute8_inheriting_features.wsml";
	
	public void testInverseOf1() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "gordi[child hasValue baz] memberOf Human", r.get(), getLPReasoner());
	}

	public void testInverseOf2() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "lola[parent hasValue baz] memberOf Human", r.get(), getLPReasoner());
	}

	public void testTransitive() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "baz[ ancestor hasValue arthur ] memberOf Human", r.get(), getLPReasoner());
	}
	
	public void testSymmetric() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "bizzi[marriedTo hasValue baz] memberOf Human", r.get(), getLPReasoner());
	}

	public void testReflexive() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "baz[friend hasValue baz]", r.get(), getLPReasoner());
	}
}
