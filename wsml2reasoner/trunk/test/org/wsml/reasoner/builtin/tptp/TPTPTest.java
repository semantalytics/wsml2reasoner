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
    
    public void testatom() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "subConceptof(_\"urn:/Foo\",  Animal)",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);
        //FIXME some assertion to come
    }

    public void testVars() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "forall {?x,?y,?z} \n " +
            " (subConceptof(?x,?y) and subConceptof(?y,?z) \n" +
            " implies subConceptof(?x,?z)) ",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);
        //FIXME some assertion to come
    }
    
    public void testConjunctionDisjunction() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "a and b or c ",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);
        //FIXME some assertion to come
    }
    
    public void testNegation() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "a or neg a",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);
        //FIXME some assertion to come
    }

    public void testFsymbols() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "a(f(b(c,d,e)))",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);
        //FIXME some assertion to come
    }
    

}
