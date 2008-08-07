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
package engine.mins;

import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
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
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/test/engine/mins/IRITest.java,v $,
 * </pre>
 * 
 * @author Holger lausen
 * 
 * @version $Revision: 1.2 $ $Date: 2007-08-23 08:47:47 $
 */
public class IRITest extends BaseReasonerTest {
    Parser parser;
    LogicalExpressionFactory leFactory;
    DataFactory dFactory;
    WsmoFactory wsmoFactory;
    LPReasoner reasoner;
    BuiltInReasoner previous;
    
    
    public void setUp(){
        parser = Factory.createParser(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
        dFactory = Factory.createDataFactory(null);
        // currently set to MINS since the other reasoning engines
		// cannot yet handle such built-ins
        previous = BaseReasonerTest.reasoner;
        BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.MINS;
        reasoner =  (LPReasoner) BaseReasonerTest.getReasoner();
        wsmoFactory = Factory.createWsmoFactory(null);
    }

    /**
     * 
     * @throws Exception
     */
    public void testIRI() throws Exception {
        String ns = "http://ex1.org#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "axiom a definedBy \n" +
                "a[sqname hasValue ab] memberOf A. \n " +
                "a[iri hasValue _\"urn:foo\"] memberOf A. \n " +
                "a[fsymbol hasValue f(x)] memberOf A. " +
                "" +
                "A[sqname ofType _iri]. " +
                "A[iri ofType _iri]. " +
                "A[fsymbol ofType _iri]. ";

        Ontology o = (Ontology) parser.parse(new StringBuffer(test))[0];
        Set<Map<Variable, Term>> result = null;
        LogicalExpression query ;
        reasoner.registerOntology(o);

        query = leFactory.createLogicalExpression("?x memberOf _iri", o);
        result = reasoner.executeQuery(query);
        System.out.println(result);
        //for(Entry<Variable, Term>)
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(IRITest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(IRITest.class)) {};
        return test;
    }
    @Override
    protected void tearDown() throws Exception {
    	// TODO Auto-generated method stub
    	super.tearDown();
    	resetReasoner(previous);
    	//System.gc();
    }

}
