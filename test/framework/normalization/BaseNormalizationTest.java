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
package framework.normalization;

import java.io.Reader;

import junit.framework.TestCase;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

import base.BaseReasonerTest;

/**
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 *
 */
public abstract class BaseNormalizationTest extends TestCase
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
