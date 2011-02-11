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

package org.wsml.reasoner.transformation.le.foldecomposition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.transformation.le.LEUtil;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.common.Identifier;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;


public class FOLMoleculeDecompositionRule implements NormalizationRule {
    
    public final static String sub = "urn://sub#";
    public final static String isa = "urn://isa#";
    public final static String hasValue = "urn://hv#";
    public final static String ofType = "urn://oftp#";
    public final static String impliesType = "urn://imtp#";
    
    private LogicalExpressionFactory leFactory;
    private WsmoFactory wsmoFactory;
    
    public FOLMoleculeDecompositionRule(FactoryContainer factory){
        this.leFactory = factory.getLogicalExpressionFactory();
        this.wsmoFactory = factory.getWsmoFactory();
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        if (expression instanceof CompoundMolecule) {
            return LEUtil.buildNaryConjunction(leFactory, decomposeMolecule((CompoundMolecule) expression));
        }
        else {
            return moleculeToAtom((Molecule) expression);
        }
    }

    public boolean isApplicable(LogicalExpression expression) {
        return expression instanceof Molecule;
    }

    protected Set<LogicalExpression> decomposeMolecule(CompoundMolecule compoundMolecule) {
        Set<LogicalExpression> simpleMolecules = new HashSet<LogicalExpression>();
        for (LogicalExpression le : compoundMolecule.listOperands()){
            if (le instanceof Molecule){
                simpleMolecules.add(moleculeToAtom((Molecule) le));
            }
        }
        return simpleMolecules;
    }

    private Atom moleculeToAtom(Molecule m) {
        Identifier id = null;
        List<Term> params = new ArrayList<Term>();
        params.add(m.getLeftParameter());
        if (m instanceof SubConceptMolecule) {
            id = wsmoFactory.createIRI(sub);
        }
        if (m instanceof MembershipMolecule) {
            id = wsmoFactory.createIRI(isa);
        }
        if (m instanceof AttributeConstraintMolecule) {
            id = wsmoFactory.createIRI(ofType);
            params.add(((AttributeConstraintMolecule) m).getAttribute());
        }
        if (m instanceof AttributeInferenceMolecule) {
            id = wsmoFactory.createIRI(impliesType);
            params.add(((AttributeInferenceMolecule) m).getAttribute());
        }
        if (m instanceof AttributeValueMolecule) {
            id = wsmoFactory.createIRI(hasValue);
            params.add(((AttributeValueMolecule) m).getAttribute());
        }
        params.add(m.getRightParameter());
        return leFactory.createAtom(id, params);
    }

    public String toString() {
        return "X[A1,...,An]\n\t=>\n X[A1] and ... and X[An]\n";
    }
}