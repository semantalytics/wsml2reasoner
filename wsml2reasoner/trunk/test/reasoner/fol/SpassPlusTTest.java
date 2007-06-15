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
package reasoner.fol;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLFOLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLFOLReasoner.EntailmentType;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.*;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

/**
 * Interface or class description
 *
 * <pre>
 * Created on 20.03.2007
 * Committed by $Author: hlausen $
 * $Source: /home/richi/temp/w2r/wsml2reasoner/test/reasoner/fol/SpassPlusTTest.java,v $,
 * </pre>
 *
 * @author Rosi, Holger
 *
 * @version $Revision: 1.2 $ $Date: 2007-06-15 10:23:41 $
 */
public class SpassPlusTTest extends TestCase {

    private WsmoFactory wsmoFactory = null;
    private LogicalExpressionFactory leFactory = null;
    
    private WSMLFOLReasoner wsmlReasoner = null;

    private Parser parser = null; 
    
    
    protected void setUp() throws Exception {
        super.setUp();
        WSMO4JManager wsmoManager = new WSMO4JManager();
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        Map<String,Object> m = new HashMap<String, Object>();
        m.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, 
        		WSMLReasonerFactory.BuiltInReasoner.SPASS_PLUS_T);
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().
        		createWSMLFOLReasoner(m);
        parser = Factory.createParser(null);
    }
    
    public void test() throws Exception{
        InputStream in = getClass().getClassLoader().getResourceAsStream(
                "reasoner/fol/family.wsml");
        Ontology ont = (Ontology)parser.parse(new InputStreamReader(in))[0];
        
        wsmlReasoner.registerOntology(ont);
        LogicalExpression conjecture = leFactory.createLogicalExpression(
                "Lisa[hasAncestor hasValue GrandPa]",ont);
        EntailmentType result = wsmlReasoner.checkEntailment(
                (IRI)ont.getIdentifier(), conjecture);
        assertEquals(EntailmentType.entailed, result);
        
        
        conjecture = leFactory.createLogicalExpression(
                "exists ?x (?x[hasChild hasValue someChild])",ont);
        result = wsmlReasoner.checkEntailment(
                (IRI)ont.getIdentifier(), conjecture);
        assertEquals(EntailmentType.entailed, result);
        
        conjecture = leFactory.createLogicalExpression(
                "exists ?x (March[hasChild hasValue ?x])",ont);
        result = wsmlReasoner.checkEntailment(
                (IRI)ont.getIdentifier(), conjecture);
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
        LogicalExpression conjecture = leFactory.createLogicalExpression(
                "exists ?x (?x[b hasValue c]).",ont);
        EntailmentType t;
        t = wsmlReasoner.checkEntailment(iri, conjecture);
        assertEquals(EntailmentType.entailed, t);
        
        conjecture = leFactory.createLogicalExpression(
                "f[b hasValue d]",ont);
        t = wsmlReasoner.checkEntailment(iri, conjecture);
        assertEquals(EntailmentType.notEntailed, t);
    }
    static int i;
    private Ontology getOntology(){
    	IRI iri = wsmoFactory.createIRI("urn://foobar"+ ++i);
        Ontology ont = wsmoFactory.createOntology(iri);
        ont.setDefaultNamespace(iri);
        return ont;
    }
    
    private void addExpression(Ontology ont ,String str) throws ParserException, SynchronisationException, InvalidModelException{
		LogicalExpression le = leFactory.createLogicalExpression(
    	            str,ont);
        Axiom a = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
        a.addDefinition(le);
        ont.addAxiom(a);
    }
    
    public void testSimpleArithmetics() throws Exception{
    	Ontology ont = getOntology();
    	addExpression(ont,"hv(i,12)");
        
    	wsmlReasoner.registerOntology(ont);
    	LogicalExpression conjecture;
    	EntailmentType t;
    	
    	conjecture= leFactory.createLogicalExpression(
                "22 > 2",ont);
        t = wsmlReasoner.checkEntailment((IRI)ont.getIdentifier(), conjecture);
        assertEquals(EntailmentType.entailed, t);
    	
    	conjecture= leFactory.createLogicalExpression(
                "hv(i,(6+6)).",ont);
        t = wsmlReasoner.checkEntailment((IRI)ont.getIdentifier(), conjecture);
        assertEquals(EntailmentType.entailed, t);
        
//        conjecture = leFactory.createLogicalExpression(
//                "f[b hasValue d]",ont);
//        t = wsmlReasoner.checkEntailment(iri, conjecture);
//        assertEquals(EntailmentType.notEntailed, t);
    }
    
}
