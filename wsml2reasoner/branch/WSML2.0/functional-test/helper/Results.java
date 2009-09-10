/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * Helper to build a result set from a query.
 * This class is used in tests that need to compare the expected query result with
 * the actual result. 
 */
public class Results
{
	/**
	 * Create the result set.
	 * @param variableNames One instance for each variable in each 'row' of the result.
	 */
	public Results( String...variableNames)
	{
		int count = variableNames.length;
		
		mVariables = new Variable[ count ];
		
		for( int v = 0; v < mVariables.length; ++v )
			mVariables[ v ] = leFactory.createVariable( variableNames[ v ] );
	}

	public static Term _string( String value )
	{
		return dataFactory.createString( value );
	}
	
	public static Term _decimal( double value )
	{
		return dataFactory.createDecimal( new BigDecimal( Double.toString( value ) ) );
	}
	
	public static Term _integer( int value )
	{
		return dataFactory.createInteger( new BigInteger( Integer.toString( value ) ) );
	}
	
	public static Term _float( float value )
	{
		return dataFactory.createFloat( value );
	}
	
	public static Term _double( double value )
	{
		return dataFactory.createDouble( value );
	}

	/**
	 * Create a wsml boolean term. 
	 * @param b The value of the term.
	 * @return The wsml boolean term object.
	 */
	public static Term _bool( boolean b )
	{
		return dataFactory.createBoolean( b );
	}
	
	/**
	 * Create a duration.
	 * @return The new wsml object.
	 */
	public static ComplexDataValue _duration( int year, int month, int day, int hour, int minute, double second)
	{
//		FIXME gigi: add duration support
		return dataFactory.createDuration(year, month, day, hour, minute, second );
	}
	
	/**
	 * Create a datetime.
	 * @return The new wsml object.
	 */
	public static ComplexDataValue _datetime(int year, int month, int day, int hour, int minute, double second, int tzHour, int tzMinute)
	{
		return dataFactory.createDateTime( year, month, day, hour, minute, (float) second, tzHour, tzMinute ); // TODO gigi: introduced the float cast, check if this makes sense
	}
	
	/**
	 * Create a time.
	 * @return The new wsml object.
	 */
	public static ComplexDataValue _time(int hour, int minute, double second, int tzHour, int tzMinute)
	{
		return dataFactory.createTime( hour, minute, (float) second, tzHour, tzMinute ); // TODO gigi: introduced the float cast, check if this makes sense
	}
	
	/**
	 * Create a date.
	 * @return The new wsml object.
	 */
	public static ComplexDataValue _date(int year, int month, int day, int tzHour, int tzMinute)
	{
		return dataFactory.createDate( year, month, day, tzHour, tzMinute );
	}
	
	public static Term _yearMonth( int year, int month )
	{
		return dataFactory.createGregorianYearMonth( year, month );
	}

	public static Term _year( int year )
	{
		return dataFactory.createGregorianYear( year );
	}

	public static Term _monthDay( int month, int day )
	{
		return dataFactory.createGregorianMonthDay( month, day );
	}

	public static Term _day( int day )
	{
		return dataFactory.createGregorianDay( day );
	}

	public static Term _month( int month )
	{
		return dataFactory.createGregorianMonth( month );
	}

	public static Term _hexBinary( String value )
	{
		return dataFactory.createHexBinary( value.getBytes() );
	}

	public static Term _base64Binary( String value )
	{
		return dataFactory.createBase64Binary( value.getBytes() );
	}

	/**
	 * Create an IRI.
	 * @param value The value of the IRI.
	 * @return The IRI object.
	 */
	public static IRI iri( String value )
	{
		return wsmoFactory.createIRI( value );
	}
	
	/**
	 * Create a constructed term object.
	 * @param functionSymbolIri The IRI identifier of the constructed term.
	 * @param terms The list of term values.
	 * @return The new term object.
	 */
	public static ConstructedTerm fn( String functionSymbolIri, Term...terms )
	{
		List<Term> termList = Arrays.asList( terms );
		
		return leFactory.createConstructedTerm( wsmoFactory.createIRI( functionSymbolIri ), termList );
	}
	
	/**
	 * Add a list of terms to a binding (row of result).
	 * Each term is bound to the corresponding variable as provided to the constructor and
	 * are bound in the same order. 
	 * @param terms The terms to be bound to variables.
	 */
	public void addBinding( Term...terms )
	{
		if( mVariables.length != terms.length )
			throw new IllegalArgumentException( "The number of terms per binding was not equal to the number of variables." );
		
		Map<Variable, Term> binding = new HashMap<Variable, Term>();
		
		for( int v = 0; v < mVariables.length; ++v )
			binding.put( mVariables[ v ], terms[ v ] );
		
		mResults.add( binding );
	}
	
	/**
	 * Get the result set proper.
	 * @return The set of maps of variable-term bindings.
	 */
	public Set<Map<Variable, Term>> get()
	{
		return mResults;
	}
	
	private final Variable[] mVariables;
	
	private final Set<Map<Variable, Term>> mResults = new HashSet<Map<Variable, Term>>();

    private static final WsmoFactory wsmoFactory;
    private static final LogicalExpressionFactory leFactory;
    private static final DataFactory dataFactory;
    private static final FactoryContainer wsmoManager;
    
    static{
    	// 	 Set up factories for creating WSML elements
	   	wsmoManager = new WsmlFactoryContainer();
	   	leFactory = wsmoManager.getLogicalExpressionFactory();
	   	wsmoFactory = wsmoManager.getWsmoFactory();
	   	dataFactory = wsmoManager.getXmlDataFactory();
   }
}
