package org.wsml.reasoner.builtin.tptp;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.*;

import junit.framework.TestCase;


public class TPTPTest extends TestCase{
    
    LogicalExpressionFactory leF = Factory.createLogicalExpressionFactory(null);
    WsmoFactory wsmoF = Factory.createWsmoFactory(null);
    Ontology nsContainer;
    TPTPFacade tptp = new TPTPFacade(new WSMO4JManager());
    
    public TPTPTest(){
        IRI i = wsmoF.createIRI("foo:bar#");
        nsContainer = wsmoF.createOntology(i);
        nsContainer.setDefaultNamespace(i);
    }
    
    public void testRegister() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "a(b) and b(c) or b",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);
        //FIXME some assertion to come
    }

}
