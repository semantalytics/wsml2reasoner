/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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
    	
    	parseThis(getReaderForFile("files/CompensationOntology.wsml"));
    	parseThis(getReaderForFile("files/CompetenceOntology.wsml"));
    	parseThis(getReaderForFile("files/DrivingLicenseOntology.wsml"));
    	parseThis(getReaderForFile("files/EconomicActivityOntology.wsml"));    	
    	parseThis(getReaderForFile("files/EducationOntology.wsml"));
    	parseThis(getReaderForFile("files/GeographyOntology.wsml"));
    	parseThis(getReaderForFile("files/JobOfferOntology.wsml"));    	
    	parseThis(getReaderForFile("files/LanguageOntology.wsml"));
    	parseThis(getReaderForFile("files/LabourRegulatoryOntology.wsml"));
    	
//    	Ontology importedOntology1 = loadOntology("test/files/CompensationOntology.wsml");
    	
//    	"test/files/DrivingLicenseOntology.wsml");
//    	"test/files/EconomicActivityOntology.wsml");
//    	"test/files/EducationOntology.wsml");
//    	"test/files/GeographyOntology.wsml");
//    	"test/files/JobOfferOntology.wsml");
//    "test/files/JobSeekerOntology.wsml");
//    	"test/files/LabourRegulatoryOntology.wsml");
//    	"test/files/LanguageOntology.wsml");
      
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
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
    	basicTest();
    	
//    	if (exists("org.wsml.reasoner.builtin.kaon2.Kaon2Facade")) { resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
//    	basicTest();
    }
    
}
