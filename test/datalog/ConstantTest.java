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

import org.wsml.reasoner.datalog.Constant;

public class ConstantTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ConstantTest.class);
    }

    public void testEquals() {
        Constant c1 = new Constant("c1");
        Constant c2 = new Constant("c1");
        Constant c3 = new Constant("c3");
        Constant c4 = null;
        Constant c5 = new Constant(null);
        Constant c6 = new Constant(null);
        String s = "c1";
        assertTrue(c1.equals(c2));
        assertFalse(c1.equals(c3));
        assertFalse(c1.equals(c4));
        assertFalse(c1.equals(c5));
        assertTrue(c5.equals(c6));
        assertFalse(c5.equals(c1));
        assertFalse(c1.equals(s));
    }

    public void testHashCode() {
        Constant c1 = new Constant("c1");
        Constant c2 = new Constant("c1");
        Constant c5 = new Constant(null);
        Constant c6 = new Constant(null);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertEquals(c5.hashCode(), c6.hashCode());
    }

}
