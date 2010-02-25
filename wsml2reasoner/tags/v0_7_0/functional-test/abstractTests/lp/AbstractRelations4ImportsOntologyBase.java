package abstractTests.lp;

import helper.LPHelper;
import helper.Results;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Ontology;

import abstractTests.LP;

public abstract class AbstractRelations4ImportsOntologyBase extends TestCase implements LP {

	protected abstract void loadOntologies( Set<Ontology> ontologies ) throws Exception;
	
	public void testRelationPopulated() throws Exception
	{
		Set<Ontology> ontologies = new HashSet<Ontology>();

		loadOntologies( ontologies );
		
//		for( Ontology ontology : ontologies )
//			System.out.println( OntologyHelper.toString( ontology ) );
			
		String NS = "http://example.com/relations4b#";

		String query = "_\"http://example.com/relations4a#binary\"(?x,?y)";
    	Results r = new Results( "x", "y" );
    	r.addBinding( Results.iri( NS + "i1" ), Results.iri( NS + "i2" ) );
		
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( ontologies, query, getLPReasoner() ) ) );
		LPHelper.executeQueryAndCheckResults( ontologies, query, r.get(), getLPReasoner() );
	}
}
