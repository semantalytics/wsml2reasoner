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

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractFlight2Example2 extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "files/flight2_example2_simpsons.wsml";

    private LogicalExpressionParser leParser;

    private Ontology ontology;
	private LPReasoner reasoner;
	
    protected void setUp() throws Exception	{
		ontology = OntologyHelper.loadOntology( ONTOLOGY_FILE );
		
		leParser = new WsmlLogicalExpressionParser(ontology);
		
		reasoner = getLPReasoner();
		reasoner.registerOntology( ontology );
    }
	
	private void query( String query, boolean positive ) throws Exception
	{
		Results result = new Results();
		if( positive )
			result.addBinding();
		
        LogicalExpression qExpression = leParser.parse( query );
		LPHelper.checkResults( reasoner.executeQuery(qExpression), result.get() );
	}

    public void testLogicalExpressionIsNotConsistentWithMembershipMolecule() throws Exception {
    	query("bart_simpson memberOf actor.", false);
    }
    
    public void testLogicalExpressionIsConsistentWithMembershipMolecule() throws Exception {
    	query("bart_simpson memberOf character.", true);
    }
    
    public void testLogicalExpressionIsConsistentWithConjunction() throws Exception {
    	query("?x memberOf school and ?x memberOf place.", true);
    	query("marge_simpson memberOf character and nancy_cartwright memberOf actor.", true);
    }
    
    public void testLogicalExpressionIsNotConsistentWithConjunction() throws Exception {
        query("?x memberOf character and ?x memberOf actor.", false);
    }
    
    public void testIsInstanceHavingInferingAttributeValue() throws Exception {
        query("marge_simpson[hasChild " + "hasValue bart_simpson].", true);
    }
    
    public void testIsInstanceNotHavingInferingAttributeValue() throws Exception {
        query("marge_simpson[hasChild " + "hasValue bobby_simpson].", false);
    }
    
    public void testIsInstanceHavingConstraintAttributeValue() throws Exception {
        query("marge_simpson[hasName " + "hasValue \"Marge Simpson\"].", true);
    }
    
    public void testIsInstanceNotHavingConstraintAttributeValue() throws Exception {
        query("marge_simpson[hasCatchPhrase " + "hasValue \"blabla\"].", false);
    }
}
