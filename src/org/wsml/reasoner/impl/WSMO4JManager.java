package org.wsml.reasoner.impl;

import java.util.Hashtable;
import java.util.Map;

import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class WSMO4JManager {

    private WsmoFactory wsmoFactory;

    private LogicalExpressionFactory leFactory;

    private DataFactory dataFactory;
    
    private Map DBConf;

    public WSMO4JManager() {
        this(Factory.createWsmoFactory(null), Factory
                .createLogicalExpressionFactory(null), Factory
                .createDataFactory(null),new Hashtable());
    }

    public WSMO4JManager(WsmoFactory wsmoFactory,
            LogicalExpressionFactory leFactory, DataFactory dataFactory, Map dbConf2) {
        this.wsmoFactory = wsmoFactory;
        this.leFactory = leFactory;
        this.dataFactory = dataFactory;
        this.DBConf = dbConf2;
    }
    
    public Map getDBConf(){
    	return DBConf;
    }

    public WsmoFactory getWSMOFactory() {
        return wsmoFactory;
    }

    public LogicalExpressionFactory getLogicalExpressionFactory() {
        return leFactory;
    }

    public DataFactory getDataFactory() {
        return dataFactory;
    }

}
