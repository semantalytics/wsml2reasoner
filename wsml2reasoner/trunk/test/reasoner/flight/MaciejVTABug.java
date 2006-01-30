/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package reasoner.flight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

import test.BaseReasonerTest;

public class MaciejVTABug extends BaseReasonerTest {

    private static final String NS = "http://www.wsmo.org/TR/d13/d13.7/ontologies/VTAServiceOntology#";

    private static final String ONTOLOGY_FILE = "reasoner/flight/VTAServiceOntology.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MaciejVTABug.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                MaciejVTABug.class)) {
            protected void setUp() throws Exception {
                setupScenario(ONTOLOGY_FILE);
            }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }

    public void testGalwayIsSource() throws Exception {
        String query = "?x[sourceLocBool hasValue t]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS
                + "galway_station"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

    public void testStations() throws Exception {
        String query = "?x memberOf station";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS
                + "galway_station"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS
                + "dublin_station"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

}
