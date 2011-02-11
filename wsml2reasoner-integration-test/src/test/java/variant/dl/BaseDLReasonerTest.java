/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package variant.dl;

import java.io.Reader;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.serializer.wsml.WSMLSerializerImpl;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

import base.BaseReasonerTest;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

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
	protected FactoryContainer factory;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        factory = new WsmlFactoryContainer();
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
        Parser parser = new WsmlParser();
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
