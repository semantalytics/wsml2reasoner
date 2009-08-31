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

import org.deri.wsmo4j.io.parser.wsml.LogicalExpressionParserImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.api.FOLReasoner;
import org.wsml.reasoner.api.FOLReasoner.EntailmentType;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

import com.ontotext.wsmo4j.parser.wsml.ParserImplTyped;

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
    private FOLReasoner wsmlReasoner = null;
    BuiltInReasoner previous;  
    private Parser wsmlParser = null;
	protected Factory wsmoManager;
    
    
    protected void setUp() throws Exception {
        super.setUp();
        wsmoManager = new FactoryImpl();
        wsmoFactory = wsmoManager.getWsmoFactory();
        previous = BaseReasonerTest.reasoner;
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createFOLReasoner(new HashMap <String, Object> ());
    	wsmlParser = new ParserImplTyped();
    }
    
    public void test() throws Exception{
        InputStream in = getClass().getClassLoader().getResourceAsStream(
                "files/family.wsml");
        Ontology ont = (Ontology)wsmlParser.parse(new InputStreamReader(in))[0];
        
        wsmlReasoner.registerOntology(ont);
        LogicalExpressionParser leParser = new LogicalExpressionParserImpl();
        LogicalExpression conjecture = leParser.parse("Lisa[hasAncestor hasValue GrandPa]");
        EntailmentType result = wsmlReasoner.checkEntailment(
                conjecture);
        assertEquals(EntailmentType.entailed, result);
        
        
        conjecture = leParser.parse("exists ?x (?x[hasChild hasValue someChild])");
        result = wsmlReasoner.checkEntailment(
                conjecture);
        assertEquals(EntailmentType.entailed, result);
        
        conjecture = leParser.parse("exists ?x (March[hasChild hasValue ?x])");
        result = wsmlReasoner.checkEntailment(
                conjecture);
        assertEquals(EntailmentType.entailed, result);
    }
    
    
    public void testhvMolecule() throws Exception{
        IRI iri = wsmoFactory.createIRI("urn://foobar");
        Ontology ont = wsmoFactory.createOntology(iri);
        ont.setDefaultNamespace(iri);
        LogicalExpressionParser leParser = new LogicalExpressionParserImpl();
		LogicalExpression le = leParser .parse("a[b hasValue c]");
        Axiom a = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
        a.addDefinition(le);
        ont.addAxiom(a);
        wsmlReasoner.registerOntology(ont);
        
    }
    
    
    
}
