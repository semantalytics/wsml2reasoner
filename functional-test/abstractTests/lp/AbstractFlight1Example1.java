/**
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.LogicalExpressionFactory;
import abstractTests.LP;

public abstract class AbstractFlight1Example1 extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "files/flight1_example1_lord_of_the_rings.wsml";

	private static final String NS = "http://example.com/flight1#";
	
    private static final LogicalExpressionFactory leFactory = new WSMO4JManager().getLogicalExpressionFactory();

    private Ontology ontology;
	private LPReasoner reasoner;
	
    protected void setUp() throws Exception	{
		ontology = OntologyHelper.loadOntology( ONTOLOGY_FILE );
		reasoner = getLPReasoner();
		reasoner.registerOntology( ontology );
    }
	
	private void query( String query, Results results ) throws Exception
	{
        LogicalExpression qExpression = leFactory.createLogicalExpression( query, ontology);
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
		r.addBinding(Results.iri(NS + "Aragorn"), Results.string( "Aragorn" ));
		query( "?x[hasName hasValue ?v] and ?v = \"Aragorn\"", r );
    }
    
    public void testNamedArwen() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results.string( "Arwen" ));
		query( "?x[hasName hasValue ?v] and ?v = \"Arwen\"", r );
    }
    
    public void testHigherThan7Feet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Aragorn"), Results.decimal( 7.5 ));
		query( "?x[heightInFeet hasValue ?v] and ?v > 7.0", r );
    }
    
    public void testLowerThan7Feet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Arwen"), Results.decimal( 6.0 ));
		query( "?x[heightInFeet hasValue ?v] and ?v < 7.0", r );
    }
    
    public void testLowerEqualThan7AndHalfFeet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Aragorn"), Results.decimal( 7.5 ));
		r.addBinding(Results.iri(NS + "Arwen"), Results.decimal( 6.0 ));
		query( "?x[heightInFeet hasValue ?v] and ?v =< 7.5", r );
    }
    
    public void testHeight6Feet() throws Exception {
		Results r = new Results("x");
		r.addBinding(Results.iri(NS + "Arwen"));
		query( "?x[heightInFeet hasValue 6.0]", r );
    }
    
    public void testHigherThan6Feet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Aragorn"), Results.decimal( 7.5 ));
		query( "?x[heightInFeet hasValue ?v] and ?v > 6.0", r );
    }
        
    /**
     * NOTE does not work for mins, since data type support is not perfext (wsml decimal will
     * be converted to numeric and .0 lost)
     * @throws Exception
     */
    public void testHigherEqualThan6Feet() throws Exception {
		Results r = new Results("x", "v");
		r.addBinding(Results.iri(NS + "Aragorn"), Results.decimal( 7.5 ));
		r.addBinding(Results.iri(NS + "Arwen"), Results.decimal( 6.0 ));
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
