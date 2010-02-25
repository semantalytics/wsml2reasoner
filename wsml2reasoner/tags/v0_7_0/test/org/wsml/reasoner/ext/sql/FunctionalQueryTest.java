package org.wsml.reasoner.ext.sql;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

public class FunctionalQueryTest extends TestCase
{

	private final File lotrOntology = new File("test/files/lordOfTheRings.wsml");
	private final File simpsonsOntology = new File(
			"test/files/simpsons.wsml");
	private URL ontologyIRI1;
	private URL ontologyIRI2;
	private Ontology preloaded;
	private Ontology preloaded2;
	
	private LPReasoner reasoner;
	private Parser parser;

	protected void setUp() throws Exception
	{
		super.setUp();
//		WsmoFactory wsmoFactory = new WsmlFactoryContainer().getWsmoFactory();
		parser = new WsmlParser();
		ontologyIRI1 = lotrOntology.toURI().toURL();
		ontologyIRI2 = simpsonsOntology.toURI().toURL();
		
        reasoner = DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(null);        
        preloaded = loadOntology(ontologyIRI1); 
        preloaded2 = loadOntology(ontologyIRI2);
        Set<Ontology> ontos = new HashSet<Ontology>();
        ontos.add(preloaded);
        ontos.add(preloaded2);
        
        reasoner.registerOntologies(ontos);
	}

	public void testBasic() throws Exception
	{
		String query = "SELECT ?x " + "FROM " + "_\"" + ontologyIRI1
				+ "\"" + " WHERE ?x memberOf ?y";
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);

		assertNotNull(r);
		assertTrue(r.size() > 0);	
	}

	public void testGroupBy() throws Exception
	{
		String query = "SELECT ?x, COUNT(*) " + "FROM " + "_\"" + ontologyIRI1
				+ "\"" + " WHERE ?x memberOf ?y " + "GROUP BY ?x";
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);

		assertNotNull(r);
		assertTrue(r.size() > 0);	
	}

	public void testHavingCount() throws Exception
	{
		String query = "SELECT ?place, COUNT(?place) FROM _\"" + ontologyIRI2
				+ "\"" + " WHERE ?employee[hasWorkingPlace hasValue ?place] "
				+"GROUP BY ?place"
				+ " HAVING COUNT(?place) > 4 ";
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);

		assertNotNull(r);
		assertTrue(r.size() > 0);	
	}

	public void testOrderBy() throws Exception
	{
		String query = "SELECT ?place, ?employee FROM _\"" + ontologyIRI2 
				+ "\"" + " WHERE ?employee[hasWorkingPlace hasValue ?place] "
				+ " ORDER BY 1, 2";
		
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);

		assertNotNull(r);
		assertTrue(r.size() > 0);			
	}
	
	public void testComplex() throws Exception
	{
		String query = "SELECT ?place, COUNT(?place) FROM _\"" + ontologyIRI2
				+ "\"" + " WHERE ?employee[hasWorkingPlace hasValue ?place] "
				+ " GROUP BY ?place " + " HAVING COUNT(?place) > 4 "
				+ " ORDER BY ?place DESC "
				+ " LIMIT 2";
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);
		
		QueryUtil.printResults(r, null);
		
		assertNotNull(r);
		assertTrue(r.size() > 0);	
	}
	
	public void testPreloadingReasoner() throws Exception
	{        
		String query = "SELECT ?x " + "FROM " + "_\"" + ontologyIRI1
		+ "\"" + " WHERE ?x memberOf ?y";
		WSMLQuery wqe = new WSMLQuery(reasoner);
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);
			
		assertNotNull(r);
		assertTrue(r.size() > 0);	
	}
	
	
	
	public void testPreloadingReasonerDoesNotNeedFrom() throws Exception
	{
		String query = "SELECT ?x " + " WHERE ?x memberOf ?y";
		
		WSMLQuery wqe = new WSMLQuery(reasoner);
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);
			
		assertNotNull(r);
		assertTrue(r.size() > 0);		
	}
	
	public void testComplexPreloadingReasonerDoesNotNeedFrom() throws Exception 
	{
		String query = "SELECT ?place, COUNT(?place) " + " WHERE ?employee[_\"http://ontologies.deri.org/hasWorkingPlace\" hasValue ?place]"
		+ " GROUP BY ?place " + " HAVING COUNT(?place) > 4 "
		+ " ORDER BY ?place DESC "
		+ " LIMIT 2";
		WSMLQuery wqe = new WSMLQuery(reasoner);
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);		
		
		QueryUtil.printResults(r, null);
		
		assertNotNull(r);		
		assertTrue(r.size() > 0);			
	}
	
	public void testPreloadingReasonerIgnoresFrom() throws Exception
	{
		String garbageURI = "http://garbage";
		String query = "SELECT ?x " + "FROM " + "_\"" + garbageURI
		+ "\"" + " WHERE ?x memberOf ?y";
		
		WSMLQuery wqe = new WSMLQuery(reasoner);
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);
		
		assertNotNull(r);
		assert(r.size() > 0);		
	}
	
	
	private Ontology loadOntology(URL location) 
	{		
		Ontology ontology = null;
		TopEntity[] identifiable;
		try {
			InputStream is = location.openStream();
			identifiable = parser.parse(new InputStreamReader(is));
			if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
		    	ontology = ((Ontology) identifiable[0]);
		    }
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (ParserException e) {			
			e.printStackTrace();
		} catch (InvalidModelException e) {			
			e.printStackTrace();
		}
	    
	    return ontology;
	}
	
	
	
	
}
