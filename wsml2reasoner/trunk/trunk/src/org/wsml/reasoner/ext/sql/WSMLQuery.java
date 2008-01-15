/**
 * Copyright (C) 2007 Digital Enterprise Research Institute (DERI), 
 * Leopold-Franzens-Universitaet Innsbruck, Technikerstrasse 21a, 
 * A-6020 Innsbruck. Austria.
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

package org.wsml.reasoner.ext.sql;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.wsml.ParserException;

/**
 * The main entry point to process a WSML-Flight-A query.
 * Only this class needs to be instantiated to use the query syntax extension.
 * 
 * @author Florian Fischer, florian.fischer@deri.at
 */
public class WSMLQuery
{

	public final static Logger logger = initLogger();

	/**
	 * Constructs an initial WSMLQuery object and sets up logging.
	 */
	public WSMLQuery()
	{
		SimpleLayout layout = new SimpleLayout();
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.addAppender(consoleAppender);
		// ALL, DEBUG, INFO, WARN, ERROR, FATAL, OFF
		logger.setLevel(Level.ERROR);
	}

	/**
	 * Executes a WSML-Flight-A query.
	 * 
	 * @param query The query in string format.
	 * @return The result.
	 */
	public Set<Map<Variable, Term>> executeQuery(String query)
	{
		if (query == null)
		{
			throw new IllegalArgumentException();
		}

		Set<Map<Variable, Term>> queryResult = null;

		try
		{
			QueryProcessor qp = new QueryProcessor(query);
			String ontologyIRI = qp.getOntologyIRI();
			//strip formating
			ontologyIRI = ontologyIRI.substring(2, ontologyIRI.length()-1);
			String wsmlQuery = qp.getWsmlQuery();			
		
			ReasonerResult result = reasonerFacade.executeWsmlQuery(wsmlQuery,
					ontologyIRI);		
			onto = reasonerFacade.getOntology();
			
			try
			{
				dbmanager.openConnection();
				String tableName = dbmanager.storeReasonerResult( new WSMLResultProcessor().process(result) );
				ResultSet sqlResult = dbmanager
						.executeQuery(qp
								.constructSqlQueryWithColumnNamesSubstitutedForVariables(tableName));
				
				queryResult = convertSQLResult(sqlResult);
				dbmanager.dropTable(tableName);
				dbmanager.closeConnection();
			}
			catch (SQLException e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			}

		}
		catch (QueryFormatException e)
		{
			logger.error(e.toString());		
		}
		catch (ParserException e)
		{
			logger.error(e.toString());		
		}
		catch (IOException e)
		{
			logger.error(e.toString());		
		}
		catch (InvalidModelException e)
		{
			logger.error(e.toString());		
		}
		catch (InconsistencyException e)
		{
			logger.error(e.toString());		
		}

		return queryResult;
	}
	
	private Set<Map<Variable, Term>> convertSQLResult(ResultSet sqlResult) throws SQLException
	{
		assert sqlResult != null;
		
		DataFactory f = Factory.createDataFactory(null);
		LogicalExpressionFactory l = Factory.createLogicalExpressionFactory(null);		
		ResultSetMetaData m = sqlResult.getMetaData();
		int columns = m.getColumnCount();
		
		Set<Map<Variable, Term>> result = new HashSet<Map<Variable,Term>>();
			
		while(sqlResult.next())
		{
			Map<Variable, Term> row = new HashMap<Variable,Term>();
			for (int i = 1; i <= columns; i++)
			{				
				String name = m.getColumnLabel(i);
				Variable v = l.createVariable(name);
				
				String className = m.getColumnClassName(i);				
				Object entry = sqlResult.getObject(i);
				//figure out object type and convert to wsmo4j implementation
				if(className.equals(String.class.getCanonicalName()))
				{
					SimpleDataValue dv = f.createWsmlString((String)entry);
					row.put(v, dv);
				}
				else if(className.equals(BigDecimal.class.getCanonicalName()))
				{
					SimpleDataValue dv = f.createWsmlDecimal((BigDecimal) entry);
					row.put(v, dv);
				}
				else if(className.equals(Boolean.class.getCanonicalName()))
				{
					ComplexDataValue dv = f.createWsmlBoolean((Boolean)entry);
					row.put(v, dv);
				}
				else if(className.equals(java.sql.Date.class.getCanonicalName()))
				{
					java.sql.Date date = (java.sql.Date) entry;
					Calendar cal = Calendar.getInstance();
					cal.clear();
					cal.setTimeInMillis(date.getTime());
					ComplexDataValue dv = f.createWsmlDateTime(cal);
					row.put(v, dv);
				}
				//this is mainly needed because COUNT returns integers
				else if(className.equals(java.lang.Integer.class.getCanonicalName()))
				{
					Integer in = (java.lang.Integer) entry;
					SimpleDataValue dv = f.createWsmlInteger(in.toString());
					row.put(v, dv);
				}
				else
				{
				
					throw new UnsupportedOperationException("Conversion from " + className 
							+ " to WSML datavalue is not supported");
				}				
			} //all columns for current row done
			result.add(row); //add converted row
		}
		
		return result;
	}
	
	/**
	 * Once a query was executed this method gives the possibility to fetch the ontology
	 * used in the query answering. This is convenient for debugging, resolving namespaces in the result, ...
	 * 
	 * @return The ontology used to answer a query.
	 */
	public Ontology getOntology()
	{
		return onto;
	}

	private static Logger initLogger()
	{
		return Logger.getRootLogger();
	}

	private Ontology onto;
	private final WSMLReasonerFacade reasonerFacade = new WSMLReasonerFacade();
	private final DatabaseManager dbmanager = new DatabaseManager();
}