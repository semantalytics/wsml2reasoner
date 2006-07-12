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
package reasoner.datatype;

import java.util.*;

import junit.framework.*;

import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.*;
import org.omwg.ontology.*;
import org.wsml.reasoner.api.*;
import org.wsmo.common.*;
import org.wsmo.factory.*;
import org.wsmo.wsml.*;

import test.*;

/**
 * Interface or class description
 * 
 * <pre>
 *   Created on 20.04.2006
 *   Committed by $Author$
 *   $Source$,
 * </pre>
 * 
 * @author Holger lausen
 * 
 * @version $Revision$ $Date$
 */
public class Date extends TestCase {
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
        System.out.println(m.get(wsmoFactory.createVariable("y")));
        assertEquals(dFactory.createDataValue(
                (ComplexDataType)dFactory.createWsmlDataType(ComplexDataType.WSML_DATE), 
                new SimpleDataValue[]{
                        dFactory.createWsmlInteger("2005"),
                        dFactory.createWsmlInteger("12"),
                        dFactory.createWsmlInteger("11"),
                }),
                m.get(wsmoFactory.createVariable("y")));
        
        query = leFactory.createLogicalExpression("?a[birth hasValue ?y] and ?b[birth hasValue ?x] and ?y<?x", o);
        result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        assertEquals(1,result.size());
        m =result.iterator().next();
        System.out.println(m.get(wsmoFactory.createVariable("a")));
        assertEquals(wsmoFactory.createIRI(ns+"a"),
                m.get(wsmoFactory.createVariable("a")));
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(Date.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(Date.class)) {};
        return test;
    }

}
