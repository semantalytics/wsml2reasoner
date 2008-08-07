/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package org.wsml.reasoner.transformation.le;

import java.util.Collection;
import java.util.Iterator;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.LogicalExpressionFactory;

public class FixedModificationRules {

    protected static final byte CONJUNCTION = 0;
    protected static final byte DISJUNCTION = 1;

    protected LogicalExpressionFactory leFactory;

    public FixedModificationRules(WSMO4JManager wsmoManager) {
        leFactory = wsmoManager.getLogicalExpressionFactory();
    }

    public Conjunction buildNaryConjunction(Collection< ? extends LogicalExpression> expressions) {
        return (Conjunction) buildNary(CONJUNCTION, expressions);
    }

    public Disjunction buildNaryDisjunction(Collection< ? extends LogicalExpression> expressions) {
        return (Disjunction) buildNary(DISJUNCTION, expressions);
    }

    protected LogicalExpression buildNary(byte operationCode, Collection< ? extends LogicalExpression> expressions) {
        LogicalExpression nary = null;
        Iterator< ? extends LogicalExpression> leIterator = expressions.iterator();
        if (leIterator.hasNext()) {
            nary = leIterator.next();
        }
        while (leIterator.hasNext()) {
            switch (operationCode) {
            case CONJUNCTION:
                nary = leFactory.createConjunction(nary, leIterator.next());
                break;

            case DISJUNCTION:
                nary = leFactory.createDisjunction(nary, leIterator.next());
                break;
            }
        }
        return nary;
    }
}