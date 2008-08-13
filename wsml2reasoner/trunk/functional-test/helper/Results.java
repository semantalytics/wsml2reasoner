package helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class Results
{
	public Results( String...variableNames )
	{
		int count = variableNames.length;
		
		mVariables = new Variable[ count ];
		
		for( int v = 0; v < mVariables.length; ++v )
			mVariables[ v ] = leFactory.createVariable( variableNames[ v ] );
	}
	
	public static Term bool( boolean b )
	{
		return dataFactory.createWsmlBoolean( b );
	}
	
	public static IRI iri( String value )
	{
		return wsmoFactory.createIRI( value );
	}
	
	public static ConstructedTerm fn( String functionSymbolIri, Term...terms )
	{
		List<Term> termList = Arrays.asList( terms );
		
		return leFactory.createConstructedTerm( wsmoFactory.createIRI( functionSymbolIri ), termList );
	}
	
	public void addBinding( Term...terms )
	{
		if( mVariables.length != terms.length )
			throw new IllegalArgumentException( "The number of terms per binding was not equal to the number of variables." );
		
		Map<Variable, Term> binding = new HashMap<Variable, Term>();
		
		for( int v = 0; v < mVariables.length; ++v )
			binding.put( mVariables[ v ], terms[ v ] );
		
		mResults.add( binding );
	}
	
	public Set<Map<Variable, Term>> get()
	{
		return mResults;
	}
	
	private final Variable[] mVariables;
	
	private final Set<Map<Variable, Term>> mResults = new HashSet<Map<Variable, Term>>();

    private static final WsmoFactory wsmoFactory;
    private static final LogicalExpressionFactory leFactory;
    private static final DataFactory dataFactory;
    private static final WSMO4JManager wsmoManager;
    
    static{
// 	 Set up factories for creating WSML elements
	   	wsmoManager = new WSMO4JManager();
	
	   	leFactory = wsmoManager.getLogicalExpressionFactory();
	   	wsmoFactory = wsmoManager.getWSMOFactory();
	   	dataFactory = wsmoManager.getDataFactory();
   }
}
