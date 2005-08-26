package datalog;

import org.deri.wsml.reasoner.wsmlcore.datalog.Constant;
import org.deri.wsml.reasoner.wsmlcore.datalog.Predicate;

import junit.framework.TestCase;

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
