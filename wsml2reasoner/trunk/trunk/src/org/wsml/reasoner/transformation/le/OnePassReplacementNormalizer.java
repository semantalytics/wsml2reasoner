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

import java.util.ArrayList;
import java.util.List;

import org.omwg.logicalexpression.CompoundExpression;
import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Constraint;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.Equivalence;
import org.omwg.logicalexpression.ExistentialQuantification;
import org.omwg.logicalexpression.Implication;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.Negation;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.UniversalQuantification;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.LogicalExpressionFactory;

/**
 * This class applies a set of normalization rules to the sub-expressions of a
 * logical expression by traversing the tree a single time. It distinguishes
 * between pre-order rules, which are applied in a top-down manner, and
 * post-order rules, which are applied in a bottom-up manner. For any set of
 * rules, all rules are apllied to a sub-expression until no rule application
 * causes any changes. The original expression will not be modified, rather a
 * copy is created.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class OnePassReplacementNormalizer implements
        LogicalExpressionNormalizer {
    protected List<NormalizationRule> preOrderRules;

    protected List<NormalizationRule> postOrderRules;

    protected LogicalExpressionFactory leFactory;

    /**
     * This constructor accepts two sets of normalization rules: one for
     * top-down application and one for bottom-up application.
     * 
     * @param preorderRules
     * @param postorderRules
     */
    public OnePassReplacementNormalizer(List<NormalizationRule> preorderRules,
            List<NormalizationRule> postorderRules, WSMO4JManager wsmoManager) {
        this.preOrderRules = preorderRules;
        this.postOrderRules = postorderRules;
        this.leFactory = wsmoManager.getLogicalExpressionFactory();
    }

    /**
     * This constructor accepts a single set of normalization rules that are
     * used for top-down application.
     * 
     * @param rules
     */
    public OnePassReplacementNormalizer(List<NormalizationRule> rules,
            WSMO4JManager wsmoManager) {
        this(rules, new ArrayList<NormalizationRule>(0), wsmoManager);
    }

    /**
     * This method recursively applies normalization rules to the
     * sub-expressions of a given logical expression. Normalization rules from
     * the pre-order set are applied befor, and normalization rules from the
     * post-order set are applied after a sub-expression is expanded. At any
     * stage, copies of a sub-expression are passed back, such that the original
     * expression remains unchanged.
     */
    public LogicalExpression normalize(LogicalExpression expression) {
        // apply pre-order normalization rules:
        expression = applyRules(expression, preOrderRules);

        // recursively normalize arguments of compound expressions:
        List<LogicalExpression> arguments = new ArrayList<LogicalExpression>();
        if (expression instanceof CompoundExpression) {
            CompoundExpression compound = (CompoundExpression) expression;
            for (LogicalExpression argument : (List<LogicalExpression>) compound
                    .listOperands()) {
                arguments.add(normalize(argument));
            }
            expression = replaceArguments(compound, arguments);
        }

        // apply post-order normalization rules:
        expression = applyRules(expression, postOrderRules);

        return expression;
    }

    /**
     * This method iteratively applies the normalization rules in a given list
     * to a given logical expression, until the expression is not changed by an
     * application of a rule anymore.
     * 
     * @param expression
     * @param rules
     * @return the resulting expression after iterative application of all
     *         applicable rules
     */
    protected LogicalExpression applyRules(LogicalExpression expression,
            List<NormalizationRule> rules) {
        boolean expressionHasChanged = true;
        while (expressionHasChanged) {
            expressionHasChanged = false;
            for (NormalizationRule rule : rules) {
                if (rule.isApplicable(expression)) {
                    LogicalExpression oldExpression = expression;
                    expression = rule.apply(expression);
                    if (!expression.equals(oldExpression))
                        expressionHasChanged = true;
                    break;
                }
            }
        }
        return expression;
    }

    /**
     * This method creates a copy of a given compound expression in which the
     * arguments are replaced by a given list of logical expressions.
     * 
     * @param compound
     * @param arguments
     * @return a compound expression with replaced arguments
     */
	protected CompoundExpression replaceArguments(CompoundExpression compound,
            List<LogicalExpression> arguments) {
        CompoundExpression result = null;
        if (compound instanceof Constraint) {
            result = leFactory.createConstraint(arguments.get(0));
        } else if (compound instanceof Negation) {
            result = leFactory.createNegation(arguments.get(0));
        } else if (compound instanceof NegationAsFailure) {
            result = leFactory.createNegationAsFailure(arguments.get(0));
        } else if (compound instanceof ExistentialQuantification) {
            ExistentialQuantification exists = (ExistentialQuantification) compound;
            result = leFactory.createExistentialQuantification(exists
                    .listVariables(), arguments.get(0));
        } else if (compound instanceof UniversalQuantification) {
            UniversalQuantification exists = (UniversalQuantification) compound;
            result = leFactory.createUniversalQuantification(exists
                    .listVariables(), arguments.get(0));
        } else if (compound instanceof Conjunction) {
            result = leFactory.createConjunction(arguments.get(0), arguments
                    .get(1));
        } else if (compound instanceof Disjunction) {
            result = leFactory.createDisjunction(arguments.get(0), arguments
                    .get(1));
        } else if (compound instanceof Equivalence) {
            result = leFactory.createEquivalence(arguments.get(0), arguments
                    .get(1));
        } else if (compound instanceof Implication) {
            result = leFactory.createImplication(arguments.get(0), arguments
                    .get(1));
        } else if (compound instanceof InverseImplication) {
            result = leFactory.createInverseImplication(arguments.get(0),
                    arguments.get(1));
        } else if (compound instanceof LogicProgrammingRule) {
            result = leFactory.createLogicProgrammingRule(arguments.get(0),
                    arguments.get(1));
        } else if (compound instanceof CompoundMolecule) {
        	List<Molecule> molecules = new ArrayList<Molecule>();
        	for (LogicalExpression le : arguments){
        		if (le instanceof Molecule){
        			molecules.add((Molecule) le);
        		}
        	}
            result = leFactory.createCompoundMolecule(molecules);
        } else {
            throw new RuntimeException(
                    "in OnePassReplacementNormalizer::replaceArguments() : reached presumably unreachable code! when handling object of type: "
                            + compound.getClass().getName());
        }
        return result;
    }
}
