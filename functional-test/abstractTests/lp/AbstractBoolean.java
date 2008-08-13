/*
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
import org.omwg.ontology.Ontology;
import abstractTests.LPTest;

/**
 * Currently does not work with PELLET (due to detected inconsistency)
 */
public abstract class AbstractBoolean extends TestCase implements LPTest{
   
    public void testSimplerBoolean() throws Exception {
    	final String ns = "http://www.yabooleantest.org#";
    	
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( ns + "truth" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology("files/simplerBoolean.wsml"), "?x[reallyExists hasValue _boolean(\"true\")]", r.get(), getLPReasoner() );
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testSimpleBoolean() throws Exception {
        String ns = "http://ex1.org#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "axiom a definedBy \n" +
                "a[f hasValue _boolean(\"false\")]. \n " +
                "a[t hasValue _boolean(\"true\")].\n " +
                "a(?a) :- a[?a hasValue ?x] and ?x != _boolean(\"false\"). ";
        
        Ontology ontology = OntologyHelper.parseOntology( test );
        
    	Results r = new Results( "y" );
    	r.addBinding( Results.bool( false ) );
    	
        LPHelper.executeQueryAndCheckResults( ontology, "a[f hasValue ?y]", r.get(), getLPReasoner() );

    	r = new Results( "y" );
    	r.addBinding( Results.bool( true ) );
    	
        LPHelper.executeQueryAndCheckResults( ontology, "a[t hasValue ?y]", r.get(), getLPReasoner() );

    	r = new Results( "y" );
    	r.addBinding( Results.iri( ns + "t" ) );

        LPHelper.executeQueryAndCheckResults( ontology, "a(?y)", r.get(), getLPReasoner() );
    }
}
