/*
 * Copyright (c) 2005 National University of Ireland, Galway
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA  
 */

package reasoner.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

import test.BaseReasonerTest;

/** 
 * Interface or class description
 * 
 * @author Adrian Mocan
 *
 * Created on 17-Feb-2006
 * Committed by $Author: adrian $
 * 
 * $Source: /home/richi/temp/w2r/wsml2reasoner/test/reasoner/rule/FunctionSymbolsTest.java,v $, 
 * @version $Revision: 1.1 $ $Date: 2006-02-17 16:07:11 $
 */

public class FunctionSymbolsTest extends BaseReasonerTest {

    private static final String NS = "http://examples.com/ontologies/travel#";

    private static final String ONTOLOGY_FILE = "reasoner/flight/Travel.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FunctionSymbolsTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                FunctionSymbolsTest.class)) {
            protected void setUp() throws Exception {
                setupScenario(ONTOLOGY_FILE);
            }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }

    public void testFunctionSymbols() throws Exception {
        String query = "?x memberOf travelVoucher";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS
                + "f(" + NS + "my_trainTicket)"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

}

