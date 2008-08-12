/*
 * Copyright (c) 2005 National University of Ireland, Galway
 *                    University of Innsbruck, Austria
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

package abstractTests.lp;

import helper.LPHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import abstractTests.LPTest;

/** 
 * Interface or class description
 * 
 * @author Adrian Mocan, Holger Lausen
 *
 * Created on 17-Feb-2006
 * Committed by $Author: graham $
 * 
 * $Source: /home/richi/temp/w2r/wsml2reasoner/test/variant/rule/FunctionSymbolsTest.java,v $, 
 * @version $Revision: 1.3 $ $Date: 2007-08-14 16:53:36 $
 */

public abstract class AbstractFunctionSymbols extends TestCase implements LPTest{

    private static final String NS = "http://examples.com/ontologies/mytravel#";

    private static final String ONTOLOGY_FILE = "files/Travel.wsml";
    
    public void testFSHasVoucher() throws Exception {
        String query = "?x memberOf travelVoucher";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        List <Term> terms = new ArrayList <Term>();
        terms.add(wsmoFactory.createIRI(NS+"my_trainTicket1"));
        binding.put(leFactory.createVariable("x"), 
                leFactory.createConstructedTerm(
                        wsmoFactory.createIRI(NS+ "f"),terms));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        terms = new ArrayList <Term>();
        terms.add(wsmoFactory.createIRI(NS+"my_trainTicket2"));
        binding.put(leFactory.createVariable("x"), 
                leFactory.createConstructedTerm(
                        wsmoFactory.createIRI(NS+ "f"),terms));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        terms = new ArrayList <Term>();
        terms.add(wsmoFactory.createIRI(NS+"my_trainTicket3"));
        binding.put(leFactory.createVariable("x"), 
                leFactory.createConstructedTerm(
                        wsmoFactory.createIRI(NS+ "f"),terms));
        expected.add(binding);
//        performQuery(query, expected);
//        System.out.println("Finished query.");
    	LPHelper.executeQuery( LPHelper.loadOntology(ONTOLOGY_FILE ), query, expected, getReasoner() );
    }
    
    public void testFSHasValidVoucher() throws Exception {
        String query = "?x memberOf validVoucher";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        List <Term> terms = new ArrayList <Term>();
        terms.add(wsmoFactory.createIRI(NS+"my_trainTicket1"));
        terms.add(wsmoFactory.createIRI(NS+"customer1"));
        binding.put(leFactory.createVariable("x"), 
                leFactory.createConstructedTerm(
                        wsmoFactory.createIRI(NS+ "f"),terms));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        terms = new ArrayList <Term>();
        terms.add(wsmoFactory.createIRI(NS+"my_trainTicket2"));
        terms.add(wsmoFactory.createIRI(NS+"customer2"));
        binding.put(leFactory.createVariable("x"), 
                leFactory.createConstructedTerm(
                        wsmoFactory.createIRI(NS+ "f"),terms));
        expected.add(binding);
    	LPHelper.executeQuery( LPHelper.loadOntology(ONTOLOGY_FILE ), query, expected, getReasoner() );
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

