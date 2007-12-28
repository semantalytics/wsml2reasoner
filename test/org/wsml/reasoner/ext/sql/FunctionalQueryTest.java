package org.wsml.reasoner.ext.sql;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.wsml.reasoner.ext.sql.WSMLQuery;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

public class FunctionalQueryTest extends TestCase
{

	private final File lotrOntology = new File("test/files/lordOfTheRings.wsml");
	private final File simpsonsOntology = new File(
			"test/files/simpsons.wsml");
	private URL ontologyIRI1;
	private URL ontologyIRI2;

	protected void setUp() throws Exception
	{
		super.setUp();
		ontologyIRI1 = lotrOntology.toURI().toURL();
		ontologyIRI2 = simpsonsOntology.toURI().toURL();
	}

	public void testBasic()
	{
		String query = "SELECT ?x " + "FROM " + "_\"" + ontologyIRI1
				+ "\"" + " WHERE ?x memberOf ?y";
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);

		assertEquals(true, r != null);
	}

	public void testGroupBy()
	{
		String query = "SELECT ?x, COUNT(*) " + "FROM " + "_\"" + ontologyIRI1
				+ "\"" + " WHERE ?x memberOf ?y " + "GROUP BY ?x";
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);

		assertEquals(true, r != null);
	}

	public void testHavingCount()
	{
		String query = "SELECT ?place, COUNT(?place) FROM _\"" + ontologyIRI2
				+ "\"" + " WHERE ?employee[hasWorkingPlace hasValue ?place] "
				+"GROUP BY ?place"
				+ " HAVING COUNT(?place) > 4 ";
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);

		assertEquals(true, r != null);
	}

	public void testOrderBy()
	{
		String query = "SELECT ?place, ?employee FROM _\"" + ontologyIRI2 
				+ "\"" + " WHERE ?employee[hasWorkingPlace hasValue ?place] "
				+ " ORDER BY 1, 2";
		
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);

		assertEquals(true, r != null);
				
	}
	
	public void testComplex()
	{
		String query = "SELECT ?place, COUNT(?place) FROM _\"" + ontologyIRI2
				+ "\"" + " WHERE ?employee[hasWorkingPlace hasValue ?place] "
				+ " GROUP BY ?place " + " HAVING COUNT(?place) > 4 "
				+ " ORDER BY ?place DESC "
				+ " LIMIT 2";
		WSMLQuery wqe = new WSMLQuery();
		Set<Map<Variable, Term>> r = wqe.executeQuery(query);
		assertEquals(true, r != null);		
	}
}
