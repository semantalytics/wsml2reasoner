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

/**
 * This class represents a single entry in a reasoner result table and is used
 * to simplify several individual processing steps. An entry simply consists of
 * a generic Java object as a value, the name of the entry (the column name, the
 * variable name, ...) and the Java class type of the entry.
 * 
 * @author Florian Fischer, florian.fischer@deri.at
 */
public class Entry {
    private Object value;

    private String name;

    private Class< ? > classMapping;

    public Entry(Object value, String name, Class< ? > mapping) {
        this.value = value;
        this.name = name;
        this.classMapping = mapping;
    }

    public Entry(Object value, Class< ? > mapping) {
        this.value = value;
        this.classMapping = mapping;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class< ? > getClassMapping() {
        return classMapping;
    }

    public void setClassMapping(Class< ? > mapping) {
        // the only upward promotion now
        if (mapping.equals(String.class)) {
            value = value.toString();
            classMapping = mapping;
        }
        // otherwise it ought to be the same class
        // this catches bugs where illegal mappings like
        // calendar -> bigdecimal
        if (!mapping.equals(classMapping)) {
            throw new IllegalArgumentException("Type promotion from " + classMapping + " to " + mapping + " is not legal.");
        }
    }

}