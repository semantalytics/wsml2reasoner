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
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.TermVisitor;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.wsmo.common.IRI;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;

/**
 * <p>
 * Concrete Visitor class. For each visited term, its corresponding Java data
 * type is determined. The following it the original table ( from
 * org.omwg.ontology.DataValue):
 * </p>
 * 
 * <pre>
 *  _string         _string(&quot;any-character*&quot;)                           java.lang.String    
 *  _decimal        _decimal(&quot;'-'?numeric+.numeric+&quot;)                   java.math.BigDecimal    
 *  _integer        _integer(&quot;'-'?numeric+&quot;)                            java.math.BigInteger    
 *  _float          _float(&quot;see XML Schema document&quot;)                   java.lang.Float 
 *  _double         _double(&quot;see XML Schema document&quot;)                  java.lang.Double    
 *  _iri            _iri(&quot;iri-according-to-rfc3987&quot;)                    java.lang.String    
 *  _sqname         _sqname(&quot;iri-rfc3987&quot;, &quot;localname&quot;)                 java.lang.String[]  
 *  _boolean        _boolean(&quot;true-or-false&quot;)                           java.lang.Boolean   
 *  _duration       _duration(year, month, day, hour, minute, second)   java.lang.String    
 *  _dateTime       _dateTime(year, month, day, hour, minute, second, timezone-hour, timezone-minute)
 *                  _dateTime(year, month, day, hour, minute, second)   java.util.Calendar  
 *  _time           _time(hour, minute, second, timezone-hour, timezone-minute) 
 *                  _time(hour, minute, second)                         java.util.Calendar  
 *  _date           _date(year, month, day, timezone-hour, timezone-minute) 
 *                  _date(year, month, day)                             java.util.Calendar
 *  _gyearmonth     _gyearmonth(year, month)                            java.lang.Integer[] 
 *  _gyear          _gyear(year)    java.lang.Integer   
 *  _gmonthday      _gmonthday(month, day)                              java.lang.Integer[] 
 *  _gday           _gday(day)                                          java.lang.Integer   
 *  _gmonth         _gmonth(month)                                      java.lang.Integer   
 *  _hexbinary      _hexbinary(hexadecimal-encoding)                    java.lang.String    
 *  _base64binary   _base64binary(hexadecimal-encoding)                 java.lang.String    
 * </pre>
 * 
 * <p>
 * Note that for the Calendar types you one can for the mapping only rely on the
 * fields given by the corresponding wsml value, e.g. if you ask the Calendar
 * object for Calendar.hour that you got from a _wsml#date value, the API
 * provides no guarantee on the return value.
 * </p>
 * 
 * This implementation resolves arrays, so that the values can be conveniently
 * stored in a database and converts all the time/date related datatypes to a
 * Java Calendar. All numeric types are mapped to Java BigDecimals. The only
 * unsupported datatypes are _hexbinary and _base64binary for the moment.
 * 
 * Several methods of the Visitor interface are not supported because the
 * DataLog Reasoner is only supposed to return i)IRIs or ii) DataValues as
 * variable bindings.
 * 
 * @see org.omwg.ontology.ComplexDataValue
 * @see org.omwg.ontology.SimpleDataValue
 * @see org.omwg.ontology.DataValue
 * @see org.deri.wsmo4j.factory.DataFactory
 * 
 * @author Florian Fischer, florian.fischer@deri.at
 */
public class DatatypeVisitor implements TermVisitor {

    private Queue<Entry> derivedMappings = new LinkedList<Entry>();

    private Map<String, Class< ? >> simpleDataTypes = new HashMap<String, Class< ? >>();

    private Map<String, Class< ? >> complextDataTypes = new HashMap<String, Class< ? >>();

    /***************************************************************************
     * Constructs a new VisitorDatatype and sets up type mappings.
     */
    public DatatypeVisitor() {
        simpleDataTypes.put(WsmlDataType.WSML_STRING, String.class);
        simpleDataTypes.put(WsmlDataType.WSML_DECIMAL, BigDecimal.class);
        simpleDataTypes.put(WsmlDataType.WSML_INTEGER, BigDecimal.class);
        
//        TODO gigi: delete if they are gone for good
//        simpleDataTypes.put(WsmlDataType.WSML_IRI, String.class);

        complextDataTypes.put(WsmlDataType.WSML_FLOAT, BigDecimal.class);
        complextDataTypes.put(WsmlDataType.WSML_DOUBLE, BigDecimal.class);
        
//        TODO gigi: delete if they are gone for good
//        complextDataTypes.put(WsmlDataType.WSML_SQNAME, String.class);
        complextDataTypes.put(WsmlDataType.WSML_BOOLEAN, Boolean.class);

        complextDataTypes.put(WsmlDataType.WSML_DURATION, String.class);

        complextDataTypes.put(WsmlDataType.WSML_DATETIME, java.sql.Timestamp.class);
        complextDataTypes.put(WsmlDataType.WSML_TIME, java.sql.Timestamp.class);
        complextDataTypes.put(WsmlDataType.WSML_DATE, java.sql.Timestamp.class);

        complextDataTypes.put(WsmlDataType.WSML_GYEARMONTH, java.sql.Timestamp.class);
        complextDataTypes.put(WsmlDataType.WSML_GMONTHDAY, java.sql.Timestamp.class);
        complextDataTypes.put(WsmlDataType.WSML_GYEAR, java.sql.Timestamp.class);
        complextDataTypes.put(WsmlDataType.WSML_GDAY, java.sql.Timestamp.class);
        complextDataTypes.put(WsmlDataType.WSML_GMONTH, java.sql.Timestamp.class);
        
//        complextDataTypes.put(WsmlDataType.WSML_DATETIME, Calendar.class);
//        complextDataTypes.put(WsmlDataType.WSML_TIME, Calendar.class);
//        complextDataTypes.put(WsmlDataType.WSML_DATE, Calendar.class);
//
//        complextDataTypes.put(WsmlDataType.WSML_GYEARMONTH, Calendar.class);
//        complextDataTypes.put(WsmlDataType.WSML_GMONTHDAY, Calendar.class);
//        complextDataTypes.put(WsmlDataType.WSML_GYEAR, Calendar.class);
//        complextDataTypes.put(WsmlDataType.WSML_GDAY, Calendar.class);
//        complextDataTypes.put(WsmlDataType.WSML_GMONTH, Calendar.class);
    }

    /**
     * Returns the topmost Value/Type Pair. VisitorDatatype in this regard
     * behaves like a FIFO data structure.
     * 
     * @return a Pair of the term value and the derived java class.
     */
    public Entry getMapping() {
        return derivedMappings.remove();
    }

    public void visit(ComplexDataValue t) {
        assert t != null;

        String complextType = t.getType().getIdentifier().toString();
        Class< ? > javaTypeForWSMLType = complextDataTypes.get(complextType);

        if (javaTypeForWSMLType == null) {
            throw new IllegalArgumentException("Datatype " + complextType + " is not supported at the moment.");
        }

        Object value = null;
        // WsmlDataType.WSML_DATETIME, WsmlDataType.WSML_TIME,
        // WsmlDataType.WSML_DATE are already stored as Calendar
        if (complextType.equals(WsmlDataType.WSML_GYEARMONTH) || complextType.equals(WsmlDataType.WSML_GMONTHDAY) || complextType.equals(WsmlDataType.WSML_GYEAR) || complextType.equals(WsmlDataType.WSML_GDAY) || complextType.equals(WsmlDataType.WSML_GMONTH)) {

        	Calendar cal = convertTimeValue(t);        
            value = new java.sql.Timestamp(cal.getTimeInMillis());
        }
        else if(complextType.equals(WsmlDataType.WSML_DATE) || 
        		complextType.equals(WsmlDataType.WSML_DATETIME) || 
        		complextType.equals(WsmlDataType.WSML_TIME)) {
        	//these are actually already implemented as calendar in wsmo4j
        	Calendar cal = (Calendar)t.getValue();
        	value = new java.sql.Timestamp(cal.getTimeInMillis());
        }
//        TODO gigi: delete if they are gone for good
//        else if (complextType.equals(WsmlDataType.WSML_SQNAME)) {
//            // according to ComplexDataValueImpl SQNames are not supported,
//            // there seems to be some stale
//            // code in there.
//            throw new UnsupportedOperationException("SQNames not supported");
//        }
        else if (complextType.equals(WsmlDataType.WSML_FLOAT)) {
            Float f = (Float) t.getValue();
            value = new BigDecimal(Float.toString( f ));
        }
        else if (complextType.equals(WsmlDataType.WSML_DOUBLE)) {
            Double d = (Double) t.getValue();
            value = new BigDecimal( Double.toString( d ));
        }
        else {
            // otherwise the type matches
            value = t.getValue();
        }

        if (value == null) {
            throw new UnsupportedOperationException("No suitable conversion found for " + complextType);
        }

        Entry mapping = new Entry(value, javaTypeForWSMLType);
        derivedMappings.add(mapping);
    }

    public void visit(ConstructedTerm t) {
        throw new UnsupportedOperationException("ConstructedTerms not supported");
    }

    public void visit(IRI t) {
//    	TODO gigi: delete if they are gone for good
//        assert t != null;
//
//        Class< ? > javaTypeForWSMLType = simpleDataTypes.get(WsmlDataType.WSML_IRI);
//        derivedMappings.add(new Entry(t.toString(), javaTypeForWSMLType));
    }

    public void visit(NumberedAnonymousID t) {
        throw new UnsupportedOperationException("NumberedAnonymousIDs not supported");
    }

    public void visit(SimpleDataValue t) {
        assert t != null;

        String simpleValueType = t.getType().getIdentifier().toString();
        Class< ? > javaTypeForWSMLType = simpleDataTypes.get(simpleValueType);
        Entry mapping = null;

        // check if type is known
        if (javaTypeForWSMLType == null) {
            throw new IllegalArgumentException("Datatype " + simpleValueType + " is not supported at the moment.");
        }

        // perform necessary conversions
        if (simpleValueType.equals(WsmlDataType.WSML_INTEGER)) {
            mapping = new Entry(convertInteger(t), javaTypeForWSMLType);
        }
        else {
            mapping = new Entry(t.getValue(), javaTypeForWSMLType);
        }

        derivedMappings.add(mapping);
    }

    public void visit(UnnumberedAnonymousID t) {
        throw new UnsupportedOperationException("UnnumberedAnonymousIDs not supported");
    }

    public void visit(Variable t) {
        throw new UnsupportedOperationException("Variables not supported");
    }

    protected BigDecimal convertInteger(SimpleDataValue i) {
        assert i.getType().getIdentifier().toString().equals(WsmlDataType.WSML_INTEGER);

        return new BigDecimal((BigInteger) i.getValue());
    }

    /**
     * Does Conversion of WSML_GYEARMONTH, WSML_GMONTHDAY, WSML_GYEAR,
     * WSML_GDAY, WSML_GMONTH to Java Calendar objects.
     * 
     * @param t
     *            the Complex Time Value.
     * @return the resulting Calendar object.
     */
    protected Calendar convertTimeValue(ComplexDataValue t) {
        String complextType = t.getType().getIdentifier().toString();
        
        GregorianCalendar tempCal = new GregorianCalendar();
        tempCal.clear();
        tempCal.setGregorianChange(new Date(Long.MIN_VALUE));

        if (complextType.equals(WsmlDataType.WSML_GYEARMONTH)) {
            Integer[] ym = (Integer[]) t.getValue();
            tempCal.set(Calendar.YEAR, ym[0]);
            tempCal.set(Calendar.MONTH, ym[1] - 1); // moths start at 0 in a
            // Java Calendar
        }
        else if (complextType.equals(WsmlDataType.WSML_GMONTHDAY)) {
            Integer[] md = (Integer[]) t.getValue();
            tempCal.set(Calendar.MONTH, md[0] - 1);
            tempCal.set(Calendar.DAY_OF_MONTH, md[1]);
        }
        else if (complextType.equals(WsmlDataType.WSML_GYEAR)) {
            Integer y = (Integer) t.getValue();
            tempCal.set(Calendar.YEAR, y);
        }
        else if (complextType.equals(WsmlDataType.WSML_GDAY)) {
            Integer d = (Integer) t.getValue();
            tempCal.set(Calendar.DAY_OF_MONTH, d);
        }
        else if (complextType.equals(WsmlDataType.WSML_GMONTH)) {
            Integer y = (Integer) t.getValue();
            tempCal.set(Calendar.MONTH, y - 1);
        }

        return tempCal;
    }

}