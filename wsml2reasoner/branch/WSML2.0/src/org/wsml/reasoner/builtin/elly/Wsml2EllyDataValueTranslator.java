package org.wsml.reasoner.builtin.elly;

import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import org.deri.iris.api.terms.ITerm;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.WsmlDataType;

/**
 * Helper Class to convert data values to terms, copied from Wsml2Datalog translation.
 *
 */
public class Wsml2EllyDataValueTranslator {

    /**
     * Converts a wsmo4j DataValue to an iris ITerm.
     * 
     * @param v the wsmo4j value to convert
     * @return the corresponding ITerm implementation
     */
    static ITerm convertWsmo4jDataValueToIrisTerm(final DataValue v) {
        if (v == null) {
            throw new NullPointerException("The data value must not be null");
        }
        final String t = v.getType().getIdentifier().toString();
        if (t.equals(WsmlDataType.WSML_BASE64BINARY)) {
            return CONCRETE.createBase64Binary(v.getValue().toString());
        }
        else if (t.equals(WsmlDataType.WSML_BOOLEAN)) {
            return CONCRETE.createBoolean(Boolean.valueOf(v.getValue().toString()));
        }
        else if (t.equals(WsmlDataType.WSML_DATE)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            int length = cv.getArity();
            return CONCRETE.createDate(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2),
            				length > 3 ? getIntFromValue(cv, 3) : 0,
            	            length > 4 ? getIntFromValue(cv, 4) : 0);
        }
        else if (t.equals(WsmlDataType.WSML_DATETIME)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            int length = cv.getArity();
            return CONCRETE.createDateTime(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2),
            				getIntFromValue(cv, 3), getIntFromValue(cv, 4), getDoubleFromValue(cv, 5),
            				length > 6 ? getIntFromValue(cv, 6) : 0,
            				length > 7 ? getIntFromValue(cv, 7) : 0);
        }
        else if (t.equals(WsmlDataType.WSML_TIME)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            int length = cv.getArity();
            return CONCRETE.createTime(getIntFromValue(cv, 0), getIntFromValue(cv, 1), getDoubleFromValue(cv, 2),
            				length > 3 ? getIntFromValue(cv, 3) : 0,
							length > 4 ? getIntFromValue(cv, 4) : 0);
        }
        else if (t.equals(WsmlDataType.WSML_DECIMAL)) {
            return CONCRETE.createDecimal(Double.parseDouble(v.getValue().toString()));
        }
        else if (t.equals(WsmlDataType.WSML_DOUBLE)) {
            return CONCRETE.createDouble(Double.parseDouble(v.getValue().toString()));
        }
        else if (t.equals(WsmlDataType.WSML_DURATION)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createDuration( true, getIntFromValue(cv, 0), getIntFromValue(cv, 1), getIntFromValue(cv, 2),
            				getIntFromValue(cv, 3), getIntFromValue(cv, 4), getDoubleFromValue(cv, 5) );
        }
        else if (t.equals(WsmlDataType.WSML_FLOAT)) {
            return CONCRETE.createFloat(Float.parseFloat(v.getValue().toString()));
        }
        else if (t.equals(WsmlDataType.WSML_GDAY)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGDay(getIntFromValue(cv, 0));
        }
        else if (t.equals(WsmlDataType.WSML_GMONTH)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGMonth(getIntFromValue(cv, 0));
        }
        else if (t.equals(WsmlDataType.WSML_GMONTHDAY)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGMonthDay(getIntFromValue(cv, 0), getIntFromValue(cv, 1));
        }
        else if (t.equals(WsmlDataType.WSML_GYEAR)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGYear(getIntFromValue(cv, 0));
        }
        else if (t.equals(WsmlDataType.WSML_GYEARMONTH)) {
            final ComplexDataValue cv = (ComplexDataValue) v;
            return CONCRETE.createGYearMonth(getIntFromValue(cv, 0), getIntFromValue(cv, 1));
        }
        else if (t.equals(WsmlDataType.WSML_HEXBINARY)) {
            return CONCRETE.createHexBinary(v.getValue().toString());
        }
        else if (t.equals(WsmlDataType.WSML_INTEGER)) {
            return CONCRETE.createInteger(Integer.parseInt(v.toString()));
        }
        else if (t.equals(WsmlDataType.WSML_STRING)) {
            return TERM.createString(v.toString());
        }
        throw new IllegalArgumentException("Can't convert a value of type " + t);
    }

    /**
     * Returns the integer value of a ComplexDataValue at a given position.
     * 
     * @param value the complex data value from where to get the int
     * @param pos the index of the integer
     * @return the extracted and converted integer
     */
    private static int getIntFromValue(final ComplexDataValue value, int pos) {
        assert value != null;
        assert pos >= 0;

        return Integer.parseInt(getFieldValue(value, pos));
    }

    /**
     * Get a double value from the specified position.
     * @param value The complex data value from which the double is extracted.
     * @param pos The zero-basd index of the desired value.
     * @return
     */
    private static double getDoubleFromValue(final ComplexDataValue value, int pos) {
        assert value != null;
        assert pos >= 0;

        return Double.parseDouble(getFieldValue(value, pos));
    }

    /**
     * Get a field of a complex value.
     * 
     * @param value
     *            The complex value
     * @param pos
     *            The position of the file (zero-based index)
     * @return The string-ised field value.
     */
    private static String getFieldValue(final ComplexDataValue value, int pos) {
        assert value != null;
        assert pos >= 0;

        return value.getArgumentValue((byte) pos).getValue().toString();
    }
}
