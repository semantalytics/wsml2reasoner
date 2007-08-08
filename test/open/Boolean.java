/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package open;

import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

/**
 * Currently does not work with MINS
 * 
 * <pre>
 *   Created on 20.04.2006
 *   Committed by $Author: graham $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/test/open/Boolean.java,v $,
 * </pre>
 * 
 * @author Holger lausen
 * 
 * @version $Revision: 1.1 $ $Date: 2007-08-08 10:57:59 $
 */
public class Boolean extends BaseReasonerTest {
    Parser parser;
    LogicalExpressionFactory leFactory;
    DataFactory dFactory;
    WsmoFactory wsmoFactory;
    WSMLReasoner reasoner;
    
    public void setUp(){
        parser = Factory.createParser(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
        dFactory = Factory.createDataFactory(null);
        reasoner =  BaseReasonerTest.getReasoner();
        wsmoFactory = Factory.createWsmoFactory(null);
    }

    /**
     * 
     * @throws Exception
     */
    public void testSimpleBoolean() throws Exception {
        String ns = "http://ex1.org#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "axiom a definedBy \n" +
                "a[f hasValue _boolean(\"false\")]. \n " +
                "a[t hasValue _boolean(\"true\")]. \n " +
                "a(?a) :- a[?a hasValue ?x] and ?x != _boolean(\"false\").  ";

        Ontology o = (Ontology) parser.parse(new StringBuffer(test))[0];
        Set<Map<Variable, Term>> result = null;
        LogicalExpression query ;
        reasoner.registerOntology(o);

        query = leFactory.createLogicalExpression("a[f hasValue ?y]", o);
        result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        assertEquals(1,result.size());
        Map<Variable,Term> m =result.iterator().next();
        System.out.println(m.get(leFactory.createVariable("y")));
        assertEquals(dFactory.createWsmlBoolean(false),
                m.get(leFactory.createVariable("y")));
        
        query = leFactory.createLogicalExpression("a[t hasValue ?y]", o);
        result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        assertEquals(1,result.size());
        m =result.iterator().next();
        System.out.println(m.get(leFactory.createVariable("y")));
        assertEquals(dFactory.createWsmlBoolean(true),
                m.get(leFactory.createVariable("y")));
        
        query = leFactory.createLogicalExpression("a(?y)", o);
        result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        assertEquals(1,result.size());
        m =result.iterator().next();
        System.out.println(m.get(leFactory.createVariable("y")));
        assertEquals(wsmoFactory.createIRI(ns+"t"),
                m.get(leFactory.createVariable("y"))); 
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(Boolean.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(Boolean.class)) {};
        return test;
    }

}
