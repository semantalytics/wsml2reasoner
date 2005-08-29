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
package datalog;

import junit.framework.TestCase;

import org.deri.wsml.reasoner.wsmlcore.datalog.DataTypeValue;

public class DataTypeValueTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DataTypeValueTest.class);
    }

    public void testEquals() {
        DataTypeValue o1 = new DataTypeValue("d1",
                DataTypeValue.DataType.DECIMAL);
        DataTypeValue o2 = new DataTypeValue("d1",
                DataTypeValue.DataType.DECIMAL);
        DataTypeValue o3 = new DataTypeValue("d1",
                DataTypeValue.DataType.INTEGER);
        DataTypeValue o4 = new DataTypeValue("d2",
                DataTypeValue.DataType.DECIMAL);
        DataTypeValue o5 = null;
        DataTypeValue o6 = new DataTypeValue(null,
                DataTypeValue.DataType.DECIMAL);
        DataTypeValue o7 = new DataTypeValue(null,
                DataTypeValue.DataType.DECIMAL);
        DataTypeValue o8 = new DataTypeValue("d1", null);
        DataTypeValue o9 = new DataTypeValue("d1", null);
        DataTypeValue o10 = new DataTypeValue(null, null);
        DataTypeValue o11 = new DataTypeValue(null, null);
        String s = "d1";
        assertTrue(o1.equals(o2));
        assertTrue(o6.equals(o7));
        assertTrue(o8.equals(o9));
        assertTrue(o10.equals(o11));
        assertFalse(o1.equals(o3));
        assertFalse(o1.equals(o4));
        assertFalse(o1.equals(o5));
        assertFalse(o1.equals(o6));
        assertFalse(o1.equals(o8));
        assertFalse(o1.equals(o10));
        assertFalse(o1.equals(s));

    }

    public void testHashCode() {
        DataTypeValue o1 = new DataTypeValue("d1",
                DataTypeValue.DataType.DECIMAL);
        DataTypeValue o2 = new DataTypeValue("d1",
                DataTypeValue.DataType.DECIMAL);
        DataTypeValue o6 = new DataTypeValue(null,
                DataTypeValue.DataType.DECIMAL);
        DataTypeValue o7 = new DataTypeValue(null,
                DataTypeValue.DataType.DECIMAL);
        DataTypeValue o8 = new DataTypeValue("d1", null);
        DataTypeValue o9 = new DataTypeValue("d1", null);
        DataTypeValue o10 = new DataTypeValue(null, null);
        DataTypeValue o11 = new DataTypeValue(null, null);
        assertEquals(o1.hashCode(), o2.hashCode());
        assertEquals(o6.hashCode(), o7.hashCode());
        assertEquals(o8.hashCode(), o9.hashCode());
        assertEquals(o10.hashCode(), o11.hashCode());
    }

}
