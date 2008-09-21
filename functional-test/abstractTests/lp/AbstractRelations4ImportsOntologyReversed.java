package abstractTests.lp;

import helper.OntologyHelper;
import java.util.Set;
import org.omwg.ontology.Ontology;

public abstract class AbstractRelations4ImportsOntologyReversed extends AbstractRelations4ImportsOntologyBase
{
	protected void loadOntologies( Set<Ontology> ontologies ) throws Exception
	{
		// The order should not matter, but it does at the moment.
		// Loading/parsing 4a and then 4b works.
		// Loading/parsing 4b and then 4a does not work.
		Ontology o1 = OntologyHelper.loadOntology( "files/relations4_relation_instances.wsml" );
		Ontology o2 = OntologyHelper.loadOntology( "files/relations4_relation_definitions.wsml" );
		ontologies.add( o1 );
		ontologies.add( o2 );
	}	
}
