/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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
package reasoner.flight;

import java.util.*;

import junit.framework.*;

import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.*;
import org.omwg.ontology.*;
import org.wsml.reasoner.api.*;
import org.wsml.reasoner.api.inconsistency.*;
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
public class SubConceptOfTest extends TestCase {
    Parser parser;
    LogicalExpressionFactory leFactory;
    WsmoFactory wsmoFactory;
    WSMLReasoner reasoner;
    
    public void setUp(){
        parser = Factory.createParser(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
        wsmoFactory = Factory.createWsmoFactory(null);

        reasoner = BaseReasonerTest.getReasoner();
    }

//    public void testSubcumption() throws Exception {
//
//        String test = "namespace { _\"i:\"} \n" +
//                "ontology simpsons \n" +
//                "concept sub subConceptOf super \n" +
//                "c ofType test \n" +
//                "instance ia memberOf super \n";
//        
//        Ontology ont = (Ontology) parser.parse(new StringBuffer(test))[0];
//
//        //String q = "?x memberOf a and ?x[c hasValue?y] and naf ?y memberOf test";
//        String q = "?x memberOf super";
//        LogicalExpression query = leFactory.createLogicalExpression(
//                q, ont);
//        System.out.println("LE:"+query+"\n\n");
//
//        Set<Map<Variable, Term>> result = null;
//
//        reasoner.registerOntology(ont);
//        result = reasoner.executeQuery((IRI) ont.getIdentifier(), query);
//        System.out.println(result);
//        assertEquals(1,result.size());
//    }
    
    public void testConstraint() throws Exception {

        String test = "namespace { _\"i:\"} \n" +
                "ontology simpsons \n" +
                "concept sub  \n" +
                "//  c impliesType (1 *)_string \n" +
                "instance ia memberOf sub  \n" +
                "axiom i definedBy !-?x memberOf sub. ";
        
        Ontology ont = (Ontology) parser.parse(new StringBuffer(test))[0];

        //String q = "?x memberOf a and ?x[c hasValue?y] and naf ?y memberOf test";
        String q = "?x memberOf super";
        LogicalExpression query = leFactory.createLogicalExpression(
                q, ont);
        //System.out.println("LE:"+query+"\n\n");

        Set<Map<Variable, Term>> result = null;
        try{
            reasoner.registerOntology(ont);
        }catch(InconsistencyException e){
            System.out.println(e.getViolations().iterator().next());
        }
        //result = reasoner.executeQuery((IRI) ont.getIdentifier(), query);
        //System.out.println(result);
        //assertEquals(1,result.size());
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(SubConceptOfTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(SubConceptOfTest.class)) {};
        return test;
    }

}