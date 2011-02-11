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
package open;

import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Holger Lausen, DERI Innsbruck
 */
public class ManyImports extends BaseReasonerTest {
	
    //String topOntology = "files/EG1.wsml";
    BuiltInReasoner previous;
    
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	previous = BaseReasonerTest.reasoner;
    	
    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
    }
    

    /**
     * loads an Ontology and performs sample query
     */
    public void basicTest() throws Exception {
    	
    	parseThis(getReaderForFile("CompensationOntology.wsml"));
    	parseThis(getReaderForFile("CompetenceOntology.wsml"));
    	parseThis(getReaderForFile("DrivingLicenseOntology.wsml"));
    	parseThis(getReaderForFile("EconomicActivityOntology.wsml"));    	
    	parseThis(getReaderForFile("EducationOntology.wsml"));
    	parseThis(getReaderForFile("GeographyOntology.wsml"));
    	parseThis(getReaderForFile("JobOfferOntology.wsml"));    	
    	parseThis(getReaderForFile("LanguageOntology.wsml"));
    	parseThis(getReaderForFile("LabourRegulatoryOntology.wsml"));
    	
//    	Ontology importedOntology1 = loadOntology("CompensationOntology.wsml");
    	
//    	"DrivingLicenseOntology.wsml");
//    	"EconomicActivityOntology.wsml");
//    	"EducationOntology.wsml");
//    	"GeographyOntology.wsml");
//    	"JobOfferOntology.wsml");
//    "JobSeekerOntology.wsml");
//    	"LabourRegulatoryOntology.wsml");
//    	"LanguageOntology.wsml");
      
//        String query = "?x memberOf Time#DateTimeDescription";
//        LogicalExpression qExpression = leFactory.createLogicalExpression(
//                query, o);
//        logExprSerializer.serialize(qExpression);
//        
//        Set<Map<Variable, Term>> result = wsmlReasoner.executeQuery((IRI) o
//                .getIdentifier(), qExpression);
//        
//        System.out.println("Found < " + result.size()
//                + " > results to the query:");
//        int i = 0;
//        for (Map<Variable, Term> vBinding : result) {
//            System.out.println("(" + (++i) + ") -- " + vBinding.toString());
//        }
//        assertEquals(5 , result.size());
//        System.out.println("Done.");
    }
    
    public void testFlightReasoners() throws Exception{
//    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
//    	basicTest();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
    	basicTest();
    	
//    	if (exists("org.wsml.reasoner.builtin.kaon2.Kaon2Facade")) { resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
//    	basicTest();
    }
    
}
