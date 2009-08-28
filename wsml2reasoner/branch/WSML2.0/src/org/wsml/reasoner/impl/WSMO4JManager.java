package org.wsml.reasoner.impl;

import org.deri.wsmo4j.io.parser.wsml.LogExprParserTypedImpl;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class WSMO4JManager {
	
	private Factory factory;

    public WSMO4JManager() {
    	this.factory = FactoryImpl.createNewInstance();
    }
    
    public WSMO4JManager(WsmoFactory wsmoFactory, LogicalExpressionFactory leFactory, DataFactory wsmlDataFactory, DataFactory xmlDataFactory) {
        throw new UnsupportedOperationException("Constructor not supported, use WSMO4JManager() instead");
    }

    public WsmoFactory getWSMOFactory() {
        return factory.getWsmoFactory();
    }

    public LogicalExpressionFactory getLogicalExpressionFactory() {
        return factory.getLogicalExpressionFactory(null, null, null);
    }

	public DataFactory getXmlDataFactory() {
		return factory.getXmlDataFactory(null);
	}

	public DataFactory getWsmlDataFactory() {
		return factory.getWsmlDataFactory(null);
	}

	public LogicalExpressionParser getLogicalExpressionParser() {
		return new LogExprParserTypedImpl(factory);
	}
    
	public LogicalExpressionParser getLogicalExpressionParser(TopEntity nsHolder) {
		return new LogExprParserTypedImpl(nsHolder, factory);
	}
    
}
