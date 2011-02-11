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

public abstract class AbstractFlight2Example2 extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "flight2_example2_simpsons.wsml";

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
