package reasoner.core;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.*;
import org.omwg.ontology.*;
import org.wsml.reasoner.api.*;
import org.wsml.reasoner.impl.*;
import org.wsmo.common.*;
import org.wsmo.factory.*;
import org.wsmo.wsml.*;

/**
 * Interface or class description
 * 
 * <pre>
 *   Created on 20.04.2006
 *   Committed by $Author$
 *   $Source$,
 * </pre>
 * 
 * @author Holger lausen, Martin Tanler
 * 
 * @version $Revision$ $Date$
 */
public class RegisterMultipleTimes extends TestCase {
    String FILE = "RegisterMultipleTimes.wsml";
    Parser parser;
    LogicalExpressionFactory leFactory;
    WSMLReasoner reasoner;
    
    public void setUp(){
        parser = Factory.createParser(null);
        Map<String, Object> m ;
        m = new HashMap<String, Object>();
        m.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.MINS);
        leFactory = Factory.createLogicalExpressionFactory(null);
        reasoner = DefaultWSMLReasonerFactory.getFactory()
        .createWSMLFlightReasoner(m);
    }

    /**
     * This test failed since WSMO4J uses statics in its default factory
     * Now all new objects are created with a unique URI, sort of waste
     * of resources, but works.
     * 
     * @throws Exception
     */
    public void testClearDeregistration() throws Exception {

        FileReader f = new FileReader(RegisterMultipleTimes.class.getResource(FILE).toURI()
                .getPath());
        Ontology[] ont = new Ontology[2];
        TopEntity[] topEntity = parser.parse(f);
        ont[0] = (Ontology) topEntity[0];
        ont[1] = (Ontology) topEntity[1];

        LogicalExpression query = leFactory.createLogicalExpression(
                "?x memberOf ?y", ont[0]);

        Set<Map<Variable, Term>> result = null;
        for (int i = 0; i < 50; i++) {
            // ///////////ONTOLOGY 0
            reasoner.registerOntology(ont[0]);
            result = reasoner.executeQuery((IRI) ont[0].getIdentifier(), query);
            reasoner.deRegisterOntology((IRI) ont[0].getIdentifier());
            assertEquals("failed in run:"+i,1,result.size());

            // ///////////ONTOLOGY 1
            reasoner.registerOntology(ont[1]);
            result = reasoner.executeQuery((IRI) ont[1].getIdentifier(), query);
            reasoner.deRegisterOntology((IRI) ont[1].getIdentifier());
            assertEquals("failed in run:"+i,1, result.size());

        }

    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(SimpleGraph.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(SimpleGraph.class)) {};
        return test;
    }

}
