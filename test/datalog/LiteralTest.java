package datalog;

import junit.framework.TestCase;

import org.deri.wsml.reasoner.wsmlcore.datalog.Constant;
import org.deri.wsml.reasoner.wsmlcore.datalog.Literal;
import org.deri.wsml.reasoner.wsmlcore.datalog.Predicate;
import org.deri.wsml.reasoner.wsmlcore.datalog.Term;

public class LiteralTest extends TestCase {

    private Term[] args1;
    
    private Term[] args11;

    private Term[] args2;

    private Predicate pred1;

    private Predicate pred2;

    private Literal.NegationType positive = Literal.NegationType.NONNEGATED;

    private Literal.NegationType negative = Literal.NegationType.NEGATIONASFAILURE;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LiteralTest.class);
    }

    public void testEquals() {
        Literal o1 = new Literal(pred1, positive, args1);
        Literal o2 = new Literal(pred1, positive, args1);
        Literal o3 = new Literal(pred1, positive, args2);
        Literal o4 = new Literal(pred1, negative, args1);
        Literal o5 = new Literal(pred2, positive, args1);
        Literal o6 = null;
        Literal o7 = new Literal(pred1, positive, args11);
        Literal o9 = new Literal(pred1, null, args1);
        Literal o10 = new Literal(pred1, null, args1);
        String s = o1.toString();
        assertTrue(o1.equals(o2));
        assertTrue(o1.equals(o7));
        assertTrue(o9.equals(o10));
        assertFalse(o1.equals(o3));
        assertFalse(o1.equals(o4));
        assertFalse(o1.equals(o5));
        assertFalse(o1.equals(o6));
        assertFalse(o1.equals(o9));
        assertFalse(o1.equals(s));

    }

    public void testHashCode() {
        Literal o1 = new Literal(pred1, positive, args1);
        Literal o2 = new Literal(pred1, positive, args1);
        Literal o7 = new Literal(pred1, positive, args11);
        Literal o9 = new Literal(pred1, null, args1);
        Literal o10 = new Literal(pred1, null, args1);
        assertEquals(o1.hashCode(), o2.hashCode());
        assertEquals(o1.hashCode(), o7.hashCode());
        assertEquals(o9.hashCode(), o10.hashCode());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pred1 = new Predicate("test1", 2);
        pred2 = new Predicate("test2", 2);
        Term arg1 = new Constant("arg1");
        Term arg2 = new Constant("arg2");
        Term arg3 = new Constant("arg3");
        Term arg4 = new Constant("arg4");
        args1 = new Term[] { arg1, arg2 };
        args11 = new Term[] { arg1, arg2 };
        args2 = new Term[] { arg3, arg4 };
    }

}
