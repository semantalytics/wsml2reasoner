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

import java.util.ArrayList;

/**
 * This class is involved in several intermediate processing steps and serves as
 * an encapsulation of the reasoner result in a format that allows db style
 * operations.
 * 
 * @author Florian Fischer, florian.fischer@deri.at
 * 
 */
public class Table {

    /**
     * Once the column count has been determined (at least one row was stored),
     * this returns the column count.
     * 
     * @return The column count of the Table.
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Returns the number of individual rows in the Table.
     * 
     * @return The row count of the Table.
     */
    public int getRowCount() {
        return table.size();
    }

    /**
     * Each column is named. This method returns the name corresponding to a
     * certain index.
     * 
     * @param column
     *            The column index, starting at 0.
     * @return The name of the column.
     */
    public String getColumnName(int column) {
        if (columnNames == null) {
            return null;
        }
        else {
            return columnNames.get(column);
        }
    }

    /**
     * Each column shares a common type for all entries. This is a requirement
     * to store it in an SQL database. This method returns the type for a column
     * index.
     * 
     * @param column
     *            The column index, starting at 0.
     * @return The Java class type of the column.
     */
    public Class< ? > getColumnTypeName(int column) {
        if (columnTypes == null) {
            return null;
        }
        else {
            return columnTypes.get(column);
        }
    }

    /**
     * This method stores a row at a certain position in the table.
     * 
     * @param i
     *            The position at which to insert the row.
     * @param row
     *            The row to store.
     */
    public void storeRow(int i, ArrayList<Entry> row) {
        columnCount = Math.max(row.size(), columnCount);
        table.add(i, row);
    }

    /**
     * Returns a row from the table.
     * 
     * @param i
     *            The index of the row to return.
     * @return The result row.
     */
    public ArrayList<Entry> getRow(int i) {
        return table.get(i);
    }

    /**
     * This method promotes data-types of entries in a column wise fashion. This
     * means that for all the entries in a column it derives the most general
     * possible data-type and converts all the entries to this representation.
     */
    public void promoteDataTypes() {
        columnTypes = new ArrayList<Class< ? >>(columnCount);

        // we traverse the whole column to determine the final type
        for (int j = 0; j < columnCount; j++) {
            for (int i = 0; i < table.size(); i++) {
                Entry e = table.get(i).get(j);
                if (i == 0) // first row
                {
                    columnTypes.add(j, e.getClassMapping());
                }
                else // for every further row we need to determine the
                        // biggest type
                {
                    Class< ? > derived = doPromote(e.getClass(), columnTypes.get(j));
                    columnTypes.add(j, derived);
                }
            }
        }

        // traverse again to fix every entry
        doFixDataTypes();
    }

    /**
     * This method determines a common name for the columns.
     */
    public void determineColumnNames() {
        if (table.size() > 0) {
            ArrayList<Entry> firstRow = table.get(0);
            // for now we only get it from the first row
            columnNames = new ArrayList<String>();
            for (int i = 0; i < firstRow.size(); i++) {
                Entry e = firstRow.get(i);
                columnNames.add(i, e.getName());
            }
        }
        else {
            throw new UnsupportedOperationException("Determining column names " + "for an empty table is not possible.");
        }
    }

    private void doFixDataTypes() {
        for (int j = 0; j < columnCount; j++) {
            for (int i = 0; i < table.size(); i++) {
                Entry e = table.get(i).get(j);
                Class< ? > type = columnTypes.get(j);
                e.setClassMapping(type);
            }
        }
    }

    private Class< ? > doPromote(Class< ? > first, Class< ? > second) {
        assert first != null;
        assert second != null;

        if (first.equals(second)) {
            return first;
        }

        return String.class;
    }

    private int columnCount = 0;

    private ArrayList<ArrayList<Entry>> table = new ArrayList<ArrayList<Entry>>();

    private ArrayList<Class< ? >> columnTypes;

    private ArrayList<String> columnNames;
}
