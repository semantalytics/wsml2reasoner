/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.deri.wsml.reasoner.wsmlcore.wrapper.dlv;

import java.util.logging.*;

import org.deri.wsml.reasoner.wsmlcore.datalog.DataTypeValue.DataType;
import org.deri.wsml.reasoner.wsmlcore.wrapper.SymbolFactory;
import org.deri.wsml.reasoner.wsmlcore.wrapper.UnsupportedFeatureException;

/**
 * SymbolFactory for the DLV system.
 * 
 * Prototypical implementation only.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class DLVSymbolFactory implements SymbolFactory {

    private long varCnt = 0;
    private long constCnt = 0;
    private long predCnt = 0;
    
    private final String VAR_PREFIX = "Var_";
    private final String CONST_PREFIX = "c_";
    private final String PRED_PREFIX = "p_";
   
    /** Some logger object for logging messages */
    private Logger logger = Logger.getLogger("org.deri.wsml.reasoner.wsmlcore.wrapper");
    //{ // init logger with a respective logger;
    //   logger.addHandler(new ConsoleHandler());
    //}
    
    
    public String getValidVariableName(String wsmlName) {
        return (VAR_PREFIX + (++varCnt));
    }

    public String getValidConstantName(String wsmlName) {
//        int i = wsmlName.indexOf('#');
//        String s = null;
//        if (i != -1 && i < s.length()) {
//            s = wsmlName.substring(i+1).toLowerCase();// non-alpahabet characters will cause problems
//        } else {
//            s = wsmlName;
//        }
        String s = null;
        return (CONST_PREFIX + (++constCnt) + ( s != null ? "_" + s : "" ));
    }

    public String getValidPredicateName(String wsmlName, int arity) throws UnsupportedFeatureException {
        
        if (!wsmlName.startsWith(org.omwg.logexpression.Constants.WSML_NAMESPACE)){
            // Predicate is not built-in but application specific
            
            String result;
            
            if (wsmlName.equals(DLVFacade.RESULT_PREDICATE_NAME)) {
                result = wsmlName; // do not change anything with the generated result predicate
            } else {
//                int i = wsmlName.indexOf('#');
//                String s = null;
//                if (i != -1 && i < s.length()) {
//                    s = wsmlName.substring(i+1).toLowerCase(); // non-alpahabet characters will cause problems
//                } else {
//                    s = wsmlName;
//                }
                String s = null;
                result = (PRED_PREFIX + (++predCnt) + ( s != null ? "_" + s : "" ) + "_" + arity); 
            }
           
            return result;
            
        } else {
            
            logger.severe("WSML Built-In Predicates need to be implemented in DLV Symbol Factory");
            throw new UnsupportedFeatureException("Unsupported Predicate: '" + wsmlName + "' of arity " + arity);
        }
       
    }

    public String getValidDataValue(String wsmlName, DataType dType) throws UnsupportedFeatureException {
        String result = null;
        if (dType == DataType.INTEGER) {
            result = wsmlName;
           
            logger.info("Handle WSML data value in DLV SymbolFactory: " + wsmlName);
            
        } else {
            // No other datatypes are supported by DLV at present.
            throw new UnsupportedFeatureException("Unsupported Datatype: Datavalue '" + wsmlName + "' of datatype " + dType.toString());
        }
        
        return result;
    }

}
