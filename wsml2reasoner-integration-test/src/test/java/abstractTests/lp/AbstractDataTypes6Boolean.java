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

import org.omwg.ontology.Ontology;

import abstractTests.LP;

/**
 * Currently does not work with PELLET (due to detected inconsistency)
 */
public abstract class AbstractDataTypes6Boolean extends TestCase implements LP{
   
    public void testSimplerBoolean() throws Exception {
    	final String ns = "http://example.com/datatypes6#";
    	
    	final Ontology ontology = OntologyHelper.loadOntology("datatypes6_boolean.wsml");
    	
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( ns + "truth" ) );
    	
    	LPHelper.executeQueryAndCheckResults( ontology, "?x[reallyExists hasValue xsd#boolean(\"true\")]", r.get(), getLPReasoner() );

    	r = new Results( "x" );
    	r.addBinding( Results.iri( ns + "mysticism" ) );
    	
    	LPHelper.executeQueryAndCheckResults( ontology, "?x[reallyExists hasValue xsd#boolean(\"false\")]", r.get(), getLPReasoner() );
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
    	r.addBinding( Results._bool( false ) );
    	
    	LPHelper.outputON();
        LPHelper.executeQueryAndCheckResults( ontology, "a[f hasValue ?y]", r.get(), getLPReasoner() );

    	r = new Results( "y" );
    	r.addBinding( Results._bool( true ) );
    	
        LPHelper.executeQueryAndCheckResults( ontology, "a[t hasValue ?y]", r.get(), getLPReasoner() );

    	r = new Results( "y" );
    	r.addBinding( Results.iri( ns + "t" ) );

        LPHelper.executeQueryAndCheckResults( ontology, "a(?y)", r.get(), getLPReasoner() );
    }
}
