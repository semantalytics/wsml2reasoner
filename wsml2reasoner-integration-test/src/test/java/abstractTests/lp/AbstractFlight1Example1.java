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

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractFlight1Example1 extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "flight1_example1_lord_of_the_rings.wsml";

	private static final String NS = "http://example.com/flight1#";
	
    private LogicalExpressionParser leParser;

    private Ontology ontology;
	private LPReasoner reasoner;
	
    protected void setUp() throws Exception	{
		ontology = OntologyHelper.loadOntology( ONTOLOGY_FILE );
		
		leParser = new WsmlLogicalExpressionParser(ontology);
		
		reasoner = getLPReasoner();
		reasoner.registerOntology( ontology );
    }
	
	private void query( String query, Results results ) throws Exception
	{
        LogicalExpression qExpression = leParser.parse( query );
		LPHelper.checkResults( reasoner.executeQuery(qExpression), results.get() );
	}

    public void testArwenIsFemale() throws Exception {
    	
		Results r = new Results("x");
		r.addBinding(Results.iri(NS + "Arwen"));
		query( "?x memberOf Female", r );
    }

    public void testNoMaleFemaleTogether() throws Exception {
		Results r = new Results("x");
		query( "?x memberOf Male and ?x memberOf Female", r );
    }
    
    public void testMales() throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri(NS + "Aragorn"));
		r.addBinding(Results.iri(NS + "Elendil"));
		r.addBinding(Results.iri(NS + "Arathorn"));
		query( "?x memberOf Male", r );
    }
    
    public void testAragornLovesElf() throws Exception {
		Results r = new Results("x", "y");
		r.addBinding(Results.iri(NS + "Aragorn"), Results.iri(NS + "Arwen"));
		query( "?x[loves hasValue ?y] and ?y memberOf Elf", r );
    }
    
    public void testAragornLovesFemale() throws Exception {
		Results r = new Results("x", "y");
		r.addBinding(Results.iri(NS + "Aragorn"), Results.iri(NS + "Arwen"));
		query( "?x[loves hasValue ?y] and ?y memberOf Female", r );
    }

    public void testAragornIsElendilsHeir() throws Exception {
		Results r = new Results();
		r.addBinding();
		query( "Aragorn[hasAncestor hasValue Elendil]", r );
    }
    
    public void testNamedAragorn() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Aragorn"), Results._string( "Aragorn" ));
		query( "?x[hasName hasValue ?v] and ?v = \"Aragorn\"", r );
    }
    
    public void testNamedArwen() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results._string( "Arwen" ));
		query( "?x[hasName hasValue ?v] and ?v = \"Arwen\"", r );
    }
    
    public void testHigherThan7Feet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Aragorn"), Results._decimal( 7.5 ));
		query( "?x[heightInFeet hasValue ?v] and ?v > 7.0", r );
    }
    
    public void testLowerThan7Feet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results._decimal( 6.0 ));
		query( "?x[heightInFeet hasValue ?v] and ?v < 7.0", r );
    }
    
    public void testLowerEqualThan7AndHalfFeet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Aragorn"), Results._decimal( 7.5 ));
		r.addBinding(Results.iri(NS + "Arwen"), Results._decimal( 6.0 ));
		query( "?x[heightInFeet hasValue ?v] and ?v =< 7.5", r );
    }
    
    public void testHeight6Feet() throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri(NS + "Arwen"));
		query( "?x[heightInFeet hasValue 6.0]", r );
    }
    
    public void testHigherThan6Feet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Aragorn"), Results._decimal( 7.5 ));
		query( "?x[heightInFeet hasValue ?v] and ?v > 6.0", r );
    }
        
    /**
     * NOTE does not work for mins, since data type support is not perfext (wsml decimal will
     * be converted to numeric and .0 lost)
     * @throws Exception
     */
    public void testHigherEqualThan6Feet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Aragorn"), Results._decimal( 7.5 ));
		r.addBinding(Results.iri(NS + "Arwen"), Results._decimal( 6.0 ));
		query( "?x[heightInFeet hasValue ?v] and ?v >= 6.0", r );
    }
        
    public void testBornBefore900() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results._integer( 500 ));
		query( "?x[wasBorn hasValue ?v] and ?v < 900", r );
    }
    
    public void testBornBeforeEqual900() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results._integer( 500 ));
		r.addBinding(Results.iri(NS + "Aragorn"), Results._integer( 900 ));
		query( "?x[wasBorn hasValue ?v] and ?v =< 900", r );
    }
    
    public void testBorn500() throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri(NS + "Arwen"));
		query( "?x[wasBorn hasValue 500]", r );
    }
    
    public void testBorn490And10() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results._integer( 500 ));
		query( "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(490 + 10))", r );
    }
    
    public void testBorn490And10LogicEqual() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results._integer( 500 ));
		query( "?x[wasBorn hasValue ?v] and ?v = (490 + 10)", r );
    }
    
    public void testBorn510Minus10() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results._integer( 500 ));
		query( "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(510 - 10))", r );
    }
    
    public void testBorn250Twice() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results._integer( 500 ));
		query( "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(250 * 2))", r );
    }
    
    public void testBorn1000DividedBy2() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results._integer( 500 ));
		query( "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(1000 / 2))", r );
    }
    public void testHappy() throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri(NS + "Aragorn"));
		r.addBinding(Results.iri(NS + "Arwen"));
		query( "?x[isHappy hasValue _boolean(\"true\")]", r );
    }
    
    public void testUnHappy() throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri(NS + "Gloin"));
		r.addBinding(Results.iri(NS + "Gimli"));
		query( "?x[isHappy hasValue _boolean(\"false\")]", r );
    }
    
    public void testHappyOrSad() throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri(NS + "Aragorn"));
		r.addBinding(Results.iri(NS + "Arwen"));
		r.addBinding(Results.iri(NS + "Gloin"));
		r.addBinding(Results.iri(NS + "Gimli"));
		query( "?x[isHappy hasValue _boolean(\"true\")] or ?x[isHappy hasValue _boolean(\"false\")]", r );
    }
}
