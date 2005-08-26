package datalog;

import org.deri.wsml.reasoner.wsmlcore.datalog.Constant;

import junit.framework.TestCase;

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
