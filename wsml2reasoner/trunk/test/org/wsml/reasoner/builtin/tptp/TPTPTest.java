package org.wsml.reasoner.builtin.tptp;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.*;

import junit.framework.TestCase;


/**
 * 
 * Checking if the String transformation is OK!
 */
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

        String fol = tptp.convertedOntologies.get("abc");
        assertTrue(fol.contains("subConceptof(foo,animal)"));
        
//        Pattern pattern = Pattern.compile("c.*\\[.*r1.*hasValue.*v1.*\\].*and.*c.*\\[.*r3.*hasValue.*v3.*].*and.*c.*\\[.*r2.*hasValue.*v2.*\\].*", Pattern.DOTALL);
//        Matcher matcher = pattern.matcher(fol);
//        assertTrue(matcher.find());
    }

    public void testVars() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "forall {?x,?y,?z} \n " +
            " (subConceptof(?x,?y) and subConceptof(?y,?z) \n" +
            " implies subConceptof(?x,?z)) ",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);

        String fol = tptp.convertedOntologies.get("abc");
//        System.out.println(fol);

        Pattern pattern = Pattern.compile("! \\[Z,Y,X\\] :"+
                ".*subConceptof\\(X,Y\\).*&.*subConceptof\\(Y,Z\\).*=>.*subConceptof\\(X,Z\\)");
        Matcher matcher = pattern.matcher(fol);
        assertTrue(matcher.find());

    }
    
    public void testConjunctionDisjunction() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "a and b or c ",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);
        
        String fol = tptp.convertedOntologies.get("abc");
//        System.out.println(fol);
        Pattern pattern = Pattern.compile(
                "\\(\\(\\(a.*&.*b\\).*|.*c\\)\\)");
        Matcher matcher = pattern.matcher(fol);
        assertTrue(matcher.find());
    }
    
    public void testNegation() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "a or neg a",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);


        String fol = tptp.convertedOntologies.get("abc");
//      System.out.println(fol);
        Pattern pattern = Pattern.compile(
                  "a.*|.*\\~.*a");
        Matcher matcher = pattern.matcher(fol);
        assertTrue(matcher.find());

    }

    public void testFsymbols() throws Exception{
        LogicalExpression le = leF.createLogicalExpression(
            "a(f(b(c,d,e)))",nsContainer);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register("abc", set);
        
        String fol = tptp.convertedOntologies.get("abc");
      
        assertTrue(fol.contains("(a(f(b(c,d,e)"));

    }
    

}
