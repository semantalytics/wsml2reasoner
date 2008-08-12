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
package abstractTests.lp;

import helper.LPHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import abstractTests.LPTest;

/**
 * Currently does not work with PELLET (due to detected inconsistency)
 */
public abstract class AbstractBoolean extends TestCase implements LPTest{
   
    public void testSimplerBoolean() throws Exception {
    	final String ns = "http://www.yabooleantest.org#";
    	
        Set<Map<Variable,Term>> expected = new HashSet<Map<Variable,Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(ns + "truth"));
        expected.add(binding);
        
    	LPHelper.executeQuery( LPHelper.loadOntology("files/simplerBoolean.wsml"), "?x[reallyExists hasValue _boolean(\"true\")]", expected, getReasoner() );
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testSimpleBoolean() throws Exception {
        String ns = "http://ex1.org#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "axiom a definedBy \n" +
                "a[f hasValue _boolean(\"false\")]. \n " +
                "a[t hasValue _boolean(\"true\")].\n " +
                "a(?a) :- a[?a hasValue ?x] and ?x != _boolean(\"false\"). ";
        
        Ontology ontology = LPHelper.parseOntology( test );
        
        {
	        Set<Map<Variable,Term>> expected = new HashSet<Map<Variable,Term>>();
	        Map<Variable, Term> binding = new HashMap<Variable, Term>();
	        binding.put(leFactory.createVariable("y"), dataFactory.createWsmlBoolean( false) );
	        expected.add(binding);
	
	
	        LPHelper.executeQuery( ontology, "a[f hasValue ?y]", expected, getReasoner() );
        }

        {
	        Set<Map<Variable,Term>> expected2 = new HashSet<Map<Variable,Term>>();
	        Map<Variable, Term> binding2 = new HashMap<Variable, Term>();
	        binding2.put(leFactory.createVariable("y"), dataFactory.createWsmlBoolean( true) );
	        expected2.add(binding2);
	
	        LPHelper.executeQuery( ontology, "a[t hasValue ?y]", expected2, getReasoner() );
        }        

        {
	        Set<Map<Variable,Term>> expected = new HashSet<Map<Variable,Term>>();
	        Map<Variable, Term> binding = new HashMap<Variable, Term>();
	        binding.put(leFactory.createVariable("y"), wsmoFactory.createIRI(ns+"t") );
	        expected.add(binding);

	        LPHelper.executeQuery( ontology, "a(?y)", expected, getReasoner() );
	    }
    }
    
    private static final WsmoFactory wsmoFactory;
    private static final LogicalExpressionFactory leFactory;
    private static final DataFactory dataFactory;
    private static final WSMO4JManager wsmoManager;
    
    static{
//  	 Set up factories for creating WSML elements
	   	wsmoManager = new WSMO4JManager();
	
	   	leFactory = wsmoManager.getLogicalExpressionFactory();
	   	wsmoFactory = wsmoManager.getWSMOFactory();
	   	dataFactory = wsmoManager.getDataFactory();
    }
}
