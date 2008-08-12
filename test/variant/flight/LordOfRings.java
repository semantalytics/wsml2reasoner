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
package variant.flight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

/**
 * lordOfTheRings.wsml is not valid
 * @author grahen
 *
 */
public class LordOfRings extends BaseReasonerTest {

    private static final String NS = "urn:fzi:lordoftherings#";

    private static final String ONTOLOGY_FILE = "files/lordOfTheRings.wsml";

    BuiltInReasoner previous;

    protected void setUp() throws Exception {
    	super.setUp();
        setupScenario(ONTOLOGY_FILE); 
        previous =  BaseReasonerTest.reasoner;             
     }

    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
    }
    public void arwenIsFemale() throws Exception {
        String query = "?x memberOf Female";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }

    public void aragornLovesElf() throws Exception {
        String query = "?x[loves hasValue ?y] and ?y memberOf Elf";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(leFactory.createVariable("y"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }
    
    public void aragornLovesFemale() throws Exception {
        String query = "?x[loves hasValue ?y] and ?y memberOf Female";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(leFactory.createVariable("y"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }

    public void aragornIsElendilsHeir() throws Exception {
        String query = "Aragorn[hasAncestor hasValue Elendil]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }
    
    public void noMaleFemaleTogether() throws Exception {
        String query = "?x memberOf Male and ?x memberOf Female";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }
    
    public void males() throws Exception {
        String query = "?x memberOf Male";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Elendil"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arathorn"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void higherThan7Feet() throws Exception {
        String query = "?x[heightInFeet hasValue ?v] and ?v > 7.0";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlDecimal("7.5"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    /**
     * NOTE does not work for mins, since data type support is not perfext (wsml decimal will
     * be converted to numeric and .0 lost)
     * @throws Exception
     */
    public void higherEqualThan6Feet() throws Exception {
        String query = "?x[heightInFeet hasValue ?v] and ?v >= 6.0";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlDecimal("7.5"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlDecimal("6.0"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
        
    public void bornBefore900() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and ?v < 900   "   ;
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlInteger("500"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void bornBeforeEqual900() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and ?v =< 900  ";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlInteger("500"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlInteger("900"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void lowerThan7Feet() throws Exception {
        String query = "?x[heightInFeet hasValue ?v] and ?v < 7.0";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlDecimal("6.0"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void lowerEqualThan7AndHalfFeet() throws Exception {
        String query = "?x[heightInFeet hasValue ?v] and ?v =< 7.5";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlDecimal("7.5"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlDecimal("6.0"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void namedAragorn() throws Exception {
        String query = "?x[hasName hasValue ?v] and ?v = \"Aragorn\"";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlString("Aragorn"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void namedArwen() throws Exception {
        String query = "?x[hasName hasValue \"Arwen\"]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void born500() throws Exception {
        String query = "?x[wasBorn hasValue 500]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void height6Feet() throws Exception {
        String query = "?x[heightInFeet hasValue 6.0]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void happy() throws Exception {
        String query = "?x[isHappy hasValue _boolean(\"true\")]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void unHappy() throws Exception {
        String query = "?x[isHappy hasValue _boolean(\"false\")]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Gloin"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Gimli"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void born490And10() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(490 + 10))";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlInteger("500"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void born490And10LogicEqual() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and ?v = (490 + 10)";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(leFactory.createVariable("v"), dataFactory.createWsmlInteger("500"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void born510Minus10() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(510 - 10))";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void born250Twice() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(250 * 2))";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void born1000DividedBy2() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(1000 / 2))";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void happyOrSad() throws Exception {
        String query = "?x[isHappy hasValue _boolean(\"true\")] or ?x[isHappy hasValue _boolean(\"false\")]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Gloin"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Gimli"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testFlightReasoners() throws Exception{
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
    	aragornIsElendilsHeir();
    	aragornLovesElf();
    	aragornLovesFemale();
    	arwenIsFemale();
    	born1000DividedBy2();
    	born250Twice();
    	born490And10();
    	born490And10LogicEqual();
    	born500();
    	born510Minus10();
    	bornBefore900();
    	bornBeforeEqual900();
    	happy();
    	happyOrSad();
    	height6Feet();
    	higherEqualThan6Feet();
    	higherThan7Feet();
    	lowerEqualThan7AndHalfFeet();
    	lowerThan7Feet();
    	males();
    	namedAragorn();
    	namedArwen();
    	noMaleFemaleTogether();

    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
    	aragornIsElendilsHeir();
    	aragornLovesElf();
    	aragornLovesFemale();
    	arwenIsFemale();
    	born1000DividedBy2();
    	born250Twice();
    	born490And10();
    	born490And10LogicEqual();
    	born500();
    	born510Minus10();
    	bornBefore900();
    	bornBeforeEqual900();
    	happy();
    	happyOrSad();
    	height6Feet();
    	higherEqualThan6Feet();
    	higherThan7Feet();
    	lowerEqualThan7AndHalfFeet();
    	lowerThan7Feet();
    	males();
    	namedAragorn();
    	namedArwen();
    	noMaleFemaleTogether();
    	
    	if (exists("org.wsml.reasoner.builtin.kaon2.Kaon2Facade")) { 
    		resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
    	   	aragornIsElendilsHeir();
	    	aragornLovesElf();
	    	aragornLovesFemale();
	    	arwenIsFemale();
	    	born1000DividedBy2();
	    	born250Twice();
	    	born490And10();
	    	born490And10LogicEqual();
	    	born500();
	    	born510Minus10();
	    	bornBefore900();
	    	bornBeforeEqual900();
	    	happy();
	    	happyOrSad();
	    	height6Feet();
	    	higherEqualThan6Feet();
	    	higherThan7Feet();
	    	lowerEqualThan7AndHalfFeet();
	    	lowerThan7Feet();
	    	males();
	    	namedAragorn();
	    	namedArwen();
	    	noMaleFemaleTogether();
    	}
    	
    }

}
