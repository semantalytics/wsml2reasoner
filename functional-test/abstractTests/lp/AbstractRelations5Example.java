package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.omwg.ontology.Ontology;
import abstractTests.LP;

public abstract class AbstractRelations5Example extends TestCase implements LP {

	public void testQuery() throws Exception
	{
		Set<Ontology> ontologies = new HashSet<Ontology>();

		// This works when loading o2 before o1, but not the other way round. It should not matter. 
		Ontology o1 = OntologyHelper.loadOntology( "files/relations5_instance.wsml" );
		Ontology o2 = OntologyHelper.loadOntology( "files/relations5_sub.wsml" );
		Ontology o3 = OntologyHelper.loadOntology( "files/relations5_super.wsml" );
		ontologies.add( o1 );
		ontologies.add( o2 );
		ontologies.add( o3 );

		String queryString = "_\"http://www.sukesh.com/example/SubOntology#loves\"(?x,?y)";
    	Results r = new Results( "x", "y" );
    	
    	String NS = "http://sukesh.com/example/InstanceOntology#";

    	r.addBinding( Results.iri( NS + "Peter" ), Results.iri( NS + "Mary" ) );

//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( ontologies, queryString, getLPReasoner() ) ) );
//    	if(true)return;
		LPHelper.executeQueryAndCheckResults( ontologies, queryString, r.get(), getLPReasoner() );
	}
}