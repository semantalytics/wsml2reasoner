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
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a mapping from Java classes to java.sql.Types and
 * furthermore to String representations in order to simplify the dynamic
 * construction of SQL statements for variable Java types.
 * 
 * More details can be found in: "JDBC API Tutorial and Reference, Second
 * Edition: Universal Data Access for the Java 2 Platform"
 */
public class DefaultTypeMapping {
    public DefaultTypeMapping() {
        typeMap.put(String.class, Types.VARCHAR);
        typeMap.put(Integer.class, Types.INTEGER);
        typeMap.put(Long.class, Types.BIGINT);
        typeMap.put(BigDecimal.class, Types.DECIMAL);
        typeMap.put(Boolean.class, Types.BIT);
        typeMap.put(Float.class, Types.FLOAT);
        typeMap.put(Double.class, Types.DOUBLE);
        
        typeMap.put(java.sql.Date.class, Types.DATE);
        typeMap.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        typeMap.put(java.sql.Time.class, Types.TIME);
        typeMap.put(Calendar.class, Types.TIMESTAMP);

        // for convenience all the common sql types are mapped here
        syntaxMap.put(java.sql.Types.BIT, "BIT");
        syntaxMap.put(java.sql.Types.TINYINT, "TINYINT");
        syntaxMap.put(java.sql.Types.SMALLINT, "SMALLINT");
        syntaxMap.put(java.sql.Types.INTEGER, "INTEGER");
        syntaxMap.put(java.sql.Types.BIGINT, "BIGINT");
        syntaxMap.put(java.sql.Types.FLOAT, "FLOAT");
        syntaxMap.put(java.sql.Types.REAL, "REAL");
        syntaxMap.put(java.sql.Types.DOUBLE, "DOUBLE");
        syntaxMap.put(java.sql.Types.NUMERIC, "NUMERIC");
        syntaxMap.put(java.sql.Types.DECIMAL, "DECIMAL");
        syntaxMap.put(java.sql.Types.CHAR, "CHAR");
        syntaxMap.put(java.sql.Types.VARCHAR, "VARCHAR(255)");
        syntaxMap.put(java.sql.Types.LONGVARCHAR, "LONGVARCHAR");
        syntaxMap.put(java.sql.Types.BINARY, "BINARY");
        syntaxMap.put(java.sql.Types.VARBINARY, "VARBINARY");
        syntaxMap.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
        syntaxMap.put(java.sql.Types.NULL, "NULL");
        syntaxMap.put(java.sql.Types.OTHER, "OTHER");
        syntaxMap.put(java.sql.Types.CLOB, "CLOB");
        
        syntaxMap.put(java.sql.Types.TIME, "TIME");
        syntaxMap.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
        syntaxMap.put(java.sql.Types.DATE, "DATE");
        

    }

    /**
     * Gets the SQL string representation for a Java type.
     * 
     * @param javaType
     *            The Java type.
     * @return A string representation mapping to the datatype which can be used
     *         in SQL statements (e.g.: INT, VARCHAR, ..).
     */
    public String getTypeString(Class< ? > javaType) {
        int typeNumber = getType(javaType);
        String typeString = syntaxMap.get(typeNumber);

        if (typeString == null) {
            throw new IllegalArgumentException("No Syntax for type " + javaType.getName() + " found.");
        }

        return typeString;
    }

    /**
     * Get the respective SQL type (as defined in
     * 
     * @see java.sql.Types) for a certain JAVA type.
     * 
     * @param javaType
     *            The Java type.
     * @return A value corresponding to one of java.sql.Types.
     */
    public int getType(Class< ? > javaType) {
        Integer typeNumber = typeMap.get(javaType);

        if (typeNumber == null) {
            throw new IllegalArgumentException("The mappoing of " + javaType.getName() + " is not supported.");
        }

        return typeNumber;
    }

    private Map<Class< ? >, Integer> typeMap = new HashMap<Class< ? >, Integer>();

    private Map<Integer, String> syntaxMap = new HashMap<Integer, String>();
}
