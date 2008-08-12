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

package org.wsml.reasoner.transformation.le.moleculedecomposition;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.Molecule;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LEUtil;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.LogicalExpressionFactory;

public class MoleculeDecompositionRule implements NormalizationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public MoleculeDecompositionRule(WSMO4JManager wsmoManager){
        this.leFactory = wsmoManager.getLogicalExpressionFactory();
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        return LEUtil.buildNaryConjunction(leFactory, decomposeMolecule((CompoundMolecule) expression));
    }

    public boolean isApplicable(LogicalExpression expression) {
        return expression instanceof CompoundMolecule;
    }

    protected Set<LogicalExpression> decomposeMolecule(CompoundMolecule compoundMolecule) {
        Set<LogicalExpression> simpleMolecules = new HashSet<LogicalExpression>();
        for (LogicalExpression le : compoundMolecule.listOperands()){
            if (le instanceof Molecule){
                simpleMolecules.add(le);
            }
        }
        return simpleMolecules;
    }
    
    public String toString() {
        return "X[A1,...,An]\n\t=>\n X[A1] and ... and X[An]\n";
    }
}