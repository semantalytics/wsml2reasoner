/*
 wsmo4j - a WSMO API and Reference Implementation

 Copyright (c) 2005, University of Innsbruck, Austria

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License along
 with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package engine.tptp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.api.FOLReasoner;
import org.wsml.reasoner.api.FOLReasoner.EntailmentType;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
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
 * Created on 20.03.2007
 * Committed by $Author: graham $
 * 
 * </pre>
 *
 * @author Rosi, Holger
 *
 * @version $Revision: 1.2 $ $Date: 2007-08-13 15:17:42 $
 */
public class TPTPEntailmentTest extends BaseReasonerTest {

    private WsmoFactory wsmoFactory = null;
    private LogicalExpressionFactory leFactory = null;
    private FOLReasoner wsmlReasoner = null;
    BuiltInReasoner previous;  
    private Parser wsmlParser = null;
	private DataFactory xmlDataFactory;
	private DataFactory wsmlDataFactory; 
    
    
    protected void setUp() throws Exception {
        super.setUp();
        wsmoFactory = FactoryImpl.getInstance().createWsmoFactory();
        wsmlDataFactory = FactoryImpl.getInstance().createWsmlDataFactory(wsmoFactory);
        xmlDataFactory = FactoryImpl.getInstance().createXmlDataFactory(wsmoFactory);
        leFactory = FactoryImpl.getInstance().createLogicalExpressionFactory(wsmoFactory, wsmlDataFactory, xmlDataFactory);
        previous = BaseReasonerTest.reasoner;
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createFOLReasoner(new HashMap <String, Object> ());
    	wsmlParser = FactoryImpl.getInstance().createParser(wsmoFactory);
    }
    
    public void test() throws Exception{
        InputStream in = getClass().getClassLoader().getResourceAsStream(
                "files/family.wsml");
        Ontology ont = (Ontology)wsmlParser.parse(new InputStreamReader(in), null)[0];
        
        wsmlReasoner.registerOntology(ont);
        LogicalExpression conjecture = leFactory.createLogicalExpression(
                "Lisa[hasAncestor hasValue GrandPa]",ont);
        EntailmentType result = wsmlReasoner.checkEntailment(
                conjecture);
        assertEquals(EntailmentType.entailed, result);
        
        
        conjecture = leFactory.createLogicalExpression(
                "exists ?x (?x[hasChild hasValue someChild])",ont);
        result = wsmlReasoner.checkEntailment(
                conjecture);
        assertEquals(EntailmentType.entailed, result);
        
        conjecture = leFactory.createLogicalExpression(
                "exists ?x (March[hasChild hasValue ?x])",ont);
        result = wsmlReasoner.checkEntailment(
                conjecture);
        assertEquals(EntailmentType.entailed, result);
    }
    
    
    public void testhvMolecule() throws Exception{
        IRI iri = wsmoFactory.createIRI("urn://foobar");
        Ontology ont = wsmoFactory.createOntology(iri);
        ont.setDefaultNamespace(iri);
        LogicalExpression le = leFactory.createLogicalExpression(
            "a[b hasValue c]",ont);
        Axiom a = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
        a.addDefinition(le);
        ont.addAxiom(a);
        wsmlReasoner.registerOntology(ont);
        
    }
    
    
    
}
