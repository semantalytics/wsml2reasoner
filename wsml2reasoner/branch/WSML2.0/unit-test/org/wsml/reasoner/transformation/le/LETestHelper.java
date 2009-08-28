/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
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
package org.wsml.reasoner.transformation.le;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;


public class LETestHelper {

    public static LogicalExpression buildLE(String theString) throws ParserException{
    	WsmoFactory wsmoFactory = FactoryImpl.createNewInstance().getWsmoFactory();
		DataFactory wsmlDataFactory = FactoryImpl.createNewInstance().getWsmlDataFactory(wsmoFactory);
		DataFactory xmlDataFactory = FactoryImpl.createNewInstance().getXmlDataFactory(wsmoFactory);
		LogicalExpressionFactory leF = FactoryImpl.createNewInstance().getLogicalExpressionFactory(wsmoFactory, wsmlDataFactory, xmlDataFactory);
		return leF.createLogicalExpression(theString);		
    }
}
