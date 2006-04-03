package reasoner;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class OntologyRegistrationTest extends TestCase {

    private WsmoFactory wsmoFactory;

    private LogicalExpressionFactory leFactory;

    private WSMLReasoner wsmlReasoner;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(OntologyRegistrationTest.class);
    }

    
    public void testOntologyRegistration() throws Exception {

        Ontology o1 = wsmoFactory.createOntology(wsmoFactory
                .createIRI("urn:test1"));
        Atom a1 = leFactory.createAtom(wsmoFactory.createIRI("urn:test:a1"),
                new ArrayList());
        Axiom ax1 = wsmoFactory.createAxiom(wsmoFactory
                .createIRI("urn:test:ax1"));
        ax1.addDefinition(a1);
        o1.addAxiom(ax1);

        try {
            executeQuery("_\"urn:test:xxx\"()", o1);
            fail();
        } catch (InternalReasonerException expected) {

        }

        wsmlReasoner.registerOntology(o1);
        assertEquals(1, executeQuery("_\"urn:test:a1\"()", o1).size());

        Ontology o2 = wsmoFactory.createOntology(wsmoFactory
                .createIRI("urn:test2"));
        Atom a2 = leFactory.createAtom(wsmoFactory.createIRI("urn:test:a2"),
                new ArrayList());
        Axiom ax2 = wsmoFactory.createAxiom(wsmoFactory
                .createIRI("urn:test:ax2"));
        ax2.addDefinition(a2);
        o2.addAxiom(ax2);

        try {
            executeQuery("_\"urn:test:a2\"()", o2);
            fail();
        } catch (InternalReasonerException expected) {

        }
        wsmlReasoner.registerOntology(o2);
        assertEquals(1, executeQuery("_\"urn:test:a1\"()", o1).size());
        assertEquals(0, executeQuery("_\"urn:test:a11\"()", o1).size());
        assertEquals(1, executeQuery("_\"urn:test:a2\"()", o2).size());

        Atom a11 = leFactory.createAtom(wsmoFactory.createIRI("urn:test:a11"),
                new ArrayList());
        Axiom ax11 = wsmoFactory.createAxiom(wsmoFactory
                .createIRI("urn:test:ax11"));
        ax11.addDefinition(a11);
        o1.addAxiom(ax11);

        wsmlReasoner.registerOntology(o1);
        assertEquals(1, executeQuery("_\"urn:test:a1\"()", o1).size());
        assertEquals(1, executeQuery("_\"urn:test:a11\"()", o1).size());
        assertEquals(1, executeQuery("_\"urn:test:a2\"()", o2).size());

         wsmlReasoner.deRegisterOntology((IRI) o2.getIdentifier());
         try {
             executeQuery("_\"urn:test:a2\"()", o2);
             fail();
         } catch (InternalReasonerException expected) {

         }
         assertEquals(1, executeQuery("_\"urn:test:a1\"()", o1).size());
         assertEquals(1, executeQuery("_\"urn:test:a11\"()", o1).size());
         
         wsmlReasoner.deRegisterOntology((IRI) o1.getIdentifier());
         try {
             executeQuery("_\"urn:test:a1\"()", o2);
             fail();
         } catch (InternalReasonerException expected) {

         }
 

    }

    private Set<Map<Variable, Term>> executeQuery(String query, Ontology o)
            throws Exception {
        // LogicalExpression qExpression = leFactory.createLogicalExpression(
        // "_\"urn:test:xxx\"()", o);
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                query, o);
        return wsmlReasoner.executeQuery((IRI) o.getIdentifier(), qExpression);
    }

    @Override
    protected void setUp() throws Exception {
        WSMO4JManager wsmoManager = new WSMO4JManager();
        leFactory = wsmoManager.getLogicalExpressionFactory();

        wsmoFactory = wsmoManager.getWSMOFactory();

        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
                .createWSMLFlightReasoner();
    }

}
