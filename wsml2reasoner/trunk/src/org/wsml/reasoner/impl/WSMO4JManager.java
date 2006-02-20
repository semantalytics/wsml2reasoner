package org.wsml.reasoner.impl;

import java.util.Map;

import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public abstract class WSMO4JManager {

    private static WsmoFactory wsmoFactory = null;

    private static LogicalExpressionFactory leFactory = null;

    private static DataFactory dataFactory = null;

    public static WsmoFactory getWSMOFactory() {
        if (wsmoFactory == null) {
            wsmoFactory = Factory.createWsmoFactory(null);
        }
        return wsmoFactory;

    }

    public static LogicalExpressionFactory getLogicalExpressionFactory() {
        if (leFactory == null) {
            leFactory = (org.wsmo.factory.LogicalExpressionFactory) Factory
                    .createLogicalExpressionFactory(null);
        }
        return leFactory;
    }

    public static DataFactory getDataFactory() {
        if (dataFactory == null) {
            dataFactory = (DataFactory) Factory.createDataFactory(null);
        }
        return dataFactory;
    }
    
    /*
     * Quick fix to allow both a WSMO Factory
     * to be created with desired properties
    */
    public static WsmoFactory createWSMOFactory(Map properties) {
    	wsmoFactory = Factory.createWsmoFactory(properties);
        return wsmoFactory;
    }

}
