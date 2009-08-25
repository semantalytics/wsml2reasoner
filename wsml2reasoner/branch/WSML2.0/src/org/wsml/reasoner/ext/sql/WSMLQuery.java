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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.api.LPReasoner;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * The main entry point to process a WSML-Flight-A query. Only this class needs
 * to be instantiated to use the query syntax extension.
 * 
 * @author Florian Fischer, florian.fischer@deri.at
 */
public class WSMLQuery {

    public final static Logger logger = initLogger();

    /**
     * This constructs a query object that works on an allready existing and 
     * preloaded reasoner. Note that in this case ontologies are not loaded on
     * demand based on a certain query and that the FROM part in a query can
     * in fact even be ommited. The query in this case will simply work on whatever
     * ontologies had been registered with the reasoner before.
     * 
     * @param reasoner The preloaded reasoner.
     */
    public WSMLQuery(LPReasoner reasoner) {
    	SimpleLayout layout = new SimpleLayout();
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
        logger.addAppender(consoleAppender);
        // ALL, DEBUG, INFO, WARN, ERROR, FATAL, OFF
        logger.setLevel(Level.ERROR);
        
        preLoadedWithReasoner = true;
        reasonerFacade = new WSMLReasonerFacade(reasoner);
    }
    
    /**
     * Constructs an initial WSMLQuery object and sets up logging.
     */
    public WSMLQuery() {
        SimpleLayout layout = new SimpleLayout();
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
        logger.addAppender(consoleAppender);
        // ALL, DEBUG, INFO, WARN, ERROR, FATAL, OFF
        logger.setLevel(Level.ERROR);
        
        reasonerFacade = new WSMLReasonerFacade();
    }

    /**
     * Executes a WSML-Flight-A query.
     * 
     * @param query
     *            The query in string format.
     * @return The result.
     * @throws Exception 
     */
    public Set<Map<Variable, Term>> executeQuery(String query) throws Exception {
        if (query == null) {
            throw new IllegalArgumentException();
        }
        
        mSelectExpressions.clear();

        Set<Map<Variable, Term>> finalResult = new HashSet<Map<Variable, Term>>();
        ReasonerResult reasonerResult;

        QueryProcessor qp = new QueryProcessor(query, preLoadedWithReasoner);
        String wsmlQuery = qp.getWsmlQuery();
        mSelectExpressions .addAll( qp.getSelectExpressions() );
        
        if(!preLoadedWithReasoner) {
        	String ontologyIRI = qp.getOntologyIRI();
            // strip formating
            ontologyIRI = ontologyIRI.substring(2, ontologyIRI.length() - 1);
            reasonerResult = reasonerFacade.executeWsmlQuery(wsmlQuery, ontologyIRI);
            onto = reasonerFacade.getOntology();
        }
        else {
        	reasonerResult = reasonerFacade.executeWsmlQuery(wsmlQuery);
        }
                
        if( reasonerResult.size() > 0 )
        {
        	finalResult = proccessOnDB(reasonerResult, qp);
        }
        
        return finalResult;       
    }
    
    public List<String> getSelectExpressions() {
    	return mSelectExpressions;
    }
    
    private Set<Map<Variable, Term>> proccessOnDB(ReasonerResult result, QueryProcessor qp) {
    	assert result != null;
    	assert qp != null;
    	
    	 Set<Map<Variable, Term>> queryResult = new HashSet<Map<Variable, Term>>();
    	 
    	 try {
             dbmanager.openConnection();
             String tableName = dbmanager.storeReasonerResult(new WSMLResultProcessor().process(result));
             ResultSet sqlResult = dbmanager.executeQuery(qp.constructSqlQueryWithColumnNamesSubstitutedForVariables(tableName));

             queryResult = convertSQLResult(sqlResult);
             dbmanager.dropTable(tableName);
             dbmanager.closeConnection();
         }
         catch (SQLException e) {
             logger.error(e.getMessage());
             e.printStackTrace();
         }
    	 
    	 return queryResult;
    }

    private Set<Map<Variable, Term>> convertSQLResult(ResultSet sqlResult) throws SQLException {
        assert sqlResult != null;

        WsmoFactory wsmoFactory = FactoryImpl.getInstance().createWsmoFactory();
		DataFactory wsmlDataFactory = FactoryImpl.getInstance().createWsmlDataFactory(wsmoFactory);
		DataFactory xmlDataFactory = FactoryImpl.getInstance().createXmlDataFactory(wsmoFactory);
        LogicalExpressionFactory l = FactoryImpl.getInstance().createLogicalExpressionFactory(wsmoFactory, wsmlDataFactory, xmlDataFactory);
        ResultSetMetaData m = sqlResult.getMetaData();
        int columns = m.getColumnCount();

        Set<Map<Variable, Term>> result = new HashSet<Map<Variable, Term>>();

        while (sqlResult.next()) {
            Map<Variable, Term> row = new HashMap<Variable, Term>();
            for (int i = 1; i <= columns; i++) {
                String name = m.getColumnLabel(i);
                Variable v = l.createVariable(name);

                String className = m.getColumnClassName(i);
                Object entry = sqlResult.getObject(i);
                // figure out object type and convert to wsmo4j implementation
                if (className.equals(String.class.getCanonicalName())) {
                    SimpleDataValue dv = wsmlDataFactory.createString((String) entry);
                    row.put(v, dv);
                }
                else if (className.equals(BigDecimal.class.getCanonicalName())) {
                    SimpleDataValue dv = wsmlDataFactory.createDecimal((BigDecimal) entry);
                    row.put(v, dv);
                }
                else if (className.equals(Boolean.class.getCanonicalName())) {
                    ComplexDataValue dv = wsmlDataFactory.createBoolean((Boolean) entry);
                    row.put(v, dv);
                }
                //it is here where additional type information could be needed.
                else if(className.equals(java.sql.Timestamp.class.getCanonicalName()))
                {
                	java.sql.Timestamp time = (java.sql.Timestamp) entry;
                	Calendar cal = Calendar.getInstance();
                	cal.clear();
                	cal.setTimeInMillis(time.getTime());
                	ComplexDataValue dv = wsmlDataFactory.createDateTime(cal);
                	row.put(v, dv);               	
                }
                // this is mainly needed because COUNT returns integers
                else if (className.equals(java.lang.Integer.class.getCanonicalName())) {
                    Integer in = (java.lang.Integer) entry;
                    SimpleDataValue dv = wsmlDataFactory.createInteger(in.toString());
                    row.put(v, dv);
                }
                else {

                    throw new UnsupportedOperationException("Conversion from " + className + " to WSML datavalue is not supported");
                }
            } // all columns for current row done
            result.add(row); // add converted row
        }

        return result;
    }

    /**
     * Once a query was executed this method gives the possibility to fetch the
     * ontology used in the query answering. This is convenient for debugging,
     * resolving namespaces in the result, ...
     * 
     * @return The ontology used to answer a query.
     */
    public Ontology getOntology() {
        return onto;
    }

    private static Logger initLogger() {
        return Logger.getRootLogger();
    }

    /* For backward compatibility it is still possible to use one single registered ontology for query answering */
    private Ontology onto;
    
    boolean preLoadedWithReasoner = false;
     
    private final WSMLReasonerFacade reasonerFacade;

    private final DatabaseManager dbmanager = new DatabaseManager();
    
    private final List<String> mSelectExpressions = new ArrayList<String>();
}