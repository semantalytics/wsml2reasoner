package reasoner;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.deri.wsml.reasoner.api.queryanswering.VariableBinding;
import org.deri.wsml.reasoner.impl.VariableBindingImpl;

import test.BaseReasonerTest;

public class TestWSMLCoreReasoner extends BaseReasonerTest {

    private static final String NS = "http://www.example.org/example/#";

    private static final String ONTOLOGY_FILE = "examples/simple-graph.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestWSMLCoreReasoner.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                TestWSMLCoreReasoner.class)) {
            protected void setUp() throws Exception {
                setupScenario(ONTOLOGY_FILE);
             }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }

    public void testElementsConnectedWithF() throws Exception {
        String query = "path(?n,f)";
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("n", NS + "a");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "b");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "c");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "h");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");

    }

    public void testConnectedPairs() throws Exception {
        String query = "path(?n1,?n2)";
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "a");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "b");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "c");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "d");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "e");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "a");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "b");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "c");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "d");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "e");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "a");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "b");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "c");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "d");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "e");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "f");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "f");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "f");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "f");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "g");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "g");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "g");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "g");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "h");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "h");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "h");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "h");
        binding.put("n2", NS + "i");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");

    }

    public void testScElementsOnADirecteCircleWithF() throws Exception {

        String query = "scElement(?n) and path(?n,f) and path(f,?n)";        
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("n", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "h");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

}
