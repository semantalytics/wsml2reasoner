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

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.common.IRI;

import base.BaseReasonerTest;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Holger Lausen, DERI Innsbruck
 */
public class ManyImports extends BaseReasonerTest {
	
    String topOntology = "files/EG1.wsml";
    BuiltInReasoner previous;
    
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	parseThis(getReaderForFile("files/JobSeekerOntology.wsml"));
    	parseThis(getReaderForFile("files/JobOfferOntology.wsml"));
    	parseThis(getReaderForFile("files/CompetenceOntology.wsml"));
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
//    	Ontology exampleOntology =  loadOntology("test/files/JobOfferOntology1.wsml");
    	
//    	Ontology importedOntology1 = loadOntology("test/files/CompensationOntology.wsml");
    	
//    	Ontology importedOntology3 = loadOntology("test/files/DrivingLicenseOntology.wsml");
//    	Ontology importedOntology4 = loadOntology("test/files/EconomicActivityOntology.wsml");
//    	Ontology importedOntology5 = loadOntology("test/files/EducationOntology.wsml");
//    	Ontology importedOntology6 = loadOntology("test/files/GeographyOntology.wsml");
//    	Ontology importedOntology7 = loadOntology("test/files/JobOfferOntology.wsml");
//    	Ontology importedOntology8 = loadOntology("test/files/JobSeekerOntology.wsml");
//    	Ontology importedOntology9 = loadOntology("test/files/LabourRegulatoryOntology.wsml");
//    	Ontology importedOntology10 = loadOntology("test/files/LanguageOntology.wsml");
      
        String query = "?x memberOf Time#DateTimeDescription";
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                query, o);
        logExprSerializer.serialize(qExpression);
        
        Set<Map<Variable, Term>> result = wsmlReasoner.executeQuery((IRI) o
                .getIdentifier(), qExpression);
        
        System.out.println("Found < " + result.size()
                + " > results to the query:");
        int i = 0;
        for (Map<Variable, Term> vBinding : result) {
            System.out.println("(" + (++i) + ") -- " + vBinding.toString());
        }
        assertEquals(5 , result.size());
        System.out.println("Done.");
    }
    
    public void testFlightReasoners() throws Exception{
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
    	basicTest();
    	
//    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
//    	basicTest();
    	
//    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
//    	basicTest();
    }
    
}
