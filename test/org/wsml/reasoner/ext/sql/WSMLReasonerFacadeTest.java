package org.wsml.reasoner.ext.sql;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.ext.sql.QueryUtil;
import org.wsml.reasoner.ext.sql.ReasonerResult;
import org.wsml.reasoner.ext.sql.WSMLReasonerFacade;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.ParserException;

public class WSMLReasonerFacadeTest extends TestCase {

	protected WSMLReasonerFacade facade;
	protected String localOntologyIRI;
	
	protected String testmemberOfQuery1;
	protected String testArwenExpected;
	 
	protected void setUp() throws Exception {
		super.setUp();
		facade = new WSMLReasonerFacade();
		
		File file = new File("test/files/lordOfTheRings.wsml");
		URL ontoTestURL = file.toURI().toURL();			
		localOntologyIRI = ontoTestURL.toString();
		
		testmemberOfQuery1 = "?x memberOf ?y";
		testArwenExpected = "?x[hasParent hasValue Elrond]";
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testExecuteWsmlQuery() {		
		try {			
			ReasonerResult res = facade.executeWsmlQuery(testmemberOfQuery1, localOntologyIRI);
			assertNotNull(res);
			
			ReasonerResult arwenInThere = facade.executeWsmlQuery(testArwenExpected, localOntologyIRI);
			Set<Map<Variable, Term>> r = arwenInThere.getResult();
			assertEquals(true, r.size()>0);
			
			for (Map<Variable, Term> row : r)
			{
				for (Variable var : row.keySet()) {						
					assertEquals("?x", var.toString()); 
					//we only expect "arwen" as string
					Term valueForVar = row.get(var);
					String termAsString = QueryUtil.termToString(valueForVar, facade.getOntology());										
					assertEquals("Arwen", termAsString);				
				}
			}					
		} catch (ParserException e) {
			fail(e.getMessage());
		} catch (InconsistencyException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidModelException e) {
			fail(e.getMessage());
		}			
	}

	public void testLoadLocalOntology() {		
		try {
			Ontology loaded = facade.loadOntology(localOntologyIRI);
			assertNotNull(loaded);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ParserException e) {
			fail(e.getMessage());
		} catch (InvalidModelException e) {
			fail(e.getMessage());
		}
	}

}
