package org.wsml.reasoner.impl;

import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class WSMO4JManager {

    private WsmoFactory wsmoFactory;

    private LogicalExpressionFactory leFactory;

	private DataFactory xmlDataFactory;

	private DataFactory wsmlDataFactory;


    public WSMO4JManager() {
        this.wsmoFactory = FactoryImpl.getInstance().createWsmoFactory();
        this.wsmlDataFactory = FactoryImpl.getInstance().createWsmlDataFactory(wsmoFactory);
        this.xmlDataFactory = FactoryImpl.getInstance().createXmlDataFactory(wsmoFactory);
        this.leFactory = FactoryImpl.getInstance().createLogicalExpressionFactory(wsmoFactory, wsmlDataFactory, xmlDataFactory);
    }
    
    public WSMO4JManager(WsmoFactory wsmoFactory, LogicalExpressionFactory leFactory, DataFactory wsmlDataFactory, DataFactory xmlDataFactory) {
        this.wsmoFactory = wsmoFactory;
        this.leFactory = leFactory;
        this.wsmlDataFactory = wsmlDataFactory;
        this.xmlDataFactory = xmlDataFactory;
    }

    public WsmoFactory getWSMOFactory() {
        return wsmoFactory;
    }

    public LogicalExpressionFactory getLogicalExpressionFactory() {
        return leFactory;
    }

	public DataFactory getXmlDataFactory() {
		return xmlDataFactory;
	}

	public DataFactory getWsmlDataFactory() {
		return wsmlDataFactory;
	}

    
}
