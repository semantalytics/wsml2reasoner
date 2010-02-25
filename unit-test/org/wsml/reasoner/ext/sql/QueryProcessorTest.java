package org.wsml.reasoner.ext.sql;

import junit.framework.TestCase;

public class QueryProcessorTest extends TestCase {

	final String mOntology = "_\"http://www.some.ontology.org/o1#process\"";
	final String mWsmlQuery = "?pe [ generatedBy hasValue ?actor ] memberOf evo#successfulEvent";
	final String mTableName = "TEMP_TABLE";
	final String mSql = "SELECT actor, COUNT(pe) AS CountOfpe FROM " + mTableName + " WHERE actor LIKE 'baz%' GROUP BY actor HAVING COUNT ( pe ) > 0 ORDER BY COUNT ( pe ) LIMIT 10 OFFSET 170";
	
	final String mSqlLikeQuery =
  		"select ?actor LIKE baz%, count(?pe)" +
  		"				from " + mOntology +
  		"				where " + mWsmlQuery +
  		"				group by ?actor" +
  		"				having count(?pe) > 0" +
  		"				order by count(?pe )" +  		
  		"				limit 10" +
  		"				offset 170";
	
	QueryProcessor mQp;
	
	protected void setUp() throws Exception
	{
		mQp = new QueryProcessor( mSqlLikeQuery ); 
	}

	public void testQueryProcessor() throws Exception
	{
		// Throws if the constructor of QueryProcessor does.
	}
	
	public void testBadlyFormedMissingByFromOrderBy()
	{
		try
		{
			new QueryProcessor( "select ?x from _\"zz\" WHERE ?x memberOf XXX ORDER ?x" );
			fail();
		}
		catch( QueryFormatException e )
		{
		}
	}

	public void testBadlyFormedMissingByFromGroupBy()
	{
		try
		{
			new QueryProcessor( "select ?x from _\"zz\" WHERE ?x memberOf XXX GROUP ?x" );
			fail();
		}
		catch( QueryFormatException e )
		{
		}
	}

	public void testGetOntologyIRI() throws Exception
	{
		assertEquals( mQp.getOntologyIRI(), mOntology );
	}
	
	public void testGetWsmlQuery()
	{
		assertEquals( mQp.getWsmlQuery(), mWsmlQuery );
	}

	public void testGetSqlSelect() throws Exception
	{
		String sql = mQp.constructSqlQueryWithColumnNamesSubstitutedForVariables( mTableName );

		assertEquals( sql, mSql );
	}
}
