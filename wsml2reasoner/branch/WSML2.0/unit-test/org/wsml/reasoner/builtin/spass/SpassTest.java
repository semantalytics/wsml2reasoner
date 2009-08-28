package org.wsml.reasoner.builtin.spass;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;


/**
 * 
 * Checking if the String transformation is OK!
 */
public class SpassTest extends TestCase{
    
    private LogicalExpressionFactory leF;
    private Ontology nsContainer;
    private SpassFacade tptp;

    protected void setUp() throws Exception {
    	WsmoFactory wsmoFactory = FactoryImpl.createNewInstance().getWsmoFactory();
		DataFactory wsmlDataFactory = FactoryImpl.createNewInstance().getWsmlDataFactory(wsmoFactory);
		DataFactory xmlDataFactory = FactoryImpl.createNewInstance().getXmlDataFactory(wsmoFactory);
		leF = FactoryImpl.createNewInstance().getLogicalExpressionFactory(wsmoFactory, wsmlDataFactory, xmlDataFactory);
        

        tptp = new SpassFacade(new WSMO4JManager(),"urn:foo");

        IRI i = wsmoFactory.createIRI("foo:bar#");
        nsContainer = wsmoFactory.createOntology(i);
        nsContainer.setDefaultNamespace(i);
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
    
    private String escape(String s){
    	s = s.replaceAll(" ", ".*");
    	s = s.replaceAll("\\(", "\\\\\\(");
    	s = s.replaceAll("\\)", "\\\\\\)");
    	System.out.println(s);
    	return s;
    }
    
    private void check(String wsml, String fol) throws Exception{
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
