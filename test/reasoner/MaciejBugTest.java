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

public class MaciejBugTest extends BaseReasonerTest {

    private static final String NS = "http://www.wsmo.org/TR/d13/d13.7/ontologies/VTAServiceOntology#";

    private static final String ONTOLOGY_FILE = "examples/VTAServiceOntology.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MaciejBugTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                MaciejBugTest.class)) {
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
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("x", NS + "galway_station");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testStations() throws Exception {
        String query = "?x memberOf station";
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("x", NS + "galway_station");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("x", NS + "dublin_station");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

}
