package org.wsml.reasoner.builtin.kaon2;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.Constants;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.WsmlDataType;
import org.semanticweb.kaon2.id;
import org.semanticweb.kaon2.ld;
import org.semanticweb.kaon2.api.DefaultOntologyResolver;
import org.semanticweb.kaon2.api.KAON2Connection;
import org.semanticweb.kaon2.api.KAON2Exception;
import org.semanticweb.kaon2.api.KAON2Factory;
import org.semanticweb.kaon2.api.KAON2Manager;
import org.semanticweb.kaon2.api.Ontology;
import org.semanticweb.kaon2.api.OntologyChangeEvent;
import org.semanticweb.kaon2.api.logic.Literal;
import org.semanticweb.kaon2.api.logic.Predicate;
import org.semanticweb.kaon2.api.logic.Rule;
import org.semanticweb.kaon2.api.logic.Term;
import org.semanticweb.kaon2.api.logic.Variable;
import org.semanticweb.kaon2.api.owl.elements.Individual;
import org.semanticweb.kaon2.api.reasoner.Query;
import org.semanticweb.kaon2.api.reasoner.Reasoner;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DatalogReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.UnsupportedFeatureException;
import org.wsml.reasoner.WSML2DatalogTransformer;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 */
public class Kaon2LPWrapperImplementation implements DatalogReasonerFacade
{
	private final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>();

	private org.wsml.reasoner.ConjunctiveQuery query;

	private KAON2Factory f = KAON2Manager.factory();

	private KAON2Connection conn = null;

	private DataFactory df;

	private WsmoFactory wf;

	private LogicalExpressionFactory lef;

	private String ontologyUri;

	public Kaon2LPWrapperImplementation( Factory wsmoManager, final Map<String, Object> config )
	{
		df = wsmoManager.getWsmlDataFactory();
		wf = wsmoManager.getWsmoFactory();
		lef = wsmoManager.getLogicalExpressionFactory();
	}

	/**
	 * Needed for testing purposes
	 */
	public KAON2Connection getKaon2Connection()
	{
		return conn;
	}

	/* (non-Javadoc)
     * @see org.wsml.reasoner.builtin.kaon2.I#evaluate(org.wsml.reasoner.ConjunctiveQuery)
     */
	public Set<Map<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term>> evaluate( ConjunctiveQuery q) throws ExternalToolException
	{

		Set<Map<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term>> result = new HashSet<Map<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term>>();
		this.query = q;

		// Derive and store the sequence of variables that defines the
		// output
		// tuples from the query
		List<org.omwg.ontology.Variable> bodyVars = q.getVariables();
		List<String> varNames = new ArrayList<String>( bodyVars.size() );
		for( org.omwg.ontology.Variable v : bodyVars )
		{
			varNames.add( v.getName() );
		}

		try
		{
			if( this.conn == null )
			{
				throw new ExternalToolException( "No ontology is registered" );
			}
			Ontology ontology = this.conn.openOntology( ontologyUri, EMPTY_MAP );
			Reasoner reasoner = ontology.createReasoner();
			Query query = translateQuery( q, reasoner, varNames );
			query.open();
			while( !query.afterLast() )
			{
				org.omwg.logicalexpression.terms.Term[] tuple = convertQueryTuple( query.tupleBuffer() );
				Map<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term> newVarBinding = new HashMap<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term>();

				for( int j = 0; j < varNames.size(); j++ )
				{
					newVarBinding.put( lef.createVariable( varNames.get( j ) ), tuple[ j ] );
				}

				result.add( newVarBinding );
				query.next();
			}
			query.close();
			query.dispose();
			reasoner.dispose();
		}
		catch( KAON2Exception e )
		{
			throw new ExternalToolException( query, e );
		}
		catch( InterruptedException e )
		{
			throw new ExternalToolException( "Kaon2 query was interrupted during execution" );
		}

		return result;
	}

	/**
	 * Translate a knowledgebase
	 * 
	 * @param p - the datalog program that constitutes the knowledgebase
	 */
	private Set<Rule> translateKnowledgebase( Set<org.wsml.reasoner.Rule> p ) throws ExternalToolException
	{

		if( p == null )
		{
			return null;
		}

		Set<Rule> result = new HashSet<Rule>();

		for( org.wsml.reasoner.Rule r : p )
		{
			result.add( translateRule( r ) );
		}
		// Add some static rules to infer membership statements for datatype
		// predicates
		Predicate mO = f.predicateSymbol( WSML2DatalogTransformer.PRED_MEMBER_OF, 2 );
		Predicate hasValue = f.predicateSymbol( WSML2DatalogTransformer.PRED_HAS_VALUE, 3 );
		Literal hV = f.literal( true, hasValue, f.variable( "x" ), f.variable( "a" ), f.variable( "v" ) );
		// wsml-member-of(?x,wsml#string) :- isstring(?x)
		Literal head = f.literal( true, mO, f.variable( "v" ), f.individual( WsmlDataType.WSML_STRING ) );
		Literal typeCheck = f.literal( true, f.ifTrue( 2 ), f.constant( "isstring($1)" ), f.variable( "v" ) );
		result.add( f.rule( head, new Literal[] { hV, typeCheck } ) );
		// wsml-member-of(?x,wsml#integer) :- isbiginteger(?x)
		head = f.literal( true, mO, f.variable( "v" ), f.individual( WsmlDataType.WSML_INTEGER ) );
		typeCheck = f.literal( true, f.ifTrue( 2 ), f.constant( "isbiginteger($1)" ), f.variable( "v" ) );
		result.add( f.rule( head, new Literal[] { hV, typeCheck } ) );
		// wsml-member-of(?x,wsml#decimal) :- isbigdecimal(?x)
		head = f.literal( true, mO, f.variable( "v" ), f.individual( WsmlDataType.WSML_DECIMAL ) );
		typeCheck = f.literal( true, f.ifTrue( 2 ), f.constant( "isbigdecimal($1)" ), f.variable( "v" ) );
		result.add( f.rule( head, new Literal[] { hV, typeCheck } ) );
		// wsml-member-of(?x,wsml#boolean) :- isboolean(?x)
		head = f.literal( true, mO, f.variable( "v" ), f.individual( WsmlDataType.WSML_BOOLEAN ) );
		typeCheck = f.literal( true, f.ifTrue( 2 ), f.constant( "isboolean($1)" ), f.variable( "v" ) );
		result.add( f.rule( head, new Literal[] { hV, typeCheck } ) );
		return result;
	}

	/**
	 * Translate the query
	 * 
	 * @param p - the datalog program that constitutes the knowledgebase
	 */
	private Query translateQuery( ConjunctiveQuery q, Reasoner reasoner, List<String> varNames )
	                throws ExternalToolException
	{

		List<Variable> distinguishedVars = new ArrayList<Variable>();

		for( String varName : varNames )
		{
			distinguishedVars.add( f.variable( varName ) );
		}

		// Translate the query:
		// Given: ?- query-expr(x1,...,xn)
		// Transform to: result(x1,...,xk) :- query-expr(x1,...,xn)
		// where x1,...,xk is the subsequence of x1,...,xn where multiple
		// occurrences of the
		// same variable have been removed.
		List<org.wsml.reasoner.Literal> body = q.getLiterals();
		List<Literal> queryLiterals = new ArrayList<Literal>();

		for( org.wsml.reasoner.Literal l : body )
		{
			queryLiterals.add( translateLiteral( l ) );
		}

		Query result = null;
		try
		{
			result = reasoner.createQuery( queryLiterals, distinguishedVars );
		}
		catch( KAON2Exception e )
		{
			throw new ExternalToolException( query, e );
		}
		return result;

	}

	/**
	 * Translate a datalog rule
	 */
	private Rule translateRule( org.wsml.reasoner.Rule r ) throws ExternalToolException
	{

		Literal head = null;

		if( !r.isConstraint() )
		{
			head = translateLiteral( r.getHead() );
		}

		List<Literal> body = new ArrayList<Literal>();

		// Care about body
		for( org.wsml.reasoner.Literal bl : r.getBody() )
		{
			body.add( translateLiteral( bl ) );
		}

		Rule rule;

		// Handle constraints
		if( head == null )
			rule = f.rule( new Literal[] {}, null, body.toArray( new Literal[ body.size() ] ) );
		else
			rule = f.rule( head, body );

		return rule;

	}

	private Literal translateLiteral( org.wsml.reasoner.Literal l ) throws ExternalToolException
	{
		if( l == null )
			return null;
		boolean isPositive = l.isPositive();
		try
		{
			String p = l.getPredicateUri();
			Predicate pred = null;
			List<Term> terms = new ArrayList<Term>();
			if( p.equals( Constants.EQUAL ) )
			{
				pred = f.equal();
				translateTerms( l, terms );
			}
			else if( p.equals( Constants.INEQUAL ) )
			{
				pred = f.equal();
				isPositive = false;
				translateTerms( l, terms );
			}
			else if( p.equals( Constants.LESS_THAN ) )
			{
				if( l.getTerms().length == 2 )
				{
					pred = f.ifTrue( 3 );
					terms.add( f.constant( "$1 < $2" ) );
					translateTerms( l, terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#lessThan should have exactly two arguments!" );
				}
			}
			else if( p.equals( Constants.LESS_EQUAL ) )
			{
				if( l.getTerms().length == 2 )
				{
					pred = f.ifTrue( 3 );
					terms.add( f.constant( "$1 <= $2" ) );
					translateTerms( l, terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#lessEqual should have exactly two arguments!" );
				}
			}
			else if( p.equals( Constants.GREATER_THAN ) )
			{
				if( l.getTerms().length == 2 )
				{
					pred = f.ifTrue( 3 );
					terms.add( f.constant( "$1 > $2" ) );
					translateTerms( l, terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#greaterThan should have exactly two arguments!" );
				}
			}
			else if( p.equals( Constants.GREATER_EQUAL ) )
			{
				if( l.getTerms().length == 2 )
				{
					pred = f.ifTrue( 3 );
					terms.add( f.constant( "$1 >= $2" ) );
					translateTerms( l, terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#greaterEqual should have exactly two arguments!" );
				}
			}
			else if( p.equals( Constants.NUMERIC_EQUAL ) )
			{
				if( l.getTerms().length == 2 )
				{
					pred = f.ifTrue( 3 );
					terms.add( f.constant( "$1 == $2" ) );
					translateTerms( l, terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#numericEqual should have exactly two arguments!" );
				}
			}
			else if( p.equals( Constants.NUMERIC_INEQUAL ) )
			{
				if( l.getTerms().length == 2 )
				{
					pred = f.ifTrue( 3 );
					terms.add( f.constant( "$1 != $2" ) );
					translateTerms( l, terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#numericInEqual should have exactly two arguments!" );
				}
			}
			else if( p.equals( Constants.STRING_EQUAL ) )
			{
				if( l.getTerms().length == 2 )
				{
					pred = f.ifTrue( 3 );
					terms.add( f.constant( "$1 == $2" ) );
					translateTerms( l, terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#stringEqual should have exactly two arguments!" );
				}
			}
			else if( p.equals( Constants.STRING_INEQUAL ) )
			{
				if( l.getTerms().length == 2 )
				{
					pred = f.ifTrue( 3 );
					terms.add( f.constant( "$1 != $2" ) );
					translateTerms( l, terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#lessThan should have exactly two arguments!" );
				}
			}
			else if( p.equals( Constants.NUMERIC_ADD ) )
			{
				if( l.getTerms().length == 3 )
				{
					pred = f.evaluate( 4 );
					terms.add( f.constant( "$1 + $2" ) );
					org.omwg.logicalexpression.terms.Term[] literalTerms = l.getTerms();
					translateTerm( literalTerms[ 1 ], terms );
					translateTerm( literalTerms[ 2 ], terms );
					translateTerm( literalTerms[ 0 ], terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#numericAdd should have exactly three arguments!" );
				}
			}
			else if( p.equals( Constants.NUMERIC_SUB ) )
			{
				if( l.getTerms().length == 3 )
				{
					pred = f.evaluate( 4 );
					terms.add( f.constant( "$1 - $2" ) );
					org.omwg.logicalexpression.terms.Term[] literalTerms = l.getTerms();
					translateTerm( literalTerms[ 1 ], terms );
					translateTerm( literalTerms[ 2 ], terms );
					translateTerm( literalTerms[ 0 ], terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#numericSubtract should have exactly three arguments!" );
				}
			}
			else if( p.equals( Constants.NUMERIC_MUL ) )
			{
				if( l.getTerms().length == 3 )
				{
					pred = f.evaluate( 4 );
					terms.add( f.constant( "$1 * $2" ) );
					org.omwg.logicalexpression.terms.Term[] literalTerms = l.getTerms();
					translateTerm( literalTerms[ 1 ], terms );
					translateTerm( literalTerms[ 2 ], terms );
					translateTerm( literalTerms[ 0 ], terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#numericAdd should have exactly three arguments!" );
				}
			}
			else if( p.equals( Constants.NUMERIC_DIV ) )
			{
				if( l.getTerms().length == 3 )
				{
					pred = f.evaluate( 4 );
					terms.add( f.constant( "$1 / $2" ) );
					org.omwg.logicalexpression.terms.Term[] literalTerms = l.getTerms();
					translateTerm( literalTerms[ 1 ], terms );
					translateTerm( literalTerms[ 2 ], terms );
					translateTerm( literalTerms[ 0 ], terms );
				}
				else
				{
					throw new ExternalToolException( "wsml#numericAdd should have exactly three arguments!" );
				}
			}
			else if( p.equals( WSML2DatalogTransformer.PRED_IMPLIES_TYPE ) )
			{
				// check whether the last argument is a datatype
				if( l.getTerms().length == 3 )
				{
					org.omwg.logicalexpression.terms.Term t = l.getTerms()[ 2 ];
					if( t instanceof IRI )
					{
						IRI rangeIri = (IRI) t;
						if( WsmlDataType.WSML_STRING.equals( rangeIri )
						                || WsmlDataType.WSML_INTEGER.equals( rangeIri.toString() )
						                || WsmlDataType.WSML_DECIMAL.equals( rangeIri.toString() )
						                || WsmlDataType.WSML_BOOLEAN.equals( rangeIri.toString() ) )
						{
							pred = f.predicateSymbol( WSML2DatalogTransformer.PRED_OF_TYPE, l.getTerms().length );
							translateTerms( l, terms );
							// System.out.println("Translated implies_type for
							// oftype for " + rangeIri);
						}
						else
						{
							pred = f.predicateSymbol( p, l.getTerms().length );
							translateTerms( l, terms );
						}
					}
					else
					{
						pred = f.predicateSymbol( p, l.getTerms().length );
						translateTerms( l, terms );
					}
				}
				else
				{
					throw new ExternalToolException( "wsml-implies-type should have exactly three arguments!" );
				}
			}
			else
			{
				pred = f.predicateSymbol( p, l.getTerms().length );
				translateTerms( l, terms );
			}

			return f.literal( isPositive, pred, terms );

		}
		catch( UnsupportedFeatureException ufe )
		{
			throw new ExternalToolException( query, ufe );
		}
	}

	private void translateTerms( org.wsml.reasoner.Literal literal, List<Term> terms )
	                throws UnsupportedFeatureException, ExternalToolException
	{
		org.omwg.logicalexpression.terms.Term[] args = literal.getTerms();
		for( org.omwg.logicalexpression.terms.Term arg : args )
		{
			translateTerm( arg, terms );

		}
	}

	private void translateTerm( org.omwg.logicalexpression.terms.Term term, List<Term> terms )
	                throws UnsupportedFeatureException, ExternalToolException
	{
		if( term instanceof org.omwg.ontology.Variable )
		{
			terms.add( f.variable( ((org.omwg.ontology.Variable) term).getName() ) );
		}
		else if( term instanceof IRI )
		{
			terms.add( f.individual( ((IRI) term).toString() ) );
		}
		else if( term instanceof DataValue )
		{
			DataValue dv = (DataValue) term;

			if( dv instanceof SimpleDataValue )
			{
				// It is BigInteger, BigDecimal or String which is
				// supported by KAON2
				Object value = dv.getValue();
				terms.add( f.constant( value ) );
			}
			else if( WsmlDataType.WSML_BOOLEAN.equals( dv.getType().getIdentifier().toString() ) )
			{
				terms.add( f.constant( dv.getValue() ) );
			}
			else
			{
				// No other datatypes are supported at present.
				throw new UnsupportedFeatureException( "Unsupported Datatype: Datavalue '" + dv.getValue()
				                + "' of datatype " + dv.getType() );
			}
		}
		else
		{
			throw new ExternalToolException( "Could not interpret WSML term, it is neither an IRI nor a data value: "
			                + term );
		}
	}

	private void appendRules( Ontology ontology, Set<Rule> rules ) throws KAON2Exception
	{
		List<OntologyChangeEvent> changes = new ArrayList<OntologyChangeEvent>();
		for( Rule rule : rules )
		{
			changes.add( new OntologyChangeEvent( rule, OntologyChangeEvent.ChangeType.ADD ) );
		}
		ontology.applyChanges( changes );
	}

	/**
	 * Converts a query tuple to a WSML Term array. For Kaon2 individuals
	 * extract the IRI, for other objects return the proper WSML Term
	 * 
	 * @param tupleBuffer
	 * @return an array of WSML Terms
	 */
	private org.omwg.logicalexpression.terms.Term[] convertQueryTuple( Object[] tupleBuffer )
	                throws ExternalToolException
	{
		org.omwg.logicalexpression.terms.Term[] result = new org.omwg.logicalexpression.terms.Term[ tupleBuffer.length ];
		for( int i = 0; i < tupleBuffer.length; i++ )
		{
			Object obj = tupleBuffer[ i ];
			if( obj instanceof id )
			{
				if( ((id) obj).getFunctionSymbol() instanceof Individual )
				{
					Individual inst = (Individual) ((id) obj).getFunctionSymbol();
					result[ i ] = wf.createIRI( inst.getURI() );
				}
				else if( ((id) obj).getFunctionSymbol() instanceof String )
				{
					result[ i ] = df.createString( (String) ((id) obj).getFunctionSymbol() );
				}
				else if( ((id) obj).getFunctionSymbol() instanceof BigInteger )
				{
					result[ i ] = df.createInteger( (BigInteger) ((id) obj).getFunctionSymbol() );
				}
				else if( ((id) obj).getFunctionSymbol() instanceof BigDecimal )
				{
					result[ i ] = df.createDecimal( (BigDecimal) ((id) obj).getFunctionSymbol() );
				}
				else if( ((id) obj).getFunctionSymbol() instanceof Boolean )
				{
					result[ i ] = df.createBoolean( (Boolean) ((id) obj).getFunctionSymbol() );
				}
			}
			else if( obj instanceof ld )
			{
				if( ((ld) obj).getFunctionSymbol() instanceof Individual )
				{
					Individual inst = (Individual) ((ld) obj).getFunctionSymbol();
					result[ i ] = wf.createIRI( inst.getURI() );
				}
				else if( ((ld) obj).getFunctionSymbol() instanceof String )
				{
					result[ i ] = df.createString( (String) ((ld) obj).getFunctionSymbol() );
				}
				else if( ((ld) obj).getFunctionSymbol() instanceof BigInteger )
				{
					result[ i ] = df.createInteger( (BigInteger) ((ld) obj).getFunctionSymbol() );
				}
				else if( ((ld) obj).getFunctionSymbol() instanceof BigDecimal )
				{
					result[ i ] = df.createDecimal( (BigDecimal) ((ld) obj).getFunctionSymbol() );
				}
				else if( ((ld) obj).getFunctionSymbol() instanceof Boolean )
				{
					result[ i ] = df.createBoolean( (Boolean) ((ld) obj).getFunctionSymbol() );
				}
			}
			else
			{
				throw new ExternalToolException( "Unknown object in the KAON2 ontology: " + obj.toString() );
			}

		}
		return result;
	}

	/* (non-Javadoc)
     * @see org.wsml.reasoner.builtin.kaon2.I#register(java.util.Set)
     */
	public void register( Set<org.wsml.reasoner.Rule> kb ) throws ExternalToolException
	{
		try
		{
			if( conn == null ){
				conn = KAON2Manager.newConnection();
				DefaultOntologyResolver resolver = new DefaultOntologyResolver();
				conn.setOntologyResolver( resolver );
			}
			deregister();
			
			ontologyUri = createUniqueURI().toString();
			
			File f = File.createTempFile( "kaon2", ".xml" );

			DefaultOntologyResolver resolver = (DefaultOntologyResolver) conn.getOntologyResolver();
			
			String fileUri = f.toURI().toString();
			resolver.registerReplacement( ontologyUri, fileUri);

			Ontology o = conn.createOntology( ontologyUri, EMPTY_MAP );
			Set<Rule> rules = translateKnowledgebase( kb );
			appendRules( o, rules );
		}
		catch( KAON2Exception e )
		{
			throw new ExternalToolException( "Cannot register ontology in KAON2", e );
		}
        catch( URISyntaxException e )
        {
        	throw new ExternalToolException( "Error creating unique ontology id", e );
        }
        catch( IOException e )
        {
        	throw new ExternalToolException( "Unable to create temporary file", e );
        }			
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		this.conn.close();
	}

	/* (non-Javadoc)
     * @see org.wsml.reasoner.builtin.kaon2.I#deregister()
     */
	public void deregister() throws ExternalToolException
	{
		try
		{
			if (ontologyUri != null){
				Ontology ontology = this.conn.openOntology( ontologyUri, EMPTY_MAP );
				this.conn.closeOntologies( Collections.singleton( ontology ) );
				ontologyUri = null;
			}
		}
		catch( KAON2Exception e )
		{
			throw new ExternalToolException( "Internal Kaon2 exception", e );
		}
		catch( InterruptedException e )
		{
			throw new ExternalToolException( "Kaon2 query was interrupted during execution" );
		}
	}

	/* (non-Javadoc)
     * @see org.wsml.reasoner.builtin.kaon2.I#checkQueryContainment(org.wsml.reasoner.ConjunctiveQuery, org.wsml.reasoner.ConjunctiveQuery)
     */
	public boolean checkQueryContainment( ConjunctiveQuery query1, ConjunctiveQuery query2 )
	{
		throw new UnsupportedOperationException( "This method is not implemented" );
	}

	/* (non-Javadoc)
     * @see org.wsml.reasoner.builtin.kaon2.I#getQueryContainment(org.wsml.reasoner.ConjunctiveQuery, org.wsml.reasoner.ConjunctiveQuery)
     */
	public Set<Map<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term>> getQueryContainment(
	                ConjunctiveQuery query1, ConjunctiveQuery query2 )
	{
		throw new UnsupportedOperationException( "This method is not implemented" );
	}

	private URI createUniqueURI() throws URISyntaxException
	{
		StringBuilder uriString = new StringBuilder();
		uriString.append( 'b' );
		byte[] bytes = new java.rmi.dgc.VMID().toString().getBytes();

		for( byte b : bytes )
		{
			uriString.append( Integer.toString( b, 16 ) );
		}
		return new URI( uriString.toString() );
	}
}
