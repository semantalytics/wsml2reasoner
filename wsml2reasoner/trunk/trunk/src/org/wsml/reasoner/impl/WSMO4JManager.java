package org.wsml.reasoner.impl;

import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class WSMO4JManager {

    private WsmoFactory wsmoFactory;

    private LogicalExpressionFactory leFactory;

    private DataFactory dataFactory;
    
    public WSMO4JManager() {
        this(Factory.createWsmoFactory(null), Factory
                .createLogicalExpressionFactory(null), Factory
                .createDataFactory(null));
    }

    public WSMO4JManager(WsmoFactory wsmoFactory,
            LogicalExpressionFactory leFactory, DataFactory dataFactory) {
        this.wsmoFactory = wsmoFactory;
        this.leFactory = leFactory;
        this.dataFactory = dataFactory;
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
