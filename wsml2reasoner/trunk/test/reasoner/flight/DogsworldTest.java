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

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsmo.common.IRI;

import test.BaseReasonerTest;

public class DogsworldTest extends BaseReasonerTest {
    private static final String NS = "urn:dogsworld#";
    private static final String ONTOLOGY_FILE = "reasoner/flight/dogsworld.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DogsworldTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                DogsworldTest.class)) {
            protected void setUp() throws Exception {
                setupScenario(ONTOLOGY_FILE);
             }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }
    
    public void testSubconceptsOfMammal() throws Exception {
        String query = "?x subConceptOf Mammal";
        Set<Map<Variable,Term>> expected = new HashSet<Map<Variable,Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Human"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "DogOwner"));
        expected.add(binding);
        performQuery(query, expected);
    }
    
    public void testConsistency() throws Exception {
        assertTrue(((WSMLFlightReasoner)wsmlReasoner).isSatisfiable((IRI)o.getIdentifier()));
    }



    public void testAnneCatOwner() throws Exception {
        String query = "Anne memberOf CatOwner";
        LogicalExpression qExpression = leFactory.createLogicalExpression(query, o);
        assertTrue(wsmlReasoner.executeGroundQuery((IRI)o.getIdentifier(), qExpression));
    }

}