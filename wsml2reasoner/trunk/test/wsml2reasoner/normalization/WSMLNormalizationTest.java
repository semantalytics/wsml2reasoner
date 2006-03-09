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
package wsml2reasoner.normalization;

import java.io.*;

import junit.framework.*;

import org.omwg.ontology.*;
import org.wsml.reasoner.impl.*;
import org.wsml.reasoner.transformation.*;
import org.wsmo.common.*;
import org.wsmo.factory.*;
import org.wsmo.wsml.*;

import test.*;

public abstract class WSMLNormalizationTest extends TestCase
{
    protected static int ontologyCount = 1;
    protected OntologyNormalizer normalizer;
    protected WsmoFactory wsmoFactory;
    protected LogicalExpressionFactory leFactory;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        WSMO4JManager wmsoManager = new WSMO4JManager();
        wsmoFactory = wmsoManager.getWSMOFactory();
        leFactory = wmsoManager.getLogicalExpressionFactory();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    protected Ontology createOntology()
    {
        return createOntology("http://LoR.wsml#");
//        int ontologyNumber = ontologyCount++;
//        Ontology ontology = wsmoFactory.createOntology(wsmoFactory.createIRI("http://mu.org#ont" + Integer.toString(ontologyNumber)));
//        ontology.setDefaultNamespace(wsmoFactory.createIRI("http://mu." + Integer.toString(ontologyNumber) + ".org#"));
//        return ontology;
    }
    
    protected Ontology createOntology(String namespace)
    {
        int ontologyNumber = ontologyCount++;
        Ontology ontology = wsmoFactory.createOntology(wsmoFactory.createIRI("urn://LoR" + Integer.toString(ontologyNumber)));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(namespace));
        return ontology;
    }
    
    protected Ontology parseOntology(String fileName) throws Exception
    {
        Parser parser = Factory.createParser(null);
        Reader input = BaseReasonerTest.getReaderForFile(fileName);
        return (Ontology)parser.parse(input)[0];
    }
    
    public static String serializeOntology(Ontology ontology)
    {
        StringBuffer buf = new StringBuffer();
        Serializer serializer = Factory.createSerializer(null);
        serializer.serialize(new TopEntity[] { ontology }, buf);
        return buf.toString();
    }
}
