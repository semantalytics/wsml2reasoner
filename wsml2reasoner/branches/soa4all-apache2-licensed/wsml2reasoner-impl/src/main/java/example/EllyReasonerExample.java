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
package example;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.Parser;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Holger Lausen, DERI Innsbruck
 */
public class EllyReasonerExample {

    /**
     * @param args
     *            none expected
     */
    public static void main(String[] args) {
        EllyReasonerExample ex = new EllyReasonerExample();
        try {
        	ex.doTestRun();
        	ex.doHumanTestRun();
            System.exit(0);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * loads an Ontology and performs sample query
     */
    public void doTestRun() throws Exception {
    	FactoryContainer container = new WsmlFactoryContainer();
    	
        Ontology exampleOntology = loadOntology("example/locations.wsml");
        if (exampleOntology == null)
            return;

        // get A reasoner
        DLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createDL2Reasoner(null);
        
        // Register ontology
        reasoner.registerOntology(exampleOntology);
        // reasoner.registerOntologyNoVerification(exampleOntology);

        /* **********
         * Query
         * **********/
        
        IRI identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/country");
        Set<Instance> instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));
        
        System.out.println("Instances of country:");
        System.out.println(instances);
        
        identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/city");
        Set<Concept> subConcepts = reasoner.getSubConcepts(container.getWsmoFactory().createConcept(identifier));
        
        System.out.println("SubConcepts of country:");
        System.out.println(subConcepts);
        
    }

    
    void doHumanTestRun() throws Exception {

    	FactoryContainer container = new WsmlFactoryContainer();
    	
        Ontology exampleOntology = loadOntology("example/human.wsml");
        if (exampleOntology == null)
            return;

        // get A reasoner
        DLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createDL2Reasoner(null);
        
        // Register ontology
        reasoner.registerOntology(exampleOntology);
        // reasoner.registerOntologyNoVerification(exampleOntology);

        /* **********
         * Query
         * **********/
        
        IRI identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
        Set<Instance> instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));
        
        System.out.println("Instances of Human:");
        System.out.println(instances);
        
        identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
        Set<Concept> subConcepts = reasoner.getSubConcepts(container.getWsmoFactory().createConcept(identifier));
        
        System.out.println("SubConcepts of Human:");
        System.out.println(subConcepts);
        
        identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/hasName");
        System.out.println("Concepts of hasName: " + reasoner.getConceptsOf(identifier));
//        System.out.println("RangesOfConstraintAttribute of hasName: " + reasoner.getRangesOfConstraintAttribute(identifier));
        System.out.println("RangesOfInferingAttribute of hasName: " + reasoner.getRangesOfInferingAttribute(identifier));
//        System.out.println("ConstraintAttributeInstances of hasName: " + reasoner.getConstraintAttributeInstances(identifier));
    }
    
    /**
     * Utility Method to get the object model of a wsml ontology
     * 
     * @param file
     *            location of source file (It will be attempted to be loaded from
     *            current class path)
     * @return object model of ontology at file location
     */
    private Ontology loadOntology(String file) {
		Parser wsmlParser = new WsmlParser();

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        try {
            final TopEntity[] identifiable = wsmlParser.parse(new InputStreamReader(is));
            if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
                return (Ontology) identifiable[0];
            }
            else {
                System.out.println("First Element of file no ontology ");
                return null;
            }

        }
        catch (Exception e) {
            System.out.println("Unable to parse ontology: " + e.getMessage());
            return null;
        }

    }

}
