/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	public static ComplexDataValue _duration( int sign, int year, int month, int day, int hour, int minute, double second)
	{
//		FIXME gigi: add duration support
		return dataFactory.createDuration(sign, year, month, day, hour, minute, second );
	}
	
	/**
	 * Create a datetime.
	 * @return The new wsml object.
	 */
	public static ComplexDataValue _datetime(int year, int month, int day, int hour, int minute, double second, int tzSign, int tzHour, int tzMinute)
	{
		return dataFactory.createDateTime( year, month, day, hour, minute, second, tzSign, tzHour, tzMinute );
	}
	
	/**
	 * Create a time.
	 * @return The new wsml object.
	 */
	public static ComplexDataValue _time(int hour, int minute, double second, int tzSign, int tzHour, int tzMinute)
	{
		return dataFactory.createTime( hour, minute, second, tzSign, tzHour, tzMinute );
	}
	
	/**
	 * Create a date.
	 * @return The new wsml object.
	 */
	public static ComplexDataValue _date(int year, int month, int day, int tzSign, int tzHour, int tzMinute)
	{
		return dataFactory.createDate( year, month, day, tzSign, tzHour, tzMinute );
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
	
	public static Term _text( String value, String lang )
	{
		return dataFactory.createPlainLiteral(value, lang);
	}
	
	public static Term _xmlliteral( String tag, String lang )
	{
		return dataFactory.createXMLLiteral(tag, lang);
	}
	
	public static Term _yearmonthduration(int sign, int year, int month)
	{
		return dataFactory.createYearMonthDuration(sign, year, month);
	}
	
	public static Term _daytimeduration(int sign, int day, int hour, int minute, double second)
	{
		return dataFactory.createDayTimeDuration(sign, day, hour, minute, second);
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
