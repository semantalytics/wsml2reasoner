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
package open.minsonly;

import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.ComplexDataType;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.SimpleDataValue;
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
 * Interface or class description
 * 
 * <pre>
 *   Created on 20.04.2006
 *   Committed by $Author: graham $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/test/open/minsonly/Date1.java,v $,
 * </pre>
 * 
 * @author Holger lausen
 * 
 * @version $Revision: 1.1 $ $Date: 2007-08-08 10:58:02 $
 */
public class Date1 extends BaseReasonerTest {
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
    public void testSimpleDate1() throws Exception {
        String ns = "http://ex2.org#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "axiom a definedBy \n" +
                "a[birth hasValue _date(2005,12,11)]. \n " +
                "b[birth hasValue _date(2005,12,20)]. \n " +
                "";

        Ontology o = (Ontology) parser.parse(new StringBuffer(test))[0];
        Set<Map<Variable, Term>> result = null;
        LogicalExpression query ;
        reasoner.registerOntology(o);

        query = leFactory.createLogicalExpression("a[birth hasValue ?y]", o);
        result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        assertEquals(1,result.size());
        Map<Variable,Term> m =result.iterator().next();
        System.out.println(m.get(leFactory.createVariable("y")));
        assertEquals(dFactory.createDataValue(
                (ComplexDataType)dFactory.createWsmlDataType(ComplexDataType.WSML_DATE), 
                new SimpleDataValue[]{
                        dFactory.createWsmlInteger("2005"),
                        dFactory.createWsmlInteger("12"),
                        dFactory.createWsmlInteger("11"),
                }),
                m.get(leFactory.createVariable("y")));
        
        query = leFactory.createLogicalExpression("?a[birth hasValue ?y] and ?b[birth hasValue ?x] and ?y<?x", o);
        result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        assertEquals(1,result.size());
        m =result.iterator().next();
        System.out.println(m.get(leFactory.createVariable("a")));
        assertEquals(wsmoFactory.createIRI(ns+"a"),
                m.get(leFactory.createVariable("a")));
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(Date1.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(Date1.class)) {};
        return test;
    }

}
