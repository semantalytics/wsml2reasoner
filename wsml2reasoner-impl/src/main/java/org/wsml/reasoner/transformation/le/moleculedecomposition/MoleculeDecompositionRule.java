/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wsml.reasoner.transformation.le.moleculedecomposition;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.Molecule;
import org.wsml.reasoner.transformation.le.LEUtil;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class MoleculeDecompositionRule implements NormalizationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public MoleculeDecompositionRule(FactoryContainer factory){
        this.leFactory = factory.getLogicalExpressionFactory();
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