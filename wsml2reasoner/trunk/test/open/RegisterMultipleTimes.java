package open;

import java.io.FileReader;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

/**
 * Interface or class description
 * 
 * <pre>
 *   Created on 20.04.2006
 *   Committed by $Author: graham $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/test/open/RegisterMultipleTimes.java,v $,
 * </pre>
 * 
 * @author Holger lausen, Martin Tanler
 * 
 * @version $Revision: 1.1 $ $Date: 2007-08-08 10:57:59 $
 */
public class RegisterMultipleTimes  extends BaseReasonerTest  {
    String FILE = "RegisterMultipleTimes.wsml";
    Parser parser;
    LogicalExpressionFactory leFactory;
    WsmoFactory wsmoFactory;
    WSMLReasoner reasoner;

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
    
    /**
     * 
     * @throws Exception
     */
    public void testRemoveInstances() throws Exception {
        String ns = "http://ex1.org#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "concept c \n" +
                "instance i1 memberOf c \n " +
                "instance i2 memberOf c \n ";
        

        Ontology o = (Ontology) parser.parse(new StringBuffer(test))[0];

        LogicalExpression query = leFactory.createLogicalExpression(
                "?x memberOf ?y", o);
        Instance instance = o.findInstance(wsmoFactory.createIRI(ns+"i1"));


        for (int i=0; i<10; i++){
            Set<Map<Variable, Term>> result = null;
            // with 2 instances
            reasoner.registerOntology(o);
            result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
            reasoner.deRegisterOntology((IRI) o.getIdentifier());
            assertEquals("failure in run "+i ,2,o.listInstances().size());
            assertEquals("failure in run "+i ,2,result.size());
            
            
            o.removeInstance(instance);
            // with 1 instance
            reasoner.registerOntology(o);
            result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
            reasoner.deRegisterOntology((IRI) o.getIdentifier());
            assertEquals("failure in run "+i ,1,o.listInstances().size());
            assertEquals("failure in run "+i ,1,result.size());
            o.addInstance(instance);
        }


    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(RegisterMultipleTimes.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(RegisterMultipleTimes.class)) {};
        return test;
    }

}
