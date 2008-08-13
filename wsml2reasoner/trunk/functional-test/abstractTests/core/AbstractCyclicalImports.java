package abstractTests.core;

import helper.CoreHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import org.omwg.ontology.Ontology;
import abstractTests.CoreTest;

public abstract class AbstractCyclicalImports extends TestCase implements CoreTest {
	
    private static final String ONTOLOGY_FILE1 = "files/CyclicalImports1.wsml";
    private static final String ONTOLOGY_FILE2 = "files/CyclicalImports2.wsml";
    
    private static final String ns1 = "http://here.comes.the.whistleman/CyclicalImports1#";
    private static final String ns2 = "http://here.comes.the.whistleman/CyclicalImports2#";

    // A reference for the imported ontology.
    private Ontology importedOntology;
    
    protected void setUp() throws Exception {
    	importedOntology = OntologyHelper.loadOntology( ONTOLOGY_FILE2 );
    }

    public void testCyclicalImports1() throws Exception {
        
        assertNotNull( importedOntology );
     
        Results r = new Results( "X", "Y" );
        
        r.addBinding( Results.iri( ns2 + "Cy2i1" ), Results.iri( ns1 + "Master" ) );
        r.addBinding( Results.iri( ns1 + "Cy1i1" ), Results.iri( ns2 + "Slave" ) );
        r.addBinding( Results.iri( ns1 + "RolandKirk" ), Results.iri( ns1 + "JazzMusician" ) );
        r.addBinding( Results.iri( ns1 + "JohnScofield" ), Results.iri( ns1 + "JazzMusician" ) );
        r.addBinding( Results.iri( ns2 + "KarlDenson" ), Results.iri( ns1 + "JazzMusician" ) );
        r.addBinding( Results.iri( ns2 + "JohnMedeski" ), Results.iri( ns1 + "JazzMusician" ) );

        CoreHelper.queryXMemberOfYAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE1 ), getReasoner(), r.get() );
    }
    
    public void testCyclicalImports2() throws Exception {
        Results r = new Results( "X" );
        
        r.addBinding( Results.iri( ns2 + "KarlDenson" ) );
        r.addBinding( Results.iri( ns2 + "JohnMedeski" ) );
        r.addBinding( Results.iri( ns1 + "RolandKirk" ) );
        r.addBinding( Results.iri( ns1 + "JohnScofield" ) );
    	
        assertNotNull( importedOntology );
        
        CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE1 ), getReasoner(), ns1 + "JazzMusician", r.get() );
    }
}
