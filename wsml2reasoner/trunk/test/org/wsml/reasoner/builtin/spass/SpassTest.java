package org.wsml.reasoner.builtin.spass;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

import base.BaseReasonerTest;


/**
 * 
 * Checking if the String transformation is OK!
 */
public class SpassTest extends BaseReasonerTest{
    
    LogicalExpressionFactory leF = Factory.createLogicalExpressionFactory(null);
    WsmoFactory wsmoF = Factory.createWsmoFactory(null);
    Ontology nsContainer;
    SpassFacade tptp = new SpassFacade(new WSMO4JManager(),"urn:foo");
	BuiltInReasoner previous;
    
    public SpassTest(){
        IRI i = wsmoF.createIRI("foo:bar#");
        nsContainer = wsmoF.createOntology(i);
        nsContainer.setDefaultNamespace(i);
    }
    
    protected void setUp() throws Exception {
    	super.setUp();
        previous =  BaseReasonerTest.reasoner;             
     }

    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
        System.gc();
    }
    
    public void testConjunctionDisjunction() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "a and b or c ",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register(set);
        
        String fol = tptp.convertedOntology;
        Pattern pattern = Pattern.compile(escape(
        		" formula ( or ( and (a , b ) , c) "));
        Matcher matcher = pattern.matcher(fol);
        assertTrue(matcher.find());
    }
    
    String escape(String s){
    	s = s.replaceAll(" ", ".*");
    	s = s.replaceAll("\\(", "\\\\\\(");
    	s = s.replaceAll("\\)", "\\\\\\)");
    	System.out.println(s);
    	return s;
    }
    
    void test(String wsml, String fol) throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
                wsml,nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register(set);
        Pattern pattern = Pattern.compile(escape( fol));
        Matcher matcher = pattern.matcher(fol);
        assertTrue("did not find "+fol,matcher.find());
    }
    
    public void testNegation() throws Exception{
    	test("a or neg a", " or ( a , not ( a ) ");
    }

    public void testImplication() throws Exception{
    	test("a impliedBy b ", " b implies a ");
    	test("a equivalent b ", " a equiv b ");
    	test("a implies ( b implies c ) ", " a implies ( b implies c )");
    }
    
    public void testCollector() throws Exception{
    	test("a. b(a).", " predicates (b,0).] ");
    }

}
