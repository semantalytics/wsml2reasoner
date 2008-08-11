package org.wsml.reasoner.builtin.kaon2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class Kaon2FacadeTest extends TestCase
{

	private WsmoFactory wsmoFactory;

	private LogicalExpressionFactory leFactory;

	private LPReasoner wsmlReasoner;

	BuiltInReasoner previous;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		leFactory = wsmoManager.getLogicalExpressionFactory();
		wsmoFactory = wsmoManager.getWSMOFactory();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		System.gc();
	}

	public void ontologyRegistration() throws Exception
	{
		// get A reasoner
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.KAON2 );
		wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createFlightReasoner( params );

		Ontology o1 = wsmoFactory.createOntology( wsmoFactory.createIRI( "urn:test1" ) );
		Atom a1 = leFactory.createAtom( wsmoFactory.createIRI( "urn:test:a1" ), new ArrayList<Term>() );
		Axiom ax1 = wsmoFactory.createAxiom( wsmoFactory.createIRI( "urn:test:ax1" ) );
		ax1.addDefinition( a1 );
		o1.addAxiom( ax1 );

		try
		{
			executeQuery( "_\"urn:test:xxx\"()", o1 );
			fail();
		}
		catch( InternalReasonerException expected )
		{
		}

		wsmlReasoner.registerOntology( o1 );
		assertEquals( 1, executeQuery( "_\"urn:test:a1\"()", o1 ).size() );
		wsmlReasoner.deRegister();

		Ontology o2 = wsmoFactory.createOntology( wsmoFactory.createIRI( "urn:test2" ) );
		Atom a2 = leFactory.createAtom( wsmoFactory.createIRI( "urn:test:a2" ), new ArrayList<Term>() );
		Axiom ax2 = wsmoFactory.createAxiom( wsmoFactory.createIRI( "urn:test:ax2" ) );
		ax2.addDefinition( a2 );
		o2.addAxiom( ax2 );

		try
		{
			executeQuery( "_\"urn:test:a2\"()", o2 );
			fail();
		}
		catch( InternalReasonerException expected )
		{

		}
		
		Set <Ontology> ontologies = new HashSet<Ontology>();
		ontologies.add(o1);
		ontologies.add(o2);
		
		wsmlReasoner.registerOntologies( ontologies );
		assertEquals( 1, executeQuery( "_\"urn:test:a1\"()", o1 ).size() );
		assertEquals( 0, executeQuery( "_\"urn:test:a11\"()", o1 ).size() );
		assertEquals( 1, executeQuery( "_\"urn:test:a2\"()", o2 ).size() );

		Atom a11 = leFactory.createAtom( wsmoFactory.createIRI( "urn:test:a11" ), new ArrayList<Term>() );
		Axiom ax11 = wsmoFactory.createAxiom( wsmoFactory.createIRI( "urn:test:ax11" ) );
		ax11.addDefinition( a11 );
		o1.addAxiom( ax11 );

		wsmlReasoner.registerOntologies( ontologies );
		assertEquals( 1, executeQuery( "_\"urn:test:a1\"()", o1 ).size() );
		assertEquals( 1, executeQuery( "_\"urn:test:a11\"()", o1 ).size() );
		assertEquals( 1, executeQuery( "_\"urn:test:a2\"()", o2 ).size() );

		wsmlReasoner.deRegister();
		try
		{
			executeQuery( "_\"urn:test:a2\"()", o2 );
			fail();
		}
		catch( InternalReasonerException expected )
		{

		}
		
		wsmlReasoner.registerOntology(o1);
		assertEquals( 1, executeQuery( "_\"urn:test:a1\"()", o1 ).size() );
		assertEquals( 1, executeQuery( "_\"urn:test:a11\"()", o1 ).size() );

		wsmlReasoner.deRegister();
		try
		{
			executeQuery( "_\"urn:test:a1\"()", o2 );
			fail();
		}
		catch( InternalReasonerException expected )
		{

		}

	}

	private Set<Map<Variable, Term>> executeQuery( String query, Ontology o ) throws Exception
	{
		LogicalExpression qExpression = leFactory.createLogicalExpression( query, o );
		return wsmlReasoner.executeQuery( qExpression );
	}

	public void testFlightReasoners() throws Exception
	{
		ontologyRegistration();
	}

}
