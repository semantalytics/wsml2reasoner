package org.wsml.reasoner.builtin.tptp;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.common.IRI;
import org.wsmo.factory.FactoryContainer;


/**
 * 
 * Checking if the String transformation is OK!
 */
public class TPTPTest extends TestCase{
    
    private FactoryContainer wsmoManager;
    private Ontology nsContainer;
    private TPTPFacade tptp;
	private LogicalExpressionParser leParser;
    
    protected void setUp() throws Exception {
    	wsmoManager = new WsmlFactoryContainer();
        
        tptp = new TPTPFacade(new WsmlFactoryContainer(),"urn:foo");

        IRI i = wsmoManager.getWsmoFactory().createIRI("foo:bar#");
        nsContainer = wsmoManager.getWsmoFactory().createOntology(i);
        nsContainer.setDefaultNamespace(i);
        leParser = new WsmlLogicalExpressionParser();
    }

    public void testatom() throws Exception{
		LogicalExpression le = leParser.parse("subConceptof(_\"urn:/Foo\",  Animal)");
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register(set);

        String fol = tptp.convertedOntology;
        assertTrue(fol.contains("subConceptof(foo,animal)"));
        
//        Pattern pattern = Pattern.compile("c.*\\[.*r1.*hasValue.*v1.*\\].*and.*c.*\\[.*r3.*hasValue.*v3.*].*and.*c.*\\[.*r2.*hasValue.*v2.*\\].*", Pattern.DOTALL);
//        Matcher matcher = pattern.matcher(fol);
//        assertTrue(matcher.find());
    }

    public void testVars() throws Exception{
        LogicalExpression le = leParser.parse(
            "forall ?x,?y,?z \n " +
            " (subConceptof(?x,?y) and subConceptof(?y,?z) \n" +
            " implies subConceptof(?x,?z)) ");
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register(set);

        String fol = tptp.convertedOntology;
//        System.out.println(fol);

        Pattern pattern = Pattern.compile("! \\[Z,Y,X\\] :"+
                ".*subConceptof\\(X,Y\\).*&.*subConceptof\\(Y,Z\\).*=>.*subConceptof\\(X,Z\\)");
        Matcher matcher = pattern.matcher(fol);
        assertTrue(matcher.find());
    }
    
    public void testConjunctionDisjunction() throws Exception{
        LogicalExpression le = leParser.parse(
            "a and b or c ");
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register( set);
        
        String fol = tptp.convertedOntology;
//        System.out.println(fol);
        Pattern pattern = Pattern.compile(
                "\\(\\(\\(a.*&.*b\\).*|.*c\\)\\)");
        Matcher matcher = pattern.matcher(fol);
        assertTrue(matcher.find());
    }
    
    public void testNegation() throws Exception{
        LogicalExpression le = leParser.parse(
            "a or neg a");
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register( set);


        String fol = tptp.convertedOntology;
//      System.out.println(fol);
        Pattern pattern = Pattern.compile(
                  "a.*|.*\\~.*a");
        Matcher matcher = pattern.matcher(fol);
        assertTrue(matcher.find());
    }

    public void testFsymbols() throws Exception{
        LogicalExpression le = leParser.parse(
            "a(f(b(c,d,e)))");
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register( set);
        
        String fol = tptp.convertedOntology;
      
        assertTrue(fol.contains("(a(f(b(c,d,e)"));
    }
}
