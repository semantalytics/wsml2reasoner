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

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.ParserException;

import abstractTests.LP;


public abstract class AbstractAttributeInheritance extends TestCase implements LP{

	
	public void testAttributeInheritance() throws ParserException, InconsistencyException, IOException, InvalidModelException {
		
		String ns = "http://ex1.org#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "concept A \n" +
                "  attr ofType A \n " +
                "concept B subConceptOf A \n ";
        
        String query = "?x[?attribute ofType ?range]";
       
        Set<Map<Variable, Term>> result = LPHelper.executeQuery(OntologyHelper.parseOntology(test), query, getLPReasoner() );
        assertEquals(2, result.size());
//        for( Map<Variable, Term> p : result){
//        	
//        }
	}   
	
}
