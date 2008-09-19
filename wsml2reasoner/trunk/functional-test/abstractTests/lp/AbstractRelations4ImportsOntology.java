package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.omwg.ontology.Ontology;
import abstractTests.LP;

public abstract class AbstractRelations4ImportsOntology extends TestCase implements LP {

	public void testImportedRelation() throws Exception
	{
		Set<Ontology> ontologies = new HashSet<Ontology>();
		
		// The order should not matter, but it does at the moment.
		// Loading/parsing 4a and then 4b works.
		// Loading/parsing 4b and then 4a does not work.
		Ontology o1 = OntologyHelper.loadOntology( "files/relations4b_relation_instances.wsml" );
		Ontology o2 = OntologyHelper.loadOntology( "files/relations4a_relation_definitions.wsml" );
		ontologies.add( o1 );
		ontologies.add( o2 );

//		System.out.println( OntologyHelper.toString( o1 ) );
//		System.out.println( OntologyHelper.toString( o2 ) );

		String NS = "http://example.com/relations4b#";

		String query = "_\"http://example.com/relations4a#binary\"(?x,?y)";
    	Results r = new Results( "x", "y" );
    	r.addBinding( r.iri( NS + "i1" ), r.iri( NS + "i2" ) );
		
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( ontologies, query, getLPReasoner() ) ) );
		LPHelper.executeQueryAndCheckResults( ontologies, query, r.get(), getLPReasoner() );
	}
}
