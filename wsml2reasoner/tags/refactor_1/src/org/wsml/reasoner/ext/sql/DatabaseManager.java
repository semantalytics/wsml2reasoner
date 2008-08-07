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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

/**
 * This class encapsulates the in-memory-database for the rest of the query
 * extension.
 * 
 * @author Florian Fischer. florian.fischer@deri.at
 * 
 */
public class DatabaseManager {
    public static final String FRAMEWORK = "mem";

    public static final String DBNAME = "WQE";

    public static final String DRIVER = "org.hsqldb.jdbcDriver";

    public static final String PROTOCOL = "jdbc:hsqldb:mem:aname";

    public static final String USERNAME = "sa";

    public static final String PASSWORD = "";

    /**
     * Constructs a new DatabaseManager by loading the appropriate database
     * driver. No Connections are opened at this point.
     */
    public DatabaseManager() {
        startHSQLDB();
    }

    private void startHSQLDB() {
        try {
            Class.forName(DRIVER).newInstance();
        }
        catch (InstantiationException e) {
            WSMLQuery.logger.fatal(e.toString());
        }
        catch (IllegalAccessException e) {
            WSMLQuery.logger.fatal(e.toString());
        }
        catch (ClassNotFoundException e) {
            WSMLQuery.logger.fatal(e.toString());
        }
    }

    /**
     * Opens a connection to the underlying database (if necessary). If this
     * method is not called previously to any other database related operations
     * then a connection is opened automatically when needed.
     * 
     * @return An open database connection
     * @throws SQLException
     */
    public Connection openConnection() throws SQLException {
        if (conn == null) {
            conn = createHSQLDBConnection();
        }
        return conn;
    }

    private Connection createHSQLDBConnection() throws SQLException {
        Properties props = new Properties();
        props.put("user", USERNAME);
        props.put("password", PASSWORD);
        return DriverManager.getConnection(PROTOCOL + FRAMEWORK + DBNAME, props);
    }

    /**
     * Closes the underlying database connection. This also shuts down the
     * database of the underlying Derby engine.
     * 
     * @throws SQLException
     *             when the connection could not be closed
     */
    public void closeConnection() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Executes a query against the underlying database.
     * 
     * @param query
     *            The SQL query in string representation.
     * @return The SQL result set.
     * @throws SQLException
     */
    public ResultSet executeQuery(String query) throws SQLException {
        if (query == null) {
            throw new IllegalArgumentException("Cannot execute query for null query string.");
        }

        Connection con = openConnection();
        Statement s = con.createStatement();
        ResultSet result = s.executeQuery(query);
        return result;
    }

    /**
     * Stores a reasoner result encapsulated in a Table in the database. This is
     * a two step operation which first creates the necessary database table and
     * then inserts into it.
     * 
     * @param toStore
     *            The reasoner result to store.
     * @return The ID of the created table.
     * @throws SQLException
     */
    public String storeReasonerResult(Table toStore) throws SQLException {
        if (toStore == null) {
            throw new IllegalArgumentException("Cannot store null Table.");
        }

        String tableName = createTable(toStore);
        insertResult(toStore, tableName);
        return tableName;
    }

    /**
     * Drops a table.
     * 
     * @param tableName
     *            The name of the table.
     * @throws SQLException
     */
    public void dropTable(String tableName) throws SQLException {
        if (tableName == null || tableName.equals("")) // maybe more checks are
                                                        // needed
        {
            throw new IllegalArgumentException("Illegal table name as argument.");
        }

        Connection con = openConnection();
        Statement s = con.createStatement();
        s.setEscapeProcessing(true);
        String dropTableString = "DROP TABLE " + tableName;
        s.executeUpdate(dropTableString);
        s.close();
    }

    /**
     * 
     * @param toStore
     * @param tableName
     * @throws SQLException
     */
    protected void insertResult(Table toStore, String tableName) throws SQLException {
        assert toStore != null;
        assert tableName != null;

        StringBuilder insertSQL = new StringBuilder();
        insertSQL.append("INSERT INTO " + tableName + " VALUES (");

        int colCount = toStore.getColumnCount();
        for (int i = 0; i < colCount; i++) {
            insertSQL.append("?");
            // no colon for the last entry
            if (i != colCount - 1) {
                insertSQL.append(",");
            }
        }
        insertSQL.append(")"); // end of insert statement

        String inserString = insertSQL.toString();
        Connection con = openConnection();

        // row by row
        int rowCount = toStore.getRowCount();
        PreparedStatement stmt = con.prepareStatement(inserString);
        for (int j = 0; j < rowCount; j++) {
            ArrayList<Entry> row = toStore.getRow(j);
            assert colCount == row.size();

            for (int i = 0; i < colCount; i++) {
                Entry e = row.get(i);
                stmt.setObject(i + 1, e.getValue());
            }
            stmt.executeUpdate();
        }

    }

    /**
     * Creates a table according to the list of variables and given Java
     * classes, using the internal type-mapping. In this operation variable
     * names are converted to legal column names, by stripping question marks
     * from them.
     * 
     * @param columnJavaDataTypeMapping
     *            the list of variable, class pairs
     * @return the unique name of the created table
     * @throws SQLException
     */
    protected String createTable(Table table) throws SQLException {
        assert table != null;

        String tableID = createTableID();
        DefaultTypeMapping javaToDDL = new DefaultTypeMapping();

        StringBuilder tableSQL = new StringBuilder();
        tableSQL.append("CREATE TABLE " + tableID + " (\n");

        int colCount = table.getColumnCount();
        for (int i = 0; i < colCount; i++) {
            String columnName = table.getColumnName(i);
            tableSQL.append(columnName + " " + javaToDDL.getTypeString(table.getColumnTypeName(i)));

            if (i == colCount - 1) // last column
            {
                tableSQL.append("\n)");
            }
            else {
                tableSQL.append(",\n");
            }
        }

        Connection con = openConnection();
        Statement stmt = con.createStatement();
        String sqlString = tableSQL.toString();
        stmt.executeUpdate(sqlString);
        stmt.close();

        return tableID;
    }

    /**
     * Creates a unique/random table name.
     * 
     * @return The generated table name.
     */
    protected String createTableID() {
        long randomID = tableGen.nextLong();
        return "ResultSet" + (Long.toString(Math.abs(randomID)));
    }

    private Random tableGen = new Random(System.currentTimeMillis());

    private Connection conn;
}
