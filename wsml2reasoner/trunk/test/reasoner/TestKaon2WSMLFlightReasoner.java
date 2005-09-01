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
package reasoner;

import java.util.HashSet;
import java.util.Set;

import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsml.reasoner.impl.VariableBindingImpl;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.BaseReasonerTest;

public class TestKaon2WSMLFlightReasoner extends BaseReasonerTest {

    private static final String NS = "urn:fzi:lordoftherings#";

    private static final String ONTOLOGY_FILE = "examples/lordOfTheRings.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestKaon2WSMLFlightReasoner.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                TestKaon2WSMLFlightReasoner.class)) {
            protected void setUp() throws Exception {
                setupScenario(ONTOLOGY_FILE);
             }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }
    
    public void testArwenIsWoman() throws Exception {
        String query = "?x memberOf Elf";
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("x", NS + "Arwen");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }

    public void testAragornLovesElf() throws Exception {
        String query = "?x[loves hasValue ?y] and ?y memberOf Elf";
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("x", NS + "Aragorn");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }
    
    public void testAragornIsElendilsHeir() throws Exception {
        String query = "Aragorn[hasAncestor hasValue Elendil]";
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }

}
