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

package org.wsml.reasoner.ext.sql;

import java.util.ArrayList;

import junit.framework.TestCase;


public class TableTest extends TestCase {
	
	private Table table;

	public TableTest() {
		super();
	}

	
	protected void setUp() throws Exception {
		super.setUp();
		table = new Table();
	}

	public void testInstantiate() {
		
		// store row, getRow
		table = new Table();
		ArrayList<Entry> row = new ArrayList<Entry>();
		row.add(new Entry("object1", "name1", this.getClass()));
		
		table.storeRow(0, row);
		assertEquals(table.getRow(0), row);
	
	
		/// getColumnCount, getRowCount
		table = new Table();
		assertEquals(table.getColumnCount(), 0);
		assertEquals(table.getRowCount(), 0);
		
		ArrayList<Entry> entryRow = new ArrayList<Entry>();
		entryRow.add(new Entry("object1", "name1", this.getClass()));
		entryRow.add(new Entry("object2", "name2", this.getClass()));
		table.storeRow(0, entryRow);
		assertEquals(table.getColumnCount(), 2);
	    assertEquals(table.getRowCount(), 1);	
	
	
	
		table = new Table();
		assertEquals(table.getColumnCount(), 0);
		assertEquals(table.getRowCount(), 0);
		
		ArrayList<Entry> entryRow1 = new ArrayList<Entry>();
		entryRow1.add(new Entry("object1", "name1", this.getClass()));
		entryRow1.add(new Entry("object2", "name2", this.getClass()));
		
		ArrayList<Entry> entryRow2 = new ArrayList<Entry>();
		entryRow2.add(new Entry("object1", "name1", this.getClass()));
		entryRow2.add(new Entry("object2", "name2", this.getClass()));
	
		
		table.storeRow(0,entryRow1);
		table.storeRow(1,entryRow2);
		
		assertEquals(table.getColumnCount(), 2);
	    assertEquals(table.getRowCount(), 2);	
	    
	    
	    // determinateColumnName , determineColumnNames, getColumnName
	    
	    assertEquals(table.getColumnName(0), null);
	    table.determineColumnNames();
	    assertEquals(table.getColumnName(0), "name1");
	    assertEquals(table.getColumnName(1), "name2");
	    
	    
	    // getColumnTypeName promoteDataTypes()
	    
	    assertEquals(table.getColumnTypeName(0), null );
	   
	    // gives a indexOutOfBoundsException - if entryRows are not equal in size!
	    table.promoteDataTypes();
	    assertEquals(table.getColumnTypeName(0), String.class);
	    assertEquals(table.getColumnTypeName(1), String.class);
	    assertEquals(table.getColumnTypeName(2), this.getClass());
	    assertEquals(table.getColumnTypeName(3), this.getClass());
	
	    
	}
	

	
	
}
