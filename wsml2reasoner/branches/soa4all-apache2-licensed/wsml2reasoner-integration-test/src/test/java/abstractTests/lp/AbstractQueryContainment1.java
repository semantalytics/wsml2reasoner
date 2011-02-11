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
package abstractTests.lp;

import helper.OntologyHelper;
import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

/**
 * Test to check the IRIS query containment task
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 *
 */
public abstract class AbstractQueryContainment1 extends TestCase implements LP {
	
	public void testSimpleQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment1#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x).\n";
        
        checkQueryContainment( ontology, "car(?x)", "vehicle(?x)", true);
	}
	
	public void testConjunctiveQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment2#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x).\n " +
                "car(?x) :- mobile(?x) and ?x[hasWheel hasValue ?y] " +
                "and ?x[hasTires hasValue 4].\n";
        
        checkQueryContainment( ontology,
        				"mobile(?x) and ?x[hasWheel hasValue ?y] and ?x[hasTires hasValue 4]",
        				"vehicle(?x)", true);
	}
	
	public void testConjunctiveQueryContainment2() throws Exception {
        String ontology = "namespace _\"http://queryContainment3#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x).\n " +
                "car(?x) :- mobile(?x) and ?x[hasWheel hasValue ?y] " +
                "and ?x[hasTires hasValue 4].\n";

        checkQueryContainment( ontology,
        				"mobile(?x) and ?x[hasWheel hasValue ?y] and ?x[hasTires hasValue 4]",
        				"vehicle(?x) and ?x[hasTires hasValue 5]", false);
	}
	
	public void testExtendedPluginQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment4#\" \n" +
                "ontology o \n" +
                "concept GM_Object " +
        		"hasSRS impliesType SRS " +
        		"concept Polygon subConceptOf GM_Object " +
        		"concept SRS " +
        		"concept projSRS subConceptOf SRS " +
        		"instance gk memberOf projSRS " +
        		"axiom gm_objectDefinition definedBy " +
        		"?x[hasSRS hasValue ?srs] implies " +
        		"?x memberOf GM_Object. \n" +
                "relation overlay/3 " +
        		"relation intersection/3 " +
        		"relation union/3 " +
        		"relation dihfference/3 " +
        		"relation symmetricDifference/3 " +
        		"axiom overlayDefinition definedBy " +
        		"intersection(?x,?y,?z) implies overlay(?x,?y,?z). " +
        		"union(?x,?y,?z) implies overlay(?x,?y,?z). " +
        		"difference(?x,?y,?z) implies overlay(?x,?y,?z). " +
        		"symmetricDifference(?x,?y,?z) implies overlay(?x,?y,?z). " +
        		"axiom symmetricDifferenceDefinition definedBy " +
        		"symmetricDifference(?x,?y,?z) implies symmetricDifference(?y,?x,?z). " +
        		"symmetricDifference(?x,?y,?z) impliedBy difference(?x,?y, ?xydif) and " +
        		"difference(?y,?x,?yxdif) and union(?xydif, ?yxdif,?z). \n";
        
        checkQueryContainment( ontology,
        				"?x[hasSRS hasValue gk] memberOf Polygon and ?y[hasSRS hasValue gk] memberOf Polygon.\n",
        				"?a[hasSRS hasValue ?refsys] memberOf GM_Object and ?b[hasSRS hasValue ?refsys] memberOf GM_Object and ?refsys memberOf projSRS.",
        				true);
	}
	
	public void testDisjunctiveQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment5#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x) and mobile(?x).\n";
        
        try {
            checkQueryContainment( ontology, "car(?x) or mobile(?x)", "vehicle(?x)", false);
        	fail();
		} catch (Exception e) {
		};
	}
	
	public void testNegativeQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment6#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x) and mobile(?x).\n";

        try {
            checkQueryContainment( ontology, "car(?x) or not(mobile(?x))", "vehicle(?x)", false);
        	fail();
		} catch (Exception e) {
		};
	}
	
	public void testBuiltInQueryContainment() throws Exception {
        String ontology = "namespace _\"http://queryContainment7#\" \n" +
                "ontology o \n" +
                "axiom definedBy \n" +
                "vehicle(?x) :- car(?x) and ?x[hasTires hasValue ?y] and ?y=4.\n";

        try {
            checkQueryContainment( ontology, "car(?x) and ?x[hasTires hasValue ?y] and ?y=5", "vehicle(?x)", false);
        	fail();
		} catch (Exception e) {
		};
	}
	
	private void checkQueryContainment( String ontologyString, String queryString1, String queryString2, boolean expected ) throws Exception
	{
        Ontology ontology = OntologyHelper.parseOntology( ontologyString );

        LPReasoner reasoner = getLPReasoner();
        reasoner.registerOntology(ontology);
        
        LogicalExpressionParser leParser = new WsmlLogicalExpressionParser(ontology);
		// build queries
        LogicalExpression query1 = leParser.parse(queryString1);
        LogicalExpression query2 = leParser.parse(queryString2);
        
        // perform query containment check
        boolean check = reasoner.checkQueryContainment(query1, query2);
        assertEquals(expected, check);
        
        reasoner.deRegister();
	}
}
