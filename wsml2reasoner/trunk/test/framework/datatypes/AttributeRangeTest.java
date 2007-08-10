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
package framework.datatypes;

import java.io.FileReader;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsmo.common.TopEntity;
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
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/test/framework/datatypes/AttributeRangeTest.java,v $,
 * </pre>
 * 
 * @author
 * 
 * @version $Revision: 1.2 $ $Date: 2007-08-10 17:17:50 $
 */
public class AttributeRangeTest extends BaseReasonerTest {
    Parser parser;
    LogicalExpressionFactory leFactory;
    DataFactory dFactory;
    WsmoFactory wsmoFactory;
    WSMLReasoner reasoner;
    
    public void setUp(){
        parser = Factory.createParser(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
        dFactory = Factory.createDataFactory(null);
        reasoner =  null;
        wsmoFactory = Factory.createWsmoFactory(null);
    }

    /**
     * 
     * @throws Exception
     */
    public void attributeRange() throws Exception {
    	Ontology exampleOntology = loadOntology("test/files/AttributeRangeOntology.wsml");
    	try{
    		reasoner.registerOntology(exampleOntology);
    		assertTrue(true);
    	}
    	catch (Exception e){
    		e.printStackTrace();
    		assertTrue(false);
    	}
    }
    
    private Ontology loadOntology(String file) {
        try {
            final TopEntity[] identifiable = parser
                    .parse(new FileReader(file));
            if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
                return (Ontology) identifiable[0];
            } else {
                System.out.println("First Element of file no ontology ");
                return null;
            }

        } catch (Exception e) {
            System.out.println("Unable to parse ontology: " + e.getMessage());
            return null;
        }

    }
    
    public void testAllReasoners() throws Exception{
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.IRIS;
    	reasoner = BaseReasonerTest.getReasoner();
    	attributeRange();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.MINS;
    	reasoner = BaseReasonerTest.getReasoner();
    	attributeRange();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.KAON2;
    	reasoner = BaseReasonerTest.getReasoner();
    	attributeRange();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.PELLET;
    	reasoner = BaseReasonerTest.getReasoner();
    	attributeRange();
    }
}
