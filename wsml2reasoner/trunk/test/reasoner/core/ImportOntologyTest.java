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
package reasoner.core;

import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.Factory;
import org.wsmo.locator.Locator;
import org.wsmo.wsml.Parser;

import test.BaseReasonerTest;

public class ImportOntologyTest extends BaseReasonerTest {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ImportOntologyTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(ImportOntologyTest.class)) {};
        return test;
    }

    public void testImportOntologyTest() throws Exception {
        String ns = "http://ex1.org#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "importsOntology o2 \n" +
                "concept c \n" +
                "instance i1 memberOf c \n " +
                "ontology o2 \n" +
                "instance i2 memberOf c \n ";
        
        
        wsmoManager = new WSMO4JManager();

        leFactory = wsmoManager.getLogicalExpressionFactory();

        wsmoFactory = wsmoManager.getWSMOFactory();

        dataFactory = wsmoManager.getDataFactory();
        
        Parser wsmlparserimpl = org.wsmo.factory.Factory.createParser(null);
        
        Ontology o = (Ontology) wsmlparserimpl.parse(new StringBuffer(test))[0];

        LogicalExpression query = leFactory.createLogicalExpression(
                "?x memberOf c", o);
        WSMLReasoner reasoner = BaseReasonerTest.getReasoner();
        Set<Map<Variable, Term>> result = null;
        reasoner.registerOntology(o);
        result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        assertEquals(2, result.size());
        reasoner.deRegisterOntology((IRI) o.getIdentifier());
        
        }
    
   /* public void testImportOntologyTestWithLocator() throws Exception {
        String ns = "http://www.example.org/example/";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o2 \n" +
                "importsOntology _\"file:///Developer/andiamo/locations.wsml\" \n" +
                "concept c \n" +
                "instance i3 memberOf c \n ";

        Locator l = new Locator(){
            @SuppressWarnings({"unchecked","unchecked"})
			public Set lookup(Identifier arg0) throws SynchronisationException {
                Set ret = new HashSet();
                try{
                    TopEntity[] te = Factory.createParser(null).parse(
                            new FileReader(arg0.toString()));
                    for (int i=0; 0<te.length; i++){
                        if (te[i].getIdentifier().equals(arg0)){
                            ret.add(te[i]);
                        }
                        if (te[i] instanceof Ontology){
                            Set s = ((Ontology)te[i]).findEntity(arg0);
                            if (s.size()>0){
                                Iterator it = s.iterator();
                                while(it.hasNext()){
                                    ret.add(it.next());
                                }
                            }
                        }
                    }
                    
                    //ret.add();
                }catch (Exception e){
                    //who what now?
                }
                return ret;
            }
            
            public Entity lookup(Identifier arg0, Class arg1) throws SynchronisationException {
            		return null;
            }
        };
        
        Factory.getLocatorManager().addLocator(l);
        
        wsmoManager = new WSMO4JManager();

        leFactory = wsmoManager.getLogicalExpressionFactory();

        wsmoFactory = wsmoManager.getWSMOFactory();

        dataFactory = wsmoManager.getDataFactory();
        
        Parser wsmlparserimpl = org.wsmo.factory.Factory.createParser(null);
        
        Ontology o = (Ontology) wsmlparserimpl.parse(new StringBuffer(test))[0];

        LogicalExpression query = leFactory.createLogicalExpression(
                "?x memberOf c", o);
        WSMLReasoner reasoner = BaseReasonerTest.getReasoner();
        Set<Map<Variable, Term>> result = null;
        reasoner.registerOntology(o);
        result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        assertEquals(2, result.size());
        }*/
}
