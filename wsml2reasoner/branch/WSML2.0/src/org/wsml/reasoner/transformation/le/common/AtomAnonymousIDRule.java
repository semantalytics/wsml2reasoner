/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wsml.reasoner.transformation.le.common;

import java.util.ArrayList;
import java.util.List;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.common.Identifier;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;


public class AtomAnonymousIDRule implements NormalizationRule {

    private LogicalExpressionFactory leFactory;
    private AnonymousIdTranslator anonymousIDTranslator;
    
    public AtomAnonymousIDRule(FactoryContainer wsmoManager, AnonymousIdTranslator anonymousIDTranslator){
        this.leFactory = wsmoManager.getLogicalExpressionFactory();
        this.anonymousIDTranslator = anonymousIDTranslator;
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        Atom atom = (Atom) expression;
        Identifier id = (Identifier) anonymousIDTranslator.translate(atom.getIdentifier());
        List<Term> args = new ArrayList<Term>();
        for (int i = 0; i < atom.getArity(); i++) {
            Term term = atom.getParameter(i);
            if (term instanceof Identifier) {
                term = anonymousIDTranslator.translate(term);
            }
            args.add(term);
        }
        return leFactory.createAtom(id, args);
    }

    public boolean isApplicable(LogicalExpression expression) {
        return expression instanceof Atom;
    }
}