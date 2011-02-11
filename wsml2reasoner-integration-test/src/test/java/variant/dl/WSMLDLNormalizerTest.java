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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.wsmo4j.validator.WsmlDLValidator;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Relation;
import org.omwg.ontology.RelationInstance;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.dl.Relation2AttributeNormalizer;
import org.wsml.reasoner.transformation.dl.WSMLDLLogExprNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.validator.ValidationError;
import org.wsmo.validator.ValidationWarning;
import org.wsmo.wsml.Parser;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

public class WSMLDLNormalizerTest extends BaseDLReasonerTest {

    protected Relation2AttributeNormalizer relTransformer;

    protected WSMLDLLogExprNormalizer logExprTransformer;

    protected AxiomatizationNormalizer axiomTransformer;

    protected Ontology ontology;

    protected void setUp() throws Exception {
        super.setUp();
        // in order to keep track of cyclic imports
        relTransformer = new Relation2AttributeNormalizer(new WsmlFactoryContainer());
        axiomTransformer = new AxiomatizationNormalizer(new WsmlFactoryContainer());
        logExprTransformer = new WSMLDLLogExprNormalizer(new WsmlFactoryContainer());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ontology = null;
        System.gc();
    }

    public void testPreProcessingSteps() throws Exception {
        // read test file and parse it
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("wsml2owlNormExample.wsml");
        assertNotNull(is);
        // WsmoFactory wsmoFactory = new WsmlFactoryContainer().getWsmoFactory();
    	Parser wsmlParser = new WsmlParser();
        // assuming first topentity in file is an ontology
        ontology = (Ontology) wsmlParser.parse(new InputStreamReader(is))[0];

        System.out.println(serializeOntology(ontology) + "\n\n\n-------------\n\n\n");

        // validate the test ontology
        WsmlDLValidator validator = new WsmlDLValidator(leFactory);
        List <ValidationError> errors = new ArrayList <ValidationError>();
        boolean b = validator.isValid(ontology, errors, new ArrayList <ValidationWarning> ());
        for (int i = 0; i < errors.size(); i++)
            System.out.println(errors.get(i));
        assertTrue(b);

        // normalize ontology with the WSMLDLNormalizer:
        createOntology();

        Set<Entity> entities = new HashSet<Entity>();
        entities.addAll(ontology.listConcepts());
        entities.addAll(ontology.listInstances());
        entities.addAll(ontology.listRelations());
        entities.addAll(ontology.listRelationInstances());
        entities.addAll(ontology.listAxioms());

        Set<Entity> e1 = relTransformer.normalizeEntities(entities);
        System.out.println(serializeOntology(createOntology(e1, "http://www.WSMLDLNormalizerTestOntology1.com")) + "\n\n\n-------------\n\n\n");

        Set<Entity> e2 = axiomTransformer.normalizeEntities(e1);
        System.out.println(serializeOntology(createOntology(e2, "http://www.WSMLDLNormalizerTestOntology2.com")) + "\n\n\n-------------\n\n\n");

        Set<Entity> e3 = logExprTransformer.normalizeEntities(e2);
        System.out.println(serializeOntology(createOntology(e3, "http://www.WSMLDLNormalizerTestOntology3.com")) + "\n\n\n-------------\n\n\n");
    }

    private Ontology createOntology(Set<Entity> entities, String IRI) throws InvalidModelException {
        Ontology o = wsmoFactory.createOntology(wsmoFactory.createIRI(IRI));
        for (Entity e : entities) {
            if (e instanceof Concept) {
                o.addConcept((Concept) e);
            }
            else if (e instanceof Instance) {
                o.addInstance((Instance) e);
            }
            else if (e instanceof Relation) {
                o.addRelation((Relation) e);
            }
            else if (e instanceof RelationInstance) {
                o.addRelationInstance((RelationInstance) e);
            }
            else if (e instanceof Axiom) {
                o.addAxiom((Axiom) e);
            }
        }
        return o;
    }

    public void testAnonIdTransformationss() throws Exception {
        // read test file and parse it
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("anonIds.wsml");
        assertNotNull(is);
        //WsmoFactory wsmoFactory = new WsmlFactoryContainer().getWsmoFactory();
    	Parser wsmlParser = new WsmlParser();
        // assuming first topentity in file is an ontology
        ontology = (Ontology) wsmlParser.parse(new InputStreamReader(is))[0];

        System.out.println(serializeOntology(ontology) + "\n\n\n-------------\n\n\n");

        // validate the test ontology
        WsmlDLValidator validator = new WsmlDLValidator(leFactory);
        List <ValidationError> errors = new ArrayList <ValidationError>();
        boolean b = validator.isValid(ontology, errors, new ArrayList <ValidationWarning> ());
        for (int i = 0; i < errors.size(); i++)
            System.out.println(errors.get(i));
        assertTrue(b);

        // normalize ontology with the WSMLDLNormalizer:
        createOntology();

        Set<Entity> entities = new HashSet<Entity>();
        entities.addAll(ontology.listConcepts());
        entities.addAll(ontology.listInstances());
        entities.addAll(ontology.listRelations());
        entities.addAll(ontology.listRelationInstances());
        entities.addAll(ontology.listAxioms());

        entities = relTransformer.normalizeEntities(entities);
        entities = logExprTransformer.normalizeEntities(entities);

        System.out.println(serializeOntology(createOntology(entities, "http://www.WSMLDLNormalizerTestOntology4.com")) + "\n\n\n-------------\n\n\n");
    }

    public void testRelationTransformations() throws Exception {
        // read test file and parse it
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("relation2attribute.wsml");
        assertNotNull(is);
        // WsmoFactory wsmoFactory = new WsmlFactoryContainer().getWsmoFactory();
    	Parser wsmlParser = new WsmlParser();
        // assuming first topentity in file is an ontology
        ontology = (Ontology) wsmlParser.parse(new InputStreamReader(is))[0];

        System.out.println(serializeOntology(ontology) + "\n\n\n-------------\n\n\n");

        // validate the test ontology
        WsmlDLValidator validator = new WsmlDLValidator(leFactory);
        List <ValidationError> errors = new ArrayList <ValidationError>();
        boolean b = validator.isValid(ontology, errors, new ArrayList <ValidationWarning> ());
        for (int i = 0; i < errors.size(); i++)
            System.out.println(errors.get(i));
        assertTrue(b);

        // normalize ontology with the WSMLDLNormalizer:
        createOntology();

        Set<Entity> entities = new HashSet<Entity>();
        entities.addAll(ontology.listConcepts());
        entities.addAll(ontology.listInstances());
        entities.addAll(ontology.listRelations());
        entities.addAll(ontology.listRelationInstances());
        entities.addAll(ontology.listAxioms());

        entities = relTransformer.normalizeEntities(entities);
        entities = logExprTransformer.normalizeEntities(entities);

        System.out.println(serializeOntology(createOntology(entities, "http://www.WSMLDLNormalizerTestOntology5.com")) + "\n\n\n-------------\n\n\n");
    }

    public void testDecompositionTransformations() throws Exception {
        // read test file and parse it
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("decomposition.wsml");
        assertNotNull(is);
        // WsmoFactory wsmoFactory = new WsmlFactoryContainer().getWsmoFactory();
    	Parser wsmlParser = new WsmlParser();
        // assuming first topentity in file is an ontology
        ontology = (Ontology) wsmlParser.parse(new InputStreamReader(is))[0];

        System.out.println(serializeOntology(ontology) + "\n\n\n-------------\n\n\n");

        // validate the test ontology
        WsmlDLValidator validator = new WsmlDLValidator(leFactory);
        List <ValidationError> errors = new ArrayList <ValidationError>();
        boolean b = validator.isValid(ontology, errors, new ArrayList <ValidationWarning> ());
        for (int i = 0; i < errors.size(); i++)
            System.out.println(errors.get(i));
        assertTrue(b);

        // normalize ontology with the WSMLDLNormalizer:
        createOntology();

        Set<Entity> entities = new HashSet<Entity>();
        entities.addAll(ontology.listConcepts());
        entities.addAll(ontology.listInstances());
        entities.addAll(ontology.listRelations());
        entities.addAll(ontology.listRelationInstances());
        entities.addAll(ontology.listAxioms());

        entities = relTransformer.normalizeEntities(entities);
        entities = logExprTransformer.normalizeEntities(entities);

        System.out.println(serializeOntology(createOntology(entities, "http://www.WSMLDLNormalizerTestOntology6.com")) + "\n\n\n-------------\n\n\n");
    }

}
