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
package org.wsml.reasoner.builtin.tptp;

import java.util.Set;

import org.apache.log4j.Logger;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.FOLAbstractFacade;
import org.wsmo.factory.Factory;

/**
 * <p>
 * The wsmo4j interface to and from TPTP
 * </p>
 * <p>
 * $Id: TPTPFacade.java,v 1.5 2007-08-10 09:44:49 graham Exp $
 * </p>
 * 
 * @author Holger Lausen
 * @version $Revision: 1.5 $
 */
public class TPTPFacade extends FOLAbstractFacade {

    Logger log = Logger.getLogger(TPTPFacade.class);

    public TPTPFacade(Factory factory, String syntax) {
        super(factory, syntax);
    }

    @Override
    public String getConjecture(LogicalExpression le, TPTPSymbolMap map) {
        TPTPLESerializeVisitor les = new TPTPLESerializeVisitor();
        les.setSymbolMap(map);
        le.accept(les);
        String conjectureString = les.getSerializedObject();
        return "fof(id" + id++ + ",conjecture, (" + conjectureString + ")). \n";
    }

    int id;

    public void register(Set<LogicalExpression> expressions) throws ExternalToolException {
        String kb = "";
        TPTPLESerializeVisitor les = new TPTPLESerializeVisitor();

        for (LogicalExpression le : expressions) {
            le.accept(les);
            String newExpression = les.getSerializedObject();
            newExpression = "fof(id" + id++ + ",axiom, (" + newExpression + ")). \n";
            kb += newExpression;
        }
        log.debug(kb);
        convertedOntology = kb;
        symbolMap = les.getSymbolMap();
    }
}
