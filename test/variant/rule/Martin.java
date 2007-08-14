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
package variant.rule;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

public class Martin extends BaseReasonerTest {

    Parser parser;
    LogicalExpressionFactory leFactory;
    WsmoFactory wsmoFactory;
    WSMLReasoner reasoner;
    BuiltInReasoner previous;
    
    public void setUp(){
        parser = Factory.createParser(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
        wsmoFactory = Factory.createWsmoFactory(null);
        previous = BaseReasonerTest.reasoner;
        BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.MINS;
        reasoner = BaseReasonerTest.getReasoner();
    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	BaseReasonerTest.reasoner = previous;
    }

    /**
     * This function tests the query: ?x[?att ofType ?y] on an ontologgy with a Concept having an
     * attirubte.
     * 
     * @throws Exception
     */
    public void testConceptWithAttribute() throws Exception {
    	String test = getOntHeader() +
    			"concept c attr ofType integer ";
        TopEntity[] topEntity = parser.parse(new StringBuffer(test));
        Ontology ont = (Ontology) topEntity[0];

        LogicalExpression query = leFactory.createLogicalExpression(
                "?x[?att ofType ?y]", ont);

        Set<Map<Variable, Term>> result = null;
        reasoner.registerOntology(ont);
        result = reasoner.executeQuery((IRI) ont.getIdentifier(), query);        
        System.out.println(result);
        assertEquals(1, result.size());
        
    }
    private String getOntHeader(){
    	return "namespace {_\"urn:foo\"} ontology bar"+System.currentTimeMillis()+" \n";
    }
   

}
