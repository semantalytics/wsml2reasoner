package org.wsml.reasoner.builtin.spass;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.LogExprParserTypedImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;


/**
 * 
 * Checking if the String transformation is OK!
 */
public class SpassTest extends TestCase{
    
    private Ontology nsContainer;
    private SpassFacade tptp;
	protected Factory wsmoManager;

    protected void setUp() throws Exception {
    	wsmoManager = new FactoryImpl();

        tptp = new SpassFacade(new FactoryImpl(),"urn:foo");

        IRI i = wsmoManager.getWsmoFactory().createIRI("foo:bar#");
        nsContainer = wsmoManager.getWsmoFactory().createOntology(i);
        nsContainer.setDefaultNamespace(i);
     }

    public void testConjunctionDisjunction() throws Exception{
    	LogicalExpressionParser leParser = new LogExprParserTypedImpl();
        LogicalExpression le = leParser.parse(
            "a and b or c ");
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register(set);
        
        String fol = tptp.convertedOntology;
        Pattern pattern = Pattern.compile(escape(
        		" formula ( or ( and (a , b ) , c) "));
        Matcher matcher = pattern.matcher(fol);
        assertTrue(matcher.find());
    }
    
    private String escape(String s){
    	s = s.replaceAll(" ", ".*");
    	s = s.replaceAll("\\(", "\\\\\\(");
    	s = s.replaceAll("\\)", "\\\\\\)");
    	System.out.println(s);
    	return s;
    }
    
    private void check(String wsml, String fol) throws Exception{
        LogicalExpressionParser leParser = new LogExprParserTypedImpl();
        LogicalExpression le = leParser.parse(wsml);
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
        set.add(le);
        tptp.register(set);
        Pattern pattern = Pattern.compile(escape( fol));
        Matcher matcher = pattern.matcher(fol);
        assertTrue("did not find "+fol,matcher.find());
    }
    
    public void testNegation() throws Exception{
    	check("a or neg a", " or ( a , not ( a ) ");
    }

    public void testImplication() throws Exception{
    	check("a impliedBy b ", " b implies a ");
    	check("a equivalent b ", " a equiv b ");
    	check("a implies ( b implies c ) ", " a implies ( b implies c )");
    }
    
    public void testCollector() throws Exception{
    	check("a. b(a).", " predicates (b,0).] ");
    }

}
