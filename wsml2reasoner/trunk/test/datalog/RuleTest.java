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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deri.wsml.reasoner.wsmlcore.datalog.*;

public class RuleTest extends TestCase {

    private Literal head1;
    
    private Literal head11;

    private Literal head2;

    private List<Literal> body1;
    
    private List<Literal> body11;

    private List<Literal> body2;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(RuleTest.class);
    }

    public void testEquals() {
        Rule o1 = new Rule(head1, body1);
        Rule o2 = new Rule(head1, body1);
        Rule o3 = new Rule(head1, body2);
        Rule o4 = new Rule(head2, body1);
        Rule o5 = null;
        Rule o6 = new Rule(null, body1);
        Rule o7 = new Rule(null, body1);
        Rule o8 = new Rule(head1, null);
        Rule o9 = new Rule(head1, null);
        Rule o10 = new Rule(null, null);
        Rule o11 = new Rule(null, null);
        Rule o12 = new Rule(head11, body1);
        Rule o13 = new Rule(head1, body11);
        String s = o1.toString();
        assertTrue(o1.equals(o2));
        assertTrue(o1.equals(o12));
        assertTrue(o1.equals(o13));
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
        Rule o1 = new Rule(head1, body1);
        Rule o2 = new Rule(head1, body1);
        Rule o6 = new Rule(null, body1);
        Rule o7 = new Rule(null, body1);
        Rule o8 = new Rule(head1, null);
        Rule o9 = new Rule(head1, null);
        Rule o10 = new Rule(null, null);
        Rule o11 = new Rule(null, null);
        Rule o12 = new Rule(head11, body1);
        Rule o13 = new Rule(head1, body11);
        assertEquals(o1.hashCode(), o2.hashCode());
        assertEquals(o1.hashCode(), o12.hashCode());
        assertEquals(o1.hashCode(), o13.hashCode());
        assertEquals(o6.hashCode(), o7.hashCode());
        assertEquals(o8.hashCode(), o9.hashCode());
        assertEquals(o10.hashCode(), o11.hashCode());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Predicate pred = new Predicate("test", 1);
        Term arg1 = new Constant("arg1");
        Term arg2 = new Constant("arg2");
        head1 = new Literal(pred, new Term[] { arg1 });
        head11 = new Literal(pred, new Term[] { arg1 });
        head2 = new Literal(pred, new Term[] { arg2 });
        body1 = new ArrayList<Literal>();
        body1.add(head1);
        body11 = new ArrayList<Literal>();
        body11.add(head11);
        body2 = new ArrayList<Literal>();
        body2.add(head1);
        body2.add(head2);
    }

}
