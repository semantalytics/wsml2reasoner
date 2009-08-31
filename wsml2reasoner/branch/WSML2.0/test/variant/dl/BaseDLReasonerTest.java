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
package variant.dl;

import java.io.Reader;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.serializer.wsml.WSMLSerializerImpl;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

import base.BaseReasonerTest;

import com.ontotext.wsmo4j.parser.wsml.ParserImplTyped;

/**
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 *
 */
public abstract class BaseDLReasonerTest extends TestCase
{
    protected static int ontologyCount = 1;
    protected OntologyNormalizer normalizer;
    protected WsmoFactory wsmoFactory;
    protected LogicalExpressionFactory leFactory;
	protected Factory factory;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        factory = new FactoryImpl();
        wsmoFactory = factory.getWsmoFactory();
        leFactory = factory.getLogicalExpressionFactory();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    protected Ontology createOntology()
    {
        return createOntology("http://LoR.wsml#");
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
        Parser parser = new ParserImplTyped();
        Reader input = BaseReasonerTest.getReaderForFile(fileName);
        return (Ontology)parser.parse(input)[0];
    }
    
    public static String serializeOntology(Ontology ontology)
    {
        StringBuffer buf = new StringBuffer();
        Serializer serializer = new WSMLSerializerImpl();
        serializer.serialize(new TopEntity[] { ontology }, buf);
        return buf.toString();
    }
}
