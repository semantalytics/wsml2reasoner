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

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;


public class QueryResultTableTest extends TestCase {
	
	private QueryResultTable table;
	private String ns = "http://ex.org#";
	private WsmoFactory wsmoFactory;
	private LogicalExpressionFactory leFactory;
	private DataFactory dataFactory;
	private Ontology ontology;

	public QueryResultTableTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager(); 
		wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        dataFactory = wsmoManager.getWsmlDataFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));	
		table = new QueryResultTable();
		
	}
	
	public void testTable() throws ParserException {
		Variable v1 = leFactory.createVariable("var01");
		Variable v2 = leFactory.createVariable("var02");
		Term t1 = dataFactory.createString( "term1" );
		Term t2 = dataFactory.createString( "term2" );
		
		Map<Variable, Term> row = new HashMap<Variable, Term>();
		row.put(v1, t1);
		row.put(v2, t2);
		
		Set<Map<Variable,Term>> resultSet = new HashSet<Map<Variable,Term>>();
		resultSet.add(row);
		table.setContent(resultSet);
		assertEquals(2,table.getColumnCount());
		assertEquals(1,table.getRowCount());
		
		String colName1 = table.getColumnName(0);
		String colName2 = table.getColumnName(1);
		
		// The ordering of the elements in 'row' can not be assured!
		assertTrue( colName1.equals( "var01" ) && colName2.equals( "var02" ) ||
					colName1.equals( "var02" ) && colName2.equals( "var01" ) );
		
		String value1 = table.getValueAt(0,0).toString();
		String value2 = table.getValueAt(0,1).toString();
		
		assertTrue( value1.equals( "\"term1\"" ) && value2.equals( "\"term2\"" ) ||
					value1.equals( "\"term2\"" ) && value2.equals( "\"term1\"" ) );
	}
}
