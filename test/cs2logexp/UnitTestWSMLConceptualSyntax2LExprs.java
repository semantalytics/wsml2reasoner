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
package cs2logexp;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.wsmo.factory.LogicalExpressionFactory;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

import test.BaseTest;

public class UnitTestWSMLConceptualSyntax2LExprs extends BaseTest{
    /**
     * no real unit test yet but only system.out...
     * @throws Exception
     */
	public void testConceptual2LogExp()throws Exception{        
        String ONTOLOGY_FILE = "examples/humanOntology.wsml";
        
        Ontology o = null;
		// Read simple ontology from file
        
        //try from classpath
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(ONTOLOGY_FILE);
        if (is != null){
            o = (Ontology)wsmlParser.parse(new InputStreamReader(is))[0];
        }else{//classpath has filed try from normal path...
            Reader ontoReader = new FileReader(ONTOLOGY_FILE);
            o = (Ontology)wsmlParser.parse(ontoReader)[0];
        }
        
        assertNotNull(o);
            
        // Print ontology in WSML
        System.out.println("WSML Ontology:\n");
        StringWriter sw = new StringWriter();
        wsmlSerializer.serialize(new TopEntity[]{o}, sw);
        System.out.println(sw.toString());
        System.out.println("--------------\n\n");
        
        System.out.println("\n\nTransforming ontology to axioms only ...");
        
        AxiomatizationNormalizer cs2le = new AxiomatizationNormalizer();
        Ontology normalizedOntology = cs2le.normalize(o);
        
        System.out.println("... finished.");
        
        System.out.println("Normalized WSML Ontology:\n");
        StringWriter sw1 = new StringWriter();
        wsmlSerializer.serialize(new TopEntity[]{normalizedOntology}, sw1);
        System.out.println(sw1.toString());
        System.out.println("--------------\n\n");
    }
	
}
