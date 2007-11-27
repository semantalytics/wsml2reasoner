/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package variant.flight;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

/**
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2007-10-03 14:58:18 $
 */
public class AttributeInheritanceTest extends BaseReasonerTest {

	private Parser parser = null;
	
    private WSMLReasoner reasoner = null;
    
    private BuiltInReasoner previous;
    
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		parser = Factory.createParser(null);
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        previous = BaseReasonerTest.reasoner;
        reasoner = BaseReasonerTest.getReasoner();
	}
	
	/**
     * @throws InconsistencyException 
	 * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception{
    	super.tearDown();
    	resetReasoner(previous);
    }
    
	public void attributeInheritanceTest() throws Exception {		
		String ns = "http://AttributeInheritanceTestOntology#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "concept A \n" +
                "  attr ofType A \n " +
                "concept B subConceptOf A \n ";

        Ontology o = (Ontology) parser.parse(new StringBuffer(test))[0];
        Set<Map<Variable, Term>> result = null;
        LogicalExpression query;
        reasoner = BaseReasonerTest.getReasoner();
        reasoner.registerOntology(o);

        query = leFactory.createLogicalExpression("?x[?attribute ofType ?range]", o);
        result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        assertEquals(2,result.size());
	}
	
    public void testFlightReasoners() throws Exception{
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
    	attributeInheritanceTest();

    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
    	attributeInheritanceTest();
   	
    	if (exists("org.wsml.reasoner.builtin.kaon2.Kaon2Facade")) { 
    		resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
    		attributeInheritanceTest();
    	}
    }
}
/*
 * $Log: not supported by cvs2svn $
 * 
 *
 */
