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
package variant.flight;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

/**
 * Interface or class description
 * 
 * <pre>
 *    Created on 20.04.2006
 *    Committed by $Author: graham $
 *    $Source: /home/richi/temp/w2r/wsml2reasoner/test/variant/flight/ConstraintViolationCheck.java,v $,
 * </pre>
 * 
 * @author Holger lausen
 * 
 * @version $Revision: 1.1 $ $Date: 2007-11-15 13:06:43 $
 */
public class ConstraintViolationCheck extends BaseReasonerTest {
    Parser parser;

    LogicalExpressionFactory leFactory;

    WsmoFactory wsmoFactory;

    WSMLReasoner reasoner;

    BuiltInReasoner previous;

    public void setUp() throws Exception {
        super.setUp();
        parser = Factory.createParser(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
        wsmoFactory = Factory.createWsmoFactory(null);
        reasoner = BaseReasonerTest.getReasoner();
        previous = BaseReasonerTest.reasoner;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        resetReasoner(previous);
    }

    // public void testSubcumption() throws Exception {
    //
    // String test = "namespace { _\"i:\"} \n" +
    // "ontology simpsons \n" +
    // "concept sub subConceptOf super \n" +
    // "c ofType test \n" +
    // "instance ia memberOf super \n";
    //        
    // Ontology ont = (Ontology) parser.parse(new StringBuffer(test))[0];
    //
    // //String q = "?x memberOf a and ?x[c hasValue?y] and naf ?y memberOf
    // test";
    // String q = "?x memberOf super";
    // LogicalExpression query = leFactory.createLogicalExpression(
    // q, ont);
    // System.out.println("LE:"+query+"\n\n");
    //
    // Set<Map<Variable, Term>> result = null;
    //
    // reasoner.registerOntology(ont);
    // result = reasoner.executeQuery((IRI) ont.getIdentifier(), query);
    // System.out.println(result);
    // assertEquals(1,result.size());
    // }

    public void constraintViolationCheck() throws Exception {

        String test = "namespace { _\"i:\"} \n" + "ontology simpsons \n" + "concept sub  \n" + "//  c impliesType (1 *)_string \n" + "instance ia memberOf sub  \n" + "axiom i definedBy !-?x memberOf sub. ";

        Ontology ont = (Ontology) parser.parse(new StringBuffer(test))[0];

        reasoner = BaseReasonerTest.getReasoner();

        try {
            reasoner.registerOntology(ont);
        }
        catch (InconsistencyException e) {
            System.out.println(e.getViolations().iterator().next());
        }
    }

    public void testFlightReasoners() throws Exception {

        resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
        constraintViolationCheck();

        resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
        constraintViolationCheck();

        if (base.BaseReasonerTest.exists("org.wsml.reasoner.builtin.kaon2.Kaon2Facade")) {
            resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
            constraintViolationCheck();
        }
    }
}
