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
package org.wsml.reasoner.ext.sql.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import junit.framework.TestCase;


public class QueryResultTableTest extends TestCase {
	
	protected QueryResultTable table;
	protected WSMO4JManager wsmoManager;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Ontology ontology;

	public QueryResultTableTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager(); 
		wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));	
		table = new QueryResultTable();
		
	}
	
	public void testTable() throws ParserException {
		Variable v1 = leFactory.createVariable("var01");
		Variable v2 = leFactory.createVariable("var02");
		Term t1 = (Term) (v1);
		Term t2 = (Term) (v2);
		
		Map<Variable, Term> map = new HashMap<Variable, Term>();
		map.put(v1, t1);
		map.put(v2, t2);
		
		Set<Map<Variable,Term>> set = new HashSet<Map<Variable,Term>>();
		set.add(map);
		table.setContent(set, ontology);
		assertEquals(2,table.getColumnCount());
		assertEquals(1,table.getRowCount());
		
	    assertEquals(v1.toString(),table.getValueAt(0,0).toString());
	    assertEquals(v2.toString(),table.getValueAt(0,1).toString());
	    
	    assertEquals( "var01", table.getColumnName(0).toString());
	    assertEquals( "var02", table.getColumnName(1).toString());
	 
	
	}
	
	
	
	

}
