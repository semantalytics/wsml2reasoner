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

import junit.framework.TestCase;


public class EntryTest extends TestCase {

	public void testInstantiate(){
		
		String object = "object";
		String name = "name";
		Class <?> clazzes = this.getClass();
		
		Entry entry = new Entry(object, name, clazzes);
		
		assertEquals(entry.getName(), "name");
		assertEquals(entry.getValue(), new String("object"));
		assertEquals(entry.getClassMapping(), this.getClass());
		
		entry.setName("other name");
		assertEquals(entry.getName(), "other name");
		
		entry.setValue("other object");
		assertEquals(entry.getValue(), new String("other object"));
		
		entry.setClassMapping(name.getClass());
		assertEquals(entry.getClassMapping(), name.getClass());
		
	}


}
