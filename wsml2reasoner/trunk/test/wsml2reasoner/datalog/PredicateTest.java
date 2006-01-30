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
package wsml2reasoner.datalog;

import junit.framework.TestCase;

import org.wsml.reasoner.datalog.Predicate;

public class PredicateTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PredicateTest.class);
    }

    public void testEquals() {
        Predicate p1 = new Predicate("p1",2);
        Predicate p2 = new Predicate("p1",2);
        Predicate p3 = new Predicate("p1",3);
        Predicate p4 = new Predicate("p2",2);
        Predicate p5 = new Predicate(null,2);
        Predicate p6 = new Predicate(null,2);
        Predicate p7 = new Predicate(null,3);
        String s = "p1";
        assertTrue(p1.equals(p2));
        assertTrue(p5.equals(p6));
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(p4));
        assertFalse(p1.equals(p5));
        assertFalse(p5.equals(p7));
        assertFalse(p1.equals(s));
    }

    public void testHashCode() {
        Predicate p1 = new Predicate("p1",2);
        Predicate p2 = new Predicate("p1",2);
        Predicate p5 = new Predicate(null,2);
        Predicate p6 = new Predicate(null,2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertEquals(p5.hashCode(), p6.hashCode());
    }

}
